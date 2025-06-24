package cn.com.traninfo.fastlcdp;

import cn.com.traninfo.fastlcdp.model.DatabaseSchema;
import cn.com.traninfo.fastlcdp.model.IndexDefinition;
import cn.com.traninfo.fastlcdp.model.RelationDefinition;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import cn.com.traninfo.fastlcdp.service.XmlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SqlFixTest {
    
    @Autowired
    private XmlParserService xmlParserService;
    
    @Test
    public void testXmlParsingWithIndexesAndRelations() throws Exception {
        // 从测试资源加载XML文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test-schema.xml");
        assertNotNull(inputStream, "test-schema.xml should exist in test resources");
        
        // 解析XML
        DatabaseSchema schema = xmlParserService.parseFromStream(inputStream);
        assertNotNull(schema);
        
        // 验证表解析
        assertFalse(schema.getTables().isEmpty(), "Should have tables");
        
        // 查找users表
        TableDefinition usersTable = schema.getTables().stream()
            .filter(table -> "users".equals(table.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(usersTable, "Should have users table");
        
        // 验证索引解析
        assertFalse(usersTable.getIndexes().isEmpty(), "Users table should have indexes");
        
        IndexDefinition usernameIndex = usersTable.getIndexes().stream()
            .filter(index -> "idx_username".equals(index.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(usernameIndex, "Should have idx_username index");
        assertFalse(usernameIndex.getColumns().isEmpty(), "Index should have columns");
        assertEquals("username", usernameIndex.getColumns().get(0).getName(), "Index column should be username");
        
        // 查找user_profiles表
        TableDefinition userProfilesTable = schema.getTables().stream()
            .filter(table -> "user_profiles".equals(table.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(userProfilesTable, "Should have user_profiles table");
        
        // 验证外键关系解析
        assertFalse(userProfilesTable.getRelations().isEmpty(), "User_profiles table should have relations");
        
        RelationDefinition userIdRelation = userProfilesTable.getRelations().stream()
            .filter(relation -> "fk_user_profiles_user_id".equals(relation.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(userIdRelation, "Should have fk_user_profiles_user_id relation");
        assertEquals("user_id", userIdRelation.getColumn(), "Relation column should be user_id");
        assertEquals("users", userIdRelation.getReferenceTable(), "Reference table should be users");
        assertEquals("id", userIdRelation.getReferenceColumn(), "Reference column should be id");
        
        System.out.println("XML parsing test passed!");
        System.out.println("Users table indexes: " + usersTable.getIndexes().size());
        System.out.println("User_profiles table relations: " + userProfilesTable.getRelations().size());
    }
}