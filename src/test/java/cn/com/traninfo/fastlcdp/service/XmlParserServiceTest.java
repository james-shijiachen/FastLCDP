package cn.com.traninfo.fastlcdp.service;

import cn.com.traninfo.fastlcdp.model.DatabaseSchema;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import cn.com.traninfo.fastlcdp.service.XmlParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XML解析服务测试类
 */
@SpringBootTest(classes = cn.com.traninfo.fastlcdp.FastLcdpApplication.class)
class XmlParserServiceTest {
    
    @Autowired
    private XmlParserService xmlParserService;
    
    @BeforeEach
    void setUp() {
        // 移除手动创建实例的代码，使用Spring注入的实例
    }
    
    @Test
    void testParseSimpleXml() throws JAXBException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <database name="test_db" version="1.0" comment="测试数据库">
                <tables>
                    <table name="user" comment="用户表">
                        <fields>
                            <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="用户ID"/>
                            <field name="username" type="STRING" length="50" nullable="false" comment="用户名"/>
                            <field name="email" type="STRING" length="100" comment="邮箱"/>
                        </fields>
                        <indexes>
                            <index name="uk_username" type="UNIQUE">
                                <columns>
                                    <column name="username"/>
                                </columns>
                            </index>
                        </indexes>
                    </table>
                </tables>
            </database>
            """;
        
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
        DatabaseSchema schema = xmlParserService.parseFromStream(inputStream);
        
        // 验证数据库信息
        assertNotNull(schema);
        assertEquals("test_db", schema.getName());
        assertEquals("1.0", schema.getVersion());
        assertEquals("测试数据库", schema.getComment());
        
        // 验证表信息
        assertEquals(1, schema.getTables().size());
        TableDefinition table = schema.getTables().get(0);
        assertEquals("user", table.getName());
        assertEquals("用户表", table.getComment());
        
        // 验证字段信息
        assertEquals(3, table.getFields().size());
        assertEquals("id", table.getFields().get(0).getName());
        assertEquals("LONG", table.getFields().get(0).getType());
        assertTrue(table.getFields().get(0).getPrimaryKey());
        assertTrue(table.getFields().get(0).getAutoIncrement());
        
        assertEquals("username", table.getFields().get(1).getName());
        assertEquals("STRING", table.getFields().get(1).getType());
        assertEquals(50, table.getFields().get(1).getLength());
        assertFalse(table.getFields().get(1).getNullable());
        
        // 验证索引信息
        assertEquals(1, table.getIndexes().size());
        assertEquals("uk_username", table.getIndexes().get(0).getName());
        assertEquals("UNIQUE", table.getIndexes().get(0).getType());
        assertEquals(1, table.getIndexes().get(0).getColumns().size());
        assertEquals("username", table.getIndexes().get(0).getColumns().get(0).getName());
    }
    
    @Test
    void testParseTableInheritance() throws JAXBException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <database name="test_db">
                <tables>
                    <table name="base_entity" comment="基础实体表">
                        <fields>
                            <field name="id" type="LONG" primaryKey="true" autoIncrement="true" comment="主键ID"/>
                            <field name="created_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP" comment="创建时间"/>
                            <field name="updated_time" type="DATETIME" defaultValue="CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" comment="更新时间"/>
                        </fields>
                    </table>
                    <table name="user" extends="base_entity" comment="用户表">
                        <fields>
                            <field name="username" type="STRING" length="50" nullable="false" comment="用户名"/>
                            <field name="email" type="STRING" length="100" comment="邮箱"/>
                        </fields>
                        <indexes>
                            <index name="uk_username" type="UNIQUE">
                                <columns>
                                    <column name="username"/>
                                </columns>
                            </index>
                        </indexes>
                    </table>
                </tables>
            </database>
            """;
        
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
        DatabaseSchema schema = xmlParserService.parseFromStream(inputStream);
        
        // 验证继承关系处理
        TableDefinition userTable = schema.getTables().stream()
                .filter(table -> "user".equals(table.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(userTable);
        assertEquals("base_entity", userTable.getExtendsTable());
        
        // 验证字段合并（应该包含父表的3个字段 + 子表的2个字段）
        assertEquals(5, userTable.getFields().size());
        
        // 验证父表字段在前
        assertEquals("id", userTable.getFields().get(0).getName());
        assertEquals("created_time", userTable.getFields().get(1).getName());
        assertEquals("updated_time", userTable.getFields().get(2).getName());
        
        // 验证子表字段在后
        assertEquals("username", userTable.getFields().get(3).getName());
        assertEquals("email", userTable.getFields().get(4).getName());
    }
    
    @Test
    void testParseWithRelations() throws JAXBException {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <database name="test_db">
                <tables>
                    <table name="user" comment="用户表">
                        <fields>
                            <field name="id" type="LONG" primaryKey="true" autoIncrement="true"/>
                            <field name="username" type="STRING" length="50" nullable="false"/>
                        </fields>
                    </table>
                    <table name="order" comment="订单表">
                        <fields>
                            <field name="id" type="LONG" primaryKey="true" autoIncrement="true"/>
                            <field name="user_id" type="LONG" nullable="false"/>
                            <field name="total_amount" type="DECIMAL" length="10" scale="2"/>
                        </fields>
                        <relations>
                            <relation name="fk_order_user" column="user_id" referenceTable="user" referenceColumn="id" onDelete="CASCADE" onUpdate="CASCADE"/>
                        </relations>
                    </table>
                </tables>
            </database>
            """;
        
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
        DatabaseSchema schema = xmlParserService.parseFromStream(inputStream);
        
        // 验证关联关系
        TableDefinition orderTable = schema.getTables().stream()
                .filter(table -> "order".equals(table.getName()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(orderTable);
        assertEquals(1, orderTable.getRelations().size());
        
        var relation = orderTable.getRelations().get(0);
        assertEquals("fk_order_user", relation.getName());
        assertEquals("user_id", relation.getColumn());
        assertEquals("user", relation.getReferenceTable());
        assertEquals("id", relation.getReferenceColumn());
        assertEquals("CASCADE", relation.getOnDelete());
        assertEquals("CASCADE", relation.getOnUpdate());
    }
    
    @Test
    void testParseInvalidXml() {
        String invalidXmlContent = "<invalid>xml</content>";
        
        InputStream inputStream = new ByteArrayInputStream(invalidXmlContent.getBytes(StandardCharsets.UTF_8));
        
        assertThrows(JAXBException.class, () -> {
            xmlParserService.parseFromStream(inputStream);
        });
    }
}