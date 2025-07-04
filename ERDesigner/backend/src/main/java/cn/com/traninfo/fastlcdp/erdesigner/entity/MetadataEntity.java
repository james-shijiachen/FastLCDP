package cn.com.traninfo.fastlcdp.erdesigner.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 元数据实体类，用于保存XML中的元数据到数据库
 */
@Data
@TableName("metadata")
public class MetadataEntity extends BaseEntity {

    @TableField("schema_name")
    private String schemaName;
    
    @TableField("table_name")
    private String tableName;
    
    @TableField("field_name")
    private String fieldName;
    
    @TableField("field_type")
    private String fieldType;
    
    @TableField("field_length")
    private Integer fieldLength;
    
    @TableField("field_scale")
    private Integer fieldScale;
    
    @TableField("is_nullable")
    private Boolean isNullable;
    
    @TableField("primary_key_type")
    private String primaryKeyType;
    
    @TableField("default_value")
    private String defaultValue;
    
    @TableField("comment")
    private String comment;
    
    @TableField("index_name")
    private String indexName;
    
    @TableField("index_type")
    private String indexType;
    
    @TableField("relation_type")
    private String relationType;
    
    @TableField("reference_table")
    private String referenceTable;
    
    @TableField("reference_field")
    private String referenceField;
    
    @TableField("xml_content")
    private String xmlContent;

    // 构造函数
    public MetadataEntity() {
        this.setCreatedTime(LocalDateTime.now());
    }

    public MetadataEntity(String schemaName, String tableName) {
        this();
        this.schemaName = schemaName;
        this.tableName = tableName;
    }
}