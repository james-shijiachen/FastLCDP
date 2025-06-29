package cn.com.traninfo.fastlcdp.enums;

public enum IndexTypeEnum {

    /**
     * 主键索引/约束
     */
    PRIMARY,

    /**
     * 一般索引（默认）
     */
    NORMAL,

    /**
     * 唯一索引
     */
    UNIQUE,

    /**
     * 全文索引
     */
    FULLTEXT,

    /**
     * 空间索引
     */
    SPATIAL

}
