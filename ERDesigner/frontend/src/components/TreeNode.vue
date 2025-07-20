<template>
  <div :class="['tree-node', node.type === TreeNodeType.DATASOURCE ? 'datasource-node' : 'entity-node', { selected: selectedEntities.some(e => e.id === node.id) }]"
  :draggable="node.type === TreeNodeType.ENTITY"
  @dragstart="onDragStart($event, node)"
  @dragover.stop="onDragOver($event)"
  @dragenter.stop="onDragEnter($event)"
  @dragleave.stop="onDragLeave($event)"
  @drop.stop="onDrop">
    <div :class="['node-content',{ 'drag-over': node.id === dragOverNodeId }]" @click="handleSelect" @dblclick="handleDoubleClick(node)" @contextmenu.prevent="handleContextMenu($event, node)">
      <span v-if="hasChildren" class="expand-icon" @click.stop="toggle">
        <svg width="12" height="12" viewBox="0 0 12 12">
          <polygon v-if="!expanded" points="4,3 9,6 4,9" fill="currentColor" />
          <polygon v-else points="3,5 9,5 6,10" fill="currentColor" />
        </svg>
      </span>
      <!-- 数据库图标 -->
      <span v-if="node.type === TreeNodeType.DATASOURCE" class="node-icon datasource-icon">
        <svg width="16" height="16" viewBox="0 0 16 16">
          <path d="M8 1C5.5 1 3 1.5 3 2.5v11c0 1 2.5 1.5 5 1.5s5-.5 5-1.5v-11C13 1.5 10.5 1 8 1z" stroke="currentColor" stroke-width="1" fill="none"/>
          <ellipse cx="8" cy="2.5" rx="5" ry="1.5" stroke="currentColor" stroke-width="1" fill="none"/>
          <ellipse cx="8" cy="6" rx="5" ry="1.5" stroke="currentColor" stroke-width="1" fill="none"/>
          <ellipse cx="8" cy="9.5" rx="5" ry="1.5" stroke="currentColor" stroke-width="1" fill="none"/>
        </svg>
      </span>
      <!-- 实体/表图标 -->
      <span v-if="node && node.type === TreeNodeType.ENTITY && node.entityType === EntityType.ABSTRACT" class="node-icon entity-icon abstract-icon">
        <!-- 抽象类图标：虚线矩形（无A字） -->
        <svg width="16" height="16" viewBox="0 0 16 16">
          <rect x="2" y="4" width="12" height="8" stroke="currentColor" stroke-width="1" fill="none" stroke-dasharray="3,2"/>
        </svg>
      </span>
      <span v-else-if="node && node.type === TreeNodeType.ENTITY" class="node-icon entity-icon">
        <svg width="16" height="16" viewBox="0 0 16 16">
          <rect x="2" y="4" width="12" height="8" stroke="currentColor" stroke-width="1" fill="none"/>
          <line x1="2" y1="7" x2="14" y2="7" stroke="currentColor" stroke-width="1"/>
        </svg>
      </span>
      <span class="node-label">{{ node.label }}</span>
      <span v-if="hasChildren" class="child-count">{{ node.children?.length }}/{{ totalDescendants }}</span>
      <span class="add-btn" @click.stop="handleAdd">+</span>
    </div>
    <div v-if="expanded && hasChildren" class="child-nodes">
      <TreeNode
        v-for="child in (node.children || []).filter(Boolean)"
        :key="child.id"
        :node="child"
        :selectedEntities="selectedEntities"
        :dragOverNodeId="dragOverNodeId"
        @doubleClick="handleDoubleClick"
        @addEntity="$emit('addEntity', $event)"
        @selectEntity="$emit('selectEntity', $event)"
        @contextmenu.prevent="handleContextMenu"
        @nodeDrop="$emit('nodeDrop', $event)"
        @dragOverNode="(id: string) => emit('dragOverNode', id)"
        @dragLeaveNode="(id: string) => emit('dragLeaveNode', id)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { TreeNode, Entity } from '../types/entity'
import { TreeNodeType, EntityType } from '../types/entity'
import { useDragStore } from '../stores/dragState'

const dragStore = useDragStore()

const props = defineProps<{
  node: TreeNode
  selectedEntities: Entity[]
  dragOverNodeId?: string | null
}>()
const emit = defineEmits(['addEntity', 'selectEntity', 'contextmenu', 'nodeDrop', 'dragOverNode', 'dragLeaveNode', 'doubleClick'])

const expanded = ref(false)
const hasChildren = computed(() => props.node.children && props.node.children.length > 0)

// 监听子节点变化，若有新子节点则自动展开
watch(() => props.node.children?.length ?? 0, (newVal, oldVal = 0) => {
  if (newVal > oldVal) {
    expanded.value = true
  }
})
// 递归统计所有后代节点数量
const totalDescendants = computed(() => {
  function count(node: TreeNode): number {
    if (!node.children || node.children.length === 0) return 0
    return node.children.length + node.children.reduce((sum, c) => sum + count(c), 0)
  }
  return count(props.node)
})
// 展开/折叠节点
function toggle() {
  expanded.value = !expanded.value
}
// 添加实体
function handleAdd() {
  emit('addEntity', props.node)
}
// 选择实体
function handleSelect() {
  if (props.node.type === TreeNodeType.ENTITY) emit('selectEntity', props.node)
}
// 右键菜单
function handleContextMenu(e: MouseEvent, node: TreeNode) {
  emit('contextmenu', e, node || props.node)
}
// 双击节点
function handleDoubleClick(node: TreeNode) {
  emit('doubleClick', node || props.node)
}
// 拖拽开始
function onDragStart(event: DragEvent, node: TreeNode) {
  event.stopPropagation()
  dragStore.startDrag(node)
}
function onDragOver(event: DragEvent) {
  if (dragStore.canDropOn(props.node)) {
    event.preventDefault() // 必须阻止默认行为
    emit('dragOverNode', props.node.id)
  }
}
function onDragEnter(event: DragEvent) {
  if (dragStore.canDropOn(props.node)) {
    event.preventDefault() // 必须阻止默认行为
    emit('dragOverNode', props.node.id)
  }
}
// 拖拽离开
function onDragLeave(event: DragEvent) {
  event.preventDefault() // 必须阻止默认行为
  emit('dragLeaveNode', props.node.id)
}
// 拖拽节点
function onDrop() {
  if (dragStore.draggingNode && dragStore.canDropOn(props.node)) {
    emit('nodeDrop', { source: dragStore.draggingNode, target: props.node })
  }
  emit('dragLeaveNode', props.node.id)
  dragStore.endDrag()
}
</script>

<style scoped>
.tree-node {
  margin-bottom: 2px;
}
/* 图标 */
.entity-icon {
  color: #28a745;
}
.node-icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 6px;
  color: #586069;
}
.expand-icon {
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 4px;
  transition: transform 0.2s;
  color: #586069;
}
.expand-icon.expanded {
  transform: rotate(90deg);
}
.dark-theme .expand-icon,
.dark-theme .node-icon {
  color: #bb86fc;
}
.dark-theme .entity-icon {
  color: #66bb6a;
}

.node-content {
  display: flex;
  align-items: center;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  font-size: 13px;
  height: 30px;
}
.node-content:hover {
  background: #f6f8fa;
  border-radius: 4px;
}
.node-label {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.child-nodes {
  margin-left: 20px;
  padding-left: 8px;
}
.add-btn {
  margin-left: 8px;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 18px;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}
.dark-theme .add-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}
.child-count {
  margin-left: 6px;
  color: #888;
  font-size: 12px;
  min-width: 32px;
  text-align: right;
}
.tree-node.selected > .node-content {
  background: #0366d6;
  color: #fff;
}
.drag-over {
  background: #cde1f8 !important;
}
.dark-theme .node-content:hover {
  background: #2c2c2c !important;
}
.dark-theme .drag-over {
  background: #3c2959 !important;
}
/* 抽象类图标特殊样式（可选） */
.abstract-icon svg {
  opacity: 0.85;
}
.abstract-icon text {
  font-weight: bold;
  font-style: italic;
  fill: #bb86fc;
}
</style>
