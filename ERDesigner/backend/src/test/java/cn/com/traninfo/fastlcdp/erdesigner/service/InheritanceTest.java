package cn.com.traninfo.fastlcdp.erdesigner.service;

import cn.com.traninfo.fastlcdp.erdesigner.config.DatabaseConfig;
import cn.com.traninfo.fastlcdp.erdesigner.model.DatabaseSchema;
import cn.com.traninfo.fastlcdp.erdesigner.model.TableDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试engine和charset继承功能
 */
@SpringBootTest(classes = cn.com.traninfo.fastlcdp.erdesigner.FastLcdpApplication.class)
@TestPropertySource(properties = {
    "database.type=MYSQL",
    "database.url=jdbc:h2:mem:testdb",
    "database.username=sa",
    "database.password=",
    "database.charset=utf8mb4",
    "database.collation=utf8mb4_unicode_ci"
})
public class InheritanceTest {
    
    @Autowired
    private XmlParserService xmlParserService;
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Autowired
    private DatabaseConfig databaseConfig;
    
    @BeforeEach
    void setUp() {
        // 设置数据库类型为MySQL以测试继承功能
        databaseConfig.setType(DatabaseConfig.DatabaseType.MYSQL);
    }
    
    @Test
    void testEngineAndCharsetInheritance() throws Exception {
        // 加载测试XML文件
        File xmlFile = new File("src/test/resources/test-inheritance.xml");
        assertTrue(xmlFile.exists(), "测试XML文件应该存在");
        
        // 解析XML
        DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
        assertNotNull(schema, "数据库模式不应为null");
        assertEquals("test_inheritance_db", schema.getName());
        assertEquals("utf8mb4", schema.getCharset());
        assertEquals("MyISAM", schema.getEngine());
        
        // 验证表定义
        assertEquals(3, schema.getTables().size(), "应该有3个表");
        
        // 测试继承表（inherit_table）
        TableDefinition inheritTable = schema.getTables().stream()
            .filter(t -> "inherit_table".equals(t.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(inheritTable, "继承表应该存在");
        assertNull(inheritTable.getEngine(), "继承表的engine应该为null（未定义）");
        assertNull(inheritTable.getCharset(), "继承表的charset应该为null（未定义）");
        
        // 测试自定义表（custom_table）
        TableDefinition customTable = schema.getTables().stream()
            .filter(t -> "custom_table".equals(t.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(customTable, "自定义表应该存在");
        assertEquals("InnoDB", customTable.getEngine(), "自定义表应该使用自己定义的engine");
        assertEquals("latin1", customTable.getCharset(), "自定义表应该使用自己定义的charset");
        
        // 测试部分自定义表（partial_table）
        TableDefinition partialTable = schema.getTables().stream()
            .filter(t -> "partial_table".equals(t.getName()))
            .findFirst()
            .orElse(null);
        assertNotNull(partialTable, "部分自定义表应该存在");
        assertEquals("InnoDB", partialTable.getEngine(), "部分自定义表应该使用自己定义的engine");
        assertNull(partialTable.getCharset(), "部分自定义表的charset应该为null（未定义）");
    }
    
    @Test
    void testSqlGenerationWithInheritance() throws Exception {
        // 加载测试XML文件
        File xmlFile = new File("src/test/resources/test-inheritance.xml");
        DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
        
        // 生成继承表的SQL
        TableDefinition inheritTable = schema.getTables().stream()
            .filter(t -> "inherit_table".equals(t.getName()))
            .findFirst()
            .orElse(null);
        
        String inheritTableSql = sqlGeneratorService.generateCreateTableSql(inheritTable, schema);
        System.out.println("继承表SQL:");
        System.out.println(inheritTableSql);
        
        // 验证继承表使用了数据库级别的配置
        assertTrue(inheritTableSql.contains("ENGINE=MyISAM"), "继承表应该使用数据库级别的engine（MyISAM）");
        assertTrue(inheritTableSql.contains("DEFAULT CHARSET=utf8mb4"), "继承表应该使用数据库级别的charset（utf8mb4）");
        
        // 生成自定义表的SQL
        TableDefinition customTable = schema.getTables().stream()
            .filter(t -> "custom_table".equals(t.getName()))
            .findFirst()
            .orElse(null);
        
        String customTableSql = sqlGeneratorService.generateCreateTableSql(customTable, schema);
        System.out.println("\n自定义表SQL:");
        System.out.println(customTableSql);
        
        // 验证自定义表使用了表级别的配置
        assertTrue(customTableSql.contains("ENGINE=InnoDB"), "自定义表应该使用表级别的engine（InnoDB）");
        assertTrue(customTableSql.contains("DEFAULT CHARSET=latin1"), "自定义表应该使用表级别的charset（latin1）");
        
        // 生成部分自定义表的SQL
        TableDefinition partialTable = schema.getTables().stream()
            .filter(t -> "partial_table".equals(t.getName()))
            .findFirst()
            .orElse(null);
        
        String partialTableSql = sqlGeneratorService.generateCreateTableSql(partialTable, schema);
        System.out.println("\n部分自定义表SQL:");
        System.out.println(partialTableSql);
        
        // 验证部分自定义表的配置
        assertTrue(partialTableSql.contains("ENGINE=InnoDB"), "部分自定义表应该使用表级别的engine（InnoDB）");
        assertTrue(partialTableSql.contains("DEFAULT CHARSET=utf8mb4"), "部分自定义表应该继承数据库级别的charset（utf8mb4）");
    }
    
    @Test
    void testFullSchemaSqlGeneration() throws Exception {
        // 加载测试XML文件
        File xmlFile = new File("src/test/resources/test-inheritance.xml");
        DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
        
        // 生成完整的模式SQL
        String fullSql = sqlGeneratorService.generateFullSchemaSql(schema);
        System.out.println("\n完整模式SQL:");
        System.out.println(fullSql);
        
        // 验证SQL包含正确的继承配置
        assertTrue(fullSql.contains("ENGINE=MyISAM"), "应该包含继承的MyISAM引擎");
        assertTrue(fullSql.contains("ENGINE=InnoDB"), "应该包含自定义的InnoDB引擎");
        assertTrue(fullSql.contains("DEFAULT CHARSET=utf8mb4"), "应该包含继承的utf8mb4字符集");
        assertTrue(fullSql.contains("DEFAULT CHARSET=latin1"), "应该包含自定义的latin1字符集");
    }
}