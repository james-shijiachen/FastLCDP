package cn.com.traninfo.fastlcdp.enums;

/**
 * 主键类型枚举
 * 定义字段的主键类型
 */
public enum PrimaryKeyTypeEnum {
    /**
     * 非主键字段（默认）
     */
    NONE,
    
    /**
     * 单字段主键
     */
    SINGLE,
    
    /**
     * 复合主键的组成字段
     */
    COMPOSITE,
    
    /**
     * 自动增长主键
     */
    AUTO_INCREMENT,
    
    /**
     * UUID主键
     */
    UUID,
    
    /**
     * 序列主键（主要用于Oracle等数据库）
     */
    SEQUENCE
}