<template>
  <div 
    ref="canvasContainer" 
    class="canvas-container"
    @contextmenu.prevent="handleCanvasRightClick"
    @wheel.prevent="handleWheel"
    @keydown.prevent="handleKeyDown"
    @touchstart.prevent="handleTouchStart"
    @touchmove.prevent="handleTouchMove"
    @touchend.prevent="handleTouchEnd">

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
    <svg
      ref="svgCanvas"
      class="canvas-svg"
      width="100%"
      height="100%"
      :viewBox="`${canvasState.panX} ${canvasState.panY} ${canvasWidth} ${canvasHeight}`"
      @mousedown="handleCanvasMouseDown">
      <!-- 关系连线 -->
      <g class="relations-layer">
        <RelationLine
          v-for="relationship in store.relationships"
          :relation="relationship"
          :key="relationship.id"
          :relationship="relationship"
          :path="getRelationPath(relationship)"
          :label="relationship.name || ''"
          :labelPosition="getRelationLabelPosition(relationship)"
          marker-id="arrowhead"
        />
      </g>
      
      <!-- 箭头标记 -->
      <defs>
        <marker id="arrowhead" markerWidth="10" markerHeight="7" 
                refX="9" refY="3.5" orient="auto">
          <polygon points="0 0, 10 3.5, 0 7" fill="#666" />
        </marker>
      </defs>
      
      <!-- 实体 -->
      <g class="entities-layer">
        <EntityNode
          v-for="entity in props.entities"
          :key="entity.id"
          :entity="entity"
          :selected="isEntitySelected(entity)"
          :visibleEntities="props.entities"
          :dragTransform="draggingEntityIds.includes(entity.id) ? `translate(${dragEntity[entity.id]?.x || entity.x},${dragEntity[entity.id]?.y || entity.y})` : undefined"
          @dblclick="handleEntityDoubleClick"
          @mousedown="handleEntityMouseDown"
          @touchstart="handleEntityTouchStart"
          @touchmove="handleEntityTouchMove"
          @touchend="handleEntityTouchEnd"/>
      </g>

      <rect
        v-if="selectionBox.visible"
        :x="selectionBox.x"
        :y="selectionBox.y"
        :width="selectionBox.width"
        :height="selectionBox.height"
        stroke-width="0.5"
        stroke-dasharray="5,5"
        class="selection-box"/>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive, computed } from 'vue'
import { useDSDiagramStore } from '../stores/dsDiagram'
import type { Entity, Relationship, CanvasState } from '../types/entity'
import EntityNode from './EntityNode.vue'
import RelationLine from './RelationLine.vue'
import { useI18n } from 'vue-i18n'
import { isDataView } from 'util/types'

interface Props {
  entities?: Entity[]
  canvasState?: CanvasState
  selectedEntities?: Entity[]
}

const props = withDefaults(defineProps<Props>(), {
  canvasState: () => ({
    zoom: 1,
    panX: 0,
    panY: 0,
    showGrid: true
  }),
  selectedEntities: () => [],
  entities: () => []
})

const emit = defineEmits<{
  'entityDoubleClick': [entity: Entity]
  'entityRightClick': [entity: Entity, event: MouseEvent]
  'canvasClick': [event: MouseEvent]
  'canvasRightClick': [event: MouseEvent]
  'selectionChange': [entities: Entity[]]
  'hideContextMenu': []
  'copyEntity': []
  'pasteEntity': []
  'undo': []
  'redo': []
}>()

const { t: $t } = useI18n()
const store = useDSDiagramStore()

// 响应式数据
const canvasContainer = ref<HTMLDivElement>()
const svgCanvas = ref<SVGSVGElement>()

// 画布选择框拖拽状态
const draggingEntityIds = ref<string[]>([])
const dragStartPos = ref<Record<string, { x: number, y: number }>>({})
const dragEntity = reactive<Record<string, { x: number, y: number }>>({})

// 触摸状态
const isTouching = ref(false)
const touchStartTime = ref(0)
const lastTouchPos = ref({ x: 0, y: 0 })
const touchEntity = ref<Entity | null>(null)
const isMultiTouch = ref(false)
const initialPinchDistance = ref(0)
const currentZoom = ref(1)

let mouseDownPos = { x: 0, y: 0 }
let animationFrameId: number | null = null

// 框选状态
const selectionBox = ref({
  visible: false,
  startX: 0,
  startY: 0,
  x: 0,
  y: 0,
  width: 0,
  height: 0
})

// ------------------------------ 公用函数 开始 ------------------------------
// 判断实体是否选中
function isEntitySelected(entity: Entity): boolean {
  return props.selectedEntities.some(e => e.id === entity.id)
}

// 将屏幕坐标转换为画布坐标（考虑缩放因子）
function screenToCanvasCoords(clientX: number, clientY: number): { x: number, y: number } {
  const svg = svgCanvas.value!
  const rect = svg.getBoundingClientRect()
  const viewBox = svg.viewBox.baseVal
  // 计算鼠标在SVG中的相对位置
  const x = ((clientX - rect.left) / rect.width) * viewBox.width + viewBox.x
  const y = ((clientY - rect.top) / rect.height) * viewBox.height + viewBox.y
  return { x, y }
}
// ------------------------------ 公用函数 结束 ------------------------------

// ------------------------------ 框选状态 开始 ------------------------------
// 画布鼠标按下
function handleCanvasMouseDown(event: MouseEvent) {
  // 只允许左键
  if (event.button !== 0) return
  mouseDownPos = { x: event.clientX, y: event.clientY }
  const canvasCoords = screenToCanvasCoords(event.clientX, event.clientY)
  selectionBox.value = {
    visible: true,
    startX: canvasCoords.x,
    startY: canvasCoords.y,
    x: canvasCoords.x,
    y: canvasCoords.y,
    width: 0,
    height: 0
  }
  document.addEventListener('mousemove', handleSelectionBoxMouseMove)
  document.addEventListener('mouseup', handleSelectionBoxMouseUp)
}
// 框选鼠标移动
function handleSelectionBoxMouseMove(event: MouseEvent) {
  if (!selectionBox.value.visible) return
  const canvasCoords = screenToCanvasCoords(event.clientX, event.clientY)
  const x = Math.min(selectionBox.value.startX, canvasCoords.x)
  const y = Math.min(selectionBox.value.startY, canvasCoords.y)
  const width = Math.abs(canvasCoords.x - selectionBox.value.startX)
  const height = Math.abs(canvasCoords.y - selectionBox.value.startY)
  selectionBox.value = {
    ...selectionBox.value,
    x,
    y,
    width,
    height
  }
}
// 框选鼠标抬起
function handleSelectionBoxMouseUp(_event: MouseEvent) {
  if (!selectionBox.value.visible) return
  const moved =
    Math.abs(_event.clientX - mouseDownPos.x) > 3 ||
    Math.abs(_event.clientY - mouseDownPos.y) > 3
  if (!moved) {
    // 单击事件
    if(_event.button === 0) {
      handleCanvasClick(_event)
    }else if(_event.button === 2) {
      handleCanvasRightClick(_event)
    }
  }else{
    // 框选实体
    const { x, y, width, height } = selectionBox.value
    const selected = props.entities.filter(entity => {
      // 判断实体中心点是否在选择框内
      const cx = entity.x + entity.width / 2
      const cy = entity.y + entity.height / 2
      return (
        cx >= x &&
        cx <= x + width &&
        cy >= y &&
        cy <= y + height
      )
    })
    emit('selectionChange', selected)
    selectionBox.value.visible = false
  }
  document.removeEventListener('mousemove', handleSelectionBoxMouseMove)
  document.removeEventListener('mouseup', handleSelectionBoxMouseUp)
}
// ------------------------------ 框选状态 结束 ------------------------------

// ------------------------------ 画布事件处理 开始 ------------------------------
// 画布点击事件
function handleCanvasClick(event: MouseEvent) {
  // 检查点击的是否是实体或实体的子元素
  console.log(event.clientX, event.clientY)
  const target = event.target as Element
  const isEntityClick = target.closest('.entity') || target.closest('.entity-group')
  
  // 如果不是点击实体，则触发canvas-click事件
  if (!isEntityClick) {
    emit('canvasClick', event)
  }
}
// 画布右键点击事件
function handleCanvasRightClick(event: MouseEvent) {
  const clickedEntity = getEntityAtPosition(event.clientX, event.clientY)
  if (!clickedEntity) {
    emit('canvasRightClick', event)
  }
}
// 判断点击位置是否在某个 entity 上
function getEntityAtPosition(clientX: number, clientY: number): Entity | null {
  const canvasCoords = screenToCanvasCoords(clientX, clientY)
  
  return props.entities.find(entity => {
    return canvasCoords.x >= entity.x && 
           canvasCoords.x <= entity.x + entity.width &&
           canvasCoords.y >= entity.y && 
           canvasCoords.y <= entity.y + entity.height
  }) || null
}
// ------------------------------ 画布事件处理 结束 ------------------------------

// ------------------------------ 实体事件处理 开始 ------------------------------
// 实体点击事件
function handleEntityClick(entity: Entity, event: MouseEvent) {
  event.stopPropagation()
  if (event.ctrlKey || event.metaKey) {
    const index = props.selectedEntities.findIndex(e => e.id === entity.id)
    if (index === -1) {
      emit('selectionChange', [...props.selectedEntities, entity])
    } else {
      emit('selectionChange', props.selectedEntities.filter(e => e.id !== entity.id))
    }
  } else {
    emit('selectionChange', [entity])
  }
}
// 实体双击事件
function handleEntityDoubleClick(entity: Entity) {
  emit('entityDoubleClick', entity)
}
// 实体右键点击事件
function handleEntityRightClick(entity: Entity, event: MouseEvent) {
  event.stopPropagation()
  // 触发实体右键菜单事件
  emit('entityRightClick', entity, event)
}
// ------------------------------ 实体事件处理 结束 ------------------------------

// ------------------------------ 实体拖拽 开始 ------------------------------
// 实体鼠标按下
function handleEntityMouseDown(entity: Entity, event: MouseEvent) {
  event.preventDefault()
  event.stopPropagation()

  mouseDownPos = { x: event.clientX, y: event.clientY }
  draggingEntityIds.value = [entity.id]
  if(isEntitySelected(entity) || props.selectedEntities.length === 0) {  //如果当前实体没选中，但其他实体有选中，则不产生拖拽效果（必须选中当前节点），否则容易误操作

    const canvasCoords = screenToCanvasCoords(event.clientX, event.clientY)
    dragEntity[entity.id] = { x: entity.x, y: entity.y }
    dragStartPos.value[entity.id] = { 
      x: canvasCoords.x - entity.x, 
      y: canvasCoords.y - entity.y 
    }

    // 记录其他选中实体的初始位置
    props.selectedEntities.forEach(e => {
      if (e.id !== entity.id) {
        dragEntity[e.id] = { x: e.x, y: e.y }
        dragStartPos.value[e.id] = { 
          x: canvasCoords.x - e.x, 
          y: canvasCoords.y - e.y 
        }
        draggingEntityIds.value.push(e.id)
      }
    })
    
    document.addEventListener('mousemove', onDragMove)
  }
  document.addEventListener('mouseup', onDragEnd)
}
// 实体拖拽移动
function onDragMove(event: MouseEvent) {
  if (draggingEntityIds.value.length === 0) return
  
  // 拖拽时隐藏右键菜单
  emit('hideContextMenu')

  // 取消之前的动画帧
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }

  // 使用 requestAnimationFrame 确保流畅
  animationFrameId = requestAnimationFrame(() => {
    const canvasCoords = screenToCanvasCoords(event.clientX, event.clientY)
    draggingEntityIds.value.forEach(id => {
      const startPos = dragStartPos.value[id]
      if (startPos) {
        // 更新拖拽时的临时位置
        dragEntity[id] = {
          x: canvasCoords.x - startPos.x,
          y: canvasCoords.y - startPos.y
        }
      }
    })
  })
}
// 实体拖拽结束
function onDragEnd(_event: MouseEvent){
  if (draggingEntityIds.value.length === 0) return

  const moved =
    Math.abs(_event.clientX - mouseDownPos.x) > 3 ||
    Math.abs(_event.clientY - mouseDownPos.y) > 3

  if (!moved) {
    // 解决点击事件和拖拽事件冲突 单击事件触发时，拖拽事件也会触发，导致拖拽事件无法触发
    const entityObj = props.entities.find(e => e.id === draggingEntityIds.value[0])
    if (entityObj) {
      if(_event.button === 0) { // 左键
        handleEntityClick(entityObj, _event)
      }else if(_event.button === 2) { // 右键
        handleEntityRightClick(entityObj, _event)
      }
    }
  }else{
    if(Object.keys(dragEntity).length > 0){   //如果当前实体没选中，但其他实体有选中，则不产生拖拽效果（必须选中当前节点），否则容易误操作
      // 更新实体位置
      draggingEntityIds.value.forEach(id => {
        const entity = props.entities.find(e => e.id === id)
        if (entity) {
          entity.x = dragEntity[id].x
          entity.y = dragEntity[id].y
          store.updateEntity(entity)

          // 同步更新 selectedEntities 中的实体
          const selectedEntity = props.selectedEntities.find(e => e.id === id)
          if (selectedEntity) {
            selectedEntity.x = dragEntity[id].x
            selectedEntity.y = dragEntity[id].y
          }

          console.log(id, dragEntity, dragEntity[id].x,  dragEntity[id].y, entity.x, entity.y,selectedEntity?.x, selectedEntity?.y)
        }
      })
    }
  }

  draggingEntityIds.value = []
  Object.keys(dragEntity).forEach(key => delete dragEntity[key])
  mouseDownPos = { x: 0, y: 0 }

  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
    animationFrameId = null
  }

  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
}
// ------------------------------ 实体拖拽 结束 ------------------------------

// ------------------------------ 关系连线 开始 ------------------------------
// 获取关系路径
function getRelationPath(relationship: Relationship): string {
  const fromEntity = props.entities.find(e => e.id === relationship.fromEntityId)
  const toEntity = props.entities.find(e => e.id === relationship.toEntityId)
  
  if (!fromEntity || !toEntity) return ''
  
  const fromX = fromEntity.x + fromEntity.width / 2
  const fromY = fromEntity.y + fromEntity.height / 2
  const toX = toEntity.x + toEntity.width / 2
  const toY = toEntity.y + toEntity.height / 2
  
  return `M ${fromX} ${fromY} L ${toX} ${toY}`
}
// 获取关系标签位置
function getRelationLabelPosition(relationship: Relationship) {
  const fromEntity = props.entities.find(e => e.id === relationship.fromEntityId)
  const toEntity = props.entities.find(e => e.id === relationship.toEntityId)
  
  if (!fromEntity || !toEntity) return{ x: 0, y: 0 }
  
  const fromX = fromEntity.x + fromEntity.width / 2
  const fromY = fromEntity.y + fromEntity.height / 2
  const toX = toEntity.x + toEntity.width / 2
  const toY = toEntity.y + toEntity.height / 2
  
  return {
    x: (fromX + toX) / 2,
    y: (fromY + toY) / 2 - 5
  }
}
// ------------------------------ 关系连线 结束 ------------------------------

// ------------------------------ 键盘事件处理 开始 ------------------------------
// 缩放和键盘事件处理
function handleWheel(event: WheelEvent) {
  event.preventDefault()
  
  if (event.ctrlKey || event.metaKey) {
    // 缩放
    const delta = event.deltaY > 0 ? 0.9 : 1.1
    const newZoom = Math.max(0.1, Math.min(3, props.canvasState.zoom * delta))
    zoomChange(newZoom)
  } else {
    // 平移画布
    props.canvasState.panX += event.deltaX / props.canvasState.zoom
    props.canvasState.panY += event.deltaY / props.canvasState.zoom
  }
}
// 键盘事件处理
function handleKeyDown(event: KeyboardEvent) {
  // 阻止浏览器默认快捷键
  if (event.ctrlKey || event.metaKey) {
    switch (event.key) {
      case '+':
      case '=':
        event.preventDefault()
        zoomIn()
        break
      case '-':
        event.preventDefault()
        zoomOut()
        break
      case '0':
        event.preventDefault()
        resetZoom()
        break
      case 'a':
        event.preventDefault()
        // 全选实体
        emit('selectionChange', props.entities)
        break
      case 'c':
        event.preventDefault()
        if(props.selectedEntities.length > 0) {
          emit('copyEntity')
        }
        break
      case 'v':
        event.preventDefault()
        emit('pasteEntity')
        break
      case 'z':
        event.preventDefault()
        emit('undo')
        break
      case 'y':
        event.preventDefault()
        emit('redo')
        break
    }
  }
  
  // ESC键取消选择
  if (event.key === 'Escape') {
    emit('selectionChange', [])
  }
  
  // Delete键删除选中实体
  if ((event.key === 'Delete' || event.key === 'Backspace') && props.selectedEntities.length > 0 && confirm($t('messages.deleteEntitiesConfirm'))) {
    event.preventDefault()
    props.selectedEntities.forEach(entity => {
      store.deleteEntity(entity.id)
    })
    emit('selectionChange', [])
  }
}
//快捷键
function zoomIn() {
  zoomChange(Math.min(props.canvasState.zoom * 1.2, 3))
}
function zoomOut() {
  zoomChange(Math.max(props.canvasState.zoom / 1.2, 0.1))
}
function resetZoom() {
  const entities = props.entities
  if (!entities.length) return

  const PADDING = 40

  const minX = Math.min(...entities.map(e => e.x))
  const minY = Math.min(...entities.map(e => e.y))
  const maxX = Math.max(...entities.map(e => e.x + e.width))
  const maxY = Math.max(...entities.map(e => e.y + e.height))

  const boxMinX = minX - PADDING
  const boxMinY = minY - PADDING
  const boxMaxX = maxX + PADDING
  const boxMaxY = maxY + PADDING

  const boxWidth = boxMaxX - boxMinX
  const boxHeight = boxMaxY - boxMinY

  const containerW = canvasContainer.value?.clientWidth || 1
  const containerH = canvasContainer.value?.clientHeight || 1

  let zoomLevel = Math.min(containerW / boxWidth, containerH / boxHeight)
  if (zoomLevel > 1) zoomLevel = 1

  const viewBoxWidth = containerW / zoomLevel
  const viewBoxHeight = containerH / zoomLevel

  const boxCenterX = (boxMinX + boxMaxX) / 2
  const boxCenterY = (boxMinY + boxMaxY) / 2

  const panX = boxCenterX - viewBoxWidth / 2
  const panY = boxCenterY - viewBoxHeight / 2

  props.canvasState.zoom = zoomLevel
  props.canvasState.panX = panX
  props.canvasState.panY = panY
}
function zoomChange(level: number) {
  props.canvasState.zoom = level
}
function toggleGrid() {
  props.canvasState.showGrid = !props.canvasState.showGrid
}
// ------------------------------ 键盘事件处理 结束 ------------------------------

// ------------------------------ 触摸事件处理 开始 ------------------------------
// 触摸事件处理
function getTouchPos(touch: Touch): { x: number, y: number } {
  return screenToCanvasCoords(touch.clientX, touch.clientY)
}
// 获取触摸距离
function getTouchDistance(touch1: Touch, touch2: Touch): number {
  const dx = touch1.clientX - touch2.clientX
  const dy = touch1.clientY - touch2.clientY
  return Math.sqrt(dx * dx + dy * dy)
}

// 触摸开始
function handleTouchStart(event: TouchEvent) {
  const touches = event.touches
  
  if (touches.length === 1) {
    // 单指触摸
    isTouching.value = true
    touchStartTime.value = Date.now()
    lastTouchPos.value = getTouchPos(touches[0])
    isMultiTouch.value = false
  } else if (touches.length === 2) {
    // 双指触摸（缩放）
    isMultiTouch.value = true
    initialPinchDistance.value = getTouchDistance(touches[0], touches[1])
    currentZoom.value = props.canvasState.zoom
  }
}
// 触摸移动
function handleTouchMove(event: TouchEvent) {
  const touches = event.touches
  
  if (touches.length === 2 && isMultiTouch.value) {
    // 双指缩放
    const currentDistance = getTouchDistance(touches[0], touches[1])
    const scale = currentDistance / initialPinchDistance.value
    const newZoom = Math.max(0.1, Math.min(3, currentZoom.value * scale))
    zoomChange(newZoom)
  }
}
// 触摸结束
function handleTouchEnd(event: TouchEvent) {
  const touchDuration = Date.now() - touchStartTime.value
  
  if (event.touches.length === 0) {
    // 所有手指离开
    if (isTouching.value && touchDuration < 300) {
      // 短触摸视为点击
      const syntheticEvent = new MouseEvent('click', {
        clientX: lastTouchPos.value.x,
        clientY: lastTouchPos.value.y,
        bubbles: true
      })
      
      // 检查触摸位置是否在 entity 上
      const clickedEntity = getEntityAtTouchPosition(lastTouchPos.value.x, lastTouchPos.value.y)
      if (clickedEntity) {
        handleEntityClick(clickedEntity, syntheticEvent)
      } else {
        handleCanvasClick(syntheticEvent)
      }
    }
    
    isTouching.value = false
    isMultiTouch.value = false
    touchEntity.value = null
  }
}
// 判断触摸位置是否在某个 entity 上
function getEntityAtTouchPosition(touchX: number, touchY: number): Entity | null {
  const canvasCoords = screenToCanvasCoords(touchX, touchY)
  
  return props.entities.find(entity => {
    return canvasCoords.x >= entity.x && 
           canvasCoords.x <= entity.x + entity.width &&
           canvasCoords.y >= entity.y && 
           canvasCoords.y <= entity.y + entity.height
  }) || null
}
// ------------------------------ 触摸事件处理 结束 ------------------------------

// ------------------------------ 实体触摸(移动端：触摸拖拽) 开始 ------------------------------
// 实体触摸开始
function handleEntityTouchStart(entity: Entity, event: TouchEvent) {
  event.stopPropagation()
  
  if (event.touches.length === 1) {
    touchEntity.value = entity
    touchStartTime.value = Date.now()
    
    const touch = event.touches[0]
    const pos = getTouchPos(touch)
    lastTouchPos.value = pos
    
    // 设置拖拽起始位置
    mouseDownPos = {
      x: pos.x,
      y: pos.y
    }
  }
}
// 实体触摸移动
function handleEntityTouchMove(entity: Entity, event: TouchEvent) {
  if (event.touches.length === 1 && touchEntity.value === entity) {
    const touch = event.touches[0]
    const pos = getTouchPos(touch)
    
    // 拖拽实体
    const newX = pos.x - mouseDownPos.x
    const newY = pos.y - mouseDownPos.y
    
    if (props.selectedEntities.length > 1 && isEntitySelected(entity)) {
      // 多选拖拽
      const deltaX = newX - entity.x
      const deltaY = newY - entity.y
      
      props.selectedEntities.forEach(selectedEntity => {
        selectedEntity.x = Math.max(0, Math.min(canvasWidth.value - selectedEntity.width, selectedEntity.x + deltaX))
        selectedEntity.y = Math.max(0, Math.min(canvasHeight.value - selectedEntity.height, selectedEntity.y + deltaY))
        store.updateEntity(selectedEntity)
      })
    } else {
      // 单个实体拖拽
      entity.x = Math.max(0, Math.min(canvasWidth.value - entity.width, newX))
      entity.y = Math.max(0, Math.min(canvasHeight.value - entity.height, newY))
      store.updateEntity(entity)
    }
  }
}
// 实体触摸结束
function handleEntityTouchEnd(entity: Entity, event: TouchEvent) {
  event.preventDefault()
  event.stopPropagation()
  const touchDuration = Date.now() - touchStartTime.value
  
  if (event.touches.length === 0) {
    if (touchEntity.value === entity && touchDuration < 300) {
      // 短触摸视为点击
      const syntheticEvent = new MouseEvent('click', {
        clientX: lastTouchPos.value.x,
        clientY: lastTouchPos.value.y,
        bubbles: true
      })
      handleEntityClick(entity, syntheticEvent)
    } else if (touchDuration >= 500) {
      // 长按视为右键
      const syntheticEvent = new MouseEvent('contextmenu', {
        clientX: lastTouchPos.value.x,
        clientY: lastTouchPos.value.y,
        bubbles: true
      })
      handleEntityRightClick(entity, syntheticEvent)
    }
    
    touchEntity.value = null
    mouseDownPos = { x: 0, y: 0 }
  }
}
// ------------------------------ 实体触摸(移动端：触摸拖拽) 结束 ------------------------------

// 暴露方法给父组件
defineExpose({
  zoomIn,
  zoomOut,
  resetZoom,
  zoomChange,
  toggleGrid
})

// 生命周期
const containerSize = ref({ width: 0, height: 0 })
onMounted(() => {
  if (canvasContainer.value) {
    const resizeObserver = new ResizeObserver(() => {
      containerSize.value = {
        width: canvasContainer.value!.clientWidth,
        height: canvasContainer.value!.clientHeight
      }
    })
    resizeObserver.observe(canvasContainer.value)
    onUnmounted(() => resizeObserver.disconnect())
  }
})

const canvasWidth = computed(() =>
 containerSize.value.width / props.canvasState.zoom
)
const canvasHeight = computed(() =>
containerSize.value.height / props.canvasState.zoom
)
</script>
<style scoped>
.canvas-container {
  position: relative;
  width: 100%;
  height: 100%;
}
.dark-theme .canvas-container {
  background: #030303;
}
.grid-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
}
.dark-theme .vertical-line{
  stroke: #2a2a2a;
}
.dark-theme .horizontal-line{
  stroke: #2a2a2a;
}
.dark-theme .grid-border{
  stroke: #2a2a2a;
}
.canvas-svg{
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 2;
}
.selection-box {
  stroke: #0366d6;
  fill:rgba(3, 102, 214, 0.1);
  pointer-events: none;
}
.dark-theme .selection-box{
  stroke: #bb86fc;
  fill: rgba(67, 44, 96, 0.1);
}
@media (max-width: var(--mobile-breakpoint)) {
  .canvas-svg {
    width: 100%;
    height: 100%;
    cursor: default;
  }
}
</style>