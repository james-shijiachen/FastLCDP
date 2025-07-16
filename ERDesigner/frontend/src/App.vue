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
      :zoomLevel="zoomLevel"
      :sidebarVisible="sidebarVisible"
      @toggleSidebar="toggleSidebar"
      @newDiagram="newDiagram"
      @saveDiagram="saveDiagram"
      @exportDiagram="exportDiagram"
      @addEntity="createEntityAtPosition"
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
        <!-- 视图标签 -->
        <ViewTabs
          :views="views"
          :activeViewId="activeViewId"
          @update:activeViewId="activeViewId = $event"
          @addView="addView"/>
        <DSCanvas 
          ref="canvasRef"
          :zoomLevel="zoomLevel"
          :showGrid="showGrid"
          :entities="visibleEntities"
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
          :show="contextMenu.show"
          :x="contextMenu.x"
          :y="contextMenu.y"
          :canPaste="canPaste"
          :type="contextMenu.type"
          :targetId="contextMenu.targetId || undefined"
          @createEntity="createEntityAtPosition"
          @paste="paste"
          @selectAll="selectAll"
          @editEntity="handleEditEntity"
          @copyEntity="copyEntity"
          @deleteEntity="handleDeleteEntity"
          @click.stop
          @editDatasource="handleEditDatasource"
          @deleteDatasource="handleDeleteDatasource"
          @createEntityFromTree="handleCreateEntity"
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
            :selectedEntities="selectedEntities"
            :hidden="!sidebarVisible"
            :is-mobile="isMobile"
            @contextmenu="showContextMenuFromTree"
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
      :parentEntity="parentEntity"
      :datasources="store.datasources"
      :availableParents="availableParents"
      :currentDatasourceId="currentDatasourceId || store.datasources[0]?.id"
      :parentFields="parentFields"
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
    <!-- 视图编辑模态框 -->
    <ViewEditModal
      v-if="showViewModal"
      :datasources="store.datasources"
      :view="editingView"
      @save="handleSaveView"
      @close="showViewModal = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDSDiagramStore } from './stores/dsDiagram'
import { DSCanvas, EntityEditModal, RelationEditModal, DatasourceTree, DatasourceEditModal, 
  Toolbar, AppHeader, ContextMenu, ViewTabs, ViewEditModal} from './components'
import ChatBox from './components/ChatBox.vue'
import type { Entity, Field, Datasource, View } from './types/entity'

const store = useDSDiagramStore()  // 数据源存储
const { locale, t: $t } = useI18n()  // 国际化
const currentLocale = ref(locale.value)  // 当前语言

// 响应式状态
const windowWidth = ref(window.innerWidth)  // 窗口宽度
const isMobile = computed(() => windowWidth.value <= 768) // 是否是移动端
const isTablet = computed(() => windowWidth.value <= 1024 && windowWidth.value > 768) // 是否是平板
const sidebarVisible = ref(!isMobile.value)  // 是否显示右侧数据库树面板
const isDarkTheme = ref(false)  // 是否暗色主题

// 响应式数据
const views = ref<View[]>([{
  id: 'default',
  name: 'Default',
  datasources: store.datasources,
  createdTime: new Date()
}])
const activeViewId = ref<string>('default') // 当前激活视图ID
const currentView = computed(() => views.value.find(v => v.id === activeViewId.value))
const visibleEntities = computed(() =>
  store.entities.filter(e => currentView.value?.datasources.some(ds => ds.id === e.datasourceId))
)

const canvasRef = ref<InstanceType<typeof DSCanvas> | null>(null)  // 画布实例
const showEntityModal = ref(false)  // 是否显示实体编辑模态框
const showRelationModal = ref(false)  // 是否显示关系编辑模态框
const showDatasourceModal = ref(false)  // 是否显示数据源编辑模态框
const showViewModal = ref(false)  // 是否显示视图编辑模态框
const editingView = ref<View | null>(null)  // 当前编辑的视图
const editingEntity = ref<Entity | null>(null)  // 当前编辑的实体
const parentEntity = ref<Entity | null>(null)   // 父实体(用于新增实体时，显示父实体信息)
const editingDatasource = ref<Datasource | null>(null)  // 当前编辑的数据源
const selectedEntities = ref<Entity[]>([])  // 选中的实体（多选）
const zoomLevel = ref(1)  // 缩放级别
const showGrid = ref(true)  // 是否显示网格
const canPaste = ref(false)  // 是否可以粘贴
const copiedEntities = ref<Entity[]>([])  // 复制选中的实体(用于粘贴)
const sidebarWidth = ref(240) //右侧数据库树面板宽度
const sidebarBottomHeight = ref(600) // 底部聊天框初始高度
const parentFields = ref<Field[]>([])  // 父实体的字段(用于新增实体时，显示父实体字段)
const currentDatasourceId = ref<string | null>(null)  // 当前编辑的数据源ID或新增实体的数据源ID

// 右键菜单
const contextMenu = ref({
  show: false,
  x: 0,
  y: 0,
  type: '' as 'datasource' | 'entity' | 'canvas',
  targetId: null as string | null
})

// 计算属性
const isMultiSelect = computed(() => selectedEntities.value.length > 1)

// 获取新增实体时，子代实体ID
const descendantIds = computed(() =>
  editingEntity.value
    ? getDescendantIds(store.entities, editingEntity.value.id)
    : []
);

// 获取新增实体时，可用父代实体（不包括子代实体和当前编辑的实体）
const availableParents = computed(() =>
  store.entities.filter(e =>
    e.datasourceId === (editingEntity.value?.datasourceId || store.datasources[0]?.id) &&
    e.id !== editingEntity.value?.id &&
    !descendantIds.value.includes(e.id)
  )
);

// 获取所有子孙节点id，防止循环继承
function getDescendantIds(entities: Entity[], entityId: string): string[] {
  const descendants: string[] = [];
  function findChildren(parentId: string) {
    entities.forEach(e => {
      if (e.parentEntityId === parentId) {
        descendants.push(e.id);
        findChildren(e.id);
      }
    });
  }
  findChildren(entityId);
  return descendants;
}

// 从树形菜单显示右键菜单
function showContextMenuFromTree(event: MouseEvent, target: any, type: string) {
  event.preventDefault()
  event.stopPropagation()

  const entity = store.entities.find(e => e.id === target.id)
  if (entity) {
    if (!selectedEntities.value.includes(entity)) {
      selectedEntities.value = [entity]
    }
  }

  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.show = true
  contextMenu.value.type = type as 'datasource' | 'entity' | 'canvas'
  contextMenu.value.targetId = target.id
}

// ------------------------------ 视图方法 start------------------------------
function addView() {
  const newView: View = {
    id: Date.now().toString(),
    name: `View ${views.value.length + 1}`,
    datasources: [], // 或默认数据
    createdTime: new Date()
  }
  views.value.push(newView)
  activeViewId.value = newView.id
}
function handleAddView() {
  editingView.value = null
  showViewModal.value = true
}
function handleEditView(view: View) {
  editingView.value = view
  showViewModal.value = true
}
function handleSaveView({ name, datasourceIds }: { name: string, datasourceIds: string[] }) {
  // 获取该数据库下所有实体
  const datasources = store.datasources.filter(ds => datasourceIds.includes(ds.id))
  if (editingView.value) {
    // 编辑
    editingView.value.name = name
    editingView.value.datasources = datasources
  } else {
    // 新增
    const newView: View = {
      id: Date.now().toString(),
      name,
      datasources: datasources,
      createdTime: new Date()
    }
    views.value.push(newView)
    activeViewId.value = newView.id
  }
  showViewModal.value = false
}
// ------------------------------ 视图方法 end------------------------------

// ------------------------------ 画布方法 start------------------------------
// 画布点击
function handleCanvasClick(event: MouseEvent) {
  if (!event.ctrlKey && !event.metaKey) {
    selectedEntities.value = []
  }
  hideContextMenus()
}
// 画布右键菜单
function handleCanvasRightClick(event: MouseEvent) {
  event.preventDefault()
  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.show = true
  contextMenu.value.type = 'canvas'
}
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
// 实体右键菜单
function handleEntityRightClick(entity: Entity, event: MouseEvent) {
  event.preventDefault()
  event.stopPropagation()
  
  if (!selectedEntities.value.includes(entity)) {
    selectedEntities.value = [entity]
  }
  
  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.show = true
  contextMenu.value.type = 'entity'
  contextMenu.value.targetId = entity.id
}
// 选择变化
function handleSelectionChange(entities: Entity[]) {
  selectedEntities.value = entities
}
// ------------------------------ 画布方法 end------------------------------

// ------------------------------ 导航栏方法 start------------------------------
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
// ------------------------------ 导航栏方法 end------------------------------

// ------------------------------ 工具栏方法 start------------------------------
// 新建图表
function newDiagram() {
  if (confirm($t('messages.newDiagramConfirm'))) {
    store.clearDiagram()
    selectedEntities.value = []
  }
}
// 保存图表
function saveDiagram() {
  // 实现保存逻辑
  console.log('保存图表')
  alert($t('messages.diagramSaved'))
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
// 缩放变化
function handleZoomChange(level: number) {
  zoomLevel.value = level
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
// 染色实体外框
function colorEntityBorder() {
  console.log('染色实体外框')
}
// ------------------------------ 工具栏方法 end------------------------------

// ------------------------------ 复制粘贴方法 start------------------------------
// 复制实体(工具栏按钮和右键菜单)
function copyEntity(entityId: string) {
  if (entityId) {
    const entity = store.entities.find(e => e.id === entityId)
    if (entity) {
      copiedEntities.value = [entity]
      canPaste.value = true
    }
  } else if (selectedEntities.value.length > 0) {
    // 深拷贝选中的实体
    copiedEntities.value = selectedEntities.value.map(entity => ({
      ...entity,
      fields: entity.fields.map(field => ({ ...field }))
    }))
    canPaste.value = true
  }
  hideContextMenus()
}
// 粘贴实体(工具栏按钮和右键菜单)
function paste() {
  if (copiedEntities.value.length > 0) {
    const offsetX = 20
    const offsetY = 20
    
    copiedEntities.value.forEach((entity, index) => {
      const newEntity: Entity = {
        ...entity,
        id: Date.now().toString() + '_' + index,
        name: entity.name + ' Copy',
        x: contextMenu.value.x + (index * offsetX),
        y: contextMenu.value.y + (index * offsetY),
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
    canPaste.value = false
  }
  hideContextMenus()
}
// ------------------------------ 复制粘贴方法 end------------------------------

// ------------------------------ 右键菜单方法 start------------------------------
// 全选
function selectAll() {
  selectedEntities.value = [...store.entities]
  hideContextMenus()
}
// 隐藏右键菜单
function hideContextMenus() {
  contextMenu.value.show = false
}
// ------------------------------ 右键菜单方法 end------------------------------

// ------------------------------ 实体方法 start------------------------------
// 关闭实体编辑模态框
function closeEntityModal() {
  showEntityModal.value = false
  editingEntity.value = null
}
// 创建实体（点击工具栏按钮和右键菜单）
function createEntityAtPosition() {
  handleCreateEntity(store.datasources[0]?.id || '', '')
  hideContextMenus()  
}
// 创建实体（树形菜单点击、工具栏点击、右键菜单点击）
function handleCreateEntity(datasourceId: string, parentEntityId?: string) {
  parentEntity.value = parentEntityId ? store.entities.find(e => e.id === parentEntityId) || null : null
  parentFields.value = parentEntityId ? getAllParentFields(store.entities, parentEntityId) : []
  currentDatasourceId.value = datasourceId
  showEntityModal.value = true
}
// 保存实体（实体编辑弹窗）
function handleEntitySave(entity: Entity) {
  if (editingEntity.value && editingEntity.value.id) {
    store.updateEntity(entity)
  } else {
    // 新增实体，parentEntityId 需保留
    store.addEntity(entity)
  }
  closeEntityModal()
}
// 递归获取所有父级字段
function getAllParentFields(entities: Entity[], parentEntityId: string | undefined): Field[] {
  const result: Field[] = [];
  let currentParentId = parentEntityId;
  while (currentParentId) {
    const parent = entities.find(e => e.id === currentParentId);
    if (parent) {
      // 先递归上级，再加本级，保证顺序
      result.unshift(...parent.fields);
      currentParentId = parent.parentEntityId;
    } else {
      break;
    }
  }
  return result;
}
// 编辑实体（双击）
function handleEntityDoubleClick(entity: Entity) {
  handleEditEntity(entity.id)
}
// 编辑实体（树形菜单点击）
function handleEditEntity(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  if (entity) {
    editingEntity.value = entity
    parentFields.value = entity.parentEntityId ? getAllParentFields(store.entities, entity.parentEntityId) : []
    showEntityModal.value = true
  }
  hideContextMenus()
}
// 删除实体（树形菜单点击）
function handleDeleteEntity(entityId: string) {
  if (confirm($t('messages.deleteEntityConfirm'))) {
    store.deleteEntity(entityId)
  }
}
// 从树中选择实体（树形菜单点击）
function handleSelectEntityFromTree(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  if (entity) {
    selectedEntities.value = [entity]
  }
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
// ------------------------------ 实体方法 end------------------------------

// ------------------------------ 数据源方法 start------------------------------
// 编辑数据源（树形菜单点击）
function handleEditDatasource(datasourceId: string) {
  const datasource = store.datasources.find(ds => ds.id === datasourceId)
  if (datasource) {
    editingDatasource.value = datasource
    showDatasourceModal.value = true
  }
}
// 保存数据源（数据源编辑弹窗）
function handleDatasourceSave(datasource: Datasource) {
  if (editingDatasource.value) {
    store.updateDatasource(datasource)
  } else {
    store.addDatasource(datasource)
  }
  showDatasourceModal.value = false
  editingDatasource.value = null
}
// 关闭数据源编辑模态框
function closeDatasourceModal() {
  showDatasourceModal.value = false
  editingDatasource.value = null
}
// 删除数据源（树形菜单点击）
function handleDeleteDatasource(datasourceId: string) {
  if (confirm($t('messages.deleteDatasourceConfirm'))) {
    store.deleteDatasource(datasourceId)
  }
}
// ------------------------------ 数据源方法 end------------------------------

// ------------------------------ 关系方法 start------------------------------
// 创建关系
function createRelation() {
  if (selectedEntities.value.length === 2) {
    showRelationModal.value = true
  }
}
// 保存关系（关系编辑弹窗）
function handleRelationSave(relation: any) {
  store.addRelationship(relation)
  closeRelationModal()
}
// 关闭关系编辑模态框
function closeRelationModal() {
  showRelationModal.value = false
}
// ------------------------------ 关系方法 end------------------------------

// ------------------------------ 侧边栏方法 start------------------------------
// 响应式方法（初始化侧边栏）
function handleResize() {
  windowWidth.value = window.innerWidth
  // 在桌面端自动显示侧边栏，移动端自动隐藏
  if (!isMobile.value) {
    sidebarVisible.value = true
  } else {
    sidebarVisible.value = false
  }
}
// 切换侧边栏（点击工具栏按钮）
function toggleSidebar() {
  sidebarVisible.value = !sidebarVisible.value
}
// 关闭侧边栏（移动端点击遮罩层）
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
// ------------------------------ 侧边栏方法 end------------------------------

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

// 卸载
onUnmounted(() => {
  document.removeEventListener('click', hideContextMenus)
  document.removeEventListener('keydown', handleKeyDown)
  window.removeEventListener('resize', handleResize)
})

// 全局键盘事件（保留 未使用）
function handleKeyDown(event: KeyboardEvent) {
  // if ((event.key === 'Delete' || event.key === 'Backspace') && selectedEntities.value.length > 0) {
  //   event.preventDefault()
  //   deleteSelectedEntities()
  // } else if (event.key === 'Escape') {
  //   selectedEntities.value = []
  //   hideContextMenus()
  // } else if (event.ctrlKey && event.key === 'a') {
  //   event.preventDefault()
  //   selectAll()
  // }
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
  flex-direction: column;
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
/* 右侧数据库树面板（高度分割线） */
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
