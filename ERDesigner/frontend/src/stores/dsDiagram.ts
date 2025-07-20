import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Entity, Relationship, Datasource, TreeNode, Index, View, HistoryState } from '../types/entity'
import { EntityType, TreeNodeType, OperationType } from '../types/entity'
// import api from '@/api'

// 常量定义
const MAX_HISTORY_SIZE = 50

export const useDSDiagramStore = defineStore('dsDiagram', () => {
  
  // 核心状态
  const datasources = ref<Datasource[]>([])
  const views = ref<View[]>([])
  const entities = ref<Entity[]>([])
  const relationships = ref<Relationship[]>([])
  const indexes = ref<Index[]>([])
  
  // 选择状态
  const refreshTreeNode = ref(true)
  const selectedEntities = ref<Entity[]>([])
  const selectedRelationships = ref<Relationship[]>([])
  
  // 画布状态
  const zoom = ref(1)
  const panX = ref(0)
  const panY = ref(0)
  const showGrid = ref(true)

  // 历史记录
  const history = ref<HistoryState[]>([])
  const historyIndex = ref(-1)

  // 计算属性
  const entityCount = computed(() => entities.value.length)
  const relationshipCount = computed(() => relationships.value.length)
  const datasourceCount = computed(() => datasources.value.length)

  const canvasState = computed(() => ({
    zoom: zoom.value,
    panX: panX.value,
    panY: panY.value,
    showGrid: showGrid.value,
  }))

  // 树形结构数据
  const treeData = computed(() => {
    const tree: TreeNode[] = []
    const entityMap: Record<string, TreeNode> = {}
    const datasourceMap: Record<string, TreeNode> = {}

    if(refreshTreeNode.value){
      // 构建所有实体节点
      entities.value.forEach(entity => {
        entityMap[entity.id] = {
          id: entity.id,
          label: entity.name,
          type: TreeNodeType.ENTITY,
          entityType: entity.entityType,
          datasourceId: entity.datasourceId,
          children: []
        }
      })

      // 构建所有数据源节点
      datasources.value.forEach(ds => {
        datasourceMap[ds.id] = {
          id: ds.id,
          label: ds.name,
          type: TreeNodeType.DATASOURCE,
          children: []
        }
      })

      // 实体挂到数据源（先处理没有父节点的实体）
      entities.value.forEach(entity => {
        const node = entityMap[entity.id]
        if (!entity.parentEntityId || !entityMap[entity.parentEntityId]) {
          datasourceMap[entity.datasourceId]?.children?.push(node)
        }
      })

      // 再挂子节点
      entities.value.forEach(entity => {
        if (entity.parentEntityId && entityMap[entity.parentEntityId]) {
          entityMap[entity.parentEntityId].children!.push(entityMap[entity.id])
        }
      })

      // 汇总到树形结构
      Object.values(datasourceMap).forEach(dsNode => tree.push(dsNode))
    }
    return tree
  })

  // 数据源操作
  function addDatasource(datasource: Datasource) {
    datasources.value.push(datasource)
    saveToHistory(OperationType.ADD_DATASOURCE, '添加数据源: ' + datasource.name, undefined, { datasource })
  }

  function updateDatasource(updatedDatasource: Datasource) {
    const index = datasources.value.findIndex(d => d.id === updatedDatasource.id)
    if (index !== -1) {
      const oldDatasource = datasources.value[index]
      datasources.value[index] = { ...updatedDatasource }
      saveToHistory(OperationType.UPDATE_DATASOURCE, '更新数据源: ' + updatedDatasource.name, { datasource: oldDatasource }, { datasource: updatedDatasource })
    }
  }

  function deleteDatasource(datasourceId: string) {
    // 删除数据库前先删除其下的所有实体
    const datasourceEntities = entities.value.filter(e => e.datasourceId === datasourceId)
    datasourceEntities.forEach(entity => deleteEntity(entity.id))
    
    const deletedDatasource = datasources.value.find(d => d.id === datasourceId)
    datasources.value = datasources.value.filter(d => d.id !== datasourceId)
    
    saveToHistory(OperationType.DELETE_DATASOURCE, '删除数据源', { datasource: deletedDatasource }, undefined)
  }

  function getDatasourceById(id: string): Datasource | undefined {
    return datasources.value.find(d => d.id === id)
  }

  function deleteView(viewId: string) {
    // 删除视图前，将视图中的数据源添加到默认视图
    const view = views.value.find(v => v.id === viewId)
    if (!view) {
      throw new Error('视图不存在')
    }

    // 删除视图前，将视图中的数据源添加到默认视图
    view.datasourceIds.forEach(dsId => {
      const datasource = datasources.value.find(d => d.id === dsId)
      if (datasource) {
        datasource.viewId = 'default'
        updateDatasource(datasource)
      }
    })

    // 给默认视图添加数据源
    const defaultView = views.value.find(v => v.id === 'default')
    if (defaultView) {
      defaultView.datasourceIds.push(...view.datasourceIds)
    }

    views.value = views.value.filter(v => v.id !== viewId)
  }

  // 实体操作
  function addEntity(entity: Entity) {
    if (!entity.id || !entity.name) {
      throw new Error('实体ID和名称不能为空')
    }
    
    if (entities.value.some(e => e.id === entity.id)) {
      throw new Error('实体ID已存在')
    }

    entities.value.push(entity)
    
    saveToHistory(OperationType.ADD_ENTITY, '添加实体', undefined, { entity })
  }

  function updateEntity(updatedEntity: Entity) {
    const index = entities.value.findIndex(e => e.id === updatedEntity.id)
    if (index === -1) {
      throw new Error('实体不存在')
    }
    const oldEntity = entities.value[index]
    entities.value[index] = updatedEntity
    saveToHistory(OperationType.UPDATE_ENTITY, '更新实体', { entity: oldEntity }, { entity: updatedEntity })
  }

  function deleteEntity(entityId: string) {

    const deletedEntity = entities.value.find(e => e.id === entityId)

    // 检查是否有子实体继承了这个实体
    const childEntities = entities.value.filter(e => e.parentEntityId === entityId)
    if (childEntities.length > 0) {

      const parentEntityId = deletedEntity?.parentEntityId;

      // 可以选择阻止删除或者清除子实体的继承关系
      childEntities.forEach(child => {
        child.parentEntityId = parentEntityId
        updateEntity(child)
      })
    }
    
    entities.value = entities.value.filter(e => e.id !== entityId)
    
    // 同时删除相关的关系
    relationships.value = relationships.value.filter(r => 
      r.fromEntityId !== entityId && r.toEntityId !== entityId
    )

    // 删除索引
    indexes.value = indexes.value.filter(i => i.entityId !== entityId)
    
    if (selectedEntities.value.some(e => e.id === entityId)) {
      selectedEntities.value = selectedEntities.value.filter(e => e.id !== entityId)
    }
    
    saveToHistory(OperationType.DELETE_ENTITY, '删除实体', { entity: deletedEntity }, undefined)
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
      entity.entityType === EntityType.ABSTRACT // 只有abstract类型的实体可以作为父实体
    )
  }

  // 关系操作
  function addRelationship(relationship: Relationship) {
    relationships.value.push(relationship)
    saveToHistory(OperationType.ADD_RELATIONSHIP, '添加关系', undefined, { relationship })
  }

  function updateRelationship(updatedRelationship: Relationship) {
    const index = relationships.value.findIndex(r => r.id === updatedRelationship.id)
    if (index !== -1) {
      const oldRelationship = relationships.value[index]
      relationships.value[index] = { ...updatedRelationship }
      saveToHistory(OperationType.UPDATE_RELATIONSHIP, '更新关系', { relationship: oldRelationship }, { relationship: updatedRelationship })
    }
  }

  function deleteRelationship(relationshipId: string) {
    const deletedRelationship = relationships.value.find(r => r.id === relationshipId)
    if (!deletedRelationship) {
      throw new Error('关系不存在')
    }

    relationships.value = relationships.value.filter(r => r.id !== relationshipId)
    
    if (selectedRelationships.value.some(r => r.id === relationshipId)) {
      selectedRelationships.value = selectedRelationships.value.filter(r => r.id !== relationshipId)
    }
    saveToHistory(OperationType.DELETE_RELATIONSHIP, '删除关系', { relationship: deletedRelationship }, undefined)
  }

  function toggleGrid() {
    showGrid.value = !showGrid.value
  }

  // 历史记录操作
  function saveToHistory(type: OperationType, description: string, before?: any, after?: any) {
    const historyState: HistoryState = {
      id: Date.now().toString(),
      type,
      description,
      undone: false,
      redone: false,
      operationTime: new Date(),
      before,
      after
    }
    
    // 移除当前索引之后的历史记录
    history.value = history.value.slice(0, historyIndex.value + 1)
    history.value.push(historyState)
    historyIndex.value = history.value.length - 1
    
    // 限制历史记录数量
    if (history.value.length > MAX_HISTORY_SIZE) {
      history.value.shift()
      historyIndex.value--
    }
  }

  function undo() {
    if (historyIndex.value > 0) {
      historyIndex.value--
      const state = history.value[historyIndex.value]
      applyHistoryState(state, true)
    }
  }

  function redo() {
    if (historyIndex.value < history.value.length - 1) {
      historyIndex.value++
      const state = history.value[historyIndex.value]
      applyHistoryState(state, false)
    }
  }

  // 应用历史状态
  function applyHistoryState(state: HistoryState, isUndo: boolean) {
    const { type, before, after } = state
    
    switch (type) {
      case OperationType.ADD_ENTITY:
        if (isUndo && after?.entity) {
          entities.value = entities.value.filter(e => e.id !== after.entity!.id)
        } else if (!isUndo && after?.entity) {
          entities.value.push(after.entity)
        }
        break
        
      case OperationType.UPDATE_ENTITY:
        if (isUndo && before?.entity) {
          const index = entities.value.findIndex(e => e.id === before.entity!.id)
          if (index !== -1) {
            entities.value[index] = before.entity
          }
        } else if (!isUndo && after?.entity) {
          const index = entities.value.findIndex(e => e.id === after.entity!.id)
          if (index !== -1) {
            entities.value[index] = after.entity
          }
        }
        break
        
      case OperationType.DELETE_ENTITY:
        if (isUndo && before?.entity) {
          entities.value.push(before.entity)
        } else if (!isUndo && after?.entity) {
          entities.value = entities.value.filter(e => e.id !== after.entity!.id)
        }
        break
        
      case OperationType.ADD_RELATIONSHIP:
        if (isUndo && after?.relationship) {
          relationships.value = relationships.value.filter(r => r.id !== after.relationship!.id)
        } else if (!isUndo && after?.relationship) {
          relationships.value.push(after.relationship)
        }
        break
        
      case OperationType.UPDATE_RELATIONSHIP:
        if (isUndo && before?.relationship) {
          const index = relationships.value.findIndex(r => r.id === before.relationship!.id)
          if (index !== -1) {
            relationships.value[index] = before.relationship
          }
        } else if (!isUndo && after?.relationship) {
          const index = relationships.value.findIndex(r => r.id === after.relationship!.id)
          if (index !== -1) {
            relationships.value[index] = after.relationship
          }
        }
        break
        
      case OperationType.DELETE_RELATIONSHIP:
        if (isUndo && before?.relationship) {
          relationships.value.push(before.relationship)
        } else if (!isUndo && after?.relationship) {
          relationships.value = relationships.value.filter(r => r.id !== after.relationship!.id)
        }
        break
        
      case OperationType.ADD_DATASOURCE:
        if (isUndo && after?.datasource) {
          datasources.value = datasources.value.filter(d => d.id !== after.datasource!.id)
        } else if (!isUndo && after?.datasource) {
          datasources.value.push(after.datasource)
        }
        break
        
      case OperationType.UPDATE_DATASOURCE:
        if (isUndo && before?.datasource) {
          const index = datasources.value.findIndex(d => d.id === before.datasource!.id)
          if (index !== -1) {
            datasources.value[index] = before.datasource
          }
        } else if (!isUndo && after?.datasource) {
          const index = datasources.value.findIndex(d => d.id === after.datasource!.id)
          if (index !== -1) {
            datasources.value[index] = after.datasource
          }
        }
        break
        
      case OperationType.DELETE_DATASOURCE:
        if (isUndo && before?.datasource) {
          datasources.value.push(before.datasource)
        } else if (!isUndo && after?.datasource) {
          datasources.value = datasources.value.filter(d => d.id !== after.datasource!.id)
        }
        break
    }
    clearSelection()
  }

  // 数据操作
  function loadDiagram(data: { views: View[], datasources: Datasource[], entities: Entity[], relationships: Relationship[], indexes: Index[] }) {
    views.value = data.views
    datasources.value = data.datasources
    entities.value = data.entities
    relationships.value = data.relationships
    indexes.value = data.indexes
    clearSelection()
  }

  function exportDiagram() {
    return {
      entities: entities.value,
      relationships: relationships.value,
      datasources: datasources.value,
      views: views.value
    }
  }

  function clearSelection(){
    selectedEntities.value = []
    selectedRelationships.value = []
  }

  // 加载数据源（含兜底逻辑）
  async function loadDatasources() {
    try {
      // TODO: 替换为你的实际 API 调用
      // const res = await api.get('/datasources')
      // let list: Datasource[] = res.data
      let list: Datasource[] = [] // mock: 实际应为 API 返回
      let viewList: View[] = [] // mock: 实际应为 API 返回
      
      if (!list || list.length === 0) {
        list = [{
          id: 'default',
          name: 'Default',
          description: ''
        }]
      }
      
      if (!viewList || viewList.length === 0) {
        viewList = [{
          id: 'default',
          name: 'Default',
          datasourceIds: ['default']
        }]
      }
      
      datasources.value = [...list]
      views.value = [...viewList]
    } catch (error) {
      console.error('加载数据源失败:', error)
      throw error
    }
  }

  return {
    // 状态
    views,
    datasources,
    entities,
    relationships,
    indexes,
    selectedEntities,
    selectedRelationships,
    zoom,
    panX,
    panY,
    showGrid,
    
    // 计算属性
    entityCount,
    relationshipCount,
    datasourceCount,
    treeData,
    canvasState,
    
    // 数据源方法
    addDatasource,
    updateDatasource,
    deleteDatasource,
    getDatasourceById,
    loadDatasources,
    
    // 视图方法
    deleteView,
    
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
    
    // 画布方法
    toggleGrid,
    
    // 历史方法
    undo,
    redo,
    saveToHistory,
    
    // 数据方法
    loadDiagram,
    exportDiagram
  }
})
