package cn.com.traninfo.fastlcdp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据库元数据，用于保存XML中的元数据到数据库
 */
@Data
@TableName("database_schema")
public class DatabaseSchemaEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("version")
    private String version;

    @TableField("charset")
    private String charset;

    @TableField("collation")
    private String collation;

    @TableField("engine")
    private String engine;

    @TableField("comment")
    private String comment;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

}

