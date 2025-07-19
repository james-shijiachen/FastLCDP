import type { Relationship } from '@/types/entity'
/**
 * 验证关系的完整性
 */
export function validateRelationship(relationship: Relationship): string[] {
  const errors: string[] = []
  // 基本字段验证
  if (!relationship.id) {
    errors.push('关系ID不能为空')
  }
  if (!relationship.fromEntityId || !relationship.toEntityId) {
    errors.push('关系必须指定源实体和目标实体')
  }
  return errors
}

/**
 * 检查关系是否为自引用
 */
export function isSelfReferencing(relationship: Relationship): boolean {
  return relationship.fromEntityId === relationship.toEntityId
}

/**
 * 生成数据库外键约束SQL
 */
export function generateForeignKeySQL(relationship: Relationship): string {
  const constraintName = `fk_${relationship.fromEntityId}_${relationship.toEntityId}`
  const onDelete = relationship.cascadeDelete ? 'CASCADE' : 'RESTRICT'
  const onUpdate = relationship.cascadeUpdate ? 'CASCADE' : 'RESTRICT'
  
  return `CONSTRAINT ${constraintName} FOREIGN KEY (${relationship.toFieldId}) 
          REFERENCES ${relationship.toEntityId}(${relationship.fromFieldId})
          ON DELETE ${onDelete} ON UPDATE ${onUpdate}`
}