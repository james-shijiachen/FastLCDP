package cn.com.traninfo.fastlcdp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;

/**
 * 关联关系定义模型
 * 对应XML中的relation元素
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class RelationDefinition {
    
    /**
     * 外键名称
     */
    @XmlAttribute(name = "name")
    private String name;
    
    /**
     * 本表字段名
     */
    @XmlAttribute(name = "column")
    private String column;
    
    /**
     * 引用的表名
     */
    @XmlAttribute(name = "referenceTable")
    private String referenceTable;
    
    /**
     * 引用的字段名
     */
    @XmlAttribute(name = "referenceColumn")
    private String referenceColumn;
    
    /**
     * 删除时的动作：CASCADE, SET_NULL, RESTRICT, NO_ACTION
     */
    @XmlAttribute(name = "onDelete")
    private String onDelete = "RESTRICT";
    
    /**
     * 更新时的动作：CASCADE, SET_NULL, RESTRICT, NO_ACTION
     */
    @XmlAttribute(name = "onUpdate")
    private String onUpdate = "RESTRICT";
    
    /**
     * 关联类型：ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY
     */
    @XmlAttribute(name = "type")
    private String type = "MANY_TO_ONE";
    
    /**
     * 关联注释
     */
    @XmlAttribute(name = "comment")
    private String comment;
}