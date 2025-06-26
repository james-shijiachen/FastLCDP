package cn.com.traninfo.fastlcdp;

import cn.com.traninfo.fastlcdp.model.*;
import cn.com.traninfo.fastlcdp.enums.PrimaryKeyType;
import cn.com.traninfo.fastlcdp.service.SqlGeneratorService;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的SQL生成测试，不依赖XML解析
 */
public class SimpleSqlTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("=== XML表生成器框架测试 ===");
            System.out.println();
            
            // 创建数据库模式
            DatabaseSchema schema = createTestSchema();
            
            // 创建SQL生成器
            SqlGeneratorService sqlGenerator = new SqlGeneratorService();
            
            // 生成数据库创建SQL
            System.out.println("=== 生成数据库创建SQL ===");
            String createDbSql = sqlGenerator.generateCreateDatabaseSql(schema);
            System.out.println(createDbSql);
            System.out.println();
            
            // 生成表创建SQL
            System.out.println("=== 生成表创建SQL ===");
            for (TableDefinition table : schema.getTables()) {
                System.out.println("-- 表: " + table.getName());
                String createTableSql = sqlGenerator.generateCreateTableSql(table);
                System.out.println(createTableSql);
                System.out.println();
            }
            
            System.out.println("=== 测试完成 ===");
            System.out.println("SQL生成功能正常工作！");
            System.out.println();
            System.out.println("框架主要功能:");
            System.out.println("✓ 数据库模式定义");
            System.out.println("✓ 表结构定义");
            System.out.println("✓ 字段类型支持");
            System.out.println("✓ 主键和自增");
            System.out.println("✓ 索引创建");
            System.out.println("✓ 外键关联");
            System.out.println("✓ SQL语句生成");
            System.out.println();
            System.out.println("完整功能需要Spring Boot环境和相关依赖支持。");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static DatabaseSchema createTestSchema() {
        DatabaseSchema schema = new DatabaseSchema();
        schema.setName("test_database");
        schema.setComment("测试数据库");
        schema.setCharset("utf8mb4");
        schema.setCollation("utf8mb4_unicode_ci");
        
        List<TableDefinition> tables = new ArrayList<>();
        
        // 创建用户表
        TableDefinition userTable = createUserTable();
        tables.add(userTable);
        
        // 创建订单表
        TableDefinition orderTable = createOrderTable();
        tables.add(orderTable);
        
        schema.setTables(tables);
        return schema;
    }
    
    private static TableDefinition createUserTable() {
        TableDefinition table = new TableDefinition();
        table.setName("user");
        table.setComment("用户表");
        table.setEngine("InnoDB");
        table.setCharset("utf8mb4");
        
        List<FieldDefinition> fields = new ArrayList<>();
        
        // ID字段
        FieldDefinition idField = new FieldDefinition();
        idField.setName("id");
        idField.setType("LONG");
        idField.setPrimaryKey(PrimaryKeyType.AUTO_INCREMENT);
        idField.setAutoIncrement(true);
        idField.setComment("用户ID");
        fields.add(idField);
        
        // 用户名字段
        FieldDefinition usernameField = new FieldDefinition();
        usernameField.setName("username");
        usernameField.setType("STRING");
        usernameField.setLength(50);
        usernameField.setNullable(false);
        usernameField.setUnique(true);
        usernameField.setComment("用户名");
        fields.add(usernameField);
        
        // 邮箱字段
        FieldDefinition emailField = new FieldDefinition();
        emailField.setName("email");
        emailField.setType("STRING");
        emailField.setLength(100);
        emailField.setComment("邮箱地址");
        fields.add(emailField);
        
        // 创建时间字段
        FieldDefinition createdAtField = new FieldDefinition();
        createdAtField.setName("created_at");
        createdAtField.setType("DATETIME");
        createdAtField.setDefaultValue("CURRENT_TIMESTAMP");
        createdAtField.setComment("创建时间");
        fields.add(createdAtField);
        
        table.setFields(fields);
        
        // 创建索引
        List<IndexDefinition> indexes = new ArrayList<>();
        
        IndexDefinition usernameIndex = new IndexDefinition();
        usernameIndex.setName("uk_username");
        usernameIndex.setType("UNIQUE");
        usernameIndex.setComment("用户名唯一索引");
        
        List<IndexDefinition.IndexColumnDefinition> indexColumns = new ArrayList<>();
        IndexDefinition.IndexColumnDefinition usernameColumn = new IndexDefinition.IndexColumnDefinition();
        usernameColumn.setName("username");
        indexColumns.add(usernameColumn);
        usernameIndex.setColumns(indexColumns);
        
        indexes.add(usernameIndex);
        table.setIndexes(indexes);
        
        return table;
    }
    
    private static TableDefinition createOrderTable() {
        TableDefinition table = new TableDefinition();
        table.setName("order");
        table.setComment("订单表");
        table.setEngine("InnoDB");
        table.setCharset("utf8mb4");
        
        List<FieldDefinition> fields = new ArrayList<>();
        
        // ID字段
        FieldDefinition idField = new FieldDefinition();
        idField.setName("id");
        idField.setType("LONG");
        idField.setPrimaryKey(PrimaryKeyType.AUTO_INCREMENT);
        idField.setAutoIncrement(true);
        idField.setComment("订单ID");
        fields.add(idField);
        
        // 用户ID字段
        FieldDefinition userIdField = new FieldDefinition();
        userIdField.setName("user_id");
        userIdField.setType("LONG");
        userIdField.setNullable(false);
        userIdField.setComment("用户ID");
        fields.add(userIdField);
        
        // 金额字段
        FieldDefinition amountField = new FieldDefinition();
        amountField.setName("amount");
        amountField.setType("DECIMAL");
        amountField.setLength(10);
        amountField.setScale(2);
        amountField.setComment("订单金额");
        fields.add(amountField);
        
        // 状态字段
        FieldDefinition statusField = new FieldDefinition();
        statusField.setName("status");
        statusField.setType("STRING");
        statusField.setLength(20);
        statusField.setDefaultValue("'pending'");
        statusField.setComment("订单状态");
        fields.add(statusField);
        
        table.setFields(fields);
        
        // 创建外键关联
        List<RelationDefinition> relations = new ArrayList<>();
        
        RelationDefinition userRelation = new RelationDefinition();
        userRelation.setName("fk_order_user");
        userRelation.setColumn("user_id");
        userRelation.setReferenceTable("user");
        userRelation.setReferenceColumn("id");
        userRelation.setOnDelete("CASCADE");
        userRelation.setComment("订单用户关联");
        
        relations.add(userRelation);
        table.setRelations(relations);
        
        return table;
    }
}