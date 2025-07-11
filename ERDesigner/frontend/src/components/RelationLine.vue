<template>
  <g class="relation">
    <path
      :d="path"
      stroke="#666"
      stroke-width="2"
      fill="none"
      :marker-end="`url(#${markerId})`"
    />
    <text
      :x="labelPosition.x"
      :y="labelPosition.y"
      text-anchor="middle"
      class="relation-label"
    >
      {{ label }}
    </text>
  </g>
</template>

<script setup lang="ts">
import { defineProps } from 'vue'

defineProps<{
  path: string
  label: string
  labelPosition: { x: number; y: number }
  markerId: string
  relation: any
}>()
</script>
<style scoped>
.relation {
  pointer-events: none;
}
.relation-label {
  font-size: 12px;
  fill: #666;
  user-select: none;
  pointer-events: none;
}
</style>