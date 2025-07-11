export interface Datasource {
  id: string
  name: string
  description?: string
  createdTime: Date
}

export interface Entity {
  datasourceId: string
  parentEntityId?: string // 继承的父实体ID
  id: string
  name: string
  comment?: string
  entityType: 'abstract' | 'entity'
  fields: Field[]
  x: number
  y: number
  width: number
  height: number
  backgroundColor?: string
  borderColor?: string
}

export interface Field {
  entityId: string
  id: string
  name: string
  type: string
  length?: number
  precision?: number
  defaultValue?: string
  comment?: string
  isPrimaryKey: boolean
  isRequired: boolean
  isUnique: boolean
}

export interface Relationship {
  id: string
  fromEntityId: string
  toEntityId: string
  type: 'one-to-one' | 'one-to-many' | 'many-to-one' | 'many-to-many'
  fromFieldId?: string
  toFieldId?: string
  name?: string
  comment?: string
}

export interface Index {
  id: string
  entityId: string
  name: string
  comment?: string
  fields: string[]
  unique: boolean
}

export interface DiagramData {
  datasources: Datasource[]
  entities: Entity[]
  relationships: Relationship[]
}

export interface TreeNode {
  id: string
  label: string
  type: 'datasource' | 'entity'
  children?: TreeNode[]
  entityType?: 'abstract' | 'entity'
  datasourceId?: string
}
