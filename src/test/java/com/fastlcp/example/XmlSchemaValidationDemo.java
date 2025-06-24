package com.fastlcp.example;

import com.fastlcp.util.XmlSchemaValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * XML Schema验证演示
 * 演示如何验证带有XSD引用的XML文件
 */
public class XmlSchemaValidationDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlSchemaValidationDemo.class);
    
    public static void main(String[] args) {
        XmlSchemaValidationDemo demo = new XmlSchemaValidationDemo();
        
        // 验证sample-database.xml
        demo.validateXmlFile("src/main/resources/examples/sample-database.xml", "示例数据库XML");
        
        // 验证simple-example.xml
        demo.validateXmlFile("src/main/resources/examples/simple-example.xml", "简单示例XML");
    }
    
    /**
     * 验证XML文件
     */
    private void validateXmlFile(String filePath, String description) {
        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                logger.error("文件不存在: {}", filePath);
                return;
            }
            
            logger.info("开始验证 {} - {}", description, filePath);
            
            // 使用默认的XSD schema进行验证
            XmlSchemaValidator validator = new XmlSchemaValidator();
            XmlSchemaValidator.ValidationResult result = validator.validate(xmlFile);
            
            if (result.isValid()) {
                logger.info("✅ {} 验证通过", description);
            } else {
                logger.error("❌ {} 验证失败: {}", description, result.getErrorMessage());
            }
            
        } catch (Exception e) {
            logger.error("验证 {} 时发生异常", description, e);
        }
    }
}