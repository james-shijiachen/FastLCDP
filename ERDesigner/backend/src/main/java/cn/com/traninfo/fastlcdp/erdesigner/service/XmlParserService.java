package cn.com.traninfo.fastlcdp.erdesigner.service;

import cn.com.traninfo.fastlcdp.erdesigner.enums.PrimaryKeyTypeEnum;
import cn.com.traninfo.fastlcdp.erdesigner.model.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * XML解析服务
 * 负责解析XML文件并转换为数据模型
 */
@Slf4j
@Service
public class XmlParserService {
    
    private final JAXBContext jaxbContext;

    public XmlParserService() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(DatabaseSchema.class, TableDefinition.class);
    }

    /**
     * 从文件解析数据库模式
     * 
     * @param xmlFile XML文件
     * @return 数据库模式对象
     * @throws JAXBException 解析异常
     */
    public DatabaseSchema parseFromFile(File xmlFile) throws JAXBException {
        log.info("Start parsing XML file: {}", xmlFile.getAbsolutePath());
        
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DatabaseSchema schema = (DatabaseSchema) unmarshaller.unmarshal(xmlFile);
        
        // 处理表继承关系
        processTableInheritance(schema);
        
        // 处理主键类型
        processPrimaryKeyTypes(schema);
        
        log.info("XML parsing completed, found {} table definitions", schema.getTables().size());
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
        log.info("Start parsing XML from the input stream");
        
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DatabaseSchema schema = (DatabaseSchema) unmarshaller.unmarshal(inputStream);
        
        // 处理表继承关系
        processTableInheritance(schema);
        
        // 处理主键类型
        processPrimaryKeyTypes(schema);
        
        log.info("XML parsing is completed, and a total of {} table definitions are parsed.", schema.getTables().size());
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
            log.warn("Circular inheritance detected for table: {}", table.getName());
            return;
        }
        
        processedTables.add(table.getName());
        
        String parentTableName = table.getExtendsTable();
        if (parentTableName != null && !parentTableName.isEmpty()) {
            TableDefinition parentTable = tableMap.get(parentTableName);
            if (parentTable == null) {
                log.warn("Parent table '{}' not found for child table '{}'", parentTableName, table.getName());
                return;
            }
            
            // 递归处理父表的继承关系
            if (parentTable.getExtendsTable() != null && !parentTable.getExtendsTable().isEmpty()) {
                processTableInheritance(parentTable, tableMap, new ArrayList<>(processedTables));
            }
            
            // 合并父表的字段（在子表字段之前），使用LinkedHashMap确保顺序并去重
            Map<String, FieldDefinition> fieldMap = new LinkedHashMap<>();
            
            // 先添加父表字段
            for (FieldDefinition field : parentTable.getFields()) {
                fieldMap.put(field.getName(), field);
            }
            
            // 再添加子表字段（如果同名则覆盖父表字段）
            for (FieldDefinition field : table.getFields()) {
                fieldMap.put(field.getName(), field);
            }
            
            table.setFields(new ArrayList<>(fieldMap.values()));
            
            // 合并父表的索引，使用LinkedHashMap去重
            Map<String, IndexDefinition> indexMap = new LinkedHashMap<>();
            
            // 先添加父表索引
            for (IndexDefinition index : parentTable.getIndexes()) {
                indexMap.put(index.getName(), index);
            }
            
            // 再添加子表索引（如果同名则覆盖父表索引）
            for (IndexDefinition index : table.getIndexes()) {
                indexMap.put(index.getName(), index);
            }
            
            table.setIndexes(new ArrayList<>(indexMap.values()));
            
            // 合并父表的关联关系，使用LinkedHashMap去重
            Map<String, RelationDefinition> relationMap = new LinkedHashMap<>();
            
            // 先添加父表关联关系
            for (RelationDefinition relation : parentTable.getRelations()) {
                relationMap.put(relation.getName(), relation);
            }
            
            // 再添加子表关联关系（如果同名则覆盖父表关联关系）
            for (RelationDefinition relation : table.getRelations()) {
                relationMap.put(relation.getName(), relation);
            }
            
            table.setRelations(new ArrayList<>(relationMap.values()));
            
            log.debug("Inheritance processed: child table '{}', parent table '{}'", table.getName(), parentTableName);
        }
    }
    
    /**
     * 处理主键类型
     * 根据primaryKey和autoIncrement属性的组合设置正确的PrimaryKeyType
     * 
     * @param schema 数据库模式
     */
    private void processPrimaryKeyTypes(DatabaseSchema schema) {
        for (TableDefinition table : schema.getTables()) {
            for (FieldDefinition field : table.getFields()) {
                // 如果primaryKey属性为true且autoIncrement为true，设置为AUTO_INCREMENT
                if (PrimaryKeyTypeEnum.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
                    field.setPrimaryKey(PrimaryKeyTypeEnum.AUTO_INCREMENT);
                }
                // 如果只有primaryKey为true但autoIncrement为false或null，保持原有逻辑
                // 这里可以根据需要扩展其他主键类型的处理逻辑
            }
        }
    }
}