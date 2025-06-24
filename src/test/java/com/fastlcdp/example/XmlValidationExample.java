package com.fastlcdp.example;

import com.fastlcdp.util.XmlSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * XML验证示例
 * 演示如何使用XmlSchemaValidator验证XML文件
 */
public class XmlValidationExample {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlValidationExample.class);
    
    public static void main(String[] args) {
        XmlValidationExample example = new XmlValidationExample();
        
        try {
            // 创建示例XML文件
            File validXmlFile = example.createValidXmlFile();
            File invalidXmlFile = example.createInvalidXmlFile();
            
            // 验证有效的XML文件
            example.validateXmlFile(validXmlFile, "有效的XML文件");
            
            // 验证无效的XML文件
            example.validateXmlFile(invalidXmlFile, "无效的XML文件");
            
            // 清理临时文件
            validXmlFile.delete();
            invalidXmlFile.delete();
            
        } catch (Exception e) {
            logger.error("XML验证示例执行失败", e);
        }
    }
    
    /**
     * 创建一个有效的XML文件
     */
    private File createValidXmlFile() throws IOException {
        File tempFile = File.createTempFile("valid_database", ".xml");
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<database name=\"test_db\" version=\"1.0\" charset=\"utf8mb4\" collation=\"utf8mb4_unicode_ci\" engine=\"InnoDB\" comment=\"测试数据库\">\n");
            writer.write("    <tables>\n");
            writer.write("        <table name=\"users\" comment=\"用户表\" engine=\"InnoDB\" charset=\"utf8mb4\">\n");
            writer.write("            <fields>\n");
            writer.write("                <field name=\"id\" type=\"LONG\" primaryKey=\"true\" autoIncrement=\"true\" comment=\"主键ID\"/>\n");
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
            writer.write("            </indexes>\n");
            writer.write("        </table>\n");
            writer.write("    </tables>\n");
            writer.write("</database>\n");
        }
        
        return tempFile;
    }
    
    /**
     * 创建一个无效的XML文件（缺少必需属性）
     */
    private File createInvalidXmlFile() throws IOException {
        File tempFile = File.createTempFile("invalid_database", ".xml");
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<database>\n"); // 缺少必需的name属性
            writer.write("    <tables>\n");
            writer.write("        <table name=\"users\">\n");
            writer.write("            <fields>\n");
            writer.write("                <field type=\"LONG\"/>\n"); // 缺少必需的name属性
            writer.write("            </fields>\n");
            writer.write("        </table>\n");
            writer.write("    </tables>\n");
            writer.write("</database>\n");
        }
        
        return tempFile;
    }
    
    /**
     * 演示简单的XML内容创建
     */
    private void demonstrateSimpleXmlCreation() {
        // 为了简化，这里只是演示概念
        
        String simpleXmlContent = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\">\n" +
            "<database name=\"simple_db\" version=\"1.0\" charset=\"utf8mb4\">\n" +
            "    <tables>\n" +
            "        <table name=\"users\" comment=\"用户表\">\n" +
            "            <fields>\n" +
            "                <field name=\"id\" type=\"LONG\" primaryKey=\"true\" autoIncrement=\"true\" comment=\"主键ID\"/>\n" +
            "                <field name=\"username\" type=\"STRING\" length=\"50\" nullable=\"false\" comment=\"用户名\"/>\n" +
            "            </fields>\n" +
            "        </table>\n" +
            "    </tables>\n" +
            "</database>\n";
        
        logger.info("简单XML示例内容:");
        logger.info(simpleXmlContent);
        
        // 这里可以进一步处理XML内容
        // 例如：写入文件、验证等
    }
    
    /**
     * 验证XML文件
     */
    private void validateXmlFile(File xmlFile, String description) {
        logger.info("开始验证{}: {}", description, xmlFile.getName());
        
        try {
            XmlSchemaValidator validator = new XmlSchemaValidator();
            XmlSchemaValidator.ValidationResult result = validator.validate(xmlFile);
            
            if (result.isValid()) {
                logger.info("✓ {} 验证通过", description);
            } else {
                logger.warn("✗ {} 验证失败: {}", description, result.getErrorMessage());
            }
            
        } catch (Exception e) {
            logger.error("验证 {} 时发生错误: {}", description, e.getMessage());
        }
        
        logger.info("完成验证{}", description);
        logger.info("---");
    }
    
    /**
     * 演示如何读取和验证现有的XML文件
     */
    private void validateExistingXmlFile(String filePath) {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            logger.warn("文件不存在: {}", filePath);
            return;
        }
        
        File xmlFile = path.toFile();
        validateXmlFile(xmlFile, "现有XML文件");
    }
}