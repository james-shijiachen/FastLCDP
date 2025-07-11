<template>
  <div 
    ref="canvasContainer" 
    class="canvas-container"
    @click="handleCanvasClick"
    @contextmenu.prevent="handleCanvasRightClick"
    @drop="handleDrop"
    @dragover="handleDragOver"
    @wheel.prevent="handleWheel"
    @keydown.prevent="handleKeyDown"
    @touchstart.prevent="handleTouchStart"
    @touchmove.prevent="handleTouchMove"
    @touchend.prevent="handleTouchEnd"
    tabindex="0">
    <svg
      ref="svgCanvas"
      class="canvas-svg"
      :width="canvasWidth"
      :height="canvasHeight"
      :viewBox="`0 0 ${canvasWidth} ${canvasHeight}`"
      @mousedown="handleCanvasMouseDown"
      @mousemove="handleCanvasMouseMove"
      @mouseup="handleCanvasMouseUp"
      @touchstart="handleSvgTouchStart"
      @touchmove="handleSvgTouchMove"
      @touchend="handleSvgTouchEnd">
      <!-- 网格背景 -->
      <defs v-if="showGrid">
        <pattern id="grid" width="20" height="20" patternUnits="userSpaceOnUse">
          <path d="M 20 0 L 0 0 0 20" fill="none" stroke="#e1e4e8" stroke-width="0.5"/>
        </pattern>
      </defs>
      <rect v-if="showGrid" width="100%" height="100%" fill="url(#grid)" />
      
      <!-- 关系连线 -->
      <g class="relations-layer">
        <RelationLine
          v-for="relationship in relationships"
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
          v-for="entity in entities"
          :key="entity.id"
          :entity="entity"
          :selected="isEntitySelected(entity)"
          :multiSelected="selectedEntities.length > 1 && isEntitySelected(entity)"
          @click="(entity, event) => handleEntityClick(entity, event)"
          @dblclick="handleEntityDoubleClick"
          @contextmenu="handleEntityRightClick"
          @mousedown="handleEntityMouseDown"
          @touchstart="handleEntityTouchStart"
          @touchmove="handleEntityTouchMove"
          @touchend="handleEntityTouchEnd"
        />
      </g>
      
      <!-- 选择框 -->
      <rect
        v-if="selectionBox.visible"
        :x="selectionBox.x"
        :y="selectionBox.y"
        :width="selectionBox.width"
        :height="selectionBox.height"
        fill="rgba(3, 102, 214, 0.1)"
        stroke="#0366d6"
        stroke-width="1"
        stroke-dasharray="5,5"
        class="selection-box"
      />
    </svg>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watchEffect } from 'vue'
import { useDSDiagramStore } from '../stores/dsDiagram'
import type { Entity, Relationship } from '../types/entity'
import EntityNode from './EntityNode.vue'
import RelationLine from './RelationLine.vue'

interface Props {
  zoomLevel?: number
  showGrid?: boolean
  selectedEntities?: Entity[]
}

const props = withDefaults(defineProps<Props>(), {
  zoomLevel: 1,
  showGrid: true,
  selectedEntities: () => []
})

const emit = defineEmits<{
  'entityClick': [entity: Entity, event: MouseEvent]
  'entityDoubleClick': [entity: Entity]
  'entityRightClick': [entity: Entity, event: MouseEvent]
  'canvasClick': [event: MouseEvent]
  'canvasRightClick': [event: MouseEvent]
  'selectionChange': [entities: Entity[]]
  'zoomChange': [level: number]
}>()

const store = useDSDiagramStore()

// 响应式数据
const canvasContainer = ref<HTMLDivElement>()
const svgCanvas = ref<SVGSVGElement>()
const canvasWidth = ref(2000)
const canvasHeight = ref(2000)

// 拖拽状态
const isDragging = ref(false)
const dragStartPos = ref({ x: 0, y: 0 })
const dragEntity = ref<Entity | null>(null)

// 调整尺寸功能已移除，实体框大小由计算自动确定

// 触摸状态
const isTouching = ref(false)
const touchStartTime = ref(0)
const lastTouchPos = ref({ x: 0, y: 0 })
const touchEntity = ref<Entity | null>(null)
const isMultiTouch = ref(false)
const initialPinchDistance = ref(0)
const currentZoom = ref(1)

// 选择框状态
const selectionBox = ref({
  visible: false,
  x: 0,
  y: 0,
  width: 0,
  height: 0,
  startX: 0,
  startY: 0
})

// 计算属性
const entities = computed(() => store.entities)
const relationships = computed(() => store.relationships)

// 方法
function isEntitySelected(entity: Entity): boolean {
  return props.selectedEntities.some(e => e.id === entity.id)
}

function handleCanvasClick(event: MouseEvent) {
  // 检查点击的是否是实体或实体的子元素
  const target = event.target as Element
  const isEntityClick = target.closest('.entity') || target.closest('.entity-group')
  
  // 如果不是点击实体，则触发canvas-click事件
  if (!isEntityClick) {
    emit('canvasClick', event)
  }
}

function handleCanvasRightClick(event: MouseEvent) {
  emit('canvasRightClick', event)
}

function handleEntityClick(entity: Entity, event: MouseEvent) {
  event.stopPropagation()
  emit('entityClick', entity, event)
}

function handleEntityDoubleClick(entity: Entity) {
  emit('entityDoubleClick', entity)
}

function handleEntityRightClick(entity: Entity, event: MouseEvent) {
  event.stopPropagation()
  // 触发实体右键菜单事件
  emit('entityRightClick', entity, event)
}

function handleCanvasMouseDown(event: MouseEvent) {
  if (event.target === svgCanvas.value) {
    // 开始选择框
    const rect = svgCanvas.value!.getBoundingClientRect()
    const x = event.clientX - rect.left
    const y = event.clientY - rect.top
    
    selectionBox.value = {
      visible: true,
      x,
      y,
      width: 0,
      height: 0,
      startX: x,
      startY: y
    }
    
    document.addEventListener('mousemove', handleSelectionMouseMove)
    document.addEventListener('mouseup', handleSelectionMouseUp)
  }
}

function handleCanvasMouseMove(event: MouseEvent) {
  if (isDragging.value && dragEntity.value) {
    const rect = svgCanvas.value!.getBoundingClientRect()
    const x = event.clientX - rect.left - dragStartPos.value.x
    const y = event.clientY - rect.top - dragStartPos.value.y
    
    dragEntity.value.x = Math.max(0, x)
    dragEntity.value.y = Math.max(0, y)
    
    store.updateEntity(dragEntity.value)
  }
}

function handleCanvasMouseUp() {
  if (isDragging.value) {
    isDragging.value = false
    dragEntity.value = null
  }
}

function handleEntityMouseDown(entity: Entity, event: MouseEvent) {
  event.stopPropagation()
  
  if (!isEntitySelected(entity)) {
    emit('entityClick', entity, event)
  }
  
  const rect = svgCanvas.value!.getBoundingClientRect()
  dragStartPos.value = {
    x: event.clientX - rect.left - entity.x,
    y: event.clientY - rect.top - entity.y
  }
  
  isDragging.value = true
  dragEntity.value = entity
  
  document.addEventListener('mousemove', handleDragMouseMove)
  document.addEventListener('mouseup', handleDragMouseUp)
}

function handleDragMouseMove(event: MouseEvent) {
  if (isDragging.value && dragEntity.value) {
    const rect = svgCanvas.value!.getBoundingClientRect()
    const x = event.clientX - rect.left - dragStartPos.value.x
    const y = event.clientY - rect.top - dragStartPos.value.y
    
    // 如果是多选，移动所有选中的实体
    if (props.selectedEntities.length > 1) {
      const deltaX = x - dragEntity.value.x
      const deltaY = y - dragEntity.value.y
      
      props.selectedEntities.forEach(entity => {
        // 限制实体在画布边界内
        entity.x = Math.max(0, Math.min(canvasWidth.value - entity.width, entity.x + deltaX))
        entity.y = Math.max(0, Math.min(canvasHeight.value - entity.height, entity.y + deltaY))
        store.updateEntity(entity)
      })
    } else {
      // 限制实体在画布边界内
      dragEntity.value.x = Math.max(0, Math.min(canvasWidth.value - dragEntity.value.width, x))
      dragEntity.value.y = Math.max(0, Math.min(canvasHeight.value - dragEntity.value.height, y))
      store.updateEntity(dragEntity.value)
    }
  }
}

function handleDragMouseUp() {
  isDragging.value = false
  dragEntity.value = null
  
  document.removeEventListener('mousemove', handleDragMouseMove)
  document.removeEventListener('mouseup', handleDragMouseUp)
}

// 调整尺寸事件处理
// 调整尺寸功能已移除，实体框大小完全由计算确定

function handleSelectionMouseMove(event: MouseEvent) {
  if (selectionBox.value.visible) {
    const rect = svgCanvas.value!.getBoundingClientRect()
    const currentX = event.clientX - rect.left
    const currentY = event.clientY - rect.top
    
    selectionBox.value.x = Math.min(selectionBox.value.startX, currentX)
    selectionBox.value.y = Math.min(selectionBox.value.startY, currentY)
    selectionBox.value.width = Math.abs(currentX - selectionBox.value.startX)
    selectionBox.value.height = Math.abs(currentY - selectionBox.value.startY)
  }
}

function handleSelectionMouseUp() {
  if (selectionBox.value.visible) {
    // 检查哪些实体在选择框内
    const selectedEntities = entities.value.filter(entity => {
      return entity.x < selectionBox.value.x + selectionBox.value.width &&
             entity.x + entity.width > selectionBox.value.x &&
             entity.y < selectionBox.value.y + selectionBox.value.height &&
             entity.y + entity.height > selectionBox.value.y
    })
    
    if (selectedEntities.length > 0) {
      emit('selectionChange', selectedEntities)
    }
    
    selectionBox.value.visible = false
  }
  
  document.removeEventListener('mousemove', handleSelectionMouseMove)
  document.removeEventListener('mouseup', handleSelectionMouseUp)
}

function handleDrop(event: DragEvent) {
  event.preventDefault()
  const type = event.dataTransfer?.getData('text/plain')
  
  if (type === 'entity') {
    const rect = canvasContainer.value!.getBoundingClientRect()
    const x = event.clientX - rect.left - 100
    const y = event.clientY - rect.top - 60
    
    const entity: Entity = {
      id: Date.now().toString(),
      name: '新实体',
      comment: '',
      fields: [
        {
          entityId: Date.now().toString(),
          id: Date.now().toString() + '_1',
          name: 'id',
          type: 'INT',
          comment: '主键',
          isPrimaryKey: true,
          isRequired: true,
          isUnique: true
        }
      ],
      x: Math.max(0, x),
      y: Math.max(0, y),
      width: 200,
      height: 60, // 最小高度，将由自动计算更新
      backgroundColor: '#ffffff',
      borderColor: '#24292e',
      datasourceId: '',
      entityType: 'entity'
    }
    
    store.addEntity(entity)
  }
}

function handleDragOver(event: DragEvent) {
  event.preventDefault()
}

function getRelationPath(relationship: Relationship): string {
  const fromEntity = entities.value.find(e => e.id === relationship.fromEntityId)
  const toEntity = entities.value.find(e => e.id === relationship.toEntityId)
  
  if (!fromEntity || !toEntity) return ''
  
  const fromX = fromEntity.x + fromEntity.width / 2
  const fromY = fromEntity.y + fromEntity.height / 2
  const toX = toEntity.x + toEntity.width / 2
  const toY = toEntity.y + toEntity.height / 2
  
  return `M ${fromX} ${fromY} L ${toX} ${toY}`
}

function getRelationLabelPosition(relationship: Relationship) {
  const fromEntity = entities.value.find(e => e.id === relationship.fromEntityId)
  const toEntity = entities.value.find(e => e.id === relationship.toEntityId)
  
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

// 缩放和键盘事件处理
function handleWheel(event: WheelEvent) {
  event.preventDefault()
  
  if (event.ctrlKey || event.metaKey) {
    // 缩放
    const delta = event.deltaY > 0 ? 0.9 : 1.1
    const newZoom = Math.max(0.1, Math.min(3, props.zoomLevel * delta))
    emit('zoomChange', newZoom)
  } else {
    // 平移画布
    const container = canvasContainer.value
    if (container) {
      container.scrollLeft += event.deltaX
      container.scrollTop += event.deltaY
    }
  }
}

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
        emit('selectionChange', entities.value)
        break
      case 'c':
      case 'v':
      case 'x':
      case 'z':
      case 'y':
        event.preventDefault()
        // 这里可以添加复制、粘贴、剪切、撤销、重做功能
        break
    }
  }
  
  // ESC键取消选择
  if (event.key === 'Escape') {
    emit('selectionChange', [])
  }
  
  // Delete键删除选中实体
  if (event.key === 'Delete' && props.selectedEntities.length > 0) {
    props.selectedEntities.forEach(entity => {
      store.deleteEntity(entity.id)
    })
    emit('selectionChange', [])
  }
}

// 公开方法
function zoomIn() {
  emit('zoomChange', Math.min(props.zoomLevel * 1.2, 3))
}

function zoomOut() {
  emit('zoomChange', Math.max(props.zoomLevel / 1.2, 0.1))
}

function resetZoom() {
  emit('zoomChange', 1)
}

// 触摸事件处理
function getTouchPos(touch: Touch): { x: number, y: number } {
  const rect = svgCanvas.value!.getBoundingClientRect()
  return {
    x: touch.clientX - rect.left,
    y: touch.clientY - rect.top
  }
}

function getTouchDistance(touch1: Touch, touch2: Touch): number {
  const dx = touch1.clientX - touch2.clientX
  const dy = touch1.clientY - touch2.clientY
  return Math.sqrt(dx * dx + dy * dy)
}

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
    currentZoom.value = props.zoomLevel
  }
}

function handleTouchMove(event: TouchEvent) {
  const touches = event.touches
  
  if (touches.length === 2 && isMultiTouch.value) {
    // 双指缩放
    const currentDistance = getTouchDistance(touches[0], touches[1])
    const scale = currentDistance / initialPinchDistance.value
    const newZoom = Math.max(0.1, Math.min(3, currentZoom.value * scale))
    emit('zoomChange', newZoom)
  }
}

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
      handleCanvasClick(syntheticEvent)
    }
    
    isTouching.value = false
    isMultiTouch.value = false
    touchEntity.value = null
  }
}

function handleSvgTouchStart(event: TouchEvent) {
  if (event.touches.length === 1 && event.target === svgCanvas.value) {
    // 在SVG画布上开始选择框
    const touch = event.touches[0]
    const pos = getTouchPos(touch)
    
    selectionBox.value = {
      visible: true,
      x: pos.x,
      y: pos.y,
      width: 0,
      height: 0,
      startX: pos.x,
      startY: pos.y
    }
  }
}

function handleSvgTouchMove(event: TouchEvent) {
  if (event.touches.length === 1 && selectionBox.value.visible) {
    const touch = event.touches[0]
    const pos = getTouchPos(touch)
    
    selectionBox.value.x = Math.min(selectionBox.value.startX, pos.x)
    selectionBox.value.y = Math.min(selectionBox.value.startY, pos.y)
    selectionBox.value.width = Math.abs(pos.x - selectionBox.value.startX)
    selectionBox.value.height = Math.abs(pos.y - selectionBox.value.startY)
  }
}

function handleSvgTouchEnd(_event: TouchEvent) {
  if (selectionBox.value.visible) {
    // 完成选择框选择
    const selectedEntities = entities.value.filter(entity => {
      return entity.x < selectionBox.value.x + selectionBox.value.width &&
             entity.x + entity.width > selectionBox.value.x &&
             entity.y < selectionBox.value.y + selectionBox.value.height &&
             entity.y + entity.height > selectionBox.value.y
    })
    
    emit('selectionChange', selectedEntities)
    selectionBox.value.visible = false
  }
}

function handleEntityTouchStart(entity: Entity, event: TouchEvent) {
  event.stopPropagation()
  
  if (event.touches.length === 1) {
    touchEntity.value = entity
    touchStartTime.value = Date.now()
    
    const touch = event.touches[0]
    const pos = getTouchPos(touch)
    lastTouchPos.value = pos
    
    // 设置拖拽起始位置
    dragStartPos.value = {
      x: pos.x - entity.x,
      y: pos.y - entity.y
    }
  }
}

function handleEntityTouchMove(entity: Entity, event: TouchEvent) {
  if (event.touches.length === 1 && touchEntity.value === entity) {
    const touch = event.touches[0]
    const pos = getTouchPos(touch)
    
    // 拖拽实体
    const newX = pos.x - dragStartPos.value.x
    const newY = pos.y - dragStartPos.value.y
    
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

function handleEntityTouchEnd(entity: Entity, event: TouchEvent) {
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
  }
}

// 计算实体的最小高度
function calculateEntityHeight(entity: Entity): number {
  // 头部高度30px + 每个字段20px，最小高度60px
  const headerHeight = 30
  const fieldHeight = 20
  const minHeight = 60
  
  const calculatedHeight = headerHeight + entity.fields.length * fieldHeight
  return Math.max(minHeight, calculatedHeight)
}

// 计算实体的最小宽度
function calculateEntityWidth(entity: Entity): number {
  // 基础最小宽度
  const minWidth = 150
  
  // 根据实体名称长度计算宽度
  const nameWidth = entity.name.length * 8 + 40
  
  // 根据字段内容计算宽度
  let maxFieldWidth = 0
  entity.fields.forEach(field => {
    const fieldNameWidth = field.name.length * 7
    const fieldTypeWidth = field.type.length * 6
    const iconWidth = field.isPrimaryKey ? 25 : 8
    const fieldWidth = iconWidth + fieldNameWidth + fieldTypeWidth + 40
    maxFieldWidth = Math.max(maxFieldWidth, fieldWidth)
  })
  
  return Math.max(minWidth, nameWidth, maxFieldWidth)
}

// 更新实体尺寸
function updateEntitySize(entity: Entity) {
  const width = calculateEntityWidth(entity)
  const height = calculateEntityHeight(entity)
  
  // 始终更新尺寸，实体框大小完全由计算确定
  entity.width = width
  entity.height = height
  
  store.updateEntity(entity)
}

// 监听实体变化，自动更新尺寸
watchEffect(() => {
  entities.value.forEach(entity => {
    updateEntitySize(entity)
  })
})

// 暴露方法给父组件
defineExpose({
  zoomIn,
  zoomOut,
  resetZoom
})

// 生命周期
onMounted(() => {
  if (canvasContainer.value) {
    const resizeObserver = new ResizeObserver(() => {
      nextTick(() => {
        if (canvasContainer.value) {
          canvasWidth.value = canvasContainer.value.clientWidth
          canvasHeight.value = canvasContainer.value.clientHeight
        }
      })
    })
    
    resizeObserver.observe(canvasContainer.value)
    
    // 初始化所有实体的尺寸
  nextTick(() => {
    store.entities.forEach(entity => {
      updateEntitySize(entity)
    })
  })
    
    onUnmounted(() => {
      resizeObserver.disconnect()
    })
  }
})
</script>
<style scoped>
.canvas-svg {
  width: 100%;
  height: 100%;
  transform-origin: 0 0;
  transform: scale(var(--zoom-level, 1));
}
.selection-box {
  pointer-events: none;
}
@media (max-width: var(--mobile-breakpoint)) {
  .canvas-svg {
    width: 100%;
    height: 100%;
    cursor: default;
  }
}
</style>