package com.fastlcp.util;

import lombok.extern.slf4j.Slf4j;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * XML配置验证器
 * <p>
 * 用于验证XML配置文件是否符合XSD规范
 * </p>
 *
 * @author FastLCP
 */
@Slf4j
public class XmlConfigValidator {

    private final Schema schema;

    /**
     * 构造函数
     *
     * @param xsdFile XSD文件
     * @throws SAXException SAX异常
     */
    public XmlConfigValidator(File xsdFile) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        this.schema = factory.newSchema(xsdFile);
        log.info("XML验证器初始化完成，使用XSD文件: {}", xsdFile.getAbsolutePath());
    }

    /**
     * 构造函数
     *
     * @param xsdPath XSD文件路径
     * @throws SAXException SAX异常
     */
    public XmlConfigValidator(String xsdPath) throws SAXException {
        this(new File(xsdPath));
    }

    /**
     * 验证XML文件
     *
     * @param xmlFile XML文件
     * @return 验证结果
     */
    public ValidationResult validateFile(File xmlFile) {
        log.info("开始验证XML文件: {}", xmlFile.getAbsolutePath());
        
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        result.setFilePath(xmlFile.getAbsolutePath());
        
        try {
            Validator validator = schema.newValidator();
            ValidationErrorHandler errorHandler = new ValidationErrorHandler();
            validator.setErrorHandler(errorHandler);
            
            validator.validate(new StreamSource(xmlFile));
            
            if (errorHandler.hasErrors()) {
                result.setValid(false);
                result.setErrors(errorHandler.getErrors());
                result.setWarnings(errorHandler.getWarnings());
            }
            
            log.info("XML文件验证完成，结果: {}", result.isValid() ? "通过" : "失败");
            
        } catch (SAXException e) {
            result.setValid(false);
            result.addError("XML解析错误: " + e.getMessage());
            log.error("XML文件验证失败", e);
        } catch (IOException e) {
            result.setValid(false);
            result.addError("文件读取错误: " + e.getMessage());
            log.error("XML文件读取失败", e);
        }
        
        return result;
    }

    /**
     * 验证XML字符串
     *
     * @param xmlContent XML内容
     * @return 验证结果
     */
    public ValidationResult validateString(String xmlContent) {
        log.info("开始验证XML字符串内容");
        
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        result.setFilePath("<string>");
        
        try {
            Validator validator = schema.newValidator();
            ValidationErrorHandler errorHandler = new ValidationErrorHandler();
            validator.setErrorHandler(errorHandler);
            
            validator.validate(new StreamSource(new StringReader(xmlContent)));
            
            if (errorHandler.hasErrors()) {
                result.setValid(false);
                result.setErrors(errorHandler.getErrors());
                result.setWarnings(errorHandler.getWarnings());
            }
            
            log.info("XML字符串验证完成，结果: {}", result.isValid() ? "通过" : "失败");
            
        } catch (SAXException e) {
            result.setValid(false);
            result.addError("XML解析错误: " + e.getMessage());
            log.error("XML字符串验证失败", e);
        } catch (IOException e) {
            result.setValid(false);
            result.addError("内容读取错误: " + e.getMessage());
            log.error("XML字符串读取失败", e);
        }
        
        return result;
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private boolean valid;
        private String filePath;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public void addError(String error) {
            this.errors.add(error);
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }

        public void addWarning(String warning) {
            this.warnings.add(warning);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationResult{\n");
            sb.append("  valid=").append(valid).append("\n");
            sb.append("  filePath='").append(filePath).append("'\n");
            
            if (hasErrors()) {
                sb.append("  errors=[\n");
                for (String error : errors) {
                    sb.append("    ").append(error).append("\n");
                }
                sb.append("  ]\n");
            }
            
            if (hasWarnings()) {
                sb.append("  warnings=[\n");
                for (String warning : warnings) {
                    sb.append("    ").append(warning).append("\n");
                }
                sb.append("  ]\n");
            }
            
            sb.append("}");
            return sb.toString();
        }
    }

    /**
     * 验证错误处理器
     */
    private static class ValidationErrorHandler implements ErrorHandler {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            String message = formatMessage("警告", exception);
            warnings.add(message);
            log.warn(message);
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            String message = formatMessage("错误", exception);
            errors.add(message);
            log.error(message);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            String message = formatMessage("严重错误", exception);
            errors.add(message);
            log.error(message);
            throw exception;
        }

        private String formatMessage(String level, SAXParseException exception) {
            return String.format("%s [行:%d, 列:%d]: %s",
                    level,
                    exception.getLineNumber(),
                    exception.getColumnNumber(),
                    exception.getMessage());
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public List<String> getErrors() {
            return errors;
        }

        public List<String> getWarnings() {
            return warnings;
        }
    }

    /**
     * 主方法，用于命令行调用
     *
     * @param args 命令行参数，第一个参数为XSD文件路径，第二个参数为XML文件路径
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("用法: java XmlConfigValidator <xsd文件路径> <xml文件路径>");
            System.exit(1);
        }

        try {
            String xsdPath = args[0];
            String xmlPath = args[1];

            XmlConfigValidator validator = new XmlConfigValidator(xsdPath);
            ValidationResult result = validator.validateFile(new File(xmlPath));

            System.out.println(result);

            if (!result.isValid()) {
                System.exit(1);
            }

        } catch (Exception e) {
            log.error("验证过程中发生错误", e);
            System.exit(1);
        }
    }
}