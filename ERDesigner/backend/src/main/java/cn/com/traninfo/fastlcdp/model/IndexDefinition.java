package cn.com.traninfo.fastlcdp.model;

import cn.com.traninfo.fastlcdp.enums.IndexMethodEnum;
import cn.com.traninfo.fastlcdp.enums.IndexTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 索引定义模型
 * 对应XML中的index元素
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class IndexDefinition {
    
    /**
     * 索引名称
     */
    @XmlAttribute(name = "name")
    private String name;
    
    /**
     * 索引类型：NORMAL, UNIQUE, FULLTEXT, SPATIAL
     */
    @XmlAttribute(name = "type")
    private IndexTypeEnum type = IndexTypeEnum.NORMAL;
    
    /**
     * 索引方法：BTREE, HASH
     */
    @XmlAttribute(name = "method")
    private IndexMethodEnum method = IndexMethodEnum.BTREE;
    
    /**
     * 注释
     */
    @XmlAttribute(name = "comment")
    private String comment;

    /**
     * 索引字段列表
     */
    @XmlElement(name = "column")
    private List<IndexColumnDefinition> columns = new ArrayList<>();

}