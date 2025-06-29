# ERDesigner 后端

**中文** | [English](README.md)

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5+-blue.svg)](https://baomidou.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)

## 项目概述

ERDesigner后端是一个强大的Spring Boot应用程序，为ERDesigner前端提供全面的API服务。它处理ER图元数据管理、DDL生成、多数据库支持，并为数据库模式设计操作提供完整的REST API。

## 🚀 核心功能

### 基础功能
- 🗄️ **元数据管理** - 存储和管理ER图元数据，支持版本控制
- 🔄 **DDL生成** - 从ER图定义生成SQL DDL语句
- 🌐 **多数据库支持** - 支持H2、MySQL、PostgreSQL、Oracle和SQL Server
- 📊 **模式验证** - 验证实体关系和数据库约束
- 🔌 **REST API** - 全面的RESTful API支持所有操作
- 📈 **自动建表** - 基于实体注解自动创建数据库表

### 高级功能
- 🛡️ **安全性** - Spring Security集成，支持认证和授权
- 📝 **API文档** - OpenAPI 3.0规范和Swagger UI
- 🔍 **健康监控** - Spring Boot Actuator应用监控
- 🗃️ **数据库迁移** - 自动模式迁移和版本管理
- 🎯 **自定义验证** - ER图一致性的业务逻辑验证
- 📊 **审计日志** - 跟踪所有变更和操作

## 🏗️ 技术架构

### 框架技术栈
- **核心框架**: Spring Boot 3.3+
- **编程语言**: Java 21 现代特性
- **数据库访问**: MyBatis Plus 3.5+
- **安全框架**: Spring Security 6+
- **文档生成**: SpringDoc OpenAPI 3
- **监控工具**: Spring Boot Actuator
- **构建工具**: Maven 3.6+

### 数据库支持
- **H2** - 默认嵌入式数据库，用于开发
- **MySQL** - 生产就绪的关系型数据库
- **PostgreSQL** - 高级开源数据库
- **Oracle** - 企业级数据库解决方案
- **SQL Server** - 微软数据库平台

## 📋 环境要求

- Java 21+
- Maven 3.6+
- Spring Boot 3.3+
- 数据库 (H2/MySQL/PostgreSQL/Oracle/SQL Server)

## 🗄️ 数据库模式与XML配置

### XML格式说明

#### 基本结构

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="数据库名称" version="版本" charset="字符集" comment="数据库注释">
    <tables>
        <table name="表名" comment="表注释" engine="存储引擎">
            <fields>
                <!-- 字段定义 -->
            </fields>
            <indexes>
                <!-- 索引定义 -->
            </indexes>
            <relations>
                <!-- 外键关系定义 -->
            </relations>
        </table>
    </tables>
</database>
```

#### 字段定义

```xml
<field name="字段名" 
       type="字段类型" 
       length="长度" 
       scale="小数位数" 
       nullable="是否允许空" 
       primaryKey="是否主键" 
       autoIncrement="是否自增" 
       defaultValue="默认值" 
       unique="是否唯一" 
       comment="字段注释"/>
```

**支持的字段类型：**
- 整数类型：`INTEGER`、`LONG`
- 浮点类型：`DECIMAL`
- 字符串类型：`CHAR`、`STRING`、`TEXT`
- 日期时间类型：`DATETIME`
- 二进制类型：`BLOB`
- 布尔类型：`BOOLEAN`
- JSON类型：`JSON`

> 注意：字段类型必须严格遵循XSD Schema定义，详见 `src/main/resources/database-schema.xsd`

#### 索引定义

```xml
<index name="索引名" type="索引类型" method="索引方法" comment="索引注释">
    <columns>
        <column name="字段名" order="排序方向" length="索引长度"/>
    </columns>
</index>
```

**索引类型：**
- `PRIMARY`：主键索引
- `UNIQUE`：唯一索引
- `NORMAL`：普通索引
- `FULLTEXT`：全文索引
- `SPATIAL`：空间索引

#### 外键关系

```xml
<relation name="外键名" 
          column="本表字段" 
          referenceTable="引用表" 
          referenceColumn="引用字段" 
          onDelete="删除动作" 
          onUpdate="更新动作" 
          comment="关系注释"/>
```

**级联动作：**
- `CASCADE`：级联操作
- `SET NULL`：设置为NULL
- `RESTRICT`：限制操作
- `NO ACTION`：无动作
- `SET DEFAULT`：设置为默认值

#### 表继承

```xml
<table name="子表" extends="父表名" comment="继承示例">
    <!-- 子表自动继承父表的字段、索引和关系 -->
    <fields>
        <!-- 可以添加子表特有字段 -->
        <field name="extra_field" type="STRING" length="100" comment="子表特有字段"/>
    </fields>
</table>
```

#### XSD Schema验证

项目提供完整的XSD Schema定义文件 `src/main/resources/database-schema.xsd`，支持：

- **IDE智能提示**：在支持XSD的IDE中编写XML时获得自动补全
- **语法验证**：实时检查XML配置语法正确性
- **类型约束**：确保字段类型、索引类型等符合规范

在XML文件中引用XSD Schema：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="sample_db" version="1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <!-- 数据库定义 -->
</database>
```

### 使用示例

#### 1. 定义简单用户表

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

#### 2. 使用表继承

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

## 🔌 API接口

### 生成数据库表

**POST** `/api/database/generate`

**请求体：**
```json
{
  "xmlContent": "XML内容字符串",
  "databaseType": "mysql"
}
```

**响应：**
```json
{
  "success": true,
  "message": "数据库表生成成功",
  "data": {
    "tablesCreated": 5,
    "indexesCreated": 8,
    "foreignKeysCreated": 3
  }
}
```

### 预览SQL语句

**POST** `/api/database/preview`

**请求体：**
```json
{
  "xmlContent": "XML内容字符串",
  "databaseType": "mysql"
}
```

**响应：**
```json
{
  "success": true,
  "data": {
    "sqlStatements": [
      "CREATE TABLE user (...)",
      "CREATE INDEX idx_username ON user (username)",
      "ALTER TABLE article ADD CONSTRAINT fk_article_author ..."
    ]
  }
}
```

### 验证XML格式

**POST** `/api/database/validate`

**请求体：**
```json
{
  "xmlContent": "XML内容字符串"
}
```

**响应：**
```json
{
  "success": true,
  "valid": true,
  "message": "XML格式有效"
}
```

### 系统状态

**GET** `/api/system/status`

**响应：**
```json
{
  "status": "UP",
  "database": "Connected",
  "version": "1.0.0",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 📄 示例XML文件

### 简单示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="simple_blog" version="1.0" charset="utf8mb4" comment="简单博客数据库"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <table name="user" comment="用户表">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="用户ID"/>
                <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="用户名"/>
                <field name="email" type="STRING" length="100" nullable="false" comment="邮箱地址"/>
                <field name="created_at" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="创建时间"/>
            </fields>
            <indexes>
                <index name="uk_username" type="UNIQUE">
                    <columns>
                        <column name="username"/>
                    </columns>
                </index>
            </indexes>
        </table>
        
        <table name="post" comment="博客文章表">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="文章ID"/>
                <field name="title" type="STRING" length="200" nullable="false" comment="文章标题"/>
                <field name="content" type="TEXT" comment="文章内容"/>
                <field name="author_id" type="LONG" nullable="false" comment="作者ID"/>
                <field name="created_at" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="创建时间"/>
            </fields>
            <relations>
                <relation name="fk_post_author" column="author_id" referenceTable="user" referenceColumn="id" onDelete="CASCADE" onUpdate="CASCADE"/>
            </relations>
        </table>
    </tables>
</database>
```

### 完整示例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="ecommerce_system" version="2.1" charset="utf8mb4" collation="utf8mb4_general_ci" comment="电商系统数据库"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <!-- 基础实体表 -->
        <table name="base_entity" comment="基础实体表，包含通用字段">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="主键ID"/>
                <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="创建时间"/>
                <field name="updated_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" comment="最后更新时间"/>
                <field name="version" type="INTEGER" defaultValue="0" comment="乐观锁版本号"/>
                <field name="is_deleted" type="BOOLEAN" defaultValue="0" comment="软删除标记"/>
            </fields>
            <indexes>
                <index name="idx_created_time" type="NORMAL">
                    <columns>
                        <column name="created_time"/>
                    </columns>
                </index>
                <index name="idx_is_deleted" type="NORMAL">
                    <columns>
                        <column name="is_deleted"/>
                    </columns>
                </index>
            </indexes>
        </table>
        
        <!-- 用户表 -->
        <table name="user" extends="base_entity" comment="用户信息表">
            <fields>
                <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="用户名"/>
                <field name="email" type="STRING" length="100" nullable="false" unique="true" comment="邮箱地址"/>
                <field name="password_hash" type="STRING" length="255" nullable="false" comment="密码哈希"/>
                <field name="phone" type="STRING" length="20" comment="手机号码"/>
                <field name="avatar_url" type="STRING" length="500" comment="头像图片URL"/>
                <field name="status" type="STRING" length="20" defaultValue="ACTIVE" comment="用户状态"/>
                <field name="last_login_time" type="DATETIME" comment="最后登录时间"/>
                <field name="profile" type="JSON" comment="用户档案JSON数据"/>
            </fields>
            <indexes>
                <index name="uk_username" type="UNIQUE">
                    <columns>
                        <column name="username"/>
                    </columns>
                </index>
                <index name="uk_email" type="UNIQUE">
                    <columns>
                        <column name="email"/>
                    </columns>
                </index>
                <index name="idx_status" type="NORMAL">
                    <columns>
                        <column name="status"/>
                    </columns>
                </index>
                <index name="idx_last_login" type="NORMAL">
                    <columns>
                        <column name="last_login_time"/>
                    </columns>
                </index>
            </indexes>
        </table>
        
        <!-- 商品分类表 -->
        <table name="category" extends="base_entity" comment="商品分类表">
            <fields>
                <field name="name" type="STRING" length="100" nullable="false" comment="分类名称"/>
                <field name="description" type="TEXT" comment="分类描述"/>
                <field name="parent_id" type="LONG" comment="父分类ID"/>
                <field name="sort_order" type="INTEGER" defaultValue="0" comment="排序顺序"/>
                <field name="is_active" type="BOOLEAN" defaultValue="1" comment="是否激活"/>
            </fields>
            <indexes>
                <index name="idx_parent_id" type="NORMAL">
                    <columns>
                        <column name="parent_id"/>
                    </columns>
                </index>
                <index name="idx_sort_order" type="NORMAL">
                    <columns>
                        <column name="sort_order"/>
                    </columns>
                </index>
            </indexes>
            <relations>
                <relation name="fk_category_parent" column="parent_id" referenceTable="category" referenceColumn="id" onDelete="SET NULL" onUpdate="CASCADE"/>
            </relations>
        </table>
        
        <!-- 商品表 -->
        <table name="product" extends="base_entity" comment="商品信息表">
            <fields>
                <field name="name" type="STRING" length="200" nullable="false" comment="商品名称"/>
                <field name="description" type="TEXT" comment="商品描述"/>
                <field name="sku" type="STRING" length="100" nullable="false" unique="true" comment="商品编码"/>
                <field name="price" type="DECIMAL" length="10" scale="2" nullable="false" comment="商品价格"/>
                <field name="cost" type="DECIMAL" length="10" scale="2" comment="商品成本"/>
                <field name="stock_quantity" type="INTEGER" defaultValue="0" comment="库存数量"/>
                <field name="category_id" type="LONG" nullable="false" comment="分类ID"/>
                <field name="brand" type="STRING" length="100" comment="商品品牌"/>
                <field name="weight" type="DECIMAL" length="8" scale="3" comment="商品重量(kg)"/>
                <field name="dimensions" type="JSON" comment="商品尺寸JSON"/>
                <field name="images" type="JSON" comment="商品图片JSON数组"/>
                <field name="attributes" type="JSON" comment="商品属性JSON"/>
                <field name="is_active" type="BOOLEAN" defaultValue="1" comment="是否激活"/>
                <field name="featured" type="BOOLEAN" defaultValue="0" comment="是否推荐商品"/>
            </fields>
            <indexes>
                <index name="uk_sku" type="UNIQUE">
                    <columns>
                        <column name="sku"/>
                    </columns>
                </index>
                <index name="idx_category_id" type="NORMAL">
                    <columns>
                        <column name="category_id"/>
                    </columns>
                </index>
                <index name="idx_price" type="NORMAL">
                    <columns>
                        <column name="price"/>
                    </columns>
                </index>
                <index name="idx_brand" type="NORMAL">
                    <columns>
                        <column name="brand"/>
                    </columns>
                </index>
                <index name="idx_is_active_featured" type="NORMAL">
                    <columns>
                        <column name="is_active"/>
                        <column name="featured"/>
                    </columns>
                </index>
                <index name="ft_name_description" type="FULLTEXT">
                    <columns>
                        <column name="name"/>
                        <column name="description"/>
                    </columns>
                </index>
            </indexes>
            <relations>
                <relation name="fk_product_category" column="category_id" referenceTable="category" referenceColumn="id" onDelete="RESTRICT" onUpdate="CASCADE"/>
            </relations>
        </table>
    </tables>
</database>
```

## 📋 环境要求

- Java 21 或更高版本
- Maven 3.6 或更高版本

## 🛠️ 开发指南

### 项目结构

```
ERDesigner/backend/
├── src/main/java/
│   └── com/fastlcdp/erdesigner/
│       ├── controller/          # REST API控制器
│       ├── service/            # 业务逻辑服务
│       ├── model/              # 数据模型
│       ├── parser/             # XML解析器
│       ├── generator/          # SQL生成器
│       ├── validator/          # 验证器
│       └── config/             # 配置类
├── src/main/resources/
│   ├── application.yml         # 应用配置
│   ├── database-schema.xsd     # XML Schema定义
│   └── sql/                    # SQL脚本
├── src/test/                   # 测试代码
└── pom.xml                     # Maven配置
```

### 扩展开发

#### 添加新的数据库类型

1. 在 `DatabaseType` 枚举中添加新类型：
```java
public enum DatabaseType {
    MYSQL, POSTGRESQL, ORACLE, SQLSERVER, H2, SQLITE, NEWDB
}
```

2. 创建对应的SQL生成器：
```java
@Component
public class NewDbSqlGenerator implements SqlGenerator {
    @Override
    public String generateCreateTableSql(Table table) {
        // 实现新数据库的建表SQL生成逻辑
    }
}
```

3. 在 `SqlGeneratorFactory` 中注册：
```java
@Component
public class SqlGeneratorFactory {
    public SqlGenerator getGenerator(DatabaseType type) {
        return switch (type) {
            case NEWDB -> new NewDbSqlGenerator();
            // 其他数据库类型...
        };
    }
}
```

#### 添加新的字段类型

1. 在 `FieldType` 枚举中添加新类型：
```java
public enum FieldType {
    STRING, INTEGER, LONG, DECIMAL, BOOLEAN, 
    DATETIME, DATE, TIME, TEXT, JSON, BLOB, UUID
}
```

2. 更新类型映射器：
```java
@Component
public class TypeMapper {
    public String mapToSqlType(FieldType fieldType, DatabaseType dbType) {
        // 添加新字段类型的SQL映射逻辑
    }
}
```

## 🧪 测试方法

### 单元测试

运行所有单元测试：
```bash
mvn test
```

运行特定测试类：
```bash
mvn test -Dtest=XmlParserTest
```

### 集成测试

运行集成测试：
```bash
mvn verify
```

使用特定数据库进行测试：
```bash
mvn test -Dspring.profiles.active=test-mysql
```

### 测试覆盖率

生成测试覆盖率报告：
```bash
mvn jacoco:report
```

查看覆盖率报告：
```bash
open target/site/jacoco/index.html
```

## ❓ 常见问题

### Q: 如何处理数据库关键字冲突？

**A:** 系统会自动检测并处理关键字冲突：

```xml
<!-- 系统会自动为关键字添加反引号或方括号 -->
<field name="order" type="STRING" length="50"/>
<!-- 生成的SQL: `order` VARCHAR(50) (MySQL) 或 [order] VARCHAR(50) (SQL Server) -->
```

### Q: 支持哪些数据库？

**A:** 目前支持以下数据库：
- MySQL 5.7+
- PostgreSQL 10+
- Oracle 11g+
- SQL Server 2012+
- H2 Database
- SQLite 3.0+

### Q: 如何处理大型XML文件？

**A:** 对于大型XML文件，建议：

1. 使用流式解析：
```java
@Service
public class StreamingXmlParser {
    public void parseXmlStream(InputStream xmlStream) {
        // 使用SAX或StAX进行流式解析
    }
}
```

2. 分批处理表定义：
```java
@Service
public class BatchTableProcessor {
    @Value("${app.batch.size:100}")
    private int batchSize;
    
    public void processTables(List<Table> tables) {
        // 分批处理表定义
    }
}
```

### Q: 表继承的最大深度是多少？

**A:** 理论上没有限制，但建议不超过5层，以避免：
- 性能问题
- 维护复杂性
- 循环继承检测开销

### Q: 如何备份生成的数据？

**A:** 建议使用数据库原生备份工具：

```bash
# MySQL
mysqldump -u username -p database_name > backup.sql

# PostgreSQL
pg_dump -U username database_name > backup.sql

# SQL Server
sqlcmd -S server -E -Q "BACKUP DATABASE [database_name] TO DISK='backup.bak'"
```

### Q: XSD Schema验证失败怎么办？

**A:** 检查以下几点：

1. XML声明是否正确：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<database xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
```

2. 必需属性是否缺失：
```xml
<!-- 错误：缺少name属性 -->
<table comment="用户表">

<!-- 正确 -->
<table name="user" comment="用户表">
```

3. 数据类型是否有效：
```xml
<!-- 错误：无效的数据类型 -->
<field name="id" type="INVALID_TYPE"/>

<!-- 正确 -->
<field name="id" type="LONG"/>
```

### Q: 如何在IDE中获得XML智能提示？

**A:** 配置IDE使用XSD Schema：

1. **IntelliJ IDEA:**
   - File → Settings → Languages & Frameworks → Schemas and DTDs
   - 添加 `database-schema.xsd` 文件

2. **Eclipse:**
   - Window → Preferences → XML → XML Catalog
   - 添加Schema Location映射

3. **VS Code:**
   - 安装XML扩展
   - 在settings.json中配置：
```json
{
  "xml.catalogs": [
    "path/to/database-schema.xsd"
  ]
}
```

### Q: 如何验证XML配置的正确性？

**A:** 使用验证API：

```bash
curl -X POST http://localhost:8080/api/database/validate \
  -H "Content-Type: application/json" \
  -d '{"xmlContent": "<?xml version=\"1.0\"?>..."}'
```

或使用在线验证工具验证XSD Schema合规性。
- 数据库（默认使用H2嵌入式数据库）

## 📊 监控和健康检查

### 健康检查端点
- `GET /actuator/health` - 应用健康状态
- `GET /actuator/info` - 应用信息
- `GET /actuator/metrics` - 应用指标

### 日志记录
日志使用Log4j2配置，存储位置：
- 开发环境：控制台输出
- 文件输出：`logs/erdesigner-backend.log`
- 生产环境：JSON格式

## 🔒 安全性

### 认证
应用支持多种认证方式：
- JWT令牌认证
- 会话认证
- 服务间调用的API密钥认证

### 授权
基于角色的访问控制（RBAC），包含以下角色：
- `USER` - 基本图表创建和编辑
- `ADMIN` - 完整系统管理
- `VIEWER` - 只读访问

---

**ERDesigner后端** 是 [ERDesigner](../README.md) 项目的一部分，隶属于 [FastLCDP](../../README.md) 平台。