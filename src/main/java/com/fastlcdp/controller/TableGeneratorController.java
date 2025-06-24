package com.fastlcdp.controller;

import com.fastlcdp.config.DatabaseConfig;
import com.fastlcdp.model.MetadataEntity;
import com.fastlcdp.service.MetadataService;
import com.fastlcdp.service.SqlGeneratorService;
import com.fastlcdp.service.TableGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表生成器控制器
 * 提供REST API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/table-generator")
@CrossOrigin(origins = "*")
public class TableGeneratorController {
    
    @Autowired
    private TableGeneratorService tableGeneratorService;
    
    @Autowired
    private SqlGeneratorService sqlGeneratorService;
    
    @Autowired
    private MetadataService metadataService;
    
    @Autowired
    private DatabaseConfig databaseConfig;
    
    /**
     * 上传XML文件并生成数据库表
     * 
     * @param file XML文件
     * @return 生成结果
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateTables(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择XML文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
                response.put("success", false);
                response.put("message", "请上传XML格式的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 保存临时文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path tempFile = tempDir.resolve("table_definition_" + System.currentTimeMillis() + ".xml");
            Files.copy(file.getInputStream(), tempFile);
            
            try {
                // 生成数据库表
                TableGeneratorService.GenerationResult result = tableGeneratorService.generateFromFile(tempFile.toFile());
                
                response.put("success", result.isSuccess());
                response.put("message", result.getMessage());
                
                if (result.getSchema() != null) {
                    response.put("databaseName", result.getSchema().getName());
                    response.put("tableCount", result.getSchema().getTables().size());
                }
                
                return ResponseEntity.ok(response);
                
            } finally {
                // 清理临时文件
                Files.deleteIfExists(tempFile);
            }
            
        } catch (IOException e) {
            log.error("文件处理失败", e);
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("生成数据库表失败", e);
            response.put("success", false);
            response.put("message", "生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 预览SQL语句
     * 
     * @param file XML文件
     * @return SQL预览结果
     */
    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> previewSql(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择XML文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
                response.put("success", false);
                response.put("message", "请上传XML格式的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 保存临时文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path tempFile = tempDir.resolve("preview_" + System.currentTimeMillis() + ".xml");
            Files.copy(file.getInputStream(), tempFile);
            
            try {
                // 预览SQL
                TableGeneratorService.SqlPreviewResult result = tableGeneratorService.previewSql(tempFile.toFile());
                
                response.put("success", result.isSuccess());
                response.put("message", result.getMessage());
                response.put("sql", result.getSql());
                
                if (result.getSchema() != null) {
                    response.put("databaseName", result.getSchema().getName());
                    response.put("tableCount", result.getSchema().getTables().size());
                }
                
                return ResponseEntity.ok(response);
                
            } finally {
                // 清理临时文件
                Files.deleteIfExists(tempFile);
            }
            
        } catch (IOException e) {
            log.error("文件处理失败", e);
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("SQL预览失败", e);
            response.put("success", false);
            response.put("message", "预览失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 验证XML文件格式
     * 
     * @param file XML文件
     * @return 验证结果
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateXml(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("valid", false);
                response.put("message", "请选择XML文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
                response.put("valid", false);
                response.put("message", "请上传XML格式的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 保存临时文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path tempFile = tempDir.resolve("validate_" + System.currentTimeMillis() + ".xml");
            Files.copy(file.getInputStream(), tempFile);
            
            try {
                // 验证XML
                TableGeneratorService.ValidationResult result = tableGeneratorService.validateXml(tempFile.toFile());
                
                response.put("valid", result.isValid());
                response.put("message", result.getMessage());
                
                if (result.getSchema() != null) {
                    response.put("databaseName", result.getSchema().getName());
                    response.put("tableCount", result.getSchema().getTables().size());
                }
                
                return ResponseEntity.ok(response);
                
            } finally {
                // 清理临时文件
                Files.deleteIfExists(tempFile);
            }
            
        } catch (IOException e) {
            log.error("文件处理失败", e);
            response.put("valid", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("XML验证失败", e);
            response.put("valid", false);
            response.put("message", "验证失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取系统状态
     * 
     * @return 系统状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("status", "running");
            response.put("message", "XML表生成器服务正常运行");
            response.put("timestamp", System.currentTimeMillis());
            response.put("databaseType", databaseConfig.getType());
            response.put("metadataStorageEnabled", databaseConfig.isMetadataStorageEnabled());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取系统状态失败", e);
            response.put("status", "error");
            response.put("message", "系统异常: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 保存XML元数据到数据库
     * 
     * @param file XML文件
     * @return 保存结果
     */
    @PostMapping("/metadata/save")
    public ResponseEntity<Map<String, Object>> saveMetadata(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择XML文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
                response.put("success", false);
                response.put("message", "请上传XML格式的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 保存临时文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path tempFile = tempDir.resolve("metadata_" + System.currentTimeMillis() + ".xml");
            Files.copy(file.getInputStream(), tempFile);
            
            try {
                // 保存元数据
                metadataService.saveSchemaFromXml(tempFile.toFile());
                
                response.put("success", true);
                response.put("message", "元数据保存成功");
                
                return ResponseEntity.ok(response);
                
            } finally {
                // 清理临时文件
                Files.deleteIfExists(tempFile);
            }
            
        } catch (IOException e) {
            log.error("文件处理失败", e);
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("保存元数据失败", e);
            response.put("success", false);
            response.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 查询所有元数据
     * 
     * @return 元数据列表
     */
    @GetMapping("/metadata/list")
    public ResponseEntity<Map<String, Object>> listMetadata() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<MetadataEntity> metadataList = metadataService.findAllMetadata();
            
            response.put("success", true);
            response.put("data", metadataList);
            response.put("count", metadataList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("查询元数据失败", e);
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 根据模式名查询元数据
     * 
     * @param schemaName 模式名
     * @return 元数据列表
     */
    @GetMapping("/metadata/schema/{schemaName}")
    public ResponseEntity<Map<String, Object>> getMetadataBySchema(@PathVariable String schemaName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<MetadataEntity> metadataList = metadataService.findBySchemaName(schemaName);
            
            response.put("success", true);
            response.put("data", metadataList);
            response.put("count", metadataList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("查询元数据失败", e);
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 删除元数据
     * 
     * @param schemaName 模式名
     * @return 删除结果
     */
    @DeleteMapping("/metadata/schema/{schemaName}")
    public ResponseEntity<Map<String, Object>> deleteMetadata(@PathVariable String schemaName) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            metadataService.deleteBySchemaName(schemaName);
            
            response.put("success", true);
            response.put("message", "元数据删除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("删除元数据失败", e);
            response.put("success", false);
            response.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 生成特定数据库类型的SQL
     * 
     * @param file XML文件
     * @param databaseType 数据库类型
     * @return SQL生成结果
     */
    @PostMapping("/generate/sql/{databaseType}")
    public ResponseEntity<Map<String, Object>> generateSqlForDatabase(
            @RequestParam("file") MultipartFile file,
            @PathVariable String databaseType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择XML文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (!file.getOriginalFilename().toLowerCase().endsWith(".xml")) {
                response.put("success", false);
                response.put("message", "请上传XML格式的文件");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 保存临时文件
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path tempFile = tempDir.resolve("sql_gen_" + System.currentTimeMillis() + ".xml");
            Files.copy(file.getInputStream(), tempFile);
            
            try {
                // 生成SQL
                String sql = sqlGeneratorService.generateSqlForDatabase(tempFile.toFile(), databaseType);
                
                response.put("success", true);
                response.put("sql", sql);
                response.put("databaseType", databaseType);
                response.put("message", "SQL生成成功");
                
                return ResponseEntity.ok(response);
                
            } finally {
                // 清理临时文件
                Files.deleteIfExists(tempFile);
            }
            
        } catch (IOException e) {
            log.error("文件处理失败", e);
            response.put("success", false);
            response.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            log.error("SQL生成失败", e);
            response.put("success", false);
            response.put("message", "生成失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 获取支持的数据库类型
     * 
     * @return 数据库类型列表
     */
    @GetMapping("/database/types")
    public ResponseEntity<Map<String, Object>> getSupportedDatabaseTypes() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String[] supportedTypes = {"H2", "MYSQL", "POSTGRESQL", "ORACLE", "SQLSERVER"};
            
            response.put("success", true);
            response.put("types", supportedTypes);
            response.put("currentType", databaseConfig.getType());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取数据库类型失败", e);
            response.put("success", false);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}