package cn.com.traninfo.fastlcdp;

import cn.com.traninfo.fastlcdp.config.DatabaseConfig;
import cn.com.traninfo.fastlcdp.dialect.MySQLDialect;
import cn.com.traninfo.fastlcdp.model.DatabaseSchema;
import cn.com.traninfo.fastlcdp.model.IndexDefinition;
import cn.com.traninfo.fastlcdp.model.RelationDefinition;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import cn.com.traninfo.fastlcdp.service.SqlGeneratorService;
import cn.com.traninfo.fastlcdp.service.XmlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "database.type=mysql",
    "database.charset=utf8mb4",
    "database.collation=utf8mb4_unicode_ci"
})
public class SqlGenerationTest {
    
    @Autowired
    private XmlParserService xmlParserService;
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Test
    public void testSqlGenerationWithFixedIndexesAndRelations() throws Exception {
        // 从测试资源加载XML文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test-schema.xml");
        assertNotNull(inputStream, "test-schema.xml should exist in test resources");
        
        // 解析XML
        DatabaseSchema schema = xmlParserService.parseFromStream(inputStream);
        assertNotNull(schema);
        
        // 查找users表
        TableDefinition usersTable = schema.getTables().stream()
            .filter(table -> "users".equals(table.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(usersTable, "Should have users table");
        
        // 生成创建表的SQL
        String createTableSql = sqlGeneratorService.generateCreateTableSql(usersTable, schema);
        assertNotNull(createTableSql);
        
        System.out.println("Generated CREATE TABLE SQL for users:");
        System.out.println(createTableSql);
        
        // 验证索引SQL不包含空括号
        assertFalse(createTableSql.contains("KEY `idx_username` ()"), "Index should not have empty columns");
        assertTrue(createTableSql.contains("KEY `idx_username` (`username` ASC)"), "Index should have proper column definition");
        
        // 查找user_profiles表
        TableDefinition userProfilesTable = schema.getTables().stream()
            .filter(table -> "user_profiles".equals(table.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(userProfilesTable, "Should have user_profiles table");
        
        // 生成创建表的SQL
        String createUserProfilesTableSql = sqlGeneratorService.generateCreateTableSql(userProfilesTable, schema);
        assertNotNull(createUserProfilesTableSql);
        
        System.out.println("\nGenerated CREATE TABLE SQL for user_profiles:");
        System.out.println(createUserProfilesTableSql);
        
        // 验证外键SQL不包含null引用
        assertFalse(createUserProfilesTableSql.contains("REFERENCES null(null)"), "Foreign key should not reference null");
        assertTrue(createUserProfilesTableSql.contains("REFERENCES `users`(`id`)"), "Foreign key should reference proper table and column");
        
        System.out.println("\nSQL generation test passed! Indexes and foreign keys are properly generated.");
    }
}