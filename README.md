# FastLCDP - Fast Low-Code Development Platform

[中文](README.zh.md) | **English**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![Java](https://img.shields.io/badge/Java-21+-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)
[![GitHub Issues](https://img.shields.io/github/issues/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/issues)
[![GitHub Stars](https://img.shields.io/github/stars/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/stargazers)

## Project Overview

FastLCDP is a low-code database platform that allows developers to quickly generate database tables and SQL statements through XML configuration files. The platform supports multiple database types and provides rich features including table inheritance, field validation, index management, and foreign key relationships.

## 🚀 Key Features

### Core Functionality
- ✅ **XML-driven Configuration**: Parse XML format database table definitions
- ✅ **Standard DDL Generation**: Generate standard DDL statements
- ✅ **Multi-database Support**: Support multiple database types (H2, MySQL, PostgreSQL, Oracle, SQL Server)
- ✅ **REST API Interface**: Provide REST API interfaces
- ✅ **Table Structure Validation**: Support table structure validation
- ✅ **Metadata Storage and Management**: Metadata storage and management
- ✅ **Database Dialect Support**: Database dialect support
- ✅ **XSD Schema Validation**: Provide complete XML Schema definition and validation
- ✅ **Table Inheritance**: Support table structure inheritance, reduce duplicate definitions
- ✅ **Configuration Validation Tools**: Provide XML configuration validation and Schema generation tools

### Advanced Features
- 🆕 **Multi-database Dialects**: Generate SQL statements for different databases based on configuration
- 🆕 **Metadata Management**: Save XML metadata to database for version management
- 🆕 **Table Inheritance Mechanism**: Support multi-level table inheritance, automatically merge parent table fields, indexes and relationships
- 🆕 **XSD Schema**: Provide complete XML Schema definition files, support IDE intelligent prompts
- 🆕 **Validation Tool Set**: Include XML configuration validation, Schema generation and other practical tools

## 📋 Environment Requirements

- Java 21+
- Maven 3.6+
- Spring Boot 3.3+

### Installation and Setup

1. **Clone the Repository**
```bash
git clone https://github.com/james-shijiachen/fastLCDP.git
cd FastLCDP
```

2. **Configure Database**

Edit `src/main/resources/application.yml` file to configure database connection:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: your_username
    password: your_password
```

3. **Build and Run**
```bash
mvn clean install
mvn spring-boot:run
```

4. **Access Application**
- Application URL: http://localhost:8080
- H2 Console (Development): http://localhost:8080/h2-console
- Health Check: http://localhost:8080/api/table-generator/status

## XML Format Description

### Basic Structure

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

### Field Definition

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

### Index Definition

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

### Foreign Key Relationships

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

### Table Inheritance

```xml
<table name="Child Table" extends="Parent Table Name" comment="Inheritance Example">
    <!-- Child table automatically inherits parent table fields, indexes and relationships -->
    <fields>
        <!-- Can add child table specific fields -->
        <field name="extra_field" type="STRING" length="100" comment="Child table specific field"/>
    </fields>
</table>
```

### XSD Schema Validation

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

## API Interfaces

### 1. Generate Database Tables

```http
POST /api/table-generator/generate
Content-Type: multipart/form-data

file: XML file
```

**Response Example:**
```json
{
    "success": true,
    "message": "Successfully generated 5 tables",
    "databaseName": "sample_db",
    "tableCount": 5
}
```

### 2. Preview SQL Statements

```http
POST /api/table-generator/preview
Content-Type: multipart/form-data

file: XML file
```

**Response Example:**
```json
{
    "success": true,
    "message": "SQL preview generated successfully",
    "sql": "CREATE DATABASE IF NOT EXISTS `sample_db`...\nCREATE TABLE IF NOT EXISTS `user`...",
    "databaseName": "sample_db",
    "tableCount": 5
}
```

### 3. Validate XML Format

```http
POST /api/table-generator/validate
Content-Type: multipart/form-data

file: XML file
```

**Response Example:**
```json
{
    "valid": true,
    "message": "XML file validation passed, contains 5 table definitions",
    "databaseName": "sample_db",
    "tableCount": 5
}
```

### 4. System Status

```http
GET /api/table-generator/status
```

**Response Example:**
```json
{
    "status": "running",
    "message": "XML table generator service is running normally",
    "timestamp": 1703123456789
}
```

## Example Files

The project provides two example XML files:

1. **Simple Example** (`examples/simple-example.xml`)
   - Contains user and order tables
   - Demonstrates basic field definitions and foreign key associations

2. **Complete Example** (`examples/sample-database.xml`)
   - Contains complete table structure for user permission management system
   - Demonstrates table inheritance, complex indexes, various field types and other advanced features

## Usage Examples

### 1. Define a Simple User Table

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

### 2. Using Table Inheritance

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

## Development Guide

### Project Structure

```
examples/                                      # Example files
├── sample-database.xml                        # Complete example
└── simple-example.xml                         # Simple example
src/
├── main/
│   ├── java/cn/com/traninfo/fastlcdp/
│   │   ├── FastLcdpApplication.java             # Main application class
│   │   ├── config/
│   │   │   └── DatabaseConfig.java            # Database configuration
│   │   ├── controller/
│   │   │   └── TableGeneratorController.java  # REST controller
│   │   ├── dialect/                           # Database dialect support
│   │   │   ├── AbstractDatabaseDialect.java   # Abstract dialect base class
│   │   │   ├── DatabaseDialect.java           # Dialect interface
│   │   │   ├── DatabaseDialectFactory.java    # Dialect factory
│   │   │   ├── H2Dialect.java                 # H2 database dialect
│   │   │   ├── MySQLDialect.java              # MySQL database dialect
│   │   │   ├── OracleDialect.java             # Oracle database dialect
│   │   │   ├── PostgreSQLDialect.java         # PostgreSQL database dialect
│   │   │   └── SqlServerDialect.java          # SQL Server database dialect
│   │   ├── model/
│   │   │   ├── DatabaseSchema.java            # Database schema model
│   │   │   ├── FieldDefinition.java           # Field definition model
│   │   │   ├── IndexDefinition.java           # Index definition model
│   │   │   ├── MetadataEntity.java            # Metadata entity
│   │   │   ├── RelationDefinition.java        # Relation definition model
│   │   │   └── TableDefinition.java           # Table definition model
│   │   ├── repository/
│   │   │   └── MetadataRepository.java        # Metadata repository
│   │   ├── service/
│   │   │   ├── DatabaseExecutorService.java   # Database execution service
│   │   │   ├── MetadataService.java           # Metadata management service
│   │   │   ├── SqlGeneratorService.java       # SQL generation service
│   │   │   ├── TableGeneratorService.java     # Core business service
│   │   │   └── XmlParserService.java          # XML parsing service
│   │   └── util/
│   │       ├── XmlConfigValidator.java        # XML configuration validator
│   │       ├── XmlSchemaGenerator.java        # XSD Schema generator
│   │       └── XmlSchemaValidator.java        # XSD Schema validator
│   └── resources/
│       ├── application.yaml                   # Application configuration
│       └── database-schema.xsd                # XSD Schema definition
└── test/
    ├── java/cn/com/traninfo/fastlcdp/
    │   ├── IntegrationTest.java               # Integration tests
    │   ├── SimpleSqlTest.java                 # Simple SQL tests
    │   ├── TestXmlParser.java                 # XML parsing tests
    │   ├── example/                           # Example and demo code
    │   │   ├── XmlSchemaValidationDemo.java   # Schema validation demo
    │   │   └── XmlValidationExample.java      # Validation example
    │   ├── service/
    │   │   ├── InheritanceTest.java           # Table inheritance tests
    │   │   ├── SqlGeneratorServiceTest.java   # SQL generation tests
    │   │   └── XmlParserServiceTest.java      # XML parsing tests
    │   └── util/
    │       └── XmlSchemaValidatorTest.java    # Schema validator tests
    └── resources/
        ├── application-test.yaml              # Test configuration
        ├── test-inheritance.xml               # Inheritance test file
        └── test-schema.xml                    # Schema test file
```

## Testing

### Extension Development

1. **Adding New Field Type Support**
   - Modify the `DataType` enum in `src/main/resources/database-schema.xsd`
   - Update type mappings in various database dialect classes
   - Add corresponding test cases

2. **Supporting New Database Types**
   - Inherit from `AbstractDatabaseDialect` to create new dialect classes
   - Register new dialects in `DatabaseDialectFactory`
   - Implement database-specific SQL generation logic

3. **Adding New Index Types**
   - Modify the `IndexType` enum in `database-schema.xsd`
   - Update the `IndexDefinition` model
   - Implement SQL generation for new index types in dialect classes

4. **Extending XSD Schema**
   - Modify `database-schema.xsd` to add new elements or attributes
   - Update corresponding model classes
   - Run `XmlSchemaValidatorTest` to ensure compatibility

5. **Adding Validation Rules**
   - Extend `XmlConfigValidator` to add custom validation logic
   - Add business rule validation in `XmlSchemaValidator`

## Testing

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

## FAQ

### Q: How to handle keyword conflicts with table or field names?
A: The framework automatically adds backticks (`) to all table and field names to avoid conflicts with database keywords.

### Q: Which databases are supported?
A: Currently supports H2, MySQL, PostgreSQL, Oracle, and SQL Server. Through the database dialect mechanism, support for other databases can be easily extended.

### Q: How to handle large XML files?
A: The framework uses streaming parsing and can handle relatively large XML files. It's recommended that single files don't exceed 10MB.

### Q: Is there a limit to table inheritance depth?
A: Theoretically no limit, but it's recommended to keep inheritance depth under 3 levels for structural clarity. Supports multi-level inheritance and automatic merging of fields, indexes, and relationships.

### Q: How to backup existing data?
A: The framework only creates table structures and won't delete existing data. It's recommended to backup the database before using in production environments.

### Q: What to do when XSD Schema validation fails?
A: Check if field types, index types, etc. in the XML file conform to definitions in `database-schema.xsd`. Common issues include using unsupported data types (e.g., `VARCHAR` should be changed to `STRING`) or collations (e.g., `utf8mb4_unicode_ci` should be changed to `utf8mb4_general_ci`).

### Q: How to get XML intelligent hints in IDE?
A: Add XSD reference in XML files: `xsi:noNamespaceSchemaLocation="../database-schema.xsd"`. Most modern IDEs will automatically provide intelligent hints and syntax checking.

### Q: How to validate XML configuration correctness?
A: You can use the validation tools provided by the project:
   - Run `XmlSchemaValidationDemo` for quick validation
   - Use `XmlSchemaValidator` class for programmatic validation
   - Use the `/validate` endpoint of the REST API for online validation

## Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE) file for details.

## Contact

- Project Homepage: [https://github.com/james-shijiachen/fastLCDP](https://github.com/james-shijiachen/fastLCDP)
- Issue Reports: [https://github.com/james-shijiachen/fastLCDP/issues](https://github.com/james-shijiachen/fastLCDP/issues)
- Email: [shijiachen@traninfo.com.cn](mailto:shijiachen@traninfo.com.cn)

---

**FastLCDP Team** - Making database table creation easier! 🚀