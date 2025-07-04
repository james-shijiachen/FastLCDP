package cn.com.traninfo.fastlcdp.erdesigner.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据库元数据，用于保存XML中的元数据到数据库
 */
@Data
@TableName("database_schema")
public class DatabaseSchemaEntity extends BaseEntity {

    @TableField("name")
    private String name;

    @TableField("charset")
    private String charset;

    @TableField("collation")
    private String collation;

    @TableField("engine")
    private String engine;

    @TableField("comment")
    private String comment;



}

