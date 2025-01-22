# oneself
## 项目结构
```text
oneself
├── oneself-common                  # 公共模块，存放共享的工具类、公共服务、通用配置等
├── oneself-gateway                 # 网关模块，处理请求路由、认证、限流等、默认端口 9001
├── oneself-service                 # 核心业务模块，包含多个子模块，处理具体的业务逻辑
│   ├── oneself-demo                # 示例服务模块，演示服务如何使用
│   └── ...                         # 其他具体的业务模块
├── oneself-service-api             # 服务的接口定义模块，供其他服务或模块调用
│   ├── oneself-demo-api            # 示例服务模块的接口定义，包含相关接口和数据传输对象
│   └── ...                         # 其他具体示例服务模块接口
```
---

## 目录结构
### 服务目录结构
```text
sql
 └── xx.sql                          # 存放数据库相关的 SQL 脚本文件（如建表、初始化数据等）

src
 └── main
     ├── java
     │   └── com
     │       └── oneself
     │           ├── annotation       # 自定义注解包，存放项目中的注解定义（如权限校验、事务等）
     │           ├── aspect           # 切面包，处理 AOP（面向切面编程）逻辑，如日志、权限、事务管理等
     │           ├── config           # 配置包，存放服务的配置类（如数据源配置、全局配置、缓存配置等）
     │           ├── controller       # 控制器包，处理前端请求，调用业务服务，负责与前端交互
     │           ├── exception        # 自定义异常包，用于定义项目中的异常类型，通常继承自 RuntimeException
     │           ├── filter           # 过滤器包，处理请求拦截、响应拦截等，通常用于日志记录、权限校验等
     │           ├── handler          # 处理器包，处理具体的业务逻辑或事件（如事件处理器、任务处理器等）
     │           ├── mapper           # 数据访问层包，存放 MyBatis 映射接口及 XML 文件，用于数据库操作
     │           ├── model            # 数据模型包，包含业务数据模型相关的类，通常包括：
     │           │   ├── dto          # 数据传输对象（DTO）包，用于服务之间传输数据或从 API 接口接收数据
     │           │   ├── pojo         # 实体类包，对应数据库表的实体映射，通常使用 JPA 或 MyBatis 映射
     │           │   ├── enums        # 枚举类包，定义项目中使用的枚举类型（如状态码、类型分类等）
     │           │   └── vo           # 视图对象（VO）包，用于向前端展示的返回数据结构
     │           ├── service          # 业务逻辑层包，存放服务接口及实现类，负责具体业务逻辑的处理
     │           ├── utils            # 工具类包，存放通用工具方法类，如日期工具、字符串处理工具等
     │           └── xxApplication.java  # Spring Boot 启动类，项目的入口，负责启动整个 Spring Boot 应用
     └── resource
         ├── mapper                      # 存放 MyBatis 的 XML 映射文件（如 SQL 查询语句）
         ├── application.yml             # Spring Boot 主配置文件，存放全局应用配置（如端口、日志级别等）
         ├── application-环境.yml         # 环境配置文件，根据不同环境（开发、生产等）配置不同参数
         ├── log4j2.xml                  # 日志配置文件，配置 Log4j2 的日志输出格式、日志级别等
         ├── run.sh                      # 启动脚本，通常用于容器化或在服务器中自动化运行应用
     
Dockerfile                           # Docker 配置文件，定义如何构建项目的 Docker 镜像
```
---
### 服务 API 目录结构
```text
main
 └── java
     └── com
         └── oneself
             ├── model                # 数据模型包，包含服务端与前端的数据传输结构
             │   ├── dto              # 数据传输对象（DTO）包，用于服务之间的数据传输
             │   ├── pojo             # 实体类包，对应数据库表的实体映射，通常与数据库相关
             │   ├── enums            # 枚举类包，定义项目中使用的枚举，如状态、类型等
             │   └── vo               # 视图对象（VO）包，用于表示前端需要的返回数据结构
             ├── client               # 客户端相关模型，通常存放与外部服务交互的模型
             │   └── fallback         # 服务降级的回退类，通常用于定义外部调用失败时的回退逻辑             
            
```
---
## 标准启动类
```java
@SpringBootApplication
@Validated
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(DemoApplication.class, args);
        // 使用工具类 打印启动信息
        ApplicationStartupUtils.printStartupInfo(application.getEnvironment());
    }
}
```
---
## 服务打包配置
### ~~服务打包配置 JDK 1.8~~
```xml

<build>
    <!-- 项目默认目标，设置为编译目标 -->
    <defaultGoal>compile</defaultGoal>

    <!-- 项目打包后的名称 -->
    <finalName>${project.artifactId}-${project.version}</finalName>

    <!-- 定义资源配置 -->
    <resources>
        <!-- 第一部分：将 src/main/resources 中的文件复制到编译输出目录 -->
        <resource>
            <directory>src/main/resources</directory>
            <targetPath>${project.build.outputDirectory}</targetPath>
            <filtering>false</filtering>
        </resource>
        <!-- 第二部分：将 src/main/resources 中的文件复制到测试编译输出目录 -->
        <resource>
            <directory>src/main/resources</directory>
            <targetPath>${project.build.testOutputDirectory}</targetPath>
            <filtering>false</filtering>
        </resource>
        <!-- 第三部分：将指定的配置文件单独复制到 config 目录，不打包进 jar -->
        <resource>
            <directory>src/main/resources</directory>
            <targetPath>${project.build.directory}/${project.artifactId}/config</targetPath>
            <includes>
                <include>application.yml</include>
                <include>application-*.yml</include>
                <include>log4j2.xml</include>
            </includes>
        </resource>
        <!-- 第四部分：将运行脚本复制到指定目录 -->
        <resource>
            <directory>src/main/resources</directory>
            <targetPath>${project.build.directory}/${project.artifactId}</targetPath>
            <includes>
                <include>run.sh</include>
            </includes>
        </resource>
    </resources>

    <plugins>
        <!-- 使用 maven-antrun-plugin 插件执行自定义的 Ant 任务 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-antrun-plugin</artifactId>
            <version>1.8</version>
            <executions>
                <execution>
                    <!-- 在 package 阶段执行 -->
                    <phase>package</phase>
                    <goals>
                        <goal>run</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- maven-clean-plugin：清理构建目录下的内容 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-clean-plugin</artifactId>
        </plugin>

        <!-- maven-compiler-plugin：编译插件，用于设置 JDK 版本和编码格式 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>1.8</source> <!-- 源代码的 JDK 版本 -->
                <target>1.8</target> <!-- 生成的 class 文件的 JDK 版本 -->
                <encoding>UTF8</encoding> <!-- 编码格式 -->
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

        <!-- maven-dependency-plugin：将依赖的 jar 包复制到指定目录 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-dependencies</id>
                    <phase>prepare-package</phase> <!-- 在 prepare-package 阶段执行 -->
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>
                            ${project.build.directory}/${project.artifactId}/lib
                        </outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <!-- maven-jar-plugin：自定义 jar 包的打包过程 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <configuration>
                <!-- jar 包的输出目录 -->
                <outputDirectory>${project.build.directory}/${project.artifactId}</outputDirectory>
                <!-- 排除一些不需要打包的文件 -->
                <excludes>
                    <include>application.yml</include>
                    <include>application-*.yml</include>
                    <include>log4j2.xml</include>
                    <include>run.sh</include>
                </excludes>
                <archive>
                    <!-- 指定程序的入口 main 函数 -->
                    <manifest>
                        <mainClass>com.oneself.QuartzApplication</mainClass>
                        <addClasspath>true</addClasspath> <!-- 添加 classpath -->
                        <classpathPrefix>lib/</classpathPrefix>
                    </manifest>
                    <!-- 将资源文件目录添加到 classpath 中 -->
                    <manifestEntries>
                        <Class-Path>config/</Class-Path>
                    </manifestEntries>
                </archive>
            </configuration>
        </plugin>

        <!-- maven-surefire-plugin：跳过单元测试 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <skip>true</skip>
            </configuration>
        </plugin>
    </plugins>
</build>

```
---
### 服务打包配置 JDK 21
```xml
<build>
        <!-- 项目默认目标，设置为编译目标 -->
        <defaultGoal>compile</defaultGoal>

        <!-- 项目打包后的名称 -->
        <finalName>${project.artifactId}-${project.version}</finalName>

        <!-- 定义资源配置 -->
        <resources>
            <!-- 第一部分：将 src/main/resources 中的文件复制到编译输出目录 -->
            <resource>
                <directory>src/main/resources</directory>
                <targetPath>${project.build.outputDirectory}</targetPath>
                <filtering>false</filtering>
            </resource>
            <!-- 第二部分：将 src/main/resources 中的文件复制到测试编译输出目录 -->
            <resource>
                <directory>src/main/resources</directory>
                <targetPath>${project.build.testOutputDirectory}</targetPath>
                <filtering>false</filtering>
            </resource>
            <!-- 第三部分：将指定的配置文件单独复制到 config 目录，不打包进 jar -->
            <resource>
                <directory>src/main/resources</directory>
                <targetPath>${project.build.directory}/${project.artifactId}/config</targetPath>
                <includes>
                    <include>application.yml</include>
                    <include>application-*.yml</include>
                    <include>log4j2.xml</include>
                </includes>
            </resource>
            <!-- 第四部分：将运行脚本复制到指定目录 -->
            <resource>
                <directory>src/main/resources</directory>
                <targetPath>${project.build.directory}/${project.artifactId}</targetPath>
                <includes>
                    <include>run.sh</include>
                </includes>
            </resource>
        </resources>

        <plugins>
            <!-- 使用 maven-antrun-plugin 插件执行自定义的 Ant 任务 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-antrun-plugin</artifactId>
                <version>3.1.0</version>
                <executions>
                    <execution>
                        <!-- 在 package 阶段执行 -->
                        <phase>package</phase>
                        <goals>
                            <goal>run</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- maven-clean-plugin：清理构建目录下的内容 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-clean-plugin</artifactId>
            </plugin>

            <!-- maven-compiler-plugin：编译插件，用于设置 JDK 版本和编码格式 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>21</source> <!-- 源代码的 JDK 版本 -->
                    <target>21</target> <!-- 生成的 class 文件的 JDK 版本 -->
                    <encoding>UTF8</encoding> <!-- 编码格式 -->
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>${lombok.version}</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>

            <!-- maven-dependency-plugin：将依赖的 jar 包复制到指定目录 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>prepare-package</phase> <!-- 在 prepare-package 阶段执行 -->
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>
                                ${project.build.directory}/${project.artifactId}/lib
                            </outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- maven-jar-plugin：自定义 jar 包的打包过程 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <configuration>
                    <!-- jar 包的输出目录 -->
                    <outputDirectory>${project.build.directory}/${project.artifactId}</outputDirectory>
                    <!-- 排除一些不需要打包的文件 -->
                    <excludes>
                        <include>application.yml</include>
                        <include>application-*.yml</include>
                        <include>log4j2.xml</include>
                        <include>run.sh</include>
                    </excludes>
                    <archive>
                        <!-- 指定程序的入口 main 函数 -->
                        <manifest>
                            <mainClass>com.example.oneself.GatewayApplication</mainClass>
                            <addClasspath>true</addClasspath> <!-- 添加 classpath -->
                            <classpathPrefix>lib/</classpathPrefix>
                        </manifest>
                        <!-- 将资源文件目录添加到 classpath 中 -->
                        <manifestEntries>
                            <Class-Path>config/</Class-Path>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>

            <!-- maven-surefire-plugin：跳过单元测试 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>

```
---
### 无主类服务打包配置
```xml

<build>
    <!-- API 模块 无主类 -->
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>repackage</id>
                    <phase>none</phase>
                </execution>
            </executions>
        </plugin>

        <!-- 配置项目的 Java 编译器版本及其他编译相关设置 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>21</source> <!-- 源代码的 JDK 版本 -->
                <target>21</target> <!-- 生成的 class 文件的 JDK 版本 -->
                <encoding>UTF8</encoding> <!-- 编码格式 -->
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>

        <!-- maven-surefire-plugin：跳过单元测试 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <skip>true</skip>
            </configuration>
        </plugin>
    </plugins>
</build>

```
---
## log4j2.xml
### 基础配置
```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- status: Log4j2 内部日志的输出级别 -->
<!-- monitorInterval: 定时检测配置文件的修改,有变化则自动重新加载配置,时间单位为秒,最小间隔为 5s -->
<Configuration status="WARN" monitorInterval="600">
    <!-- properties: 设置全局变量 -->
    <properties>
        <property name="LOG_HOME">logs</property>
        <property name="LOG_NAME">${sys:log.name:-info}</property>
        <property name="pattern">%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %c{1.1.1.*}[%M : %L(%tid)] [%X{traceId}] - %msg%n</property>
    </properties>
    <!-- Appenders: 定义日志输出目的地，内容和格式等 -->
    <Appenders>
        <console name="Console" target="SYSTEM_OUT">
            <!-- pattern: 日期,线程名,日志级别,日志名称,日志信息,换行 -->
            <PatternLayout pattern="${pattern}"/>
        </console>
        <!-- info log -->
        <!-- RollingFile: 日志输出到文件,下面的文件都使用相对路径 -->
        <!-- fileName: 当前日志输出的文件名称 -->
        <!-- filePattern: 备份日志文件名称，要求按年月日滚动生成日志文件 -->
        <RollingRandomAccessFile name="InfoFile" fileName="${LOG_HOME}/${LOG_NAME}-info.log" filePattern="${LOG_HOME}/${LOG_NAME}-info-%d{yyyy-MM-dd}-%i.log">
            <!--控制台只输出 level 及其以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
            <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
            <!-- 日期,容器主机名,应用名称,pod哈希码,日志名称,行号,日志级别,日志信息,换行等 （以单个空格做为分隔符） -->
            <PatternLayout pattern="${pattern}"/>
            <!--Policies: 触发策略决定何时执行备份 -->
            <Policies>
                <!-- 单个日志文件生成的大小-->
                <SizeBasedTriggeringPolicy size="200MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30">
                <!-- 指向所需删除的日志目录名称 -->
                <Delete basePath="${LOG_HOME}">
                    <!-- 删除 7 天前的日志文件，格式为：P天数D -->
                    <IfLastModified age="P7D"/>
                </Delete>
            </DefaultRolloverStrategy>
        </RollingRandomAccessFile>
        <!-- debug log -->
        <RollingRandomAccessFile name="DebugFile" fileName="${LOG_HOME}/${LOG_NAME}-debug.log" filePattern="${LOG_HOME}/${LOG_NAME}-debug-%d{yyyy-MM-dd}-%i.log">
            <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${pattern}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="200MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30">
                <Delete basePath="${LOG_HOME}">
                    <IfLastModified age="P7D"/>
                </Delete>
            </DefaultRolloverStrategy>
        </RollingRandomAccessFile>
        <!-- warn log -->
        <RollingRandomAccessFile name="WarnFile" fileName="${LOG_HOME}/${LOG_NAME}-warn.log" filePattern="${LOG_HOME}/${LOG_NAME}-warn-%d{yyyy-MM-dd}-%i.log">
            <ThresholdFilter level="WARN" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${pattern}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="200MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30">
                <Delete basePath="${LOG_HOME}">
                    <IfLastModified age="P7D"/>
                </Delete>
            </DefaultRolloverStrategy>
        </RollingRandomAccessFile>
        <!-- error log -->
        <RollingRandomAccessFile name="ErrorFile" fileName="${LOG_HOME}/${LOG_NAME}-error.log" filePattern="${LOG_HOME}/${LOG_NAME}-error-%d{yyyy-MM-dd}-%i.log">
            <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
            <PatternLayout pattern="${pattern}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="200MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30">
                <Delete basePath="${LOG_HOME}">
                    <IfLastModified age="P7D"/>
                </Delete>
            </DefaultRolloverStrategy>
        </RollingRandomAccessFile>
    </Appenders>
    <!--Loggers: 定义日志级别和使用的输出 -->
    <Loggers>
        <!-- Root:日志默认打印到控制台 -->
        <!-- level日志级别: ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL < OFF -->
        <Root level="INFO">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="InfoFile"/>
            <AppenderRef ref="DebugFile"/>
            <AppenderRef ref="WarnFile"/>
            <AppenderRef ref="ErrorFile"/>
        </Root>
    </Loggers>
</Configuration>
```
---
## Dockerfile
1. Dockerfile 文件内容
```dockerfile
# 基础镜像
FROM openjdk:21-jdk-alpine

# 构建参数（服务名和版本号）
ARG SERVICE_NAME=service-name
ARG SERVICE_VERSION=1.0.0
ARG ENV_PROFILE=dev

# 环境变量
ENV SPRING_PROFILES_ACTIVE=${ENV_PROFILE}

# 创建工作目录
RUN mkdir -p /usr/${SERVICE_NAME}
WORKDIR /usr/${SERVICE_NAME}

# 复制 Jar 文件到工作目录
COPY ${SERVICE_NAME}-${SERVICE_VERSION}.jar /usr/${SERVICE_NAME}/${SERVICE_NAME}-${SERVICE_VERSION}.jar

# 设置启动命令
ENTRYPOINT ["java", "-jar", "/usr/${SERVICE_NAME}/${SERVICE_NAME}-${SERVICE_VERSION}.jar", "--spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]
```
2. 构建镜像：`docker build --build-arg SERVICE_NAME=my-service --build-arg SERVICE_VERSION=1.0.0 --build-arg ENV_PROFILE=prod -t my-service:1.0.0 .`
3. 运行容器：`docker run -e SPRING_PROFILES_ACTIVE=prod -d my-service:1.0.0`
---
### 日志推送 Kafka 配置
1. 添加 maven 依赖
```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
</dependency>
```
2. 配置 `log4j2.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<Configuration status="WARN" monitorInterval="600">
    <!-- 添加 kafka 配置 -->
    <Appenders>
        <Kafka name="Kafka" topic="${topic name}">
            <PatternLayout pattern="${pattern}"/>
            <Property name="bootstrap.servers">${kafka address}</Property>
        </Kafka>
    </Appenders>

    <Loggers>
        <Root level="DEBUG">
            <AppenderRef ref="Kafka"/>
        </Root>
        <Logger name="org.apache.kafka" level="INFO"/> <!-- 避免递归日志记录 -->
    </Loggers>
</Configuration>
```
---

## 环境（JDK 1.8 升级至 JDK 21）
- ~~JDK 1.8~~ -> JDK 21
- ~~Spring Boot 2.7.18~~ -> ~~Spring Boot 3.2.12~~ -> Spring Boot 3.3.7
- ~~Spring Cloud 2021.0.9~~ -> Spring Cloud 2023.0.5
- ~~Spring Cloud Alibaba 2021.0.6.2~~ -> Spring Cloud Alibaba 2023.0.3.2
- ~~Eleasticsearch 7.17.7~~ -> Eleasticsearch 8.17.0
- ~~knife4j-openapi2-spring-boot-starter 4.5.0~~ -> openapi3-jakarta-spring-boot-starter.version 4.4.0
- ~~mybatis-plus-boot-starter 3.5.3.1~~ -> mybatis-plus-spring-boot3-starter 3.5.5
- Nacos 2.4.1
- MySQL 8.0.39
- Maven 3.9.9
---
## 配置
### 开启 Swagger
1. 引入依赖 oneself-common
```xml
<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-common</artifactId>
    <version>1.0.0</version>
</dependency>
```
2. ~~在 `application.yml` 中添加以下配置（JDK 1.8）：~~
```yaml
oneself:
  swagger:
    enable: true
    title: oneself-demo
    description: oneself-demo API 文档
    version: 1.0.0
    base-package: com.oneself
    service-url: http://127.0.0.1:9011/oneself-demo
```
3. 在 `application.yml` 中添加以下配置（JDK 21）：
```yaml
oneself:
  swagger: # swagger 主页配置
    enable: true # 开启 Swagger 主页配置，默认 false
    base-package: com.oneself # 扫描的包名，默认扫描 com.oneself 包下的所有接口
    title: oneself-demo # 接口文档标题，默认 oneself
    description: oneself-demo description # 接口文档描述，默认 oneself description
    license: http://www.apache.org/licenses/LICENSE-2.0 # 默认的 license
    serviceUrl: http://localhost:8080 #服务条款，默认为空
    contact-name: crane # 联系人名称，默认 crane
    contact-email: crane0927@163.com # 联系人邮箱，默认 crane0927@163.com
    contact-url: https://github.com/crane0927 # 联系人 URL，默认 https://github.com/crane0927
    version: 1.0 # 接口文档版本，默认 1.0
    group-name: oneself-demo # 接口文档分组名称，默认 oneself
    
knife4j:
  enable: true # 开启增强配置，启用 Knife4j 功能
  documents: # 自定义文档集合，该属性是数组
    - group: 2.X版本 # 所属分组
      name: 接口签名 # 类似于接口中的 tag，对于自定义文档的分组
      locations: classpath:sign/* # markdown 文件路径,可以是一个文件夹
  setting: # 前端 Ui 的个性化配置属性
    language: zh-CN # 文档界面语言，默认中文 (zh-CN)，也可设置为英文 (en-US)
    enable-swagger-models: true # 是否显示界面中 Swagger Model 功能，默认开启
    enable-document-manage: true # 是否显示文档管理功能，默认开启
    swagger-model-name: Swagger Models # 重命名 Swagger Model 名称，默认 Swagger Models
    enable-version: false # 是否开启界面中对某接口的版本控制，如果开启，后端变化后 Ui 界面会存在小蓝点，默认关闭
    enable-reload-cache-parameter: false # 是否在每个 Debug 调试栏后显示刷新变量按钮，默认不显示
    enable-after-script: true # 调试 Tab 是否显示 AfterScript 功能，默认开启
    enable-filter-multipart-api-method-type: POST # 具体接口的过滤类型
    enable-filter-multipart-apis: false # 针对 RequestMapping 的接口请求类型，在不指定参数类型的情况下，如果不过滤，默认会显示 7 个类型的接口地址参数,如果开启此配置,默认展示一个 Post 类型的接口地址
    enable-request-cache: true # 是否开启请求参数缓存，默认开启
    enable-host: false # 是否启用 Host，默认关闭
    enable-host-text: false # HOST 地址，默认当前服务地址
    enable-home-custom: false # 是否使用自定义主页内容，默认关闭
    home-custom-path: classpath:markdown/home.md # 自定义主页内容的 Markdown 文件路径
    enable-search: false # 是否禁用 Ui 界面中的搜索框，默认不禁止
    enable-footer: true # 是否显示 Footer，默认显示
    enable-footer-custom: false # 是否开启自定义 Footer，默认关闭
    footer-custom-content: GitHub-[oneself](https://github.com/crane0927) # 自定义 Footer 内容
    enable-dynamic-parameter: false # 是否开启动态参数调试功能，默认关闭
    enable-debug: true # 启用调试，默认开启
    enable-open-api: true # 显示 OpenAPI 规范，默认开启
    enable-group: true # 显示服务分组，默认开启
  cors: false # 是否开启一个默认的跨域配置，该功能配合自定义 Host 使用
  production: false # 是否开启生产环境保护策略，生产环境中会隐藏 Swagger 界面
  basic: # 用户认证配置
    enable: true # 是否启用用户认证功能
    username: dev # 用户名，用于登录 Swagger 界面
    password: dev123 # 密码，用于登录 Swagger 界面
```
---
### 配置 FTP/SFTP
1. 引入依赖 oneself-common
```xml
<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-common</artifactId>
    <version>1.0.0</version>
</dependency>
```
2. 在 `application.yml` 中添加以下配置：
```yaml
oneself:
  file:
    ftp:
      host: "ftp.oneself.com"
      port: 21
      user: "ftpUser"
      password: "ftpPassword"
    sftp:
      host: "sftp.oneself.com"
      port: 22
      user: "ftpUser"
      password: "ftpPassword"
```
---
### 配置 Nacos
1. 引入 nacos 配置依赖
```xml

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>

```
2. 引入 nacos 注册中心依赖
```xml

<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
```
3. 在 `application.yml` 中添加以下配置：
```yaml
spring:
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848 # Nacos 地址
      config:
        import-check:
          enabled: false # 校验 Nacos 配置中心的配置文件是否存在
        file-extension: yaml # 在 Nacos 的文件后缀名
        group: DEFAULT_GROUP # 配置组名，可以自定义
        refresh-enabled: true # 启用自动刷新配置
        namespace: dev # 配置命名空间，使用命名空间唯一 ID
      discovery:
        cluster-name: dev # 设置 Nacos 的集群属性
        namespace: dev # 配置命名空间，使用命名空间唯一 ID
```
4. 启动类中添加注解 `@EnableDiscoveryClient`

---
### 配置 Elasticsearch
1. 引入依赖 oneself-common
```xml
<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-common</artifactId>
    <version>1.0.0</version>
</dependency>
```
2. 在 `application.yml` 中添加以下配置：
```yaml
oneself:
  elasticsearch:
    enabled: true # 开启 Elasticsearch
    nodes: # 节点列表 单机模式支持一个地址，集群模式支持多个地址
      - "localhost:9200"
      - "127.0.0.1:9200"
    username: "elastic" # 用户名（如果启用了安全认证）
    password: "elastic" # 密码（如果启用了安全认证）
    connect-timeout: 5000 # 连接超时时间（毫秒）
    socket-timeout: 60000 # 读超时时间（毫秒）
    max-connections: 100 # 最大连接数
    max-connections-per-route: 10 # 每个路由的最大连接数
```
---
## run.sh
```bash
#!/bin/sh

project_name=$(basename "$(pwd)")

start_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        echo "Please specify the version. Usage: $0 {start|stop|restart} <version>"
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -z "$PID" ]; then
        java -Xms1g -Xmx1g -XX:+HeapDumpOnOutOfMemoryError -Dlog.name="${project_name}" -jar "${jar_name}" > /dev/null 2>&1 &
        sleep 2
        PID=$(pgrep -f "${jar_name}")

        if [ -n "$PID" ]; then
            printf "%-5s %-22s %-8s\n" start "$jar_name" success
        else
            printf "%-5s %-22s %-8s\n" start "$jar_name" failed
        fi
    else
        printf "%-5s %-22s %-8s\n" start "$jar_name" "already running"
    fi
}

stop_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        echo "Please specify the version. Usage: $0 {start|stop|restart} <version>"
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        kill -9 "$PID"
        sleep 2
        PID1=$(pgrep -f "${jar_name}")

        if [ -z "$PID1" ]; then
            printf "%-5s %-22s %-8s\n" stop "$jar_name" success
        else
            printf "%-5s %-22s %-8s\n" stop "$jar_name" failed
        fi
    else
        printf "%-5s %-22s %-8s\n" stop "$jar_name" "not running"
    fi
}

restart_project() {
    version="$1"
    stop_project "$version"
    start_project "$version"
}

if [ -z "$2" ]; then
    echo "Please specify the version. Usage: $0 {start|stop|restart} <version>"
    exit 1
fi

case "$1" in
    start)
        start_project "$2"
        ;;
    stop)
        stop_project "$2"
        ;;
    restart)
        restart_project "$2"
        ;;
    *)
        echo "Usage: $0 {start|stop|restart} <version>"
        exit 1
        ;;
esac

exit 0
```
---
## 问题记录
1. JDK 21 中使用 knife4j-openapi3 如何配置 Swagger 在生产环境中关闭
```yaml
knife4j:
  enable: true # 开启增强配置，启用 Knife4j 功能
  production: true # 是否开启生产环境保护策略，生产环境中会隐藏 Swagger 界面
```
2. 深拷贝过程中使用了 writeValueAsString 和 readValue，导致原始对象的类型信息和泛型信息在反序列化时丢失问题（SensitiveDataUtils 38 - 41 行）
