# oneself
---
## 1 环境（JDK 1.8 升级至 JDK 21）
- ~~JDK 1.8~~ -> JDK 21
- ~~Spring Boot 2.7.18~~ -> ~~Spring Boot 3.2.12~~ -> ~~Spring Boot 3.3.7~~ -> Spring Boot 3.4.4
- ~~Spring Cloud 2021.0.9~~ -> ~~Spring Cloud 2023.0.5~~ -> Spring Cloud 2024.0.1
- ~~Spring Cloud Alibaba 2021.0.6.2~~ -> Spring Cloud Alibaba 2023.0.3.2
- ~~Eleasticsearch 7.17.7~~ -> Eleasticsearch 8.17.0
- ~~knife4j-openapi2-spring-boot-starter 4.5.0~~ -> ~~openapi3-jakarta-spring-boot-starter.version 4.4.0~~ -> openapi3-jakarta-spring-boot-starter.version 4.5.0
- ~~mybatis-plus-boot-starter 3.5.3.1~~ -> ~~mybatis-plus-spring-boot3-starter 3.5.5~~ -> mybatis-plus-spring-boot3-starter 3.5.8
- Nacos 2.4.3
- MySQL 8.0.39
- PostgreSQL 14.15
- Maven 3.9.11
---
## 2 项目结构
```text
oneself
├── oneself-common                  # 公共模块，存放共享的工具类、公共服务、通用配置等
│   ├── oneself-common-core         # 公共核心模块
│   ├── oneself-common-utils        # 公共工具模块
│   └── ...
├── oneself-gateway                 # 网关模块，处理请求路由、认证、限流等、默认端口 9100
├── oneself-service                 # 核心业务模块，包含多个子模块，处理具体的业务逻辑 端口从 9101 开始
│   ├── oneself-demo                # 示例服务模块，演示服务如何使用
│   ├── oneself-ai                  # 人工智能模块，提供 AI 服务，如文本翻译、语音识别、语音合成等（未开发）
│   └── ...                         # 其他具体的业务模块
├── oneself-service-api             # 服务的接口定义模块，供其他服务或模块调用
│   ├── oneself-demo-api            # 示例服务模块的接口定义，包含相关接口和数据传输对象
│   └── ...                         # 其他具体示例服务模块接口
```
---

## 3 目录结构
### 3.1 服务目录结构
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
     │           │   └── bo           # 通用数据对象（BO）包，用于封装业务逻辑处理过程中需要的数据
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
### 3.2 服务 API 目录结构
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
## 4 标准启动类
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
## 5 服务打包配置
### ~~5.1 服务打包配置 JDK 1.8~~
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
### 5.2 服务打包配置 JDK 21
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
### 5.3 无主类服务打包配置
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
## 6 log4j2.xml
### 6.1 基础配置
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
### 6.2 日志推送 Kafka 配置
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
## 7 run.sh
```bash
#!/bin/bash

# 项目名 = 当前文件夹名
project_name=$(basename "$(pwd)")

# 输出日志函数（带颜色）
log_message() {
    local color_reset="\e[0m"
    local color_info="\e[32m"
    local color_warn="\e[33m"
    local color_error="\e[31m"

    case "$2" in
        info) echo -e "$(date '+%Y-%m-%d %H:%M:%S') ${color_info}$1${color_reset}" ;;
        warn) echo -e "$(date '+%Y-%m-%d %H:%M:%S') ${color_warn}$1${color_reset}" ;;
        error) echo -e "$(date '+%Y-%m-%d %H:%M:%S') ${color_error}$1${color_reset}" ;;
        *) echo "$(date '+%Y-%m-%d %H:%M:%S') $1" ;;
    esac
}

start_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        log_message "请指定版本号，例如：$0 {start|stop|restart|status} <version>" error
        exit 1
    fi

    if [ ! -f "${jar_name}" ]; then
        log_message "Jar 文件不存在：${jar_name}" error
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -z "$PID" ]; then
        log_message "准备启动：${jar_name}" info
        nohup java -Xms1g -Xmx1g -XX:+HeapDumpOnOutOfMemoryError -Dlog.name="${project_name}" -jar "${jar_name}" > /dev/null 2>&1 &
        sleep 3
        PID=$(pgrep -f "${jar_name}")
        if [ -n "$PID" ]; then
            log_message "启动成功，PID: $PID" info
        else
            log_message "启动失败，请检查日志或确认是否已有其他实例运行。" error
        fi
    else
        log_message "程序已在运行中（PID: $PID），无需重复启动。" warn
    fi
}

stop_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        log_message "请指定版本号，例如：$0 {start|stop|restart|status} <version>" error
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        log_message "尝试停止进程：PID $PID" info
        kill "$PID"
        sleep 3
        PID2=$(pgrep -f "${jar_name}")
        if [ -z "$PID2" ]; then
            log_message "停止成功。" info
        else
            log_message "普通停止失败，尝试强制 kill -9..." warn
            kill -9 "$PID2"
            sleep 2
            PID3=$(pgrep -f "${jar_name}")
            if [ -z "$PID3" ]; then
                log_message "强制停止成功。" info
            else
                log_message "无法停止进程，请手动检查。" error
            fi
        fi
    else
        log_message "程序未运行，无需停止。" warn
    fi
}

restart_project() {
    version="$1"
    stop_project "$version"
    start_project "$version"
}

status_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        log_message "请指定版本号，例如：$0 {start|stop|restart|status} <version>" error
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        log_message "${jar_name} 正在运行，PID: $PID" info
    else
        log_message "${jar_name} 未运行。" warn
    fi
}

# 参数校验
if [ -z "$2" ]; then
    log_message "用法: $0 {start|stop|restart|status} <version>" error
    exit 1
fi

# 主执行逻辑
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
    status)
        status_project "$2"
        ;;
    *)
        log_message "未知命令: $1" error
        log_message "用法: $0 {start|stop|restart|status} <version>" warn
        exit 1
        ;;
esac

exit 0
```
---
## 8 Dockerfile
1. Dockerfile 文件内容
```dockerfile
# 基础镜像
FROM openjdk:21-jdk-alpine

# 构建参数（服务名和版本号）
ARG SERVICE_NAME=oneself-demo
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
EXPOSE 8080

# 启动命令
ENTRYPOINT ["/bin/sh", "-c", "./run.sh start ${SERVICE_VERSION}"]
```
2. 构建镜像：`docker build --build-arg SERVICE_NAME=oneself-demo --build-arg SERVICE_VERSION=1.0.0 --build-arg ENV_PROFILE=prod -t oneself-demo:1.0.0 .`
3. 运行容器：`docker run -e SPRING_PROFILES_ACTIVE=prod -d -p 8080:8080 oneself-demo:1.0.0`
---
## 9 配置
### 9.1 开启 Swagger
1. 引入依赖 oneself-common
```xml
<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-common-swagger</artifactId>
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
### 9.2 配置 FTP/SFTP
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
### 9.3 配置 Nacos
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
### 9.4 配置 Sentinel
1. 引入依赖 Sentinel 依赖
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```
2. 在 `application.yml` 中添加以下配置：
```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: 127.0.0.1:8090 # Sentinel 地址

feign:
  sentinel:
    enabled: true # 开启 feign 对 Sentinel 的支持
```
---
### 9.5 配置 Elasticsearch
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
### 9.6 多数据源配置
```yaml
spring:
  datasource:
    dynamic:
      primary: postgres  # 默认数据源（这里选 postgres，也可以改 mysql）
      strict: false
      datasource:
        mysql:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://127.0.0.1:3306/oneself_demo?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai&useSSL=false
          username: liuhuan
          password: Z6eQaNaitK5vSX
        postgres:
          driver-class-name: org.postgresql.Driver
          url: jdbc:postgresql://127.0.0.1:5432/oneself?currentSchema=oneself_demo&useUnicode=true&characterEncoding=utf-8
          username: admin
          password: PK3uK7pUIwUTi1

      druid: # ================== 全局 Druid 连接池配置 ==================
        # -------- 基本参数 --------
        initial-size: 10       # 初始连接数（服务启动时就建 10 个连接）
        min-idle: 10           # 最小空闲连接数（保证一定的可用连接）
        max-active: 100        # 最大活跃连接数（并发高时最多可用 100 个连接）
        max-wait: 60000        # 获取连接最大等待时间（60s），超过报错

        # -------- 空闲连接检测 --------
        time-between-eviction-runs-millis: 60000   # 每 60s 检测一次空闲连接
        min-evictable-idle-time-millis: 300000     # 连接空闲超过 5 分钟回收
        keep-alive: true                          # 保持一定数量的连接长期不被回收

        # -------- SQL 有效性检测 --------
        validation-query: SELECT 1                # 检测连接有效性的 SQL
        test-while-idle: true                     # 空闲检测时执行 validation-query
        test-on-borrow: false                     # 取连接时不检测（性能更优）
        test-on-return: false                     # 还连接时不检测（性能更优）

        # -------- PSCache（预编译 SQL 缓存）--------
        pool-prepared-statements: true
        max-pool-prepared-statement-per-connection-size: 50 # 每个连接缓存 50 条 SQL
        share-prepared-statements: true            # 是否多个连接共用缓存（一般打开）

        # -------- 监控与日志 --------
        filters: stat,wall,slf4j   # 开启统计（stat）、防火墙（wall）、SQL 日志（slf4j）

        # -------- 统计参数 --------
        stat:
          merge-sql: true              # 合并统计相同 SQL
          slow-sql-millis: 5000        # 慢 SQL 阈值（毫秒），大于 5s 记为慢 SQL
          log-slow-sql: true           # 打印慢 SQL 日志

        # -------- 防火墙参数（WallFilter）--------
        wall:
          config:
            multi-statement-allow: true  # 允许一次执行多条 SQL（默认 false）
            none-base-statement-allow: false # 禁止无 where 的 update/delete
```
---
### 9.7 服务间调用
1. 调用方引入负载均衡依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-loadbalancer</artifactId>
</dependency>
```
2. 调用方引入对应服务的 api 依赖
```xml

<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-demo-api</artifactId>
    <version>1.0.0</version>
</dependency>
```
3. 调用方启动类新增注解 `@EnableFeignClients(basePackages = "com.oneself.client")`
---
### 9.8 配置 JWT 密钥
1. 生成 JWT 密钥（至少32个字符，推荐64个字符）
```bash
# 使用 openssl 生成64位十六进制密钥（推荐）
openssl rand -hex 32

# 或生成32位十六进制密钥（最小要求）
openssl rand -hex 16
```

2. 设置环境变量
```bash
# Linux/Mac
export ONESELF_JWT_SECRET=$(openssl rand -hex 32)
export ONESELF_JWT_ISSUER=oneself  # 可选，默认为 "oneself"

# Windows (PowerShell)
$env:ONESELF_JWT_SECRET = (openssl rand -hex 32)
$env:ONESELF_JWT_ISSUER = "oneself"  # 可选，默认为 "oneself"
```

3. Docker 环境变量配置
```dockerfile
# 在 Dockerfile 中
ENV ONESELF_JWT_SECRET=your-secret-key-here-must-be-at-least-32-chars
ENV ONESELF_JWT_ISSUER=oneself
```

4. Docker Compose 配置
```yaml
services:
  oneself-auth:
    environment:
      - ONESELF_JWT_SECRET=${ONESELF_JWT_SECRET}
      - ONESELF_JWT_ISSUER=oneself
```

5. Kubernetes 配置
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: jwt-secret
type: Opaque
stringData:
  ONESELF_JWT_SECRET: "your-secret-key-here-must-be-at-least-32-chars"
  ONESELF_JWT_ISSUER: "oneself"
```

**注意事项：**
- JWT 密钥必须至少32个字符（256位）
- 生产环境必须使用强随机密钥，不要使用默认值
- 密钥一旦设置，不要随意更改，否则会导致所有已签发的 Token 失效
- 建议将密钥存储在安全的密钥管理系统中（如 Vault、AWS Secrets Manager 等）
---
# 问题记录
1. JDK 21 中使用 knife4j-openapi3 如何配置 Swagger 在生产环境中关闭
```yaml
knife4j:
  enable: true # 开启增强配置，启用 Knife4j 功能
  production: true # 是否开启生产环境保护策略，生产环境中会隐藏 Swagger 界面
```
2. 深拷贝过程中使用了 writeValueAsString 和 readValue，导致原始对象的类型信息和泛型信息在反序列化时丢失问题（SensitiveDataUtils 38 - 41 行）
3. 服务间调用时，用户信息认证问题
4. Nacos 2.4.2 无法读取到 nacos 配置中心配置文件问题
> 版本问题， NacosConfigAutoConfiguration 类不能注入到 Spring 容器内，导致配置的类不能正确读取到
解决方案：
```yaml
spring:
  config:
    import:
      - nacos:${spring.application.name}-${spring.profiles.active}.yaml?refreshEnabled=true
```