package cn.com.traninfo.fastlcdp;

import cn.com.traninfo.fastlcdp.config.DatabaseConfig;
import cn.com.traninfo.fastlcdp.model.MetadataEntity;
import cn.com.traninfo.fastlcdp.service.MetadataService;
import cn.com.traninfo.fastlcdp.service.SqlGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集成测试类
 * 测试多数据库支持和元数据管理功能
 */
@SpringBootTest(classes = cn.com.traninfo.fastlcdp.FastLcdpApplication.class)
@ActiveProfiles("test")
public class IntegrationTest {
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Autowired
    private MetadataService metadataService;
    
    @Autowired
    private DatabaseConfig databaseConfig;
    
    private File getTestXmlFile() {
        return new File("src/test/resources/test-schema.xml");
    }
    
    @Test
    public void testDatabaseConfig() {
        assertNotNull(databaseConfig);
        assertNotNull(databaseConfig.getType());
        System.out.println("当前数据库类型: " + databaseConfig.getType());
        System.out.println("元数据存储启用: " + databaseConfig.isMetadataStorageEnabled());
    }
    
    @Test
    public void testGenerateSqlForH2() {
        File xmlFile = getTestXmlFile();
        assertTrue(xmlFile.exists(), "测试XML文件不存在");
        
        String sql = sqlGeneratorService.generateSqlForDatabase(xmlFile, "H2");
        assertNotNull(sql);
        assertFalse(sql.isEmpty());
        
        System.out.println("=== H2 SQL ===");
        System.out.println(sql);
        
        // 验证H2特有的语法
        assertTrue(sql.contains("IF NOT EXISTS"));
        assertTrue(sql.contains("AUTO_INCREMENT"));
    }
    
    @Test
    public void testGenerateSqlForMySQL() {
        File xmlFile = getTestXmlFile();
        assertTrue(xmlFile.exists(), "测试XML文件不存在");
        
        String sql = sqlGeneratorService.generateSqlForDatabase(xmlFile, "MYSQL");
        assertNotNull(sql);
        assertFalse(sql.isEmpty());
        
        System.out.println("=== MySQL SQL ===");
        System.out.println(sql);
        
        // 验证MySQL特有的语法
        assertTrue(sql.contains("AUTO_INCREMENT"));
        assertTrue(sql.contains("ENGINE=InnoDB"));
        assertTrue(sql.contains("CHARSET=utf8mb4"));
    }
    
    @Test
    public void testGenerateSqlForPostgreSQL() {
        File xmlFile = getTestXmlFile();
        assertTrue(xmlFile.exists(), "测试XML文件不存在");
        
        String sql = sqlGeneratorService.generateSqlForDatabase(xmlFile, "POSTGRESQL");
        assertNotNull(sql);
        assertFalse(sql.isEmpty());
        
        System.out.println("=== PostgreSQL SQL ===");
        System.out.println(sql);
        
        // 验证PostgreSQL特有的语法
        assertTrue(sql.contains("SERIAL") || sql.contains("BIGSERIAL"));
        assertTrue(sql.contains("IF NOT EXISTS"));
    }
    
    @Test
    public void testGenerateSqlForOracle() {
        File xmlFile = getTestXmlFile();
        assertTrue(xmlFile.exists(), "测试XML文件不存在");
        
        String sql = sqlGeneratorService.generateSqlForDatabase(xmlFile, "ORACLE");
        assertNotNull(sql);
        assertFalse(sql.isEmpty());
        
        System.out.println("=== Oracle SQL ===");
        System.out.println(sql);
        
        // 验证Oracle特有的语法
        assertTrue(sql.contains("NUMBER"));
        assertTrue(sql.contains("VARCHAR2"));
    }
    
    @Test
    public void testGenerateSqlForSqlServer() {
        File xmlFile = getTestXmlFile();
        assertTrue(xmlFile.exists(), "测试XML文件不存在");
        
        String sql = sqlGeneratorService.generateSqlForDatabase(xmlFile, "SQLSERVER");
        assertNotNull(sql);
        assertFalse(sql.isEmpty());
        
        System.out.println("=== SQL Server SQL ===");
        System.out.println(sql);
        
        // 验证SQL Server特有的语法
        assertTrue(sql.contains("IDENTITY"));
        assertTrue(sql.contains("NVARCHAR"));
    }
    
    @Test
    public void testMetadataService() {
        // 测试元数据表DDL生成
        String ddl = metadataService.generateMetadataTableDDL();
        assertNotNull(ddl);
        assertFalse(ddl.isEmpty());
        
        System.out.println("=== 元数据表DDL ===");
        System.out.println(ddl);
        
        // 如果启用了元数据存储，测试保存和查询功能
        if (databaseConfig.isMetadataStorageEnabled()) {
            try {
                File xmlFile = getTestXmlFile();
                
                // 保存元数据
                metadataService.saveSchemaFromXml(xmlFile);
                
                // 查询元数据
                List<MetadataEntity> metadataList = metadataService.findBySchemaName("test_db");
                assertNotNull(metadataList);
                assertFalse(metadataList.isEmpty());
                
                System.out.println("=== 保存的元数据 ===");
                for (MetadataEntity metadata : metadataList) {
                    System.out.println("Schema: " + metadata.getSchemaName() + 
                                     ", Table: " + metadata.getTableName() + 
                                     ", Field: " + metadata.getFieldName() + 
                                     ", Type: " + metadata.getFieldType());
                }
                
                // 清理测试数据
                metadataService.deleteBySchemaName("test_db");
                
            } catch (Exception e) {
                System.out.println("元数据存储测试跳过（可能是数据库未配置）: " + e.getMessage());
            }
        } else {
            System.out.println("元数据存储功能未启用，跳过相关测试");
        }
    }
    
    @Test
    public void testAllDatabaseTypes() {
        File xmlFile = getTestXmlFile();
        assertTrue(xmlFile.exists(), "测试XML文件不存在");
        
        String[] databaseTypes = {"H2", "MYSQL", "POSTGRESQL", "ORACLE", "SQLSERVER"};
        
        for (String dbType : databaseTypes) {
            try {
                String sql = sqlGeneratorService.generateSqlForDatabase(xmlFile, dbType);
                assertNotNull(sql, "生成的SQL不能为空: " + dbType);
                assertFalse(sql.isEmpty(), "生成的SQL不能为空字符串: " + dbType);
                
                System.out.println("✓ " + dbType + " SQL生成成功");
                
            } catch (Exception e) {
                fail("生成 " + dbType + " SQL失败: " + e.getMessage());
            }
        }
        
        System.out.println("所有数据库类型SQL生成测试通过！");
    }
}