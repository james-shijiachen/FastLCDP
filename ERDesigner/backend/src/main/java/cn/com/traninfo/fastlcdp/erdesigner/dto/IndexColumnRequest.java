package cn.com.traninfo.fastlcdp.erdesigner.dto;

import lombok.Data;

@Data
public class IndexColumnRequest {
    private String name;
    private Integer length;
    private String order;
    private String comment;
} 