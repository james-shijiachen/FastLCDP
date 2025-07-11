package cn.com.traninfo.fastlcdp.erdesigner.dto;

import lombok.Data;

@Data
public class RelationRequest {
    private String name;
    private String column;
    private String referenceTable;
    private String referenceColumn;
    private String onDelete;
    private String onUpdate;
    private String type;
    private String comment;
} 