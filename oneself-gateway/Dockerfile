# 基础镜像
FROM openjdk:21-jdk-alpine

# 构建参数（服务名和版本号）
ARG SERVICE_NAME=oneself-gateway
ARG SERVICE_VERSION=1.0.0
ARG ENV_PROFILE=dev

# 环境变量
ENV SPRING_PROFILES_ACTIVE=${ENV_PROFILE}

# 创建工作目录
RUN mkdir -p /usr/${SERVICE_NAME}
WORKDIR /usr/${SERVICE_NAME}

# 复制 Jar 文件到工作目录
COPY oneself.jar /usr/${SERVICE_NAME}/${SERVICE_NAME}-${SERVICE_VERSION}.jar

# 设置启动命令
ENTRYPOINT ["java", "-jar", "/usr/${SERVICE_NAME}/${SERVICE_NAME}-${SERVICE_VERSION}.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
