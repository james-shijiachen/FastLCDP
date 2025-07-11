package cn.com.traninfo.fastlcdp.erdesigner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import cn.com.traninfo.fastlcdp.erdesigner.service.TableGeneratorService;
import cn.com.traninfo.fastlcdp.erdesigner.service.DatabaseExecutorService;
import cn.com.traninfo.fastlcdp.erdesigner.service.SqlGeneratorService;
import cn.com.traninfo.fastlcdp.erdesigner.service.MetadataService;
import cn.com.traninfo.fastlcdp.erdesigner.model.TableDefinition;
import cn.com.traninfo.fastlcdp.erdesigner.model.DatabaseSchema;
import cn.com.traninfo.fastlcdp.erdesigner.dto.TableRequest;
import cn.com.traninfo.fastlcdp.erdesigner.dto.FieldRequest;
import cn.com.traninfo.fastlcdp.erdesigner.dto.IndexRequest;
import cn.com.traninfo.fastlcdp.erdesigner.dto.IndexColumnRequest;
import cn.com.traninfo.fastlcdp.erdesigner.dto.RelationRequest;

/**
 * 数据库管理相关API
 * 包含建表、字段、索引、关联、数据库、导入导出、查表、树结构等操作
 */
@Tag(name = "Database Management", description = "${api.database.management.tag.description}")
@RestController
@RequestMapping("/api/database-management")
public class DatabaseManagementController {

    @Autowired
    private TableGeneratorService tableGeneratorService;
    @Autowired
    private DatabaseExecutorService databaseExecutorService;
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    @Autowired
    private MetadataService metadataService;

    /**
     * 创建表（字段、索引、关联一并创建）
     */
    @Operation(summary = "${api.table.create.summary}", description = "${api.table.create.desc}")
    @PostMapping("/table/create")
    public ResponseEntity<?> createTable(
            @Parameter(description = "${api.table.create.param.tableDefinition}")
            @RequestBody TableRequest tableRequest) {
        // DTO 转 entity
        TableDefinition table = convertToTableDefinition(tableRequest);
        boolean success = databaseExecutorService.createTable(table);
        return ResponseEntity.ok(Map.of("success", success));
    }

    // DTO 转 entity 工具方法
    private TableDefinition convertToTableDefinition(TableRequest dto) {
        TableDefinition entity = new TableDefinition();
        entity.setName(dto.getName());
        entity.setType(dto.getType() != null ? cn.com.traninfo.fastlcdp.erdesigner.enums.TableTypeEnum.valueOf(dto.getType()) : null);
        entity.setComment(dto.getComment());
        entity.setExtendsTable(dto.getExtendsTable());
        entity.setEngine(dto.getEngine());
        entity.setCharset(dto.getCharset());
        // 字段
        if (dto.getFields() != null) {
            var fields = new java.util.ArrayList<cn.com.traninfo.fastlcdp.erdesigner.model.FieldDefinition>();
            for (FieldRequest f : dto.getFields()) {
                var field = new cn.com.traninfo.fastlcdp.erdesigner.model.FieldDefinition();
                field.setName(f.getName());
                field.setType(f.getType());
                field.setLength(f.getLength());
                field.setPrecision(f.getPrecision());
                field.setScale(f.getScale());
                field.setNullable(f.getNullable());
                if (f.getPrimaryKey() != null)
                    field.setPrimaryKey(cn.com.traninfo.fastlcdp.erdesigner.enums.PrimaryKeyTypeEnum.valueOf(f.getPrimaryKey()));
                field.setDefaultValue(f.getDefaultValue());
                field.setComment(f.getComment());
                field.setUnique(f.getUnique());
                field.setCharset(f.getCharset());
                field.setCollation(f.getCollation());
                fields.add(field);
            }
            entity.setFields(fields);
        }
        // 索引
        if (dto.getIndexes() != null) {
            var indexes = new java.util.ArrayList<cn.com.traninfo.fastlcdp.erdesigner.model.IndexDefinition>();
            for (IndexRequest idx : dto.getIndexes()) {
                var index = new cn.com.traninfo.fastlcdp.erdesigner.model.IndexDefinition();
                index.setName(idx.getName());
                if (idx.getType() != null)
                    index.setType(cn.com.traninfo.fastlcdp.erdesigner.enums.IndexTypeEnum.valueOf(idx.getType()));
                if (idx.getMethod() != null)
                    index.setMethod(cn.com.traninfo.fastlcdp.erdesigner.enums.IndexMethodEnum.valueOf(idx.getMethod()));
                index.setComment(idx.getComment());
                if (idx.getColumns() != null) {
                    var columns = new java.util.ArrayList<cn.com.traninfo.fastlcdp.erdesigner.model.IndexColumnDefinition>();
                    for (IndexColumnRequest c : idx.getColumns()) {
                        var col = new cn.com.traninfo.fastlcdp.erdesigner.model.IndexColumnDefinition();
                        col.setName(c.getName());
                        col.setLength(c.getLength());
                        if (c.getOrder() != null)
                            col.setOrder(cn.com.traninfo.fastlcdp.erdesigner.enums.IndexSortOrderEnum.valueOf(c.getOrder()));
                        col.setComment(c.getComment());
                        columns.add(col);
                    }
                    index.setColumns(columns);
                }
                indexes.add(index);
            }
            entity.setIndexes(indexes);
        }
        // 关联
        if (dto.getRelations() != null) {
            var relations = new java.util.ArrayList<cn.com.traninfo.fastlcdp.erdesigner.model.RelationDefinition>();
            for (RelationRequest r : dto.getRelations()) {
                var rel = new cn.com.traninfo.fastlcdp.erdesigner.model.RelationDefinition();
                rel.setName(r.getName());
                rel.setColumn(r.getColumn());
                rel.setReferenceTable(r.getReferenceTable());
                rel.setReferenceColumn(r.getReferenceColumn());
                if (r.getOnDelete() != null)
                    rel.setOnDelete(cn.com.traninfo.fastlcdp.erdesigner.enums.RelationActionEnum.valueOf(r.getOnDelete()));
                if (r.getOnUpdate() != null)
                    rel.setOnUpdate(cn.com.traninfo.fastlcdp.erdesigner.enums.RelationActionEnum.valueOf(r.getOnUpdate()));
                if (r.getType() != null)
                    rel.setType(cn.com.traninfo.fastlcdp.erdesigner.enums.RelationTypeEnum.valueOf(r.getType()));
                rel.setComment(r.getComment());
                relations.add(rel);
            }
            entity.setRelations(relations);
        }
        return entity;
    }

    /**
     * 单独创建字段
     */
    @Operation(summary = "${api.field.create.summary}", description = "${api.field.create.desc}")
    @PostMapping("/field/create")
    public ResponseEntity<?> createField(
            @Parameter(description = "${api.field.create.param.fieldDefinition}")
            @RequestBody Map<String, Object> fieldDefinition) {
        // TODO: 解析 fieldDefinition，找到表名，加载表结构，添加字段，重新建表
        // ...
        return ResponseEntity.ok().build();
    }

    /**
     * 修改字段
     */
    @Operation(summary = "${api.field.update.summary}", description = "${api.field.update.desc}")
    @PutMapping("/field/update")
    public ResponseEntity<?> updateField(
            @Parameter(description = "${api.field.update.param.fieldUpdate}")
            @RequestBody Map<String, Object> fieldUpdate) {
        // TODO: 解析 fieldUpdate，找到表名和字段，修改表结构，重新建表
        // ...
        return ResponseEntity.ok().build();
    }

    /**
     * 修改表
     */
    @Operation(summary = "${api.table.update.summary}", description = "${api.table.update.desc}")
    @PutMapping("/table/update")
    public ResponseEntity<?> updateTable(
            @Parameter(description = "${api.table.update.param.tableUpdate}")
            @RequestBody Map<String, Object> tableUpdate) {
        // TODO: 解析 tableUpdate，找到表名，修改表结构，重新建表
        // ...
        return ResponseEntity.ok().build();
    }

    /**
     * 创建数据库
     */
    @Operation(summary = "${api.database.create.summary}", description = "${api.database.create.desc}")
    @PostMapping("/database/create")
    public ResponseEntity<?> createDatabase(
            @Parameter(description = "${api.database.create.param.dbDefinition}")
            @RequestBody Map<String, Object> dbDefinition) {
        // TODO: Map 转 DatabaseSchema
        DatabaseSchema schema = new DatabaseSchema();
        // ...
        boolean success = databaseExecutorService.createDatabaseSchema(schema);
        return ResponseEntity.ok(Map.of("success", success));
    }

    /**
     * 创建关联
     */
    @Operation(summary = "${api.relation.create.summary}", description = "${api.relation.create.desc}")
    @PostMapping("/relation/create")
    public ResponseEntity<?> createRelation(
            @Parameter(description = "${api.relation.create.param.relationDefinition}")
            @RequestBody Map<String, Object> relationDefinition) {
        // TODO: 解析 relationDefinition，找到表名，添加关联，重新建表
        // ...
        return ResponseEntity.ok().build();
    }

    /**
     * 创建索引
     */
    @Operation(summary = "${api.index.create.summary}", description = "${api.index.create.desc}")
    @PostMapping("/index/create")
    public ResponseEntity<?> createIndex(
            @Parameter(description = "${api.index.create.param.indexDefinition}")
            @RequestBody Map<String, Object> indexDefinition) {
        // TODO: 解析 indexDefinition，找到表名，添加索引，重新建表
        // ...
        return ResponseEntity.ok().build();
    }

    /**
     * 导入数据库结构（如XML/SQL等）
     */
    @Operation(summary = "${api.import.summary}", description = "${api.import.desc}")
    @PostMapping("/import")
    public ResponseEntity<?> importDatabase(
            @Parameter(description = "${api.import.param.file}")
            @RequestParam("file") MultipartFile file) {
        // 解析XML文件并生成数据库表
        try {
            File tempFile = File.createTempFile("import_db_", ".xml");
            file.transferTo(tempFile);
            TableGeneratorService.GenerationResult result = tableGeneratorService.generateFromFile(tempFile);
            tempFile.delete();
            return ResponseEntity.ok(Map.of(
                "success", result.isSuccess(),
                "message", result.getMessage(),
                "databaseName", result.getSchema() != null ? result.getSchema().getName() : null
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 导出数据库结构
     */
    @Operation(summary = "${api.export.summary}", description = "${api.export.desc}")
    @GetMapping("/export")
    public ResponseEntity<?> exportDatabase(
            @Parameter(description = "${api.export.param.dbName}")
            @RequestParam String dbName) {
        // 从元数据生成SQL导出
        try {
            String sql = sqlGeneratorService.generateSqlFromMetadata(dbName);
            return ResponseEntity.ok(Map.of("success", true, "sql", sql));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * 查表（支持条件查询）
     */
    @Operation(summary = "${api.table.list.summary}", description = "${api.table.list.desc}")
    @GetMapping("/table/list")
    public ResponseEntity<List<Map<String, Object>>> listTables(
            @Parameter(description = "${api.table.list.param.dbName}")
            @RequestParam(required = false) String dbName) {
        // 查询数据库下所有表
        // TODO: 这里假设用元数据服务
        if (dbName == null) {
            return ResponseEntity.badRequest().build();
        }
        DatabaseSchema schema = metadataService.getSchemaDefinition(dbName);
        List<Map<String, Object>> tables = new java.util.ArrayList<>();
        if (schema != null && schema.getTables() != null) {
            for (var table : schema.getTables()) {
                Map<String, Object> t = new java.util.HashMap<>();
                t.put("name", table.getName());
                t.put("comment", table.getComment());
                t.put("fields", table.getFields());
                t.put("indexes", table.getIndexes());
                t.put("relations", table.getRelations());
                tables.add(t);
            }
        }
        return ResponseEntity.ok(tables);
    }

    /**
     * 返回数据库-表-字段树形结构（JSON）
     */
    @Operation(summary = "${api.tree.summary}", description = "${api.tree.desc}")
    @GetMapping("/tree")
    public ResponseEntity<?> getDatabaseTableTree() {
        // 返回数据库-表-字段树形结构
        List<Map<String, Object>> tree = new java.util.ArrayList<>();
        // TODO: 遍历所有数据库和表，组装树结构
        // 可用 metadataService.findAllMetadata()
        // ...
        return ResponseEntity.ok(tree);
    }
} 