package com.fastlcdp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 数据库配置类
 */
@Component
@ConfigurationProperties(prefix = "database")
public class DatabaseConfig {
    
    /**
     * 数据库类型枚举
     */
    public enum DatabaseType {
        H2("H2", "org.h2.Driver", "jdbc:h2:mem:testdb"),
        MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/"),
        POSTGRESQL("PostgreSQL", "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/"),
        ORACLE("Oracle", "oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@localhost:1521:"),
        SQLSERVER("SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://localhost:1433;databaseName=");
        
        private final String displayName;
        private final String driverClassName;
        private final String urlPrefix;
        
        DatabaseType(String displayName, String driverClassName, String urlPrefix) {
            this.displayName = displayName;
            this.driverClassName = driverClassName;
            this.urlPrefix = urlPrefix;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getDriverClassName() {
            return driverClassName;
        }
        
        public String getUrlPrefix() {
            return urlPrefix;
        }
    }
    
    /**
     * 当前数据库类型
     */
    private DatabaseType type = DatabaseType.H2;
    
    /**
     * 数据库字符集
     */
    private String charset = "utf8mb4";
    
    /**
     * 数据库排序规则
     */
    private String collation = "utf8mb4_unicode_ci";
    
    /**
     * 是否启用元数据保存
     */
    private boolean enableMetadataStorage = true;
    
    /**
     * 元数据表前缀
     */
    private String metadataTablePrefix = "xml_meta_";
    
    /**
     * 是否自动创建元数据表
     */
    private boolean autoCreateMetadataTables = true;
    
    /**
     * 数据库方言配置
     */
    private DialectConfig dialect = new DialectConfig();
    
    public DatabaseType getType() {
        return type;
    }
    
    public void setType(DatabaseType type) {
        this.type = type;
    }
    
    public String getCharset() {
        return charset;
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public String getCollation() {
        return collation;
    }
    
    public void setCollation(String collation) {
        this.collation = collation;
    }
    
    /**
     * 检查是否启用元数据存储
     */
    public boolean isMetadataStorageEnabled() {
        return true; // 默认启用元数据存储
    }
    
    public boolean isEnableMetadataStorage() {
        return enableMetadataStorage;
    }
    
    public void setEnableMetadataStorage(boolean enableMetadataStorage) {
        this.enableMetadataStorage = enableMetadataStorage;
    }
    
    public String getMetadataTablePrefix() {
        return metadataTablePrefix;
    }
    
    public void setMetadataTablePrefix(String metadataTablePrefix) {
        this.metadataTablePrefix = metadataTablePrefix;
    }
    
    public boolean isAutoCreateMetadataTables() {
        return autoCreateMetadataTables;
    }
    
    public void setAutoCreateMetadataTables(boolean autoCreateMetadataTables) {
        this.autoCreateMetadataTables = autoCreateMetadataTables;
    }
    
    public DialectConfig getDialect() {
        return dialect;
    }
    
    public void setDialect(DialectConfig dialect) {
        this.dialect = dialect;
    }
    
    /**
     * 数据库方言配置
     */
    public static class DialectConfig {
        /**
         * 标识符引用字符
         */
        private String identifierQuote = "`";
        
        /**
         * 是否支持IF NOT EXISTS
         */
        private boolean supportsIfNotExists = true;
        
        /**
         * 自增关键字
         */
        private String autoIncrementKeyword = "AUTO_INCREMENT";
        
        /**
         * 当前时间戳函数
         */
        private String currentTimestampFunction = "CURRENT_TIMESTAMP";
        
        /**
         * 字符串连接操作符
         */
        private String stringConcatOperator = "CONCAT";
        
        /**
         * 限制查询语法
         */
        private String limitSyntax = "LIMIT";
        
        public String getIdentifierQuote() {
            return identifierQuote;
        }
        
        public void setIdentifierQuote(String identifierQuote) {
            this.identifierQuote = identifierQuote;
        }
        
        public boolean isSupportsIfNotExists() {
            return supportsIfNotExists;
        }
        
        public void setSupportsIfNotExists(boolean supportsIfNotExists) {
            this.supportsIfNotExists = supportsIfNotExists;
        }
        
        public String getAutoIncrementKeyword() {
            return autoIncrementKeyword;
        }
        
        public void setAutoIncrementKeyword(String autoIncrementKeyword) {
            this.autoIncrementKeyword = autoIncrementKeyword;
        }
        
        public String getCurrentTimestampFunction() {
            return currentTimestampFunction;
        }
        
        public void setCurrentTimestampFunction(String currentTimestampFunction) {
            this.currentTimestampFunction = currentTimestampFunction;
        }
        
        public String getStringConcatOperator() {
            return stringConcatOperator;
        }
        
        public void setStringConcatOperator(String stringConcatOperator) {
            this.stringConcatOperator = stringConcatOperator;
        }
        
        public String getLimitSyntax() {
            return limitSyntax;
        }
        
        public void setLimitSyntax(String limitSyntax) {
            this.limitSyntax = limitSyntax;
        }
    }
}