package cn.com.traninfo.fastlcdp.erdesigner.dialect;

import cn.com.traninfo.fastlcdp.erdesigner.config.DatabaseConfig;
import org.springframework.stereotype.Component;

/**
 * 数据库方言工厂类
 */
@Component
public class DatabaseDialectFactory {
    
    /**
     * 根据数据库类型获取对应的方言实现
     */
    public static DatabaseDialect createDialect(DatabaseConfig.DatabaseType databaseType) {
        switch (databaseType) {
            case H2:
                return new H2Dialect();
            case MYSQL:
                return new MySQLDialect();
            case POSTGRESQL:
                return new PostgreSQLDialect();
            case ORACLE:
                return new OracleDialect();
            case SQLSERVER:
                return new SqlServerDialect();
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }
}