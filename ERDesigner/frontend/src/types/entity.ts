// 数据源类型枚举
export enum DatasourceType {
  DATABASE = 'DATABASE',
  NOSQL = 'NOSQL',
  DOCUMENT = 'DOCUMENT'
}

// 数据库类型枚举
export enum DatasourceCategory {
  SQLITE = 'SQLITE',
  MYSQL = 'MYSQL',
  ORACLE = 'ORACLE',
  POSTGRESQL = 'POSTGRESQL',
  SQLSERVER = 'SQLSERVER',
  REDIS = 'REDIS',
  MONGODB = 'MONGODB',
  ELASTICSEARCH = 'ELASTICSEARCH',
  JSON = 'JSON',
  XML = 'XML'
}

// 关系类型枚举
export enum RelationshipType {
  HARD = 'HARD',
  SOFT = 'SOFT',
  VIRTUAL = 'VIRTUAL'
}

// 关联类型枚举
export enum RelationshipCategory {
  ONE_TO_ONE = 'ONE_TO_ONE',
  ONE_TO_MANY = 'ONE_TO_MANY',
  MANY_TO_ONE = 'MANY_TO_ONE',
  MANY_TO_MANY = 'MANY_TO_MANY'
}

export enum CascadeOperation {
  CASCADE = 'CASCADE',
  NO_ACTION = 'NO_ACTION',
  SET_NULL = 'SET_NULL',
  SET_DEFAULT = 'SET_DEFAULT',
  RESTRICT = 'RESTRICT'
}

// 实体类型枚举
export enum EntityType {
  ABSTRACT = 'ABSTRACT', // 抽象实体，可作为父实体
  ENTITY = 'ENTITY'     // 具体实体
}

// 树节点类型枚举
export enum TreeNodeType {
  DATASOURCE = 'DATASOURCE',
  ENTITY = 'ENTITY'
}

// 字段类型枚举
export enum FieldType {
  VARCHAR = 'VARCHAR',
  TEXT = 'TEXT',
  INT = 'INT',
  LONG = 'LONG',
  DECIMAL = 'DECIMAL',
  DATETIME = 'DATETIME',
  BOOLEAN = 'BOOLEAN',
  BINARY = 'BINARY',
  JSON = 'JSON'
}

// 索引类型枚举
export enum IndexType {
  BTREE = 'BTREE',
  HASH = 'HASH',
  FULLTEXT = 'FULLTEXT'
}

// 数据源版本枚举
export enum DatasourceVersion {
  MYSQL_5_X = 'MYSQL_5_X',
  MYSQL_8_X = 'MYSQL_8_X  ',
  ORACLE_11_X = 'ORACLE_11_X',
  ORACLE_12_X = 'ORACLE_12_X',
  ORACLE_19_X = 'ORACLE_19_X',
  ORACLE_21_X = 'ORACLE_21_X',
  ORACLE_23_X = 'ORACLE_23_X',
  ORACLE_25_X = 'ORACLE_25_X',
  POSTGRESQL_10_X = 'POSTGRESQL_10_X',
  POSTGRESQL_11_X = 'POSTGRESQL_11_X',
  POSTGRESQL_12_X = 'POSTGRESQL_12_X',
  POSTGRESQL_13_X = 'POSTGRESQL_13_X',
  POSTGRESQL_14_X = 'POSTGRESQL_14_X',
  POSTGRESQL_15_X = 'POSTGRESQL_15_X',
  POSTGRESQL_16_X = 'POSTGRESQL_16_X',
  SQLSERVER_2008_X = 'SQLSERVER_2008_X',
  SQLSERVER_2012_X = 'SQLSERVER_2012_X',
  SQLSERVER_2014_X = 'SQLSERVER_2014_X',
  SQLSERVER_2016_X = 'SQLSERVER_2016_X',
  SQLSERVER_2017_X = 'SQLSERVER_2017_X',
  SQLSERVER_2019_X = 'SQLSERVER_2019_X',
  SQLSERVER_2022_X = 'SQLSERVER_2022_X',
}

// 视图接口
export interface View {
  id: string
  name: string
  datasourceIds: string[]
  canvasState: CanvasState
}

// 数据源接口
export interface Datasource {
  // 基础配置
  id: string
  name: string
  viewId?: string
  description?: string
  type?: DatasourceType
  category?: DatasourceCategory
  version?: DatasourceVersion

  // 数据库高级配置
  connectionString?: string
  host?: string
  port?: number
  database?: string
  username?: string
  password?: string
}

// 字段接口
export interface Field {
  entityId: string
  id: string
  serialNo: number
  name: string
  type: FieldType | string
  length?: number
  scale?: number
  defaultValue?: string
  comment?: string
  isPrimaryKey: boolean
  isRequired: boolean
  isUnique: boolean
  isAutoIncrement?: boolean
  isUnsigned?: boolean
  extended?: {
    entityId: string // 来源的实体ID
    fieldId: string  // 来源的实体字段ID
  }
  foreignKey?: [{
    relationId: string // 关联ID
    referencedEntityId: string  // 关联的实体ID
    referencedFieldId: string   // 关联的实体字段ID
  }]
}

// 实体接口
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

// 关系接口
export interface Relationship {
  datasourceId: string
  id: string
  name?: string
  fromEntityId: string
  toEntityId: string
  type: RelationshipType
  category?: RelationshipCategory  
  fromFieldId?: string
  toFieldId?: string
  x: number
  y: number
  // 级联操作
  cascadeCreate?: boolean   // 针对JSON/XML，父实体创建时，子实体是否必须创建，默认false
  cascadeDelete?: CascadeOperation  // 被关联实体删除时，是否级联删除，默认NO_ACTION
  cascadeUpdate?: CascadeOperation  // 被关联实体更新时，是否级联更新，默认NO_ACTION
}

// 索引接口
export interface Index {
  datasourceId: string
  id: string
  name: string
  entityId: string
  fields: Field[]
  unique: boolean
  comment?: string
  type?: IndexType
}

// 树节点接口
export interface TreeNode {
  id: string
  label: string
  type: TreeNodeType
  children?: TreeNode[]
  entityType?: EntityType
  datasourceCategory?: DatasourceCategory
  datasourceId?: string
}

// 选择状态接口
export interface SelectionState {
  entities: Entity[]
  relationships: Relationship[]
  datasources: Datasource[]
}

// 画布状态接口
export interface CanvasState {
  MAX_ZOOM: number
  MIN_ZOOM: number
  zoom: number
  panX: number
  panY: number
  showGrid: boolean
}

// 操作类型枚举
export enum OperationType {
  ADD_ENTITY = 'ADD_ENTITY',
  UPDATE_ENTITY = 'UPDATE_ENTITY',
  DELETE_ENTITY = 'DELETE_ENTITY',
  ADD_RELATIONSHIP = 'ADD_RELATIONSHIP',
  UPDATE_RELATIONSHIP = 'UPDATE_RELATIONSHIP',
  DELETE_RELATIONSHIP = 'DELETE_RELATIONSHIP',
  ADD_DATASOURCE = 'ADD_DATASOURCE',
  UPDATE_DATASOURCE = 'UPDATE_DATASOURCE',
  DELETE_DATASOURCE = 'DELETE_DATASOURCE',
  DELETE_VIEW = 'DELETE_VIEW',
  ADD_INDEX = 'ADD_INDEX',
  UPDATE_INDEX = 'UPDATE_INDEX',
  DELETE_INDEX = 'DELETE_INDEX',
  IMPORT_DATA = 'IMPORT_DATA'
}

// 操作历史接口
export interface HistoryState {
  id: string
  type: OperationType
  description: string
  undone: boolean | false // 是否已撤销
  redone: boolean | false // 是否已重做
  operationTime: Date
  // 操作前的状态
  before?: {
    view?: View 
    datasource?: Datasource
    entity?: Entity
    relationship?: Relationship
    index?: Index
    views?: View[]
    datasources?: Datasource[]
    entities?: Entity[]
    relationships?: Relationship[]
    indexes?: Index[]
  }
  // 操作后的状态
  after?: {
    view?: View 
    datasource?: Datasource
    entity?: Entity
    relationship?: Relationship
    index?: Index
    views?: View[]
    datasources?: Datasource[]
    entities?: Entity[]
    relationships?: Relationship[]
    indexes?: Index[]
  }
}
