package cn.com.traninfo.fastlcdp.erdesigner.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

/**
 * XML Schema 校验工具类
 * 用于验证XML配置文件是否符合XSD schema定义
 * 
 * @author FastLCDP
 * @since 1.0.0
 */
@Component
public class XmlSchemaValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(XmlSchemaValidator.class);

    private String DEFAULT_SCHEMA_PATH;

    @Value("${sax.maxErrorCount:10}")
    private Integer maxErrorCount;

    private Schema schema;

    /**:
     * 默认构造校验器
     *
     */
    public XmlSchemaValidator(@Value("${sax.schemaPath:/database-schema.xsd}") String schemaPath) throws IOException, SAXException {
        this.DEFAULT_SCHEMA_PATH = schemaPath;
        this.loadValidator(DEFAULT_SCHEMA_PATH);
    }

    /**
     * 使用指定的XSD schema路径构造校验器
     * 
     * @param schemaPath XSD schema文件路径（classpath相对路径）
     */
    public void loadValidator(String schemaPath) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream schemaStream = XmlSchemaValidator.class.getResourceAsStream(schemaPath);
        if(schemaStream == null){
            throw new IllegalArgumentException("Schema resource not found: " + schemaPath);
        }
        this.schema = factory.newSchema(new StreamSource(schemaStream));
        logger.info("XML Schema validator initialized with schema: {}", schemaPath);
    }
    
    /**
     * 使用XSD schema文件构造校验器
     * 
     * @param schemaFile XSD schema文件
     */
    public void loadValidator(File schemaFile) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = factory.newSchema(schemaFile);
        logger.info("XML Schema validator initialized with schema file: {}", schemaFile.getAbsolutePath());
    }
    
    /**
     * 使用XSD schema URL构造校验器
     * 
     * @param schemaUrl XSD schema URL
     */
    public void loadValidator(URL schemaUrl) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = factory.newSchema(schemaUrl);
        logger.info("XML Schema validator initialized with schema URL: {}", schemaUrl.toString());
    }
    
    /**
     * 校验XML文件
     * 
     * @param xmlFile 要校验的XML文件
     * @return 校验结果
     */
    public XmlValidationResult validate(File xmlFile) {

        XmlValidationResult result = XmlValidationResult.success();
        result.setFilePath(xmlFile.getAbsolutePath());

        try {
            Validator validator = schema.newValidator();
            XmlValidationErrorHandler errorHandler = new XmlValidationErrorHandler(maxErrorCount);
            validator.setErrorHandler(errorHandler);

            validator.validate(new StreamSource(xmlFile));

            if (errorHandler.hasErrors()) {
                result.setValid(false);
                result.setErrors(errorHandler.getErrors());
                result.setWarnings(errorHandler.getWarnings());
            }

            logger.debug("XML file validation successful: {}", xmlFile.getAbsolutePath());
        } catch (SAXException e) {
            result.setValid(false);
            result.addError("XML file validation failed: " + e.getMessage());
            logger.error("XML file validation failed: {}", xmlFile.getAbsolutePath(), e);
        } catch (IOException e) {
            result.setValid(false);
            result.addError("Failed to read XML file: " + e.getMessage());
            logger.error("Failed to read XML file: {}", xmlFile.getAbsolutePath(), e);
        }
        return result;
    }

    /**
     * 校验XML文件内容
     * @param xmlContent
     * @return
     */
    public XmlValidationResult validate(String xmlContent) {

        XmlValidationResult result = XmlValidationResult.success();
        result.setFilePath("<string>");

        try {
            Validator validator = schema.newValidator();
            XmlValidationErrorHandler errorHandler = new XmlValidationErrorHandler(maxErrorCount);
            validator.setErrorHandler(errorHandler);

            validator.validate(new StreamSource(new StringReader(xmlContent)));

            if (errorHandler.hasErrors()) {
                result.setValid(false);
                result.setErrors(errorHandler.getErrors());
                result.setWarnings(errorHandler.getWarnings());
            }

            logger.debug("XML file validation successful: {}", xmlContent);

        } catch (SAXException e) {
            result.setValid(false);
            result.addError("XML Content validation failed: " + e.getMessage());
            logger.error("XML Content validation failed: {}", xmlContent, e);
        } catch (IOException e) {
            result.setValid(false);
            result.addError("Failed to read XML Content: " + e.getMessage());
            logger.error("Failed to read XML Content: {}", xmlContent, e);
        }
        return result;
    }

    /**
     * 校验XML文件路径
     * 
     * @param xmlFilePath XML文件路径
     * @return 校验结果
     */
    public XmlValidationResult validateXmlFilePath(String xmlFilePath) {
        return validate(new File(xmlFilePath));
    }
    
    /**
     * 校验XML输入流
     * 
     * @param xmlStream XML输入流
     * @return 校验结果
     */
    public XmlValidationResult validate(InputStream xmlStream) {
        XmlValidationResult result = XmlValidationResult.success();
        result.setFilePath("<stream>");

        try {
            Validator validator = schema.newValidator();
            XmlValidationErrorHandler errorHandler = new XmlValidationErrorHandler(maxErrorCount);
            validator.setErrorHandler(errorHandler);
            validator.validate(new StreamSource(xmlStream));
            if (errorHandler.hasErrors()) {
                result.setValid(false);
                result.setErrors(errorHandler.getErrors());
                result.setWarnings(errorHandler.getWarnings());
            }
            logger.debug("XML stream validation successful");

        } catch (SAXException e) {
            result.setValid(false);
            result.addError("XML stream validation failed: " + e.getMessage());
            logger.error("XML stream validation failed:", e);
        } catch (IOException e) {
            result.setValid(false);
            result.addError("Failed to read XML stream: " + e.getMessage());
            logger.error("Failed to read XML stream", e);
        }
        return result;
    }
}