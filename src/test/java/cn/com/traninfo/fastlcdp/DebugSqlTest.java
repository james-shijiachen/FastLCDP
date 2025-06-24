package cn.com.traninfo.fastlcdp;

import cn.com.traninfo.fastlcdp.model.DatabaseSchema;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import cn.com.traninfo.fastlcdp.service.SqlGeneratorService;
import cn.com.traninfo.fastlcdp.service.XmlParserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.InputStream;

@SpringBootTest
@TestPropertySource(properties = {
    "database.type=mysql",
    "database.charset=utf8mb4",
    "database.collation=utf8mb4_unicode_ci"
})
public class DebugSqlTest {
    
    @Autowired
    private XmlParserService xmlParserService;
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Test
    public void debugSqlGeneration() throws Exception {
        // 从测试资源加载XML文件
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("test-schema.xml");
        
        // 解析XML
        DatabaseSchema schema = xmlParserService.parseFromStream(inputStream);
        
        // 查找users表
        TableDefinition usersTable = schema.getTables().stream()
            .filter(table -> "users".equals(table.getName()))
            .findFirst()
            .orElse(null);
        
        System.out.println("=== Users Table Debug Info ===");
        System.out.println("Table name: " + usersTable.getName());
        System.out.println("Number of indexes: " + usersTable.getIndexes().size());
        
        usersTable.getIndexes().forEach(index -> {
            System.out.println("Index: " + index.getName());
            System.out.println("  Type: " + index.getType());
            System.out.println("  Columns count: " + index.getColumns().size());
            index.getColumns().forEach(column -> {
                System.out.println("    Column: " + column.getName());
            });
        });
        
        // 生成创建表的SQL
        String createTableSql = sqlGeneratorService.generateCreateTableSql(usersTable, schema);
        System.out.println("\n=== Generated SQL ===");
        System.out.println(createTableSql);
        
        // 查找user_profiles表
        TableDefinition userProfilesTable = schema.getTables().stream()
            .filter(table -> "user_profiles".equals(table.getName()))
            .findFirst()
            .orElse(null);
        
        System.out.println("\n=== User Profiles Table Debug Info ===");
        System.out.println("Table name: " + userProfilesTable.getName());
        System.out.println("Number of relations: " + userProfilesTable.getRelations().size());
        
        userProfilesTable.getRelations().forEach(relation -> {
            System.out.println("Relation: " + relation.getName());
            System.out.println("  Column: " + relation.getColumn());
            System.out.println("  Reference table: " + relation.getReferenceTable());
            System.out.println("  Reference column: " + relation.getReferenceColumn());
        });
        
        // 生成创建表的SQL
        String createUserProfilesTableSql = sqlGeneratorService.generateCreateTableSql(userProfilesTable, schema);
        System.out.println("\n=== Generated User Profiles SQL ===");
        System.out.println(createUserProfilesTableSql);
    }
}