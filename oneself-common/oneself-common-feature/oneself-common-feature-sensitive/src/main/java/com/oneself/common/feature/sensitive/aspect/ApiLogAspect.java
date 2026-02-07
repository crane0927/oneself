package com.oneself.common.feature.sensitive.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneself.common.feature.sensitive.annotation.ApiLog;
import com.oneself.common.core.exception.OneselfException;
import com.oneself.common.core.utils.JacksonUtils;
import com.oneself.common.feature.sensitive.utils.SensitiveDataUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;


/**
 * @author liuhuan
 * date 2025/1/15
 * packageName com.oneself.aspect
 * className RequestLoggingAspect
 * description 记录日志的切面 集成 SensitiveDataUtils 实现敏感数据差异化脱敏
 * version 1.0
 */
@Slf4j
@Aspect
@Component
public class ApiLogAspect {

    // 复用项目统一的ObjectMapper（避免重复创建，与SensitiveDataUtils保持一致）
    private final ObjectMapper objectMapper = JacksonUtils.getObjectMapper();

    /**
     * 环绕通知：拦截类/方法上标注@ApiLog的接口，打印请求参数（脱敏后）、响应结果（脱敏后）
     * 优先级：方法级@ApiLog > 类级@ApiLog（方法注解覆盖类注解配置）
     */
    @Around("@within(com.oneself.annotation.ApiLog) || @annotation(com.oneself.annotation.ApiLog)")
    public Object recordApiLog(ProceedingJoinPoint joinPoint) throws OneselfException {
        // 1. 解析@ApiLog注解配置（判断是否打印请求/响应日志）
        ApiLogConfig config = resolveApiLogConfig(joinPoint);
        String controllerMethod = getControllerMethodName(joinPoint); // 如：UserController.getUser
        log.info("开始处理接口请求：{}", controllerMethod);

        // 2. 打印脱敏后的请求参数（仅当logRequest=true时）
        if (config.logRequest()) {
            logRequestParams(joinPoint, controllerMethod);
        }

        // 3. 执行目标接口方法（捕获异常并封装）
        Object responseResult;
        long startTime = System.currentTimeMillis(); // 新增耗时统计（可选）
        try {
            responseResult = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("接口[{}]执行异常，异常信息：{}", controllerMethod, e.getMessage(), e);
            throw new OneselfException("接口执行失败：" + e.getMessage(), e);
        } finally {
            log.info("接口[{}]处理结束，耗时：{}ms", controllerMethod, System.currentTimeMillis() - startTime);
        }

        // 4. 打印脱敏后的响应结果（仅当logResponse=true时）
        if (config.logResponse()) {
            logResponseResult(responseResult, controllerMethod);
        }

        return responseResult;
    }

    /**
     * 解析@ApiLog注解配置：优先取方法级注解，无则取类级注解
     *
     * @return 封装后的日志配置（logRequest、logResponse）
     */
    private ApiLogConfig resolveApiLogConfig(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        ApiLog methodApiLog = method.getAnnotation(ApiLog.class);
        ApiLog classApiLog = joinPoint.getTarget().getClass().getAnnotation(ApiLog.class);

        // 优先级：方法注解 > 类注解 > 默认值（true, true）
        boolean logRequest = true;
        boolean logResponse = true;
        if (methodApiLog != null) {
            logRequest = methodApiLog.logRequest();
            logResponse = methodApiLog.logResponse();
        } else if (classApiLog != null) {
            logRequest = classApiLog.logRequest();
            logResponse = classApiLog.logResponse();
        }
        return new ApiLogConfig(logRequest, logResponse);
    }

    /**
     * 打印脱敏后的请求参数
     *
     * @param joinPoint        切入点（获取请求参数）
     * @param controllerMethod 接口方法名（日志标识）
     */
    private void logRequestParams(ProceedingJoinPoint joinPoint, String controllerMethod) {
        Object[] requestArgs = joinPoint.getArgs();
        if (requestArgs == null || requestArgs.length == 0) {
            log.info("接口[{}]请求参数：无", controllerMethod);
            return;
        }

        // 遍历参数并脱敏（调用改造后的SensitiveDataUtils.copyAndDesensitize）
        for (int i = 0; i < requestArgs.length; i++) {
            Object originalParam = requestArgs[i];
            try {
                // 1. 脱敏参数（深拷贝+按@Sensitive规则脱敏，不修改原始请求参数）
                Object desensitizedParam = SensitiveDataUtils.copyAndDesensitize(originalParam);
                // 2. 序列化为JSON字符串（便于日志查看）
                String paramJson = objectMapper.writeValueAsString(desensitizedParam);
                // 3. 打印参数索引+参数类型+脱敏后JSON（提升日志可读性）
                log.info("接口[{}]请求参数[{}]（类型：{}）：{}",
                        controllerMethod, i + 1, originalParam.getClass().getSimpleName(), paramJson);
            } catch (Exception e) {
                // 脱敏失败时打印原始参数类型（避免日志报错，但不打印原始值以防敏感信息泄露）
                log.warn("接口[{}]请求参数[{}]（类型：{}）脱敏失败，原因：{}",
                        controllerMethod, i + 1, originalParam.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    /**
     * 打印脱敏后的响应结果
     *
     * @param responseResult   接口返回结果
     * @param controllerMethod 接口方法名（日志标识）
     */
    private void logResponseResult(Object responseResult, String controllerMethod) {
        if (responseResult == null) {
            log.info("接口[{}]响应结果：null", controllerMethod);
            return;
        }

        try {
            // 1. 脱敏响应结果（深拷贝+按@Sensitive规则脱敏，不修改原始响应）
            Object desensitizedResult = SensitiveDataUtils.copyAndDesensitize(responseResult);
            // 2. 序列化为JSON字符串
            String resultJson = objectMapper.writeValueAsString(desensitizedResult);
            log.info("接口[{}]响应结果（脱敏后）：{}", controllerMethod, resultJson);
        } catch (Exception e) {
            // 脱敏失败时打印结果类型（不打印原始值）
            log.warn("接口[{}]响应结果（类型：{}）脱敏失败，原因：{}",
                    controllerMethod, responseResult.getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * 获取接口方法名（格式：类名.方法名，如：UserController.getUser）
     */
    private String getControllerMethodName(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = ((MethodSignature) joinPoint.getSignature()).getMethod().getName();
        return className + "." + methodName;
    }

    /**
     * 内部静态类：封装@ApiLog的配置（logRequest、logResponse）
     * 避免零散变量传递，提升代码可读性
     */
    private record ApiLogConfig(boolean logRequest, boolean logResponse) {
    }
}