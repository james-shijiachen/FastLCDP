package cn.com.traninfo.fastlcdp.erdesigner.dto;

import lombok.Data;
import java.util.List;

@Data
public class TableRequest {
    private String name;
    private String type;
    private String comment;
    private String extendsTable;
    private String engine;
    private String charset;
    private List<FieldRequest> fields;
    private List<IndexRequest> indexes;
    private List<RelationRequest> relations;
} 