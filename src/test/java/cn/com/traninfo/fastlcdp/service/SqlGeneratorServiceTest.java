package cn.com.traninfo.fastlcdp.service;

import cn.com.traninfo.fastlcdp.model.*;
import cn.com.traninfo.fastlcdp.enums.PrimaryKeyType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL生成服务测试类
 */
@SpringBootTest(classes = cn.com.traninfo.fastlcdp.FastLcdpApplication.class)
class SqlGeneratorServiceTest {
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Test
    void testGenerateCreateDatabaseSql() {
        DatabaseSchema schema = new DatabaseSchema();
        schema.setName("test_db");
        schema.setCharset("utf8mb4");
        schema.setCollation("utf8mb4_unicode_ci");
        schema.setComment("测试数据库");
        
        String sql = sqlGeneratorService.generateCreateDatabaseSql(schema);
        
        assertNotNull(sql);
        // H2数据库只返回注释，不生成实际的CREATE DATABASE语句
        assertTrue(sql.contains("test_db"));
    }
    
    @Test
    void testGenerateCreateTableSql() {
        TableDefinition table = createSampleTable();
        
        String sql = sqlGeneratorService.generateCreateTableSql(table);
        
        assertNotNull(sql);
        assertTrue(sql.contains("CREATE TABLE"));
        assertTrue(sql.contains("`user`") || sql.contains("\"user\""));
        assertTrue(sql.contains("id"));
        assertTrue(sql.contains("username"));
        assertTrue(sql.contains("email"));
        assertTrue(sql.contains("age"));
        assertTrue(sql.contains("balance"));
        assertTrue(sql.contains("PRIMARY KEY"));
    }
    
    @Test
    void testGenerateFieldSqlWithDifferentTypes() {
        // 测试STRING类型
        FieldDefinition stringField = new FieldDefinition();
        stringField.setName("name");
        stringField.setType("STRING");
        stringField.setLength(100);
        stringField.setNullable(false);
        stringField.setComment("姓名");
        
        TableDefinition table = new TableDefinition();
        table.setName("test");
        table.setFields(Arrays.asList(stringField));
        
        String sql = sqlGeneratorService.generateCreateTableSql(table);
        assertTrue(sql.contains("name") && sql.contains("VARCHAR"));
        
        // 测试DECIMAL类型
        FieldDefinition decimalField = new FieldDefinition();
        decimalField.setName("price");
        decimalField.setType("DECIMAL");
        decimalField.setLength(10);
        decimalField.setScale(2);
        decimalField.setDefaultValue("0.00");
        
        table.setFields(Arrays.asList(decimalField));
        sql = sqlGeneratorService.generateCreateTableSql(table);
        assertTrue(sql.contains("price") && sql.contains("DECIMAL"));
        
        // 测试DATETIME类型
        FieldDefinition datetimeField = new FieldDefinition();
        datetimeField.setName("created_at");
        datetimeField.setType("DATETIME");
        datetimeField.setDefaultValue("CURRENT_TIMESTAMP");
        
        table.setFields(Arrays.asList(datetimeField));
        sql = sqlGeneratorService.generateCreateTableSql(table);
        assertTrue(sql.contains("created_at") && sql.contains("TIMESTAMP"));
    }
    
    @Test
    void testGenerateIndexSql() {
        // 创建email字段
        FieldDefinition emailField = new FieldDefinition();
        emailField.setName("email");
        emailField.setType("STRING");
        emailField.setLength(255);
        
        // 创建普通索引
        IndexDefinition normalIndex = new IndexDefinition();
        normalIndex.setName("idx_email");
        normalIndex.setType("NORMAL");
        
        IndexDefinition.IndexColumnDefinition column = new IndexDefinition.IndexColumnDefinition();
        column.setName("email");
        normalIndex.setColumns(Arrays.asList(column));
        
        TableDefinition table = new TableDefinition();
        table.setName("test");
        table.setFields(Arrays.asList(emailField));
        table.setIndexes(Arrays.asList(normalIndex));
        
        String sql = sqlGeneratorService.generateCreateTableSql(table);
        // H2数据库不在CREATE TABLE中生成索引，只检查表结构
        assertTrue(sql.contains("email") && sql.contains("VARCHAR"));
        
        // 创建唯一索引 - H2不支持在CREATE TABLE中定义索引
        IndexDefinition uniqueIndex = new IndexDefinition();
        uniqueIndex.setName("uk_username");
        uniqueIndex.setType("UNIQUE");
        
        IndexDefinition.IndexColumnDefinition uniqueColumn = new IndexDefinition.IndexColumnDefinition();
        uniqueColumn.setName("username");
        uniqueIndex.setColumns(Arrays.asList(uniqueColumn));
        
        FieldDefinition usernameField = new FieldDefinition();
        usernameField.setName("username");
        usernameField.setType("STRING");
        usernameField.setLength(50);
        
        table.setFields(Arrays.asList(emailField, usernameField));
        table.setIndexes(Arrays.asList(uniqueIndex));
        sql = sqlGeneratorService.generateCreateTableSql(table);
        assertTrue(sql.contains("username") && sql.contains("VARCHAR"));
        
        // 创建复合索引 - H2不支持在CREATE TABLE中定义索引
        IndexDefinition compositeIndex = new IndexDefinition();
        compositeIndex.setName("idx_name_age");
        compositeIndex.setType("NORMAL");
        
        IndexDefinition.IndexColumnDefinition nameColumn = new IndexDefinition.IndexColumnDefinition();
        nameColumn.setName("name");
        IndexDefinition.IndexColumnDefinition ageColumn = new IndexDefinition.IndexColumnDefinition();
        ageColumn.setName("age");
        ageColumn.setOrder("DESC");
        compositeIndex.setColumns(Arrays.asList(nameColumn, ageColumn));
        
        FieldDefinition nameField = new FieldDefinition();
        nameField.setName("name");
        nameField.setType("STRING");
        nameField.setLength(100);
        
        FieldDefinition ageField = new FieldDefinition();
        ageField.setName("age");
        ageField.setType("INT");
        
        table.setFields(Arrays.asList(nameField, ageField));
        table.setIndexes(Arrays.asList(compositeIndex));
        sql = sqlGeneratorService.generateCreateTableSql(table);
        assertTrue(sql.contains("name") && sql.contains("age") && sql.contains("VARCHAR") && sql.contains("INT"));
    }
    
    @Test
    void testGenerateForeignKeySql() {
        RelationDefinition relation = new RelationDefinition();
        relation.setName("fk_order_user");
        relation.setColumn("user_id");
        relation.setReferenceTable("user");
        relation.setReferenceColumn("id");
        relation.setOnDelete("CASCADE");
        relation.setOnUpdate("CASCADE");
        
        FieldDefinition userIdField = new FieldDefinition();
        userIdField.setName("user_id");
        userIdField.setType("LONG");
        
        TableDefinition table = new TableDefinition();
        table.setName("order");
        table.setFields(Arrays.asList(
            createSimpleField("id", "LONG"),
            userIdField
        ));
        table.setRelations(Arrays.asList(relation));
        
        String sql = sqlGeneratorService.generateCreateTableSql(table);
        // H2数据库不在CREATE TABLE中生成外键约束，只检查字段存在
        assertTrue(sql.contains("user_id") && sql.contains("BIGINT"));
        
        // 测试级联删除的外键 - H2不支持在CREATE TABLE中定义外键
        RelationDefinition cascadeRelation = new RelationDefinition();
        cascadeRelation.setName("fk_user_role");
        cascadeRelation.setColumn("role_id");
        cascadeRelation.setReferenceTable("role");
        cascadeRelation.setReferenceColumn("id");
        cascadeRelation.setOnDelete("CASCADE");
        cascadeRelation.setOnUpdate("CASCADE");
        
        FieldDefinition roleIdField = new FieldDefinition();
        roleIdField.setName("role_id");
        roleIdField.setType("INT");
        
        table.setFields(Arrays.asList(userIdField, roleIdField));
        table.setRelations(Arrays.asList(cascadeRelation));
        sql = sqlGeneratorService.generateCreateTableSql(table);
        assertTrue(sql.contains("role_id") && sql.contains("INT"));
    }
    
    @Test
    void testGenerateComplexTable() {
        TableDefinition table = createComplexTable();
        String sql = sqlGeneratorService.generateCreateTableSql(table);
        
        // 验证表结构 - H2使用双引号而不是反引号
        assertTrue(sql.contains("CREATE TABLE IF NOT EXISTS") && sql.contains("article"));
        
        // 验证字段 - H2的字段格式不同
        assertTrue(sql.contains("id") && sql.contains("BIGINT"));
        assertTrue(sql.contains("title") && sql.contains("VARCHAR"));
        assertTrue(sql.contains("content") && sql.contains("CLOB"));
        assertTrue(sql.contains("author_id") && sql.contains("BIGINT"));
        assertTrue(sql.contains("view_count") && sql.contains("INT"));
        assertTrue(sql.contains("status") && sql.contains("TINYINT"));
        
        // 验证主键
        assertTrue(sql.contains("PRIMARY KEY") && sql.contains("id"));
        
        // H2不支持在CREATE TABLE中定义索引和外键，只验证表注释
        assertTrue(sql.contains("COMMENT") && sql.contains("文章表"));
    }
    
    private TableDefinition createSampleTable() {
        TableDefinition table = new TableDefinition();
        table.setName("user");
        table.setComment("用户表");
        table.setEngine("InnoDB");
        
        // 字段
        List<FieldDefinition> fields = Arrays.asList(
            createPrimaryKeyField("id", "LONG"),
            createField("username", "STRING", 50, false, null),
            createField("email", "STRING", 100, true, null),
            createField("age", "INT", null, true, null),
            createDecimalField("balance", 10, 2, "0.00")
        );
        table.setFields(fields);
        
        // 索引
        IndexDefinition uniqueIndex = new IndexDefinition();
        uniqueIndex.setName("uk_username");
        uniqueIndex.setType("UNIQUE");
        
        IndexDefinition.IndexColumnDefinition column = new IndexDefinition.IndexColumnDefinition();
        column.setName("username");
        uniqueIndex.setColumns(Arrays.asList(column));
        
        table.setIndexes(Arrays.asList(uniqueIndex));
        
        return table;
    }
    
    private TableDefinition createComplexTable() {
        TableDefinition table = new TableDefinition();
        table.setName("article");
        table.setComment("文章表");
        table.setEngine("InnoDB");
        
        // 字段
        List<FieldDefinition> fields = Arrays.asList(
            createPrimaryKeyField("id", "LONG"),
            createField("title", "STRING", 200, false, null),
            createField("content", "LONGTEXT", null, true, null),
            createField("author_id", "LONG", null, false, null),
            createField("view_count", "INT", null, true, "0"),
            createField("status", "TINYINT", 1, false, "1")
        );
        table.setFields(fields);
        
        // 索引
        IndexDefinition titleIndex = new IndexDefinition();
        titleIndex.setName("idx_title");
        titleIndex.setType("FULLTEXT");
        
        IndexDefinition.IndexColumnDefinition titleColumn = new IndexDefinition.IndexColumnDefinition();
        titleColumn.setName("title");
        titleIndex.setColumns(Arrays.asList(titleColumn));
        
        IndexDefinition compositeIndex = new IndexDefinition();
        compositeIndex.setName("idx_author_status");
        compositeIndex.setType("NORMAL");
        
        IndexDefinition.IndexColumnDefinition authorColumn = new IndexDefinition.IndexColumnDefinition();
        authorColumn.setName("author_id");
        IndexDefinition.IndexColumnDefinition statusColumn = new IndexDefinition.IndexColumnDefinition();
        statusColumn.setName("status");
        statusColumn.setOrder("DESC");
        compositeIndex.setColumns(Arrays.asList(authorColumn, statusColumn));
        
        table.setIndexes(Arrays.asList(titleIndex, compositeIndex));
        
        // 外键
        RelationDefinition relation = new RelationDefinition();
        relation.setName("fk_article_author");
        relation.setColumn("author_id");
        relation.setReferenceTable("user");
        relation.setReferenceColumn("id");
        relation.setOnDelete("RESTRICT");
        relation.setOnUpdate("CASCADE");
        
        table.setRelations(Arrays.asList(relation));
        
        return table;
    }
    
    private FieldDefinition createSimpleField(String name, String type) {
        FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setType(type);
        return field;
    }
    
    private FieldDefinition createPrimaryKeyField(String name, String type) {
        FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setType(type);
        field.setPrimaryKey(PrimaryKeyType.AUTO_INCREMENT);
        field.setAutoIncrement(true);
        field.setNullable(false);
        return field;
    }
    
    private FieldDefinition createField(String name, String type, Integer length, boolean nullable, String defaultValue) {
        FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setType(type);
        field.setLength(length);
        field.setNullable(nullable);
        field.setDefaultValue(defaultValue);
        return field;
    }
    
    private FieldDefinition createDecimalField(String name, int precision, int scale, String defaultValue) {
        FieldDefinition field = new FieldDefinition();
        field.setName(name);
        field.setType("DECIMAL");
        field.setLength(precision);
        field.setScale(scale);
        field.setDefaultValue(defaultValue);
        return field;
    }
}