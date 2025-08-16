<template>
  <div class="x6-ds-canvas" :class="{ 'drag-mode': canvasState.isDragMode, 'select-mode': !canvasState.isDragMode, 'dark-theme': isDarkTheme }">
    <!-- 固定网格层 -->
    <svg class="grid-layer" width="100%" height="100%">
      <!-- 网格背景 -->
      <defs v-if="canvasState.showGrid">
        <!-- 亮色主题3x3网格模式：一个大格包含9个小格 -->
        <pattern 
         id="grid"
         :width="60 * canvasState.zoom" 
         :height="60 * canvasState.zoom" 
         patternUnits="userSpaceOnUse" >
          <!-- 大格子边框 -->
          <rect class="grid-border"
            :x="0" 
            :y="0" 
            :width="60 * canvasState.zoom" 
            :height="60 * canvasState.zoom" 
            fill="none" 
            stroke="#e2e7eb" 
            :stroke-width="1 * canvasState.zoom"
          />
          
          <!-- 内部3x3小格子 -->
          <!-- 垂直分割线 -->
          <line class="vertical-line" :x1="20 * canvasState.zoom" :y1="0" :x2="20 * canvasState.zoom" :y2="60 * canvasState.zoom" stroke="#e9eaed" :stroke-width="0.5 * canvasState.zoom"/>
          <line class="vertical-line" :x1="40 * canvasState.zoom" :y1="0" :x2="40 * canvasState.zoom" :y2="60 * canvasState.zoom" stroke="#e9eaed" :stroke-width="0.5 * canvasState.zoom"/>
          
          <!-- 水平分割线 -->
          <line class="horizontal-line" :x1="0" :y1="20 * canvasState.zoom" :x2="60 * canvasState.zoom" :y2="20 * canvasState.zoom" stroke="#e9eaed" :stroke-width="0.5 * canvasState.zoom"/>
          <line class="horizontal-line" :x1="0" :y1="40 * canvasState.zoom" :x2="60 * canvasState.zoom" :y2="40 * canvasState.zoom" stroke="#e9eaed" :stroke-width="0.5 * canvasState.zoom"/>
        </pattern>
      </defs>
      <rect v-if="canvasState.showGrid" width="100%" height="100%" fill="url(#grid)"/>
    </svg>
    <div ref="canvas" class="x6-canvas"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted, onBeforeUnmount } from 'vue'
import { Graph, Node, Edge } from '@antv/x6'
import { Selection } from '@antv/x6-plugin-selection'
import { Snapline } from '@antv/x6-plugin-snapline'
import { Keyboard } from '@antv/x6-plugin-keyboard'
import { CanvasState, Entity, Field, Relationship } from '../types/entity'
import { updateEntitySize, getAllParentFields } from '../utils/datasourceUtil'

// Props定义
const props = defineProps<{
  isSplitScreen: boolean
  currentFocusPane: string
  isDarkTheme?: boolean
  canvasState: CanvasState
  entities: Entity[]
  relationships: Relationship[]
  allFieldsCache: Record<string, Field[]>
  virtualEntities: Entity[]
  viewId?: string // 新增视图ID属性
}>()

// 定义emit事件
const emit = defineEmits<{
  'entityDoubleClick': [entity: Entity, event: MouseEvent]
  'entityRightClick': [entity: Entity, event: MouseEvent]
  'canvasClick': [event: MouseEvent]
  'canvasRightClick': [event: MouseEvent]
  'selectionChange': [entities: Entity[]]
  'updateEntitySize': [entity: Entity]
  'zoomChange': [level: number]
  'panChange': [panX: number, panY: number]
  'setFocusPane': [pane: string]
  'updateEntityPosition': [entity: Entity]
  'editRelation': [relation: Relationship]
}>()

const canvas = ref<HTMLDivElement>()
const allEntities = computed(() => [...props.entities, ...props.virtualEntities])

// 使用全局节点映射，通过hide/show控制可见性
const globalNodeMap = new Map<string, Node>()
// 使用全局边映射，管理关系连线
const globalEdgeMap = new Map<string, Edge>()
// 使用单一图实例，通过节点的hide/show控制视图
const graph = ref<Graph | null>(null)
const currentNodeMap = computed(() => globalNodeMap)
const currentEdgeMap = computed(() => globalEdgeMap)
const currentGraph = computed(() => graph.value)

function initializeGraph() {
  if (!canvas.value) return
  
  // 创建基础图实例
  const graphInstance = createGraphConfig()
  
  // 添加选择插件
  addSelectionPlugin(graphInstance)
  
  // 应用初始画布状态
  applyInitialCanvasState(graphInstance)
  
  // 设置事件监听器
  setupEventListeners(graphInstance)
  
  // 设置图实例
  graph.value = graphInstance
  
  // 注册实体节点
  registerNode()

  // 创建初始节点
  createEntityNodes()
  
  // 创建初始连线
  createRelationshipEdges()
}

// 创建图配置
function createGraphConfig() {
  return new Graph({
    container: canvas.value,
    autoResize: true,
    panning: {
      enabled: true,
      eventTypes: props.canvasState.isDragMode ? ['leftMouseDown', 'mouseWheel'] : ['mouseWheel']
    },
    mousewheel: {
      enabled: true,
      minScale: props.canvasState.MIN_ZOOM,
      maxScale: props.canvasState.MAX_ZOOM,
      modifiers: ['ctrl', 'meta'],
      guard: (e) => {
        e.preventDefault()
        // 只有当画布容器或其子元素有焦点时才允许缩放
        if(props.isSplitScreen && props.currentFocusPane === 'code') return false
        return true
      }
    },
    background: {
      color: 'transparent'
    },
    grid: {
      visible: false
    },
    interacting: {
      nodeMovable: (cellView: any) => {
        // 检查节点是否被选中
        const graph = cellView.graph
        const selection = graph.getPlugin('selection')
        if (selection) {
          const selectedCells = selection.getSelectedCells()
          return selectedCells.some((cell: any) => cell.id === cellView.cell.id)
        }
        return false
      }
    }
  })
}

// 应用初始画布状态
function applyInitialCanvasState(graph: Graph) {
  if (!props.canvasState) return
  
  if (props.canvasState.zoom !== undefined) {
    graph.zoomTo(props.canvasState.zoom)
  }
  
  if (props.canvasState.panX !== undefined && props.canvasState.panY !== undefined) {
    graph.translate(props.canvasState.panX, props.canvasState.panY)
  }
}

// 添加选择插件
function addSelectionPlugin(graph: Graph) {
  // 使用框选插件
  graph.use(
    new Selection({
      enabled: true,
      multiple: true,
      rubberband: !props.canvasState.isDragMode,
      showNodeSelectionBox: !props.canvasState.isDragMode,
      showEdgeSelectionBox: false,
      pointerEvents: 'none',
      className: 'x6-widget-selection',
      strict: false,
      filter: (cell) => {
        // 过滤隐藏的节点，使其不能被选中
        return cell.isVisible()
      }
    })
  )
  // 使用对齐线插件
  graph.use(
    new Snapline({
      enabled: true,
    })
  )
  // 使用快捷键插件
  graph.use(
    new Keyboard({
      enabled: true,
    })
  )
}

function registerNode(){

  // 注册实体节点
  const HEADER_HEIGHT = 30
  const LINE_HEIGHT = 25

  // 端口纵向布局：按索引依次从表头下方开始排布
  Graph.registerPortLayout(
    'fieldListPosition',
    (portsPositionArgs) =>
      portsPositionArgs.map((_, index) => ({
        position: { x: 0, y: HEADER_HEIGHT + index * LINE_HEIGHT },
        angle: 0
      })),
    true
  )

  Graph.registerNode('entityNode', {
    markup: [
      { tagName: 'rect', selector: 'body'},
      { tagName: 'text', selector: 'title'},
      { tagName: 'line', selector: 'separator'}
    ],
    attrs: {
      body: { strokeWidth: 1, rx: 4, ry: 4},
      title: { x: '50%', y: 20, textAnchor: 'middle', fontWeight: 'bold', fontSize: 14},
      separator: { x1: 0, y1: HEADER_HEIGHT, x2: '100%', y2: HEADER_HEIGHT, strokeWidth: 1}
    },
    // 定义锚点，支持左右方向连接
    anchors: [
      { name: 'left', x: 0, y: '50%' },
      { name: 'right', x: '100%', y: '50%' }
    ],
    ports: {
      groups: {
        fieldPort: {
          markup: [
            { tagName: 'rect', selector: 'fieldBody'},
            { tagName: 'path', selector: 'icon'},
            { tagName: 'text', selector: 'fieldName'},
            { tagName: 'path', selector: 'requiredIcon'},
            { tagName: 'text', selector: 'fieldType'},
            { tagName: 'line', selector: 'fieldSeparator'}
          ],
          attrs: {
            fieldBody: {
              width: '100%',
              height: LINE_HEIGHT,
              fill: 'transparent',
              stroke: 'none',
              magnet: false
            },
            icon: {
              transform: `translate(5, ${LINE_HEIGHT / 2 - 8}) scale(0.7)`,
              visibility: 'hidden'
            },
            fieldName: {
              ref: 'fieldBody',
              textAnchor: 'start',
              dominantBaseline: 'middle',
              fontSize: 12
            },
            requiredIcon: {
              transform: `translate(5, ${LINE_HEIGHT / 2 - 8}) scale(0.7)`,
              visibility: 'hidden'
            },
            fieldType: {
              ref: 'fieldBody',
              refDx: -5,     // 向左偏移5px
              textAnchor: 'end',
              dominantBaseline: 'middle',
              fontSize: 12
            },
            fieldSeparator: {
              ref: 'fieldBody',
              x1: 0,
              strokeWidth: 1
            }
          },
          position: 'fieldListPosition'
        }
      }
    }
  })
}

// 监听主题变化，更新节点背景（网格已禁用）
watch(() => props.isDarkTheme, (newTheme) => {
  if (currentGraph) {
    // 更新所有节点的背景颜色
    updateAllNodesTheme()
  }
})

// 监听画布状态变化
watch(() => props.canvasState, (newState) => {
  if (currentGraph.value && newState) {
    // 注意：X6不支持动态更新mousewheel约束，但我们在scale事件中会进行约束检查
    
    // 控制缩放
    if (newState.zoom !== undefined) {
      const currentZoom = currentGraph.value.zoom()
      if (Math.abs(currentZoom - newState.zoom) > 0.01) {
        // 确保缩放值在约束范围内
        const minZoom = newState.MIN_ZOOM || 0.1
        const maxZoom = newState.MAX_ZOOM || 3
        const constrainedZoom = Math.max(minZoom, Math.min(maxZoom, newState.zoom))
        currentGraph.value.zoomTo(constrainedZoom)
      }
    }
    
    // 控制网格显示（已禁用）
    // if (newState.showGrid !== undefined) {
    //   if (newState.showGrid) {
    //     currentGraph.value.showGrid()
    //   } else {
    //     currentGraph.value.hideGrid()
    //   }
    // }

    // 更新事件类型
    currentGraph.value.options.panning = {
      enabled: true,
      eventTypes: newState.isDragMode 
        ? ['leftMouseDown', 'rightMouseDown', 'mouseWheel'] // 拖拽模式：支持鼠标拖拽和两指平移
        : ['mouseWheel'] // 选择模式：仅支持两指平移和滚轮平移
    }
    
    // 获取 Selection 插件实例
    const selectionPlugin = currentGraph.value.getPlugin('selection') as Selection
    if (selectionPlugin) {
      if (newState.isDragMode) {
        // 拖拽模式：禁用框选，只允许单选
        selectionPlugin.disable()
        // 清除当前选择
        currentGraph.value.resetSelection()
      } else {
        // 选择模式：启用框选
        selectionPlugin.enable()
      }
    }

  }
}, { deep: true })

watch(() => props.entities, () => {
  createEntityNodes()
}, { deep: true })

// 监听关系变化，创建连线
watch(() => props.relationships, () => {
  createRelationshipEdges()
}, { deep: true })

// 创建ER实体节点 - 使用hide/show优化版本
function createEntityNodes() {
  if (!currentGraph.value) return

  const nodeMap = currentNodeMap.value
  const currentEntityIds = new Set(props.entities.map(e => e.id))
  
  // 隐藏不在当前视图中的节点，而不是删除
  nodeMap.forEach(node => {
    if (!currentEntityIds.has(node.id)) {
      // 隐藏节点而不是删除
      node.hide()
      // 取消隐藏节点的选中状态
      currentGraph.value?.unselect(node)
    } else {
      // 显示节点（如果之前被隐藏）
      node.show()
    }
  })

  // 添加或更新节点
  props.entities?.forEach(entity => {
    const existingNode = nodeMap.get(entity.id)
    if (existingNode) {
      updateEntityNode(entity, existingNode)
      existingNode.show()
    } else {
      // 创建新节点
      createEntityNode(entity)
    }
  })
}

// 创建关系连线 - 使用hide/show优化版本
function createRelationshipEdges() {
  console.log('createRelationshipEdges')
  if (!currentGraph.value) return

  const currentRelationshipIds = new Set(props.relationships.map(r => r.id))
  console.log(currentRelationshipIds, currentEdgeMap.value, currentGraph.value.getEdges().map(e => e.id))
  // 移除不存在的连线
  currentEdgeMap.value.forEach(edge => {
    if (!currentRelationshipIds.has(edge.id)) {
      edge.hide()
    }else{
      edge.show()
    }
  })

  // 添加或更新连线
  props.relationships?.forEach(relationship => {
    const existingEdge = currentEdgeMap.value.get(relationship.id)
    console.log(existingEdge, relationship.id)
    if (existingEdge) {
      // 更新现有连线
      updateRelationshipEdge(relationship, existingEdge)
      existingEdge.show()
    } else {
      // 创建新连线
      createRelationshipEdge(relationship)
    }
  })
}

// 创建单个关系连线
function createRelationshipEdge(relationship: Relationship) {
  console.log('createRelationshipEdge', relationship)
  if (!currentGraph.value || !relationship.fromFieldId || !relationship.toFieldId) return
  
  const sourcePortId = `${relationship.fromEntityId}-${relationship.fromFieldId}`
  const targetPortId = `${relationship.toEntityId}-${relationship.toFieldId}`
  
  // 检查源节点和目标节点是否存在
  const sourceNode = currentNodeMap.value.get(relationship.fromEntityId)
  const targetNode = currentNodeMap.value.get(relationship.toEntityId)
  
  if (!sourceNode || !targetNode) {
    console.warn('Source or target node not found for relationship:', relationship.id)
    return
  }
  
  try {
    // 使用统一的锚点计算逻辑
    const anchorResult = calculateRelationPathAnchor(sourceNode, targetNode)
    const sourceAnchor = anchorResult.source
    const targetAnchor = anchorResult.target
    
    // 计算避让偏移量
    const offset = calculateEdgeOffset(sourceNode, targetNode, relationship)
    
    const edge = currentGraph.value.addEdge({
      id: relationship.id,
      source: {
        cell: relationship.fromEntityId,
        port: sourcePortId,
        anchor: sourceAnchor
      },
      target: {
        cell: relationship.toEntityId,
        port: targetPortId,
        anchor: targetAnchor
      },
      attrs: {
        line: {
          stroke: props.isDarkTheme ? '#ffffff' : '#24292e',
          strokeWidth: 1,
          targetMarker: {
            name: 'classic',
            size: 8
          }
        }
      },
      connector: {
        name: 'rounded',
        args: {
          radius: 10
        }
      },
      router: {
        name: 'orth',
        args: {
          padding: {
            left: Math.abs(50 - offset),
            right: Math.abs(50 - offset),
            top: 1000, // 设置很大的值阻止从上方连接
            bottom: 1000 // 设置很大的值阻止从下方连接
          }
        }
      }
    })
    
    currentEdgeMap.value.set(relationship.id, edge)
  } catch (error) {
    console.warn('Failed to create edge for relationship:', relationship.id, error)
  }
}

// 更新现有关系连线
function updateRelationshipEdge(relationship: Relationship, edge: any) {
  console.log('updateRelationshipEdge', relationship, edge)
  if (!currentGraph.value || !relationship.fromFieldId || !relationship.toFieldId) return
  
  const sourcePortId = `${relationship.fromEntityId}-${relationship.fromFieldId}`
  const targetPortId = `${relationship.toEntityId}-${relationship.toFieldId}`
  
  // 获取源节点和目标节点来计算锚点
  const sourceNode = currentNodeMap.value.get(relationship.fromEntityId)
  const targetNode = currentNodeMap.value.get(relationship.toEntityId)
  
  if (sourceNode && targetNode) {
    // 使用统一的锚点计算逻辑
    const anchorResult = calculateRelationPathAnchor(sourceNode, targetNode)
    const sourceAnchor = anchorResult.source
    const targetAnchor = anchorResult.target
    
    // 计算避让偏移量
    const offset = calculateEdgeOffset(sourceNode, targetNode, relationship)
    
    // 更新路由器配置以应用偏移量
    edge.setRouter({
      name: 'orth',
      args: {
        padding: {
          left: Math.abs(50 - offset),
          right: Math.abs(50 - offset),
          top: 1000, // 设置很大的值阻止从上方连接
          bottom: 1000 // 设置很大的值阻止从下方连接
        }
      }
    })
    
    // 更新连线的源和目标（仅当发生变化时）
    const oldSource = edge.getSource()
    const oldTarget = edge.getTarget()
    const needUpdateSource = oldSource?.cell !== relationship.fromEntityId || (oldSource as any)?.port !== sourcePortId
    const needUpdateTarget = oldTarget?.cell !== relationship.toEntityId || (oldTarget as any)?.port !== targetPortId
    if (needUpdateSource) {
      edge.setSource({ cell: relationship.fromEntityId, port: sourcePortId, anchor: sourceAnchor })
    }
    if (needUpdateTarget) {
      edge.setTarget({ cell: relationship.toEntityId, port: targetPortId, anchor: targetAnchor })
    }
  }
}

// 创建单个实体节点
function createEntityNode(entity: Entity) {
  if (!currentGraph.value) return
  
  // 确保实体有正确的尺寸
  const entityWithSize = { ...entity }
  entityWithSize.fields = cacheAllFields(entity) as Field[]
  updateEntitySize(entityWithSize)
  emit('updateEntitySize', entityWithSize)

  // 创建X6节点，只提供基本信息和 nodedata
  const node = currentGraph.value.addNode({
    shape: 'entityNode',
    id: entityWithSize.id,
    x: entityWithSize.x,
    y: entityWithSize.y,
    width: entityWithSize.width,
    height: entityWithSize.height,
    ports: {
      items: entityWithSize.fields.map((field) => ({
        id: entityWithSize.id + '-' + field.id,
        group: 'fieldPort',
        args: {
          ...field
        }
      }))
    }
  })
  
  // 根据 nodedata 更新节点样式
  updateNodeFromData(node, entityWithSize)
  
  // 存储节点映射到当前视图
  currentNodeMap.value.set(entityWithSize.id, node)
}

// 更新现有实体节点
function updateEntityNode(entity: Entity, node: Node) {
  if (!currentGraph.value) return
  
  // 确保实体有正确的尺寸
  const entityWithSize = { ...entity }
  entityWithSize.fields = cacheAllFields(entity) as Field[]
  updateEntitySize(entityWithSize)
  emit('updateEntitySize', entityWithSize)
  
  // 更新节点位置和尺寸
  node.setPosition(entityWithSize.x, entityWithSize.y)
  node.setSize(entityWithSize.width, entityWithSize.height)
  
  // 更新端口 - 增量更新，避免连线消失
  // 先按 serialNo 排序字段，确保端口按正确顺序显示
  const newPorts = entityWithSize.fields.map((field) => ({
    id: entityWithSize.id + '-' + field.id,
    group: 'fieldPort',
    args: { ...field }
  }))
  
  // 获取当前端口
  const currentPorts = node.getPorts()
  const currentPortIds = new Set(currentPorts.map((port: any) => port.id))
  const newPortIds = new Set(newPorts.map(port => port.id))
  
  // 删除不再需要的端口
  currentPorts.forEach((port: any) => {
    if (port.id && typeof port.id === 'string' && !newPortIds.has(port.id)) {
      node.removePort(port.id)
    }
  })
  
  // 添加新端口或更新现有端口的 args
  newPorts.forEach((port, index) => {
    if (currentPortIds.has(port.id)) {
      // 更新现有端口的 args - 使用 setPortProp 方法，过滤掉 undefined 的 scale 和 length
      node.setPortProp(port.id, 'args', port.args)
    } else {
      // 添加新端口
      node.addPort(port)
    }
  })
  
  // 重新设置端口顺序 - 通过重新设置所有端口来确保正确的顺序
  const allCurrentPorts = node.getPorts()
  const reorderedPorts = newPorts.map(newPort => {
    const existingPort = allCurrentPorts.find((p: any) => p.id === newPort.id)
    return existingPort || newPort
  })
  
  // 使用 X6 的内部方法重新设置端口顺序
  if (reorderedPorts.length > 0) {
    node.prop('ports/items', reorderedPorts)
  }
  
  // 根据 nodedata 更新节点样式
  updateNodeFromData(node, entityWithSize)
}

// 根据 nodedata 更新节点样式
function updateNodeFromData(node: Node, entityWithSize: Entity) {
  if (!node || !entityWithSize) return
  
  // 更新节点基本属性
  node.attr({
    body: {
      width: entityWithSize.width,
      height: entityWithSize.height,
      fill: entityWithSize.backgroundColor || (props.isDarkTheme ? '#141313' : '#ffffff'),
      stroke: entityWithSize.borderColor || (props.isDarkTheme ? '#676767' : '#24292e'),

    },
    title: {
      x: entityWithSize.width / 2,
      fill: entityWithSize.fontColor || (props.isDarkTheme ? '#ffffff' : '#24292e'),
      text: entityWithSize.name
    },
    separator: {
      x2: entityWithSize.width,
      stroke: entityWithSize.borderColor || (props.isDarkTheme ? '#676767' : '#24292e'),
    }
  })
  
  // 更新字段端口样式 - 使用端口的args中的字段信息
  const ports = node.getPorts()
  const fieldPorts = ports.filter((port: any) => port.group === 'fieldPort' && port.args)
  fieldPorts.forEach((port: any, index: number) => {
    let { id, name, type, isPrimaryKey, isUnique, isRequired, length, scale } = port.args
    const field = entityWithSize.fields.filter((field) => field.id === id)[0]
    scale = field.scale
    length = field.length
    if (name) {
      const fieldHeight = 25 // 与 PortLayout 的 LINE_HEIGHT 保持一致
      const isLastField = index === fieldPorts.length - 1 // 判断是否为最后一个字段
      node.portProp(entityWithSize.id + '-' + id, 'attrs', {
        fieldBody: {
          // 只更新宽度；位置由 PortLayout 决定。减去边框避免视觉溢出
          width: Math.max(0, entityWithSize.width - 2)
        },
        icon: {
          d: isPrimaryKey
            ? 'M15.6809 5.34814C14.0521 5.34814 12.7265 6.66395 12.7265 8.29353C12.7265 9.92311 14.0521 11.2389 15.6809 11.2389C17.3097 11.2389 18.6353 9.92311 18.6353 8.29353C18.6353 6.66395 17.3097 5.34814 15.6809 5.34814ZM14.2265 8.29353C14.2265 7.49816 14.8748 6.84814 15.6809 6.84814C16.487 6.84814 17.1353 7.49816 17.1353 8.29353C17.1353 9.0889 16.487 9.73891 15.6809 9.73891C14.8748 9.73891 14.2265 9.0889 14.2265 8.29353Z M9.52998 20.8783C9.86298 20.414 9.97017 19.9429 9.96222 19.5233C10.3544 19.6387 10.7424 19.6533 11.1141 19.5828C11.8825 19.437 12.4511 18.9512 12.7527 18.5507L12.758 18.5437L12.7631 18.5366C13.2883 17.8043 13.2872 17.0543 13.1586 16.5164C13.0956 16.2528 13.0021 16.0361 12.9245 15.8846C12.8853 15.8081 12.849 15.746 12.8207 15.7005C12.8132 15.6885 12.8063 15.6775 12.7999 15.6677C12.7112 15.5021 12.6111 15.3719 12.5269 15.2737L12.5359 15.2647L13.0001 14.8024C13.3817 14.9849 13.7957 15.0999 14.1583 15.1749C14.744 15.2962 15.3171 15.3369 15.6807 15.3369C19.582 15.3369 22.75 12.1863 22.75 8.29344C22.75 4.40056 19.582 1.25 15.6807 1.25C11.7794 1.25 8.61144 4.40056 8.61144 8.29344C8.61144 9.2105 8.82018 9.99588 9.02588 10.549C9.07825 10.6898 9.13081 10.8166 9.18035 10.9279L1.92511 18.1535C1.66869 18.4089 1.36789 18.853 1.27697 19.4092C1.17837 20.0124 1.34031 20.6829 1.92511 21.2654L2.80687 22.1435C2.82046 22.1571 2.83457 22.1701 2.84916 22.1825C3.10385 22.3999 3.53164 22.6513 4.04572 22.7273C4.59712 22.8088 5.23527 22.6818 5.77579 22.1435L6.34232 21.5793C6.87523 21.8849 7.43853 21.9545 7.95941 21.8548C8.63497 21.7254 9.19686 21.321 9.51964 20.8924L9.5249 20.8854L9.52998 20.8783ZM10.1114 8.29344C10.1114 5.23477 12.602 2.75 15.6807 2.75C18.7594 2.75 21.25 5.23477 21.25 8.29344C21.25 11.3521 18.7594 13.8369 15.6807 13.8369C15.4075 13.8369 14.9372 13.8044 14.4623 13.7061C13.9654 13.6032 13.5752 13.4504 13.3674 13.2779C13.0699 13.031 12.6332 13.0508 12.3592 13.3237L11.4774 14.2019C11.2757 14.4028 11.0818 14.6305 10.9794 14.8933C10.8499 15.2261 10.8912 15.5463 11.0394 15.8121C11.1273 15.9697 11.2689 16.1202 11.3278 16.183L11.3476 16.2042C11.4173 16.2811 11.4555 16.3314 11.4834 16.387L11.5098 16.4397L11.54 16.4817L11.5468 16.4924C11.5558 16.507 11.5712 16.533 11.5895 16.5685C11.6267 16.6412 11.6709 16.7445 11.6997 16.8652C11.7544 17.0937 11.7538 17.3656 11.5494 17.6551C11.4087 17.8384 11.1424 18.0506 10.8345 18.1091C10.5769 18.1579 10.1571 18.1261 9.59673 17.5681C9.30409 17.2766 8.83089 17.2766 8.53825 17.5681L8.24433 17.8608C7.96748 18.1365 7.94891 18.5782 8.20054 18.8761C8.20194 18.8778 8.2058 18.8826 8.2116 18.8903C8.22363 18.9062 8.24339 18.9336 8.2668 18.9704C8.31483 19.0461 8.37128 19.1508 8.41138 19.2706C8.48694 19.4963 8.49882 19.7374 8.31639 19.9966C8.19643 20.1519 7.95303 20.3287 7.67726 20.3815C7.4429 20.4264 7.14284 20.3931 6.8045 20.0562C6.51186 19.7647 6.03866 19.7647 5.74602 20.0562L4.7173 21.0807C4.55241 21.2449 4.4068 21.2643 4.26505 21.2434C4.09729 21.2186 3.93333 21.1293 3.84077 21.0562L2.9836 20.2025C2.74543 19.9653 2.73591 19.7821 2.75733 19.6511C2.78643 19.4731 2.89711 19.3025 2.9836 19.2163L10.6279 11.6033C10.8747 11.3575 10.9185 10.9735 10.7333 10.6784L10.7311 10.6748C10.7284 10.6703 10.7232 10.6615 10.7158 10.6487C10.7012 10.6231 10.6781 10.5814 10.6494 10.5251C10.5918 10.4123 10.5122 10.2423 10.4318 10.0262C10.2701 9.59135 10.1114 8.98632 10.1114 8.29344ZM8.20054 18.8761C8.20192 18.8777 8.2033 18.8793 8.20469 18.881L8.20354 18.8796L8.20054 18.8761Z'
            : isUnique
            ? 'M12 0.75C5.787 0.75 0.75 5.787 0.75 12s5.037 11.25 11.25 11.25s11.25-5.037 11.25-11.25S18.213 0.75 12 0.75m4.366 12.357c0 1.156-.179 2.057-.538 2.702c-.668 1.181-1.943 1.771-3.826 1.771c-1.882 0-3.159-.59-3.832-1.771c-.359-.644-.538-1.546-.538-2.702V6.419h2.31v6.683c0 .747.089 1.293.265 1.637c.275.61.873.914 1.795.914c.917 0 1.513-.305 1.788-.914c.177-.344.264-.89.264-1.637V6.419h2.311v6.688z'
            : '',
          fill: entityWithSize.fontColor || (props.isDarkTheme ? '#ffffff' : '#24292e'),
          // 垂直居中：相对 fieldBody 的本地坐标
          transform: `translate(5, ${fieldHeight / 2 - 8}) scale(0.7)`,
          visibility: isPrimaryKey || isUnique ? 'visible' : 'hidden'
        },
        requiredIcon: {
          d: !isPrimaryKey && isRequired
            ? 'M12.0001 1.25C12.4143 1.25 12.7501 1.58579 12.7501 2V4.18934L14.4697 2.46967C14.7626 2.17678 15.2375 2.17678 15.5304 2.46967C15.8233 2.76256 15.8233 3.23744 15.5304 3.53033L12.7501 6.31066V10.701L16.5521 8.50588L17.5698 4.70788C17.677 4.30778 18.0882 4.07034 18.4883 4.17755C18.8884 4.28475 19.1259 4.69601 19.0187 5.09611L18.3892 7.44522L20.2852 6.35055C20.644 6.14344 21.1026 6.26635 21.3098 6.62507C21.5169 6.98379 21.394 7.44248 21.0352 7.64959L19.1392 8.74426L21.4883 9.3737C21.8884 9.48091 22.1259 9.89216 22.0187 10.2923C21.9114 10.6924 21.5002 10.9298 21.1001 10.8226L17.3021 9.80492L13.5001 12L17.3023 14.1952L21.1003 13.1775C21.5004 13.0703 21.9116 13.3078 22.0189 13.7079C22.1261 14.108 21.8886 14.5192 21.4885 14.6264L19.1394 15.2559L21.0354 16.3505C21.3942 16.5577 21.5171 17.0163 21.31 17.3751C21.1028 17.7338 20.6442 17.8567 20.2854 17.6496L18.3894 16.5549L19.0189 18.904C19.1261 19.3041 18.8886 19.7154 18.4885 19.8226C18.0884 19.9298 17.6772 19.6924 17.57 19.2923L16.5523 15.4943L12.7501 13.299V17.6893L15.5304 20.4697C15.8233 20.7626 15.8233 21.2374 15.5304 21.5303C15.2375 21.8232 14.7626 21.8232 14.4697 21.5303L12.7501 19.8107V22C12.7501 22.4142 12.4143 22.75 12.0001 22.75C11.5859 22.75 11.2501 22.4142 11.2501 22V19.8107L9.53041 21.5303C9.23752 21.8232 8.76264 21.8232 8.46975 21.5303C8.17686 21.2374 8.17686 20.7626 8.46975 20.4697L11.2501 17.6893V13.299L7.44787 15.4943L6.4302 19.2923C6.32299 19.6924 5.91174 19.9298 5.51164 19.8226C5.11154 19.7154 4.8741 19.3041 4.98131 18.904L5.61075 16.5549L3.71473 17.6496C3.35601 17.8567 2.89731 17.7338 2.69021 17.3751C2.4831 17.0163 2.60601 16.5577 2.96473 16.3505L4.86075 15.2559L2.51164 14.6264C2.11154 14.5192 1.8741 14.108 1.98131 13.7079C2.08851 13.3078 2.49977 13.0703 2.89987 13.1775L6.69787 14.1952L10.5001 12L6.69806 9.80492L2.90006 10.8226C2.49996 10.9298 2.08871 10.6924 1.9815 10.2923C1.8743 9.89216 2.11173 9.48091 2.51183 9.3737L4.86095 8.74426L2.96492 7.64959C2.6062 7.44248 2.4833 6.98379 2.6904 6.62507C2.89751 6.26635 3.3562 6.14344 3.71492 6.35055L5.61095 7.44522L4.9815 5.09611C4.8743 4.69601 5.11173 4.28475 5.51183 4.17755C5.91193 4.07034 6.32319 4.30778 6.43039 4.70788L7.44806 8.50588L11.2501 10.701V6.31066L8.46975 3.53033C8.17686 3.23744 8.17686 2.76256 8.46975 2.46967C8.76264 2.17678 9.23752 2.17678 9.53041 2.46967L11.2501 4.18934V2C11.2501 1.58579 11.5859 1.25 12.0001 1.25Z'
            : '',
          fill: entityWithSize.fontColor || (props.isDarkTheme ? '#ffffff' : '#24292e'),
          transform: (() => {
            // 计算图标的x轴偏移量
            let xOffset = 5;
            if (isPrimaryKey || isUnique) {
              if (isRequired) {
                xOffset = 25;
              }
            }
            return `translate(${xOffset}, ${fieldHeight / 2 - 8}) scale(0.7)`;
          })(),
          visibility: isRequired ? 'visible' : 'hidden'
        },
        fieldName: {
          refX: (() => {
            // 根据字段类型计算X轴偏移量
            if (isPrimaryKey) {
              return 25;
            }
            if (isUnique) {
              return isRequired ? 45 : 25;
            }
            return isRequired ? 25 : 10;
          })(),
          refY: fieldHeight / 4,
          fill: entityWithSize.fontColor || (props.isDarkTheme ? '#ffffff' : '#24292e'),
          text: name
        },
        fieldType: {
          refY: fieldHeight / 4,
          fill: entityWithSize.fontColor || (props.isDarkTheme ? '#cccccc' : '#666666'),
          text: (() => {
            // 根据字段类型和长度生成显示文本
            if (scale !== undefined && scale !== null && scale !== '') {
              return `${type}(${length || ''},${scale})`
            }
            if (length !== undefined && length !== null && length !== '') {
              return `${type}(${length})`
            }
            return `${type}`
          })()
        },
        fieldSeparator: {
          y1: fieldHeight,
          x2: entityWithSize.width,
          y2: fieldHeight,
          stroke: entityWithSize.borderColor || (props.isDarkTheme ? '#676767' : '#e1e4e8'),
          visibility: isLastField ? 'hidden' : 'visible' // 最后一个字段隐藏分隔线
        }
      })
    }
  })
}

// 更新所有节点的主题样式
function updateAllNodesTheme() {
  if (!currentGraph.value) return
  
  // 遍历所有节点，更新其主题相关的样式
  currentGraph.value.getNodes().forEach(node => {
    const entity = allEntities.value.find(e => e.id === node.id)
    if (entity) {
      // 重新应用节点样式，这会使用当前的主题设置
      updateNodeFromData(node, entity)
    }
  })
  
  // 更新所有连线的主题样式
  currentGraph.value.getEdges().forEach(edge => {
    edge.attr({
      line: {
        stroke: props.isDarkTheme ? '#ffffff' : '#24292e'
      }
    })
  })
}

// 设置事件监听器
function setupEventListeners(graphInstance: Graph) {
  // 监听缩放变化
  graphInstance.on('scale', ({ sx }) => {
    if (props.canvasState) {
      // 确保缩放值在约束范围内
      const minZoom = props.canvasState.MIN_ZOOM || 0.1
      const maxZoom = props.canvasState.MAX_ZOOM || 3
      const constrainedZoom = Math.max(minZoom, Math.min(maxZoom, sx))
      
      // 如果缩放值超出约束，立即调整画布缩放
      if (Math.abs(sx - constrainedZoom) > 0.01) {
        graphInstance.zoomTo(constrainedZoom)
      }
      
      // 只有当约束后的缩放值与当前canvasState不同时才发出更新事件
      if (Math.abs(props.canvasState.zoom - constrainedZoom) > 0.01) {
        emit('zoomChange', constrainedZoom)
      }
    }
  })
  
  // 监听平移变化
  graphInstance.on('translate', ({ tx, ty }) => {
    if (props.canvasState && 
        (Math.abs(props.canvasState.panX - tx) > 1 || Math.abs(props.canvasState.panY - ty) > 1)) {
      emit('panChange', Math.round(tx), Math.round(ty))
    }
  })
  
  // 监听选择变化（仅在选择框模式下）
  graphInstance.on('selection:changed', ({ selected, removed }: { selected: any[], removed: any[] }) => {
    if (props.canvasState.isDragMode === false) {
      // 将X6的选中节点转换为Entity格式，只包含显示的节点
      const selectedEntities: Entity[] = selected.map((cell: any) => {
        // 如果是节点且节点可见，尝试获取其数据
        if (cell.isNode && cell.isNode() && cell.isVisible()) {
          const data = props.entities.find((entity) => entity.id === cell.id)
          return {
            id: cell.id,
            name: data?.name || cell.id,
            x: cell.position().x,
            y: cell.position().y,
            width: cell.size().width,
            height: cell.size().height,
            fields: data?.fields || [],
            ...data
          } as Entity
        }
        return null
      }).filter(Boolean) as Entity[]

      // 发出选择变化事件
      emit('selectionChange', selectedEntities)
    }

    // 只有在分屏模式下才设置焦点
    if(props.isSplitScreen) {
      emit('setFocusPane', 'canvas')
    }
  })
  
  // 监听节点双击事件
  graphInstance.on('cell:dblclick', ({ cell, e }: { cell: any, e: any }) => {
    if (cell.isNode()) {
      const entity = props.entities.find((entity) => entity.id === cell.id)
      if (entity) {
        emit('entityDoubleClick', entity, e as MouseEvent)
      } 
    }else if(cell.isEdge()){
      const relation = props.relationships.find((relation) => relation.id === cell.id)
      console.log('relation', relation)
      if (relation) {
        emit('editRelation', relation)
      }
    }
  })
  
  // 监听节点右键点击事件
  graphInstance.on('cell:contextmenu', ({ cell, e }: { cell: any, e: any }) => {
    if (cell.isNode()) {
      // 右键点击时选中节点
      const selection = graphInstance.getPlugin('selection') as Selection
      if (selection && selection.isEnabled()) {
        // 如果不是多选模式（没有按住Ctrl/Cmd），先清除其他选择
        if (!e.ctrlKey && !e.metaKey) {
          graphInstance.resetSelection()
          emit('selectionChange', [])
        }
        // 选中当前节点
        graphInstance.select(cell)
      }
      
      const entity = props.entities.find((entity) => entity.id === cell.id)
      if (entity) {
        emit('entityRightClick', entity, e as MouseEvent)
      }
    }else if(cell.isEdge()){
      const relation = props.relationships.find((relation) => relation.id === cell.id)
      if (relation) {
        emit('editRelation', relation)
      }
    }
  })

  // 监听连线鼠标移入事件
  graphInstance.on('edge:mouseenter', ({ cell }) => {
    if (cell.isEdge()) {
      cell.attr({
        line: {
          stroke: props.isDarkTheme ? '#bb86fc' : '#0366d6',
          strokeWidth: 2
        }
      })
    }
  })

  // 监听连线鼠标移出事件
  graphInstance.on('edge:mouseleave', ({ cell }) => {
    if (cell.isEdge()) {
      cell.attr({
        line: {
          stroke: props.isDarkTheme ? '#ffffff' : '#24292e',
          strokeWidth: 1
        }
      })
    }
  })

  // 监听节点拖拽完成事件
  graphInstance.on('node:moved', (args: any) => {
    const node = args.node || args.current
    if (node && node.position) {
      const entity = props.entities.find((entity) => entity.id === node.id)
      if (entity) {
        const position = node.position()
        const updatedEntity = { ...entity, x: position.x, y: position.y }
        emit('updateEntityPosition', updatedEntity)
      }
    }

    // 移动过程中和移动后都刷新相关连线的路由
    const movedNodeId = (args.node || args.current)?.id
    if (movedNodeId && currentGraph.value) {
      // 仅更新与该节点相关的边，避免全量重建
      props.relationships?.forEach((rel) => {
        if (rel.fromEntityId === movedNodeId || rel.toEntityId === movedNodeId) {
          const edge = currentEdgeMap.value.get(rel.id)
          if (edge) {
            // 获取源节点和目标节点来重新计算锚点
            const sourceNode = currentNodeMap.value.get(rel.fromEntityId)
            const targetNode = currentNodeMap.value.get(rel.toEntityId)
            
            if(sourceNode && targetNode){
              const result = calculateRelationPathAnchor(sourceNode, targetNode)
               const sourceAnchor = result.source
               const targetAnchor = result.target
               console.log('node:moved1', rel.id, result)
               
               // 计算新的避让偏移量
               const offset = calculateEdgeOffset(sourceNode, targetNode, rel)
               console.log('node:moved: offset', offset)
               
               // 更新路由器配置
               edge.setRouter({
                 name: 'orth',
                 args: {
                   padding: {
                     left: Math.abs(50 - offset),
                     right: Math.abs(50 - offset),
                     top: 1000, // 设置很大的值阻止从上方连接
                     bottom: 1000 // 设置很大的值阻止从下方连接
                   }
                 }
               })
              
              // 重新设置源和目标，包含新的锚点
              const sourcePortId = `${rel.fromEntityId}-${rel.fromFieldId}`
              const targetPortId = `${rel.toEntityId}-${rel.toFieldId}`
              console.log('node:moved2', sourceAnchor, targetAnchor)
              edge.setSource({ cell: rel.fromEntityId, port: sourcePortId, anchor: sourceAnchor })
              edge.setTarget({ cell: rel.toEntityId, port: targetPortId, anchor: targetAnchor })
              console.log('node:moved3', edge.getSource(), edge.getTarget())
            }
          }
        }
      })
    }

    // 只有在分屏模式下才设置焦点
    if(props.isSplitScreen) {
      emit('setFocusPane', 'canvas')
    }
  })
  
  // 监听画布点击
  graphInstance.on('blank:click', (event: any) => {
    // Pass the original DOM event or the X6 event object
    const mouseEvent = event.e || event
    emit('canvasClick', mouseEvent)
  })
  
  // 监听画布右键点击
  graphInstance.on('blank:contextmenu', (event: any) => {
    // Pass the original DOM event or create a compatible event object
    const mouseEvent = event.e || event
    emit('canvasRightClick', mouseEvent)
  })
}

// 计算关系路径锚点
function calculateRelationPathAnchor(sourceNode: Node, targetNode: Node) {
  if (!sourceNode || !targetNode) {
    return { source: 'right', target: 'left' }
  }

  const sourceBBox = sourceNode.getBBox()
  const targetBBox = targetNode.getBBox()
  console.log('calculateRelationPathAnchor', sourceBBox.x, targetBBox.x, sourceBBox.width, targetBBox.width)
  const sourceCenterX = sourceBBox.x + sourceBBox.width / 2
  const targetCenterX = targetBBox.x + targetBBox.width / 2
  console.log('calculateRelationPathAnchor', sourceCenterX, targetCenterX)
  
  // 计算节点间的距离阈值
  const distanceThreshold = sourceBBox.width / 2 + targetBBox.width / 2 + 100
  const isNodesClose = Math.abs(sourceCenterX - targetCenterX) < distanceThreshold
  
  // 根据节点位置关系确定锚点
  const isSourceLeftOfTarget = sourceCenterX < targetCenterX
  console.log('calculateRelationPathAnchor', isSourceLeftOfTarget, isNodesClose)
  return {
    source: isSourceLeftOfTarget ? (isNodesClose ? 'right' : 'right') : (isNodesClose ? 'left' : 'left'),
    target: isSourceLeftOfTarget ? (isNodesClose ? 'right' : 'left') : (isNodesClose ? 'left' : 'right'),
    isNodesClose: isNodesClose
  }
}

// 计算连线避让偏移量
function calculateEdgeOffset(sourceNode: Node, targetNode: Node, currentRelationship: any): number {
  if (!currentGraph.value || !sourceNode || !targetNode) {
    return 0
  }

  // 获取所有现有的连线
  const allEdges = currentGraph.value.getEdges()
  
  // 找到连接相同两个节点的并行连线
  const parallelEdges = allEdges.filter(edge => {
    const source = edge.getSource()
    const target = edge.getTarget()
    
    // 类型安全检查
    if (!source || !target || typeof source !== 'object' || typeof target !== 'object') {
      return false
    }
    
    // 检查是否有cell属性
    const sourceCell = (source as any).cell
    const targetCell = (target as any).cell
    
    if (!sourceCell || !targetCell) {
      return false
    }
    
    // 检查是否连接相同的节点对（双向匹配）
    return (
      (sourceCell === sourceNode.id && targetCell === targetNode.id) ||
      (sourceCell === targetNode.id && targetCell === sourceNode.id)
    )
  })

  // 如果只有一条连线或没有并行连线，不需要偏移
  if (parallelEdges.length <= 1) {
    return 0
  }

  // 找到当前连线在并行连线中的索引
  const currentEdgeIndex = parallelEdges.findIndex(edge => edge.id === currentRelationship.id)
  
  // 如果找不到当前连线（新建连线的情况），使用并行连线的数量作为索引
  const index = currentEdgeIndex >= 0 ? currentEdgeIndex : parallelEdges.length
  
  // 计算偏移量：使用交替正负偏移，避免所有连线都偏向一侧
  const baseOffset = 30 // 基础偏移量
  const offsetMultiplier = Math.floor((index + 1) / 2) // 偏移倍数
  const isPositive = index % 2 === 0 // 偶数索引向上偏移，奇数索引向下偏移
  
  return isPositive ? offsetMultiplier * baseOffset : -offsetMultiplier * baseOffset
}

// 判断是否有父实体并且缓存中存在父实体的字段
function cacheAllFields(entity: Entity) {
  let allFields = props.allFieldsCache[entity.id]
  if(!allFields || allFields.length === 0) {
    if (entity.parentEntityId) {
      allFields = [...getAllParentFields(allEntities.value, entity.parentEntityId), ...entity.fields]
    } else {
      allFields = entity.fields
    }
    props.allFieldsCache[entity.id] = allFields
  }

  return allFields
}

// 初始化图实例
onMounted(() => {
  initializeGraph()

  onBeforeUnmount(() => {
    Graph.unregisterNode('entityNode')
  })
})

// 暴露方法给父组件
defineExpose({
  resetZoom() {
    if (currentGraph.value) {
      // 获取画布内容的边界框
      const contentBBox = currentGraph.value.getContentBBox()
      if (contentBBox && contentBBox.width > 0 && contentBBox.height > 0) {
        // 添加一些边距并使用正确的 API
        const padding = 50
        currentGraph.value.zoomToFit({
          padding: padding,
          maxScale: 2, // 最大缩放比例
          minScale: 0.1 // 最小缩放比例
        })
      } else {
        // 如果没有内容，重置到默认视图
        currentGraph.value.centerContent()
        currentGraph.value.zoomTo(1)
      }
    }
  }
})
</script>

<style scoped>
.x6-ds-canvas {
  position: relative;
  width: 100%;
  height: 100%;
  outline: none; /* 移除获得焦点时的蓝色外框 */
}
.grid-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}
.dark-theme .grid-border{
  stroke: #2a2a2a;
}
.dark-theme .vertical-line{
  stroke: #2a2a2a;
}
.dark-theme .horizontal-line{
  stroke: #2a2a2a;
}
/* 拖拽模式：显示抓手指针 */
.x6-ds-canvas.drag-mode,
.x6-ds-canvas.drag-mode .x6-canvas,
.x6-ds-canvas.drag-mode .x6-canvas * {
  cursor: grab !important;
}

.x6-ds-canvas.drag-mode:active,
.x6-ds-canvas.drag-mode:active .x6-canvas,
.x6-ds-canvas.drag-mode:active .x6-canvas * {
  cursor: grabbing !important;
}

/* 选择模式：显示默认箭头指针 */
.x6-ds-canvas.select-mode,
.x6-ds-canvas.select-mode .x6-canvas,
.x6-ds-canvas.select-mode .x6-canvas * {
  cursor: default !important;
}

.dark-theme .x6-ds-canvas {
  background: #101010;
}

/* Selection 插件样式 - 浅色主题 */
:deep(.x6-widget-selection) {
  width: 0px;
  height: 0px;
}

:deep(.x6-widget-selection-inner) {
  width: 0px;
  height: 0px;
  border: 0px;
  box-shadow: none;
}


:deep(.x6-widget-selection-rubberband){
  border: 1px dashed #0366d6;
  background-color: rgba(3, 102, 214, 0.2);
  pointer-events: none;
}

:deep(.x6-widget-selection-box) {
  border: 2px solid #0366d6;
  border-radius: 4px;
  background-color: transparent;
}

/* Selection 插件样式 - 暗色主题 */
.dark-theme :deep(.x6-widget-selection-rubberband){
  border: 1px dashed #bb86fc;
  background-color: rgba(187, 134, 252, 0.2);
  pointer-events: none;
}

.dark-theme :deep(.x6-widget-selection-box) {
  border: 2px solid #bb86fc;
  background-color: transparent;
  border-radius: 4px;
  box-shadow: none;
}
</style>