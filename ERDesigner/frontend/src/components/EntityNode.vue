<template>
  <g class="entity"
    :class="{ selected }"
    :transform="dragTransform || `translate(${entity.x}, ${entity.y})`"
    @dblclick.stop="$emit('dblclick', entity)"
    @mousedown="e => $emit('mousedown', entity, e)"
    @touchstart="e => $emit('touchstart', entity, e)"
    @touchmove="e => $emit('touchmove', entity, e)"
    @touchend="e => $emit('touchend', entity, e)">
    <!-- 实体主体 -->
    <rect class="entity-rect"
      :width="viewedEntity.width"
      :height="viewedEntity.height"
      :fill="entity.backgroundColor || '#ffffff'"
      :stroke="selected ? '#0366d6' : (entity.borderColor || '#24292e')"
      :stroke-width="selected ? 3 : 1"
      rx="4"/>
    <!-- 实体名称 -->
    <text class="entity-name"
      :x="viewedEntity.width / 2"
      y="20"
      text-anchor="middle"
      font-weight="bold"
      font-size="14"
      fill="#24292e">
      {{ entity.name }}
    </text>
    <!-- 表名和字段之间的分隔线 -->
    <line class="header-separator"
      x1="0"
      y1="30"
      :x2="viewedEntity.width"
      y2="30"
      :stroke="entity.borderColor || '#24292e'"/>
    <!-- 字段列表 -->
    <g class="fields">
      <g class="field"
        v-for="(field, index) in viewedEntity.fields" 
        :key="field.id"
        :transform="`translate(0, ${30 + index * 20})`">
        <line
          v-if="index !== 0"
          class="field-separator"
          x1="0"
          y1="0"
          :x2="viewedEntity.width"
          y2="0"
          stroke="#d7d7d7"
          stroke-width="1"/>
        <!-- 字段背景 -->
        <rect class="field-bg"
          :width="viewedEntity.width"
          height="20"
          fill="transparent"/>
        <!-- 主键图标 -->
        <text class="key-icon"
          v-if="field.isPrimaryKey"
          x="8"
          y="14"
          font-size="10"
          fill="#f39c12">
          🔑
        </text>
        <!-- 字段名 -->
        <text class="field-name"
          :x="field.isPrimaryKey ? 25 : 8"
          y="14"
          font-size="12"
          fill="#24292e">
          {{ field.name }}
        </text>
        <!-- 字段类型 -->
        <text class="field-type" v-if="field.length && field.scale"
          :x="viewedEntity.width - 8"
          y="14"
          text-anchor="end"
          font-size="10"
          fill="#586069">
          {{ field.type }}({{ field.length }},{{ field.scale }})
        </text>
        <text class="field-type" v-else-if="field.length"
          :x="viewedEntity.width - 8"
          y="14"
          text-anchor="end"
          font-size="10"
          fill="#586069">
          {{ field.type }}({{ field.length }})
        </text>
        <text class="field-type" v-else
          :x="viewedEntity.width - 8"
          y="14"
          text-anchor="end"
          font-size="10"
          fill="#586069">
          {{ field.type }}
        </text>
      </g>
    </g>
  </g>
</template>

<script setup lang="ts">
import type { Entity, Field } from '../types/entity'
import { defineProps, defineEmits, computed, onMounted } from 'vue'


const props = defineProps<{
  entity: Entity
  selected: boolean
  dragTransform?: string
  visibleEntities: Entity[]
}>()

const viewedEntity = computed(() => {
  const entity = { ...props.entity }
  const allFields = props.entity.parentEntityId ? [...getAllParentFields(props.visibleEntities, props.entity.parentEntityId), ...props.entity.fields] : props.entity.fields
  entity.fields = allFields
  updateEntitySize(entity)
  return entity
})


defineEmits([
  'dblclick',
  'contextmenu',
  'mousedown',
  'touchstart',
  'touchmove',
  'touchend',
])

// 递归获取所有父级字段
function getAllParentFields(entities: Entity[], parentEntityId: string | undefined): Field[] {
  const result: Field[] = [];
  let currentParentId = parentEntityId;
  while (currentParentId) {
    const parent = entities.find(e => e.id === currentParentId);
    if (parent) {
      
      // 设置来源信息，用于显示来源字段
      parent.fields.forEach(field => {
        field.extended = {
          entityId: parent.id,
          fieldId: field.id
        }
      })

      // 先递归上级，再加本级，保证顺序
      result.unshift(...parent.fields);
      currentParentId = parent.parentEntityId;
    } else {
      break;
    }
  }
  return result;
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
    const fieldWidth = iconWidth + fieldNameWidth + fieldTypeWidth + 50
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
}

</script>

<style scoped>
.entity {
  border: 2px solid #010101;
  cursor: pointer;
  transition: all 0.1s ease;
}
.entity.selected {
  cursor: move;
}
.dark-theme .entity-rect {
  fill: #010101;
  stroke: #676767;
}
.dark-theme .header-separator{
  stroke: #676767;
}
.dark-theme .field-name{
  fill: #ffffff;
}
.dark-theme .field-separator{
  stroke: #676767;
}
.dark-theme .entity-name{
  fill:#ffffff
}
.entity, .entity * {
  user-select: none;
  -webkit-user-select: none; /* Safari */
  -ms-user-select: none;     /* IE10+/Edge */
}
.entity-header {
  padding: 2px 4px;
  font-weight: bold;
}
.dark-theme .entity-header {
  background: #2c2c2c;
  border-bottom: 1px solid #444444;
  color: #ffffff;
}
.field-type {
  font-size: 11px;
  color: #586069;
  pointer-events: none;
}
.field-name {
  font-size: 13px;
  font-weight: 500;
  color: #24292e;
  display: flex;
  align-items: center;
  gap: 4px;
  pointer-events: none;
}

@media (max-width: var(--mobile-breakpoint)) {
  .entity.selected .entity-rect {
    filter: drop-shadow(0 0 8px rgba(3, 102, 214, 0.3));
  }
  .entity.multi-selected .entity-rect {
    stroke-dasharray: 5,5;
  }
  .entity-rect {
    transition: all 0.2s ease;
  }
  .entity-name {
    user-select: none;
    pointer-events: none;
  }
  .field {
    transition: all 0.1s ease;
  }
  .field:hover .field-bg {
    fill: rgba(3, 102, 214, 0.05);
  }
  .entity.selected .entity-rect {
    filter: drop-shadow(0 0 12px rgba(3, 102, 214, 0.4));
  }
  .field {
    min-height: 24px;
  }
  .field-name,
  .field-type {
    font-size: 13px;
  }
  .entity-name {
    font-size: 15px;
  }
}
</style>