package cn.com.traninfo.fastlcdp.erdesigner.config;

import com.baomidou.mybatisplus.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * MyBatis Plus 自动建表配置
 * 在Spring Boot启动时根据Entity注解自动创建数据库表
 */
@Slf4j
@Configuration
public class AutoTableGeneratorConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DatabaseConfig databaseConfig;

    private static final String ENTITY_PACKAGE = "cn.com.traninfo.fastlcdp.entity";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (databaseConfig.isAutoCreateMetadataTables()) {
            log.info("开始自动创建数据库表...");
            try {
                createTablesFromEntities();
                log.info("自动创建数据库表完成");
            } catch (Exception e) {
                log.error("自动创建数据库表失败", e);
            }
        }
    }

    /**
     * 扫描Entity类并创建对应的数据库表
     */
    private void createTablesFromEntities() throws Exception {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
        
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(ENTITY_PACKAGE) + "/**/*.class";
        
        Resource[] resources = resolver.getResources(packageSearchPath);
        
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(TableName.class)) {
                        createTableFromEntity(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("无法加载类: {}", className, e);
                }
            }
        }
    }

    /**
     * 根据Entity类创建数据库表
     */
    private void createTableFromEntity(Class<?> entityClass) {
        TableName tableNameAnnotation = entityClass.getAnnotation(TableName.class);
        String tableName = tableNameAnnotation.value();
        
        if (!StringUtils.hasText(tableName)) {
            tableName = camelToSnake(entityClass.getSimpleName().replace("Entity", ""));
        }
        tableName = databaseConfig.getMetadataTablePrefix() + tableName;

        log.info("检查表是否存在: {}", tableName);
        if (!isTableExists(tableName)) {
            log.info("表不存在，开始创建表: {}", tableName);
            String createTableSql = generateCreateTableSql(entityClass, tableName);
            log.debug("执行建表SQL: {}", createTableSql);
            
            try {
                jdbcTemplate.execute(createTableSql);
                log.info("表创建成功: {}", tableName);
            } catch (Exception e) {
                log.error("创建表失败: {}", tableName, e);
            }
        } else {
            log.info("表已存在，跳过创建: {}", tableName);
        }
    }

    /**
     * 检查表是否存在
     */
    private boolean isTableExists(String tableName) {
        try {
            String sql = getTableExistsSql(tableName);
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("检查表存在性失败: {}", tableName, e);
            return false;
        }
    }

    /**
     * 获取检查表存在性的SQL
     */
    private String getTableExistsSql(String tableName) {
        return switch (databaseConfig.getType()) {
            case H2 -> "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName.toUpperCase() + "'";
            case MYSQL -> "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = '" + tableName + "'";
            case POSTGRESQL -> "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'";
            case ORACLE -> "SELECT COUNT(*) FROM user_tables WHERE table_name = '" + tableName.toUpperCase() + "'";
            case SQLSERVER -> "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'";
        };
    }

    /**
     * 生成创建表的SQL语句
     */
    private String generateCreateTableSql(Class<?> entityClass, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        
        // 根据数据库类型添加IF NOT EXISTS支持
        if (databaseConfig.getType() == DatabaseConfig.DatabaseType.H2 || 
            databaseConfig.getType() == DatabaseConfig.DatabaseType.MYSQL) {
            sql.append("IF NOT EXISTS ");
        }
        
        sql.append(escapeIdentifier(tableName)).append(" (\n");
        
        List<String> columnDefinitions = new ArrayList<>();
        List<String> primaryKeys = new ArrayList<>();
        
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(TableField.class) || field.isAnnotationPresent(TableId.class)) {
                String columnDef = generateColumnDefinition(field);
                if (StringUtils.hasText(columnDef)) {
                    columnDefinitions.add(columnDef);
                }
                
                if (field.isAnnotationPresent(TableId.class)) {
                    String columnName = getColumnName(field);
                    primaryKeys.add(columnName);
                }
            }
        }
        
        sql.append("    ").append(String.join(",\n    ", columnDefinitions));
        
        // 添加主键约束
        if (!primaryKeys.isEmpty()) {
            sql.append(",\n    PRIMARY KEY (").append(String.join(", ", primaryKeys)).append(")");
        }
        
        sql.append("\n)");
        
        // 添加表选项
        sql.append(getTableOptions());
        
        return sql.toString();
    }

    /**
     * 生成列定义
     */
    private String generateColumnDefinition(Field field) {
        String columnName = getColumnName(field);
        String columnType = getColumnType(field);
        boolean nullable = isNullable(field);
        boolean autoIncrement = isAutoIncrement(field);
        
        StringBuilder columnDef = new StringBuilder();
        columnDef.append(escapeIdentifier(columnName)).append(" ").append(columnType);
        
        if (autoIncrement) {
            columnDef.append(getAutoIncrementKeyword());
        }
        
        if (!nullable) {
            columnDef.append(" NOT NULL");
        }
        
        return columnDef.toString();
    }

    /**
     * 获取列名
     */
    private String getColumnName(Field field) {
        if (field.isAnnotationPresent(TableField.class)) {
            TableField tableField = field.getAnnotation(TableField.class);
            if (StringUtils.hasText(tableField.value())) {
                return tableField.value();
            }
        }
        return camelToSnake(field.getName());
    }

    /**
     * 获取列类型
     */
    private String getColumnType(Field field) {
        Class<?> fieldType = field.getType();
        
        return switch (databaseConfig.getType()) {
            case H2 -> getH2ColumnType(fieldType);
            case MYSQL -> getMySQLColumnType(fieldType);
            case POSTGRESQL -> getPostgreSQLColumnType(fieldType);
            case ORACLE -> getOracleColumnType(fieldType);
            case SQLSERVER -> getSqlServerColumnType(fieldType);
        };
    }

    /**
     * 获取H2数据库列类型
     */
    private String getH2ColumnType(Class<?> fieldType) {
        if (fieldType == String.class) return "VARCHAR(255)";
        if (fieldType == Long.class || fieldType == long.class) return "BIGINT";
        if (fieldType == Integer.class || fieldType == int.class) return "INTEGER";
        if (fieldType == Boolean.class || fieldType == boolean.class) return "BOOLEAN";
        if (fieldType == LocalDateTime.class) return "TIMESTAMP";
        if (fieldType == LocalDate.class) return "DATE";
        if (fieldType == LocalTime.class) return "TIME";
        if (fieldType == BigDecimal.class) return "DECIMAL(19,2)";
        if (fieldType == Double.class || fieldType == double.class) return "DOUBLE";
        if (fieldType == Float.class || fieldType == float.class) return "REAL";
        return "VARCHAR(255)";
    }

    /**
     * 获取MySQL数据库列类型
     */
    private String getMySQLColumnType(Class<?> fieldType) {
        if (fieldType == String.class) return "VARCHAR(255)";
        if (fieldType == Long.class || fieldType == long.class) return "BIGINT";
        if (fieldType == Integer.class || fieldType == int.class) return "INT";
        if (fieldType == Boolean.class || fieldType == boolean.class) return "TINYINT(1)";
        if (fieldType == LocalDateTime.class) return "DATETIME";
        if (fieldType == LocalDate.class) return "DATE";
        if (fieldType == LocalTime.class) return "TIME";
        if (fieldType == BigDecimal.class) return "DECIMAL(19,2)";
        if (fieldType == Double.class || fieldType == double.class) return "DOUBLE";
        if (fieldType == Float.class || fieldType == float.class) return "FLOAT";
        return "VARCHAR(255)";
    }

    /**
     * 获取PostgreSQL数据库列类型
     */
    private String getPostgreSQLColumnType(Class<?> fieldType) {
        if (fieldType == String.class) return "VARCHAR(255)";
        if (fieldType == Long.class || fieldType == long.class) return "BIGINT";
        if (fieldType == Integer.class || fieldType == int.class) return "INTEGER";
        if (fieldType == Boolean.class || fieldType == boolean.class) return "BOOLEAN";
        if (fieldType == LocalDateTime.class) return "TIMESTAMP";
        if (fieldType == LocalDate.class) return "DATE";
        if (fieldType == LocalTime.class) return "TIME";
        if (fieldType == BigDecimal.class) return "DECIMAL(19,2)";
        if (fieldType == Double.class || fieldType == double.class) return "DOUBLE PRECISION";
        if (fieldType == Float.class || fieldType == float.class) return "REAL";
        return "VARCHAR(255)";
    }

    /**
     * 获取Oracle数据库列类型
     */
    private String getOracleColumnType(Class<?> fieldType) {
        if (fieldType == String.class) return "VARCHAR2(255)";
        if (fieldType == Long.class || fieldType == long.class) return "NUMBER(19)";
        if (fieldType == Integer.class || fieldType == int.class) return "NUMBER(10)";
        if (fieldType == Boolean.class || fieldType == boolean.class) return "NUMBER(1)";
        if (fieldType == LocalDateTime.class) return "TIMESTAMP";
        if (fieldType == LocalDate.class) return "DATE";
        if (fieldType == LocalTime.class) return "TIMESTAMP";
        if (fieldType == BigDecimal.class) return "NUMBER(19,2)";
        if (fieldType == Double.class || fieldType == double.class) return "NUMBER";
        if (fieldType == Float.class || fieldType == float.class) return "NUMBER";
        return "VARCHAR2(255)";
    }

    /**
     * 获取SQL Server数据库列类型
     */
    private String getSqlServerColumnType(Class<?> fieldType) {
        if (fieldType == String.class) return "NVARCHAR(255)";
        if (fieldType == Long.class || fieldType == long.class) return "BIGINT";
        if (fieldType == Integer.class || fieldType == int.class) return "INT";
        if (fieldType == Boolean.class || fieldType == boolean.class) return "BIT";
        if (fieldType == LocalDateTime.class) return "DATETIME2";
        if (fieldType == LocalDate.class) return "DATE";
        if (fieldType == LocalTime.class) return "TIME";
        if (fieldType == BigDecimal.class) return "DECIMAL(19,2)";
        if (fieldType == Double.class || fieldType == double.class) return "FLOAT";
        if (fieldType == Float.class || fieldType == float.class) return "REAL";
        return "NVARCHAR(255)";
    }

    /**
     * 判断字段是否可为空
     */
    private boolean isNullable(Field field) {
        if (field.isAnnotationPresent(TableId.class)) {
            return false; // 主键不能为空
        }
        
        if (field.isAnnotationPresent(TableField.class)) {
            // 检查基本类型，基本类型不能为空
            Class<?> fieldType = field.getType();
            return !fieldType.isPrimitive();
        }
        
        return true;
    }

    /**
     * 判断字段是否自增
     */
    private boolean isAutoIncrement(Field field) {
        if (field.isAnnotationPresent(TableId.class)) {
            TableId tableId = field.getAnnotation(TableId.class);
            return tableId.type() == IdType.AUTO;
        }
        return false;
    }

    /**
     * 获取自增关键字
     */
    private String getAutoIncrementKeyword() {
        return switch (databaseConfig.getType()) {
            case H2, MYSQL -> " AUTO_INCREMENT";
            case ORACLE, POSTGRESQL -> ""; //使用序列
            case SQLSERVER -> " IDENTITY(1,1)";
        };
    }

    /**
     * 获取表选项
     */
    private String getTableOptions() {
        return switch (databaseConfig.getType()) {
            case MYSQL -> " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            default -> "";
        };
    }

    /**
     * 转义标识符
     */
    private String escapeIdentifier(String identifier) {
        return switch (databaseConfig.getType()) {
            case H2, MYSQL -> "`" + identifier + "`";
            case POSTGRESQL -> "\"" + identifier + "\"";
            case ORACLE, SQLSERVER -> "[" + identifier + "]";
        };
    }

    /**
     * 驼峰转下划线
     */
    private String camelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}