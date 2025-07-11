package cn.com.traninfo.fastlcdp.erdesigner.dto;

import lombok.Data;
import java.util.List;

@Data
public class IndexRequest {
    private String name;
    private String type;
    private String method;
    private String comment;
    private List<IndexColumnRequest> columns;
} 