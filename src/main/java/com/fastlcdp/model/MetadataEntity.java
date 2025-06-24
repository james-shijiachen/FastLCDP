package com.fastlcdp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 元数据实体类，用于保存XML中的元数据到数据库
 */
@Entity
@Table(name = "metadata")
public class MetadataEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "schema_name", nullable = false, length = 100)
    private String schemaName;
    
    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;
    
    @Column(name = "field_name", length = 100)
    private String fieldName;
    
    @Column(name = "field_type", length = 50)
    private String fieldType;
    
    @Column(name = "field_length")
    private Integer fieldLength;
    
    @Column(name = "field_scale")
    private Integer fieldScale;
    
    @Column(name = "is_nullable")
    private Boolean isNullable;
    
    @Column(name = "is_primary_key")
    private Boolean isPrimaryKey;
    
    @Column(name = "is_auto_increment")
    private Boolean isAutoIncrement;
    
    @Column(name = "default_value", length = 255)
    private String defaultValue;
    
    @Column(name = "comment", length = 500)
    private String comment;
    
    @Column(name = "index_name", length = 100)
    private String indexName;
    
    @Column(name = "index_type", length = 20)
    private String indexType;
    
    @Column(name = "relation_type", length = 50)
    private String relationType;
    
    @Column(name = "reference_table", length = 100)
    private String referenceTable;
    
    @Column(name = "reference_field", length = 100)
    private String referenceField;
    
    @Column(name = "xml_content", columnDefinition = "TEXT")
    private String xmlContent;
    
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    // 构造函数
    public MetadataEntity() {
        this.createdTime = LocalDateTime.now();
    }
    
    public MetadataEntity(String schemaName, String tableName) {
        this();
        this.schemaName = schemaName;
        this.tableName = tableName;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSchemaName() {
        return schemaName;
    }
    
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
    
    public Integer getFieldLength() {
        return fieldLength;
    }
    
    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }
    
    public Integer getFieldScale() {
        return fieldScale;
    }
    
    public void setFieldScale(Integer fieldScale) {
        this.fieldScale = fieldScale;
    }
    
    public Boolean getIsNullable() {
        return isNullable;
    }
    
    public void setIsNullable(Boolean isNullable) {
        this.isNullable = isNullable;
    }
    
    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }
    
    public void setIsPrimaryKey(Boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }
    
    public Boolean getIsAutoIncrement() {
        return isAutoIncrement;
    }
    
    public void setIsAutoIncrement(Boolean isAutoIncrement) {
        this.isAutoIncrement = isAutoIncrement;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getIndexName() {
        return indexName;
    }
    
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
    
    public String getIndexType() {
        return indexType;
    }
    
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }
    
    public String getRelationType() {
        return relationType;
    }
    
    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }
    
    public String getReferenceTable() {
        return referenceTable;
    }
    
    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }
    
    public String getReferenceField() {
        return referenceField;
    }
    
    public void setReferenceField(String referenceField) {
        this.referenceField = referenceField;
    }
    
    public String getXmlContent() {
        return xmlContent;
    }
    
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }
    
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
    
    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "MetadataEntity{" +
                "id=" + id +
                ", schemaName='" + schemaName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}