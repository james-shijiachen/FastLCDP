package cn.com.traninfo.fastlcdp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;

/**
 * 字段定义模型
 * 对应XML中的field元素
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldDefinition {
    
    /**
     * 字段名
     */
    @XmlAttribute(name = "name")
    private String name;
    
    /**
     * 字段类型
     */
    @XmlAttribute(name = "type")
    private String type;
    
    /**
     * 字段长度
     */
    @XmlAttribute(name = "length")
    private Integer length;
    
    /**
     * 精度（用于decimal类型的总位数）
     */
    @XmlAttribute(name = "precision")
    private Integer precision;
    
    /**
     * 小数位数（用于decimal类型）
     */
    @XmlAttribute(name = "scale")
    private Integer scale;
    
    /**
     * 是否允许为空
     */
    @XmlAttribute(name = "nullable")
    private Boolean nullable = true;
    
    /**
     * 是否为主键
     */
    @XmlAttribute(name = "primaryKey")
    private Boolean primaryKey = false;
    
    /**
     * 是否自增
     */
    @XmlAttribute(name = "autoIncrement")
    private Boolean autoIncrement = false;
    
    /**
     * 默认值
     */
    @XmlAttribute(name = "defaultValue")
    private String defaultValue;
    
    /**
     * 字段注释
     */
    @XmlAttribute(name = "comment")
    private String comment;
    
    /**
     * 是否唯一
     */
    @XmlAttribute(name = "unique")
    private Boolean unique = false;
    
    /**
     * 字符集（用于字符串类型）
     */
    @XmlAttribute(name = "charset")
    private String charset;
    
    /**
     * 排序规则（用于字符串类型）
     */
    @XmlAttribute(name = "collation")
    private String collation;
}