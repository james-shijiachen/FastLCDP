import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Entity, Relation, Database, TreeNode } from '../types/entity'

export const useERDiagramStore = defineStore('erDiagram', () => {
  // 状态
  const databases = ref<Database[]>([])
  const entities = ref<Entity[]>([])
  const relations = ref<Relation[]>([])
  const selectedEntity = ref<Entity | null>(null)
  const selectedRelation = ref<Relation | null>(null)
  const selectedDatabase = ref<Database | null>(null)
  const isSelectMode = ref(true)
  const zoom = ref(1)
  const panX = ref(0)
  const panY = ref(0)

  // 计算属性
  const entityCount = computed(() => entities.value.length)
  const relationCount = computed(() => relations.value.length)
  const databaseCount = computed(() => databases.value.length)
  
  // 可见实体（只显示entity类型，不显示abstract类型）
  const visibleEntities = computed(() => 
    entities.value.filter(entity => entity.entityType === 'entity')
  )
  
  // 树形结构数据
  const treeData = computed(() => {
    const tree: TreeNode[] = []
    
    databases.value.forEach(database => {
      const databaseNode: TreeNode = {
        id: database.id,
        label: database.name,
        type: 'database',
        children: []
      }
      
      // 获取该数据库下的所有实体
      const databaseEntities = entities.value.filter(entity => entity.databaseId === database.id)
      databaseEntities.forEach(entity => {
        const entityNode: TreeNode = {
          id: entity.id,
          label: entity.name,
          type: 'entity',
          entityType: entity.entityType,
          databaseId: database.id
        }
        databaseNode.children!.push(entityNode)
      })
      
      tree.push(databaseNode)
    })
    
    return tree
  })

  // 数据库操作
  function addDatabase(database: Database) {
    databases.value.push(database)
  }

  function updateDatabase(updatedDatabase: Database) {
    const index = databases.value.findIndex(d => d.id === updatedDatabase.id)
    if (index !== -1) {
      databases.value[index] = { ...updatedDatabase }
    }
  }

  function deleteDatabase(databaseId: string) {
    // 删除数据库前先删除其下的所有实体
    const databaseEntities = entities.value.filter(e => e.databaseId === databaseId)
    databaseEntities.forEach(entity => deleteEntity(entity.id))
    
    databases.value = databases.value.filter(d => d.id !== databaseId)
    if (selectedDatabase.value?.id === databaseId) {
      selectedDatabase.value = null
    }
  }

  function getDatabaseById(id: string): Database | undefined {
    return databases.value.find(d => d.id === id)
  }

  // 实体操作
  function addEntity(entity: Entity) {
    // 如果有父实体，继承父实体的字段
    if (entity.parentEntityId) {
      const parentEntity = getEntityById(entity.parentEntityId)
      if (parentEntity) {
        const inheritedFields = parentEntity.fields.map(field => ({
          ...field,
          id: `${entity.id}_${field.id}`, // 生成新的字段ID
          isPrimaryKey: false // 继承的字段默认不是主键
        }))
        entity.fields = [...inheritedFields, ...entity.fields]
      }
    }
    entities.value.push(entity)
  }

  function updateEntity(updatedEntity: Entity) {
    const index = entities.value.findIndex(e => e.id === updatedEntity.id)
    if (index !== -1) {
      entities.value[index] = { ...updatedEntity }
    }
  }

  function deleteEntity(entityId: string) {
    // 检查是否有子实体继承了这个实体
    const childEntities = entities.value.filter(e => e.parentEntityId === entityId)
    if (childEntities.length > 0) {
      // 可以选择阻止删除或者清除子实体的继承关系
      childEntities.forEach(child => {
        child.parentEntityId = undefined
        updateEntity(child)
      })
    }
    
    entities.value = entities.value.filter(e => e.id !== entityId)
    // 同时删除相关的关系
    relations.value = relations.value.filter(r => 
      r.fromEntityId !== entityId && r.toEntityId !== entityId
    )
    if (selectedEntity.value?.id === entityId) {
      selectedEntity.value = null
    }
  }

  function getEntityById(id: string): Entity | undefined {
    return entities.value.find(e => e.id === id)
  }

  // 获取实体的完整字段（包括继承的字段）
  function getEntityWithInheritedFields(entityId: string): Entity | undefined {
    const entity = getEntityById(entityId)
    if (!entity) return undefined

    if (!entity.parentEntityId) return entity

    const parentEntity = getEntityById(entity.parentEntityId)
    if (!parentEntity) return entity

    // 递归获取所有继承的字段
    const parentWithInherited = getEntityWithInheritedFields(entity.parentEntityId)
    if (!parentWithInherited) return entity

    const inheritedFields = parentWithInherited.fields.map(field => ({
      ...field,
      id: `${entity.id}_inherited_${field.id}`,
      isPrimaryKey: false
    }))

    return {
      ...entity,
      fields: [...inheritedFields, ...entity.fields]
    }
  }

  // 获取可以作为父实体的实体列表（同一数据库下的实体）
  function getAvailableParentEntities(databaseId: string, excludeEntityId?: string): Entity[] {
    return entities.value.filter(entity => 
      entity.databaseId === databaseId && 
      entity.id !== excludeEntityId &&
      entity.entityType === 'abstract' // 只有abstract类型的实体可以作为父实体
    )
  }

  // 关系操作
  function addRelation(relation: Relation) {
    relations.value.push(relation)
  }

  function updateRelation(updatedRelation: Relation) {
    const index = relations.value.findIndex(r => r.id === updatedRelation.id)
    if (index !== -1) {
      relations.value[index] = { ...updatedRelation }
    }
  }

  function deleteRelation(relationId: string) {
    relations.value = relations.value.filter(r => r.id !== relationId)
    if (selectedRelation.value?.id === relationId) {
      selectedRelation.value = null
    }
  }

  // 选择操作
  function selectEntity(entity: Entity) {
    selectedEntity.value = entity
    selectedRelation.value = null
  }

  function selectRelation(relation: Relation) {
    selectedRelation.value = relation
    selectedEntity.value = null
  }

  function clearSelection() {
    selectedEntity.value = null
    selectedRelation.value = null
  }

  // 模式切换
  function toggleSelectMode() {
    isSelectMode.value = !isSelectMode.value
  }

  function setSelectMode(mode: boolean) {
    isSelectMode.value = mode
  }

  // 视图操作
  function setZoom(newZoom: number) {
    zoom.value = Math.max(0.1, Math.min(3, newZoom))
  }

  function setPan(x: number, y: number) {
    panX.value = x
    panY.value = y
  }

  function resetView() {
    zoom.value = 1
    panX.value = 0
    panY.value = 0
  }

  // 数据操作
  function loadDiagram(data: { entities: Entity[], relations: Relation[] }) {
    entities.value = data.entities
    relations.value = data.relations
    clearSelection()
  }

  function exportDiagram() {
    return {
      entities: entities.value,
      relations: relations.value
    }
  }

  function clearDiagram() {
    entities.value = []
    relations.value = []
    clearSelection()
    resetView()
  }

  // 撤销/重做功能
  const history = ref<{ entities: Entity[], relations: Relation[] }[]>([])
  const historyIndex = ref(-1)

  function saveToHistory() {
    const currentState = {
      entities: JSON.parse(JSON.stringify(entities.value)),
      relations: JSON.parse(JSON.stringify(relations.value))
    }
    
    // 移除当前索引之后的历史记录
    history.value = history.value.slice(0, historyIndex.value + 1)
    history.value.push(currentState)
    historyIndex.value = history.value.length - 1
    
    // 限制历史记录数量
    if (history.value.length > 50) {
      history.value.shift()
      historyIndex.value--
    }
  }

  function undo() {
    if (historyIndex.value > 0) {
      historyIndex.value--
      const state = history.value[historyIndex.value]
      entities.value = JSON.parse(JSON.stringify(state.entities))
      relations.value = JSON.parse(JSON.stringify(state.relations))
      clearSelection()
    }
  }

  function redo() {
    if (historyIndex.value < history.value.length - 1) {
      historyIndex.value++
      const state = history.value[historyIndex.value]
      entities.value = JSON.parse(JSON.stringify(state.entities))
      relations.value = JSON.parse(JSON.stringify(state.relations))
      clearSelection()
    }
  }

  return {
    // 状态
    databases,
    entities,
    relations,
    selectedEntity,
    selectedRelation,
    selectedDatabase,
    isSelectMode,
    zoom,
    panX,
    panY,
    
    // 计算属性
    entityCount,
    relationCount,
    databaseCount,
    visibleEntities,
    treeData,
    
    // 数据库方法
    addDatabase,
    updateDatabase,
    deleteDatabase,
    getDatabaseById,
    
    // 实体方法
    addEntity,
    updateEntity,
    deleteEntity,
    getEntityById,
    getEntityWithInheritedFields,
    getAvailableParentEntities,
    
    // 关系方法
    addRelation,
    updateRelation,
    deleteRelation,
    
    // 选择方法
    selectEntity,
    selectRelation,
    clearSelection,
    
    // 其他方法
    toggleSelectMode,
    setSelectMode,
    setZoom,
    setPan,
    resetView,
    loadDiagram,
    exportDiagram,
    clearDiagram,
    undo,
    redo,
    saveToHistory
  }
})
