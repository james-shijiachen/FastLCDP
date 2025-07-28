<template>
  <g class="entity"
    :class="{ selected }"
    :data-entity-id="entity.id"
    :transform="dragTransform || `translate(${entity.x}, ${entity.y})`"
    @dblclick.stop="$emit('dblclick', entity)"
    @mousedown="e => $emit('mousedown', entity, e)"
    @touchstart="e => $emit('touchstart', entity, e)"
    @touchmove="e => $emit('touchmove', entity, e)"
    @touchend="e => $emit('touchend', entity, e)">
    <!-- 实体主体 -->
    <rect class="entity-rect"
      :class="{ selected }"
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
        :ref="el => setFieldRef(entity.id, field.id, el as HTMLElement)"
        :transform="`translate(0, ${props.ENTITY_HEADER_HEIGHT + index * props.FIELD_HEIGHT})`">
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
          :height="props.FIELD_HEIGHT"
          fill="transparent"/>
        <!-- 主键图标 -->
        <foreignObject v-if="field.isPrimaryKey" x="5.5" y="1" width="20" height="20">
          <div>
            <component :is="KeyIcon" style="width: 14px; height: 14px;" />
          </div>
        </foreignObject>
        <!-- 唯一键图标 -->
        <foreignObject v-else-if="field.isUnique" x="5.5" y="1" width="20" height="20">
          <div>
            <component :is="UniqueIcon" style="width: 14px; height: 14px;" />
          </div>
        </foreignObject>
        <!-- 字段名 -->
        <text class="field-name"
          :x="field.isPrimaryKey || field.isUnique ? 25 : 8"
          y="18"
          font-size="12"
          fill="#24292e">
          {{ field.name }}
        </text>
        <!-- 字段类型 -->
        <text class="field-type" v-if="field.length && field.scale"
          :x="viewedEntity.width - 8"
          y="18"
          text-anchor="end"
          font-size="10"
          fill="#586069">
          {{ field.type }}({{ field.length }},{{ field.scale }})
        </text>
        <text class="field-type" v-else-if="field.length"
          :x="viewedEntity.width - 8"
          y="18"
          text-anchor="end"
          font-size="10"
          fill="#586069">
          {{ field.type }}({{ field.length }})
        </text>
        <text class="field-type" v-else
          :x="viewedEntity.width - 8"
          y="18"
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
import { defineProps, defineEmits, computed } from 'vue'
import KeyIcon from '../assets/KeyIcon.vue'
import UniqueIcon from '../assets/UniqueIcon.vue'
import { getAllParentFields, updateEntitySize } from '@/utils/datasourceUtil'

const props = defineProps<{
  entity: Entity
  selected: boolean
  dragTransform?: string
  visibleEntities: Entity[]
  virtualEntities: Entity[]
  allFieldsCache: Record<string, Field[]>
  ENTITY_HEADER_HEIGHT: number
  FIELD_HEIGHT: number
}>()

// 设置字段引用
function setFieldRef(entityId: string, fieldId: string, el: HTMLElement | null) {
  if (el) {
    emit('setFieldRef', entityId, fieldId, el)
  }
}

const viewedEntity = computed(() => {
  const entity = { ...props.entity }
  const allEntities = [...props.visibleEntities, ...props.virtualEntities]
  // 判断是否有父实体并且缓存中存在父实体的字段
  let allFields = props.allFieldsCache[props.entity.id]
  if (!allFields || allFields.length === 0) {
    if (props.entity.parentEntityId) {
      allFields = [...getAllParentFields(allEntities, props.entity.parentEntityId), ...entity.fields]
    } else {
      allFields = entity.fields
    }
    props.allFieldsCache[props.entity.id] = allFields
  }
  entity.fields = allFields
  updateEntitySize(entity)
  emit('updateEntitySize', entity)
  return entity
})


const emit = defineEmits([
  'dblclick',
  'contextmenu',
  'mousedown',
  'touchstart',
  'touchmove',
  'touchend',
  'setFieldRef',
  'updateEntitySize'
])
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
  fill: #141313;
  stroke: #676767;
}
.dark-theme .entity-rect.selected {
  stroke: #bb86fc;
}
.dark-theme .header-separator{
  stroke: #676767;
}
.dark-theme .field-separator{
  stroke: #2f2e2e;
}
.dark-theme .field-name, .dark-theme .field-type, .dark-theme .entity-name {
  fill: #ffffff;
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
.key-icon {
  color: #2a2a29;
}
.dark-theme .key-icon {
  color: #ffffff;
}

@media (max-width: var(--mobile-breakpoint)) {
  .entity.selected .entity-rect {
    filter: drop-shadow(0 0 12px rgba(3, 102, 214, 0.4));
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
    font-size: 15px;
  }
  .field {
    transition: all 0.1s ease;
    min-height: 24px;
  }
  .field:hover .field-bg {
    fill: rgba(3, 102, 214, 0.05);
  }
  .field-name,
  .field-type {
    font-size: 13px;
  }
}
</style>