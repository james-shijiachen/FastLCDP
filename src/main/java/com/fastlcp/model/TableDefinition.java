package com.fastlcp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 表定义模型
 * 对应XML中的table元素
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "table")
@XmlAccessorType(XmlAccessType.FIELD)
public class TableDefinition {
    
    /**
     * 表名
     */
    @XmlAttribute(name = "name")
    private String name;
    
    /**
     * 表注释
     */
    @XmlAttribute(name = "comment")
    private String comment;
    
    /**
     * 继承的父表名
     */
    @XmlAttribute(name = "extends")
    private String extendsTable;
    
    /**
     * 表引擎类型（MySQL专用）
     * 如果未设置，将继承数据库级别的默认engine
     */
    @XmlAttribute(name = "engine")
    private String engine;
    
    /**
     * 字符集
     * 如果未设置，将继承数据库级别的默认charset
     */
    @XmlAttribute(name = "charset")
    private String charset;
    
    /**
     * 字段列表
     */
    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private List<FieldDefinition> fields = new ArrayList<>();
    
    /**
     * 索引列表
     */
    @XmlElementWrapper(name = "indexes")
    @XmlElement(name = "index")
    private List<IndexDefinition> indexes = new ArrayList<>();
    
    /**
     * 外键关联列表
     */
    @XmlElementWrapper(name = "relations")
    @XmlElement(name = "relation")
    private List<RelationDefinition> relations = new ArrayList<>();
}