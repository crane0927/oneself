# 基础镜像
FROM centos7-jdk
# 创建目录
RUN mkdir -p /usr/oneself-gataway
# 指定路径
WORKDIR /usr/oneself-gataway
# 复制jar文件到路径
COPY oneself.jar /usr/oneself-gataway/oneself-gataway.jar
# 启动系统服务
ENTRYPOINT ["java","-jar","oneself-gataway.jar","--spring.profiles.active=test"]
