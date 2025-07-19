<template>
  <aside 
    class="datasource-panel" 
    :class="{ 
      'datasource-panel-hidden': hidden,
      'datasource-panel-overlay': isMobile && !hidden
    }">
    <div class="datasource-tree" @wheel.prevent="handleModalWheel">
      <div class="tree-header">
        <h3>{{ $t('datasource.structure') }}</h3>
        <button class="btn btn-primary" @click="$emit('createDatasource')" :title="$t('datasource.newDatasource')">
          {{ $t('datasource.newDatasource') }}
        </button>
      </div>
      <div class="tree-content" ref="treeContentRef">
        <div class="tree-nodes">
          <TreeNodeComponent 
            v-for="child in treeData" 
            :key="child.id" 
            :node="child" 
            :selectedEntities="selectedEntities" 
            :dragOverNodeId="dragOverNodeId"
            @addEntity="handleAddEntity" 
            @selectEntity="handleSelectEntity" 
            @contextmenu="showContextMenu"
            @nodeDrop="handleNodeDrop"
            @dragOverNode="(id: string) => dragOverNodeId = id"
            @dragLeaveNode="(id: string) => { if (dragOverNodeId === id) dragOverNodeId = null }" />
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import type { TreeNode, Entity } from '../types/entity'
import { TreeNodeType } from '../types/entity'
import TreeNodeComponent from './TreeNode.vue'

interface Props {
  treeData: TreeNode[]
  selectedEntities: Entity[]
  hidden?: boolean
  isMobile?: boolean
}

const props = defineProps<Props>()
const { t: $t } = useI18n()
const dragOverNodeId = ref<string | null>(null)
const treeContentRef = ref<HTMLDivElement | null>(null)

const emit = defineEmits<{
  'createDatasource': []
  'editDatasource': [datasourceId: string]
  'deleteDatasource': [datasourceId: string]
  'createEntity': [datasourceId: string, parentEntityId?: string]
  'editEntity': [entityId: string]
  'deleteEntity': [entityId: string]
  'selectEntity': [entityId: string]
  'contextmenu': [event: MouseEvent, node: TreeNode, type: string]
}>()

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const container = treeContentRef.value;
  if (container) {
    container.scrollLeft += event.deltaX; // 横向滚动
    container.scrollTop += event.deltaY; // 纵向滚动
  }
}

// 拖拽节点（待实现）
function handleNodeDrop(_: { sourceId: string, targetId: string }) {
  // 这里处理数据结构变更（如调整 parentEntityId），并可调用 API 或 store 方法
  // 例如：store.moveEntity(sourceId, targetId)
}

// 显示右键菜单
function showContextMenu(event: MouseEvent, node: TreeNode) {
  emit('contextmenu', event, node, node.type)
}

// 添加实体
function handleAddEntity(node: TreeNode) {
  if (node.type === TreeNodeType.DATASOURCE) {
    emit('createEntity', node.id)
  } else if (node.type === TreeNodeType.ENTITY) {
    emit('selectEntity', node.id)
    emit('createEntity', node.datasourceId!, node.id)
  }
}
// 选择实体
function handleSelectEntity(node: TreeNode) {
  emit('selectEntity', node.id)
}
</script>

<style scoped>
/* 数据源树面板（右侧） */
.datasource-panel {
  position: relative;
  background: #fff;
  display: flex;
  height: 100%;
  flex-direction: column;
  transition: transform 0.2s ease;
  z-index: 1000;
}
.datasource-panel, .datasource-panel * {
  user-select: none;
  -webkit-user-select: none; /* Safari */
  -ms-user-select: none;     /* IE10+/Edge */
}
/* 隐藏数据源树面板 */
.datasource-panel-hidden {
  display: none;
}
/* 数据源树面板遮罩层 */
.datasource-panel-overlay {
  z-index: 1000;
}
/* 数据源树面板内容 */
.datasource-tree {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
}
.dark-theme .datasource-tree {
  background: #1a1a1a;
  color: #ffffff;
  height: 100%;
  border-right: 1px solid #404040;
}
.tree-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}
.dark-theme .tree-content {
  background: #1a1a1a;
}
.tree-nodes {
  padding: 0 8px;
}
.dark-theme .tree-nodes {
  background: transparent;
}
.tree-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #e1e4e8;
  background: #f6f8fa;
}
.dark-theme .tree-header {
  background: #2a2a2a;
  border-bottom: 1px solid #404040;
}
.tree-header h3 {
  margin: 0;
  font-size: 20px;
  color: #24292e;
}
.dark-theme .tree-header h3 {
  color: #ffffff;
}
</style>
