# 基础镜像
FROM centos7-jdk
# 创建目录
RUN mkdir -p /usr/oneself-quartz
# 指定路径
WORKDIR /usr/oneself-quartz
# 复制jar文件到路径
COPY oneself.jar /usr/oneself-quartz/oneself-quartz.jar
# 启动系统服务
ENTRYPOINT ["java","-jar","oneself-quartz.jar","--spring.profiles.active=test"]
