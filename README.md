# 项目结构
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

# 目录结构
## 服务目录结构
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
     │           ├── handler          # 处理器包，处理具体的业务逻辑或事件（如事件处理器、任务处理器等）
     │           ├── mapper           # 数据访问层包，存放 MyBatis 映射接口及 XML 文件，用于数据库操作
     │           ├── model            # 数据模型包，包含业务数据模型相关的类，通常包括：
     │           │   ├── dto          # 数据传输对象（DTO）包，用于服务之间传输数据或从 API 接口接收数据
     │           │   ├── entity       # 实体类包，对应数据库表的实体映射，通常使用 JPA 或 MyBatis 映射
     │           │   ├── enums        # 枚举类包，定义项目中使用的枚举类型（如状态码、类型分类等）
     │           │   └── vo           # 视图对象（VO）包，用于向前端展示的返回数据结构
     │           ├── service          # 业务逻辑层包，存放服务接口及实现类，负责具体业务逻辑的处理
     │           ├── utils            # 工具类包，存放通用工具方法类，如日期工具、字符串处理工具等
     │           └── xxApplication.java  # Spring Boot 启动类，项目的入口，负责启动整个 Spring Boot 应用
     └── resource
         ├── mapper                      # 存放 MyBatis 的 XML 映射文件（如 SQL 查询语句）
         ├── application.yml             # Spring Boot 主配置文件，存放全局应用配置（如端口、日志级别等）
         ├── application-环境.yml        # 环境配置文件，根据不同环境（开发、生产等）配置不同参数
         ├── log4j2.xml                  # 日志配置文件，配置 Log4j2 的日志输出格式、日志级别等
         ├── run.sh                      # 启动脚本，通常用于容器化或在服务器中自动化运行应用
     
Dockerfile                           # Docker 配置文件，定义如何构建项目的 Docker 镜像
```
---
## 服务 API 目录结构
```text
main
 └── java
     └── com
         └── oneself
             ├── model                # 数据模型包，包含服务端与前端的数据传输结构
             │   ├── dto              # 数据传输对象（DTO）包，用于服务之间的数据传输
             │   ├── entity           # 实体类包，对应数据库表的实体映射，通常与数据库相关
             │   ├── enums            # 枚举类包，定义项目中使用的枚举，如状态、类型等
             │   └── vo               # 视图对象（VO）包，用于表示前端需要的返回数据结构
             ├── client               # 客户端相关模型，通常存放与外部服务交互的模型
             │   └── fallback         # 服务降级的回退类，通常用于定义外部调用失败时的回退逻辑             
            
```
---
# 标准启动类
```java
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(DemoApplication.class, args);
        // 使用工具类 打印启动信息
        ApplicationStartupUtils.printStartupInfo(application.getEnvironment());
    }
}
```
---
# 服务打包配置
```xml

<build>
    <!-- 项目默认目标，设置为编译目标 -->
    <defaultGoal>compile</defaultGoal>

    <!-- 项目打包后的名称 -->
    <finalName>${project.artifactId}</finalName>

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
# 环境
- JDK 1.8
- Spring Boot 2.7.18
- Spring Cloud 2021.0.9
- Spring Cloud Alibaba 2021.0.6.2
- Nacos 2.4.1
- MySQL 8.0.39
- Maven 3.9.9
---
# 配置
## 开启 Swagger
1. 引入依赖 oneself-common
```xml
<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
2. 在 `application.yml` 中添加以下配置：
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
---
## 配置 FTP/SFTP
1. 引入依赖 oneself-common
```xml
<dependency>
    <groupId>com.oneself</groupId>
    <artifactId>oneself-common</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
2. 在 `application.yml` 中添加以下配置：
```yml
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
## 配置 Nacos
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

