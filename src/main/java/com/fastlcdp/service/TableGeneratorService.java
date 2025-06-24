package com.fastlcdp.service;

import com.fastlcdp.model.DatabaseSchema;
import com.fastlcdp.model.TableDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * 表生成器服务
 * 核心服务类，整合XML解析、SQL生成和数据库执行功能
 */
@Slf4j
@Service
public class TableGeneratorService {
    
    @Autowired
    private XmlParserService xmlParserService;
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Autowired
    private DatabaseExecutorService databaseExecutorService;
    
    /**
     * 从XML文件生成数据库表
     * 
     * @param xmlFile XML文件
     * @return 生成结果
     */
    public GenerationResult generateFromFile(File xmlFile) {
        GenerationResult result = new GenerationResult();
        
        try {
            log.info("开始从XML文件生成数据库表: {}", xmlFile.getAbsolutePath());
            
            // 解析XML文件
            DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
            result.setSchema(schema);
            
            // 生成并执行SQL
            boolean success = databaseExecutorService.createDatabaseSchema(schema);
            result.setSuccess(success);
            
            if (success) {
                result.setMessage(String.format("成功生成 %d 个表", schema.getTables().size()));
                log.info("数据库表生成完成: {}", xmlFile.getAbsolutePath());
            }
            
        } catch (JAXBException e) {
            String errorMsg = "XML解析失败: " + e.getMessage();
            result.setSuccess(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "生成数据库表失败: " + e.getMessage();
            result.setSuccess(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        }
        
        return result;
    }
    
    /**
     * 从输入流生成数据库表
     * 
     * @param inputStream 输入流
     * @return 生成结果
     */
    public GenerationResult generateFromStream(InputStream inputStream) {
        GenerationResult result = new GenerationResult();
        
        try {
            log.info("开始从输入流生成数据库表");
            
            // 解析XML
            DatabaseSchema schema = xmlParserService.parseFromStream(inputStream);
            result.setSchema(schema);
            
            // 生成并执行SQL
            boolean success = databaseExecutorService.createDatabaseSchema(schema);
            result.setSuccess(success);
            
            if (success) {
                result.setMessage(String.format("成功生成 %d 个表", schema.getTables().size()));
                log.info("数据库表生成完成");
            }
            
        } catch (JAXBException e) {
            String errorMsg = "XML解析失败: " + e.getMessage();
            result.setSuccess(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "生成数据库表失败: " + e.getMessage();
            result.setSuccess(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        }
        
        return result;
    }
    
    /**
     * 预览生成的SQL语句（不执行）
     * 
     * @param xmlFile XML文件
     * @return SQL预览结果
     */
    public SqlPreviewResult previewSql(File xmlFile) {
        SqlPreviewResult result = new SqlPreviewResult();
        
        try {
            log.info("开始预览SQL: {}", xmlFile.getAbsolutePath());
            
            // 解析XML文件
            DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
            result.setSchema(schema);
            
            // 生成SQL语句
            StringBuilder allSql = new StringBuilder();
            
            // 生成创建数据库的SQL
            if (schema.getName() != null && !schema.getName().isEmpty()) {
                String createDbSql = sqlGeneratorService.generateCreateDatabaseSql(schema);
                allSql.append(createDbSql).append("\n");
            }
            
            // 生成创建表的SQL
            for (TableDefinition table : schema.getTables()) {
                String createTableSql = sqlGeneratorService.generateCreateTableSql(table);
                allSql.append(createTableSql).append("\n");
            }
            
            result.setSql(allSql.toString());
            result.setSuccess(true);
            result.setMessage("SQL预览生成成功");
            
            log.info("SQL预览完成: {}", xmlFile.getAbsolutePath());
            
        } catch (JAXBException e) {
            String errorMsg = "XML解析失败: " + e.getMessage();
            result.setSuccess(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "SQL预览失败: " + e.getMessage();
            result.setSuccess(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        }
        
        return result;
    }
    
    /**
     * 验证XML文件格式
     * 
     * @param xmlFile XML文件
     * @return 验证结果
     */
    public ValidationResult validateXml(File xmlFile) {
        ValidationResult result = new ValidationResult();
        
        try {
            log.info("开始验证XML文件: {}", xmlFile.getAbsolutePath());
            
            // 尝试解析XML文件
            DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
            
            // 基本验证
            if (schema.getTables() == null || schema.getTables().isEmpty()) {
                result.setValid(false);
                result.setMessage("XML文件中没有定义任何表");
                return result;
            }
            
            // 验证每个表的定义
            for (TableDefinition table : schema.getTables()) {
                if (table.getName() == null || table.getName().trim().isEmpty()) {
                    result.setValid(false);
                    result.setMessage("存在未命名的表定义");
                    return result;
                }
                
                if (table.getFields() == null || table.getFields().isEmpty()) {
                    result.setValid(false);
                    result.setMessage(String.format("表 '%s' 没有定义任何字段", table.getName()));
                    return result;
                }
            }
            
            result.setValid(true);
            result.setMessage(String.format("XML文件验证通过，包含 %d 个表定义", schema.getTables().size()));
            result.setSchema(schema);
            
            log.info("XML文件验证完成: {}", xmlFile.getAbsolutePath());
            
        } catch (JAXBException e) {
            String errorMsg = "XML格式错误: " + e.getMessage();
            result.setValid(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "XML验证失败: " + e.getMessage();
            result.setValid(false);
            result.setMessage(errorMsg);
            log.error(errorMsg, e);
        }
        
        return result;
    }
    
    /**
     * 生成结果类
     */
    public static class GenerationResult {
        private boolean success;
        private String message;
        private DatabaseSchema schema;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public DatabaseSchema getSchema() { return schema; }
        public void setSchema(DatabaseSchema schema) { this.schema = schema; }
    }
    
    /**
     * SQL预览结果类
     */
    public static class SqlPreviewResult {
        private boolean success;
        private String message;
        private String sql;
        private DatabaseSchema schema;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSql() { return sql; }
        public void setSql(String sql) { this.sql = sql; }
        public DatabaseSchema getSchema() { return schema; }
        public void setSchema(DatabaseSchema schema) { this.schema = schema; }
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;
        private DatabaseSchema schema;
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public DatabaseSchema getSchema() { return schema; }
        public void setSchema(DatabaseSchema schema) { this.schema = schema; }
    }
}