# 基础镜像
FROM centos7-jdk
# 创建目录
RUN mkdir -p /usr/oneself-demo
# 指定路径
WORKDIR /usr/oneself-demo
# 复制jar文件到路径
COPY oneself.jar /usr/oneself-demo/oneself-demo.jar
# 启动系统服务
ENTRYPOINT ["java","-jar","oneself-demo.jar","--spring.profiles.active=test"]
