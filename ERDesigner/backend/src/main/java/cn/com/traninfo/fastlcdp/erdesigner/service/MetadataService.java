package cn.com.traninfo.fastlcdp.erdesigner.service;

import cn.com.traninfo.fastlcdp.erdesigner.base.service.EntityService;
import cn.com.traninfo.fastlcdp.erdesigner.base.service.SearchService;
import cn.com.traninfo.fastlcdp.erdesigner.config.DatabaseConfig;
import cn.com.traninfo.fastlcdp.erdesigner.dialect.DatabaseDialect;
import cn.com.traninfo.fastlcdp.erdesigner.dialect.DatabaseDialectFactory;
import cn.com.traninfo.fastlcdp.erdesigner.entity.MetadataEntity;
import cn.com.traninfo.fastlcdp.erdesigner.enums.IndexTypeEnum;
import cn.com.traninfo.fastlcdp.erdesigner.enums.PrimaryKeyTypeEnum;
import cn.com.traninfo.fastlcdp.erdesigner.model.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Getter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 元数据服务类
 */
@Service
public class MetadataService {
    
    private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);
    
    @Autowired
    private EntityService<MetadataEntity> entityService;
    
    @Autowired
    private SearchService<MetadataEntity> searchService;
    
    @Autowired
    private DatabaseConfig databaseConfig;
    
    @Autowired
    private XmlParserService xmlParserService;
    
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Getter
    private List<MetadataEntity> metadataList;

    /**
     * 保存模式定义到数据库
     */
    @Transactional
    public void saveSchemaDefinition(DatabaseSchema schema) {
        logger.info("开始保存模式定义: {}", schema.getName());
        
        // 删除已存在的元数据
        deleteBySchemaName(schema.getName());
        
        List<MetadataEntity> metadataList = new ArrayList<>();
        
        // 保存表定义
        if (schema.getTables() != null) {
            for (TableDefinition table : schema.getTables()) {
                metadataList.addAll(convertTableToMetadata(schema.getName(), table));
            }
        }
        
        // 批量保存
        if (!metadataList.isEmpty()) {
            try {
                entityService.saveBatch(metadataList);
            } catch (Exception e) {
                logger.error("批量保存元数据失败: {}", e.getMessage(), e);
                throw new RuntimeException("批量保存元数据失败", e);
            }
        }
        
        logger.info("模式定义保存完成，共保存 {} 条元数据记录", metadataList.size());
    }
    
    /**
     * 将表定义转换为元数据实体列表
     */
    private List<MetadataEntity> convertTableToMetadata(String schemaName, TableDefinition table) {
        List<MetadataEntity> metadataList = new ArrayList<>();
        
        // 保存表信息
        MetadataEntity tableMetadata = new MetadataEntity(schemaName, table.getName());
        tableMetadata.setComment(table.getComment());
        metadataList.add(tableMetadata);
        
        // 保存字段信息
        if (table.getFields() != null) {
            for (FieldDefinition field : table.getFields()) {
                MetadataEntity fieldMetadata = new MetadataEntity(schemaName, table.getName());
                fieldMetadata.setFieldName(field.getName());
                fieldMetadata.setFieldType(field.getType());
                fieldMetadata.setFieldLength(field.getLength());
                fieldMetadata.setFieldScale(field.getScale());
                fieldMetadata.setIsNullable(field.getNullable());
                fieldMetadata.setPrimaryKeyType(field.getPrimaryKey().name());
                fieldMetadata.setDefaultValue(field.getDefaultValue());
                fieldMetadata.setComment(field.getComment());
                metadataList.add(fieldMetadata);
            }
        }
        
        // 保存索引信息
        if (table.getIndexes() != null) {
            for (IndexDefinition index : table.getIndexes()) {
                MetadataEntity indexMetadata = new MetadataEntity(schemaName, table.getName());
                indexMetadata.setIndexName(index.getName());
                indexMetadata.setIndexType(index.getType() != null ? index.getType().name() : null);
                indexMetadata.setComment(index.getComment());
                metadataList.add(indexMetadata);
            }
        }
        
        // 保存关系信息
        if (table.getRelations() != null) {
            for (RelationDefinition relation : table.getRelations()) {
                MetadataEntity relationMetadata = new MetadataEntity(schemaName, table.getName());
                relationMetadata.setFieldName(relation.getColumn());
                relationMetadata.setRelationType(relation.getName());
                relationMetadata.setReferenceTable(relation.getReferenceTable());
                relationMetadata.setReferenceField(relation.getReferenceColumn());
                metadataList.add(relationMetadata);
            }
        }
        
        return metadataList;
    }
    
    /**
     * 根据模式名查询模式定义
     */
    public DatabaseSchema getSchemaDefinition(String schemaName) {
        logger.info("查询模式定义: {}", schemaName);
        
        QueryWrapper<MetadataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schema_name", schemaName);
        List<MetadataEntity> metadataList = searchService.list(queryWrapper);
        if (metadataList.isEmpty()) {
            logger.warn("未找到模式定义: {}", schemaName);
            return null;
        }
        
        return convertMetadataToSchema(schemaName, metadataList);
    }
    
    /**
     * 将元数据实体列表转换为模式定义
     */
    private DatabaseSchema convertMetadataToSchema(String schemaName, List<MetadataEntity> metadataList) {
        this.metadataList = metadataList;
        DatabaseSchema schema = new DatabaseSchema();
        schema.setName(schemaName);
        
        // 获取所有表名
        QueryWrapper<MetadataEntity> tableQueryWrapper = new QueryWrapper<>();
        tableQueryWrapper.eq("schema_name", schemaName)
                         .select("DISTINCT table_name");
        List<MetadataEntity> tableEntities = searchService.list(tableQueryWrapper);
        List<String> tableNames = tableEntities.stream()
                .map(MetadataEntity::getTableName)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
        List<TableDefinition> tables = new ArrayList<>();
        
        for (String tableName : tableNames) {
            TableDefinition table = convertMetadataToTable(schemaName, tableName);
            if (table != null) {
                tables.add(table);
            }
        }
        
        schema.setTables(tables);
        return schema;
    }
    
    /**
     * 将元数据转换为表定义
     */
    private TableDefinition convertMetadataToTable(String schemaName, String tableName) {
        QueryWrapper<MetadataEntity> tableMetadataWrapper = new QueryWrapper<>();
        tableMetadataWrapper.eq("schema_name", schemaName)
                           .eq("table_name", tableName);
        List<MetadataEntity> tableMetadata = searchService.list(tableMetadataWrapper);
        if (tableMetadata.isEmpty()) {
            return null;
        }
        
        TableDefinition table = new TableDefinition();
        table.setName(tableName);
        
        // 设置表注释
        tableMetadata.stream()
                .filter(m -> StringUtils.hasText(m.getComment()) && m.getFieldName() == null)
                .findFirst()
                .ifPresent(m -> table.setComment(m.getComment()));
        
        // 转换字段
        QueryWrapper<MetadataEntity> fieldWrapper = new QueryWrapper<>();
        fieldWrapper.eq("schema_name", schemaName)
                   .eq("table_name", tableName)
                   .isNotNull("field_name");
        List<MetadataEntity> fieldMetadata = searchService.list(fieldWrapper);
        List<FieldDefinition> fields = new ArrayList<>();
        for (MetadataEntity metadata : fieldMetadata) {
            FieldDefinition field = new FieldDefinition();
            field.setName(metadata.getFieldName());
            field.setType(metadata.getFieldType());
            field.setLength(metadata.getFieldLength());
            field.setScale(metadata.getFieldScale());
            field.setNullable(metadata.getIsNullable());
            field.setPrimaryKey(PrimaryKeyTypeEnum.valueOf(metadata.getPrimaryKeyType()));
            field.setDefaultValue(metadata.getDefaultValue());
            field.setComment(metadata.getComment());
            fields.add(field);
        }
        table.setFields(fields);
        
        // 转换索引
        QueryWrapper<MetadataEntity> indexWrapper = new QueryWrapper<>();
        indexWrapper.eq("schema_name", schemaName)
                   .eq("table_name", tableName)
                   .isNotNull("index_name");
        List<MetadataEntity> indexMetadata = searchService.list(indexWrapper);
        List<IndexDefinition> indexes = new ArrayList<>();
        for (MetadataEntity metadata : indexMetadata) {
            IndexDefinition index = new IndexDefinition();
            index.setName(metadata.getIndexName());
            index.setType(metadata.getIndexType() != null ? IndexTypeEnum.valueOf(metadata.getIndexType()) : null);
            index.setComment(metadata.getComment());
            indexes.add(index);
        }
        table.setIndexes(indexes);
        
        // 转换关系
        QueryWrapper<MetadataEntity> relationWrapper = new QueryWrapper<>();
        relationWrapper.eq("schema_name", schemaName)
                      .eq("table_name", tableName)
                      .isNotNull("relation_type");
        List<MetadataEntity> relationMetadata = searchService.list(relationWrapper);
        List<RelationDefinition> relations = new ArrayList<>();
        for (MetadataEntity metadata : relationMetadata) {
            RelationDefinition relation = new RelationDefinition();
            relation.setColumn(metadata.getFieldName());
            relation.setName(metadata.getRelationType());
            relation.setReferenceTable(metadata.getReferenceTable());
            relation.setReferenceColumn(metadata.getReferenceField());
            relations.add(relation);
        }
        table.setRelations(relations);
        
        return table;
    }
    
    /**
     * 生成元数据表的DDL语句
     */
    public String generateMetadataTableDDL() {
        DatabaseDialect dialect = DatabaseDialectFactory.createDialect(databaseConfig.getType());
        
        // 创建元数据表定义
        TableDefinition metadataTable = createMetadataTableDefinition();
        
        StringBuilder ddl = new StringBuilder();
        
        // 生成创建表语句
        ddl.append(dialect.generateCreateTableSql(metadataTable));
        ddl.append(";\n\n");
        
        // 生成索引语句
        if (metadataTable.getIndexes() != null) {
            for (IndexDefinition index : metadataTable.getIndexes()) {
                ddl.append(dialect.generateCreateIndexSql(metadataTable.getName(), index));
                ddl.append(";\n");
            }
        }
        
        return ddl.toString();
    }
    
    /**
     * 创建元数据表定义
     */
    private TableDefinition createMetadataTableDefinition() {
        TableDefinition table = new TableDefinition();
        table.setName("metadata");
        table.setComment("XML元数据存储表");
        
        List<FieldDefinition> fields = new ArrayList<>();
        
        // ID字段
        FieldDefinition idField = new FieldDefinition();
        idField.setName("id");
        idField.setType("LONG");
        idField.setPrimaryKey(PrimaryKeyTypeEnum.AUTO_INCREMENT);
        idField.setNullable(false);
        idField.setComment("主键ID");
        fields.add(idField);
        
        // 模式名字段
        FieldDefinition schemaNameField = new FieldDefinition();
        schemaNameField.setName("schema_name");
        schemaNameField.setType("STRING");
        schemaNameField.setLength(100);
        schemaNameField.setNullable(false);
        schemaNameField.setComment("模式名称");
        fields.add(schemaNameField);
        
        // 表名字段
        FieldDefinition tableNameField = new FieldDefinition();
        tableNameField.setName("table_name");
        tableNameField.setType("STRING");
        tableNameField.setLength(100);
        tableNameField.setNullable(false);
        tableNameField.setComment("表名称");
        fields.add(tableNameField);
        
        // 字段名字段
        FieldDefinition fieldNameField = new FieldDefinition();
        fieldNameField.setName("field_name");
        fieldNameField.setType("STRING");
        fieldNameField.setLength(100);
        fieldNameField.setComment("字段名称");
        fields.add(fieldNameField);
        
        // 字段类型字段
        FieldDefinition fieldTypeField = new FieldDefinition();
        fieldTypeField.setName("field_type");
        fieldTypeField.setType("STRING");
        fieldTypeField.setLength(50);
        fieldTypeField.setComment("字段类型");
        fields.add(fieldTypeField);
        
        // 字段长度字段
        FieldDefinition fieldLengthField = new FieldDefinition();
        fieldLengthField.setName("field_length");
        fieldLengthField.setType("INT");
        fieldLengthField.setComment("字段长度");
        fields.add(fieldLengthField);
        
        // 字段精度字段
        FieldDefinition fieldScaleField = new FieldDefinition();
        fieldScaleField.setName("field_scale");
        fieldScaleField.setType("INT");
        fieldScaleField.setComment("字段精度");
        fields.add(fieldScaleField);
        
        // 是否可空字段
        FieldDefinition isNullableField = new FieldDefinition();
        isNullableField.setName("is_nullable");
        isNullableField.setType("BOOLEAN");
        isNullableField.setComment("是否可空");
        fields.add(isNullableField);
        
        // 是否主键字段
        FieldDefinition isPrimaryKeyField = new FieldDefinition();
        isPrimaryKeyField.setName("is_primary_key");
        isPrimaryKeyField.setType("BOOLEAN");
        isPrimaryKeyField.setComment("是否主键");
        fields.add(isPrimaryKeyField);
        
        // 是否自增字段
        FieldDefinition isAutoIncrementField = new FieldDefinition();
        isAutoIncrementField.setName("is_auto_increment");
        isAutoIncrementField.setType("BOOLEAN");
        isAutoIncrementField.setComment("是否自增");
        fields.add(isAutoIncrementField);
        
        // 默认值字段
        FieldDefinition defaultValueField = new FieldDefinition();
        defaultValueField.setName("default_value");
        defaultValueField.setType("STRING");
        defaultValueField.setLength(255);
        defaultValueField.setComment("默认值");
        fields.add(defaultValueField);
        
        // 注释字段
        FieldDefinition commentField = new FieldDefinition();
        commentField.setName("comment");
        commentField.setType("STRING");
        commentField.setLength(500);
        commentField.setComment("注释");
        fields.add(commentField);
        
        // 索引名字段
        FieldDefinition indexNameField = new FieldDefinition();
        indexNameField.setName("index_name");
        indexNameField.setType("STRING");
        indexNameField.setLength(100);
        indexNameField.setComment("索引名称");
        fields.add(indexNameField);
        
        // 索引类型字段
        FieldDefinition indexTypeField = new FieldDefinition();
        indexTypeField.setName("index_type");
        indexTypeField.setType("STRING");
        indexTypeField.setLength(20);
        indexTypeField.setComment("索引类型");
        fields.add(indexTypeField);
        
        // 关系类型字段
        FieldDefinition relationTypeField = new FieldDefinition();
        relationTypeField.setName("relation_type");
        relationTypeField.setType("STRING");
        relationTypeField.setLength(20);
        relationTypeField.setComment("关系类型");
        fields.add(relationTypeField);
        
        // 引用表字段
        FieldDefinition referenceTableField = new FieldDefinition();
        referenceTableField.setName("reference_table");
        referenceTableField.setType("STRING");
        referenceTableField.setLength(100);
        referenceTableField.setComment("引用表");
        fields.add(referenceTableField);
        
        // 引用字段字段
        FieldDefinition referenceFieldField = new FieldDefinition();
        referenceFieldField.setName("reference_field");
        referenceFieldField.setType("STRING");
        referenceFieldField.setLength(100);
        referenceFieldField.setComment("引用字段");
        fields.add(referenceFieldField);
        
        // XML内容字段
        FieldDefinition xmlContentField = new FieldDefinition();
        xmlContentField.setName("xml_content");
        xmlContentField.setType("TEXT");
        xmlContentField.setComment("XML内容");
        fields.add(xmlContentField);
        
        // 创建时间字段
        FieldDefinition createdTimeField = new FieldDefinition();
        createdTimeField.setName("created_time");
        createdTimeField.setType("TIMESTAMP");
        createdTimeField.setNullable(false);
        createdTimeField.setDefaultValue("CURRENT_TIMESTAMP");
        createdTimeField.setComment("创建时间");
        fields.add(createdTimeField);
        
        // 更新时间字段
        FieldDefinition updatedTimeField = new FieldDefinition();
        updatedTimeField.setName("updated_time");
        updatedTimeField.setType("TIMESTAMP");
        updatedTimeField.setComment("更新时间");
        fields.add(updatedTimeField);
        
        table.setFields(fields);
        
        // 创建索引
        List<IndexDefinition> indexes = new ArrayList<>();
        
        // 模式名索引
        IndexDefinition schemaIndex = new IndexDefinition();
        schemaIndex.setName("idx_metadata_schema");
        schemaIndex.setType(IndexTypeEnum.NORMAL);
        List<IndexColumnDefinition> schemaColumns = new ArrayList<>();
        IndexColumnDefinition schemaColumn = new IndexColumnDefinition();
        schemaColumn.setName("schema_name");
        schemaColumns.add(schemaColumn);
        schemaIndex.setColumns(schemaColumns);
        indexes.add(schemaIndex);
        
        // 表名索引
        IndexDefinition tableIndex = new IndexDefinition();
        tableIndex.setName("idx_metadata_table");
        tableIndex.setType(IndexTypeEnum.NORMAL);
        List<IndexColumnDefinition> tableColumns = new ArrayList<>();
        IndexColumnDefinition schemaTableColumn1 = new IndexColumnDefinition();
        schemaTableColumn1.setName("schema_name");
        IndexColumnDefinition schemaTableColumn2 = new IndexColumnDefinition();
        schemaTableColumn2.setName("table_name");
        tableColumns.add(schemaTableColumn1);
        tableColumns.add(schemaTableColumn2);
        tableIndex.setColumns(tableColumns);
        indexes.add(tableIndex);
        
        table.setIndexes(indexes);
        
        return table;
    }

    /**
     * 检查模式是否存在
     */
    public boolean schemaExists(String schemaName) {
        QueryWrapper<MetadataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schema_name", schemaName);
        return searchService.exists(queryWrapper);
    }

    /**
     * 删除模式定义
     */
    @Transactional
    public void deleteSchemaDefinition(String schemaName) {
        logger.info("删除模式定义: {}", schemaName);
        QueryWrapper<MetadataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schema_name", schemaName);
        entityService.delete(queryWrapper);
    }
    
    /**
     * 根据模式名称删除元数据
     */
    @Transactional
    public void deleteBySchemaName(String schemaName) {
        QueryWrapper<MetadataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schema_name", schemaName);
        entityService.delete(queryWrapper);
    }
    
    /**
     * 根据模式名称查找元数据
     */
    public List<MetadataEntity> findBySchemaName(String schemaName) {
        QueryWrapper<MetadataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("schema_name", schemaName);
        return searchService.list(queryWrapper);
    }
    
    /**
     * 查找所有元数据
     */
    public List<MetadataEntity> findAllMetadata() {
        return searchService.list(new QueryWrapper<>());
    }
    
    /**
     * 从XML文件保存模式定义
     */
    @Transactional
    public void saveSchemaFromXml(File xmlFile) {
        logger.info("从XML文件保存模式定义: {}", xmlFile.getName());
        
        try {
            // 使用注入的XmlParserService，而不是手动创建
            DatabaseSchema schema = xmlParserService.parseFromFile(xmlFile);
            
            // 保存模式定义
            saveSchemaDefinition(schema);
            
            logger.info("XML文件模式定义保存完成: {}", schema.getName());
        } catch (Exception e) {
            logger.error("保存XML文件模式定义失败: {}", e.getMessage(), e);
            throw new RuntimeException("保存XML文件模式定义失败", e);
        }
    }

}