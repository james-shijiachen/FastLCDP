package cn.com.traninfo.fastlcdp.dialect;

import cn.com.traninfo.fastlcdp.enums.PrimaryKeyType;
import cn.com.traninfo.fastlcdp.model.FieldDefinition;
import cn.com.traninfo.fastlcdp.model.IndexDefinition;
import cn.com.traninfo.fastlcdp.model.RelationDefinition;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MySQL数据库方言实现
 */
public class MySQLDialect extends AbstractDatabaseDialect {
    
    @Override
    public String getIdentifierQuote() {
        return "`";
    }
    
    @Override
    public boolean supportsIfNotExists() {
        return true;
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "AUTO_INCREMENT";
    }
    
    @Override
    public String getCurrentTimestampFunction() {
        return "CURRENT_TIMESTAMP";
    }
    
    @Override
    public String generateCreateDatabaseSql(String databaseName, String charset, String collation) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE DATABASE IF NOT EXISTS ").append(escapeIdentifier(databaseName));
        
        if (StringUtils.hasText(charset)) {
            sql.append(" DEFAULT CHARACTER SET ").append(charset);
        }
        
        if (StringUtils.hasText(collation)) {
            sql.append(" DEFAULT COLLATE ").append(collation);
        }
        
        return sql.toString();
    }
    
    @Override
    public String generateDropDatabaseSql(String databaseName) {
        return "DROP DATABASE IF EXISTS " + escapeIdentifier(databaseName);
    }
    
    @Override
    public String generateFieldType(FieldDefinition field) {
        String type = field.getType().toUpperCase();
        StringBuilder fieldType = new StringBuilder();
        
        switch (type) {
            // 新的通用类型映射
            case "INTEGER":
                fieldType.append("INT");
                if (field.getLength() != null) {
                    fieldType.append("(").append(field.getLength()).append(")");
                }
                break;
                
            case "LONG":
                fieldType.append("BIGINT");
                if (field.getLength() != null) {
                    fieldType.append("(").append(field.getLength()).append(")");
                }
                break;
                
            case "DECIMAL":
                fieldType.append("DECIMAL");
                int precision = field.getPrecision() != null ? field.getPrecision() : 
                               (field.getLength() != null ? field.getLength() : 10);
                int scale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("(").append(precision).append(",").append(scale).append(")");
                break;
                
            case "BOOLEAN":
                fieldType.append("TINYINT(1)");
                break;
                
            case "CHAR":
                fieldType.append("CHAR");
                int charLength = field.getLength() != null ? field.getLength() : 1;
                fieldType.append("(").append(charLength).append(")");
                break;
                
            case "STRING":
                fieldType.append("VARCHAR");
                int varcharLength = field.getLength() != null ? field.getLength() : 255;
                fieldType.append("(").append(varcharLength).append(")");
                break;
                
            case "TEXT":
                fieldType.append("TEXT");
                break;
                
            case "BINARY":
                fieldType.append("VARBINARY");
                int binaryLength = field.getLength() != null ? field.getLength() : 255;
                fieldType.append("(").append(binaryLength).append(")");
                break;
                
            case "BLOB":
                fieldType.append("BLOB");
                break;
                
            case "DATETIME":
                fieldType.append("DATETIME");
                break;
                
            case "JSON":
                fieldType.append("JSON");
                break;
                
            // 保留对旧类型的兼容性支持
            case "VARCHAR":
                fieldType.append("VARCHAR");
                int length = field.getLength() != null ? field.getLength() : 255;
                fieldType.append("(").append(length).append(")");
                break;
                
            case "NUMERIC":
                fieldType.append("DECIMAL");
                int numPrecision = field.getLength() != null ? field.getLength() : 10;
                int numScale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("(").append(numPrecision).append(",").append(numScale).append(")");
                break;
                
            case "FLOAT":
            case "DOUBLE":
                fieldType.append(type);
                if (field.getLength() != null) {
                    fieldType.append("(").append(field.getLength());
                    if (field.getScale() != null) {
                        fieldType.append(",").append(field.getScale());
                    }
                    fieldType.append(")");
                }
                break;
                
            case "INT":
                fieldType.append("INT");
                if (field.getLength() != null) {
                    fieldType.append("(").append(field.getLength()).append(")");
                }
                break;
                
            case "BIGINT":
                fieldType.append("BIGINT");
                if (field.getLength() != null) {
                    fieldType.append("(").append(field.getLength()).append(")");
                }
                break;
                
            case "TINYINT":
                if (field.getLength() != null) {
                    fieldType.append("(").append(field.getLength()).append(")");
                } else {
                    fieldType.append("(4)");
                }
                break;
                
            case "SMALLINT":
                if (field.getLength() != null) {
                    fieldType.append("(").append(field.getLength()).append(")");
                }
                break;
                
            case "BOOL":
                fieldType = new StringBuilder("TINYINT(1)");
                break;
                
            case "LONGTEXT":
            case "MEDIUMTEXT":
            case "TINYTEXT":
            case "LONGBLOB":
            case "MEDIUMBLOB":
            case "TINYBLOB":
            case "DATE":
            case "TIME":
            case "TIMESTAMP":
            case "YEAR":
                // 这些类型不需要长度参数
                break;
                
            default:
                // 对于未知类型，保持原样
                break;
        }
        
        return fieldType.toString();
    }
    
    @Override
    public String generateCreateTableSql(TableDefinition table) {
        return generateCreateTableSql(table, null);
    }
    
    /**
     * 生成创建表的SQL语句，支持数据库级别的默认配置继承
     * 
     * @param table 表定义
     * @param databaseSchema 数据库模式定义，用于获取默认的engine和charset
     * @return 创建表的SQL语句
     */
    public String generateCreateTableSql(TableDefinition table, cn.com.traninfo.fastlcdp.model.DatabaseSchema databaseSchema) {
        StringBuilder sql = new StringBuilder();
        
        // CREATE TABLE
        sql.append("CREATE TABLE ");
        if (supportsIfNotExists()) {
            sql.append("IF NOT EXISTS ");
        }
        sql.append(escapeIdentifier(table.getName())).append(" (\n");
        
        // 字段定义
        if (table.getFields() != null && !table.getFields().isEmpty()) {
            String fieldDefinitions = table.getFields().stream()
                    .map(this::generateFieldDefinition)
                    .collect(Collectors.joining(",\n    "));
            sql.append("    ").append(fieldDefinitions);
        }
        
        // 主键定义
        List<FieldDefinition> primaryKeyFields = table.getFields().stream()
                .filter(field -> field.getPrimaryKey() != null && !PrimaryKeyType.NONE.equals(field.getPrimaryKey()))
                .collect(Collectors.toList());
        
        if (!primaryKeyFields.isEmpty()) {
            sql.append(",\n    ").append(generatePrimaryKeyDefinition(primaryKeyFields));
        }
        
        // 索引定义
        if (table.getIndexes() != null && !table.getIndexes().isEmpty()) {
            for (IndexDefinition index : table.getIndexes()) {
                sql.append(",\n    ").append(generateIndexDefinition(index));
            }
        }
        
        // 外键约束
        if (table.getRelations() != null && !table.getRelations().isEmpty()) {
            for (RelationDefinition relation : table.getRelations()) {
                sql.append(",\n    ").append(generateForeignKeyConstraint(relation));
            }
        }
        
        sql.append("\n)");
        
        // MySQL特有的表选项 - 支持继承数据库级别的默认配置
        String effectiveEngine = getEffectiveEngine(table, databaseSchema);
        if (StringUtils.hasText(effectiveEngine)) {
            sql.append(" ENGINE=").append(effectiveEngine);
        }
        
        String effectiveCharset = getEffectiveCharset(table, databaseSchema);
        if (StringUtils.hasText(effectiveCharset)) {
            sql.append(" DEFAULT CHARSET=").append(effectiveCharset);
        }
        
        // 默认使用utf8mb4_unicode_ci排序规则
        sql.append(" COLLATE=utf8mb4_unicode_ci");
        
        // 表注释
        if (StringUtils.hasText(table.getComment())) {
            sql.append(" COMMENT=").append(escapeStringValue(table.getComment()));
        }
        
        return sql.toString();
    }
    
    /**
     * 获取有效的存储引擎
     * 优先使用表定义的engine，如果表没有定义则使用数据库级别的默认值
     * 
     * @param table 表定义
     * @param databaseSchema 数据库模式定义
     * @return 有效的存储引擎
     */
    private String getEffectiveEngine(TableDefinition table, cn.com.traninfo.fastlcdp.model.DatabaseSchema databaseSchema) {
        // 优先使用表级别的engine定义
        if (StringUtils.hasText(table.getEngine())) {
            return table.getEngine();
        }
        
        // 如果表没有定义engine，且数据库模式存在，则使用数据库级别的engine
        if (databaseSchema != null && StringUtils.hasText(databaseSchema.getEngine())) {
            return databaseSchema.getEngine();
        }
        
        // 默认使用InnoDB
        return "InnoDB";
    }
    
    /**
     * 获取有效的字符集
     * 优先使用表定义的charset，如果表没有定义则使用数据库级别的默认值
     * 
     * @param table 表定义
     * @param databaseSchema 数据库模式定义
     * @return 有效的字符集
     */
    private String getEffectiveCharset(TableDefinition table, cn.com.traninfo.fastlcdp.model.DatabaseSchema databaseSchema) {
        // 优先使用表级别的charset定义
        if (StringUtils.hasText(table.getCharset())) {
            return table.getCharset();
        }
        
        // 如果表没有定义charset，且数据库模式存在，则使用数据库级别的charset
        if (databaseSchema != null && StringUtils.hasText(databaseSchema.getCharset())) {
            return databaseSchema.getCharset();
        }
        
        // 默认使用utf8mb4
        return "utf8mb4";
    }
    
    /**
     * 生成MySQL索引定义
     */
    private String generateIndexDefinition(IndexDefinition index) {
        StringBuilder sql = new StringBuilder();
        
        // 索引类型
        if ("UNIQUE".equalsIgnoreCase(index.getType())) {
            sql.append("UNIQUE ");
        } else if ("FULLTEXT".equalsIgnoreCase(index.getType())) {
            sql.append("FULLTEXT ");
        } else if ("SPATIAL".equalsIgnoreCase(index.getType())) {
            sql.append("SPATIAL ");
        }
        
        sql.append("KEY ");
        
        // 索引名
        if (StringUtils.hasText(index.getName())) {
            sql.append(escapeIdentifier(index.getName())).append(" ");
        }
        
        // 索引列
        sql.append("(");
        if (index.getColumns() != null && !index.getColumns().isEmpty()) {
            String columns = index.getColumns().stream()
                    .map(column -> {
                        StringBuilder columnDef = new StringBuilder(escapeIdentifier(column.getName()));
                        if (column.getLength() != null && column.getLength() > 0) {
                            columnDef.append("(").append(column.getLength()).append(")");
                        }
                        if (StringUtils.hasText(column.getOrder())) {
                            columnDef.append(" ").append(column.getOrder());
                        }
                        return columnDef.toString();
                    })
                    .collect(Collectors.joining(", "));
            sql.append(columns);
        }
        sql.append(")");
        
        // 索引注释
        if (StringUtils.hasText(index.getComment())) {
            sql.append(" COMMENT ").append(escapeStringValue(index.getComment()));
        }
        
        return sql.toString();
    }
    
    @Override
    public String generateTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = " + escapeStringValue(tableName);
    }
    
    @Override
    public String generateDescribeTableSql(String tableName) {
        return "DESCRIBE " + escapeIdentifier(tableName);
    }
    
    @Override
    public String getDatabaseTypeName() {
        return "MySQL";
    }
    
    @Override
    public String generateLimitSql(String baseSql, int offset, int limit) {
        if (offset > 0) {
            return baseSql + " LIMIT " + offset + ", " + limit;
        } else {
            return baseSql + " LIMIT " + limit;
        }
    }
}