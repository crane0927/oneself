# 基础镜像
FROM centos7-jdk
# 创建目录
RUN mkdir -p /usr/oneself-user
# 指定路径
WORKDIR /usr/oneself-user
# 复制jar文件到路径
COPY oneself.jar /usr/oneself-user/oneself-user.jar
# 启动系统服务
ENTRYPOINT ["java","-jar","oneself-user.jar","--spring.profiles.active=test"]
