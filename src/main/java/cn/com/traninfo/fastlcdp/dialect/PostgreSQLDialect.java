package cn.com.traninfo.fastlcdp.dialect;

import cn.com.traninfo.fastlcdp.enums.PrimaryKeyTypeEnum;
import cn.com.traninfo.fastlcdp.model.FieldDefinition;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import cn.com.traninfo.fastlcdp.model.IndexDefinition;
import org.springframework.util.StringUtils;

/**
 * PostgreSQL数据库方言实现
 */
public class PostgreSQLDialect extends AbstractDatabaseDialect {
    
    @Override
    public String getIdentifierQuote() {
        return "\"";
    }
    
    @Override
    public boolean supportsIfNotExists() {
        return true;
    }
    
    @Override
    public String getAutoIncrementKeyword() {
        return "SERIAL";
    }
    
    @Override
    public String getCurrentTimestampFunction() {
        return "CURRENT_TIMESTAMP";
    }
    
    @Override
    public String generateCreateDatabaseSql(String databaseName, String charset, String collation) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE DATABASE ").append(escapeIdentifier(databaseName));
        
        if (StringUtils.hasText(charset)) {
            sql.append(" WITH ENCODING '").append(charset).append("'");
        }
        
        if (StringUtils.hasText(collation)) {
            sql.append(" LC_COLLATE '").append(collation).append("'");
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
                if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
                    fieldType.append("SERIAL");
                } else {
                    fieldType.append("INTEGER");
                }
                break;
                
            case "LONG":
                if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
                    fieldType.append("BIGSERIAL");
                } else {
                    fieldType.append("BIGINT");
                }
                break;
                
            case "DECIMAL":
                int precision = field.getPrecision() != null ? field.getPrecision() : 
                               (field.getLength() != null ? field.getLength() : 10);
                int scale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("NUMERIC(").append(precision).append(",").append(scale).append(")");
                break;
                
            case "BOOLEAN":
                fieldType.append("BOOLEAN");
                break;
                
            case "CHAR":
                int charLength = field.getLength() != null ? field.getLength() : 1;
                fieldType.append("CHAR(").append(charLength).append(")");
                break;
                
            case "STRING":
                int length = field.getLength() != null ? field.getLength() : 255;
                fieldType.append("VARCHAR(").append(length).append(")");
                break;
                
            case "TEXT":
                fieldType.append("TEXT");
                break;
                
            case "BINARY":
                fieldType.append("BYTEA");
                break;
                
            case "BLOB":
                fieldType.append("BYTEA");
                break;
                
            case "DATETIME":
                fieldType.append("TIMESTAMP");
                break;
                
            case "JSON":
                fieldType.append("JSONB"); // PostgreSQL原生支持JSONB
                break;
                
            // 保留对旧类型的兼容性支持
            case "VARCHAR":
                int varcharLength = field.getLength() != null ? field.getLength() : 255;
                fieldType.append("VARCHAR(").append(varcharLength).append(")");
                break;
                
            case "LONGTEXT":
            case "MEDIUMTEXT":
            case "TINYTEXT":
                fieldType.append("TEXT");
                break;
                
            case "INT":
                if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
                    fieldType.append("SERIAL");
                } else {
                    fieldType.append("INTEGER");
                }
                break;
                
            case "BIGINT":
                if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
                    fieldType.append("BIGSERIAL");
                } else {
                    fieldType.append("BIGINT");
                }
                break;
                
            case "TINYINT":
            case "SMALLINT":
                if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
                    fieldType.append("SMALLSERIAL");
                } else {
                    fieldType.append("SMALLINT");
                }
                break;
                
            case "NUMERIC":
                int numPrecision = field.getLength() != null ? field.getLength() : 10;
                int numScale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("NUMERIC(").append(numPrecision).append(",").append(numScale).append(")");
                break;
                
            case "FLOAT":
                fieldType.append("REAL");
                break;
                
            case "DOUBLE":
                fieldType.append("DOUBLE PRECISION");
                break;
                
            case "BOOL":
                fieldType.append("BOOLEAN");
                break;
                
            case "DATE":
                fieldType.append("DATE");
                break;
                
            case "TIME":
                fieldType.append("TIME");
                break;
                
            case "TIMESTAMP":
                fieldType.append("TIMESTAMP");
                break;
                
            case "LONGBLOB":
            case "MEDIUMBLOB":
            case "TINYBLOB":
                fieldType.append("BYTEA");
                break;
                
            default:
                // 对于未知类型，保持原样
                fieldType.append(type);
                break;
        }
        
        return fieldType.toString();
    }
    
    @Override
    public String generateFieldDefinition(FieldDefinition field) {
        StringBuilder sql = new StringBuilder();
        
        // 字段名
        sql.append(escapeIdentifier(field.getName()));
        
        // 字段类型
        sql.append(" ").append(generateFieldType(field));
        
        // 序列主键的默认值
        if (field.getPrimaryKey() == PrimaryKeyTypeEnum.SEQUENCE) {
            String sequenceName = "seq_" + field.getName();
            sql.append(" DEFAULT nextval('").append(sequenceName).append("')");
        }
        
        // 是否允许为空
        if (field.getNullable() != null && !field.getNullable()) {
            sql.append(" NOT NULL");
        }
        
        // 默认值（非序列字段）
        if (StringUtils.hasText(field.getDefaultValue()) && field.getPrimaryKey() != PrimaryKeyTypeEnum.SEQUENCE) {
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
        
        // 处理主键类型
        if (field.getPrimaryKey() == PrimaryKeyTypeEnum.AUTO_INCREMENT) {
            sql.append(" ").append(getAutoIncrementKeyword());
        }
        
        // 兼容旧的自增字段
        if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
            sql.append(" ").append(getAutoIncrementKeyword());
        }
        
        // 注释
        if (StringUtils.hasText(field.getComment())) {
            sql.append(" -- ").append(field.getComment());
        }
        
        return sql.toString();
    }
    
    @Override
    public String generateTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public' AND table_name = " + escapeStringValue(tableName);
    }
    
    @Override
    public String generateDescribeTableSql(String tableName) {
        return "SELECT column_name, data_type, is_nullable, column_default FROM information_schema.columns WHERE table_name = " + escapeStringValue(tableName) + " ORDER BY ordinal_position";
    }
    
    @Override
    public String getDatabaseTypeName() {
        return "PostgreSQL";
    }
    
    @Override
    protected String generateTableOptions(TableDefinition table) {
        // PostgreSQL不支持ENGINE等MySQL特有的表选项
        return "";
    }
    
    @Override
    public String generateCreateIndexSql(String tableName, IndexDefinition index) {
        StringBuilder sql = new StringBuilder();
        
        // 创建索引语句开始
        sql.append("CREATE ");
        
        // 索引类型
        if (index.getType() != null && "UNIQUE".equalsIgnoreCase(index.getType().name())) {
            sql.append("UNIQUE ");
        }
        
        sql.append("INDEX ");
        
        // 索引名
        if (StringUtils.hasText(index.getName())) {
            sql.append(escapeIdentifier(index.getName())).append(" ");
        }
        
        // 表名
        sql.append("ON ").append(escapeIdentifier(tableName));
        
        // 索引方法
        if (index.getMethod() != null) {
            sql.append(" USING ").append(index.getMethod().name());
        }
        
        // 索引列
        sql.append(" (");
        if (index.getColumns() != null && !index.getColumns().isEmpty()) {
            String columns = index.getColumns().stream()
                    .map(column -> {
                        StringBuilder columnDef = new StringBuilder(escapeIdentifier(column.getName()));
                        if (column.getOrder() != null) {
                            columnDef.append(" ").append(column.getOrder().name());
                        }
                        return columnDef.toString();
                    })
                    .collect(java.util.stream.Collectors.joining(", "));
            sql.append(columns);
        }
        sql.append(")");
        
        return sql.toString();
    }
    
    @Override
    public String generateLimitSql(String baseSql, int offset, int limit) {
        return baseSql + " LIMIT " + limit + " OFFSET " + offset;
    }
    
    @Override
    public String generateCreateSequenceSql(String sequenceName) {
        return "CREATE SEQUENCE " + escapeIdentifier(sequenceName) + 
               " START 1 INCREMENT 1";
    }
    
    @Override
    public String generateDropSequenceSql(String sequenceName) {
        return "DROP SEQUENCE IF EXISTS " + escapeIdentifier(sequenceName);
    }
}