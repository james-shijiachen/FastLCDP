# MyBatis Plus 自动建表功能使用指南

## 功能概述

本项目集成了MyBatis Plus自动建表功能，可以在Spring Boot启动时根据Entity类的注解自动创建对应的数据库表，无需手动编写DDL语句。

## 配置说明

### 1. 启用自动建表功能

在 `application.yaml` 中配置：

```yaml
database:
  type: MYSQL  # 数据库类型
  auto-create-metadata-tables: true  # 启用自动建表功能
```

### 2. 支持的数据库类型

- **H2**: 内存数据库，适用于开发测试
- **MYSQL**: MySQL数据库
- **POSTGRESQL**: PostgreSQL数据库
- **ORACLE**: Oracle数据库
- **SQL_SERVER**: SQL Server数据库

### 3. MyBatis Plus配置

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 驼峰命名转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
  global-config:
    db-config:
      id-type: auto  # 主键自增
      logic-delete-field: deleted  # 逻辑删除字段
      logic-delete-value: 1
      logic-not-delete-value: 0
```

## Entity类注解说明

### 1. 表注解

```java
@TableName("sys_user")  // 指定表名
public class UserEntity {
    // ...
}
```

### 2. 主键注解

```java
@TableId(type = IdType.AUTO)  // 自增主键
private Long id;
```

支持的主键类型：
- `IdType.AUTO`: 数据库自增
- `IdType.ASSIGN_ID`: 雪花算法
- `IdType.ASSIGN_UUID`: UUID
- `IdType.INPUT`: 手动输入

### 3. 字段注解

```java
@TableField("username")  // 指定字段名
private String username;

@TableField(value = "created_time", fill = FieldFill.INSERT)  // 自动填充
private LocalDateTime createdTime;

@TableLogic  // 逻辑删除
private Integer deleted;
```

### 4. 自动填充类型

- `FieldFill.INSERT`: 插入时填充
- `FieldFill.UPDATE`: 更新时填充
- `FieldFill.INSERT_UPDATE`: 插入和更新时填充

## 数据类型映射

### Java类型到数据库类型的映射

| Java类型 | MySQL | H2 | PostgreSQL | Oracle | SQL Server |
|---------|-------|----|-----------|---------|-----------|
| String | VARCHAR(255) | VARCHAR(255) | VARCHAR(255) | VARCHAR2(255) | NVARCHAR(255) |
| Long/long | BIGINT | BIGINT | BIGINT | NUMBER(19) | BIGINT |
| Integer/int | INT | INTEGER | INTEGER | NUMBER(10) | INT |
| Boolean/boolean | TINYINT(1) | BOOLEAN | BOOLEAN | NUMBER(1) | BIT |
| LocalDateTime | DATETIME | TIMESTAMP | TIMESTAMP | TIMESTAMP | DATETIME2 |
| LocalDate | DATE | DATE | DATE | DATE | DATE |
| LocalTime | TIME | TIME | TIME | TIMESTAMP | TIME |
| BigDecimal | DECIMAL(19,2) | DECIMAL(19,2) | DECIMAL(19,2) | NUMBER(19,2) | DECIMAL(19,2) |
| Double/double | DOUBLE | DOUBLE | DOUBLE PRECISION | NUMBER | FLOAT |
| Float/float | FLOAT | REAL | REAL | NUMBER | REAL |

## 使用示例

### 1. 创建Entity类

```java
@Data
@TableName("sys_user")
public class UserEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("email")
    private String email;
    
    @TableField("status")
    private Integer status;
    
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
    
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
    
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
```

### 2. 生成的SQL（MySQL示例）

```sql
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT AUTO_INCREMENT,
    `username` VARCHAR(255),
    `password` VARCHAR(255),
    `email` VARCHAR(255),
    `status` INT,
    `deleted` INT,
    `created_time` DATETIME,
    `updated_time` DATETIME,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

## 注意事项

### 1. 安全考虑

- 生产环境建议关闭自动建表功能
- 建议在开发和测试环境使用
- 生产环境应使用专门的数据库迁移工具

### 2. 性能考虑

- 自动建表只在应用启动时执行一次
- 会检查表是否存在，避免重复创建
- 对于大量表的项目，启动时间可能会增加

### 3. 限制说明

- 只支持基本的表结构创建
- 不支持复杂的约束和索引
- 不支持表结构变更（需要手动处理）
- 字段长度使用默认值，如需自定义需要手动建表

## 配置开关

可以通过配置文件控制自动建表功能：

```yaml
database:
  auto-create-metadata-tables: false  # 关闭自动建表
```

或者通过环境变量：

```bash
DATABASE_AUTO_CREATE_METADATA_TABLES=false
```

## 故障排除

### 1. 表创建失败

- 检查数据库连接配置
- 检查数据库用户权限
- 查看启动日志中的错误信息

### 2. 字段类型不匹配

- 检查Java类型是否支持
- 查看数据类型映射表
- 考虑使用自定义字段类型

### 3. 主键冲突

- 确保每个Entity只有一个@TableId注解
- 检查主键类型配置

## 最佳实践

1. **命名规范**: 使用统一的命名规范，如表名使用下划线分隔
2. **字段注释**: 在Entity类中添加详细的字段注释
3. **版本控制**: 将Entity类变更纳入版本控制
4. **测试验证**: 在测试环境充分验证表结构
5. **备份策略**: 生产环境部署前做好数据备份

## 扩展功能

如需更复杂的表结构管理，建议使用：

- **Flyway**: 数据库版本管理工具
- **Liquibase**: 数据库变更管理工具
- **MyBatis Generator**: 代码生成工具

这些工具可以与自动建表功能配合使用，提供更完整的数据库管理解决方案。