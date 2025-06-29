<template>
  <div class="app-container" :class="{ 'dark-theme': isDarkTheme }">
    <!-- 顶部工具栏 -->
    <header class="top-toolbar">
      <div class="toolbar-left">
        <!-- 移动端菜单按钮 -->
        <button 
          class="mobile-menu-btn toolbar-btn" 
          @click="toggleSidebar" 
          v-if="isMobile"
          :title="$t('toolbar.menu')"
        >
          <span class="icon">☰</span>
        </button>
        
        <div class="logo">
          <span class="logo-icon">📊</span>
          <span class="logo-text" :class="{ 'logo-text-mobile': isMobile }">{{ $t('app.title') }}</span>
        </div>
        
        <div class="language-switcher">
          <select v-model="currentLocale" @change="changeLanguage">
            <option value="en">{{ $t('language.english') }}</option>
            <option value="zh">{{ $t('language.chinese') }}</option>
          </select>
        </div>
        
        <div class="toolbar-group">
          <button class="toolbar-btn" @click="newDiagram" :title="$t('toolbar.new')">
            <span class="icon">📄</span>
          </button>
          <button class="toolbar-btn" @click="saveDiagram" :title="$t('toolbar.save')">
            <span class="icon">💾</span>
          </button>
          <button class="toolbar-btn" @click="exportDiagram" :title="$t('toolbar.export')">
            <span class="icon">📤</span>
          </button>
        </div>
        
        <div class="toolbar-separator"></div>
        
        <div class="toolbar-group">
          <button class="toolbar-btn" @click="undo" :title="$t('toolbar.undo')">
            <span class="icon">↶</span>
          </button>
          <button class="toolbar-btn" @click="redo" :title="$t('toolbar.redo')">
            <span class="icon">↷</span>
          </button>
        </div>
        
        <div class="toolbar-separator"></div>
        
        <div class="toolbar-group">
          <button class="toolbar-btn" @click="zoomIn" :title="$t('toolbar.zoomIn')">
            <span class="icon">+</span>
          </button>
          <span class="zoom-level">{{ Math.round(zoomLevel * 100) }}%</span>
          <button class="toolbar-btn" @click="zoomOut" :title="$t('toolbar.zoomOut')">
            <span class="icon">−</span>
          </button>
          <button class="toolbar-btn" @click="resetZoom" :title="$t('toolbar.fitWindow')">
            <span class="icon">□</span>
          </button>
          <button class="toolbar-btn" @click="toggleTheme" :title="isDarkTheme ? $t('toolbar.toggleThemeLight') : $t('toolbar.toggleThemeDark')">
            <span class="icon">{{ isDarkTheme ? '○' : '●' }}</span>
          </button>
          <button class="toolbar-btn" @click="toggleGrid" :title="$t('toolbar.grid')">
            <span class="icon">#</span>
          </button>
          <button class="toolbar-btn" @click="toggleFullscreen" :title="$t('toolbar.fullscreen')">
            <span class="icon">⛶</span>
          </button>
        </div>

      </div>
    </header>
    
    <div class="main-layout">
      <!-- 左侧数据库树面板 -->
      <aside 
        class="database-panel" 
        :class="{ 
          'database-panel-hidden': isMobile && !sidebarVisible,
          'database-panel-overlay': isMobile && sidebarVisible
        }"
        :aria-label="$t('panel.databaseStructure')"
      >
        <!-- 移动端关闭按钮 -->
        <div v-if="isMobile" class="mobile-panel-header">
          <h3>{{ $t('panel.databaseStructure') }}</h3>
          <button class="close-btn" @click="closeSidebar">
            <span class="icon">✕</span>
          </button>
        </div>
        
        <DatabaseTree
          :tree-data="store.treeData"
          :selected-entity-id="selectedEntities[0]?.id"
          @create-database="showDatabaseModal = true"
          @edit-database="handleEditDatabase"
          @delete-database="handleDeleteDatabase"
          @create-entity="handleCreateEntity"
          @edit-entity="handleEditEntity"
          @delete-entity="handleDeleteEntity"
          @select-entity="handleSelectEntityFromTree"
        />
      </aside>
      
      <!-- 移动端遮罩层 -->
      <div 
        v-if="isMobile && sidebarVisible" 
        class="sidebar-overlay"
        @click="closeSidebar"
      ></div>
      
      <!-- 中央画布区域 -->
      <main class="canvas-container">
        <ERCanvas 
          ref="canvasRef"
          :zoom-level="zoomLevel"
          :show-grid="showGrid"
          :selected-entities="selectedEntities"
          @entity-click="handleEntityClick"
          @entity-double-click="handleEntityDoubleClick"
          @entity-right-click="handleEntityRightClick"
          @canvas-click="handleCanvasClick"
          @canvas-right-click="handleCanvasRightClick"
          @selection-change="handleSelectionChange"
          @zoom-change="handleZoomChange"
          :style="{ '--zoom-level': zoomLevel }"
        />
        
        <!-- 右键菜单 -->
        <div 
          v-if="showContextMenu" 
          class="context-menu"
          :style="{ left: contextMenuX + 'px', top: contextMenuY + 'px' }"
          @click.stop
        >
          <div class="context-menu-item" @click="addEntityAtPosition">
            <span class="icon">📦</span>
            {{ $t('contextMenu.addEntity') }}
          </div>
          <div class="context-menu-separator"></div>
          <div class="context-menu-item" @click="paste" :disabled="!canPaste">
            <span class="icon">📋</span>
            {{ $t('contextMenu.paste') }}
          </div>
          <div class="context-menu-item" @click="selectAll">
            <span class="icon">⊞</span>
            {{ $t('contextMenu.selectAll') }}
          </div>
        </div>
        
        <!-- 实体右键菜单 -->
        <div 
          v-if="showEntityContextMenu" 
          class="context-menu"
          :style="{ left: contextMenuX + 'px', top: contextMenuY + 'px' }"
          @click.stop
        >
          <div class="context-menu-item" @click="editEntity">
            <span class="icon">✏️</span>
            {{ $t('contextMenu.edit') }}
          </div>
          <div class="context-menu-item" @click="copyEntity">
            <span class="icon">📄</span>
            {{ $t('contextMenu.copy') }}
          </div>
          <div class="context-menu-item" @click="deleteSelectedEntities">
            <span class="icon">🗑️</span>
            {{ $t('contextMenu.delete') }}
          </div>
          <div class="context-menu-separator"></div>
          <div class="context-menu-item" @click="bringToFront">
            <span class="icon">⬆️</span>
            {{ $t('contextMenu.bringToFront') }}
          </div>
          <div class="context-menu-item" @click="sendToBack">
            <span class="icon">⬇️</span>
            {{ $t('contextMenu.sendToBack') }}
          </div>
        </div>
        
        <!-- 关系创建提示 -->
        <div v-if="selectedEntities.length === 2" class="relation-hint">
          <div class="hint-content">
            <span>{{ $t('relation.hint') }} {{ selectedEntities.length }} {{ $t('panel.entities') }}</span>
            <button @click="createRelation" class="create-relation-btn">{{ $t('relation.createRelation') }}</button>
          </div>
        </div>
      </main>
      
      <!-- 右侧属性面板 -->
      <aside v-if="showPropertyPanel" class="property-panel">
        <div class="panel-header">
          <h3>{{ $t('panel.properties') }}</h3>
          <button @click="closePropertyPanel" class="close-btn">×</button>
        </div>
        
        <div v-if="selectedEntities.length === 1" class="entity-properties">
          <div class="property-section">
            <h4>{{ $t('panel.basicInfo') }}</h4>
            <div class="form-group">
              <label>{{ $t('entity.name') }}:</label>
              <input v-model="selectedEntities[0].name" @input="updateEntity" />
            </div>
            <div class="form-group">
              <label>{{ $t('entity.comment') }}:</label>
              <textarea v-model="selectedEntities[0].comment" @input="updateEntity" rows="2"></textarea>
            </div>
          </div>
          
          <div class="property-section">
            <h4>{{ $t('panel.fields') }} ({{ selectedEntities[0].fields.length }})</h4>
            <div class="fields-list">
              <div v-for="field in selectedEntities[0].fields" :key="field.id" class="field-row">
                <div class="field-info">
                  <div class="field-name">
                    <span v-if="field.isPrimaryKey" class="key-icon">🔑</span>
                    {{ field.name }}
                  </div>
                  <div class="field-type">{{ field.type }}</div>
                </div>
                <button @click="editField(field)" class="edit-field-btn">✏️</button>
              </div>
            </div>
            <button @click="addField" class="add-field-btn">+ {{ $t('field.add') }}</button>
          </div>
          
          <div class="property-section">
            <h4>{{ $t('panel.style') }}</h4>
            <div class="form-group">
              <label>{{ $t('panel.backgroundColor') }}:</label>
              <input type="color" v-model="selectedEntities[0].backgroundColor" @input="updateEntity" />
            </div>
            <div class="form-group">
              <label>{{ $t('panel.borderColor') }}:</label>
              <input type="color" v-model="selectedEntities[0].borderColor" @input="updateEntity" />
            </div>
          </div>
        </div>
        
        <div v-else-if="selectedEntities.length > 1" class="multi-selection">
          <h4>{{ $t('panel.multiSelection') }} ({{ selectedEntities.length }} {{ $t('panel.entities') }})</h4>
          <button @click="alignLeft" class="align-btn">{{ $t('alignment.alignLeft') }}</button>
          <button @click="alignCenter" class="align-btn">{{ $t('alignment.alignCenter') }}</button>
          <button @click="alignRight" class="align-btn">{{ $t('alignment.alignRight') }}</button>
          <button @click="distributeHorizontally" class="align-btn">{{ $t('alignment.distributeHorizontally') }}</button>
          <button @click="distributeVertically" class="align-btn">{{ $t('alignment.distributeVertically') }}</button>
        </div>
      </aside>
    </div>
    
    <!-- 实体编辑模态框 -->
    <EntityEditModal 
      v-if="showEntityModal"
      :entity="editingEntity"
      :databases="store.databases"
      :available-parents="store.entities.filter(e => e.databaseId === (editingEntity?.databaseId || store.databases[0]?.id))"
      :default-database-id="store.databases[0]?.id"
      @save="handleEntitySave"
      @close="closeEntityModal"
    />
    
    <!-- 关系编辑模态框 -->
    <RelationEditModal 
      v-if="showRelationModal"
      :entities="selectedEntities"
      @save="handleRelationSave"
      @close="closeRelationModal"
    />
    
    <!-- 数据库编辑模态框 -->
    <DatabaseEditModal 
      v-if="showDatabaseModal"
      :database="editingDatabase"
      @save="handleDatabaseSave"
      @close="closeDatabaseModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useERDiagramStore } from './stores/erDiagram'
import ERCanvas from './components/ERCanvas.vue'
import EntityEditModal from './components/EntityEditModal.vue'
import RelationEditModal from './components/RelationEditModal.vue'
import DatabaseTree from './components/DatabaseTree.vue'
import DatabaseEditModal from './components/DatabaseEditModal.vue'
import type { Entity, Field, Database } from './types/entity'

const store = useERDiagramStore()
const { locale, t: $t } = useI18n()
const currentLocale = ref(locale.value)

// 响应式状态
const windowWidth = ref(window.innerWidth)
const isMobile = computed(() => windowWidth.value <= 768)
// const isTablet = computed(() => windowWidth.value <= 1024 && windowWidth.value > 768)
const sidebarVisible = ref(!isMobile.value)
const isDarkTheme = ref(false)

// 响应式数据
const canvasRef = ref<InstanceType<typeof ERCanvas> | null>(null)
const showEntityModal = ref(false)
const showRelationModal = ref(false)
const showDatabaseModal = ref(false)
const showPropertyPanel = ref(false)
const showContextMenu = ref(false)
const showEntityContextMenu = ref(false)
const editingEntity = ref<Entity | null>(null)
const editingDatabase = ref<Database | null>(null)
const selectedEntities = ref<Entity[]>([])
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const zoomLevel = ref(1)
const showGrid = ref(true)
const canPaste = ref(false)
const copiedEntities = ref<Entity[]>([])

// 计算属性
// const isMultiSelect = computed(() => selectedEntities.value.length > 1)

// 事件处理
function handleEntityClick(entity: Entity, event: MouseEvent) {
  if (event.altKey) {
    // Alt+点击进行多选
    const index = selectedEntities.value.findIndex(e => e.id === entity.id)
    if (index >= 0) {
      selectedEntities.value.splice(index, 1)
    } else {
      selectedEntities.value.push(entity)
    }
  } else {
    // 单选
    selectedEntities.value = [entity]
  }
  
  showPropertyPanel.value = selectedEntities.value.length > 0
  hideContextMenus()
}

function handleEntityDoubleClick(entity: Entity) {
  editingEntity.value = entity
  showEntityModal.value = true
}

function handleCanvasClick(event: MouseEvent) {
  if (!event.ctrlKey && !event.metaKey) {
    selectedEntities.value = []
    showPropertyPanel.value = false
  }
  hideContextMenus()
}

function handleCanvasRightClick(event: MouseEvent) {
  event.preventDefault()
  contextMenuX.value = event.clientX
  contextMenuY.value = event.clientY
  showContextMenu.value = true
  showEntityContextMenu.value = false
}

function handleEntityRightClick(entity: Entity, event: MouseEvent) {
  event.preventDefault()
  event.stopPropagation()
  
  if (!selectedEntities.value.includes(entity)) {
    selectedEntities.value = [entity]
  }
  
  contextMenuX.value = event.clientX
  contextMenuY.value = event.clientY
  showEntityContextMenu.value = true
  showContextMenu.value = false
}

function handleSelectionChange(entities: Entity[]) {
  selectedEntities.value = entities
  showPropertyPanel.value = entities.length > 0
}

function handleZoomChange(level: number) {
  zoomLevel.value = level
}

// 工具栏操作
function newDiagram() {
  if (confirm($t('messages.newDiagramConfirm'))) {
    store.clearDiagram()
    selectedEntities.value = []
    showPropertyPanel.value = false
  }
}

function saveDiagram() {
  // 实现保存逻辑
  console.log('保存图表')
  alert($t('messages.diagramSaved'))
}

function exportDiagram() {
  // 实现导出逻辑
  console.log('导出图表')
}

function undo() {
  store.undo()
}

function redo() {
  store.redo()
}

function toggleTheme() {
  isDarkTheme.value = !isDarkTheme.value
  // 保存主题设置到本地存储
  localStorage.setItem('theme', isDarkTheme.value ? 'dark' : 'light')
}

function loadTheme() {
  const savedTheme = localStorage.getItem('theme')
  isDarkTheme.value = savedTheme === 'dark'
}

function changeLanguage() {
  locale.value = currentLocale.value
  localStorage.setItem('locale', currentLocale.value)
}

function loadLocale() {
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale) {
    currentLocale.value = savedLocale
    locale.value = savedLocale
  }
}

function zoomIn() {
  canvasRef.value?.zoomIn()
}

function zoomOut() {
  canvasRef.value?.zoomOut()
}

function resetZoom() {
  canvasRef.value?.resetZoom()
}

function toggleGrid() {
  showGrid.value = !showGrid.value
}

function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

function addEntityAtPosition() {
  const entity: Entity = {
    id: Date.now().toString(),
    name: $t('defaults.newEntity'),
    comment: '',
    fields: [],
    x: contextMenuX.value - 100,
    y: contextMenuY.value - 50,
    width: 200,
    height: 60, // 最小高度，将由ERCanvas自动计算更新
    backgroundColor: '#ffffff',
    borderColor: '#000000',
    databaseId: store.databases[0]?.id || '',
    entityType: 'entity'
  }
  
  store.addEntity(entity)
  hideContextMenus()
}

function editEntity() {
  if (selectedEntities.value.length === 1) {
    editingEntity.value = selectedEntities.value[0]
    showEntityModal.value = true
  }
  hideContextMenus()
}

function copyEntity() {
  if (selectedEntities.value.length > 0) {
    // 深拷贝选中的实体
    copiedEntities.value = selectedEntities.value.map(entity => ({
      ...entity,
      fields: entity.fields.map(field => ({ ...field }))
    }))
    canPaste.value = true
  }
  hideContextMenus()
}

function paste() {
  if (copiedEntities.value.length > 0) {
    const offsetX = 20
    const offsetY = 20
    
    copiedEntities.value.forEach((entity, index) => {
      const newEntity: Entity = {
        ...entity,
        id: Date.now().toString() + '_' + index,
        name: entity.name + ' Copy',
        x: contextMenuX.value + (index * offsetX),
        y: contextMenuY.value + (index * offsetY),
        fields: entity.fields.map(field => ({
          ...field,
          id: Date.now().toString() + '_field_' + Math.random().toString(36).substr(2, 9)
        }))
      }
      
      store.addEntity(newEntity)
    })
    
    // 清空选择并选中新粘贴的实体
    selectedEntities.value = []
    copiedEntities.value = []
  }
  hideContextMenus()
}

function deleteSelectedEntities() {
  if (selectedEntities.value.length > 0 && confirm($t('messages.deleteEntitiesConfirm'))) {
    selectedEntities.value.forEach(entity => {
      store.deleteEntity(entity.id)
    })
    selectedEntities.value = []
    showPropertyPanel.value = false
  }
  hideContextMenus()
}

function selectAll() {
  selectedEntities.value = [...store.entities]
  showPropertyPanel.value = true
  hideContextMenus()
}

function createRelation() {
  if (selectedEntities.value.length === 2) {
    showRelationModal.value = true
  }
}

// 对齐操作
function alignLeft() {
  if (selectedEntities.value.length > 1) {
    const minX = Math.min(...selectedEntities.value.map(e => e.x))
    selectedEntities.value.forEach(entity => {
      entity.x = minX
      store.updateEntity(entity)
    })
  }
}

function alignCenter() {
  if (selectedEntities.value.length > 1) {
    const centerX = selectedEntities.value.reduce((sum, e) => sum + e.x + e.width / 2, 0) / selectedEntities.value.length
    selectedEntities.value.forEach(entity => {
      entity.x = centerX - entity.width / 2
      store.updateEntity(entity)
    })
  }
}

function alignRight() {
  if (selectedEntities.value.length > 1) {
    const maxX = Math.max(...selectedEntities.value.map(e => e.x + e.width))
    selectedEntities.value.forEach(entity => {
      entity.x = maxX - entity.width
      store.updateEntity(entity)
    })
  }
}

function distributeHorizontally() {
  // 实现水平分布
}

function distributeVertically() {
  // 实现垂直分布
}

// 其他操作
function bringToFront() {
  hideContextMenus()
}

function sendToBack() {
  hideContextMenus()
}

function hideContextMenus() {
  showContextMenu.value = false
  showEntityContextMenu.value = false
}

function closePropertyPanel() {
  showPropertyPanel.value = false
  selectedEntities.value = []
}

function closeEntityModal() {
  showEntityModal.value = false
  editingEntity.value = null
}

function closeRelationModal() {
  showRelationModal.value = false
}

function updateEntity() {
  if (selectedEntities.value.length === 1) {
    store.updateEntity(selectedEntities.value[0])
  }
}

function addField() {
  if (selectedEntities.value.length === 1) {
    const entity = selectedEntities.value[0]
    const newField: Field = {
      id: Date.now().toString(),
      name: $t('field.new'),
      type: 'VARCHAR(50)',
      comment: '',
      isPrimaryKey: false,
      isRequired: false,
      isUnique: false
    }
    entity.fields.push(newField)
    updateEntity()
  }
}

function editField(_field: Field) {
  // 实现字段编辑
}

function handleEntitySave(entity: Entity) {
  if (editingEntity.value) {
    store.updateEntity(entity)
  } else {
    store.addEntity(entity)
  }
  closeEntityModal()
}

function handleRelationSave(relation: any) {
  store.addRelation(relation)
  closeRelationModal()
}

// 数据库相关操作
function handleEditDatabase(databaseId: string) {
  const database = store.databases.find(db => db.id === databaseId)
  if (database) {
    editingDatabase.value = database
    showDatabaseModal.value = true
  }
}

function handleDeleteDatabase(databaseId: string) {
  if (confirm($t('messages.deleteDatabaseConfirm'))) {
    store.deleteDatabase(databaseId)
  }
}

function handleCreateEntity(_databaseId: string) {
  editingEntity.value = null
  showEntityModal.value = true
  // 设置默认数据库ID
}

function handleEditEntity(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  if (entity) {
    editingEntity.value = entity
    showEntityModal.value = true
  }
}

function handleDeleteEntity(entityId: string) {
  if (confirm($t('messages.deleteEntityConfirm'))) {
    store.deleteEntity(entityId)
  }
}

function handleSelectEntityFromTree(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  if (entity) {
    selectedEntities.value = [entity]
    showPropertyPanel.value = true
    // 可以添加画布聚焦到实体的逻辑
  }
}

function handleDatabaseSave(database: Database) {
  if (editingDatabase.value) {
    store.updateDatabase(database)
  } else {
    store.addDatabase(database)
  }
  showDatabaseModal.value = false
  editingDatabase.value = null
}

function closeDatabaseModal() {
  showDatabaseModal.value = false
  editingDatabase.value = null
}

// 响应式方法
function handleResize() {
  windowWidth.value = window.innerWidth
  // 在桌面端自动显示侧边栏，移动端自动隐藏
  if (!isMobile.value) {
    sidebarVisible.value = true
  } else {
    sidebarVisible.value = false
  }
}

function toggleSidebar() {
  sidebarVisible.value = !sidebarVisible.value
}

function closeSidebar() {
  sidebarVisible.value = false
}

// 生命周期
onMounted(() => {
  document.addEventListener('click', hideContextMenus)
  document.addEventListener('keydown', handleKeyDown)
  window.addEventListener('resize', handleResize)
  
  // 初始化侧边栏状态
  handleResize()
  
  // 加载主题设置
  loadTheme()
  
  // 加载语言设置
  loadLocale()
})

onUnmounted(() => {
  document.removeEventListener('click', hideContextMenus)
  document.removeEventListener('keydown', handleKeyDown)
  window.removeEventListener('resize', handleResize)
})

function handleKeyDown(event: KeyboardEvent) {
  if (event.key === 'Delete' && selectedEntities.value.length > 0) {
    deleteSelectedEntities()
  } else if (event.key === 'Escape') {
    selectedEntities.value = []
    showPropertyPanel.value = false
    hideContextMenus()
  } else if (event.ctrlKey && event.key === 'a') {
    event.preventDefault()
    selectAll()
  }
}
</script>

<style scoped>
.app-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.top-toolbar {
  height: 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-image: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 20" fill="none"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="%23ffffff" stroke-width="0.5" opacity="0.1"/></pattern></defs><rect width="100" height="20" fill="url(%23grid)"/></svg>');
  border-bottom: 3px solid #5a67d8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  position: relative;
  overflow: hidden;
}

.top-toolbar::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  min-width: 0;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  color: #ffffff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  padding: 8px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.logo-text {
  font-size: 20px;
  white-space: nowrap;
  letter-spacing: 0.5px;
}

.logo-text-mobile {
  display: none;
}

.logo-icon {
  font-size: 24px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.toolbar-group {
  display: flex;
  align-items: center;
  gap: 4px;
}

.toolbar-btn {
  width: 32px;
  height: 32px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: transparent;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #ffffff;
  font-size: 14px;
  transition: all 0.2s ease;
}

.mobile-menu-btn {
  margin-right: 12px;
  padding: 8px;
  min-width: auto;
}

.mobile-menu-btn .icon {
  font-size: 18px;
  line-height: 1;
}

.toolbar-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.5);
}

.toolbar-separator {
  width: 1px;
  height: 20px;
  background: rgba(255, 255, 255, 0.2);
  margin: 0 8px;
}

.zoom-level {
  font-size: 12px;
  color: #ffffff;
  min-width: 40px;
  text-align: center;
  font-weight: 400;
  opacity: 0.8;
}

.main-layout {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.database-panel {
  width: var(--sidebar-width);
  background: #fff;
  border-right: 1px solid #e1e4e8;
  overflow-y: auto;
  transition: transform 0.3s ease;
}

.database-panel-hidden {
  transform: translateX(-100%);
}

.database-panel-overlay {
  position: fixed;
  top: var(--toolbar-height);
  left: 0;
  height: calc(100vh - var(--toolbar-height));
  z-index: 1000;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

.mobile-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #e1e4e8;
  background: #f6f8fa;
}

.mobile-panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #24292e;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background: #e1e4e8;
}

.close-btn .icon {
  font-size: 16px;
  line-height: 1;
  color: #586069;
}

.panel-header {
  padding: 16px;
  border-bottom: 1px solid #e1e4e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #24292e;
}

.shape-category {
  padding: 16px;
  border-bottom: 1px solid #f6f8fa;
}

.shape-category h4 {
  margin: 0 0 12px 0;
  font-size: 12px;
  font-weight: 600;
  color: #586069;
  text-transform: uppercase;
}

.shape-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
}

.shape-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  cursor: pointer;
  background: #fff;
  transition: all 0.2s;
}

.shape-item:hover {
  border-color: #0366d6;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.shape-preview {
  width: 60px;
  height: 40px;
  margin-bottom: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.entity-preview {
  border: 1px solid #24292e;
  border-radius: 4px;
  background: #fff;
  font-size: 8px;
  padding: 2px;
}

.entity-header {
  background: #f6f8fa;
  padding: 2px 4px;
  font-weight: bold;
  border-bottom: 1px solid #e1e4e8;
}

.entity-field {
  padding: 1px 4px;
  font-size: 7px;
}

.relation-preview {
  position: relative;
}

.relation-line {
  width: 40px;
  height: 2px;
  background: #24292e;
  position: relative;
}

.relation-line::after {
  content: '';
  position: absolute;
  right: -4px;
  top: -2px;
  width: 0;
  height: 0;
  border-left: 6px solid #24292e;
  border-top: 3px solid transparent;
  border-bottom: 3px solid transparent;
}

.basic-rect {
  width: 40px;
  height: 24px;
  border: 1px solid #24292e;
  background: #fff;
}

.basic-circle {
  width: 32px;
  height: 32px;
  border: 1px solid #24292e;
  border-radius: 50%;
  background: #fff;
}

.shape-label {
  font-size: 11px;
  color: #586069;
  text-align: center;
}

.canvas-container {
  flex: 1;
  position: relative;
  background: #f6f8fa;
}

.property-panel {
  width: 280px;
  background: #fff;
  border-left: 1px solid #e1e4e8;
  overflow-y: auto;
}

.context-menu {
  position: fixed;
  background: #fff;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  min-width: 160px;
  padding: 4px 0;
}

.context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 13px;
  color: #24292e;
}

.context-menu-item:hover {
  background: #f6f8fa;
}

.context-menu-item[disabled] {
  color: #959da5;
  cursor: not-allowed;
}

.context-menu-separator {
  height: 1px;
  background: #e1e4e8;
  margin: 4px 0;
}

.relation-hint {
  position: absolute;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  background: #0366d6;
  color: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
}

.hint-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.create-relation-btn {
  background: #fff;
  color: #0366d6;
  border: none;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.create-relation-btn:hover {
  background: #f6f8fa;
}

.close-btn {
  background: none;
  border: none;
  font-size: 18px;
  color: #586069;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.close-btn:hover {
  background: #f6f8fa;
  color: #24292e;
}

.entity-properties {
  padding: 16px;
}

.property-section {
  margin-bottom: 24px;
}

.property-section h4 {
  margin: 0 0 12px 0;
  font-size: 13px;
  font-weight: 600;
  color: #24292e;
}

.form-group {
  margin-bottom: 12px;
}

.form-group label {
  display: block;
  margin-bottom: 4px;
  font-size: 12px;
  font-weight: 500;
  color: #586069;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 6px 8px;
  border: 1px solid #e1e4e8;
  border-radius: 4px;
  font-size: 13px;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #0366d6;
  box-shadow: 0 0 0 2px rgba(3, 102, 214, 0.2);
}

.fields-list {
  max-height: 200px;
  overflow-y: auto;
  border: 1px solid #e1e4e8;
  border-radius: 4px;
}

.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-bottom: 1px solid #f6f8fa;
}

.field-row:last-child {
  border-bottom: none;
}

.field-info {
  flex: 1;
}

.field-name {
  font-size: 13px;
  font-weight: 500;
  color: #24292e;
  display: flex;
  align-items: center;
  gap: 4px;
}

.key-icon {
  font-size: 10px;
}

.field-type {
  font-size: 11px;
  color: #586069;
}

.edit-field-btn {
  background: none;
  border: none;
  color: #586069;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.edit-field-btn:hover {
  background: #f6f8fa;
  color: #24292e;
}

.add-field-btn {
  width: 100%;
  padding: 8px;
  background: #f6f8fa;
  border: 1px dashed #e1e4e8;
  border-radius: 4px;
  color: #586069;
  cursor: pointer;
  font-size: 12px;
}

.add-field-btn:hover {
  background: #e1e4e8;
  border-color: #0366d6;
  color: #0366d6;
}

.multi-selection {
  padding: 16px;
}

.align-btn {
  width: 100%;
  margin-bottom: 8px;
  padding: 8px;
  background: #f6f8fa;
  border: 1px solid #e1e4e8;
  border-radius: 4px;
  color: #24292e;
  cursor: pointer;
  font-size: 12px;
}

.align-btn:hover {
  background: #e1e4e8;
  border-color: #0366d6;
}

/* 中等屏幕优化 */
@media (max-width: 1024px) {
  .toolbar-left {
    gap: 12px;
  }
  
  .toolbar-group {
    gap: 3px;
  }
  
  .toolbar-btn {
    width: 34px;
    height: 34px;
  }
  
  .toolbar-separator {
    margin: 0 8px;
  }
}

/* 响应式样式 */
@media (max-width: 768px) {
  .app-container {
    flex-direction: column;
  }
  
  .top-toolbar {
    padding: 8px 12px;
    height: auto;
    min-height: var(--toolbar-height);
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .toolbar-left {
    flex: 1;
    min-width: 0;
    gap: 8px;
    overflow: hidden;
  }
  
  .toolbar-right {
    flex-shrink: 0;
    display: flex;
    gap: 4px;
  }
  
  .logo {
    padding: 6px 12px;
  }
  
  .logo-text {
    font-size: 16px;
  }
  
  .logo-text-mobile {
    display: none;
  }
  
  .language-switcher {
    margin-left: 8px;
    margin-right: 8px;
  }
  
  .language-switcher select {
    padding: 4px 8px;
    font-size: 12px;
  }
  
  .toolbar-group {
    gap: 2px;
  }
  
  .toolbar-btn {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }
  
  .zoom-level {
    font-size: 11px;
    min-width: 35px;
  }
  
  .toolbar-separator {
    height: 20px;
    margin: 0 6px;
  }
  
  .main-layout {
    position: relative;
  }
  
  .canvas-container {
    width: 100%;
    margin-left: 0;
  }
  
  .database-panel {
    width: 280px;
  }
}

@media (max-width: 480px) {
  .top-toolbar {
    padding: 6px 8px;
    height: 60px;
  }
  
  .toolbar-left {
    gap: 4px;
  }
  
  .toolbar-right {
    gap: 2px;
  }
  
  .logo {
    padding: 4px 8px;
  }
  
  .logo-text {
    font-size: 14px;
  }
  
  .logo-icon {
    font-size: 18px;
  }
  
  .language-switcher {
    margin-left: 4px;
    margin-right: 4px;
  }
  
  .language-switcher select {
    padding: 2px 6px;
    font-size: 11px;
  }
  
  .toolbar-group {
    gap: 1px;
  }
  
  .toolbar-btn {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }
  
  .zoom-level {
    font-size: 10px;
    min-width: 30px;
  }
  
  .toolbar-separator {
    height: 16px;
    margin: 0 4px;
  }
  
  /* 在极小屏幕上隐藏一些不太重要的按钮 */
  .toolbar-group:nth-child(4) .toolbar-btn:nth-child(3),
  .toolbar-group:nth-child(4) .toolbar-btn:nth-child(4) {
    display: none;
  }
  
  .database-panel {
    width: 260px;
  }
  
  .mobile-panel-header {
    padding: 12px;
  }
  
  .mobile-panel-header h3 {
    font-size: 14px;
  }
}

/* 暗色主题样式 - Material Design 风格 */
.dark-theme .top-toolbar {
  background: #1e1e1e;
  border-bottom: 1px solid #333333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}

.dark-theme .logo {
  background: #2c2c2c;
  color: #ffffff;
  border: 1px solid #444444;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.dark-theme .logo-text {
  color: #ffffff;
  font-weight: 500;
}

.dark-theme .logo-icon {
  color: #bb86fc;
}

/* 浅色主题下的样式覆盖 */
.app-container:not(.dark-theme) .toolbar-btn {
  border: 1px solid rgba(0, 0, 0, 0.2);
  background: transparent;
  color: #374151;
}

.app-container:not(.dark-theme) .toolbar-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  border-color: rgba(0, 0, 0, 0.3);
}

.app-container:not(.dark-theme) .logo {
  background: rgba(255, 255, 255, 0.9);
  color: #374151;
  border: 1px solid #d1d5db;
  text-shadow: none;
}

.app-container:not(.dark-theme) .zoom-level {
  color: #374151;
  opacity: 0.7;
}

.app-container:not(.dark-theme) .toolbar-separator {
  background: rgba(0, 0, 0, 0.15);
}

.app-container:not(.dark-theme) .language-switcher select {
  background: #ffffff;
  color: #374151;
  border: 1px solid #d1d5db;
}

.app-container:not(.dark-theme) .language-switcher select:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
  background: #ffffff;
}

.app-container:not(.dark-theme) .language-switcher select option {
  background: #ffffff;
  color: #374151;
}

.dark-theme .toolbar-btn {
  background: transparent;
  border: 1px solid #444444;
  color: #ffffff;
  transition: all 0.2s ease;
}

.dark-theme .toolbar-btn:hover {
  background: #333333;
  border-color: #bb86fc;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.dark-theme .toolbar-separator {
  background: #444444;
}

.dark-theme .zoom-level {
  color: #bb86fc;
  opacity: 0.9;
  font-weight: 500;
}

.dark-theme .database-panel {
  background: #1e1e1e;
  border-right: 1px solid #333333;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.4);
}

.dark-theme .mobile-panel-header {
  background: #1e1e1e;
  border-bottom: 1px solid #333333;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.dark-theme .mobile-panel-header h3 {
  color: #ffffff;
  font-weight: 500;
}

.dark-theme .close-btn:hover {
  background: #333333;
  transform: scale(1.1);
  border-radius: 4px;
}

.dark-theme .close-btn .icon {
  color: #bb86fc;
}

.dark-theme .panel-header {
  border-bottom: 1px solid #333333;
  background: #1e1e1e;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.dark-theme .panel-header h3 {
  color: #ffffff;
  font-weight: 500;
}

/* 形状相关样式已移至 style.css 统一管理 */

/* 语言切换器样式 */
.language-switcher {
  margin-left: auto;
  margin-right: 16px;
}

.language-switcher select {
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  font-size: 14px;
  cursor: pointer;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  font-weight: 500;
}

.language-switcher select:focus {
  outline: none;
  border-color: rgba(255, 255, 255, 0.5);
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.15);
}

.language-switcher select option {
  background: #667eea;
  color: #ffffff;
  padding: 8px;
}

.dark-theme .language-switcher select {
  background: rgba(60, 60, 60, 0.9);
  border: 1px solid #505050;
  color: #ffffff;
  backdrop-filter: blur(10px);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
}

/* 黑色主题下的属性面板样式 */
.dark-theme .property-panel {
  background: #1a1a1a;
  border-left: 1px solid #404040;
  color: #ffffff;
}

.dark-theme .property-panel .close-btn {
  color: #ffffff;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.dark-theme .property-panel .close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.dark-theme .entity-properties {
  color: #ffffff;
}

.dark-theme .property-section h4 {
  color: #ffffff;
  font-weight: 600;
}

.dark-theme .form-group label {
  color: #cccccc;
}

.dark-theme .form-group input,
.dark-theme .form-group textarea {
  background: #2a2a2a;
  border: 1px solid #505050;
  color: #ffffff;
}

.dark-theme .form-group input:focus,
.dark-theme .form-group textarea:focus {
  border-color: #0366d6;
  box-shadow: 0 0 0 2px rgba(3, 102, 214, 0.3);
}

.dark-theme .fields-list {
  border: 1px solid #505050;
  background: #2a2a2a;
}

.dark-theme .field-row {
  border-bottom: 1px solid #404040;
}

.dark-theme .field-name {
  color: #ffffff;
}

.dark-theme .field-type {
  color: #cccccc;
}

.dark-theme .edit-field-btn {
  color: #cccccc;
}

.dark-theme .edit-field-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
}

.dark-theme .add-field-btn {
  background: #2a2a2a;
  border: 1px dashed #505050;
  color: #cccccc;
}

.dark-theme .add-field-btn:hover {
  background: #333333;
  color: #ffffff;
}

.dark-theme .multi-selection h4 {
  color: #ffffff;
}

.dark-theme .align-btn {
  background: #2a2a2a;
  border: 1px solid #505050;
  color: #ffffff;
  margin: 4px;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
}

.dark-theme .align-btn:hover {
  background: #333333;
  border-color: #707070;
}

/* 黑色主题下的数据库树样式 */
.dark-theme .database-tree {
  background: #1a1a1a !important;
  border: 1px solid #404040;
  color: #ffffff;
}

.dark-theme .tree-content {
  background: #1a1a1a !important;
}

.dark-theme .tree-nodes {
  background: transparent !important;
}

.dark-theme .tree-header {
  background: #2a2a2a;
  border-bottom: 1px solid #404040;
}

.dark-theme .tree-header h3 {
  color: #ffffff;
}

.dark-theme .btn {
  background: #404040;
  color: #ffffff;
  border-color: #404040;
}

.dark-theme .btn-primary {
  background: #0366d6;
  color: #ffffff;
  border-color: #0366d6;
}

.dark-theme .btn:hover {
  background: #505050;
  border-color: #505050;
}

.dark-theme .btn-primary:hover {
  background: #0256c7;
  border-color: #0256c7;
}

.dark-theme .empty-state {
  color: #cccccc;
}

.dark-theme .database-node .node-content:hover {
  background: #333333 !important;
  color: #ffffff !important;
}

.dark-theme .entity-node .node-content:hover {
  background: #2d4a5a !important;
  color: #ffffff !important;
}

.dark-theme .node-content {
  background: transparent !important;
  color: #ffffff !important;
}

.dark-theme .entity-node.selected .node-content {
  background: #0366d6;
  color: #ffffff;
}

.dark-theme .expand-icon {
  color: #cccccc;
}

.dark-theme .node-icon {
  color: #cccccc;
}

.dark-theme .database-icon {
  color: #4fc3f7;
}

.dark-theme .entity-icon {
  color: #66bb6a;
}

.dark-theme .add-icon {
  color: #ba68c8;
}

.dark-theme .node-label {
  color: #ffffff;
}

.dark-theme .entity-count {
  color: #cccccc;
}

.dark-theme .entity-type-badge {
  background: #404040;
  color: #cccccc;
}

.dark-theme .child-nodes {
  border-left: 1px solid #404040;
}

.dark-theme .add-entity-node .node-content {
  color: #ba68c8 !important;
  background: transparent !important;
}

.dark-theme .add-entity-node .node-content:hover {
  background: #2a2a2a !important;
  color: #ba68c8 !important;
}

.dark-theme .context-menu {
  background: #2a2a2a;
  border: 1px solid #404040;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.dark-theme .context-menu-item {
  color: #ffffff;
}

.dark-theme .context-menu-item:hover {
  background: #333333;
}

.dark-theme .context-menu-item[disabled] {
  color: #777777;
  cursor: not-allowed;
}

.dark-theme .context-menu-separator {
  background: #404040;
}

.dark-theme .menu-item {
  color: #ffffff;
}

.dark-theme .menu-item:hover {
  background: #333333;
}

.dark-theme .menu-item.danger {
  color: #ff6b6b;
}

.dark-theme .menu-item.danger:hover {
  background: #3d1a1a;
}

.dark-theme .language-switcher select:focus {
  border-color: #707070;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.1);
  background: rgba(70, 70, 70, 0.9);
}

.dark-theme .language-switcher select option {
  background: #404040;
  color: #ffffff;
}

/* 黑色主题下的模态框样式 */
.dark-theme .modal-overlay {
  background: rgba(0, 0, 0, 0.8) !important;
}

.dark-theme .modal,
.dark-theme .modal-content {
  background: #2d3748 !important;
  color: #e1e4e8 !important;
  border: 1px solid #4a5568 !important;
}

.dark-theme .modal-header {
  background: #2d3748 !important;
  border-bottom: 1px solid #4a5568 !important;
}

.dark-theme .modal-header h3,
.dark-theme .modal-title {
  color: #e1e4e8 !important;
}

.dark-theme .modal-body {
  background: #2d3748 !important;
  color: #e1e4e8 !important;
}

.dark-theme .modal-footer {
  background: #2d3748 !important;
  border-top: 1px solid #4a5568 !important;
}

.dark-theme .close-btn {
  color: #a0aec0 !important;
  background: transparent !important;
}

.dark-theme .close-btn:hover {
  color: #e1e4e8 !important;
  background: #4a5568 !important;
}

.dark-theme .form-group label {
  color: #e1e4e8 !important;
}

/* 更具体的选择器来覆盖scoped样式 */
.dark-theme .form-group input,
.dark-theme .form-group textarea,
.dark-theme .form-group select,
.dark-theme input,
.dark-theme textarea,
.dark-theme select {
  background: #4a5568 !important;
  color: #e1e4e8 !important;
  border: 1px solid #718096 !important;
}

.dark-theme .form-group input:focus,
.dark-theme .form-group textarea:focus,
.dark-theme .form-group select:focus,
.dark-theme input:focus,
.dark-theme textarea:focus,
.dark-theme select:focus {
  border-color: #4299e1 !important;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.2) !important;
}

.dark-theme .form-group input.error {
  border-color: #e53e3e !important;
}

.dark-theme .btn,
.dark-theme .btn-secondary {
  background: #4a5568 !important;
  color: #e1e4e8 !important;
  border-color: #4a5568 !important;
}

.dark-theme .btn:hover,
.dark-theme .btn-secondary:hover {
  background: #2d3748 !important;
  border-color: #2d3748 !important;
}

.dark-theme .btn-primary {
  background: #4299e1 !important;
  color: #ffffff !important;
  border-color: #4299e1 !important;
}

.dark-theme .btn-primary:hover:not(:disabled) {
  background: #3182ce !important;
  border-color: #3182ce !important;
}

.dark-theme .btn-primary:disabled {
  background: #4a5568 !important;
  border-color: #4a5568 !important;
  opacity: 0.6 !important;
}

.dark-theme .btn-danger {
  background: #e53e3e !important;
  color: #ffffff !important;
  border-color: #e53e3e !important;
}

.dark-theme .btn-danger:hover {
  background: #c53030 !important;
  border-color: #c53030 !important;
}
</style>
