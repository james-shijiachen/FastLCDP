package com.fastlcdp.repository;

import com.fastlcdp.model.MetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 元数据仓库接口
 */
@Repository
public interface MetadataRepository extends JpaRepository<MetadataEntity, Long> {
    
    /**
     * 根据模式名查找元数据
     */
    List<MetadataEntity> findBySchemaName(String schemaName);
    
    /**
     * 根据模式名和表名查找元数据
     */
    List<MetadataEntity> findBySchemaNameAndTableName(String schemaName, String tableName);
    
    /**
     * 根据模式名和表名查找字段元数据
     */
    @Query("SELECT m FROM MetadataEntity m WHERE m.schemaName = :schemaName AND m.tableName = :tableName AND m.fieldName IS NOT NULL")
    List<MetadataEntity> findFieldsBySchemaNameAndTableName(@Param("schemaName") String schemaName, @Param("tableName") String tableName);
    
    /**
     * 根据模式名和表名查找索引元数据
     */
    @Query("SELECT m FROM MetadataEntity m WHERE m.schemaName = :schemaName AND m.tableName = :tableName AND m.indexName IS NOT NULL")
    List<MetadataEntity> findIndexesBySchemaNameAndTableName(@Param("schemaName") String schemaName, @Param("tableName") String tableName);
    
    /**
     * 根据模式名和表名查找关系元数据
     */
    @Query("SELECT m FROM MetadataEntity m WHERE m.schemaName = :schemaName AND m.tableName = :tableName AND m.relationType IS NOT NULL")
    List<MetadataEntity> findRelationsBySchemaNameAndTableName(@Param("schemaName") String schemaName, @Param("tableName") String tableName);
    
    /**
     * 根据模式名查找所有表名
     */
    @Query("SELECT DISTINCT m.tableName FROM MetadataEntity m WHERE m.schemaName = :schemaName")
    List<String> findTableNamesBySchemaName(@Param("schemaName") String schemaName);
    
    /**
     * 根据模式名和表名删除元数据
     */
    void deleteBySchemaNameAndTableName(String schemaName, String tableName);
    
    /**
     * 根据模式名删除元数据
     */
    void deleteBySchemaName(String schemaName);
    
    /**
     * 检查模式是否存在
     */
    @Query("SELECT COUNT(m) > 0 FROM MetadataEntity m WHERE m.schemaName = :schemaName")
    boolean existsBySchemaName(@Param("schemaName") String schemaName);
    
    /**
     * 检查表是否存在
     */
    @Query("SELECT COUNT(m) > 0 FROM MetadataEntity m WHERE m.schemaName = :schemaName AND m.tableName = :tableName")
    boolean existsBySchemaNameAndTableName(@Param("schemaName") String schemaName, @Param("tableName") String tableName);
}