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
      :fill="entity.backgroundColor || (props.isDarkTheme ? '#141313' : '#ffffff')"
      :stroke="selected ? (props.isDarkTheme ? '#bb86fc' : '#0366d6') : entity.borderColor || (props.isDarkTheme ? '#676767' : '#24292e')"
      :stroke-width="selected ? 3 : 1"
      rx="4"/>
    <!-- 实体名称 -->
    <text class="entity-name"
      :x="viewedEntity.width / 2"
      y="20"
      text-anchor="middle"
      font-weight="bold"
      font-size="14"
      :fill="entity.fontColor || (props.isDarkTheme ? '#ffffff' : '#24292e')">
      {{ entity.name }}
    </text>
    <!-- 表名和字段之间的分隔线 -->
    <line class="header-separator"
      x1="0"
      y1="30"
      :x2="viewedEntity.width"
      y2="30"
      :stroke="entity.borderColor || (props.isDarkTheme ? '#676767' : '#24292e')"/>
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
          :stroke="entity.borderColor || (props.isDarkTheme ? '#2f2e2e' : '#24292e')"
          stroke-width="1"/>
        <!-- 字段背景 -->
        <rect class="field-bg"
          :width="viewedEntity.width"
          :height="props.FIELD_HEIGHT"
          fill="transparent"/>
        <!-- 主键图标 & 唯一键图标 -->
        <foreignObject x="5.5" y="1" width="20" height="20">
          <div>
            <Icon :name="field.isPrimaryKey ? 'key' : (field.isUnique ? 'unique' : '')" style="width: 14px; height: 14px;" :color="entity.fontColor || (props.isDarkTheme ? '#ffffff' : '#24292e')" />
          </div>
        </foreignObject>
        <!-- 字段名 -->
        <text class="field-name"
          :x="field.isPrimaryKey || field.isUnique ? 25 : 8"
          y="18"
          font-size="12"
          :fill="entity.fontColor || (props.isDarkTheme ? '#ffffff' : '#24292e')">
          {{ field.name }}
        </text>
        <!-- 字段类型 -->
        <text class="field-type" v-if="field.length && field.scale"
          :x="viewedEntity.width - 8"
          y="18"
          text-anchor="end"
          font-size="10"
          :fill="entity.fontColor || (props.isDarkTheme ? '#ffffff' : '#586069')">
          {{ field.type }}({{ field.length }},{{ field.scale }})
        </text>
        <text class="field-type" v-else-if="field.length"
          :x="viewedEntity.width - 8"
          y="18"
          text-anchor="end"
          font-size="10"
          :fill="entity.fontColor || (props.isDarkTheme ? '#ffffff' : '#586069')">
          {{ field.type }}({{ field.length }})
        </text>
        <text class="field-type" v-else
          :x="viewedEntity.width - 8"
          y="18"
          text-anchor="end"
          font-size="10"
          :fill="entity.fontColor || (props.isDarkTheme ? '#ffffff' : '#586069')">
          {{ field.type }}
        </text>
      </g>
    </g>
  </g>
</template>

<script setup lang="ts">
import type { Entity, Field } from '../types/entity'
import { defineProps, defineEmits, computed, watch } from 'vue'
import Icon from '@/components/Icon.vue'
import { getAllParentFields, updateEntitySize } from '@/utils/datasourceUtil'

const props = defineProps<{
  entity: Entity
  selected: boolean
  isDarkTheme: boolean
  dragTransform?: string
  visibleEntities: Entity[]
  virtualEntities: Entity[]
  allFieldsCache: Record<string, Field[]>
  ENTITY_HEADER_HEIGHT: number
  FIELD_HEIGHT: number
}>()

const allEntities = computed(() => [...props.visibleEntities, ...props.virtualEntities])

// 设置字段引用
function setFieldRef(entityId: string, fieldId: string, el: HTMLElement | null) {
  if (el) {
    emit('setFieldRef', entityId, fieldId, el)
  }
}

const viewedEntity = computed(() => {
  console.log('viewedEntity - props.entity:', props.entity.id)
  const entity = { ...props.entity }
  entity.fields = cacheAllFields(entity) as Field[]
  updateEntitySize(entity)
  emit('updateEntitySize', entity)
  return entity
})

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
  cursor: pointer;
  transition: all 0.1s ease;
}
.entity.selected {
  cursor: move;
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
  pointer-events: none;
}
.field-name {
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  pointer-events: none;
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
  .field-name,
  .field-type {
    font-size: 13px;
  }
}
</style>