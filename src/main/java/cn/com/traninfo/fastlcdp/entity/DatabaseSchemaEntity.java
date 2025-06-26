package cn.com.traninfo.fastlcdp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 数据库元数据，用于保存XML中的元数据到数据库
 */
@Entity
@Table(name = "metadata_database_schema")
@Getter
@Setter
@ToString
public class DatabaseSchemaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "version", length = 20)
    private String version;

    @Column(name = "charset", length = 100)
    private String charset;

    @Column(name = "collation", length = 100)
    private String collation;

    @Column(name = "engine", length = 20)
    private String engine;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

}
