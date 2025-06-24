package cn.com.traninfo.fastlcdp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * XML Schema 校验工具类
 * 用于验证XML配置文件是否符合XSD schema定义
 * 
 * @author FastLCDP
 * @since 1.0.0
 */
public class XmlSchemaValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlSchemaValidator.class);
    
    private static final String DEFAULT_SCHEMA_PATH = "/database-schema.xsd";
    
    private final Schema schema;
    
    /**
     * 使用默认的XSD schema构造校验器
     */
    public XmlSchemaValidator() {
        this(DEFAULT_SCHEMA_PATH);
    }
    
    /**
     * 使用指定的XSD schema路径构造校验器
     * 
     * @param schemaPath XSD schema文件路径（classpath相对路径）
     */
    public XmlSchemaValidator(String schemaPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            InputStream schemaStream = getClass().getResourceAsStream(schemaPath);
            if (schemaStream == null) {
                throw new IllegalArgumentException("Schema file not found: " + schemaPath);
            }
            this.schema = factory.newSchema(new StreamSource(schemaStream));
            logger.info("XML Schema validator initialized with schema: {}", schemaPath);
        } catch (SAXException e) {
            throw new RuntimeException("Failed to load XML schema: " + schemaPath, e);
        }
    }
    
    /**
     * 使用XSD schema文件构造校验器
     * 
     * @param schemaFile XSD schema文件
     */
    public XmlSchemaValidator(File schemaFile) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.schema = factory.newSchema(schemaFile);
            logger.info("XML Schema validator initialized with schema file: {}", schemaFile.getAbsolutePath());
        } catch (SAXException e) {
            throw new RuntimeException("Failed to load XML schema from file: " + schemaFile.getAbsolutePath(), e);
        }
    }
    
    /**
     * 使用XSD schema URL构造校验器
     * 
     * @param schemaUrl XSD schema URL
     */
    public XmlSchemaValidator(URL schemaUrl) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            this.schema = factory.newSchema(schemaUrl);
            logger.info("XML Schema validator initialized with schema URL: {}", schemaUrl.toString());
        } catch (SAXException e) {
            throw new RuntimeException("Failed to load XML schema from URL: " + schemaUrl.toString(), e);
        }
    }
    
    /**
     * 校验XML文件
     * 
     * @param xmlFile 要校验的XML文件
     * @return 校验结果
     */
    public ValidationResult validate(File xmlFile) {
        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));
            logger.debug("XML file validation successful: {}", xmlFile.getAbsolutePath());
            return ValidationResult.success();
        } catch (SAXException e) {
            logger.warn("XML file validation failed: {} - {}", xmlFile.getAbsolutePath(), e.getMessage());
            return ValidationResult.failure(e.getMessage());
        } catch (IOException e) {
            logger.error("Failed to read XML file: {}", xmlFile.getAbsolutePath(), e);
            return ValidationResult.failure("Failed to read XML file: " + e.getMessage());
        }
    }
    
    /**
     * 校验XML文件
     * 
     * @param xmlFilePath XML文件路径
     * @return 校验结果
     */
    public ValidationResult validate(String xmlFilePath) {
        return validate(new File(xmlFilePath));
    }
    
    /**
     * 校验XML输入流
     * 
     * @param xmlStream XML输入流
     * @return 校验结果
     */
    public ValidationResult validate(InputStream xmlStream) {
        try {
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlStream));
            logger.debug("XML stream validation successful");
            return ValidationResult.success();
        } catch (SAXException e) {
            logger.warn("XML stream validation failed: {}", e.getMessage());
            return ValidationResult.failure(e.getMessage());
        } catch (IOException e) {
            logger.error("Failed to read XML stream", e);
            return ValidationResult.failure("Failed to read XML stream: " + e.getMessage());
        }
    }
    
    /**
     * 校验结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        /**
         * 创建成功的校验结果
         * 
         * @return 成功的校验结果
         */
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        /**
         * 创建失败的校验结果
         * 
         * @param errorMessage 错误信息
         * @return 失败的校验结果
         */
        public static ValidationResult failure(String errorMessage) {
            return new ValidationResult(false, errorMessage);
        }
        
        /**
         * 是否校验成功
         * 
         * @return true表示校验成功，false表示校验失败
         */
        public boolean isValid() {
            return valid;
        }
        
        /**
         * 获取错误信息
         * 
         * @return 错误信息，校验成功时为null
         */
        public String getErrorMessage() {
            return errorMessage;
        }
        
        @Override
        public String toString() {
            if (valid) {
                return "ValidationResult{valid=true}";
            } else {
                return "ValidationResult{valid=false, errorMessage='" + errorMessage + "'}";
            }
        }
    }
}