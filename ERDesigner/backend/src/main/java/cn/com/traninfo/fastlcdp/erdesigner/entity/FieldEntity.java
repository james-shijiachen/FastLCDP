package cn.com.traninfo.fastlcdp.erdesigner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 字段元数据，用于保存XML中的元数据到数据库
 */
@Data
@TableName("field")
public class FieldEntity extends BaseEntity {

    @TableField("table_id")
    private Long tableId;

    @TableField("name")
    private String name;

    @TableField("type")
    private String type;

    @TableField("length")
    private Integer length;

    @TableField("scale")
    private Integer scale;

    @TableField("nullable")
    private Boolean nullable;

    @TableField("default_value")
    private String defaultValue;

    @TableField("comment")
    private String comment;

}
