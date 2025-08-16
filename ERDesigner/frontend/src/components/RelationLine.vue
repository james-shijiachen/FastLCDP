<template>
<g v-if="relationLineType === 'canvas'">
  <path :d="relationPath" 
    stroke="currentColor" 
    stroke-width="1.5"
    :stroke-dasharray="strokeDashArray"
    fill="none" 
    :marker-start="fromMarker"
    :marker-end="toMarker"
    class="relation-path"
    @click="handleRelationClick" />
      <defs>
        <!-- N的标记：分叉的两条线 -->
        <marker id="many-marker" markerWidth="22" markerHeight="14" refX="22" refY="7.5" orient="auto" markerUnits="userSpaceOnUse">
              <line x1="5" y1="7.5" x2="25" y2="0" stroke="currentColor" stroke-width="1"/>
              <line x1="5" y1="7.5" x2="25" y2="15" stroke="currentColor" stroke-width="1"/>
        </marker>
        
        <!-- 反向N的标记：分叉的两条线 -->
        <marker id="many-marker-reverse" markerWidth="22" markerHeight="14" refX="0" refY="7.5" orient="auto" markerUnits="userSpaceOnUse">
              <line x1="16" y1="7.5" x2="-10" y2="-3" stroke="currentColor" stroke-width="1"/>
              <line x1="16" y1="7.5" x2="-10" y2="18" stroke="currentColor" stroke-width="1"/>
            </marker>
      </defs>
</g>
<svg v-else :class="`relation-line-svg-${relationLineType}-${relationship?.id}`">
  <path :d="relationPath" 
    stroke="currentColor" 
    stroke-width="1.5"
    :stroke-dasharray="strokeDashArray"
    fill="none" 
    :marker-start="fromMarker"
    :marker-end="toMarker"
    class="relation-path"
    @click="handleRelationClick" />
  <defs>
    <!-- N的标记：分叉的两条线 -->
    <marker id="many-marker" markerWidth="22" markerHeight="14" refX="22" refY="7.5" orient="auto" markerUnits="userSpaceOnUse">
          <line x1="5" y1="7.5" x2="25" y2="0" stroke="currentColor" stroke-width="1"/>
          <line x1="5" y1="7.5" x2="25" y2="15" stroke="currentColor" stroke-width="1"/>
    </marker>
    
    <!-- 反向N的标记：分叉的两条线 -->
    <marker id="many-marker-reverse" markerWidth="22" markerHeight="14" refX="0" refY="7.5" orient="auto" markerUnits="userSpaceOnUse">
          <line x1="16" y1="7.5" x2="-10" y2="-3" stroke="currentColor" stroke-width="1"/>
          <line x1="16" y1="7.5" x2="-10" y2="18" stroke="currentColor" stroke-width="1"/>
        </marker>
  </defs>
</svg>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import type { Relationship, Entity, Field } from '../types/entity'
import { RelationshipType } from '../types/entity'

const relationPath = ref('')

const props = defineProps<{
  relationLineType: 'editor' | 'canvas'
  relationship?: Relationship
  fieldRefs?: Record<string, Record<string, HTMLElement>>
  visibleEntities?: Entity[]
  allFieldsCache?: Record<string, Field[]>
  dragEntity?: Record<string, { x: number, y: number }>  // Entity拖拽时的临时位置,因为实体拖拽时不会改变entity的位置(为了流畅，不能监听entity)
  fieldUniqueCache: Record<string, boolean>
  ENTITY_HEADER_HEIGHT: number
  FIELD_HEIGHT: number
}>()

const emit = defineEmits<{
  relationClick: [relationship: Relationship]
}>()

function handleRelationClick() {
  if (props.relationship) {
    console.log('relationClick', props.relationship)
    emit('relationClick', props.relationship)
  }
}

// 根据线条样式计算stroke-dasharray
const strokeDashArray = computed(() => {
  if (!props.relationship) return 'none'
  return props.relationship.type === RelationshipType.VIRTUAL ? '8,4' : 'none'
})

// 根据关系类型计算起始端标记
const fromMarker = computed(() => {
  if (!props.relationship) return ''
  if(!props.fieldUniqueCache[props.relationship?.fromFieldId || '']){
    return 'url(#many-marker-reverse)'
  }
})

// 根据关系类型计算结束端标记
const toMarker = computed(() => {
  if (!props.relationship) return ''
  if(!props.fieldUniqueCache[props.relationship?.toFieldId || '']){
    return 'url(#many-marker)'
  }
})

const fromFieldRef = computed(() => {
  // 使用类型守卫确保fromEntityId和fromFieldId不为undefined
  const fromEntityId = props.relationship?.fromEntityId
  const fromFieldId = props.relationship?.fromFieldId
  return fromEntityId && fromFieldId ? props.fieldRefs?.[fromEntityId]?.[fromFieldId] : undefined
})

const toFieldRef = computed(() => {
  // 使用类型守卫确保toEntityId和toFieldId不为undefined
  const toEntityId = props.relationship?.toEntityId
  const toFieldId = props.relationship?.toFieldId
  return toEntityId && toFieldId ? props.fieldRefs?.[toEntityId]?.[toFieldId] : undefined
})

// 计算相关实体（考虑拖拽时的临时位置）
const fromDragEntity = computed(() => {
  // 如果实体正在被拖拽，使用临时位置
  const fromEntityId = props.relationship?.fromEntityId;
  if (props.dragEntity && fromEntityId && props.dragEntity[fromEntityId]) {
    return props.dragEntity[fromEntityId]
  }
  return null
})

const toDragEntity = computed(() => {
  // 如果实体正在被拖拽，使用临时位置
  const toEntityId = props.relationship?.toEntityId;
  if (props.dragEntity && toEntityId && props.dragEntity[toEntityId]) {
    return props.dragEntity[toEntityId]
  }
  return null
})

// 辅助函数：计算水平连线路径（画布坐标版本）
function calculateHorizontalPathCanvas(fromRect: { x: number, y: number, width: number, height: number }, toRect: { x: number, y: number, width: number, height: number }): string {
  // 编辑器模式下，需要根据svg的位置调整坐标
  if(props.relationLineType === 'editor'){
    const svgRef = document.querySelector(`.relation-line-svg-editor-${props.relationship?.id}`)
    if(svgRef){
      const svgRect = svgRef.getBoundingClientRect()
      fromRect.x = fromRect.x - svgRect.left
      fromRect.y = fromRect.y - svgRect.top
      toRect.x = toRect.x - svgRect.left
      toRect.y = toRect.y - svgRect.top
    }
  }
  // canvas模式下不需要坐标调整，因为已经是画布坐标
  const fromCenterX = fromRect.x + fromRect.width / 2
  const fromCenterY = fromRect.y + fromRect.height / 2
  const toCenterX = toRect.x + toRect.width / 2
  const toCenterY = toRect.y + toRect.height / 2
  
  const isFromLeft = fromCenterX < toCenterX

  const fromX = isFromLeft ? fromRect.x + fromRect.width : fromRect.x
  const toX = isFromLeft ? toRect.x : toRect.x + toRect.width
  
  const controlOffset = isFromLeft ? 50 : -50
  const controlX1 = fromX + controlOffset
  const controlX2 = toX - controlOffset
  
  return `M ${fromX} ${fromCenterY} C ${controlX1} ${fromCenterY}, ${controlX2} ${toCenterY}, ${toX} ${toCenterY}`
}

// 辅助函数：计算垂直连线路径（画布坐标版本）
function calculateVerticalPathCanvas(fromRect: { x: number, y: number, width: number, height: number }, toRect: { x: number, y: number, width: number, height: number }): string {
    
  // 编辑器模式下，需要根据svg的位置调整坐标
  if(props.relationLineType === 'editor'){
    const svgRef = document.querySelector(`.relation-line-svg-editor-${props.relationship?.id}`)
    if(svgRef){
      const svgRect = svgRef.getBoundingClientRect()
      fromRect.x = fromRect.x - svgRect.left
      fromRect.y = fromRect.y - svgRect.top
      toRect.x = toRect.x - svgRect.left
      toRect.y = toRect.y - svgRect.top
    }
  }
  
  const radius = 16
  const fromCenterX = fromRect.x + fromRect.width / 2
  const fromCenterY = fromRect.y + fromRect.height / 2
  const toCenterX = toRect.x + toRect.width / 2
  const toCenterY = toRect.y + toRect.height / 2
  
  const isFromLeft = fromCenterX < toCenterX
  
  const fromX = isFromLeft ? fromRect.x + fromRect.width : fromRect.x
  const toX = isFromLeft ? toRect.x : toRect.x + toRect.width
  
  const midX = isFromLeft ? (toX - fromX) / 2 + fromX : (fromX - toX) / 2 + toX
  const dirY = toCenterY > fromCenterY ? 1 : -1
  
  const pathData = isFromLeft 
    ? calculateLeftToRightPath(fromX, fromCenterY, toX, toCenterY, midX, dirY, radius)
    : calculateRightToLeftPath(fromX, fromCenterY, toX, toCenterY, midX, dirY, radius)
  
  return pathData.replace(/\s+/g, ' ')
}

// 辅助函数：计算从左到右的路径
function calculateLeftToRightPath(fromX: number, fromY: number, toX: number, toY: number, midX: number, dirY: number, radius: number): string {
  const p1x = midX - radius
  const arc1EndX = midX
  const arc1EndY = fromY + dirY * radius
  const p2y = toY - dirY * radius
  const arc2EndX = midX + radius
  const sweep1 = dirY > 0 ? 1 : 0
  const sweep2 = dirY > 0 ? 0 : 1
  
  return `
    M ${fromX} ${fromY}
    L ${p1x} ${fromY}
    A ${radius} ${radius} 0 0 ${sweep1} ${arc1EndX} ${arc1EndY}
    L ${midX} ${p2y}
    A ${radius} ${radius} 0 0 ${sweep2} ${arc2EndX} ${toY}
    L ${toX} ${toY}
  `
}

// 辅助函数：计算从右到左的路径
function calculateRightToLeftPath(fromX: number, fromY: number, toX: number, toY: number, midX: number, dirY: number, radius: number): string {
  const p1x = midX + radius
  const arc1EndX = midX
  const arc1EndY = fromY + dirY * radius
  const p2y = toY - dirY * radius
  const arc2EndX = midX - radius
  const sweep1 = dirY > 0 ? 0 : 1
  const sweep2 = dirY > 0 ? 1 : 0
  
  return `
    M ${fromX} ${fromY}
    L ${p1x} ${fromY}
    A ${radius} ${radius} 0 0 ${sweep1} ${arc1EndX} ${arc1EndY}
    L ${midX} ${p2y}
    A ${radius} ${radius} 0 0 ${sweep2} ${arc2EndX} ${toY}
    L ${toX} ${toY}
  `
}

// 计算并调整矩形坐标偏差的方法
function calculateDragOffset(dragEntity: { x: number, y: number } | null, entityId:string, fieldId: string): { x: number, y: number, width: number, height: number } {
  
  let adjustedY = dragEntity?.y || 0
  // 如果提供了fieldId，需要根据字段索引计算正确的y坐标
  // 获取对应的实体信息
  const entity = props.visibleEntities?.find(e => e.id === entityId)
  if(entity) {
    // 获取所有字段（包括继承字段）
    const allFields = props.allFieldsCache?.[entityId] || []
    
    // 找到字段索引
    const fieldIndex = allFields.findIndex((f: any) => f.id === fieldId)
    if (fieldIndex !== -1) {
      // 计算字段的y坐标：实体y + 头部高度(30) + 字段索引 * 字段高度(25)
      adjustedY = (dragEntity?.y || 0) + props.ENTITY_HEADER_HEIGHT + fieldIndex * props.FIELD_HEIGHT
    }
    
    return {
      x: dragEntity?.x || 0,
      y: adjustedY,
      width: entity?.width || 0,
      height: 25
    }
  }
  return {
    x: 0,
    y: 0,
    width: 0,
    height: 0
  };
}

// 主要的路径计算函数
function calculateRelationPath(): void {
  if (!fromFieldRef.value || !toFieldRef.value) {
    relationPath.value = ''
    return
  }
  
  // 获取视口坐标的DOMRect
  let fromRect = null
  let toRect = null


  if(props.relationLineType === 'canvas') {
    // canvas模式下使用画布坐标，因为RelationLine现在直接在主SVG内渲染
    //fromRect = rectToCanvasCoords(fromFieldRef.value.getBoundingClientRect())
    //toRect = rectToCanvasCoords(toFieldRef.value.getBoundingClientRect())
    fromRect = calculateDragOffset(fromDragEntity.value, props.relationship?.fromEntityId || '', props.relationship?.fromFieldId || '')
    toRect = calculateDragOffset(toDragEntity.value, props.relationship?.toEntityId || '', props.relationship?.toFieldId || '')
  }else if(props.relationLineType === 'editor') {
    // 编辑器坐标(视口坐标)
    fromRect = fromFieldRef.value.getBoundingClientRect()
    toRect = toFieldRef.value.getBoundingClientRect()
  }

  if(!fromRect || !toRect) {
    relationPath.value = ''
    return
  }

  // 根据拖拽实体坐标调整矩形坐标
  //fromRect = adjustRectWithDragOffset(fromRect, fromDragEntity.value, props.relationship?.fromEntityId, props.relationship?.fromFieldId)
  //toRect = adjustRectWithDragOffset(toRect, toDragEntity.value, props.relationship?.toEntityId, props.relationship?.toFieldId)

  const fromCenterX = fromRect.x + fromRect.width / 2
  const fromCenterY = fromRect.y + fromRect.height / 2
  const toCenterX = toRect.x + toRect.width / 2
  const toCenterY = toRect.y + toRect.height / 2
  const isHorizontalLine = Math.abs(fromCenterY - toCenterY) <= 36 // radius + 20
  const isVerticalLine = Math.abs(fromCenterX - toCenterX) - (fromRect.width / 2 + toRect.width / 2) <= 36 // radius + 20
  
  relationPath.value = isHorizontalLine 
    ? calculateHorizontalPathCanvas(fromRect, toRect)
    : calculateVerticalPathCanvas(fromRect, toRect)

  console.log('calculateRelationPath - path:', relationPath.value, isVerticalLine)
}

// 计算关联线路径 - 监听字段引用、相关实体位置变化和拖拽状态
watch([fromDragEntity, toDragEntity, () => props.relationship, () => props.visibleEntities], async () => {
  await nextTick()
  console.log('watch relationship id:', props.relationship?.id)
  calculateRelationPath()
}, { immediate: true })
</script>

<style scoped>
svg[class^="relation-line-svg-canvas-"] {
  display: block;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 3000;
}

svg[class^="relation-line-svg-canvas-"] .relation-path {
  pointer-events: stroke;
  cursor: pointer;
}

svg[class^="relation-line-svg-canvas-"] .relation-path:hover {
  stroke-width: 3;
  stroke: #0366d6;
}

svg[class^="relation-line-svg-canvas-"]:hover defs marker line {
  stroke-width: 3;
}

svg[class^="relation-line-svg-editor-"] {
  display: block;
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 3000;
}


</style>