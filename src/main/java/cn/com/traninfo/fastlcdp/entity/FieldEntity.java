package cn.com.traninfo.fastlcdp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 表元数据，用于保存XML中的元数据到数据库
 */
@Entity
@Table(name = "metadata_field")
@Getter
@Setter
@ToString
public class FieldEntity {
}
