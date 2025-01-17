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
### 服务 API 目录结构
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
## 标准启动类
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
## 服务打包配置
### ~~服务打包配置 JDK 1.8~~
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
### 服务打包配置 JDK 21
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

## 环境（JDK 1.8 升级至 JDK 21）
- ~~JDK 1.8~~ -> JDK 21
- ~~Spring Boot 2.7.18~~ -> Spring Boot 3.2.12
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
    <version>0.0.1-SNAPSHOT</version>
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
    <version>0.0.1-SNAPSHOT</version>
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
    <version>0.0.1-SNAPSHOT</version>
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
## TODO
1. AOP 拦截器 @LogRequestDetails 在 SensitiveDataUtils 进行敏感信息处理范型数据时报错 java.lang.reflect.InaccessibleObjectException
2. oneself-quartz 模块功能未完成