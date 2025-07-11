<template>
  <g
    class="entity"
    :class="{ selected, 'multi-selected': multiSelected }"
    :transform="`translate(${entity.x}, ${entity.y})`"
    @click="e => $emit('click', entity, e)"
    @dblclick="$emit('dblclick', entity)"
    @contextmenu.prevent="e => $emit('contextmenu', entity, e)"
    @mousedown="e => $emit('mousedown', entity, e)"
    @touchstart="e => $emit('touchstart', entity, e)"
    @touchmove="e => $emit('touchmove', entity, e)"
    @touchend="e => $emit('touchend', entity, e)"
  >
    <!-- 实体主体 -->
    <rect
      :width="entity.width"
      :height="entity.height"
      :fill="entity.backgroundColor || '#ffffff'"
      :stroke="selected ? '#0366d6' : (entity.borderColor || '#24292e')"
      :stroke-width="selected ? 2 : 1"
      rx="4"
      class="entity-rect"
    />
    <!-- 实体标题 -->
    <rect
      :width="entity.width"
      height="30"
      :fill="entity.backgroundColor || '#f6f8fa'"
      :stroke="entity.borderColor || '#24292e'"
      stroke-width="1"
      rx="4"
      class="entity-header"/>
    <rect
      :width="entity.width"
      height="26"
      :fill="entity.backgroundColor || '#f6f8fa'"
      stroke="none"
      class="entity-header-fill"/>
    <!-- 实体名称 -->
    <text
      :x="entity.width / 2"
      y="20"
      text-anchor="middle"
      class="entity-name"
      font-weight="bold"
      font-size="14"
      fill="#24292e">
      {{ entity.name }}
    </text>
    <!-- 表名和字段之间的分隔线 -->
    <line
      x1="0"
      y1="30"
      :x2="entity.width"
      y2="30"
      :stroke="entity.borderColor || '#24292e'"
      stroke-width="1"
      class="header-separator"
    />
    <!-- 字段列表 -->
    <g class="fields">
      <g 
        v-for="(field, index) in entity.fields" 
        :key="field.id"
        class="field"
        :transform="`translate(0, ${30 + index * 20})`"
      >
        <!-- 字段背景 -->
        <rect
          :width="entity.width"
          height="20"
          fill="transparent"
          class="field-bg"
        />
        <!-- 主键图标 -->
        <text
          v-if="field.isPrimaryKey"
          x="8"
          y="14"
          font-size="10"
          fill="#f39c12"
          class="key-icon">
          🔑
        </text>
        <!-- 字段名 -->
        <text
          :x="field.isPrimaryKey ? 25 : 8"
          y="14"
          font-size="12"
          fill="#24292e"
          class="field-name">
          {{ field.name }}
        </text>
        <!-- 字段类型 -->
        <text
          :x="entity.width - 8"
          y="14"
          text-anchor="end"
          font-size="10"
          fill="#586069"
          class="field-type"
        >
          {{ field.type }}
        </text>
      </g>
    </g>
  </g>
</template>

<script setup lang="ts">
import type { Entity } from '../types/entity'
import { defineProps, defineEmits } from 'vue'

defineProps<{
  entity: Entity
  selected: boolean
  multiSelected: boolean
}>()

defineEmits([
  'click',
  'dblclick',
  'contextmenu',
  'mousedown',
  'touchstart',
  'touchmove',
  'touchend',
])
</script>

<style scoped>
.field-type {
  font-size: 11px;
  color: #586069;
}
.field-name {
  font-size: 13px;
  font-weight: 500;
  color: #24292e;
  display: flex;
  align-items: center;
  gap: 4px;
}
.field-name,
.field-type{
  pointer-events: none;
}

@media (max-width: var(--mobile-breakpoint)) {
.entity {
  cursor: move;
  transition: all 0.1s ease;
}
.entity.selected .entity-rect {
  filter: drop-shadow(0 0 8px rgba(3, 102, 214, 0.3));
}
.entity.multi-selected .entity-rect {
  stroke-dasharray: 5,5;
}
.entity-rect {
  transition: all 0.2s ease;
  stroke-width: 2;
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
    stroke-width: 3;
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