# FastLCDP Login Module

这是FastLCDP项目的登录模块，基于Spring Boot框架构建。

## 项目结构

```
Login/
├── pom.xml                                    # Maven配置文件
├── README.md                                  # 项目说明文档
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fastlcdp/login/
│   │   │       ├── LoginApplication.java     # 主应用类
│   │   │       └── controller/
│   │   │           └── LoginController.java  # 登录控制器
│   │   └── resources/
│   │       └── application.properties        # 应用配置文件
│   └── test/
│       ├── java/
│       │   └── com/fastlcdp/login/
│       │       └── LoginApplicationTest.java # 测试类
│       └── resources/
│           └── application-test.properties   # 测试配置文件
```

## 技术栈

- Java 11
- Spring Boot 2.7.8
- Maven 3.x
- JUnit 5

## 快速开始

### 编译项目

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 启动应用

```bash
mvn spring-boot:run
```

应用启动后，可以访问以下端点：

- 健康检查: http://localhost:8080/api/login/health
- 应用信息: http://localhost:8080/api/login/info

### 打包应用

```bash
mvn clean package
```

## 开发说明

这是一个基础的Maven + Spring Boot项目框架，包含了：

1. 标准的Maven目录结构
2. Spring Boot主应用类
3. 示例控制器
4. 基本的配置文件
5. 单元测试框架

可以在此基础上继续开发登录相关的功能。