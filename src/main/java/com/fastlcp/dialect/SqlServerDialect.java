package com.fastlcp.dialect;

import com.fastlcp.model.FieldDefinition;
import com.fastlcp.model.IndexDefinition;
import com.fastlcp.model.TableDefinition;
import com.fastlcp.model.RelationDefinition;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL Server数据库方言实现
 */
public class SqlServerDialect extends AbstractDatabaseDialect {
    
    @Override
    public String getIdentifierQuote() {
        return "[";
    }
    
    @Override
    public boolean supportsIfNotExists() {
        return false; // SQL Server不支持IF NOT EXISTS语法
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "IDENTITY(1,1)";
    }
    
    @Override
    public String getCurrentTimestampFunction() {
        return "GETDATE()";
    }
    
    @Override
    public String generateCreateDatabaseSql(String databaseName, String charset, String collation) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE DATABASE ").append(escapeIdentifier(databaseName));
        
        if (StringUtils.hasText(collation)) {
            sql.append(" COLLATE ").append(collation);
        }
        
        return sql.toString();
    }
    
    @Override
    public String generateDropDatabaseSql(String databaseName) {
        return "DROP DATABASE " + escapeIdentifier(databaseName);
    }
    
    @Override
    public String generateFieldType(FieldDefinition field) {
        String type = field.getType().toUpperCase();
        StringBuilder fieldType = new StringBuilder();
        
        switch (type) {
            // 新的通用类型映射
            case "INTEGER":
                fieldType.append("INT");
                break;
                
            case "LONG":
                fieldType.append("BIGINT");
                break;
                
            case "DECIMAL":
                int precision = field.getPrecision() != null ? field.getPrecision() : 
                               (field.getLength() != null ? field.getLength() : 18);
                int scale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("DECIMAL(").append(precision).append(",").append(scale).append(")");
                break;
                
            case "BOOLEAN":
                fieldType.append("BIT");
                break;
                
            case "CHAR":
                int charLength = field.getLength() != null ? field.getLength() : 1;
                fieldType.append("CHAR(").append(charLength).append(")");
                break;
                
            case "STRING":
                int length = field.getLength() != null ? field.getLength() : 255;
                if (length > 8000) {
                    fieldType.append("VARCHAR(MAX)");
                } else {
                    fieldType.append("VARCHAR(").append(length).append(")");
                }
                break;
                
            case "TEXT":
                fieldType.append("NVARCHAR(MAX)");
                break;
                
            case "BINARY":
                fieldType.append("VARBINARY(255)");
                break;
                
            case "BLOB":
                fieldType.append("VARBINARY(MAX)");
                break;
                
            case "DATETIME":
                fieldType.append("DATETIME2");
                break;
                
            case "JSON":
                fieldType.append("NVARCHAR(MAX)"); // SQL Server 2016+支持JSON，但存储为NVARCHAR
                break;
                
            // 保留对旧类型的兼容性支持
            case "VARCHAR":
                int varcharLength = field.getLength() != null ? field.getLength() : 255;
                if (varcharLength > 8000) {
                    fieldType.append("VARCHAR(MAX)");
                } else {
                    fieldType.append("VARCHAR(").append(varcharLength).append(")");
                }
                break;
                
            case "LONGTEXT":
            case "MEDIUMTEXT":
                fieldType.append("NVARCHAR(MAX)");
                break;
                
            case "TINYTEXT":
                fieldType.append("NVARCHAR(255)");
                break;
                
            case "INT":
                fieldType.append("INT");
                break;
                
            case "BIGINT":
                fieldType.append("BIGINT");
                break;
                
            case "TINYINT":
                fieldType.append("TINYINT");
                break;
                
            case "SMALLINT":
                fieldType.append("SMALLINT");
                break;
                
            case "NUMERIC":
                int numPrecision = field.getLength() != null ? field.getLength() : 18;
                int numScale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("DECIMAL(").append(numPrecision).append(",").append(numScale).append(")");
                break;
                
            case "FLOAT":
                fieldType.append("FLOAT");
                break;
                
            case "DOUBLE":
                fieldType.append("FLOAT(53)"); // SQL Server的DOUBLE对应FLOAT(53)
                break;
                
            case "BOOL":
                fieldType.append("BIT");
                break;
                
            case "DATE":
                fieldType.append("DATE");
                break;
                
            case "TIME":
                fieldType.append("TIME");
                break;
                
            case "TIMESTAMP":
                fieldType.append("DATETIME2");
                break;
                
            case "LONGBLOB":
            case "MEDIUMBLOB":
                fieldType.append("VARBINARY(MAX)");
                break;
                
            case "TINYBLOB":
                fieldType.append("VARBINARY(255)");
                break;
                
            default:
                // 对于未知类型，保持原样
                fieldType.append(type);
                break;
        }
        
        return fieldType.toString();
    }
    
    @Override
    public String generateCreateTableSql(TableDefinition table) {
        StringBuilder sql = new StringBuilder();
        
        // CREATE TABLE (SQL Server不支持IF NOT EXISTS)
        sql.append("CREATE TABLE ").append(escapeIdentifier(table.getName())).append(" (\n");
        
        // 字段定义
        if (table.getFields() != null && !table.getFields().isEmpty()) {
            String fieldDefinitions = table.getFields().stream()
                    .map(this::generateFieldDefinition)
                    .collect(Collectors.joining(",\n    "));
            sql.append("    ").append(fieldDefinitions);
        }
        
        // 主键定义
        List<FieldDefinition> primaryKeyFields = table.getFields().stream()
                .filter(field -> field.getPrimaryKey() != null && field.getPrimaryKey())
                .collect(Collectors.toList());
        
        if (!primaryKeyFields.isEmpty()) {
            sql.append(",\n    ").append(generatePrimaryKeyDefinition(primaryKeyFields));
        }
        
        // 外键约束
        if (table.getRelations() != null && !table.getRelations().isEmpty()) {
            for (RelationDefinition relation : table.getRelations()) {
                sql.append(",\n    ").append(generateForeignKeyConstraint(relation));
            }
        }
        
        sql.append("\n)");
        
        return sql.toString();
    }
    
    @Override
    public String generateDropTableSql(String tableName) {
        return "DROP TABLE " + escapeIdentifier(tableName);
    }
    
    @Override
    public String generateTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = " + escapeStringValue(tableName);
    }
    
    @Override
    public String generateDescribeTableSql(String tableName) {
        return "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = " + escapeStringValue(tableName) + " ORDER BY ORDINAL_POSITION";
    }
    
    @Override
    public String getDatabaseTypeName() {
        return "SQL Server";
    }
    
    @Override
    protected String generateTableOptions(TableDefinition table) {
        // SQL Server可以添加表选项，如文件组等
        return "";
    }
    
    @Override
    public String generateLimitSql(String baseSql, int offset, int limit) {
        // SQL Server 2012及以上版本支持OFFSET/FETCH
        StringBuilder sql = new StringBuilder(baseSql);
        
        // SQL Server需要ORDER BY才能使用OFFSET/FETCH
        if (!baseSql.toUpperCase().contains("ORDER BY")) {
            sql.append(" ORDER BY (SELECT NULL)");
        }
        
        sql.append(" OFFSET ").append(offset).append(" ROWS");
        sql.append(" FETCH NEXT ").append(limit).append(" ROWS ONLY");
        
        return sql.toString();
    }
    
    @Override
    public String generateCreateIndexSql(String tableName, IndexDefinition index) {
        StringBuilder sql = new StringBuilder();
        
        // 创建索引语句开始
        sql.append("CREATE ");
        
        // 索引类型
        if ("UNIQUE".equalsIgnoreCase(index.getType())) {
            sql.append("UNIQUE ");
        }
        
        sql.append("INDEX ");
        
        // 索引名
        if (StringUtils.hasText(index.getName())) {
            sql.append(escapeIdentifier(index.getName())).append(" ");
        }
        
        // 表名
        sql.append("ON ").append(escapeIdentifier(tableName));
        
        // 索引列
        sql.append(" (");
        if (index.getColumns() != null && !index.getColumns().isEmpty()) {
            String columns = index.getColumns().stream()
                    .map(column -> {
                        StringBuilder columnDef = new StringBuilder(escapeIdentifier(column.getName()));
                        if (StringUtils.hasText(column.getOrder())) {
                            columnDef.append(" ").append(column.getOrder());
                        }
                        return columnDef.toString();
                    })
                    .collect(Collectors.joining(", "));
            sql.append(columns);
        }
        sql.append(")");
        
        return sql.toString();
    }
    
    @Override
    public String escapeIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        return "[" + identifier.replace("]", "]]") + "]";
    }
    
    @Override
    public String generateFieldDefinition(FieldDefinition field) {
        StringBuilder sql = new StringBuilder();
        
        // 字段名
        sql.append(escapeIdentifier(field.getName()));
        
        // 字段类型
        sql.append(" ").append(generateFieldType(field));
        
        // 自增（SQL Server使用IDENTITY）
        if (field.getAutoIncrement() != null && field.getAutoIncrement()) {
            sql.append(" ").append(getAutoIncrementKeyword());
        }
        
        // 是否允许为空
        if (field.getNullable() != null && !field.getNullable()) {
            sql.append(" NOT NULL");
        }
        
        // 默认值
        if (StringUtils.hasText(field.getDefaultValue()) && 
            (field.getAutoIncrement() == null || !field.getAutoIncrement())) {
            sql.append(" DEFAULT ");
            if (field.getDefaultValue().equalsIgnoreCase("CURRENT_TIMESTAMP") ||
                field.getDefaultValue().equalsIgnoreCase("NOW()")) {
                sql.append(getCurrentTimestampFunction());
            } else if (field.getDefaultValue().startsWith("'") && field.getDefaultValue().endsWith("'")) {
                sql.append(field.getDefaultValue());
            } else {
                sql.append(escapeStringValue(field.getDefaultValue()));
            }
        }
        
        return sql.toString();
    }
}