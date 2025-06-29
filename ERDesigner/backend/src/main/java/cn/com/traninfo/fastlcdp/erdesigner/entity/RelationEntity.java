package cn.com.traninfo.fastlcdp.erdesigner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 关系元数据，用于保存XML中的元数据到数据库
 */
@Data
@TableName("relation")
public class RelationEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    @TableField("name")
    private String name;
    
    @TableField("type")
    private String type;
    
    @TableField("reference_table")
    private String referenceTable;
    
    @TableField("reference_field")
    private String referenceField;
    
    @TableField("on_delete")
    private String onDelete;
    
    @TableField("on_update")
    private String onUpdate;
    
    @TableField("comment")
    private String comment;
    
    @TableField("table_id")
    private Long tableId;
}
