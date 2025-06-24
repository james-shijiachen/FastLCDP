package com.fastlcdp.service;

import com.fastlcdp.config.DatabaseConfig;
import com.fastlcdp.dialect.DatabaseDialect;
import com.fastlcdp.dialect.DatabaseDialectFactory;
import com.fastlcdp.model.*;
import com.fastlcdp.service.XmlParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL生成服务
 */
@Service
public class SqlGeneratorService {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlGeneratorService.class);
    
    @Autowired
    private DatabaseConfig databaseConfig;
    
    @Autowired
    private MetadataService metadataService;
    
    @Autowired
    private XmlParserService xmlParserService;
    
    /**
     * 生成创建数据库的SQL语句
     */
    public String generateCreateDatabaseSql(DatabaseSchema schema) {
        if (schema == null || !StringUtils.hasText(schema.getName())) {
            throw new IllegalArgumentException("Schema name cannot be null or empty");
        }
        
        logger.info("生成创建数据库SQL: {}", schema.getName());
        
        DatabaseDialect dialect = DatabaseDialectFactory.createDialect(databaseConfig.getType());
        return dialect.generateCreateDatabaseSql(schema.getName(), databaseConfig.getCharset(), databaseConfig.getCollation());
    }
    
    /**
     * 生成创建表的SQL语句
     */
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
    public String generateCreateTableSql(TableDefinition table, DatabaseSchema databaseSchema) {
        if (table == null || !StringUtils.hasText(table.getName())) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        
        logger.info("生成创建表SQL: {}", table.getName());
        
        DatabaseDialect dialect = DatabaseDialectFactory.createDialect(databaseConfig.getType());
        
        // 如果是MySQL方言且支持数据库模式继承，则使用扩展方法
        if (dialect instanceof com.fastlcdp.dialect.MySQLDialect) {
            com.fastlcdp.dialect.MySQLDialect mysqlDialect = (com.fastlcdp.dialect.MySQLDialect) dialect;
            return mysqlDialect.generateCreateTableSql(table, databaseSchema);
        }
        
        // 其他方言使用标准方法
        return dialect.generateCreateTableSql(table);
    }
    
    /**
     * 生成字段定义
     */
    private String generateFieldDefinition(FieldDefinition field) {
        DatabaseDialect dialect = DatabaseDialectFactory.createDialect(databaseConfig.getType());
        return dialect.generateFieldDefinition(field);
    }
    
    /**
     * 生成字段类型
     */
    private String generateFieldType(FieldDefinition field) {
        DatabaseDialect dialect = DatabaseDialectFactory.createDialect(databaseConfig.getType());
        return dialect.generateFieldType(field);
    }
    
    /**
     * 生成索引SQL定义
     * 
     * @param index 索引定义
     * @return 索引SQL
     */
    private String generateIndexSql(IndexDefinition index) {
        StringBuilder sql = new StringBuilder();
        
        String indexType = index.getType().toUpperCase();
        switch (indexType) {
            case "UNIQUE":
                sql.append("  UNIQUE KEY");
                break;
            case "FULLTEXT":
                sql.append("  FULLTEXT KEY");
                break;
            case "SPATIAL":
                sql.append("  SPATIAL KEY");
                break;
            default:
                sql.append("  KEY");
                break;
        }
        
        sql.append(" `").append(index.getName()).append("` (");
        
        String columns = index.getColumns().stream()
                .map(col -> {
                    StringBuilder colSql = new StringBuilder();
                    colSql.append("`").append(col.getName()).append("`");
                    if (col.getLength() != null) {
                        colSql.append("(").append(col.getLength()).append(")");
                    }
                    if ("DESC".equalsIgnoreCase(col.getOrder())) {
                        colSql.append(" DESC");
                    }
                    return colSql.toString();
                })
                .collect(Collectors.joining(", "));
        
        sql.append(columns).append(")");
        
        if (StringUtils.hasText(index.getMethod()) && !"BTREE".equalsIgnoreCase(index.getMethod())) {
            sql.append(" USING ").append(index.getMethod().toUpperCase());
        }
        
        if (StringUtils.hasText(index.getComment())) {
            sql.append(" COMMENT '").append(escapeString(index.getComment())).append("'");
        }
        
        return sql.toString();
    }
    
    /**
     * 生成外键SQL定义
     * 
     * @param relation 关联定义
     * @return 外键SQL
     */
    private String generateForeignKeySql(RelationDefinition relation) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("  CONSTRAINT `").append(relation.getName()).append("`");
        sql.append(" FOREIGN KEY (`").append(relation.getColumn()).append("`)");
        sql.append(" REFERENCES `").append(relation.getReferenceTable()).append("`");
        sql.append(" (`").append(relation.getReferenceColumn()).append("`)");
        
        if (StringUtils.hasText(relation.getOnDelete()) && !"RESTRICT".equalsIgnoreCase(relation.getOnDelete())) {
            sql.append(" ON DELETE ").append(relation.getOnDelete().replace("_", " ").toUpperCase());
        }
        
        if (StringUtils.hasText(relation.getOnUpdate()) && !"RESTRICT".equalsIgnoreCase(relation.getOnUpdate())) {
            sql.append(" ON UPDATE ").append(relation.getOnUpdate().replace("_", " ").toUpperCase());
        }
        
        return sql.toString();
    }
    
    /**
     * 判断是否为字符串类型
     * 
     * @param type 字段类型
     * @return 是否为字符串类型
     */
    private boolean isStringType(String type) {
        String upperType = type.toUpperCase();
        return upperType.contains("CHAR") || upperType.contains("TEXT") || 
               upperType.equals("DATE") || upperType.equals("TIME") || 
               upperType.equals("DATETIME") || upperType.equals("TIMESTAMP");
    }
    
    /**
     * 生成创建索引的SQL语句
     */
    public String generateCreateIndexSql(String tableName, IndexDefinition index) {
        if (!StringUtils.hasText(tableName) || index == null) {
            throw new IllegalArgumentException("Table name and index definition cannot be null");
        }
        
        logger.info("生成创建索引SQL: {} on {}", index.getName(), tableName);
        
        DatabaseDialect dialect = DatabaseDialectFactory.createDialect(databaseConfig.getType());
        return dialect.generateCreateIndexSql(tableName, index);
    }
    
    /**
     * 转义标识符
     */
    private String escapeIdentifier(String identifier) {
        DatabaseDialect dialect = DatabaseDialectFactory.createDialect(databaseConfig.getType());
        return dialect.escapeIdentifier(identifier);
    }
    
    /**
     * 生成完整的模式SQL（包括数据库、表、索引等）
     */
    public String generateFullSchemaSql(DatabaseSchema schema) {
        if (schema == null) {
            throw new IllegalArgumentException("Schema definition cannot be null");
        }
        
        logger.info("生成完整模式SQL: {}", schema.getName());
        
        StringBuilder sql = new StringBuilder();
        
        // 生成数据库创建语句
        sql.append("-- 创建数据库\n");
        sql.append(generateCreateDatabaseSql(schema));
        sql.append("\n\n");
        
        // 生成表创建语句
        if (schema.getTables() != null && !schema.getTables().isEmpty()) {
            sql.append("-- 创建表\n");
            for (TableDefinition table : schema.getTables()) {
                sql.append(generateCreateTableSql(table, schema));
                sql.append("\n\n");
            }
            
            // 生成索引创建语句
            sql.append("-- 创建索引\n");
            for (TableDefinition table : schema.getTables()) {
                if (table.getIndexes() != null && !table.getIndexes().isEmpty()) {
                    for (IndexDefinition index : table.getIndexes()) {
                        sql.append(generateCreateIndexSql(table.getName(), index));
                        sql.append("\n");
                    }
                }
            }
        }
        
        return sql.toString();
    }
    
    /**
     * 生成并保存元数据到数据库
     */
    public void generateAndSaveMetadata(DatabaseSchema schema) {
        if (schema == null) {
            throw new IllegalArgumentException("Schema definition cannot be null");
        }
        
        logger.info("生成并保存元数据: {}", schema.getName());
        
        // 保存元数据到数据库
        metadataService.saveSchemaDefinition(schema);
        
        logger.info("元数据保存完成: {}", schema.getName());
    }
    
    /**
     * 从数据库加载元数据并生成SQL
     */
    public String generateSqlFromMetadata(String schemaName) {
        if (!StringUtils.hasText(schemaName)) {
            throw new IllegalArgumentException("Schema name cannot be null or empty");
        }
        
        logger.info("从元数据生成SQL: {}", schemaName);
        
        // 从数据库加载元数据
        DatabaseSchema schema = metadataService.getSchemaDefinition(schemaName);
        if (schema == null) {
            throw new IllegalArgumentException("Schema not found: " + schemaName);
        }
        
        // 生成SQL
        return generateFullSchemaSql(schema);
    }
    
    /**
     * 生成元数据表DDL
     */
    public String generateMetadataTableDDL() {
        return metadataService.generateMetadataTableDDL();
    }
    
    /**
     * 为指定数据库类型生成SQL
     * 
     * @param xmlFile XML文件
     * @param databaseType 数据库类型
     * @return 生成的SQL
     */
    public String generateSqlForDatabase(File xmlFile, String databaseType) {
        if (xmlFile == null || !xmlFile.exists()) {
            throw new IllegalArgumentException("XML file cannot be null or does not exist");
        }
        
        if (!StringUtils.hasText(databaseType)) {
            throw new IllegalArgumentException("Database type cannot be null or empty");
        }
        
        logger.info("为数据库类型 {} 生成SQL: {}", databaseType, xmlFile.getName());
        
        try {
            // 解析XML文件
            DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
            
            // 使用指定的数据库方言生成SQL
            DatabaseConfig.DatabaseType dbType = DatabaseConfig.DatabaseType.valueOf(databaseType.toUpperCase());
            DatabaseDialect dialect = DatabaseDialectFactory.createDialect(dbType);
            
            StringBuilder sql = new StringBuilder();
            
            // 生成数据库创建语句
            sql.append("-- 创建数据库 (").append(databaseType).append(")\n");
            sql.append(dialect.generateCreateDatabaseSql(schema.getName(), databaseConfig.getCharset(), databaseConfig.getCollation()));
            sql.append("\n\n");
            
            // 生成表创建语句
            if (schema.getTables() != null && !schema.getTables().isEmpty()) {
                sql.append("-- 创建表\n");
                for (TableDefinition table : schema.getTables()) {
                    // 如果是MySQL方言且支持数据库模式继承，则使用扩展方法
                    if (dialect instanceof com.fastlcdp.dialect.MySQLDialect) {
                com.fastlcdp.dialect.MySQLDialect mysqlDialect = (com.fastlcdp.dialect.MySQLDialect) dialect;
                        sql.append(mysqlDialect.generateCreateTableSql(table, schema));
                    } else {
                        sql.append(dialect.generateCreateTableSql(table));
                    }
                    sql.append("\n\n");
                }
                
                // 生成索引创建语句
                sql.append("-- 创建索引\n");
                for (TableDefinition table : schema.getTables()) {
                    if (table.getIndexes() != null && !table.getIndexes().isEmpty()) {
                        for (IndexDefinition index : table.getIndexes()) {
                            sql.append(dialect.generateCreateIndexSql(table.getName(), index));
                            sql.append("\n");
                        }
                    }
                }
            }
            
            logger.info("SQL生成完成，数据库类型: {}", databaseType);
            return sql.toString();
            
        } catch (Exception e) {
            logger.error("生成SQL失败，数据库类型: {}, 文件: {}", databaseType, xmlFile.getName(), e);
            throw new RuntimeException("Failed to generate SQL for database type: " + databaseType, e);
        }
    }
    
    /**
     * 转义字符串中的特殊字符
     * 
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    private String escapeString(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "\\'").replace("\\", "\\\\");
    }
}