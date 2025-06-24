package com.fastlcp.service;

import com.fastlcp.model.DatabaseSchema;
import com.fastlcp.model.TableDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据库执行服务
 * 负责连接数据库并执行SQL语句
 */
@Slf4j
@Service
public class DatabaseExecutorService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    /**
     * 创建数据库模式
     * 
     * @param schema 数据库模式定义
     * @return 执行结果
     */
    @Transactional
    public boolean createDatabaseSchema(DatabaseSchema schema) {
        try {
            log.info("开始创建数据库模式: {}", schema.getName());
            
            // 生成并执行创建数据库的SQL
            if (schema.getName() != null && !schema.getName().isEmpty()) {
                String createDbSql = sqlGeneratorService.generateCreateDatabaseSql(schema);
                log.debug("执行创建数据库SQL: {}", createDbSql);
                jdbcTemplate.execute(createDbSql);
            }
            
            // 创建所有表
            for (TableDefinition table : schema.getTables()) {
                createTable(table);
            }
            
            log.info("数据库模式创建完成: {}", schema.getName());
            return true;
            
        } catch (Exception e) {
            log.error("创建数据库模式失败: {}", schema.getName(), e);
            throw new RuntimeException("创建数据库模式失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 创建单个表
     * 
     * @param table 表定义
     * @return 执行结果
     */
    @Transactional
    public boolean createTable(TableDefinition table) {
        try {
            log.info("开始创建表: {}", table.getName());
            
            String createTableSql = sqlGeneratorService.generateCreateTableSql(table);
            log.debug("执行创建表SQL: {}", createTableSql);
            
            jdbcTemplate.execute(createTableSql);
            
            log.info("表创建完成: {}", table.getName());
            return true;
            
        } catch (Exception e) {
            log.error("创建表失败: {}", table.getName(), e);
            throw new RuntimeException("创建表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 批量创建表
     * 
     * @param tables 表定义列表
     * @return 执行结果
     */
    @Transactional
    public boolean createTables(List<TableDefinition> tables) {
        try {
            log.info("开始批量创建 {} 个表", tables.size());
            
            for (TableDefinition table : tables) {
                createTable(table);
            }
            
            log.info("批量创建表完成");
            return true;
            
        } catch (Exception e) {
            log.error("批量创建表失败", e);
            throw new RuntimeException("批量创建表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查表是否存在
     * 
     * @param tableName 表名
     * @return 是否存在
     */
    public boolean tableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ? AND table_schema = DATABASE()";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("检查表是否存在时发生异常: {}", tableName, e);
            return false;
        }
    }
    
    /**
     * 删除表
     * 
     * @param tableName 表名
     * @return 执行结果
     */
    @Transactional
    public boolean dropTable(String tableName) {
        try {
            log.info("开始删除表: {}", tableName);
            
            String dropSql = "DROP TABLE IF EXISTS `" + tableName + "`";
            jdbcTemplate.execute(dropSql);
            
            log.info("表删除完成: {}", tableName);
            return true;
            
        } catch (Exception e) {
            log.error("删除表失败: {}", tableName, e);
            throw new RuntimeException("删除表失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 执行自定义SQL语句
     * 
     * @param sql SQL语句
     * @return 执行结果
     */
    @Transactional
    public boolean executeSql(String sql) {
        try {
            log.debug("执行自定义SQL: {}", sql);
            jdbcTemplate.execute(sql);
            return true;
        } catch (Exception e) {
            log.error("执行SQL失败: {}", sql, e);
            throw new RuntimeException("执行SQL失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取数据库连接信息
     * 
     * @return 连接信息
     */
    public String getDatabaseInfo() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            String database = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            return String.format("数据库版本: %s, 当前数据库: %s", version, database);
        } catch (Exception e) {
            log.warn("获取数据库信息失败", e);
            return "无法获取数据库信息";
        }
    }
}