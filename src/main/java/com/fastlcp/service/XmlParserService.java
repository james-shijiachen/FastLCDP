package com.fastlcp.service;

import com.fastlcp.model.DatabaseSchema;
import com.fastlcp.model.TableDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * XML解析服务
 * 负责解析XML文件并转换为数据模型
 */
@Slf4j
@Service
public class XmlParserService {
    
    private final JAXBContext jaxbContext;
    
    public XmlParserService() throws JAXBException {
        this.jaxbContext = JAXBContext.newInstance(DatabaseSchema.class, TableDefinition.class);
    }
    
    /**
     * 从文件解析数据库模式
     * 
     * @param xmlFile XML文件
     * @return 数据库模式对象
     * @throws JAXBException 解析异常
     */
    public DatabaseSchema parseFromFile(File xmlFile) throws JAXBException {
        log.info("开始解析XML文件: {}", xmlFile.getAbsolutePath());
        
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DatabaseSchema schema = (DatabaseSchema) unmarshaller.unmarshal(xmlFile);
        
        // 处理表继承关系
        processTableInheritance(schema);
        
        log.info("XML文件解析完成，共解析到 {} 个表定义", schema.getTables().size());
        return schema;
    }
    
    /**
     * 从输入流解析数据库模式
     * 
     * @param inputStream 输入流
     * @return 数据库模式对象
     * @throws JAXBException 解析异常
     */
    public DatabaseSchema parseFromStream(InputStream inputStream) throws JAXBException {
        log.info("开始从输入流解析XML");
        
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DatabaseSchema schema = (DatabaseSchema) unmarshaller.unmarshal(inputStream);
        
        // 处理表继承关系
        processTableInheritance(schema);
        
        log.info("XML解析完成，共解析到 {} 个表定义", schema.getTables().size());
        return schema;
    }
    
    /**
     * 处理表继承关系
     * 将父表的字段、索引、关联关系合并到子表中
     * 
     * @param schema 数据库模式
     */
    private void processTableInheritance(DatabaseSchema schema) {
        Map<String, TableDefinition> tableMap = new HashMap<>();
        
        // 构建表名到表定义的映射
        for (TableDefinition table : schema.getTables()) {
            tableMap.put(table.getName(), table);
        }
        
        // 处理每个表的继承关系
        for (TableDefinition table : schema.getTables()) {
            if (table.getExtendsTable() != null && !table.getExtendsTable().isEmpty()) {
                processTableInheritance(table, tableMap, new ArrayList<>());
            }
        }
    }
    
    /**
     * 递归处理单个表的继承关系
     * 
     * @param table 当前表
     * @param tableMap 表映射
     * @param processedTables 已处理的表列表（防止循环继承）
     */
    private void processTableInheritance(TableDefinition table, Map<String, TableDefinition> tableMap, List<String> processedTables) {
        if (processedTables.contains(table.getName())) {
            log.warn("检测到循环继承关系，表: {}", table.getName());
            return;
        }
        
        processedTables.add(table.getName());
        
        String parentTableName = table.getExtendsTable();
        if (parentTableName != null && !parentTableName.isEmpty()) {
            TableDefinition parentTable = tableMap.get(parentTableName);
            if (parentTable == null) {
                log.warn("找不到父表: {}，子表: {}", parentTableName, table.getName());
                return;
            }
            
            // 递归处理父表的继承关系
            if (parentTable.getExtendsTable() != null && !parentTable.getExtendsTable().isEmpty()) {
                processTableInheritance(parentTable, tableMap, new ArrayList<>(processedTables));
            }
            
            // 合并父表的字段（在子表字段之前）
            List<com.fastlcp.model.FieldDefinition> mergedFields = new ArrayList<>(parentTable.getFields());
            mergedFields.addAll(table.getFields());
            table.setFields(mergedFields);
            
            // 合并父表的索引
            List<com.fastlcp.model.IndexDefinition> mergedIndexes = new ArrayList<>(parentTable.getIndexes());
            mergedIndexes.addAll(table.getIndexes());
            table.setIndexes(mergedIndexes);
            
            // 合并父表的关联关系
            List<com.fastlcp.model.RelationDefinition> mergedRelations = new ArrayList<>(parentTable.getRelations());
            mergedRelations.addAll(table.getRelations());
            table.setRelations(mergedRelations);
            
            log.debug("表 {} 继承了父表 {} 的定义", table.getName(), parentTableName);
        }
    }
}