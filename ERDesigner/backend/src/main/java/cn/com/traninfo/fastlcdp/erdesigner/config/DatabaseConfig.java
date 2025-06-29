package cn.com.traninfo.fastlcdp.erdesigner.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 数据库配置类
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "database")
public class DatabaseConfig {
    
    /**
     * 数据库类型枚举
     */
    @Getter
    public enum DatabaseType {
        H2("H2", "org.h2.Driver"),
        MYSQL("MySQL", "com.mysql.cj.jdbc.Driver"),
        POSTGRESQL("PostgreSQL", "org.postgresql.Driver"),
        ORACLE("Oracle", "oracle.jdbc.driver.OracleDriver"),
        SQLSERVER("SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        private final String displayName;
        private final String driverClassName;
        
        DatabaseType(String displayName, String driverClassName) {
            this.displayName = displayName;
            this.driverClassName = driverClassName;
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

    /**
     * 检查是否启用元数据存储
     */
    public boolean isMetadataStorageEnabled() {
        return true; // 默认启用元数据存储
    }

    /**
     * 数据库方言配置
     */
    @Setter
    @Getter
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

    }
}