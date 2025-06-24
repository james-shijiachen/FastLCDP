# FastLCDP Complete User Guide

This document is the complete configuration guide for the FastLCDP XML project, including XML configuration instructions, a detailed explanation of the table inheritance feature, and a guide to using XSD validation.

## Table of Contents

1.  [XML Configuration Guide](#xml-configuration-guide)
2.  [Table Inheritance Feature Guide](#table-inheritance-feature-guide)
3.  [XSD Validation User Guide](#xsd-validation-user-guide)

---

# XML Configuration Guide

## Overview

FastLCDP is a database table structure generation tool based on XML configuration. It supports defining database structures through XML files, including tables, fields, indexes, relationships, and table inheritance features. This document details the format, element meanings, and configuration rules of the XML configuration file.

## XML Document Structure

### Root Element: database

The root element for database definitions, containing the configuration information for the entire database.

```xml
<database name="database_name" version="version_number" charset="character_set" collation="collation_rule" comment="database_comment">
    <tables>
        <!-- Table definitions -->
    </tables>
</database>
```

#### Attribute Description

| Attribute | Type | Required | Description | Example |
|---|---|---|---|---|
| name | String | Yes | Database name | `blog_system` |
| version | String | No | Database version | `1.0.0` |
| charset | CharsetType | No | Default character set, inheritable at the table level | `utf8mb4` |
| collation | CollationType | No | Default collation rule | `utf8mb4_general_ci` |
| engine | EngineType | No | Default storage engine, inheritable at the table level | `InnoDB` |
| comment | String | No | Database comment | `E-commerce platform database` |

#### Supported Character Set Types (CharsetType)

| Value | Description |
|---|---|
| `utf8` | UTF-8 character set (up to 3 bytes) |
| `utf8mb4` | UTF-8 character set (up to 4 bytes, recommended) |
| `latin1` | Latin-1 character set (1 character per byte) |
| `gbk` | GBK Chinese character set |
| `binary` | Binary character set |

##### Character Set Mapping Across Databases

| CharsetType | H2 | MySQL | PostgreSQL | SQLite | Oracle | SQL Server |
|---|---|---|---|---|---|---|
| `utf8` | UTF8 | utf8 | UTF8 | UTF-8 | AL32UTF8 | - |
| `utf8mb4` | UTF8 | utf8mb4 | UTF8 | UTF-8 | AL32UTF8 | UTF-8 |
| `latin1` | ISO8859_1 | latin1 | LATIN1 | - | WE8ISO8859P1 | Latin1_General_CI_AS |
| `gbk` | - | gbk | - | - | ZHS16GBK | Chinese_PRC_CI_AS |
| `binary` | BINARY | binary | - | BINARY | - | Latin1_General_BIN |

#### Supported Collation Types (CollationType)

| Value | Description |
|---|---|
| `utf8_general_ci` | UTF-8 general collation (case-insensitive) |
| `utf8mb4_general_ci` | UTF-8MB4 general collation (case-insensitive, recommended) |
| `latin1_swedish_ci` | Latin-1 Swedish collation |
| `gbk_chinese_ci` | GBK Chinese collation |
| `binary` | Binary collation (case-sensitive) |

##### Collation Mapping Across Databases

| CollationType | H2 | MySQL | PostgreSQL | SQLite | Oracle | SQL Server |
|---|---|---|---|---|---|---|
| `utf8_general_ci` | - | utf8_general_ci | - | NOCASE | - | SQL_Latin1_General_CP1_CI_AS |
| `utf8mb4_general_ci` | - | utf8mb4_general_ci | - | NOCASE | - | SQL_Latin1_General_CP1_CI_AS |
| `latin1_swedish_ci` | - | latin1_swedish_ci | - | - | - | Latin1_General_CI_AS |
| `gbk_chinese_ci` | - | gbk_chinese_ci | - | - | - | Chinese_PRC_CI_AS |
| `binary` | - | binary | C | BINARY | BINARY | Latin1_General_BIN |

> **Note**: H2 and PostgreSQL primarily set collation at the database level. SQLite's collation is relatively simple. Oracle uses NLS parameters to control sorting behavior.

#### Supported Storage Engine Types (EngineType)

| Value | Description |
|---|---|
| `InnoDB` | Storage engine supporting transactions and foreign keys (recommended) |
| `MyISAM` | High-performance non-transactional storage engine |
| `Memory` | In-memory storage engine |
| `Archive` | Compressed storage engine |

##### Storage Engine Mapping Across Databases

| EngineType | H2 | MySQL | PostgreSQL | SQLite | Oracle | SQL Server |
|---|---|---|---|---|---|---|
| `InnoDB` | - | InnoDB | - | - | - | - |
| `MyISAM` | - | MyISAM | - | - | - | - |
| `Memory` | - | MEMORY | - | :memory: | - | - |
| `Archive` | - | ARCHIVE | - | - | - | - |

> **Note**: Storage engines are mainly a MySQL concept. H2, PostgreSQL, Oracle, and SQL Server have their own storage mechanisms that do not directly correspond to MySQL's storage engines. SQLite's `:memory:` indicates an in-memory database.

## Table Definition: table

A table is a fundamental unit of a database, containing definitions for fields, indexes, relationships, etc.

```xml
<table name="table_name" comment="table_comment" extends="parent_table_name" engine="storage_engine" charset="character_set">
    <fields>
        <!-- Field definitions -->
    </fields>
    <indexes>
        <!-- Index definitions -->
    </indexes>
    <relations>
        <!-- Relationship definitions -->
    </relations>
</table>
```

### Table Attribute Description

| Attribute | Type | Required | Description | Example |
|---|---|---|---|---|
| name | String | Yes | Table name | `users` |
| comment | String | No | Table comment | `User table` |
| extends | String | No | Name of the parent table to inherit from | `base_entity` |
| engine | EngineType | No | Storage engine, inherits from database default if not set | `InnoDB` |
| charset | CharsetType | No | Table character set, inherits from database default if not set | `utf8mb4` |
| collation | CollationType | No | Table collation, inherits from database default if not set | `utf8mb4_general_ci` |

### Configuration Inheritance Mechanism

#### Engine and Charset Inheritance

The `engine` and `charset` attributes support inheritance from the database level to the table level:

1.  **Table Level Priority**: If the `engine` or `charset` attribute is explicitly set in the table definition, the table-level configuration is used.
2.  **Database Level Inheritance**: If the `engine` or `charset` attribute is not set in the table definition, the default configuration from the database level is automatically inherited.
3.  **System Default**: If neither the database nor the table has it set, the system default is used (`engine=InnoDB`, `charset=utf8mb4`).

#### Inheritance Example

```xml
<database name="example_db" charset="utf8mb4" engine="MyISAM">
    <tables>
        <!-- Table 1: Fully inherits database configuration -->
        <table name="inherit_table" comment="Inherits database configuration">
            <!-- Will use engine=MyISAM, charset=utf8mb4 -->
        </table>
        
        <!-- Table 2: Custom configuration, no inheritance -->
        <table name="custom_table" engine="InnoDB" charset="latin1">
            <!-- Will use engine=InnoDB, charset=latin1 -->
        </table>
        
        <!-- Table 3: Partial inheritance -->
        <table name="partial_table" engine="InnoDB">
            <!-- Will use engine=InnoDB (custom), charset=utf8mb4 (inherited) -->
        </table>
    </tables>
</database>
```

### Table Inheritance Feature

Table inheritance is a core feature of FastLCDP, allowing a child table to automatically inherit fields, indexes, and relationship definitions from a parent table.

#### Inheritance Rules

1.  **Field Inheritance**: The child table inherits all fields from the parent table. Parent table fields are inserted before the child table's own fields.
2.  **Index Inheritance**: The child table inherits all index definitions from the parent table.
3.  **Relationship Inheritance**: The child table inherits all foreign key relationships from the parent table.
4.  **Circular Detection**: The system automatically detects and prevents circular inheritance.
5.  **Multi-level Inheritance**: Supports multiple levels of inheritance, recommended not to exceed 3 levels.

#### Inheritance Example

```xml
<!-- Base entity table -->
<table name="base_entity" comment="Base entity table">
    <fields>
        <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="Primary key ID"/>
        <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="Creation time"/>
        <field name="updated_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" comment="Update time"/>
        <field name="is_deleted" type="BOOLEAN" length="1" defaultValue="0" comment="Is deleted"/>
    </fields>
</table>

<!-- User table inheriting from base entity table -->
<table name="users" extends="base_entity" comment="User table">
    <fields>
        <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="Username"/>
        <field name="email" type="STRING" length="100" nullable="false" comment="Email"/>
        <field name="password" type="STRING" length="255" nullable="false" comment="Password"/>
    </fields>
    <indexes>
        <index name="idx_username" type="NORMAL">
            <columns>
                <column name="username"/>
            </columns>
        </index>
    </indexes>
</table>
```

## Field Definition: field

A field is a basic component of a table, defining the data storage structure and constraints.

```xml
<field name="field_name" 
       type="data_type" 
       length="length" 
       precision="numeric_precision" 
       scale="decimal_places" 
       nullable="is_nullable" 
       primaryKey="is_primary_key" 
       autoIncrement="is_auto_increment" 
       unique="is_unique"
       defaultValue="default_value" 
       comment="field_comment" />
```

### Field Attribute Description

| Attribute | Type | Required | Description | Example |
|---|---|---|---|---|
| name | String | Yes | Field name | `username` |
| type | DataType | Yes | Data type, see supported data types | `STRING`, `INTEGER`, `DECIMAL` |
| length | Integer | No | Field length (positive integer) | `50`, `255` |
| precision | Integer | No | Numeric precision (positive integer, for DECIMAL type) | `10` |
| scale | Integer | No | Decimal places (non-negative integer, for DECIMAL type) | `2` |
| nullable | Boolean | No | Whether NULL is allowed, default true | `false` |
| primaryKey | Boolean | No | Whether it is a primary key, default false | `true` |
| autoIncrement | Boolean | No | Whether it is auto-incrementing, default false | `true` |
| unique | Boolean | No | Whether it is unique, default false | `true` |
| defaultValue | String | No | Default value | `0`, `CURRENT_TIMESTAMP` |
| comment | String | No | Field comment | `Username` |

### Supported Data Types

According to the XSD definition, the system supports the following data types:

#### Numeric Types
- `INTEGER`: Integer type
- `LONG`: Long integer type
- `DECIMAL`: Exact decimal type
- `BOOLEAN`: Boolean type

#### String Types
- `CHAR`: Fixed-length string
- `STRING`: Variable-length string
- `TEXT`: Text type

#### Binary Types
- `BLOB`: Binary large object

#### Date/Time Types
- `DATETIME`: Date and time type

#### JSON Type
- `JSON`: JSON data type

#### Data Type Mapping Across Databases

| DataType | H2 | MySQL | PostgreSQL | SQLite | Oracle | SQL Server |
|---|---|---|---|---|---|---|
| `INTEGER` | INT | INT | INTEGER | INTEGER | NUMBER(10) | INT |
| `LONG` | BIGINT | BIGINT | BIGINT | INTEGER | NUMBER(19) | BIGINT |
| `DECIMAL` | DECIMAL(p,s) | DECIMAL(p,s) | DECIMAL(p,s) | REAL | NUMBER(p,s) | DECIMAL(p,s) |
| `BOOLEAN` | BOOLEAN | TINYINT(1) | BOOLEAN | INTEGER | NUMBER(1) | BIT |
| `CHAR` | CHAR(n) | CHAR(n) | CHAR(n) | TEXT | CHAR(n) | CHAR(n) |
| `STRING` | VARCHAR(n) | VARCHAR(n) | VARCHAR(n) | TEXT | VARCHAR2(n) | NVARCHAR(n) |
| `TEXT` | CLOB | TEXT | TEXT | TEXT | CLOB | NTEXT |
| `BLOB` | BLOB | BLOB | BYTEA | BLOB | BLOB | VARBINARY(MAX) |
| `DATETIME` | TIMESTAMP | DATETIME | TIMESTAMP | TEXT | DATE | DATETIME2 |
| `JSON` | JSON | JSON | JSON | TEXT | CLOB | NVARCHAR(MAX) |

> **Note**:
> - `p` represents precision, `s` represents scale, `n` represents length.
> - SQLite uses a dynamic type system, most types are stored as TEXT or INTEGER.
> - Oracle's DATE type includes both date and time information.
> - SQL Server recommends using NVARCHAR to support Unicode characters.
> - These are the standard data types defined in the XSD. During the actual SQL generation process, the system will map these generic types to specific database types based on the target database.

## Index Definition: index

Indexes are used to improve query performance and support multiple index types.

```xml
<index name="index_name" type="index_type" method="index_method" comment="index_comment">
    <columns>
        <column name="field_name" order="sort_order" length="index_length"/>
    </columns>
</index>
```

### Index Attribute Description

| Attribute | Type | Required | Description | Possible Values |
|---|---|---|---|---|
| name | String | Yes | Index name | `idx_username` |
| type | IndexTypeEnum | No | Index type, default NORMAL | See Index Type Description |
| comment | String | No | Index comment | `Username index` |

### Index Column Attribute Description

| Attribute | Type | Required | Description | Possible Values |
|---|---|---|---|---|
| name | String | Yes | Field name | `username` |
| order | SortOrder | No | Sort order, default ASC | `ASC`, `DESC` |
| length | Integer | No | Index length (positive integer, for prefix indexes) | `10` |

### Index Type Description (IndexTypeEnum)

| Value | Description |
|---|---|
| `PRIMARY` | Primary key index, uniquely identifies each row in the table. |
| `UNIQUE` | Unique index, ensures that the values in the indexed column are unique in the table. |
| `NORMAL` | Normal index (default), used to improve query performance, allows duplicate values. |
| `FULLTEXT` | Full-text index, used for full-text searches in text fields. |
| `SPATIAL` | Spatial index, used for indexing geospatial data types. |

### Index Example

```xml
<indexes>
    <!-- Normal index -->
    <index name="idx_email" type="NORMAL">
        <columns>
            <column name="email"/>
        </columns>
    </index>
    
    <!-- Unique index -->
    <index name="uk_username" type="UNIQUE">
        <columns>
            <column name="username"/>
        </columns>
    </index>
    
    <!-- Composite index -->
    <index name="idx_name_age" type="NORMAL">
        <columns>
            <column name="last_name"/>
            <column name="first_name"/>
            <column name="age" order="DESC"/>
        </columns>
    </index>
    
    <!-- Prefix index -->
    <index name="idx_content_prefix" type="NORMAL">
        <columns>
            <column name="content" length="100"/>
        </columns>
    </index>
</indexes>
```

## Relationship Definition: relation

Relationship definitions are used to establish foreign key constraints between tables.

```xml
<relation name="foreign_key_name" 
          column="local_column" 
          referenceTable="referenced_table" 
          referenceColumn="referenced_column" 
          onDelete="delete_action" 
          onUpdate="update_action" 
          type="relationship_type" 
          comment="relationship_comment"/>
```

### Relationship Attribute Description

| Attribute | Type | Required | Description | Example |
|---|---|---|---|---|
| name | String | Yes | Foreign key constraint name | `fk_user_profile` |
| column | String | Yes | The foreign key column in the local table | `user_id` |
| referenceTable | String | Yes | The name of the referenced table | `users` |
| referenceColumn | String | Yes | The column in the referenced table | `id` |
| onDelete | ReferentialAction | No | Action on delete, default RESTRICT | See Referential Action Description |
| onUpdate | ReferentialAction | No | Action on update, default RESTRICT | See Referential Action Description |
| comment | String | No | Relationship comment | `User profile foreign key` |

### Referential Action Description (ReferentialAction)

| Value | Description |
|---|---|
| `RESTRICT` | Restricts the delete or update operation on the parent table if related records exist in the child table (default). |
| `CASCADE` | When a record in the parent table is deleted or updated, automatically delete or update the related records in the child table. |
| `SET NULL` | When a record in the parent table is deleted or updated, set the related foreign key fields in the child table to NULL. |
| `NO ACTION` | Similar to RESTRICT, but the check may be deferred depending on the database implementation. |
| `SET DEFAULT` | When a record in the parent table is deleted or updated, set the related foreign key fields in the child table to their default values. |

### Relationship Example

```xml
<relations>
    <!-- User profile relationship -->
    <relation name="fk_user_profile" 
              column="user_id" 
              referenceTable="users" 
              referenceColumn="id" 
              onDelete="CASCADE" 
              onUpdate="CASCADE" 
              comment="User profile association"/>
    
    <!-- Post author relationship -->
    <relation name="fk_post_author" 
              column="author_id" 
              referenceTable="users" 
              referenceColumn="id" 
              onDelete="RESTRICT" 
              onUpdate="CASCADE" 
              comment="Post author association"/>
</relations>
```

## Complete Example

The following is a complete XML configuration example showing the database structure of a blog system:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<database name="blog_system" version="1.0.0" charset="utf8mb4" collation="utf8mb4_unicode_ci" comment="Blog System Database">
    <tables>
        <!-- Base entity table -->
        <table name="base_entity" comment="Base entity table">
            <fields>
                <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="Primary Key ID"/>
                <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="Creation Time"/>
                <field name="updated_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" comment="Update Time"/>
                <field name="is_deleted" type="BOOLEAN" length="1" defaultValue="0" comment="Is Deleted"/>
            </fields>
        </table>
        
        <!-- User table -->
        <table name="users" extends="base_entity" comment="User table">
            <fields>
                <field name="username" type="STRING" length="50" nullable="false" unique="true" comment="Username"/>
                <field name="email" type="STRING" length="100" nullable="false" comment="Email"/>
                <field name="password" type="STRING" length="255" nullable="false" comment="Password"/>
                <field name="nickname" type="STRING" length="100" comment="Nickname"/>
                <field name="avatar" type="STRING" length="255" comment="Avatar URL"/>
                <field name="status" type="BOOLEAN" length="1" defaultValue="1" comment="Status: 0-disabled, 1-enabled"/>
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
            </indexes>
        </table>
        
        <!-- Post table -->
        <table name="posts" extends="base_entity" comment="Post table">
            <fields>
                <field name="title" type="STRING" length="200" nullable="false" comment="Title"/>
                <field name="content" type="TEXT" comment="Content"/>
                <field name="summary" type="STRING" length="500" comment="Summary"/>
                <field name="author_id" type="LONG" nullable="false" comment="Author ID"/>
                <field name="category_id" type="LONG" comment="Category ID"/>
                <field name="status" type="BOOLEAN" length="1" defaultValue="0" comment="Status: 0-draft, 1-published"/>
                <field name="view_count" type="LONG" defaultValue="0" comment="View count"/>
                <field name="like_count" type="LONG" defaultValue="0" comment="Like count"/>
            </fields>
            <indexes>
                <index name="idx_author" type="NORMAL">
                    <columns>
                        <column name="author_id"/>
                    </columns>
                </index>
                <index name="idx_category" type="NORMAL">
                    <columns>
                        <column name="category_id"/>
                    </columns>
                </index>
                <index name="idx_status" type="NORMAL">
                    <columns>
                        <column name="status"/>
                    </columns>
                </index>
                <index name="idx_created_time" type="NORMAL">
                    <columns>
                        <column name="created_time" order="DESC"/>
                    </columns>
                </index>
                <index name="ft_title_content" type="FULLTEXT">
                    <columns>
                        <column name="title"/>
                        <column name="content"/>
                    </columns>
                </index>
            </indexes>
            <relations>
                <relation name="fk_post_author" 
                          column="author_id" 
                          referenceTable="users" 
                          referenceColumn="id" 
                          onDelete="RESTRICT" 
                          onUpdate="CASCADE" 
                          comment="Post author association"/>
            </relations>
        </table>
        
        <!-- Comment table -->
        <table name="comments" extends="base_entity" comment="Comment table">
            <fields>
                <field name="post_id" type="LONG" nullable="false" comment="Post ID"/>
                <field name="user_id" type="LONG" nullable="false" comment="User ID"/>
                <field name="parent_id" type="LONG" comment="Parent comment ID"/>
                <field name="content" type="STRING" nullable="false" comment="Comment content"/>
                <field name="status" type="BOOLEAN" length="1" defaultValue="1" comment="Status: 0-hidden, 1-visible"/>
            </fields>
            <indexes>
                <index name="idx_post" type="NORMAL">
                    <columns>
                        <column name="post_id"/>
                    </columns>
                </index>
                <index name="idx_user" type="NORMAL">
                    <columns>
                        <column name="user_id"/>
                    </columns>
                </index>
                <index name="idx_parent" type="NORMAL">
                    <columns>
                        <column name="parent_id"/>
                    </columns>
                </index>
            </indexes>
            <relations>
                <relation name="fk_comment_post" 
                          column="post_id" 
                          referenceTable="posts" 
                          referenceColumn="id" 
                          onDelete="CASCADE" 
                          onUpdate="CASCADE" 
                          comment="Comment-post association"/>
                <relation name="fk_comment_user" 
                          column="user_id" 
                          referenceTable="users" 
                          referenceColumn="id" 
                          onDelete="CASCADE" 
                          onUpdate="CASCADE" 
                          comment="Comment-user association"/>
                <relation name="fk_comment_parent" 
                          column="parent_id" 
                          referenceTable="comments" 
                          referenceColumn="id" 
                          onDelete="CASCADE" 
                          onUpdate="CASCADE" 
                          comment="Parent comment association"/>
            </relations>
        </table>
    </tables>
</database>
```

## Configuration Rules and Best Practices

### Naming Conventions

1.  **Database Name**: Use lowercase letters and underscores, e.g., `blog_system`
2.  **Table Name**: Use lowercase letters and underscores, plural form, e.g., `users`, `posts`
3.  **Field Name**: Use lowercase letters and underscores, e.g., `user_id`, `created_time`
4.  **Index Name**:
    *   Normal index: `idx_field_name`, e.g., `idx_username`
    *   Unique index: `uk_field_name`, e.g., `uk_email`
    *   Full-text index: `ft_field_name`, e.g., `ft_title_content`
5.  **Foreign Key Name**: `fk_table_name_field_name`, e.g., `fk_user_profile`

### Field Design Principles

1.  **Primary Key Design**: Recommend using an auto-incrementing BIGINT type as the primary key.
2.  **Time Fields**: Consistently use the DATETIME type and set default values.
3.  **Status Fields**: Use the TINYINT type and add comments explaining the meaning of each status.
4.  **String Length**: Set a reasonable length based on actual requirements.
5.  **NOT NULL Constraints**: Unless business requirements dictate otherwise, fields should be set to NOT NULL.
6.  **Default Values**: Set reasonable default values for fields.

### Index Design Principles

1.  **Primary Key Index**: Automatically created, no need to define manually.
2.  **Foreign Key Index**: Create indexes for foreign key fields.
3.  **Query Index**: Create indexes for fields frequently used in WHERE clauses.
4.  **Composite Index**: Follow the leftmost prefix principle.
5.  **Avoid Over-indexing**: Indexes affect write performance.

### Foreign Key Relationship Principles

1.  **Cascade Delete**: Use CASCADE with caution to avoid accidental data deletion.
2.  **Referential Integrity**: Ensure the referenced tables and fields exist.
3.  **Performance Consideration**: Foreign key constraints affect performance; choose based on needs.

### Character Set and Collation

1.  **Recommended Character Set**: `utf8mb4` (supports the complete UTF-8 character set).
2.  **Recommended Collation**: `utf8mb4_unicode_ci` (supports multilingual sorting).
3.  **Consistency**: Maintain consistency in character sets across the database, tables, and fields.

### Inheritance Design Principles

1.  **Base Entity**: Create a base entity table containing common fields.
2.  **Inheritance Hierarchy**: It is recommended not to exceed 3 levels of inheritance.
3.  **Field Conflicts**: Avoid defining fields in child tables with the same names as in parent tables.
4.  **Circular Detection**: The system automatically detects circular inheritance.

## Common Issues

### Q: Is there a limit to the depth of table inheritance?
A: There is no theoretical limit, but it is recommended that the inheritance depth not exceed 3 levels to maintain a clear structure.

### Q: Can a child table override the fields of a parent table?
A: It is not recommended to override parent table fields as this can cause conflicts. If modifications are necessary, it is recommended to make them in the parent table.

### Q: How are field name conflicts handled?
A: The system merges fields according to the inheritance order. If fields with the same name exist, the one defined later will override the one defined earlier.

### Q: Must tables referenced by foreign keys be in the same XML file?
A: Yes, the current version requires all related tables to be defined in the same XML file.

### Q: Which databases are supported?
A: Currently supported databases include H2, MySQL, PostgreSQL, SQLite, Oracle, and SQL Server. Support for other databases is under development.

### Q: How can I validate the correctness of the XML configuration?
A: The project defines an XSD (XML Schema Definition) for the XML files. It is recommended to use XSD for static validation of the XML configuration.

## XSD Type Definition Summary

This section summarizes all types and enumerations defined in `database-schema.xsd` for developer reference:

### Complex Types (ComplexType)

| Type Name | Description | Usage |
|---|---|---|
| `DatabaseType` | Database type definition | Type for the root element `database` |
| `TablesType` | Table collection type definition | Contains multiple `table` elements |
| `TableType` | Table type definition | Definition of a single table |
| `FieldsType` | Field collection type definition | Contains multiple `field` elements |
| `FieldType` | Field type definition | Definition of a single field |
| `IndexesType` | Index collection type definition | Contains multiple `index` elements |
| `IndexType` | Index type definition | Definition of a single index |
| `IndexColumnsType` | Index column collection type definition | Contains multiple `column` elements |
| `IndexColumnType` | Index column type definition | Definition of a single index column |
| `RelationsType` | Relationship collection type definition | Contains multiple `relation` elements |
| `RelationType` | Relationship type definition | Definition of a single foreign key relationship |

### Simple Types (SimpleType) Enumerations

| Enumeration Name | Possible Values | Description |
|---|---|---|
| `DataType` | INTEGER, LONG, DECIMAL, BOOLEAN, CHAR, STRING, TEXT, BLOB, DATETIME, JSON | Field data types |
| `CharsetType` | utf8, utf8mb4, latin1, gbk, binary | Character set types |
| `CollationType` | utf8_general_ci, utf8mb4_general_ci, latin1_swedish_ci, gbk_chinese_ci, binary | Collation types |
| `EngineType` | InnoDB, MyISAM, Memory, Archive | Storage engine types |
| `IndexTypeEnum` | PRIMARY, UNIQUE, NORMAL, FULLTEXT, SPATIAL | Index types |
| `SortOrder` | ASC, DESC | Sort order |
| `ReferentialAction` | RESTRICT, CASCADE, SET NULL, NO ACTION, SET DEFAULT | Foreign key referential actions |

### Attribute Constraint Description

- **required**: Required attribute
- **optional**: Optional attribute
- **default**: Default value
- **positiveInteger**: Positive integer (>0)
- **nonNegativeInteger**: Non-negative integer (≥0)
- **boolean**: Boolean value (true/false)

---

# Table Inheritance Feature Guide

## Overview

Table inheritance is a core feature of FastLCDP, allowing a child table to automatically inherit field, index, and relationship definitions from a parent table. This greatly simplifies repetitive work in database design and improves the maintainability and consistency of the configuration.

## Inheritance Mechanism Principles

### Inheritance Processing Flow

1.  **Parsing Phase**: The XML parser reads all table definitions and identifies tables with the `extends` attribute.
2.  **Mapping Construction**: A mapping from table names to table definitions is created.
3.  **Dependency Resolution**: Inheritance dependencies are resolved, processing parent tables first.
4.  **Circular Detection**: Prevents infinite recursion caused by circular inheritance.
5.  **Content Merging**: Merges the fields, indexes, and relationships from the parent table into the child table.

### Core Implementation Code

```java
/**
 * Process inheritance relationships for all tables.
 */
private void processTableInheritance(DatabaseSchema schema) {
    Map<String, TableDefinition> tableMap = new HashMap<>();
    
    // Build a map from table names to table definitions.
    for (TableDefinition table : schema.getTables()) {
        tableMap.put(table.getName(), table);
    }
    
    // Process the inheritance relationship for each table.
    for (TableDefinition table : schema.getTables()) {
        if (table.getExtendsTable() != null && !table.getExtendsTable().isEmpty()) {
            processTableInheritance(table, tableMap, new ArrayList<>());
        }
    }
}

/**
 * Recursively process the inheritance relationship for a single table.
 */
private void processTableInheritance(TableDefinition table, 
                                   Map<String, TableDefinition> tableMap, 
                                   List<String> processedTables) {
    // Circular inheritance detection.
    if (processedTables.contains(table.getName())) {
        log.warn("Circular inheritance detected for table: {}", table.getName());
        return;
    }
    
    processedTables.add(table.getName());
    
    String parentTableName = table.getExtendsTable();
    if (parentTableName != null && !parentTableName.isEmpty()) {
        TableDefinition parentTable = tableMap.get(parentTableName);
        if (parentTable == null) {
            log.warn("Parent table not found: {}, for child table: {}", parentTableName, table.getName());
            return;
        }
        
        // Recursively process the parent table's inheritance relationship.
        if (parentTable.getExtendsTable() != null && !parentTable.getExtendsTable().isEmpty()) {
            processTableInheritance(parentTable, tableMap, new ArrayList<>(processedTables));
        }
        
        // Merge fields from the parent table (before the child table's fields).
        List<FieldDefinition> mergedFields = new ArrayList<>(parentTable.getFields());
        mergedFields.addAll(table.getFields());
        table.setFields(mergedFields);
        
        // Merge indexes from the parent table.
        List<IndexDefinition> mergedIndexes = new ArrayList<>(parentTable.getIndexes());
        mergedIndexes.addAll(table.getIndexes());
        table.setIndexes(mergedIndexes);
        
        // Merge relationships from the parent table.
        List<RelationDefinition> mergedRelations = new ArrayList<>(parentTable.getRelations());
        mergedRelations.addAll(table.getRelations());
        table.setRelations(mergedRelations);
        
        log.debug("Table {} inherited definitions from parent table {}", table.getName(), parentTableName);
    }
}
```

## Detailed Inheritance Rules

### 1. Field Inheritance Rules

-   **Inheritance Order**: Parent table fields are inserted at the **beginning** of the child table's field list.
-   **Field Merging**: The fields of the parent and child tables are merged into a complete field list.
-   **Name Conflict Handling**: If the parent and child tables have fields with the same name, the child table's field is retained (the one defined later overrides the one defined earlier).
-   **Attribute Inheritance**: All field attributes (type, length, constraints, etc.) are inherited.

#### Example

```xml
<!-- Parent table -->
<table name="base_entity">
    <fields>
        <field name="id" type="LONG" primaryKey="true" autoIncrement="true"/>
        <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP"/>
        <field name="updated_time" type="DATETIME"/>
    </fields>
</table>

<!-- Child table -->
<table name="users" extends="base_entity">
    <fields>
        <field name="username" type="STRING" length="50"/>
        <field name="email" type="STRING" length="100"/>
    </fields>
</table>
```

**Field order in the `users` table after inheritance**:
1.  `id` (from `base_entity`)
2.  `created_time` (from `base_entity`)
3.  `updated_time` (from `base_entity`)
4.  `username` (from `users` table)
5.  `email` (from `users` table)

### 2. Index Inheritance Rules

-   **Complete Inheritance**: The child table inherits all index definitions from the parent table.
-   **Index Merging**: The indexes of the parent and child tables are merged.
-   **Name Uniqueness**: You need to ensure that index names do not conflict after merging.
-   **Field Dependency**: Inherited indexes must be based on inherited fields.

#### Example

```xml
<!-- Parent table -->
<table name="base_entity">
    <fields>
        <field name="id" type="LONG" primaryKey="true"/>
        <field name="created_time" type="DATETIME"/>
    </fields>
    <indexes>
        <index name="idx_created_time" type="NORMAL">
            <columns>
                <column name="created_time"/>
            </columns>
        </index>
    </indexes>
</table>

<!-- Child table -->
<table name="users" extends="base_entity">
    <fields>
        <field name="username" type="STRING" length="50"/>
    </fields>
    <indexes>
        <index name="uk_username" type="UNIQUE">
            <columns>
                <column name="username"/>
            </columns>
        </index>
    </indexes>
</table>
```

**Indexes in the `users` table after inheritance**:
1.  `idx_created_time` (from `base_entity`)
2.  `uk_username` (from `users` table)

### 3. Relationship Inheritance Rules

-   **Relationship Transfer**: The child table inherits all foreign key relationships from the parent table.
-   **Reference Validity**: Inherited foreign key relationships must be based on inherited fields.
-   **Constraint Merging**: The foreign key constraints of the parent and child tables are merged.
-   **Cascade Behavior**: The cascade behavior of inherited foreign keys remains unchanged.

#### Example

```xml
<!-- Parent table -->
<table name="base_entity">
    <fields>
        <field name="id" type="LONG" primaryKey="true"/>
        <field name="creator_id" type="LONG"/>
    </fields>
    <relations>
        <relation name="fk_creator" 
                  column="creator_id" 
                  referenceTable="users" 
                  referenceColumn="id"/>
    </relations>
</table>

<!-- Child table -->
<table name="articles" extends="base_entity">
    <fields>
        <field name="title" type="STRING" length="200"/>
        <field name="category_id" type="LONG"/>
    </fields>
    <relations>
        <relation name="fk_category" 
                  column="category_id" 
                  referenceTable="categories" 
                  referenceColumn="id"/>
    </relations>
</table>
```

**Relationships in the `articles` table after inheritance**:
1.  `fk_creator` (from `base_entity`)
2.  `fk_category` (from `articles` table)

## Multi-level Inheritance

### Inheritance Chain Processing

FastLCDP supports multi-level inheritance, and the system recursively processes the entire inheritance chain:

```xml
<!-- Level 1: Base entity -->
<table name="base_entity">
    <fields>
        <field name="id" type="LONG" primaryKey="true" autoIncrement="true"/>
        <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP"/>
        <field name="updated_time" type="DATETIME"/>
    </fields>
</table>

<!-- Level 2: Auditable entity -->
<table name="auditable_entity" extends="base_entity">
    <fields>
        <field name="created_by" type="LONG"/>
        <field name="updated_by" type="LONG"/>
        <field name="version" type="LONG" defaultValue="0"/>
    </fields>
    <relations>
        <relation name="fk_created_by" column="created_by" referenceTable="users" referenceColumn="id"/>
        <relation name="fk_updated_by" column="updated_by" referenceTable="users" referenceColumn="id"/>
    </relations>
</table>

<!-- Level 3: Business entity -->
<table name="articles" extends="auditable_entity">
    <fields>
        <field name="title" type="STRING" length="200"/>
        <field name="content" type="TEXT"/>
        <field name="status" type="LONG" defaultValue="0"/>
    </fields>
    <indexes>
        <index name="idx_status" type="NORMAL">
            <columns>
                <column name="status"/>
            </columns>
        </index>
    </indexes>
</table>
```

**Final fields contained in the `articles` table**:
1.  `id` (from `base_entity`)
2.  `created_time` (from `base_entity`)
3.  `updated_time` (from `base_entity`)
4.  `created_by` (from `auditable_entity`)
5.  `updated_by` (from `auditable_entity`)
6.  `version` (from `auditable_entity`)
7.  `title` (from `articles` table)
8.  `content` (from `articles` table)
9.  `status` (from `articles` table)

## Circular Inheritance Detection

### Detection Mechanism

The system detects circular inheritance by maintaining a list of processed tables:

```java
private void processTableInheritance(TableDefinition table, 
                                   Map<String, TableDefinition> tableMap, 
                                   List<String> processedTables) {
    // Detect circular inheritance
    if (processedTables.contains(table.getName())) {
        log.warn("Circular inheritance detected for table: {}", table.getName());
        return;
    }
    
    processedTables.add(table.getName());
    // ... continue processing
}
```

### Circular Inheritance Example

```xml
<!-- Incorrect circular inheritance example -->
<table name="table_a" extends="table_b">
    <!-- ... -->
</table>

<table name="table_b" extends="table_c">
    <!-- ... -->
</table>

<table name="table_c" extends="table_a">
    <!-- ... -->
</table>
```

The system will detect this circular inheritance, log a warning message, and stop processing to avoid infinite recursion.

## Best Practices for Inheritance

### 1. Design Principles

#### Single Responsibility Principle
- Each parent table should have a clear responsibility.