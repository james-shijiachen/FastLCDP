# FastLCDP - 快速低代码数据库平台

**中文** | [English](README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)
[![GitHub Issues](https://img.shields.io/github/issues/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/issues)
[![GitHub Stars](https://img.shields.io/github/stars/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/stargazers)

## 项目简介

一个基于Spring Boot的XML表定义解析和数据库表生成框架，支持多种数据库类型、表继承、XSD Schema验证和元数据管理。

## 🚀 功能特性

### 核心功能
- ✅ 解析XML格式的数据库表定义
- ✅ 生成标准的DDL语句
- ✅ 支持多种数据库类型（H2、MySQL、PostgreSQL、Oracle、SQL Server）
- ✅ 提供REST API接口
- ✅ 支持表结构验证
- ✅ 元数据存储和管理
- ✅ 数据库方言支持
- ✅ **XSD Schema验证**: 提供完整的XML Schema定义和验证
- ✅ **表继承功能**: 支持表结构继承，减少重复定义
- ✅ **配置验证工具**: 提供XML配置验证和Schema生成工具

### 高级功能
- 🆕 **多数据库方言**: 根据配置生成不同数据库的SQL语句
- 🆕 **元数据管理**: 将XML元数据保存到数据库中进行版本管理
- 🆕 **表继承机制**: 支持多层次表继承，自动合并父表字段、索引和关系
- 🆕 **XSD Schema**: 提供完整的XML Schema定义文件，支持IDE智能提示
- 🆕 **验证工具集**: 包含XML配置验证、Schema生成等实用工具

## 📋 环境要求

- Java 21+
- Maven 3.6+
- Spring Boot 3.3+

### 安装和运行

1. **克隆项目**
```bash
git clone https://github.com/james-shijiachen/fastLCDP.git
cd FastLCDP
```

2. **配置数据库**

编辑 `src/main/resources/application.yml` 文件，配置数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
```

3. **编译和运行**
```bash
mvn clean install
mvn spring-boot:run
```

4. **访问应用**
- 应用地址: http://localhost:8080
- H2控制台 (开发环境): http://localhost:8080/h2-console
- 健康检查: http://localhost:8080/api/table-generator/status

## XML格式说明

### 基本结构

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="数据库名" version="版本" charset="字符集" comment="数据库注释">
    <tables>
        <table name="表名" comment="表注释" engine="存储引擎">
            <fields>
                <!-- 字段定义 -->
            </fields>
            <indexes>
                <!-- 索引定义 -->
            </indexes>
            <relations>
                <!-- 外键关联定义 -->
            </relations>
        </table>
    </tables>
</database>
```

### 字段定义

```xml
<field name="字段名" 
       type="字段类型" 
       length="长度" 
       scale="小数位数" 
       nullable="是否允许为空" 
       primaryKey="是否为主键" 
       autoIncrement="是否自增" 
       defaultValue="默认值" 
       unique="是否唯一" 
       comment="字段注释"/>
```

**支持的字段类型:**
- 整数类型: `INTEGER`, `LONG`
- 浮点类型: `DECIMAL`
- 字符串类型: `CHAR`, `STRING`, `TEXT`
- 日期时间类型: `DATETIME`
- 二进制类型: `BLOB`
- 布尔类型: `BOOLEAN`
- JSON类型: `JSON`

> 注意：字段类型必须严格按照XSD Schema定义使用，详见 `src/main/resources/database-schema.xsd`

### 索引定义

```xml
<index name="索引名" type="索引类型" method="索引方法" comment="索引注释">
    <columns>
        <column name="字段名" order="排序方向" length="索引长度"/>
    </columns>
</index>
```

**索引类型:**
- `PRIMARY`: 主键索引
- `UNIQUE`: 唯一索引
- `NORMAL`: 普通索引
- `FULLTEXT`: 全文索引
- `SPATIAL`: 空间索引

### 外键关联

```xml
<relation name="外键名" 
          column="本表字段" 
          referenceTable="引用表" 
          referenceColumn="引用字段" 
          onDelete="删除动作" 
          onUpdate="更新动作" 
          comment="关联注释"/>
```

**级联动作:**
- `CASCADE`: 级联操作
- `SET NULL`: 设置为NULL
- `RESTRICT`: 限制操作
- `NO ACTION`: 无动作
- `SET DEFAULT`: 设置为默认值

### 表继承

```xml
<table name="子表" extends="父表名" comment="继承示例">
    <!-- 子表会自动继承父表的字段、索引和关联关系 -->
    <fields>
        <!-- 可以添加子表特有的字段 -->
        <field name="extra_field" type="STRING" length="100" comment="子表特有字段"/>
    </fields>
</table>
```

### XSD Schema验证

项目提供了完整的XSD Schema定义文件 `src/main/resources/database-schema.xsd`，支持：

- **IDE智能提示**: 在支持XSD的IDE中编写XML时获得自动补全
- **语法验证**: 实时检查XML配置的语法正确性
- **类型约束**: 确保字段类型、索引类型等符合规范

在XML文件中引用XSD Schema：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="sample_db" version="1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <!-- 数据库定义 -->
</database>
```

## API接口

### 1. 生成数据库表

```http
POST /api/table-generator/generate
Content-Type: multipart/form-data

file: XML文件
```

**响应示例:**
```json
{
    "success": true,
    "message": "成功生成 5 个表",
    "databaseName": "sample_db",
    "tableCount": 5
}
```

### 2. 预览SQL语句

```http
POST /api/table-generator/preview
Content-Type: multipart/form-data

file: XML文件
```

**响应示例:**
```json
{
    "success": true,
    "message": "SQL预览生成成功",
    "sql": "CREATE DATABASE IF NOT EXISTS `sample_db`...\nCREATE TABLE IF NOT EXISTS `user`...",
    "databaseName": "sample_db",
    "tableCount": 5
}
```

### 3. 验证XML格式

```http
POST /api/table-generator/validate
Content-Type: multipart/form-data

file: XML文件
```

**响应示例:**
```json
{
    "valid": true,
    "message": "XML文件验证通过，包含 5 个表定义",
    "databaseName": "sample_db",
    "tableCount": 5
}
```

### 4. 系统状态

```http
GET /api/table-generator/status
```

**响应示例:**
```json
{
    "status": "running",
    "message": "XML表生成器服务正常运行",
    "timestamp": 1703123456789
}
```

## 示例文件

项目提供了两个示例XML文件：

1. **简单示例** (`examples/simple-example.xml`)
   - 包含用户表和订单表
   - 展示基本的字段定义和外键关联

2. **完整示例** (`examples/sample-database.xml`)
   - 包含用户权限管理系统的完整表结构
   - 展示表继承、复杂索引、多种字段类型等高级功能

## 使用示例

### 1. 定义一个简单的用户表

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="my_app" comment="我的应用数据库"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <table name="user" comment="用户表">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="用户ID"/>
                <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="用户名"/>
                <field name="email" type="STRING" length="100" nullable="false" comment="邮箱"/>
                <field name="password" type="STRING" length="255" nullable="false" comment="密码"/>
                <field name="created_at" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="创建时间"/>
            </fields>
            <indexes>
                <index name="uk_username" type="UNIQUE">
                    <columns>
                        <column name="username"/>
                    </columns>
                </index>
                <index name="idx_email" type="NORMAL">
                    <columns>
                        <column name="email"/>
                    </columns>
                </index>
            </indexes>
        </table>
    </tables>
</database>
```

### 2. 使用表继承

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="blog_system" charset="utf8mb4" collation="utf8mb4_general_ci"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <!-- 基础实体表 -->
        <table name="base_entity" comment="基础实体表">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="主键ID"/>
                <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="创建时间"/>
                <field name="updated_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" comment="更新时间"/>
                <field name="is_deleted" type="BOOLEAN" defaultValue="0" comment="是否删除"/>
            </fields>
        </table>
        
        <!-- 用户表继承基础实体表 -->
        <table name="user" extends="base_entity" comment="用户表">
            <fields>
                <field name="username" type="STRING" length="50" nullable="false" comment="用户名"/>
                <field name="email" type="STRING" length="100" nullable="false" comment="邮箱"/>
            </fields>
        </table>
        
        <!-- 文章表也继承基础实体表 -->
        <table name="article" extends="base_entity" comment="文章表">
            <fields>
                <field name="title" type="STRING" length="200" nullable="false" comment="标题"/>
                <field name="content" type="TEXT" comment="内容"/>
                <field name="author_id" type="LONG" nullable="false" comment="作者ID"/>
            </fields>
            <relations>
                <relation name="fk_article_author" column="author_id" referenceTable="user" referenceColumn="id" onDelete="CASCADE" onUpdate="CASCADE"/>
            </relations>
        </table>
    </tables>
</database>
```

## 开发指南

### 项目结构

```
examples/                                      # 示例文件
├── sample-database.xml                        # 完整示例
└── simple-example.xml                         # 简单示例
src/
├── main/
│   ├── java/cn/com/traninfo/fastlcdp/
│   │   ├── FastLcdpApplication.java             # 主应用类
│   │   ├── config/
│   │   │   └── DatabaseConfig.java            # 数据库配置
│   │   ├── controller/
│   │   │   └── TableGeneratorController.java  # REST控制器
│   │   ├── dialect/                           # 数据库方言支持
│   │   │   ├── AbstractDatabaseDialect.java   # 抽象方言基类
│   │   │   ├── DatabaseDialect.java           # 方言接口
│   │   │   ├── DatabaseDialectFactory.java    # 方言工厂
│   │   │   ├── H2Dialect.java                 # H2数据库方言
│   │   │   ├── MySQLDialect.java              # MySQL数据库方言
│   │   │   ├── OracleDialect.java             # Oracle数据库方言
│   │   │   ├── PostgreSQLDialect.java         # PostgreSQL数据库方言
│   │   │   └── SqlServerDialect.java          # SQL Server数据库方言
│   │   ├── model/
│   │   │   ├── DatabaseSchema.java            # 数据库模式模型
│   │   │   ├── FieldDefinition.java           # 字段定义模型
│   │   │   ├── IndexDefinition.java           # 索引定义模型
│   │   │   ├── MetadataEntity.java            # 元数据实体
│   │   │   ├── RelationDefinition.java        # 关联定义模型
│   │   │   └── TableDefinition.java           # 表定义模型
│   │   ├── repository/
│   │   │   └── MetadataRepository.java        # 元数据仓库
│   │   ├── service/
│   │   │   ├── DatabaseExecutorService.java   # 数据库执行服务
│   │   │   ├── MetadataService.java           # 元数据管理服务
│   │   │   ├── SqlGeneratorService.java       # SQL生成服务
│   │   │   ├── TableGeneratorService.java     # 核心业务服务
│   │   │   └── XmlParserService.java          # XML解析服务
│   │   └── util/
│   │       ├── XmlConfigValidator.java        # XML配置验证器
│   │       ├── XmlSchemaGenerator.java        # XSD Schema生成器
│   │       └── XmlSchemaValidator.java        # XSD Schema验证器
│   └── resources/
│       ├── application.yaml                   # 应用配置
│       └── database-schema.xsd                # XSD Schema定义
└── test/
    ├── java/cn/com/traninfo/fastlcdp/
    │   ├── IntegrationTest.java               # 集成测试
    │   ├── SimpleSqlTest.java                 # 简单SQL测试
    │   ├── TestXmlParser.java                 # XML解析测试
    │   ├── example/                           # 示例和演示代码
    │   │   ├── XmlSchemaValidationDemo.java   # Schema验证演示
    │   │   └── XmlValidationExample.java      # 验证示例
    │   ├── service/
    │   │   ├── InheritanceTest.java           # 表继承测试
    │   │   ├── SqlGeneratorServiceTest.java   # SQL生成测试
    │   │   └── XmlParserServiceTest.java      # XML解析测试
    │   └── util/
    │       └── XmlSchemaValidatorTest.java    # Schema验证器测试
    └── resources/
        ├── application-test.yaml              # 测试配置
        ├── test-inheritance.xml               # 继承测试文件
        └── test-schema.xml                    # Schema测试文件
```

### 扩展开发

1. **添加新的字段类型支持**
   - 修改 `src/main/resources/database-schema.xsd` 中的 `DataType` 枚举
   - 更新各数据库方言类中的类型映射
   - 添加相应的测试用例

2. **支持新的数据库类型**
   - 继承 `AbstractDatabaseDialect` 创建新的方言类
   - 在 `DatabaseDialectFactory` 中注册新方言
   - 实现特定数据库的SQL生成逻辑

3. **添加新的索引类型**
   - 修改 `database-schema.xsd` 中的 `IndexType` 枚举
   - 更新 `IndexDefinition` 模型
   - 在各方言类中实现新索引类型的SQL生成

4. **扩展XSD Schema**
   - 修改 `database-schema.xsd` 添加新元素或属性
   - 更新相应的模型类
   - 运行 `XmlSchemaValidatorTest` 确保兼容性

5. **添加验证规则**
   - 扩展 `XmlConfigValidator` 添加自定义验证逻辑
   - 在 `XmlSchemaValidator` 中添加业务规则验证

## 测试

### 运行单元测试

```bash
mvn test
```

### 运行集成测试

```bash
mvn verify
```

### 测试覆盖率

```bash
mvn jacoco:report
```

## 常见问题

### Q: 如何处理表名或字段名的关键字冲突？
A: 框架会自动为所有表名和字段名添加反引号(`)，避免与数据库关键字冲突。

### Q: 支持哪些数据库？
A: 目前支持H2、MySQL、PostgreSQL、Oracle和SQL Server。通过数据库方言机制，可以轻松扩展支持其他数据库。

### Q: 如何处理大型XML文件？
A: 框架使用流式解析，可以处理较大的XML文件。建议单个文件不超过10MB。

### Q: 表继承的深度有限制吗？
A: 理论上没有限制，但建议继承深度不超过3层，以保持结构清晰。支持多层继承和字段、索引、关系的自动合并。

### Q: 如何备份现有数据？
A: 框架只创建表结构，不会删除现有数据。建议在生产环境使用前先备份数据库。

### Q: XSD Schema验证失败怎么办？
A: 检查XML文件中的字段类型、索引类型等是否符合 `database-schema.xsd` 中的定义。常见问题包括使用了不支持的数据类型（如 `VARCHAR` 应改为 `STRING`）或排序规则（如 `utf8mb4_unicode_ci` 应改为 `utf8mb4_general_ci`）。

### Q: 如何在IDE中获得XML智能提示？
A: 在XML文件中添加XSD引用：`xsi:noNamespaceSchemaLocation="../database-schema.xsd"`，大多数现代IDE都会自动提供智能提示和语法检查。

### Q: 如何验证XML配置的正确性？
A: 可以使用项目提供的验证工具：
   - 运行 `XmlSchemaValidationDemo` 进行快速验证
   - 使用 `XmlSchemaValidator` 类进行编程验证
   - 通过REST API的 `/validate` 端点进行在线验证

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE) 文件了解详情。

## 联系方式

- 项目主页: [https://github.com/james-shijiachen/fastLCDP](https://github.com/james-shijiachen/fastLCDP)
- 问题反馈: [https://github.com/james-shijiachen/fastLCDP/issues](https://github.com/james-shijiachen/fastLCDP/issues)
- 邮箱: [shijiachen@traninfo.com.cn](mailto:shijiachen@traninfo.com.cn)

---

**FastLCDP Team** - 让数据库表创建更简单！🚀