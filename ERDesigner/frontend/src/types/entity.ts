export enum DatasourceType {
  DATABASE = 'DATABASE',
  NOSQL = 'NOSQL',
  DOCUMENT = 'DOCUMENT'
}

export enum Category {
  MYSQL = 'MYSQL',
  ORACLE = 'ORACLE',
  POSTGRESQL = 'POSTGRESQL',
  SQLSERVER = 'SQLSERVER',
  REDIS = 'REDIS',
  JSON = 'JSON',
  XML = 'XML'
}

export enum RelationshipType {
  ONE_TO_ONE = 'ONE_TO_ONE',
  ONE_TO_MANY = 'ONE_TO_MANY',
  MANY_TO_ONE = 'MANY_TO_ONE',
  MANY_TO_MANY = 'MANY_TO_MANY'
}

export enum EntityType {
  ABSTRACT = 'ABSTRACT',
  ENTITY = 'ENTITY'
}

export enum TreeNodeType {
  DATASOURCE = 'DATASOURCE',
  ENTITY = 'ENTITY'
}

export interface View {
  id: string
  name: string
  datasources: Datasource[]
  createdTime: Date
}

export interface Datasource {
  id: string
  name: string
  description?: string
  type?: DatasourceType
  category?: Category
  views?: View[]
  createdTime: Date
}

export interface Entity {
  datasourceId: string
  parentEntityId?: string // 继承的父实体ID
  id: string
  name: string
  comment?: string
  entityType: EntityType
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
  scale?: number
  defaultValue?: string
  comment?: string
  isPrimaryKey: boolean
  isRequired: boolean
  isUnique: boolean
  extendEntityId?: string // 继承至的实体ID
  extendFieldId?: string // 继承至的实体字段ID
}

export interface Relationship {
  id: string
  fromEntityId: string
  toEntityId: string
  type: RelationshipType
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
  fields: Field[]
  unique: boolean
}

export interface DiagramData {
  datasources: Datasource[]
  entities: Entity[]
  relationships: Relationship[]
  indexs: Index[]
}

export interface TreeNode {
  id: string
  label: string
  type: TreeNodeType
  children?: TreeNode[]
  entityType?: EntityType
  datasourceId?: string
}
