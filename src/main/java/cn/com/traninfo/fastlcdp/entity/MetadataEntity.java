package cn.com.traninfo.fastlcdp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 元数据实体类，用于保存XML中的元数据到数据库
 */
@Entity
@Table(name = "metadata")
@Getter
@Setter
@ToString
public class MetadataEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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
    
    @Column(name = "primary_key_type")
    private String primaryKeyType;
    
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
}