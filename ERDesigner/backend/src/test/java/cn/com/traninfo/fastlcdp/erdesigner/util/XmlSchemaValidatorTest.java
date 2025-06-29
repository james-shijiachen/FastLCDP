package cn.com.traninfo.fastlcdp.erdesigner.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * XML Schema 校验工具测试类
 * 
 * @author FastLCDP
 * @since 1.0.0
 */
class XmlSchemaValidatorTest {

    private XmlSchemaValidator validator;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException, SAXException {
        validator = new XmlSchemaValidator("/database-schema.xsd");
    }
    
    @Test
    void testValidXmlFile() {
        // 测试有效的XML文件
        File xmlFile = new File("data/simple_db.xml");
        if (xmlFile.exists()) {
            XmlValidationResult result = validator.validate(xmlFile);
            assertTrue(result.isValid(), "Valid XML file should pass validation");
            assertNull(result.getErrorMessage(), "Valid XML should have no error message");
        }
    }
    
    @Test
    void testValidInheritanceXmlFile() {
        // 测试继承功能的XML文件
        File xmlFile = new File("src/test/resources/test-inheritance.xml");
        if (xmlFile.exists()) {
            XmlValidationResult result = validator.validate(xmlFile);
            assertTrue(result.isValid(), "Valid inheritance XML file should pass validation");
            assertNull(result.getErrorMessage(), "Valid XML should have no error message");
        }
    }
    
    @Test
    void testInvalidXmlFile() throws IOException {
        // 创建一个无效的XML文件
        File invalidXml = tempDir.resolve("invalid.xml").toFile();
        try (FileWriter writer = new FileWriter(invalidXml)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<database>\n");
            writer.write("    <!-- 缺少必需的name属性 -->\n");
            writer.write("    <tables>\n");
            writer.write("        <table name=\"test_table\">\n");
            writer.write("            <fields>\n");
            writer.write("                <!-- 缺少必需的type属性 -->\n");
            writer.write("                <field name=\"id\"/>\n");
            writer.write("            </fields>\n");
            writer.write("        </table>\n");
            writer.write("    </tables>\n");
            writer.write("</database>\n");
        }
        
        XmlValidationResult result = validator.validate(invalidXml);
        assertFalse(result.isValid(), "Invalid XML file should fail validation");
        assertNotNull(result.getErrorMessage(), "Invalid XML should have error message");
        assertTrue(result.getErrorMessage().contains("name") || result.getErrorMessage().contains("type"), 
                   "Error message should mention missing required attributes");
    }
    
    @Test
    void testInvalidDataType() throws IOException {
        // 创建一个包含无效数据类型的XML文件
        File invalidXml = tempDir.resolve("invalid-datatype.xml").toFile();
        try (FileWriter writer = new FileWriter(invalidXml)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<database name=\"test_db\">\n");
            writer.write("    <tables>\n");
            writer.write("        <table name=\"test_table\">\n");
            writer.write("            <fields>\n");
            writer.write("                <!-- 无效的数据类型 -->\n");
            writer.write("                <field name=\"id\" type=\"INVALID_TYPE\"/>\n");
            writer.write("            </fields>\n");
            writer.write("        </table>\n");
            writer.write("    </tables>\n");
            writer.write("</database>\n");
        }
        
        XmlValidationResult result = validator.validate(invalidXml);
        assertFalse(result.isValid(), "XML with invalid data type should fail validation");
        assertNotNull(result.getErrorMessage(), "Invalid XML should have error message");
        assertTrue(result.getErrorMessage().contains("INVALID_TYPE") || result.getErrorMessage().contains("enumeration"), 
                   "Error message should mention invalid data type");
    }
    
    @Test
    void testInvalidIndexType() throws IOException {
        // 创建一个包含无效索引类型的XML文件
        File invalidXml = tempDir.resolve("invalid-indextype.xml").toFile();
        try (FileWriter writer = new FileWriter(invalidXml)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<database name=\"test_db\">\n");
            writer.write("    <tables>\n");
            writer.write("        <table name=\"test_table\">\n");
            writer.write("            <fields>\n");
            writer.write("                <field name=\"id\" type=\"LONG\"/>\n");
            writer.write("            </fields>\n");
            writer.write("            <indexes>\n");
            writer.write("                <!-- 无效的索引类型 -->\n");
            writer.write("                <index name=\"idx_id\" type=\"INVALID_INDEX_TYPE\">\n");
            writer.write("                    <columns>\n");
            writer.write("                        <column name=\"id\"/>\n");
            writer.write("                    </columns>\n");
            writer.write("                </index>\n");
            writer.write("            </indexes>\n");
            writer.write("        </table>\n");
            writer.write("    </tables>\n");
            writer.write("</database>\n");
        }
        
        XmlValidationResult result = validator.validate(invalidXml);
        assertFalse(result.isValid(), "XML with invalid index type should fail validation");
        assertNotNull(result.getErrorMessage(), "Invalid XML should have error message");
    }
    
    @Test
    void testValidCompleteXml() throws IOException {
        // 创建一个完整的有效XML文件
        File validXml = tempDir.resolve("valid-complete.xml").toFile();
        try (FileWriter writer = new FileWriter(validXml)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<database name=\"test_db\" version=\"1.0\" charset=\"utf8mb4\" collation=\"utf8mb4_general_ci\" engine=\"InnoDB\" comment=\"测试数据库\">\n");
            writer.write("    <tables>\n");
            writer.write("        <table name=\"users\" comment=\"用户表\" engine=\"InnoDB\" charset=\"utf8mb4\">\n");
            writer.write("            <fields>\n");
            writer.write("                <field name=\"id\" type=\"LONG\" primaryKey=\"AUTO_INCREMENT\" comment=\"主键ID\"/>\n");
            writer.write("                <field name=\"username\" type=\"STRING\" length=\"50\" nullable=\"false\" unique=\"true\" comment=\"用户名\"/>\n");
            writer.write("                <field name=\"email\" type=\"STRING\" length=\"100\" nullable=\"false\" comment=\"邮箱\"/>\n");
            writer.write("                <field name=\"created_time\" type=\"DATETIME\" nullable=\"false\" defaultValue=\"CURRENT_TIMESTAMP\" comment=\"创建时间\"/>\n");
            writer.write("            </fields>\n");
            writer.write("            <indexes>\n");
            writer.write("                <index name=\"uk_username\" type=\"UNIQUE\">\n");
            writer.write("                    <columns>\n");
            writer.write("                        <column name=\"username\"/>\n");
            writer.write("                    </columns>\n");
            writer.write("                </index>\n");
            writer.write("                <index name=\"idx_email\" type=\"NORMAL\">\n");
            writer.write("                    <columns>\n");
            writer.write("                        <column name=\"email\" order=\"ASC\"/>\n");
            writer.write("                    </columns>\n");
            writer.write("                </index>\n");
            writer.write("            </indexes>\n");
            writer.write("        </table>\n");
            writer.write("        <table name=\"orders\" comment=\"订单表\">\n");
            writer.write("            <fields>\n");
            writer.write("                <field name=\"id\" type=\"LONG\" primaryKey=\"AUTO_INCREMENT\" comment=\"主键ID\"/>\n");
            writer.write("                <field name=\"user_id\" type=\"LONG\" nullable=\"false\" comment=\"用户ID\"/>\n");
            writer.write("                <field name=\"amount\" type=\"DECIMAL\" precision=\"10\" scale=\"2\" nullable=\"false\" comment=\"金额\"/>\n");
            writer.write("            </fields>\n");
            writer.write("            <relations>\n");
            writer.write("                <relation name=\"fk_orders_user\" column=\"user_id\" referenceTable=\"users\" referenceColumn=\"id\" onDelete=\"CASCADE\" onUpdate=\"CASCADE\" comment=\"用户外键\"/>\n");
            writer.write("            </relations>\n");
            writer.write("        </table>\n");
            writer.write("    </tables>\n");
            writer.write("</database>\n");
        }
        
        XmlValidationResult result = validator.validate(validXml);
        assertTrue(result.isValid(), "Complete valid XML file should pass validation");
        assertNull(result.getErrorMessage(), "Valid XML should have no error message");
    }
    
    @Test
    void testNonExistentFile() {
        // 测试不存在的文件
        File nonExistentFile = new File("non-existent-file.xml");
        XmlValidationResult result = validator.validate(nonExistentFile);
        assertFalse(result.isValid(), "Non-existent file should fail validation");
        assertNotNull(result.getErrorMessage(), "Non-existent file should have error message");
    }
    
    @Test
    void testValidationResultToString() {
        // 测试ValidationResult的toString方法
        XmlValidationResult successResult = XmlValidationResult.success();
        assertTrue(successResult.toString().contains("valid=true"));
        
        XmlValidationResult failureResult = XmlValidationResult.failure("Test error");
        assertTrue(failureResult.toString().contains("valid=false"));
        assertTrue(failureResult.toString().contains("Test error"));
    }
}