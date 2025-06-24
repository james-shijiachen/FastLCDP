package cn.com.traninfo.fastlcdp.dialect;

import cn.com.traninfo.fastlcdp.model.FieldDefinition;
import cn.com.traninfo.fastlcdp.model.IndexDefinition;
import cn.com.traninfo.fastlcdp.model.RelationDefinition;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽象数据库方言基类
 * 提供通用的SQL生成逻辑
 */
public abstract class AbstractDatabaseDialect implements DatabaseDialect {
    
    @Override
    public String escapeIdentifier(String identifier) {
        if (!StringUtils.hasText(identifier)) {
            return identifier;
        }
        String quote = getIdentifierQuote();
        return quote + identifier + quote;
    }
    
    @Override
    public String escapeStringValue(String value) {
        if (value == null) {
            return "NULL";
        }
        return "'" + value.replace("'", "''") + "'";
    }
    
    @Override
    public String generateFieldDefinition(FieldDefinition field) {
        StringBuilder sql = new StringBuilder();
        
        // 字段名
        sql.append(escapeIdentifier(field.getName()));
        
        // 字段类型
        sql.append(" ").append(generateFieldType(field));
        
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
        
        // 自增
        if (field.getAutoIncrement() != null && field.getAutoIncrement()) {
            sql.append(" ").append(getAutoIncrementKeyword());
        }
        
        // 注释
        if (StringUtils.hasText(field.getComment())) {
            sql.append(" COMMENT ").append(escapeStringValue(field.getComment()));
        }
        
        return sql.toString();
    }
    
    @Override
    public String generatePrimaryKeyDefinition(List<FieldDefinition> primaryKeyFields) {
        if (primaryKeyFields == null || primaryKeyFields.isEmpty()) {
            return "";
        }
        
        String columns = primaryKeyFields.stream()
                .map(field -> escapeIdentifier(field.getName()))
                .collect(Collectors.joining(", "));
        
        return "PRIMARY KEY (" + columns + ")";
    }
    
    @Override
    public String generateCreateIndexSql(String tableName, IndexDefinition index) {
        StringBuilder sql = new StringBuilder();
        
        // 创建索引语句开始
        sql.append("CREATE ");
        
        // 索引类型
        if ("UNIQUE".equalsIgnoreCase(index.getType())) {
            sql.append("UNIQUE ");
        } else if ("FULLTEXT".equalsIgnoreCase(index.getType())) {
            sql.append("FULLTEXT ");
        } else if ("SPATIAL".equalsIgnoreCase(index.getType())) {
            sql.append("SPATIAL ");
        }
        
        sql.append("INDEX ");
        
        // 索引名
        if (StringUtils.hasText(index.getName())) {
            sql.append(escapeIdentifier(index.getName())).append(" ");
        }
        
        // 表名
        sql.append("ON ").append(escapeIdentifier(tableName));
        
        // 索引方法
        if (StringUtils.hasText(index.getMethod())) {
            sql.append(" USING ").append(index.getMethod());
        }
        
        // 索引列
        sql.append(" (");
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
    public String generateForeignKeyConstraint(RelationDefinition relation) {
        StringBuilder sql = new StringBuilder();
        
        // 约束名
        if (StringUtils.hasText(relation.getName())) {
            sql.append("CONSTRAINT ").append(escapeIdentifier(relation.getName())).append(" ");
        }
        
        // 外键定义
        sql.append("FOREIGN KEY (").append(escapeIdentifier(relation.getColumn())).append(") ");
        sql.append("REFERENCES ").append(escapeIdentifier(relation.getReferenceTable()));
        sql.append("(").append(escapeIdentifier(relation.getReferenceColumn())).append(")");
        
        // 级联操作
        if (StringUtils.hasText(relation.getOnDelete())) {
            sql.append(" ON DELETE ").append(relation.getOnDelete());
        }
        if (StringUtils.hasText(relation.getOnUpdate())) {
            sql.append(" ON UPDATE ").append(relation.getOnUpdate());
        }
        
        return sql.toString();
    }
    
    @Override
    public String generateCreateTableSql(TableDefinition table) {
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
        
        // 表选项
        sql.append(generateTableOptions(table));
        
        return sql.toString();
    }
    
    @Override
    public String generateDropTableSql(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("DROP TABLE ");
        if (supportsIfNotExists()) {
            sql.append("IF EXISTS ");
        }
        sql.append(escapeIdentifier(tableName));
        return sql.toString();
    }
    
    @Override
    public String generateTableExistsSql(String tableName) {
        return "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = " + escapeStringValue(tableName);
    }
    
    @Override
    public String generateDescribeTableSql(String tableName) {
        return "DESCRIBE " + escapeIdentifier(tableName);
    }
    
    @Override
    public String generateLimitSql(String baseSql, int offset, int limit) {
        return baseSql + " LIMIT " + limit + (offset > 0 ? " OFFSET " + offset : "");
    }
    
    /**
     * 生成表选项（如引擎、字符集等）
     * 子类可以重写此方法以提供特定的表选项
     */
    protected String generateTableOptions(TableDefinition table) {
        StringBuilder options = new StringBuilder();
        
        // 存储引擎
        if (StringUtils.hasText(table.getEngine())) {
            options.append(" ENGINE=").append(table.getEngine());
        }
        
        // 字符集
        if (StringUtils.hasText(table.getCharset())) {
            options.append(" DEFAULT CHARSET=").append(table.getCharset());
        }
        
        // 表注释
        if (StringUtils.hasText(table.getComment())) {
            options.append(" COMMENT=").append(escapeStringValue(table.getComment()));
        }
        
        return options.toString();
    }
}