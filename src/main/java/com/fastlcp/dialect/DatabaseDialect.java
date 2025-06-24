package com.fastlcp.dialect;

import com.fastlcp.model.FieldDefinition;
import com.fastlcp.model.IndexDefinition;
import com.fastlcp.model.RelationDefinition;
import com.fastlcp.model.TableDefinition;

import java.util.List;

/**
 * 数据库方言接口
 * 定义不同数据库的SQL生成规则
 */
public interface DatabaseDialect {
    
    /**
     * 获取标识符引用字符
     */
    String getIdentifierQuote();
    
    /**
     * 是否支持 IF NOT EXISTS 语法
     */
    boolean supportsIfNotExists();
    
    /**
     * 获取自增关键字
     */
    String getAutoIncrementKeyword();
    
    /**
     * 获取当前时间戳函数
     */
    String getCurrentTimestampFunction();
    
    /**
     * 生成创建数据库的SQL语句
     */
    String generateCreateDatabaseSql(String databaseName, String charset, String collation);
    
    /**
     * 生成删除数据库的SQL语句
     */
    String generateDropDatabaseSql(String databaseName);
    
    /**
     * 生成字段类型定义
     */
    String generateFieldType(FieldDefinition field);
    
    /**
     * 生成字段定义SQL
     */
    String generateFieldDefinition(FieldDefinition field);
    
    /**
     * 生成主键定义SQL
     */
    String generatePrimaryKeyDefinition(List<FieldDefinition> primaryKeyFields);
    
    /**
     * 生成索引创建SQL
     */
    String generateCreateIndexSql(String tableName, IndexDefinition index);
    
    /**
     * 生成外键约束SQL
     */
    String generateForeignKeyConstraint(RelationDefinition relation);
    
    /**
     * 生成创建表的SQL语句
     */
    String generateCreateTableSql(TableDefinition table);
    
    /**
     * 生成删除表的SQL语句
     */
    String generateDropTableSql(String tableName);
    
    /**
     * 生成检查表是否存在的SQL语句
     */
    String generateTableExistsSql(String tableName);
    
    /**
     * 生成获取表结构的SQL语句
     */
    String generateDescribeTableSql(String tableName);
    
    /**
     * 转义标识符（表名、字段名等）
     */
    String escapeIdentifier(String identifier);
    
    /**
     * 转义字符串值
     */
    String escapeStringValue(String value);
    
    /**
     * 获取分页查询SQL
     */
    String generateLimitSql(String baseSql, int offset, int limit);
    
    /**
     * 获取数据库类型名称
     */
    String getDatabaseTypeName();
}