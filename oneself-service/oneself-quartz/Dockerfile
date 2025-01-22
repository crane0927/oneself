# 基础镜像
FROM openjdk:21-jdk-alpine

# 构建参数（服务名和版本号）
ARG SERVICE_NAME=oneself-quartz
ARG SERVICE_VERSION=1.0.0
ARG ENV_PROFILE=dev

# 环境变量
ENV SPRING_PROFILES_ACTIVE=${ENV_PROFILE}

# 创建应用目录
RUN mkdir -p /usr/${SERVICE_NAME}
WORKDIR /usr/${SERVICE_NAME}

# 复制打包后的 Jar 文件
COPY ${SERVICE_NAME}-${SERVICE_VERSION}.jar /usr/${SERVICE_NAME}/${SERVICE_NAME}-${SERVICE_VERSION}.jar

# 复制依赖库
COPY lib /usr/${SERVICE_NAME}/lib

# 复制配置文件
COPY config /usr/${SERVICE_NAME}/config

# 复制运行脚本
COPY run.sh /usr/${SERVICE_NAME}/run.sh
RUN chmod +x /usr/${SERVICE_NAME}/run.sh

# 暴露端口（根据你的应用配置调整）
EXPOSE 9102

# 启动命令
ENTRYPOINT ["/bin/sh", "-c", "./run.sh start ${SERVICE_VERSION}"]