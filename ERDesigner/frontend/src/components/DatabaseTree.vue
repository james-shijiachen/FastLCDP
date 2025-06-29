<template>
  <div class="database-tree">
    <div class="tree-header">
      <h3>{{ $t('database.structure') }}</h3>
      <button class="btn btn-primary btn-sm" @click="$emit('create-database')" :title="$t('database.newDatabase')">
        <span class="icon">+</span>
        {{ $t('database.newDatabase') }}
      </button>
    </div>
    
    <div class="tree-content">
      <div v-if="treeData.length === 0" class="empty-state">
        <div class="empty-icon">📊</div>
        <p>{{ $t('database.noDatabase') }}</p>
        <p class="empty-hint">{{ $t('database.createFirst') }}</p>
      </div>
      
      <div v-else class="tree-nodes">
        <div
          v-for="database in treeData"
          :key="database.id"
          class="tree-node database-node"
        >
          <div
            class="node-content"
            @click="toggleDatabase(database.id)"
            @contextmenu.prevent="showDatabaseContextMenu($event, database)"
          >
            <span class="expand-icon" :class="{ expanded: expandedDatabases.has(database.id) }">
              <svg width="12" height="12" viewBox="0 0 12 12">
                <path d="M4.5 3L7.5 6L4.5 9" stroke="currentColor" stroke-width="1.5" fill="none"/>
              </svg>
            </span>
            <span class="node-icon database-icon">
              <svg width="16" height="16" viewBox="0 0 16 16">
                <path d="M8 1C5.5 1 3 1.5 3 2.5v11c0 1 2.5 1.5 5 1.5s5-.5 5-1.5v-11C13 1.5 10.5 1 8 1z" stroke="currentColor" stroke-width="1" fill="none"/>
                <ellipse cx="8" cy="2.5" rx="5" ry="1.5" stroke="currentColor" stroke-width="1" fill="none"/>
                <ellipse cx="8" cy="6" rx="5" ry="1.5" stroke="currentColor" stroke-width="1" fill="none"/>
                <ellipse cx="8" cy="9.5" rx="5" ry="1.5" stroke="currentColor" stroke-width="1" fill="none"/>
              </svg>
            </span>
            <span class="node-label">{{ database.label }}</span>
            <span class="entity-count">({{ getEntityCount(database.id) }})</span>
          </div>
          
          <div v-if="expandedDatabases.has(database.id)" class="child-nodes">
            <div
              v-for="entity in database.children"
              :key="entity.id"
              class="tree-node entity-node"
              :class="{ 
                selected: selectedEntityId === entity.id,
                abstract: entity.entityType === 'abstract'
              }"
              @click="selectEntity(entity)"
              @contextmenu.prevent="showEntityContextMenu($event, entity)"
            >
              <div class="node-content">
                <span class="node-icon entity-icon">
                  <svg v-if="entity.entityType === 'abstract'" width="16" height="16" viewBox="0 0 16 16">
                    <rect x="2" y="4" width="12" height="8" stroke="currentColor" stroke-width="1" fill="none" stroke-dasharray="2,2"/>
                    <line x1="2" y1="7" x2="14" y2="7" stroke="currentColor" stroke-width="1"/>
                  </svg>
                  <svg v-else width="16" height="16" viewBox="0 0 16 16">
                    <rect x="2" y="4" width="12" height="8" stroke="currentColor" stroke-width="1" fill="none"/>
                    <line x1="2" y1="7" x2="14" y2="7" stroke="currentColor" stroke-width="1"/>
                  </svg>
                </span>
                <span class="node-label">{{ entity.label }}</span>
                <span v-if="entity.entityType === 'abstract'" class="entity-type-badge">{{ $t('entity.abstract') }}</span>
              </div>
            </div>
            
            <div class="add-entity-node" @click="$emit('create-entity', database.id)">
              <span class="node-icon add-icon">+</span>
              <span class="node-label">{{ $t('database.addEntity') }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 右键菜单 -->
    <div
      v-if="contextMenu.show"
      class="context-menu"
      :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
      @click.stop
    >
      <div v-if="contextMenu.type === 'database'" class="menu-items">
        <div class="menu-item" @click="editDatabase">
          <span class="menu-icon">✏️</span>
          {{ $t('database.editDatabase') }}
        </div>
        <div class="menu-item" @click="contextMenu.target && $emit('create-entity', contextMenu.target.id)">
          <span class="menu-icon">➕</span>
          {{ $t('database.addEntity') }}
        </div>
        <div class="menu-item danger" @click="deleteDatabase">
          <span class="menu-icon">🗑️</span>
          {{ $t('database.deleteDatabase') }}
        </div>
      </div>
      
      <div v-if="contextMenu.type === 'entity'" class="menu-items">
        <div class="menu-item" @click="editEntity">
          <span class="menu-icon">✏️</span>
          {{ $t('entity.editEntity') }}
        </div>
        <div class="menu-item danger" @click="deleteEntity">
          <span class="menu-icon">🗑️</span>
          {{ $t('entity.deleteEntity') }}
        </div>
      </div>
    </div>
    
    <!-- 遮罩层用于关闭右键菜单 -->
    <div v-if="contextMenu.show" class="context-menu-overlay" @click="hideContextMenu"></div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { TreeNode } from '../types/entity'

interface Props {
  treeData: TreeNode[]
  selectedEntityId?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  'create-database': []
  'edit-database': [databaseId: string]
  'delete-database': [databaseId: string]
  'create-entity': [databaseId: string]
  'edit-entity': [entityId: string]
  'delete-entity': [entityId: string]
  'select-entity': [entityId: string]
}>()

// 展开的数据库
const expandedDatabases = ref(new Set<string>())

// 右键菜单
const contextMenu = ref({
  show: false,
  x: 0,
  y: 0,
  type: '' as 'database' | 'entity',
  target: null as TreeNode | null
})

// 切换数据库展开状态
function toggleDatabase(databaseId: string) {
  if (expandedDatabases.value.has(databaseId)) {
    expandedDatabases.value.delete(databaseId)
  } else {
    expandedDatabases.value.add(databaseId)
  }
}

// 选择实体
function selectEntity(entity: TreeNode) {
  emit('select-entity', entity.id)
}

// 获取数据库下的实体数量
function getEntityCount(databaseId: string): number {
  const database = props.treeData.find(db => db.id === databaseId)
  return database?.children?.length || 0
}

// 显示数据库右键菜单
function showDatabaseContextMenu(event: MouseEvent, database: TreeNode) {
  contextMenu.value = {
    show: true,
    x: event.clientX,
    y: event.clientY,
    type: 'database',
    target: database
  }
}

// 显示实体右键菜单
function showEntityContextMenu(event: MouseEvent, entity: TreeNode) {
  contextMenu.value = {
    show: true,
    x: event.clientX,
    y: event.clientY,
    type: 'entity',
    target: entity
  }
}

// 隐藏右键菜单
function hideContextMenu() {
  contextMenu.value.show = false
}

// 编辑数据库
function editDatabase() {
  if (contextMenu.value.target) {
    // 传递数据库ID，让父组件处理
    emit('edit-database', contextMenu.value.target.id)
  }
  hideContextMenu()
}

// 删除数据库
function deleteDatabase() {
  if (contextMenu.value.target) {
    emit('delete-database', contextMenu.value.target.id)
  }
  hideContextMenu()
}

// 编辑实体
function editEntity() {
  if (contextMenu.value.target) {
    emit('edit-entity', contextMenu.value.target.id)
  }
  hideContextMenu()
}

// 删除实体
function deleteEntity() {
  if (contextMenu.value.target) {
    emit('delete-entity', contextMenu.value.target.id)
  }
  hideContextMenu()
}
</script>

<style scoped>
/* DatabaseTree 特有样式 */
.database-tree {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
}

.btn-sm {
  padding: 4px 8px;
  font-size: 11px;
}

.database-node .node-content:hover {
  background: #f6f8fa;
}

.entity-node .node-content:hover {
  background: #e1f5fe;
}

.entity-node.selected .node-content {
  background: #0366d6;
  color: #fff;
}

.entity-node.abstract .node-content {
  font-style: italic;
  opacity: 0.8;
}

.database-icon {
  color: #0366d6;
}

.entity-icon {
  color: #28a745;
}

.add-icon {
  color: #6f42c1;
  font-weight: bold;
}

.entity-type-badge {
  font-size: 10px;
  background: #f1f3f4;
  color: #5f6368;
  padding: 2px 6px;
  border-radius: 10px;
  margin-left: 6px;
}

.add-entity-node {
  margin-top: 4px;
}

.add-entity-node .node-content {
  color: #6f42c1;
  font-size: 13px;
  opacity: 0.8;
}

.add-entity-node .node-content:hover {
  background: #f3f0ff;
  opacity: 1;
}

.menu-item.danger {
  color: #d73a49;
}

.menu-item.danger:hover {
  background: #ffeef0;
}


</style>
