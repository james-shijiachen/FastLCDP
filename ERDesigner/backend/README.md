# ERDesigner Backend

[中文](README.zh.md) | **English**

[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)
[![MyBatis Plus](https://img.shields.io/badge/MyBatis%20Plus-3.5+-blue.svg)](https://baomidou.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)

## Overview

The ERDesigner Backend is a robust Spring Boot application that provides comprehensive API services for the ERDesigner frontend. It handles ER diagram metadata management, DDL generation, multi-database support, and provides a complete REST API for database schema design operations.

## 🚀 Key Features

### Core Functionality
- 🗄️ **Metadata Management** - Store and manage ER diagram metadata with version control
- 🔄 **DDL Generation** - Generate SQL DDL statements from ER diagram definitions
- 🌐 **Multi-Database Support** - Support for H2, MySQL, PostgreSQL, Oracle, and SQL Server
- 📊 **Schema Validation** - Validate entity relationships and database constraints
- 🔌 **REST API** - Comprehensive RESTful API for all operations
- 📈 **Auto Table Creation** - Automatic database table creation based on entity annotations

### Advanced Features
- 🛡️ **Security** - Spring Security integration for authentication and authorization
- 📝 **API Documentation** - OpenAPI 3.0 specification with Swagger UI
- 🔍 **Health Monitoring** - Spring Boot Actuator for application monitoring
- 🗃️ **Database Migration** - Automatic schema migration and version management
- 🎯 **Custom Validation** - Business logic validation for ER diagram consistency
- 📊 **Audit Logging** - Track all changes and operations

## 🏗️ Technical Architecture

### Framework Stack
- **Core Framework**: Spring Boot 3.3+
- **Language**: Java 21 with modern features
- **Database Access**: MyBatis Plus 3.5+
- **Security**: Spring Security 6+
- **Documentation**: SpringDoc OpenAPI 3
- **Monitoring**: Spring Boot Actuator
- **Build Tool**: Maven 3.6+

### Database Support
- **H2** - Default embedded database for development
- **MySQL** - Production-ready relational database
- **PostgreSQL** - Advanced open-source database
- **Oracle** - Enterprise database solution
- **SQL Server** - Microsoft database platform

## 📋 Prerequisites

- Java 21+
- Maven 3.6+
- Spring Boot 3.3+
- Database (H2/MySQL/PostgreSQL/Oracle/SQL Server)

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
- Integer Types: `INTEGER`, `LONG`
- Floating Point Types: `DECIMAL`
- String Types: `CHAR`, `STRING`, `TEXT`
- Date/Time Types: `DATETIME`
- Binary Types: `BLOB`
- Boolean Types: `BOOLEAN`
- JSON Types: `JSON`

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
        
        <!-- Article Table Also Inherits Base Entity Table -->
        <table name="article" extends="base_entity" comment="Article Table">
            <fields>
                <field name="title" type="STRING" length="200" nullable="false" comment="Title"/>
                <field name="content" type="TEXT" comment="Content"/>
                <field name="author_id" type="LONG" nullable="false" comment="Author ID"/>
            </fields>
            <relations>
                <relation name="fk_article_author" column="author_id" referenceTable="user" referenceColumn="id" onDelete="CASCADE" onUpdate="CASCADE"/>
            </relations>
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
│   │   │       │   ├── DatabaseConfig.java            # Database Configuration
│   │   │       │   ├── SwaggerConfig.java             # API Documentation Configuration
│   │   │       │   └── WebConfig.java                 # Web Configuration
│   │   │       ├── controller/                        # REST Controllers
│   │   │       │   ├── DatabaseController.java        # Database Operations API
│   │   │       │   └── SystemController.java          # System Status API
│   │   │       ├── service/                           # Business Logic Services
│   │   │       │   ├── DatabaseService.java           # Database Service
│   │   │       │   ├── XmlParserService.java          # XML Parsing Service
│   │   │       │   └── SqlGeneratorService.java       # SQL Generation Service
│   │   │       ├── model/                             # Data Models
│   │   │       │   ├── dto/                           # Data Transfer Objects
│   │   │       │   ├── entity/                        # Database Entities
│   │   │       │   └── xml/                           # XML Model Classes
│   │   │       ├── repository/                        # Data Access Layer
│   │   │       ├── exception/                         # Custom Exceptions
│   │   │       └── util/                              # Utility Classes
│   │   └── resources/
│   │       ├── application.yml                        # Main Configuration
│   │       ├── application-docker.yml                 # Docker Configuration
│   │       ├── database-schema.xsd                    # XSD Schema Definition
│   │       ├── static/                                # Static Resources
│   │       └── templates/                             # Template Files
│   └── test/
│       ├── java/                                      # Unit Tests
│       └── resources/                                 # Test Resources
├── target/                                            # Build Output
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

2. **Register Dialect**
```java
@Configuration
public class DatabaseDialectConfig {
    @Bean
    public DatabaseDialectRegistry dialectRegistry() {
        DatabaseDialectRegistry registry = new DatabaseDialectRegistry();
        registry.register(new MySQLDialect());
        registry.register(new PostgreSQLDialect());
        return registry;
    }
}
```

#### Adding New Field Types

1. **Extend Field Type Enum**
```java
public enum FieldType {
    // Existing types...
    UUID("UUID", "UUID type for unique identifiers"),
    GEOMETRY("GEOMETRY", "Spatial geometry type");
}
```

2. **Update XSD Schema**
```xml
<xs:simpleType name="fieldType">
    <xs:restriction base="xs:string">
        <!-- Existing types... -->
        <xs:enumeration value="UUID"/>
        <xs:enumeration value="GEOMETRY"/>
    </xs:restriction>
</xs:simpleType>
```

## 🧪 Testing

### Unit Tests

Run unit tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=XmlParserServiceTest
```

### Integration Tests

Run integration tests:
```bash
mvn verify
```

Run with specific profile:
```bash
mvn verify -Pintegration-test
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

## ❓ Frequently Asked Questions

### Q: How to handle keyword conflicts?
**A:** Use backticks or quotes around field/table names:
```xml
<field name="order" type="STRING" comment="Use quotes for reserved keywords"/>
<table name="user" comment="Backticks will be added automatically for MySQL">
```

### Q: Which databases are supported?
**A:** Currently supports:
- **MySQL 5.7+** (Recommended)
- **PostgreSQL 12+**
- **Oracle 19c+**
- **SQL Server 2019+**
- **H2 Database** (For development/testing)

### Q: How to handle large XML files?
**A:** For large XML files (>10MB):
1. Use streaming XML parser
2. Split into multiple smaller files
3. Use batch processing mode
4. Increase JVM heap size: `-Xmx2g`

### Q: What's the maximum table inheritance depth?
**A:** Recommended maximum depth is 5 levels. Deeper inheritance may cause:
- Performance issues
- Complex SQL generation
- Maintenance difficulties

### Q: How to backup existing data before table generation?
**A:** Always backup before running:
```bash
# MySQL
mysqldump -u username -p database_name > backup.sql

# PostgreSQL
pg_dump -U username database_name > backup.sql
```

### Q: XSD Schema validation fails?
**A:** Common solutions:
1. Check XML encoding (must be UTF-8)
2. Verify XSD schema path is correct
3. Ensure all required attributes are present
4. Validate field types against XSD definitions

### Q: How to get IDE smart prompts?
**A:** Configure your IDE:

**IntelliJ IDEA:**
1. File → Settings → Languages & Frameworks → Schemas and DTDs
2. Add XSD schema file
3. Associate with XML files

**VS Code:**
1. Install XML extension
2. Configure schema association in settings.json:
```json
{
  "xml.fileAssociations": [
    {
      "pattern": "**/*database*.xml",
      "systemId": "path/to/database-schema.xsd"
    }
  ]
}
```

### Q: How to validate XML configuration?
**A:** Multiple validation methods:

1. **API Validation:**
```bash
curl -X POST http://localhost:8080/api/database/validate \
  -H "Content-Type: application/json" \
  -d '{"xmlContent": "your-xml-content"}'
```

2. **Command Line Validation:**
```bash
xmllint --schema database-schema.xsd your-database.xml
```

3. **IDE Validation:** Real-time validation with XSD schema association

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Database (H2 embedded by default)

## 🚀 Quick Start

### 1. Clone and Navigate
```bash
git clone https://github.com/james-shijiachen/fastLCDP.git
cd FastLCDP/ERDesigner/backend
```

### 2. Build the Application
```bash
mvn clean install
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on http://localhost:8080

### 4. Access API Documentation
Open http://localhost:8080/swagger-ui.html in your browser

## ⚙️ Configuration

### Database Configuration

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: ERDesigner-Backend
  datasource:
    url: jdbc:h2:file:./data/erdesigner
    username: sa
    password: 
    driver-class-name: org.h2.Driver

database:
  type: H2
  charset: utf8mb4
  collation: utf8mb4_unicode_ci
  auto-create-metadata-tables: true
  metadata:
    save-to-database: true
    table-prefix: "meta_"

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: ASSIGN_ID
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.fastlcdp.erdesigner.entity
```

### Production Configuration

For MySQL production setup:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/erdesigner?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver

database:
  type: MYSQL
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/fastlcdp/erdesigner/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── entity/          # JPA entities
│   │       ├── service/         # Business logic
│   │       ├── mapper/          # MyBatis mappers
│   │       ├── dto/             # Data transfer objects
│   │       ├── util/            # Utility classes
│   │       └── ErDesignerApplication.java
│   └── resources/
│       ├── application.yaml     # Main configuration
│       ├── application-dev.yaml # Development config
│       ├── application-prod.yaml# Production config
│       └── mapper/              # MyBatis XML mappers
└── test/
    └── java/                    # Unit and integration tests
```

## 🔌 API Endpoints

### Entity Management
- `GET /api/entities` - List all entities
- `POST /api/entities` - Create new entity
- `PUT /api/entities/{id}` - Update entity
- `DELETE /api/entities/{id}` - Delete entity
- `GET /api/entities/{id}/fields` - Get entity fields

### Relationship Management
- `GET /api/relationships` - List all relationships
- `POST /api/relationships` - Create relationship
- `PUT /api/relationships/{id}` - Update relationship
- `DELETE /api/relationships/{id}` - Delete relationship

### Schema Operations
- `POST /api/schema/generate-ddl` - Generate DDL from ER diagram
- `POST /api/schema/validate` - Validate ER diagram
- `GET /api/schema/export/{format}` - Export schema in various formats

### Project Management
- `GET /api/projects` - List projects
- `POST /api/projects` - Create project
- `GET /api/projects/{id}` - Get project details
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
```

## 🔧 Development

### Code Style
The project follows Google Java Style Guide. Use the provided checkstyle configuration:

```bash
mvn checkstyle:check
```

### Database Migration
The application automatically creates tables based on entity annotations. For custom migrations:

1. Add migration scripts to `src/main/resources/db/migration/`
2. Follow naming convention: `V{version}__{description}.sql`

### Adding New Features

1. Create entity classes with proper annotations
2. Implement service layer with business logic
3. Add REST controllers with proper validation
4. Write comprehensive tests
5. Update API documentation

## 🐳 Docker Support

### Build Docker Image
```bash
mvn clean package
docker build -t erdesigner-backend .
```

### Run with Docker Compose
```bash
cd ../../docker
docker-compose up -d
```

## 📊 Monitoring and Health Checks

### Health Check Endpoint
- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Logging
Logs are configured with Log4j2 and stored in:
- Console output for development
- File output: `logs/erdesigner-backend.log`
- JSON format for production environments

## 🔒 Security

### Authentication
The application supports multiple authentication methods:
- JWT token-based authentication
- Session-based authentication
- API key authentication for service-to-service calls

### Authorization
Role-based access control (RBAC) with the following roles:
- `USER` - Basic diagram creation and editing
- `ADMIN` - Full system administration
- `VIEWER` - Read-only access

## 🚀 Deployment

### Production Deployment

1. **Build the application**
   ```bash
   mvn clean package -Pprod
   ```

2. **Set environment variables**
   ```bash
   export DB_USERNAME=your_db_user
   export DB_PASSWORD=your_db_password
   export JWT_SECRET=your_jwt_secret
   ```

3. **Run the application**
   ```bash
   java -jar target/erdesigner-backend-*.jar --spring.profiles.active=prod
   ```

### Performance Tuning

- **JVM Options**: `-Xmx2g -Xms1g -XX:+UseG1GC`
- **Connection Pool**: HikariCP with optimized settings
- **Caching**: Redis integration for session and data caching

## 🤝 Contributing

Please read our [Contributing Guide](../../CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](../../LICENSE) file for details.

---

**ERDesigner Backend** is part of the [ERDesigner](../README.md) project within the [FastLCDP](../../README.md) platform.