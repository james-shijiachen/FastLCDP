package cn.com.traninfo.fastlcdp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 表元数据，用于保存XML中的元数据到数据库
 */
@Entity
@Table(name = "metadata_table")
@Getter
@Setter
@ToString
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "comment", length = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "extends_table")
    private TableEntity extendsTable;

    @Column(name = "engine", length = 20)
    private String engine;

    @Column(name = "charset", length = 100)
    private String charset;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "database_id")
    private DatabaseSchemaEntity database;

}
