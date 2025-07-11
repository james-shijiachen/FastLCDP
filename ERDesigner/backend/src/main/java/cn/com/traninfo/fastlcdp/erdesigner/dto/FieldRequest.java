package cn.com.traninfo.fastlcdp.erdesigner.dto;

import lombok.Data;

@Data
public class FieldRequest {
    private String name;
    private String type;
    private Integer length;
    private Integer precision;
    private Integer scale;
    private Boolean nullable;
    private String primaryKey;
    private String defaultValue;
    private String comment;
    private Boolean unique;
    private String charset;
    private String collation;
} 