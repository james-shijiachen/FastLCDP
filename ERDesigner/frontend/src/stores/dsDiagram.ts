import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Entity, Relationship, Datasource, TreeNode } from '../types/entity'
// import api from '@/api'

export const useDSDiagramStore = defineStore('dsDiagram', () => {
  // 状态
  const datasources = ref<Datasource[]>([])
  const entities = ref<Entity[]>([])
  const relationships = ref<Relationship[]>([])
  const selectedEntity = ref<Entity | null>(null)
  const selectedRelationship = ref<Relationship | null>(null)
  const selectedDatasource = ref<Datasource | null>(null)
  const isSelectMode = ref(true)
  const zoom = ref(1)
  const panX = ref(0)
  const panY = ref(0)

  // 加载数据源（含兜底逻辑）
  async function loadDatasources() {
    // TODO: 替换为你的实际 API 调用
    // const res = await api.get('/datasources')
    // let list: Datasource[] = res.data
    let list: Datasource[] = [] // mock: 实际应为 API 返回
    if (!list || list.length === 0) {
      list = [{
        id: 'default',
        name: 'default',
        description: '',
        createdTime: new Date()
      }]
    }
    datasources.value = list
  }

  // 计算属性
  const entityCount = computed(() => entities.value.length)
  const relationshipCount = computed(() => relationships.value.length)
  const datasourceCount = computed(() => datasources.value.length)
  
  // 可见实体（只显示entity类型，不显示abstract类型）
  const visibleEntities = computed(() => 
    entities.value.filter(entity => entity.entityType === 'entity')
  )
  
  // 树形结构数据
  const treeData = computed(() => {
    const tree: TreeNode[] = []
    const entityMap: Record<string, TreeNode> = {}
    const datasourceMap: Record<string, TreeNode> = {}

    // 1. 先建所有节点
    entities.value.forEach(entity => {
      entityMap[entity.id] = {
        id: entity.id,
        label: entity.name,
        type: 'entity',
        entityType: entity.entityType,
        datasourceId: entity.datasourceId,
        children: []
      }
    })
    datasources.value.forEach(ds => {
      datasourceMap[ds.id] = {
        id: ds.id,
        label: ds.name,
        type: 'datasource',
        children: []
      }
    })

    // 2. 先全部挂到 datasource
    entities.value.forEach(entity => {
      const node = entityMap[entity.id]
      if (!entity.parentEntityId || !entityMap[entity.parentEntityId]) {
        // 没有父节点，直接挂到数据源
        datasourceMap[entity.datasourceId]?.children?.push(node)
      }
    })
    // 3. 再挂子节点
    entities.value.forEach(entity => {
      if (entity.parentEntityId && entityMap[entity.parentEntityId]) {
        entityMap[entity.parentEntityId].children!.push(entityMap[entity.id])
      }
    })

    // 4. 汇总
    Object.values(datasourceMap).forEach(dsNode => tree.push(dsNode))
    return tree
  })

  // 数据库操作
  function addDatasource(datasource: Datasource) {
    datasources.value.push(datasource)
  }

  function updateDatasource(updatedDatasource: Datasource) {
    const index = datasources.value.findIndex(d => d.id === updatedDatasource.id)
    if (index !== -1) {
      datasources.value[index] = { ...updatedDatasource }
    }
  }

  function deleteDatasource(datasourceId: string) {
    // 删除数据库前先删除其下的所有实体
    const datasourceEntities = entities.value.filter(e => e.datasourceId === datasourceId)
    datasourceEntities.forEach(entity => deleteEntity(entity.id))
    
    datasources.value = datasources.value.filter(d => d.id !== datasourceId)
    if (selectedDatasource.value?.id === datasourceId) {
      selectedDatasource.value = null
    }
  }

  function getDatasourceById(id: string): Datasource | undefined {
    return datasources.value.find(d => d.id === id)
  }

  // 实体操作
  function addEntity(entity: Entity) {
    // // 如果有父实体，继承父实体的字段
    // if (entity.parentEntityId) {
    //   const parentEntity = getEntityById(entity.parentEntityId)
    //   if (parentEntity) {
    //     const inheritedFields = parentEntity.fields.map(field => ({
    //       ...field,
    //       id: `${entity.id}_${field.id}`, // 生成新的字段ID
    //     }))
    //     entity.fields = [...inheritedFields, ...entity.fields]
    //   }
    // }
    console.log('addEntity-end', entity)
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
    relationships.value = relationships.value.filter(r => 
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
  function getAvailableParentEntities(datasourceId: string, excludeEntityId?: string): Entity[] {
    return entities.value.filter(entity => 
      entity.datasourceId === datasourceId && 
      entity.id !== excludeEntityId &&
      entity.entityType === 'abstract' // 只有abstract类型的实体可以作为父实体
    )
  }

  // 关系操作
  function addRelationship(relationship: Relationship) {
    relationships.value.push(relationship)
  }

  function updateRelationship(updatedRelationship: Relationship) {
    const index = relationships.value.findIndex(r => r.id === updatedRelationship.id)
    if (index !== -1) {
      relationships.value[index] = { ...updatedRelationship }
    }
  }

  function deleteRelationship(relationshipId: string) {
    relationships.value = relationships.value.filter(r => r.id !== relationshipId)
    if (selectedRelationship.value?.id === relationshipId) {
      selectedRelationship.value = null
    }
  }

  // 选择操作
  function selectEntity(entity: Entity) {
    selectedEntity.value = entity
    selectedRelationship.value = null
  }

  function selectRelationship(relationship: Relationship) {
    selectedRelationship.value = relationship
    selectedEntity.value = null
  }

  function clearSelection() {
    selectedEntity.value = null
    selectedRelationship.value = null
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
  function loadDiagram(data: { entities: Entity[], relationships: Relationship[] }) {
    entities.value = data.entities
    relationships.value = data.relationships
    clearSelection()
  }

  function exportDiagram() {
    return {
      entities: entities.value,
      relationships: relationships.value
    }
  }

  function clearDiagram() {
    entities.value = []
    relationships.value = []
    clearSelection()
    resetView()
  }

  // 撤销/重做功能
  const history = ref<{ entities: Entity[], relationships: Relationship[] }[]>([])
  const historyIndex = ref(-1)

  function saveToHistory() {
    const currentState = {
      entities: JSON.parse(JSON.stringify(entities.value)),
      relationships: JSON.parse(JSON.stringify(relationships.value))
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
      relationships.value = JSON.parse(JSON.stringify(state.relationships))
      clearSelection()
    }
  }

  function redo() {
    if (historyIndex.value < history.value.length - 1) {
      historyIndex.value++
      const state = history.value[historyIndex.value]
      entities.value = JSON.parse(JSON.stringify(state.entities))
      relationships.value = JSON.parse(JSON.stringify(state.relationships))
      clearSelection()
    }
  }

  return {
    // 状态
    datasources,
    entities,
    relationships,
    selectedEntity,
    selectedRelationship,
    selectedDatasource,
    isSelectMode,
    zoom,
    panX,
    panY,
    
    // 计算属性
    entityCount,
    relationshipCount,
    datasourceCount,
    visibleEntities,
    treeData,
    
    // 数据库方法
    addDatasource,
    updateDatasource,
    deleteDatasource,
    getDatasourceById,
    loadDatasources,
    
    // 实体方法
    addEntity,
    updateEntity,
    deleteEntity,
    getEntityById,
    getEntityWithInheritedFields,
    getAvailableParentEntities,
    
    // 关系方法
    addRelationship,
    updateRelationship,
    deleteRelationship,
    
    // 选择方法
    selectEntity,
    selectRelationship,
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
