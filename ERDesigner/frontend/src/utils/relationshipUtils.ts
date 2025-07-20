import type { Relationship } from '@/types/entity'
/**
 * Validate relationship integrity
 */
export function validateRelationship(relationship: Relationship): string[] {
  const errors: string[] = []
  // Basic field validation
  if (!relationship.id) {
    errors.push('Relationship ID cannot be empty')
  }
  if (!relationship.fromEntityId || !relationship.toEntityId) {
    errors.push('Relationship must specify source and target entities')
  }
  return errors
}

/**
 * Check if relationship is self-referencing
 */
export function isSelfReferencing(relationship: Relationship): boolean {
  return relationship.fromEntityId === relationship.toEntityId
}

/**
 * Generate database foreign key constraint SQL
 */
export function generateForeignKeySQL(relationship: Relationship): string {
  const constraintName = `fk_${relationship.fromEntityId}_${relationship.toEntityId}`
  const onDelete = relationship.cascadeDelete ? 'CASCADE' : 'RESTRICT'
  const onUpdate = relationship.cascadeUpdate ? 'CASCADE' : 'RESTRICT'
  
  return `CONSTRAINT ${constraintName} FOREIGN KEY (${relationship.toFieldId}) 
          REFERENCES ${relationship.toEntityId}(${relationship.fromFieldId})
          ON DELETE ${onDelete} ON UPDATE ${onUpdate}`
}