<template>
  <div class="app-container" :class="{ 'dark-theme': isDarkTheme }">
    <!-- 顶部导航栏 -->
    <AppHeader
      :is-mobile="isMobile"
      :is-dark-theme="isDarkTheme"
      :current-locale="currentLocale"
      @toggleSidebar="toggleSidebar"
      @changeLanguage="changeLanguage"
      @toggleTheme="toggleTheme"
    />
    <!-- 工具栏 -->
    <Toolbar
      :is-mobile="isMobile"
      :is-dark-theme="isDarkTheme"
      :current-locale="currentLocale"
      :zoom-level="zoomLevel"
      :sidebar-visible="sidebarVisible"
      @toggleSidebar="toggleSidebar"
      @newDiagram="newDiagram"
      @saveDiagram="saveDiagram"
      @exportDiagram="exportDiagram"
      @addEntity="addEntityAtPosition"
      @undo="undo"
      @redo="redo"
      @zoomIn="zoomIn"
      @zoomOut="zoomOut"
      @setZoom="handleZoomChange"
      @resetZoom="resetZoom"
      @toggleGrid="toggleGrid"
      @toggleFullscreen="toggleFullscreen"
      @copyEntity="copyEntity"
      @pasteEntity="paste"
      @deleteEntity="deleteSelectedEntities"
      @importDiagram="importDiagram"
      @colorEntityBorder="colorEntityBorder"
    />

    <!-- 主布局 -->
    <div class="main-layout">
      <!-- 移动端遮罩层 -->
      <div 
        v-if="isMobile && sidebarVisible" 
        class="sidebar-overlay"
        @click="closeSidebar">
      </div>
      <!-- 中央画布区域 -->
      <main class="canvas-container">
        <DSCanvas 
          ref="canvasRef"
          :zoomLevel="zoomLevel"
          :showGrid="showGrid"
          :selectedEntities="selectedEntities"
          @entityClick="handleEntityClick"
          @entityDoubleClick="handleEntityDoubleClick"
          @entityRightClick="handleEntityRightClick"
          @canvasClick="handleCanvasClick"
          @canvasRightClick="handleCanvasRightClick"
          @selectionChange="handleSelectionChange"
          @zoomChange="handleZoomChange"
          :style="{ '--zoom-level': zoomLevel }"
        />
        <!-- 右键菜单 -->
        <ContextMenu
          :show="showContextMenu || showEntityContextMenu"
          :x="contextMenuX"
          :y="contextMenuY"
          :canPaste="canPaste"
          :type="showEntityContextMenu ? 'entity' : 'canvas'"
          @addEntity="addEntityAtPosition"
          @paste="paste"
          @selectAll="selectAll"
          @editEntity="editEntity"
          @copyEntity="copyEntity"
          @deleteEntity="deleteSelectedEntities"
          @bringToFront="bringToFront"
          @sendToBack="sendToBack"
          @click.stop
        />
        <!-- 关系创建提示 -->
        <div v-if="selectedEntities.length === 2" class="relation-hint">
          <div class="hint-content">
            <span>{{ $t('relation.hint') }} {{ selectedEntities.length }} {{ $t('panel.entities') }}</span>
            <button @click="createRelation" class="create-relation-btn">{{ $t('relation.createRelation') }}</button>
          </div>
        </div>
      </main>
      <!-- 右侧数据库树面板（上下布局） -->
      <div v-if="sidebarVisible" class="sidebar-vertical" :style="{ width: sidebarWidth + 'px' }">
        <div class="resize-handle" @mousedown="startSidebarResize"></div>
        <div class="sidebar-top" :style="{ height: `calc(100% - ${sidebarBottomHeight}px)` }">
          <DatasourceTree
            :treeData="store.treeData"
            :selectedEntityId="selectedEntities[0]?.id"
            :hidden="!sidebarVisible"
            :is-mobile="isMobile"
            @createDatasource="showDatasourceModal = true"
            @editDatasource="handleEditDatasource"
            @deleteDatasource="handleDeleteDatasource"
            @createEntity="handleCreateEntity"
            @editEntity="handleEditEntity"
            @deleteEntity="handleDeleteEntity"
            @selectEntity="handleSelectEntityFromTree"
          />
        </div>
        <div class="sidebar-divider" @mousedown="startSidebarInnerResize"></div>
        <div class="sidebar-bottom" :style="{ height: sidebarBottomHeight + 'px' }">
          <ChatBox v-if="sidebarVisible" />
        </div>
      </div>
    </div>
    <!-- 实体编辑模态框 -->
    <EntityEditModal 
      v-if="showEntityModal"
      :entity="editingEntity"
      :datasources="store.datasources"
      :available-parents="store.entities.filter(e => e.datasourceId === (editingEntity?.datasourceId || store.datasources[0]?.id))"
      :default-datasource-id="store.datasources[0]?.id"
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
    <DatasourceEditModal 
      v-if="showDatasourceModal"
      :datasource="editingDatasource"
      @save="handleDatasourceSave"
      @close="closeDatasourceModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDSDiagramStore } from './stores/dsDiagram'
import { DSCanvas, EntityEditModal, RelationEditModal, DatasourceTree, DatasourceEditModal, 
  Toolbar, AppHeader, ContextMenu} from './components'
import ChatBox from './components/ChatBox.vue'
import type { Entity, Field, Datasource } from './types/entity'

const store = useDSDiagramStore()
const { locale, t: $t } = useI18n()
const currentLocale = ref(locale.value)

// 响应式状态
const windowWidth = ref(window.innerWidth)
const isMobile = computed(() => windowWidth.value <= 768)
const isTablet = computed(() => windowWidth.value <= 1024 && windowWidth.value > 768)
const sidebarVisible = ref(!isMobile.value)
const isDarkTheme = ref(false)

// 响应式数据
const canvasRef = ref<InstanceType<typeof DSCanvas> | null>(null)
const showEntityModal = ref(false)
const showRelationModal = ref(false)
const showDatasourceModal = ref(false)
const showContextMenu = ref(false)
const showEntityContextMenu = ref(false)
const editingEntity = ref<Entity | null>(null)
const editingDatasource = ref<Datasource | null>(null)
const selectedEntities = ref<Entity[]>([])
const contextMenuX = ref(0)
const contextMenuY = ref(0)
const zoomLevel = ref(1)
const showGrid = ref(true)
const canPaste = ref(false)
const copiedEntities = ref<Entity[]>([])
const sidebarWidth = ref(240) //右侧数据库树面板宽度
const sidebarBottomHeight = ref(220) // 底部聊天框初始高度

// 计算属性
const isMultiSelect = computed(() => selectedEntities.value.length > 1)

// 实体点击
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
  hideContextMenus()
}

function colorEntityBorder() {
  console.log('染色实体外框')
}

// 实体双击
function handleEntityDoubleClick(entity: Entity) {
  editingEntity.value = entity
  showEntityModal.value = true
}

// 画布点击
function handleCanvasClick(event: MouseEvent) {
  if (!event.ctrlKey && !event.metaKey) {
    selectedEntities.value = []
  }
  console.log('handleCanvasClick', event)
  hideContextMenus()
}

// 画布右键菜单
function handleCanvasRightClick(event: MouseEvent) {
  event.preventDefault()
  contextMenuX.value = event.clientX
  contextMenuY.value = event.clientY
  showContextMenu.value = true
  showEntityContextMenu.value = false
}

// 实体右键菜单
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

// 选择变化
function handleSelectionChange(entities: Entity[]) {
  selectedEntities.value = entities
}

// 缩放变化
function handleZoomChange(level: number) {
  zoomLevel.value = level
}

// 新建图表
function newDiagram() {
  if (confirm($t('messages.newDiagramConfirm'))) {
    store.clearDiagram()
    selectedEntities.value = []
  }
}

// 导入图表
function importDiagram() {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.json,application/json'
  input.onchange = (e: any) => {
    const file = e.target.files[0]
    if (!file) return
    const reader = new FileReader()
    reader.onload = (event) => {
      try {
        const data = JSON.parse(event.target?.result as string)
        if (data.entities && data.relations) {
          store.loadDiagram(data)
          alert($t('messages.importSuccess'))
        } else {
          alert($t('messages.importInvalid'))
        }
      } catch {
        alert($t('messages.importInvalid'))
      }
    }
    reader.readAsText(file)
  }
  input.click()
}

// 保存图表
function saveDiagram() {
  // 实现保存逻辑
  console.log('保存图表')
  alert($t('messages.diagramSaved'))
}

// 导出图表
function exportDiagram() {
  // 实现导出逻辑
  console.log('导出图表')
}

// 撤销
function undo() {
  store.undo()
}

// 重做
function redo() {
  store.redo()
}

// 切换主题
function toggleTheme() {
  isDarkTheme.value = !isDarkTheme.value
  // 保存主题设置到本地存储
  localStorage.setItem('theme', isDarkTheme.value ? 'dark' : 'light')
}

// 加载主题
function loadTheme() {
  const savedTheme = localStorage.getItem('theme')
  isDarkTheme.value = savedTheme === 'dark'
}

// 切换语言
function changeLanguage(newLocale: string) {
  currentLocale.value = newLocale
  locale.value = newLocale
  localStorage.setItem('locale', newLocale)
  document.title = $t('app.title')
}

// 加载语言
function loadLocale() {
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale) {
    currentLocale.value = savedLocale
    locale.value = savedLocale
  }
}

// 放大
function zoomIn() {
  canvasRef.value?.zoomIn()
}

// 缩小
function zoomOut() {
  canvasRef.value?.zoomOut()
}

// 重置缩放
function resetZoom() {
  canvasRef.value?.resetZoom()
}

// 切换网格
function toggleGrid() {
  showGrid.value = !showGrid.value
}

// 切换全屏
function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 添加实体
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
    datasourceId: store.datasources[0]?.id || '',
    entityType: 'entity'
  }
  
  store.addEntity(entity)
  hideContextMenus()
}

// 编辑实体
function editEntity() {
  if (selectedEntities.value.length === 1) {
    editingEntity.value = selectedEntities.value[0]
    showEntityModal.value = true
  }
  hideContextMenus()
}

// 复制实体
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

// 粘贴实体
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

// 删除选中的实体
function deleteSelectedEntities() {
  if (selectedEntities.value.length > 0 && confirm($t('messages.deleteEntitiesConfirm'))) {
    selectedEntities.value.forEach(entity => {
      store.deleteEntity(entity.id)
    })
    selectedEntities.value = []
  }
  hideContextMenus()
}

// 全选
function selectAll() {
  selectedEntities.value = [...store.entities]
  hideContextMenus()
}

// 创建关系
function createRelation() {
  if (selectedEntities.value.length === 2) {
    showRelationModal.value = true
  }
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

function closeEntityModal() {
  showEntityModal.value = false
  editingEntity.value = null
}

function closeRelationModal() {
  showRelationModal.value = false
}

// 保存实体
function handleEntitySave(entity: Entity) {
  if (editingEntity.value && editingEntity.value.id) {
    store.updateEntity(entity)
  } else {
    // 新增实体，parentEntityId 需保留
    store.addEntity(entity)
  }
  closeEntityModal()
  // 新增后刷新树结构（如有必要）
  if (typeof store.loadDatasources === 'function') {
    store.loadDatasources()
  }
}

// 保存关系
function handleRelationSave(relation: any) {
  store.addRelationship(relation)
  closeRelationModal()
}

// 编辑数据源
function handleEditDatasource(datasourceId: string) {
  const datasource = store.datasources.find(ds => ds.id === datasourceId)
  if (datasource) {
    editingDatasource.value = datasource
    showDatasourceModal.value = true
  }
}

// 删除数据源
function handleDeleteDatasource(datasourceId: string) {
  if (confirm($t('messages.deleteDatasourceConfirm'))) {
    store.deleteDatasource(datasourceId)
  }
}

// 创建实体
function handleCreateEntity(datasourceId: string, parentEntityId?: string) {
  // 新增实体时，弹窗应带上 datasourceId 和 parentEntityId
  editingEntity.value = {
    id: '',
    name: '',
    comment: '',
    datasourceId,
    parentEntityId,
    entityType: 'entity',
    fields: [],
    x: 100,
    y: 100,
    width: 200,
    height: 60,
    backgroundColor: '#ffffff',
    borderColor: '#24292e'
  }
  showEntityModal.value = true
}

// 编辑实体
function handleEditEntity(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  if (entity) {
    editingEntity.value = entity
    showEntityModal.value = true
  }
}

// 删除实体
function handleDeleteEntity(entityId: string) {
  if (confirm($t('messages.deleteEntityConfirm'))) {
    store.deleteEntity(entityId)
  }
}

// 从树中选择实体
function handleSelectEntityFromTree(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  if (entity) {
    selectedEntities.value = [entity]
  }
}

// 保存数据库
function handleDatasourceSave(datasource: Datasource) {
  if (editingDatasource.value) {
    store.updateDatasource(datasource)
  } else {
    store.addDatasource(datasource)
  }
  showDatasourceModal.value = false
  editingDatasource.value = null
}

// 关闭数据库编辑模态框
function closeDatasourceModal() {
  showDatasourceModal.value = false
  editingDatasource.value = null
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

// 切换侧边栏
function toggleSidebar() {
  sidebarVisible.value = !sidebarVisible.value
}

// 关闭侧边栏
function closeSidebar() {
  sidebarVisible.value = false
}

// 调整右侧面板宽度（拖拽调整）
function startSidebarResize(e: MouseEvent) {
  const startX = e.clientX
  const startWidth = sidebarWidth.value
  function onMouseMove(ev: MouseEvent) {
    const newWidth = Math.max(160, Math.min(500, startWidth - (ev.clientX - startX)))
    sidebarWidth.value = newWidth
  }
  function onMouseUp() {
    window.removeEventListener('mousemove', onMouseMove)
    window.removeEventListener('mouseup', onMouseUp)
  }
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}

// 调整右侧chatbox底部高度（拖拽调整）
function startSidebarInnerResize(e: MouseEvent) {
  const startY = e.clientY
  const startHeight = sidebarBottomHeight.value
  function onMouseMove(ev: MouseEvent) {
    const delta = ev.clientY - startY
    const newHeight = Math.max(200, Math.min(600, startHeight - delta))
    sidebarBottomHeight.value = newHeight
  }
  function onMouseUp() {
    window.removeEventListener('mousemove', onMouseMove)
    window.removeEventListener('mouseup', onMouseUp)
  }
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
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
  // 加载数据源
  store.loadDatasources()
})
onUnmounted(() => {
  document.removeEventListener('click', hideContextMenus)
  document.removeEventListener('keydown', handleKeyDown)
  window.removeEventListener('resize', handleResize)
})

// 键盘事件
function handleKeyDown(event: KeyboardEvent) {
  if (event.key === 'Delete' && selectedEntities.value.length > 0) {
    deleteSelectedEntities()
  } else if (event.key === 'Escape') {
    selectedEntities.value = []
    hideContextMenus()
  } else if (event.ctrlKey && event.key === 'a') {
    event.preventDefault()
    selectAll()
  }
}
</script>

<style scoped>
/* 主容器 */
.app-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
/* 主布局 */
.main-layout {
  flex: 1 1 0;
  min-height: 0;
  height: 100%;
  display: flex;
}
/* 画布容器 */
.canvas-container {
  flex: 1;
  height: 100%;
  min-height: 0;
  display: flex;
  background: #f6f8fa;
  overflow: hidden;
}
/* 右侧数据库树面板（上下布局） */
.sidebar-vertical {
  display: flex;
  flex-direction: column;
  width: var(--sidebar-width, 280px);
  min-width: 280px;
  max-width: 500px;
  height: 100%;
  min-height: 0;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  z-index: 1000;
  position: relative;
}
/* 右侧数据库树面板（顶部） */
.sidebar-top {
  width: 100%;
  overflow: auto;
  transition: height 0.1s;
}
/* 右侧数据库树面板（分割线） */
.sidebar-divider {
  width: 100%;
  height: 3px;
  cursor: ns-resize;
  z-index: 2;
  background: transparent;
  transition: background 0.2s;
  border-top: 1px solid #e1e4e8;
}
.dark-theme .sidebar-divider {
  border-top: 1px solid #404040;
  background: #2a2a2a;
}
.sidebar-divider:hover {
  background: #e1e4e8;
}
/* 右侧数据库树面板（底部） */
.sidebar-bottom {
  width: 100%;
  min-height: var(--sidebar-bottom-height);
  background: #f8fafc;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.04);
  transition: height 0.1s;
  overflow: auto;
}
/* 移动端遮罩层 */
.sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
}
/* 调整右侧数据库树面板宽度（拖拽调整） */
.resize-handle {
  position: absolute;
  left: 0;
  top: 0;
  width: 3px;
  height: 100%;
  cursor: ew-resize;
  background: transparent;
  z-index: 2;
  transition: background 0.2s;
}
.resize-handle:hover {
  background: #e1e4e8;
}
/* 关系创建提示 */
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
/* 响应式样式 */
@media (max-width: var(--mobile-breakpoint)) {
  .app-container {
    flex-direction: column;
  }
  .main-layout {
    position: relative;
  }
  .canvas-container {
    width: 100%;
    margin-left: 0;
  }
}
</style>
