package cn.com.traninfo.fastlcdp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;

/**
 * 索引列定义模型
 * 对应XML中的column元素
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class IndexColumnDefinition {
    
    /**
     * 列名
     */
    @XmlAttribute(name = "name")
    private String name;
    
    /**
     * 索引长度（用于前缀索引）
     */
    @XmlAttribute(name = "length")
    private Integer length;
    
    /**
     * 排序方式：ASC, DESC
     */
    @XmlAttribute(name = "order")
    private String order = "ASC";
    
    /**
     * 列注释
     */
    @XmlAttribute(name = "comment")
    private String comment;
}