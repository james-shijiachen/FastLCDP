package cn.com.traninfo.fastlcdp.erdesigner.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 数据库元数据，用于保存XML中的元数据到数据库
 */
@Data
@TableName("datasource")
public class DataSourceEntity extends BaseEntity {

    @TableField("name")
    private String name;

    @TableField("charset")
    private String charset;

    @TableField("collation")
    private String collation;

    @TableField("engine")
    private String engine;

    @TableField("description")
    private String description;

}

