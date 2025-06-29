# ERDesigner Backend

[中文](README.zh.md) | **English**

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5+-blue.svg)](https://baomidou.com/)

## Overview

ERDesigner Backend is a Spring Boot application that provides API services for database schema design and management. It supports XML-based database schema definition, multi-database DDL generation, and comprehensive REST API services.

## 🚀 Core Features

### Basic Features
- 🗄️ **XML Schema Definition** - Define database schemas using XML configuration
- 🔄 **DDL Generation** - Generate SQL DDL statements for multiple databases
- 🌐 **Multi-Database Support** - Support for MySQL, PostgreSQL, Oracle, SQL Server, H2, SQLite
- 📊 **Schema Validation** - Validate XML configuration and database constraints
- 🔌 **REST API** - Complete RESTful API for all operations
- 📈 **Table Inheritance** - Support for table inheritance relationships

### Advanced Features
- 🛡️ **XSD Schema Validation** - Complete XSD schema definition with IDE smart prompts
- 📝 **API Documentation** - OpenAPI 3.0 specification with Swagger UI
- 🔍 **Health Monitoring** - Spring Boot Actuator for application monitoring
- 🗃️ **Keyword Handling** - Automatic handling of database keyword conflicts
- 🎯 **Batch Processing** - Support for large XML file processing
- 📊 **Audit Logging** - Track all database operations

## 🏗️ Technical Architecture

### Framework Stack
- **Core Framework**: Spring Boot 3.3+
- **Language**: Java 21+
- **Database Access**: MyBatis Plus 3.5+
- **Security**: Spring Security 6+
- **Documentation**: SpringDoc OpenAPI 3
- **Monitoring**: Spring Boot Actuator
- **Build Tool**: Maven 3.6+

### Database Support
- **MySQL** - Production-ready relational database
- **PostgreSQL** - Advanced open-source database
- **Oracle** - Enterprise database solution
- **SQL Server** - Microsoft database platform
- **H2** - Embedded database for development
- **SQLite** - Lightweight database solution

## 📋 Environment Requirements

- Java 21+
- Maven 3.6+
- Database (default uses H2 embedded database)

## 🗄️ Database Schema & XML Configuration

### XML Format Description

#### Basic Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="Database Name" version="Version" charset="Character Set" comment="Database Comment">
    <tables>
        <table name="Table Name" comment="Table Comment" engine="Storage Engine">
            <fields>
                <!-- Field definitions -->
            </fields>
            <indexes>
                <!-- Index definitions -->
            </indexes>
            <relations>
                <!-- Foreign key relationship definitions -->
            </relations>
        </table>
    </tables>
</database>
```

#### Field Definition

```xml
<field name="Field Name" 
       type="Field Type" 
       length="Length" 
       scale="Decimal Places" 
       nullable="Allow Null" 
       primaryKey="Is Primary Key" 
       autoIncrement="Auto Increment" 
       defaultValue="Default Value" 
       unique="Is Unique" 
       comment="Field Comment"/>
```

**Supported Field Types:**
```
STRING, INTEGER, LONG, DECIMAL, BOOLEAN, 
DATETIME, DATE, TIME, TEXT, JSON, BLOB, UUID
```

> Note: Field types must strictly follow XSD Schema definitions, see `src/main/resources/database-schema.xsd`

#### Index Definition

```xml
<index name="Index Name" type="Index Type" method="Index Method" comment="Index Comment">
    <columns>
        <column name="Field Name" order="Sort Direction" length="Index Length"/>
    </columns>
</index>
```

**Index Types:**
- `PRIMARY`: Primary key index
- `UNIQUE`: Unique index
- `NORMAL`: Normal index
- `FULLTEXT`: Full-text index
- `SPATIAL`: Spatial index

#### Foreign Key Relationships

```xml
<relation name="Foreign Key Name" 
          column="Local Table Field" 
          referenceTable="Referenced Table" 
          referenceColumn="Referenced Field" 
          onDelete="Delete Action" 
          onUpdate="Update Action" 
          comment="Relationship Comment"/>
```

**Cascade Actions:**
- `CASCADE`: Cascade operation
- `SET NULL`: Set to NULL
- `RESTRICT`: Restrict operation
- `NO ACTION`: No action
- `SET DEFAULT`: Set to default value

#### Table Inheritance

```xml
<table name="Child Table" extends="Parent Table Name" comment="Inheritance Example">
    <!-- Child table automatically inherits parent table fields, indexes and relationships -->
    <fields>
        <!-- Can add child table specific fields -->
        <field name="extra_field" type="STRING" length="100" comment="Child table specific field"/>
    </fields>
</table>
```

#### XSD Schema Validation

The project provides complete XSD Schema definition file `src/main/resources/database-schema.xsd`, supporting:

- **IDE Smart Prompts**: Get auto-completion when writing XML in XSD-supported IDEs
- **Syntax Validation**: Real-time checking of XML configuration syntax correctness
- **Type Constraints**: Ensure field types, index types etc. comply with specifications

Reference XSD Schema in XML files:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="sample_db" version="1.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <!-- Database definition -->
</database>
```

### Usage Examples

#### 1. Define a Simple User Table

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="my_app" comment="My Application Database"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <table name="user" comment="User Table">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="User ID"/>
                <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="Username"/>
                <field name="email" type="STRING" length="100" nullable="false" comment="Email"/>
                <field name="password" type="STRING" length="255" nullable="false" comment="Password"/>
                <field name="created_at" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="Creation Time"/>
            </fields>
            <indexes>
                <index name="uk_username" type="UNIQUE">
                    <columns>
                        <column name="username"/>
                    </columns>
                </index>
            </indexes>
        </table>
    </tables>
</database>
```
#### 2. Using Table Inheritance

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="blog_system" charset="utf8mb4" collation="utf8mb4_general_ci"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <!-- Base Entity Table -->
        <table name="base_entity" comment="Base Entity Table">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="Primary Key ID"/>
                <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="Creation Time"/>
                <field name="updated_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" comment="Update Time"/>
                <field name="is_deleted" type="BOOLEAN" defaultValue="0" comment="Is Deleted"/>
            </fields>
        </table>
        
        <!-- User Table Inherits Base Entity Table -->
        <table name="user" extends="base_entity" comment="User Table">
            <fields>
                <field name="username" type="STRING" length="50" nullable="false" comment="Username"/>
                <field name="email" type="STRING" length="100" nullable="false" comment="Email"/>
            </fields>
        </table>
    </tables>
</database>
```

## 🔌 API Interfaces

### Generate Database Tables

**POST** `/api/database/generate`

**Request Body:**
```json
{
  "xmlContent": "XML content string",
  "databaseType": "mysql"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Database tables generated successfully",
  "data": {
    "tablesCreated": 5,
    "indexesCreated": 8,
    "foreignKeysCreated": 3
  }
}
```

### Preview SQL Statements

**POST** `/api/database/preview`

**Request Body:**
```json
{
  "xmlContent": "XML content string",
  "databaseType": "mysql"
}
```

**Response:**
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

### Validate XML Format

**POST** `/api/database/validate`

**Request Body:**
```json
{
  "xmlContent": "XML content string"
}
```

**Response:**
```json
{
  "success": true,
  "valid": true,
  "message": "XML format is valid"
}
```

### System Status

**GET** `/api/system/status`

**Response:**
```json
{
  "status": "UP",
  "database": "Connected",
  "version": "1.0.0",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## 📄 Example XML Files

### Simple Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="simple_blog" version="1.0" charset="utf8mb4" comment="Simple Blog Database"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <table name="user" comment="User Table">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="User ID"/>
                <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="Username"/>
                <field name="email" type="STRING" length="100" nullable="false" comment="Email Address"/>
                <field name="created_at" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="Creation Time"/>
            </fields>
            <indexes>
                <index name="uk_username" type="UNIQUE">
                    <columns>
                        <column name="username"/>
                    </columns>
                </index>
            </indexes>
        </table>
        
        <table name="post" comment="Blog Post Table">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="Post ID"/>
                <field name="title" type="STRING" length="200" nullable="false" comment="Post Title"/>
                <field name="content" type="TEXT" comment="Post Content"/>
                <field name="author_id" type="LONG" nullable="false" comment="Author ID"/>
                <field name="created_at" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="Creation Time"/>
            </fields>
            <relations>
                <relation name="fk_post_author" column="author_id" referenceTable="user" referenceColumn="id" onDelete="CASCADE" onUpdate="CASCADE"/>
            </relations>
        </table>
    </tables>
</database>
```

### Complete Example

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="ecommerce_system" version="2.1" charset="utf8mb4" collation="utf8mb4_general_ci" comment="E-commerce System Database"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
    <tables>
        <!-- Base Entity Table -->
        <table name="base_entity" comment="Base Entity Table with Common Fields">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="Primary Key ID"/>
                <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="Creation Time"/>
                <field name="updated_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" comment="Last Update Time"/>
                <field name="version" type="INTEGER" defaultValue="0" comment="Optimistic Lock Version"/>
                <field name="is_deleted" type="BOOLEAN" defaultValue="0" comment="Soft Delete Flag"/>
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
        
        <!-- User Table -->
        <table name="user" extends="base_entity" comment="User Information Table">
            <fields>
                <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="Username"/>
                <field name="email" type="STRING" length="100" nullable="false" unique="true" comment="Email Address"/>
                <field name="password_hash" type="STRING" length="255" nullable="false" comment="Password Hash"/>
                <field name="phone" type="STRING" length="20" comment="Phone Number"/>
                <field name="avatar_url" type="STRING" length="500" comment="Avatar Image URL"/>
                <field name="status" type="STRING" length="20" defaultValue="ACTIVE" comment="User Status"/>
                <field name="last_login_time" type="DATETIME" comment="Last Login Time"/>
                <field name="profile" type="JSON" comment="User Profile JSON Data"/>
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
        
        <!-- Category Table -->
        <table name="category" extends="base_entity" comment="Product Category Table">
            <fields>
                <field name="name" type="STRING" length="100" nullable="false" comment="Category Name"/>
                <field name="description" type="TEXT" comment="Category Description"/>
                <field name="parent_id" type="LONG" comment="Parent Category ID"/>
                <field name="sort_order" type="INTEGER" defaultValue="0" comment="Sort Order"/>
                <field name="is_active" type="BOOLEAN" defaultValue="1" comment="Is Active"/>
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
        
        <!-- Product Table -->
        <table name="product" extends="base_entity" comment="Product Information Table">
            <fields>
                <field name="name" type="STRING" length="200" nullable="false" comment="Product Name"/>
                <field name="description" type="TEXT" comment="Product Description"/>
                <field name="sku" type="STRING" length="100" nullable="false" unique="true" comment="Stock Keeping Unit"/>
                <field name="price" type="DECIMAL" length="10" scale="2" nullable="false" comment="Product Price"/>
                <field name="cost" type="DECIMAL" length="10" scale="2" comment="Product Cost"/>
                <field name="stock_quantity" type="INTEGER" defaultValue="0" comment="Stock Quantity"/>
                <field name="category_id" type="LONG" nullable="false" comment="Category ID"/>
                <field name="brand" type="STRING" length="100" comment="Product Brand"/>
                <field name="weight" type="DECIMAL" length="8" scale="3" comment="Product Weight (kg)"/>
                <field name="dimensions" type="JSON" comment="Product Dimensions JSON"/>
                <field name="images" type="JSON" comment="Product Images JSON Array"/>
                <field name="attributes" type="JSON" comment="Product Attributes JSON"/>
                <field name="is_active" type="BOOLEAN" defaultValue="1" comment="Is Active"/>
                <field name="featured" type="BOOLEAN" defaultValue="0" comment="Is Featured Product"/>
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
        
        <!-- Order Table -->
        <table name="order" extends="base_entity" comment="Order Information Table">
            <fields>
                <field name="order_number" type="STRING" length="50" nullable="false" unique="true" comment="Order Number"/>
                <field name="user_id" type="LONG" nullable="false" comment="User ID"/>
                <field name="status" type="STRING" length="20" defaultValue="PENDING" comment="Order Status"/>
                <field name="total_amount" type="DECIMAL" length="12" scale="2" nullable="false" comment="Total Amount"/>
                <field name="shipping_address" type="JSON" comment="Shipping Address JSON"/>
                <field name="billing_address" type="JSON" comment="Billing Address JSON"/>
                <field name="payment_method" type="STRING" length="50" comment="Payment Method"/>
                <field name="payment_status" type="STRING" length="20" defaultValue="UNPAID" comment="Payment Status"/>
                <field name="shipping_method" type="STRING" length="50" comment="Shipping Method"/>
                <field name="tracking_number" type="STRING" length="100" comment="Tracking Number"/>
                <field name="notes" type="TEXT" comment="Order Notes"/>
                <field name="shipped_at" type="DATETIME" comment="Shipped Time"/>
                <field name="delivered_at" type="DATETIME" comment="Delivered Time"/>
            </fields>
            <indexes>
                <index name="uk_order_number" type="UNIQUE">
                    <columns>
                        <column name="order_number"/>
                    </columns>
                </index>
                <index name="idx_user_id" type="NORMAL">
                    <columns>
                        <column name="user_id"/>
                    </columns>
                </index>
                <index name="idx_status" type="NORMAL">
                    <columns>
                        <column name="status"/>
                    </columns>
                </index>
                <index name="idx_payment_status" type="NORMAL">
                    <columns>
                        <column name="payment_status"/>
                    </columns>
                </index>
                <index name="idx_created_time" type="NORMAL">
                    <columns>
                        <column name="created_time"/>
                    </columns>
                </index>
            </indexes>
            <relations>
                <relation name="fk_order_user" column="user_id" referenceTable="user" referenceColumn="id" onDelete="RESTRICT" onUpdate="CASCADE"/>
            </relations>
        </table>
        
        <!-- Order Item Table -->
        <table name="order_item" extends="base_entity" comment="Order Item Table">
            <fields>
                <field name="order_id" type="LONG" nullable="false" comment="Order ID"/>
                <field name="product_id" type="LONG" nullable="false" comment="Product ID"/>
                <field name="quantity" type="INTEGER" nullable="false" comment="Quantity"/>
                <field name="unit_price" type="DECIMAL" length="10" scale="2" nullable="false" comment="Unit Price"/>
                <field name="total_price" type="DECIMAL" length="12" scale="2" nullable="false" comment="Total Price"/>
                <field name="product_snapshot" type="JSON" comment="Product Snapshot JSON"/>
            </fields>
            <indexes>
                <index name="idx_order_id" type="NORMAL">
                    <columns>
                        <column name="order_id"/>
                    </columns>
                </index>
                <index name="idx_product_id" type="NORMAL">
                    <columns>
                        <column name="product_id"/>
                    </columns>
                </index>
                <index name="uk_order_product" type="UNIQUE">
                    <columns>
                        <column name="order_id"/>
                        <column name="product_id"/>
                    </columns>
                </index>
            </indexes>
            <relations>
                <relation name="fk_order_item_order" column="order_id" referenceTable="order" referenceColumn="id" onDelete="CASCADE" onUpdate="CASCADE"/>
                <relation name="fk_order_item_product" column="product_id" referenceTable="product" referenceColumn="id" onDelete="RESTRICT" onUpdate="CASCADE"/>
            </relations>
        </table>
    </tables>
</database>
```

## 🛠️ Development Guide

### Project Structure

```
ERDesigner/backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fastlcdp/erdesigner/
│   │   │       ├── ErDesignerApplication.java          # Main Application Class
│   │   │       ├── config/                            # Configuration Classes
│   │   │       ├── controller/                        # REST Controllers
│   │   │       ├── service/                           # Business Logic Services
│   │   │       ├── model/                             # Data Models
│   │   │       ├── repository/                        # Data Access Layer
│   │   │       ├── exception/                         # Custom Exceptions
│   │   │       └── util/                              # Utility Classes
│   │   └── resources/
│   │       ├── application.yml                        # Main Configuration
│   │       ├── database-schema.xsd                    # XSD Schema Definition
│   │       └── static/                                # Static Resources
│   └── test/
│       └── java/                                      # Unit Tests
├── pom.xml                                            # Maven Configuration
└── README.md                                          # Project Documentation
```

### Extension Development

#### Adding New Database Types

1. **Create Database Dialect**
```java
@Component
public class PostgreSQLDialect implements DatabaseDialect {
    @Override
    public String getDialectName() {
        return "postgresql";
    }
    
    @Override
    public String generateCreateTableSQL(Table table) {
        // Implementation for PostgreSQL specific SQL
    }
}
```

2. **Update Type Mapper**
```java
@Component
public class TypeMapper {
    public String mapToSqlType(FieldType fieldType, DatabaseType dbType) {
        // Add new field type SQL mapping logic
    }
}
```

## 🧪 Testing

### Unit Tests

Run all unit tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=XmlParserTest
```

### Integration Tests

Run integration tests:
```bash
mvn verify
```

Test with specific database:
```bash
mvn test -Dspring.profiles.active=test-mysql
```

### Test Coverage

Generate test coverage report:
```bash
mvn jacoco:report
```

View coverage report:
```bash
open target/site/jacoco/index.html
```

## 📊 Monitoring and Health Checks

### Health Check Endpoints
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Logging
Logs are configured with Log4j2 and stored in:
- Development: Console output
- File output: `logs/erdesigner-backend.log`
- Production: JSON format

## 🔒 Security

### Authentication
The application supports multiple authentication methods:
- JWT token authentication
- Session-based authentication
- API key authentication for service-to-service calls

### Authorization
Role-based access control (RBAC) with the following roles:
- `USER` - Basic diagram creation and editing
- `ADMIN` - Full system administration
- `VIEWER` - Read-only access

## ❓ FAQ

### Q: How to handle database keyword conflicts?

**A:** The system automatically detects and handles keyword conflicts:

```xml
<!-- System automatically adds backticks or brackets for keywords -->
<field name="order" type="STRING" length="50"/>
<!-- Generated SQL: `order` VARCHAR(50) (MySQL) or [order] VARCHAR(50) (SQL Server) -->
```

### Q: Which databases are supported?

**A:** Currently supports the following databases:
- MySQL 5.7+
- PostgreSQL 10+
- Oracle 11g+
- SQL Server 2012+
- H2 Database
- SQLite 3.0+

### Q: How to handle large XML files?

**A:** For large XML files, it is recommended to:

1. Use streaming parsing:
```java
@Service
public class StreamingXmlParser {
    public void parseXmlStream(InputStream xmlStream) {
        // Use SAX or StAX for streaming parsing
    }
}
```

2. Process table definitions in batches:
```java
@Service
public class BatchTableProcessor {
    @Value("${app.batch.size:100}")
    private int batchSize;
    
    public void processTables(List<Table> tables) {
        // Process table definitions in batches
    }
}
```

### Q: What is the maximum depth for table inheritance?

**A:** There is no theoretical limit, but it is recommended not to exceed 5 levels to avoid:
- Performance issues
- Maintenance complexity
- Circular inheritance detection overhead

### Q: How to backup generated data?

**A:** It is recommended to use native database backup tools:

```bash
# MySQL
mysqldump -u username -p database_name > backup.sql

# PostgreSQL
pg_dump -U username database_name > backup.sql

# SQL Server
sqlcmd -S server -E -Q "BACKUP DATABASE [database_name] TO DISK='backup.bak'"
```

### Q: What to do when XSD Schema validation fails?

**A:** Check the following points:

1. Check if XML declaration is correct:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<database xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:noNamespaceSchemaLocation="../database-schema.xsd">
```

2. Check if required attributes are missing:
```xml
<!-- Error: missing name attribute -->
<table comment="User table">

<!-- Correct -->
<table name="user" comment="User table">
```

3. Check if data types are valid:
```xml
<!-- Error: invalid data type -->
<field name="id" type="INVALID_TYPE"/>

<!-- Correct -->
<field name="id" type="LONG"/>
```

### Q: How to get XML intelligent hints in IDE?

**A:** Configure IDE to use XSD Schema:

1. **IntelliJ IDEA:**
   - File → Settings → Languages & Frameworks → Schemas and DTDs
   - Add `database-schema.xsd` file

2. **Eclipse:**
   - Window → Preferences → XML → XML Catalog
   - Add Schema Location mapping

3. **VS Code:**
   - Install XML extension
   - Configure in settings.json:
```json
{
  "xml.catalogs": [
    "path/to/database-schema.xsd"
  ]
}
```

### Q: How to validate XML configuration correctness?

**A:** Use validation API:

```bash
curl -X POST http://localhost:8080/api/database/validate \
  -H "Content-Type: application/json" \
  -d '{"xmlContent": "<?xml version=\"1.0\"?>..."}'
```

Or use online validation tools to verify XSD Schema compliance.

---

**ERDesigner Backend** is part of the [ERDesigner](../README.md) project, which belongs to the [FastLCDP](../../README.md) platform.