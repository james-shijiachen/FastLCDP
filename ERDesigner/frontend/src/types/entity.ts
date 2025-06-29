export interface Field {
  id: string
  name: string
  type: string
  comment?: string
  isPrimaryKey: boolean
  isRequired: boolean
  isUnique: boolean
}

export interface Database {
  id: string
  name: string
  description?: string
  createdAt: Date
}

export interface Entity {
  id: string
  name: string
  comment?: string
  fields: Field[]
  x: number
  y: number
  width: number
  height: number
  backgroundColor?: string
  borderColor?: string
  databaseId: string
  entityType: 'abstract' | 'entity'
  parentEntityId?: string // 继承的父实体ID
}

export interface Relationship {
  id: string
  fromEntityId: string
  toEntityId: string
  type: 'one-to-one' | 'one-to-many' | 'many-to-one' | 'many-to-many'
  fromField?: string
  toField?: string
  name?: string
  comment?: string
}

export interface Relation {
  id: string
  name: string
  fromEntityId: string
  toEntityId: string
  fromFieldId: string
  toFieldId: string
  relationType: 'one-to-one' | 'one-to-many' | 'many-to-many'
}

export interface DiagramData {
  databases: Database[]
  entities: Entity[]
  relations: Relation[]
}

export interface TreeNode {
  id: string
  label: string
  type: 'database' | 'entity'
  children?: TreeNode[]
  entityType?: 'abstract' | 'entity'
  databaseId?: string
}
