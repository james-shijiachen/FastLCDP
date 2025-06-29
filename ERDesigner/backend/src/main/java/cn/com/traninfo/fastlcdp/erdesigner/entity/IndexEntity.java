package cn.com.traninfo.fastlcdp.erdesigner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 索引元数据，用于保存XML中的元数据到数据库
 */
@Data
@TableName("index")
public class IndexEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    @TableField("name")
    private String name;
    
    @TableField("type")
    private String type;
    
    @TableField("method")
    private String method;
    
    @TableField("unique_index")
    private Boolean uniqueIndex;
    
    @TableField("comment")
    private String comment;
    
    @TableField("table_id")
    private Long tableId;
}
