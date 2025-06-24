package cn.com.traninfo.fastlcdp.model;

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
    private String type = "NORMAL";
    
    /**
     * 索引方法：BTREE, HASH
     */
    @XmlAttribute(name = "method")
    private String method = "BTREE";
    
    /**
     * 注释
     */
    @XmlAttribute(name = "comment")
    private String comment;
    
    /**
     * 获取索引字段列表（合并两种格式）
     */
    public List<IndexColumnDefinition> getColumns() {
        if (!columns.isEmpty()) {
            return columns;
        }
        return nestedColumns;
    }
    
    /**
     * 设置索引字段列表
     */
    public void setColumns(List<IndexColumnDefinition> columns) {
        this.columns = columns;
    }
    
    /**
     * 索引字段列表
     */
    @XmlElement(name = "column")
    private List<IndexColumnDefinition> columns = new ArrayList<>();
    
    /**
     * 索引字段列表（嵌套格式）
     */
    @XmlElementWrapper(name = "columns")
    @XmlElement(name = "column")
    private List<IndexColumnDefinition> nestedColumns = new ArrayList<>();
    
    /**
     * 索引字段定义
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class IndexColumnDefinition {
        
        /**
         * 字段名
         */
        @XmlAttribute(name = "name")
        private String name;
        
        /**
         * 排序方向：ASC, DESC
         */
        @XmlAttribute(name = "order")
        private String order = "ASC";
        
        /**
         * 索引长度（用于字符串字段的前缀索引）
         */
        @XmlAttribute(name = "length")
        private Integer length;
    }
}