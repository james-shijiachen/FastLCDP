package cn.com.traninfo.fastlcdp.service;

import cn.com.traninfo.fastlcdp.enums.PrimaryKeyType;
import cn.com.traninfo.fastlcdp.model.DatabaseSchema;
import cn.com.traninfo.fastlcdp.model.TableDefinition;
import cn.com.traninfo.fastlcdp.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * XML解析服务
 * 负责解析XML文件并转换为数据模型
 */
@Slf4j
@Service
public class XmlParserService {
    
    private final JAXBContext jaxbContext;
    private final MessageUtils messageUtils;
    
    @Autowired
    public XmlParserService(MessageUtils messageUtils) throws JAXBException {
        this.messageUtils = messageUtils;
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
        log.info(messageUtils.getMessage("xml.parse.start", xmlFile.getAbsolutePath()));
        
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        DatabaseSchema schema = (DatabaseSchema) unmarshaller.unmarshal(xmlFile);
        
        // 处理表继承关系
        processTableInheritance(schema);
        
        // 处理主键类型
        processPrimaryKeyTypes(schema);
        
        log.info(messageUtils.getMessage("xml.parse.success", schema.getTables().size()));
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
        
        // 处理主键类型
        processPrimaryKeyTypes(schema);
        
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
            log.warn(messageUtils.getMessage("inheritance.circular", table.getName()));
            return;
        }
        
        processedTables.add(table.getName());
        
        String parentTableName = table.getExtendsTable();
        if (parentTableName != null && !parentTableName.isEmpty()) {
            TableDefinition parentTable = tableMap.get(parentTableName);
            if (parentTable == null) {
                log.warn(messageUtils.getMessage("inheritance.parent.not.found", parentTableName, table.getName()));
                return;
            }
            
            // 递归处理父表的继承关系
            if (parentTable.getExtendsTable() != null && !parentTable.getExtendsTable().isEmpty()) {
                processTableInheritance(parentTable, tableMap, new ArrayList<>(processedTables));
            }
            
            // 合并父表的字段（在子表字段之前），使用LinkedHashMap确保顺序并去重
            Map<String, cn.com.traninfo.fastlcdp.model.FieldDefinition> fieldMap = new LinkedHashMap<>();
            
            // 先添加父表字段
            for (cn.com.traninfo.fastlcdp.model.FieldDefinition field : parentTable.getFields()) {
                fieldMap.put(field.getName(), field);
            }
            
            // 再添加子表字段（如果同名则覆盖父表字段）
            for (cn.com.traninfo.fastlcdp.model.FieldDefinition field : table.getFields()) {
                fieldMap.put(field.getName(), field);
            }
            
            table.setFields(new ArrayList<>(fieldMap.values()));
            
            // 合并父表的索引，使用LinkedHashMap去重
            Map<String, cn.com.traninfo.fastlcdp.model.IndexDefinition> indexMap = new LinkedHashMap<>();
            
            // 先添加父表索引
            for (cn.com.traninfo.fastlcdp.model.IndexDefinition index : parentTable.getIndexes()) {
                indexMap.put(index.getName(), index);
            }
            
            // 再添加子表索引（如果同名则覆盖父表索引）
            for (cn.com.traninfo.fastlcdp.model.IndexDefinition index : table.getIndexes()) {
                indexMap.put(index.getName(), index);
            }
            
            table.setIndexes(new ArrayList<>(indexMap.values()));
            
            // 合并父表的关联关系，使用LinkedHashMap去重
            Map<String, cn.com.traninfo.fastlcdp.model.RelationDefinition> relationMap = new LinkedHashMap<>();
            
            // 先添加父表关联关系
            for (cn.com.traninfo.fastlcdp.model.RelationDefinition relation : parentTable.getRelations()) {
                relationMap.put(relation.getName(), relation);
            }
            
            // 再添加子表关联关系（如果同名则覆盖父表关联关系）
            for (cn.com.traninfo.fastlcdp.model.RelationDefinition relation : table.getRelations()) {
                relationMap.put(relation.getName(), relation);
            }
            
            table.setRelations(new ArrayList<>(relationMap.values()));
            
            log.debug(messageUtils.getMessage("inheritance.success", table.getName(), parentTableName));
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
            for (cn.com.traninfo.fastlcdp.model.FieldDefinition field : table.getFields()) {
                // 如果primaryKey属性为true且autoIncrement为true，设置为AUTO_INCREMENT
                if (PrimaryKeyType.AUTO_INCREMENT.equals(field.getPrimaryKey())) {
                    field.setPrimaryKey(cn.com.traninfo.fastlcdp.enums.PrimaryKeyType.AUTO_INCREMENT);
                }
                // 如果只有primaryKey为true但autoIncrement为false或null，保持原有逻辑
                // 这里可以根据需要扩展其他主键类型的处理逻辑
            }
        }
    }
}