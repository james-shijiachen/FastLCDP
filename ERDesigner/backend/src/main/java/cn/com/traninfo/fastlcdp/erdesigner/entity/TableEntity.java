package cn.com.traninfo.fastlcdp.erdesigner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 表元数据，用于保存XML中的元数据到数据库
 */
@Data
@TableName("table")
public class TableEntity extends BaseEntity {

    @TableField("database_id")
    private Long databaseId;

    @TableField("extends_table")
    private Long extendsTableId;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("engine")
    private String engine;

    @TableField("charset")
    private String charset;

    @TableField("comment")
    private String comment;

}
