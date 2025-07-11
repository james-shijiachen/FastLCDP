<template>
  <aside 
    class="datasource-panel" 
    :class="{ 
      'datasource-panel-hidden': hidden,
      'datasource-panel-overlay': isMobile && !hidden
    }">
    <div class="datasource-tree">
      <div class="tree-header">
        <h3>{{ $t('datasource.structure') }}</h3>
        <button class="btn btn-primary" @click="$emit('createDatasource')" :title="$t('datasource.newDatasource')">
          {{ $t('datasource.newDatasource') }}
        </button>
      </div>
      
      <div class="tree-content">
        <div class="tree-nodes">
          <TreeNodeComponent 
            v-for="child in treeData" 
            :key="child.id" 
            :node="child" 
            :selectedEntityId="selectedEntityId" 
            :dragOverNodeId="dragOverNodeId"
            @addEntity="handleAddEntity" 
            @selectEntity="handleSelectEntity" 
            @contextmenu="showContextMenu"
            @nodeDrop="handleNodeDrop"
            @dragOverNode="(id: string) => dragOverNodeId = id"
            @dragLeaveNode="(id: string) => { if (dragOverNodeId === id) dragOverNodeId = null }" />
        </div>
      </div>
      
      <!-- 右键菜单 -->
      <ContextMenu
        :show="contextMenu.show"
        :x="contextMenu.x"
        :y="contextMenu.y"
        :type="contextMenu.type === 'datasource' ? 'datasource' : 'entity'"
        :target="contextMenu.target"
        @editDatasource="(id: string) => $emit('editDatasource', id)"
        @deleteDatasource="(id: string) => $emit('deleteDatasource', id)"
        @createEntity="(id: string) => $emit('createEntity', id)"
        @editEntity="(id: string) => $emit('editEntity', id)"
        @deleteEntity="(id: string) => $emit('deleteEntity', id)"
        @close="hideContextMenu"
      />
      <!-- 遮罩层用于关闭右键菜单 -->
      <div v-if="contextMenu.show" class="context-menu-overlay" @click="hideContextMenu"></div>
    </div>
    
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import type { TreeNode } from '../types/entity'
import ContextMenu from './ContextMenu.vue'
import TreeNodeComponent from './TreeNode.vue'

interface Props {
  treeData: TreeNode[]
  selectedEntityId?: string
  hidden?: boolean
  isMobile?: boolean
}

const props = defineProps<Props>()
const { t: $t } = useI18n()
const dragOverNodeId = ref<string | null>(null)

const emit = defineEmits<{
  'createDatasource': []
  'editDatasource': [datasourceId: string]
  'deleteDatasource': [datasourceId: string]
  'createEntity': [datasourceId: string, parentEntityId?: string]
  'editEntity': [entityId: string]
  'deleteEntity': [entityId: string]
  'selectEntity': [entityId: string]
}>()

const contextMenu = ref({
  show: false,
  x: 0,
  y: 0,
  type: '' as 'datasource' | 'entity',
  target: null as TreeNode | null
})

// 拖拽节点（待实现）
function handleNodeDrop(_: { sourceId: string, targetId: string }) {
  // 这里处理数据结构变更（如调整 parentEntityId），并可调用 API 或 store 方法
  // 例如：store.moveEntity(sourceId, targetId)
}

// 显示右键菜单
function showContextMenu(event: MouseEvent, node: TreeNode) {
  contextMenu.value = {
    show: true,
    x: event.clientX,
    y: event.clientY,
    type: node.type,
    target: node
  }
}

// 隐藏右键菜单
function hideContextMenu() {
  contextMenu.value.show = false
}

// 添加实体
function handleAddEntity(node: TreeNode) {
  if (node.type === 'datasource') {
    emit('createEntity', node.id)
  } else if (node.type === 'entity') {
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

/* 右键菜单遮罩层 */
.context-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}
</style>
