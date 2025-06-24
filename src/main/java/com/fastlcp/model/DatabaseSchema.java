package com.fastlcp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 数据库模式定义模型
 * 对应XML根元素，包含多个表定义
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "database")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseSchema {
    
    /**
     * 数据库名称
     */
    @XmlAttribute(name = "name")
    private String name;
    
    /**
     * 数据库版本
     */
    @XmlAttribute(name = "version")
    private String version;
    
    /**
     * 数据库字符集
     */
    @XmlAttribute(name = "charset")
    private String charset = "utf8mb4";
    
    /**
     * 数据库排序规则
     */
    @XmlAttribute(name = "collation")
    private String collation = "utf8mb4_unicode_ci";
    
    /**
     * 数据库默认存储引擎（MySQL专用）
     */
    @XmlAttribute(name = "engine")
    private String engine = "InnoDB";
    
    /**
     * 数据库注释
     */
    @XmlAttribute(name = "comment")
    private String comment;
    
    /**
     * 表定义列表
     */
    @XmlElementWrapper(name = "tables")
    @XmlElement(name = "table")
    private List<TableDefinition> tables = new ArrayList<>();
}