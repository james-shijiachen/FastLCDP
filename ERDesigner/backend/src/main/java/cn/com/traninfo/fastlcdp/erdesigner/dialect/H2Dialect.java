package cn.com.traninfo.fastlcdp.erdesigner.dialect;

import cn.com.traninfo.fastlcdp.erdesigner.enums.PrimaryKeyTypeEnum;
import cn.com.traninfo.fastlcdp.erdesigner.model.FieldDefinition;
import cn.com.traninfo.fastlcdp.erdesigner.model.TableDefinition;
import org.springframework.util.StringUtils;

/**
 * H2数据库方言实现
 */
public class H2Dialect extends AbstractDatabaseDialect {
    
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
        return "AUTO_INCREMENT";
    }
    
    @Override
    public String getCurrentTimestampFunction() {
        return "CURRENT_TIMESTAMP";
    }
    
    @Override
    public String generateCreateDatabaseSql(String databaseName, String charset, String collation) {
        // H2是文件数据库，不需要显式创建数据库
        return "-- H2数据库会自动创建: " + databaseName;
    }
    
    @Override
    public String generateDropDatabaseSql(String databaseName) {
        return "-- H2数据库删除需要删除文件: " + databaseName;
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
                               (field.getLength() != null ? field.getLength() : 10);
                int scale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("DECIMAL(").append(precision).append(",").append(scale).append(")");
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
                fieldType.append("CLOB");
                break;
                
            case "BINARY":
                int binaryLength = field.getLength() != null ? field.getLength() : 255;
                fieldType.append("VARBINARY(").append(binaryLength).append(")");
                break;
                
            case "BLOB":
                fieldType.append("BLOB");
                break;
                
            case "DATETIME":
                fieldType.append("TIMESTAMP");
                break;
                
            case "JSON":
                fieldType.append("CLOB"); // H2不直接支持JSON，使用CLOB存储
                break;
                
            // 保留对旧类型的兼容性支持
            case "VARCHAR":
                int varcharLength = field.getLength() != null ? field.getLength() : 255;
                fieldType.append("VARCHAR(").append(varcharLength).append(")");
                break;
                
            case "LONGTEXT":
            case "MEDIUMTEXT":
            case "TINYTEXT":
                fieldType.append("CLOB");
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
                int numPrecision = field.getLength() != null ? field.getLength() : 10;
                int numScale = field.getScale() != null ? field.getScale() : 0;
                fieldType.append("DECIMAL(").append(numPrecision).append(",").append(numScale).append(")");
                break;
                
            case "FLOAT":
                fieldType.append("REAL");
                break;
                
            case "DOUBLE":
                fieldType.append("DOUBLE");
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
                fieldType.append("BLOB");
                break;
                
            default:
                // 对于未知类型，保持原样
                fieldType.append(type);
                break;
        }
        
        return fieldType.toString();
    }
    
    @Override
    public String generateTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = " + escapeStringValue(tableName.toUpperCase());
    }
    
    @Override
    public String generateDescribeTableSql(String tableName) {
        return "SELECT column_name, data_type, is_nullable, column_default FROM information_schema.columns WHERE table_name = " + escapeStringValue(tableName.toUpperCase()) + " ORDER BY ordinal_position";
    }
    
    @Override
    public String getDatabaseTypeName() {
        return "H2";
    }
    
    @Override
    protected String generateTableOptions(TableDefinition table) {
        // H2不支持ENGINE等MySQL特有的表选项，但支持注释
        StringBuilder options = new StringBuilder();
        
        // 表注释
        if (StringUtils.hasText(table.getComment())) {
            options.append(" COMMENT ").append(escapeStringValue(table.getComment()));
        }
        
        return options.toString();
    }
    
    @Override
    public String generateFieldDefinition(FieldDefinition field) {
        StringBuilder sql = new StringBuilder();
        
        // 字段名
        sql.append(escapeIdentifier(field.getName()));
        
        // 字段类型
        sql.append(" ").append(generateFieldType(field));
        
        // 自增（H2中AUTO_INCREMENT要在类型后面）
        if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
            sql.append(" ").append(getAutoIncrementKeyword());
        }
        
        // 是否允许为空
        if (field.getNullable() != null && !field.getNullable()) {
            sql.append(" NOT NULL");
        }
        
        // 默认值
        if (StringUtils.hasText(field.getDefaultValue())) {
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
        
        // 注释
        if (StringUtils.hasText(field.getComment())) {
            sql.append(" COMMENT ").append(escapeStringValue(field.getComment()));
        }
        
        return sql.toString();
    }
}