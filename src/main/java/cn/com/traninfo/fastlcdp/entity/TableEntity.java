package cn.com.traninfo.fastlcdp.entity;

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
public class TableEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("comment")
    private String comment;

    @TableField("extends_table")
    private Long extendsTableId;

    @TableField("engine")
    private String engine;

    @TableField("charset")
    private String charset;

    @TableField("database_id")
    private Long databaseId;

}
