<template>
  <div class="app-container" :class="{ 'dark-theme': isDarkTheme }" :data-locale="currentLocale" tabindex="0" @keydown="handleKeyDown">
    <!-- 顶部导航栏 -->
    <AppHeader
      :isMobile="isMobile"
      :isDarkTheme="isDarkTheme"
      :currentLocale="currentLocale"
      @toggleSidebar="toggleSidebar"
      @changeLanguage="changeLanguage"
      @toggleTheme="toggleTheme"
    />
    <!-- 工具栏 -->
    <Toolbar
      :isMobile="isMobile"
      :isDarkTheme="isDarkTheme"
      :currentLocale="currentLocale"
      :zoomLevel="!isSplitScreen || currentFocusPane === 'canvas' ? currentCanvasState?.zoom : codeCanvasState?.zoom"
      :canPaste="canPaste"
      :isSelectedEntity="isSelectedEntity"
      :isSplitScreen="isSplitScreen"
      :sidebarVisible="sidebarVisible"
      :canvasState="currentCanvasState"
      :currentFocusPane="currentFocusPane"
      :activeViewId="activeViewId"
      @saveDiagram="saveDiagram"
      @exportDiagram="exportDiagram"
      @addDatasource="showDatasourceModal = true"
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
      @pasteEntity="pasteEntity"
      @deleteEntity="deleteSelectedEntities"
      @importDiagram="importDiagram"
      @colorEntityBorder="colorEntityBorder"
      @colorEntityFont="colorEntityFont"
      @colorEntity="colorEntity"
      @toggleSidebar="toggleSidebar"
      @toggleSplitScreen="toggleSplitScreen"
      @formatCode="formatCode"
      @hideColorPalette="hideColorPalette"
      @dragCanvas="currentCanvasState.isDragMode = $event"/>

    <!-- 主布局 -->
    <div class="main-layout">
      <!-- 移动端遮罩层 -->
      <div 
        v-if="isMobile && sidebarVisible" 
        class="sidebar-overlay"
        @click="closeSidebar">
      </div>

      <!-- 中央画布区域 -->
      <main class="canvas-container" :class="{ 'split-screen': isSplitScreen }">

        <!-- 视图标签 -->
        <ViewTabs
          :views="store.views"
          :activeViewId="activeViewId"
          :isSplitScreen="isSplitScreen"
          @contextmenu="handleViewRightClick"
          @update:activeViewId="activeViewId = $event; setFocusPane('canvas')"/>

        <!-- 分屏模式 -->
        <template v-if="isSplitScreen">
           <!-- 上半部分：其他视图 -->
           <div class="split-top" :style="{ height: splitTopHeight + '%' }">
            <DSCanvas 
              ref="canvasRef"
              :isDarkTheme="isDarkTheme"
              :fieldUniqueCache="store.fieldUniqueCache"
              :canvasState="currentCanvasState"
              :entities="visibleEntities"
              :virtualEntities="virtualEntities"
              :relationships="visibleRelationships"
              :selectedEntities="store.selectedEntities"
              :isSplitScreen="isSplitScreen"
              :currentFocusPane="currentFocusPane"
              :allFieldsCache="store.allFieldsCache"
              :ENTITY_HEADER_HEIGHT="store.ENTITY_HEADER_HEIGHT"
              :FIELD_HEIGHT="store.FIELD_HEIGHT"
              :viewId="activeViewId"
              @editRelation="editRelation"
              @setFocusPane="(pane: string) => { if(pane === 'canvas' || pane === 'code') setFocusPane(pane) }"
              @entityDoubleClick="handleEntityDoubleClick"
              @entityRightClick="handleEntityRightClick"
              @canvasClick="handleCanvasClick"
              @canvasRightClick="handleCanvasRightClick"
              @selectionChange="handleSelectionChange"
              @updateEntitySize="handleUpdateEntitySize"
              @updateEntityPosition="handleUpdateEntityPosition"
              @relationshipRightClick="(relation, event) => handleRelationshipRightClick(event, relation)"
              @zoomChange="handleZoomChange"
              @panChange="handlePanChange"
              @copyEntity="copyEntity"
              @pasteEntity="pasteEntity"
              @createFieldRelation="createFieldRelation" />
          </div>
           <!-- 分屏调节手柄 -->
           <div class="split-resize-handle" :class="{ resizing: isResizingSplit }" @mousedown="startSplitResize"></div>
           <!-- 下半部分：代码视图 -->
           <div class="split-bottom" :style="{ height: 100 - splitTopHeight + '%' }" @click="handleCodeClick">
             <CodeDesignView 
               ref="codeViewRef"
               :code="codeContent"
               :language="codeLanguage"
               :theme="isDarkTheme ? 'custom-dark' : 'vs'"
               :codeDesignStatus="!isSplitScreen ? currentCanvasState : codeCanvasState"
               @update:code="codeContent = $event"
               @update:language="codeLanguage = $event"
               @save="handleCodeSave" />
           </div>
        </template>
        <!-- 普通模式 -->
        <template v-else>
          <!-- 代码设计视图 -->
          <CodeDesignView 
            v-if="activeViewId === 'code'"
            ref="codeViewRef"
            :code="codeContent"
            :language="codeLanguage"
            :theme="isDarkTheme ? 'custom-dark' : 'vs'"
            :codeDesignStatus="currentCanvasState"
            @update:code="codeContent = $event"
            @update:language="codeLanguage = $event"
            @save="handleCodeSave"
          />
          <!-- 默认画布视图 -->
          <DSCanvas 
            v-else
            ref="canvasRef"
            :isDarkTheme="isDarkTheme"
            :fieldUniqueCache="store.fieldUniqueCache"
            :canvasState="currentCanvasState"
            :entities="visibleEntities"
            :virtualEntities="virtualEntities"
            :relationships="visibleRelationships"
            :selectedEntities="store.selectedEntities"
            :isSplitScreen="isSplitScreen"
            :currentFocusPane="currentFocusPane"
            :allFieldsCache="store.allFieldsCache"
            :ENTITY_HEADER_HEIGHT="store.ENTITY_HEADER_HEIGHT"
            :FIELD_HEIGHT="store.FIELD_HEIGHT"
            :viewId="activeViewId"
            @editRelation="editRelation"
            @entityDoubleClick="handleEntityDoubleClick"
            @entityRightClick="handleEntityRightClick"
            @canvasClick="handleCanvasClick"
            @canvasRightClick="handleCanvasRightClick"
            @selectionChange="handleSelectionChange"
            @updateEntitySize="handleUpdateEntitySize"
            @updateEntityPosition="handleUpdateEntityPosition"
            @relationshipRightClick="(relation, event) => handleRelationshipRightClick(event, relation)"
            @zoomChange="handleZoomChange"
            @panChange="handlePanChange"
            @copyEntity="copyEntity"
            @pasteEntity="pasteEntity"
            @createFieldRelation="createFieldRelation" />
        </template>
        <!-- 右键菜单 -->
        <ContextMenu
          :show="contextMenu.show"
          :x="contextMenu.x"
          :y="contextMenu.y"
          :canPaste="canPaste"
          :type="contextMenu.type"
          :targetId="contextMenu.targetId || undefined"
          :entities="visibleEntities"
          :relationships="visibleRelationships"
          :isMultiSelect="isMultiSelect"
          @createEntity="createEntityAtPosition"
          @paste="pasteEntity"
          @selectAll="handleSelectAll"
          @editEntity="handleEditEntity"
          @copyEntity="copyEntity"
          @deleteEntity="handleDeleteEntity"
          @click.stop
          @editDatasource="handleEditDatasource"
          @deleteDatasource="handleDeleteDatasource"
          @createEntityFromTree="handleCreateEntity"
          @deleteRelationship="handleDeleteRelationship"
          @deleteView="handleDeleteView"
          @zoomChange="handleZoomChange"
        />
        <!-- 关系创建提示 -->
        <div v-if="store.selectedEntities.length === 2" class="relation-hint">
          <div class="hint-content">
            <span>{{ $t('relation.hint') }} {{ store.selectedEntities.length }} {{ $t('panel.entities') }}</span>
            <button @click="createRelation" class="create-relation-btn">{{ $t('relation.createRelation') }}</button>
          </div>
        </div>
        <!-- 关系创建错误提示 -->
        <div v-if="showRelationError" class="relation-error">
          <div class="error-content">
            <span>{{ relationErrorMessage }}</span>
            <button @click="showRelationError = false" class="close-error-btn">×</button>
          </div>
        </div>
      </main>
      <!-- 右侧数据库树面板（上下布局） -->
      <div v-if="sidebarVisible" class="sidebar-vertical" :style="{ width: sidebarWidth + 'px' }">
        <div class="resize-handle" :class="{ resizing: isResizingSidebar }" @mousedown="startSidebarResize"></div>
        <div class="sidebar-top" :style="{ height: 100 - sidebarBottomHeight + '%' }">
          <DatasourceTree
            :treeData="store.treeData"
            :selectedEntities="store.selectedEntities"
            :hidden="!sidebarVisible"
            :isMobile="isMobile"
            @contextmenu="showContextMenuFromTree"
            @createDatasource="showDatasourceModal = true"
            @editDatasource="handleEditDatasource"
            @deleteDatasource="handleDeleteDatasource"
            @createEntity="handleCreateEntity"
            @editEntity="handleEditEntity"
            @deleteEntity="handleDeleteEntity"
            @doubleClick="handleDoubleClick"
            @selectEntity="handleSelectEntityFromTree"
            @moveEntity="handleMoveEntity"
          />
        </div>
        <div class="sidebar-divider" :class="{ resizing: isResizingChatBox }" @mousedown="startSidebarInnerResize"></div>
        <div class="sidebar-bottom" :style="{ height: sidebarBottomHeight + '%' }">
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
      :entities="store.entities"
      :relationships="store.relationships"
      :entityNameCache="store.entityNameCache"
      :fieldNameCache="store.fieldNameCache"
      :currentDatasourceId="currentDatasourceId || store.datasources[0]?.id" 
      @save="handleEntitySave"
      @deleteRelation="handleDeleteRelation"
      @close="closeEntityModal" />
    <!-- 关系编辑模态框 -->
    <RelationEditModal
      v-if="showRelationModal"
      :ENTITY_HEADER_HEIGHT="store.ENTITY_HEADER_HEIGHT"
      :FIELD_HEIGHT="store.FIELD_HEIGHT"
      :relationship="editingRelationship as Relationship"
      :entities="store.entities"
      :fieldUniqueCache="store.fieldUniqueCache"
      :selectedEntities="store.selectedEntities"
      :relationships="visibleRelationships"
      @save="handleRelationSave"
      @close="closeRelationModal" />
    <!-- 数据库编辑模态框 -->
    <DatasourceEditModal 
      v-if="showDatasourceModal"
      :datasource="editingDatasource"
      @save="handleDatasourceSave"
      @close="closeDatasourceModal" />
    <!-- 调色板容器 -->
    <ColorPalette 
      v-if="showColorPalette"
      :visible="showColorPalette"
      :currentColor="currentColor"
      @close="showColorPalette = false"
      @colorSelected="handleColorSelected"
      @reset="handleColorReset" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDSDiagramStore } from './stores/dsDiagram'
import { DSCanvas, EntityEditModal, RelationEditModal, DatasourceTree, DatasourceEditModal, 
  Toolbar, AppHeader, ContextMenu, ViewTabs, CodeDesignView, ColorPalette } from '@/components'
import ChatBox from './components/ChatBox.vue'
import type { Entity, Datasource, View, TreeNode, CanvasState, Relationship } from '@/types/entity'
import { EntityType, TreeNodeType, RelationshipType, RelationshipCategory, CascadeOperation } from './types/entity'
import { errorHandler } from '@/utils/errorHandler'
import { relationshipApi } from './api'
import { getAllParentFields } from '@/utils/datasourceUtil'

const store = useDSDiagramStore()  // 数据源存储
const { locale, t: $t } = useI18n()  // 国际化
const currentLocale = ref(locale.value)  // 当前语言

// 响应式状态
const windowWidth = ref(window.innerWidth)  // 窗口宽度
const isMobile = computed(() => windowWidth.value <= 768) // 是否是移动端
const isTablet = computed(() => windowWidth.value <= 1024 && windowWidth.value > 768) // 是否是平板
const sidebarVisible = ref(!isMobile.value)  // 是否显示右侧数据库树面板
const isDarkTheme = ref(false)  // 是否暗色主题
const isSplitScreen = ref(false) // 是否分屏模式
const splitTopHeight = ref(50) // 上半部分高度百分比

const isResizingSplit = ref(false)  // 是否正在调整(包上下屏)
const isResizingSidebar = ref(false)  // 是否正在调整(侧屏左右调节)
const isResizingChatBox = ref(false)  // 是否正在调整(侧屏上下调节)
const currentFocusPane = ref<'canvas' | 'code'>('canvas')  // 当前焦点面板

// 调色板相关状态
const showColorPalette = ref(false)
const currentColor = ref<string>('')
const colorPaletteType = ref<'background' | 'border' | 'font'>('background')

const codeCanvasState = ref({
  MAX_ZOOM: store.MAX_ZOOM,
  MIN_ZOOM: store.MIN_ZOOM,
  zoom: 1,
  panX: 0,
  panY: 0,
  showGrid: false,
  isDragMode: false
})

// 响应式数据
const activeViewId = ref<string>('default') // 当前激活视图ID
const currentView = computed(() => 
  store.views.find(v => v.id === activeViewId.value) || codeDesignView.value
) // 当前视图

// 代码设计视图相关状态
const codeContent = ref(store.generateDatasourceCode())
const codeLanguage = ref('datasource')
// 可见实体（只显示entity类型，不显示abstract类型）
const visibleEntities = computed(() =>
  store.entities.filter(e => currentView.value?.datasourceIds.some(ds => ds === e.datasourceId) && e.entityType === EntityType.ENTITY)
)
// 可见关联（只显示当前视图下的关联）
const visibleRelationships = computed(() =>
  store.relationships.filter(r => currentView.value?.datasourceIds.some(ds => ds === r.datasourceId))
)
// 虚拟实体（用于在画布上给实体显示继承的字段信息）
const virtualEntities = computed(() =>
  store.entities.filter(e => currentView.value?.datasourceIds.some(ds => ds === e.datasourceId) && e.entityType === EntityType.ABSTRACT)
)
const currentCanvasState = computed(() => currentView.value?.canvasState || codeCanvasState)  // 当前视图的画布状态,如果视图未定义则返回默认状态

const canvasRef = ref<InstanceType<typeof DSCanvas> | null>(null)  // 画布实例
const codeViewRef = ref<InstanceType<typeof CodeDesignView> | null>(null)  // 代码视图实例
const showEntityModal = ref(false)  // 是否显示实体编辑模态框
const showRelationModal = ref(false)  // 是否显示关系编辑模态框
const showDatasourceModal = ref(false)  // 是否显示数据源编辑模态框
const showRelationError = ref(false)  // 是否显示关系创建错误提示
const relationErrorMessage = ref('')  // 关系创建错误信息
const editingEntity = ref<Entity | null>(null)  // 当前编辑的实体
const editingRelationship = ref<Relationship | null>(null)  // 当前编辑的关系
const parentEntity = ref<Entity | null>(null)   // 父实体(用于点击树+号新增实体时，显示父实体信息)
const editingDatasource = ref<Datasource | null>(null)  // 当前编辑的数据源
const canPaste = ref(false)  // 是否可以粘贴
const copiedEntities = ref<Entity[]>([])  // 复制选中的实体(用于粘贴)
const sidebarWidth = ref(240) //右侧数据库树面板宽度
const sidebarBottomHeight = ref(60) // 底部聊天框初始高度
const currentDatasourceId = ref<string | null>(null)  // 只有当点击树的+时，才需要告知当前数据库ID
// 代码设计视图
const codeDesignView = ref<View>({
  id: 'code',
  name: 'Code',
  datasourceIds: [],
  canvasState: codeCanvasState.value
})

// 右键菜单
const contextMenu = ref({
  show: false,
  x: 0,
  y: 0,
  type: '' as 'DATASOURCE' | 'ENTITY' | 'CANVAS' | 'VIEW' | 'RELATIONSHIP',
  targetId: null as string | null
})

// 计算属性
const isMultiSelect = computed(() => store.selectedEntities.length > 1)
const isSelectedEntity = computed(() => store.selectedEntities.length > 0)

watch([() => store.views, () => store.datasources, () => store.entities, () => store.relationships, () => store.indexes], async () => {
  await nextTick()
  codeContent.value = store.generateDatasourceCode()
}, { immediate: true, deep: true })

// ------------------------------ 视图方法 start------------------------------
function handleDeleteView(viewId: string) {
  if (confirm($t('messages.deleteViewConfirm'))) {
    store.deleteView(viewId)
  }
  activeViewId.value = 'default'
  hideContextMenus()
}
// 添加视图
function handleAddView(datasource: Datasource) {
  const newView: View = {
    id: Date.now().toString(),
    name: datasource.name,
    datasourceIds: [datasource.id],
    canvasState: {
      MAX_ZOOM: store.MAX_ZOOM,
      MIN_ZOOM: store.MIN_ZOOM,
      zoom: 1,
      panX: 0,
      panY: 0,
      showGrid: false,
      isDragMode: false
    }
  }
  store.views.push(newView)
  activeViewId.value = newView.id
  return newView
}
// 将数据源添加到视图
function addDatasourceToView(datasource: Datasource, view: View) {
  view.datasourceIds.push(datasource.id)
  activeViewId.value = view.id
}
// ------------------------------ 视图方法 end------------------------------

// ------------------------------ 代码设计视图方法 start------------------------------
// 处理代码保存
function handleCodeSave(code: string, language: string) {
  codeContent.value = code
  codeLanguage.value = language
  console.log('Code saved:', { code, language })
}
// ------------------------------ 代码设计视图方法 end------------------------------

// ------------------------------ 画布方法 start------------------------------
// 画布点击
function handleCanvasClick(event: MouseEvent) {
  if (!event.ctrlKey && !event.metaKey) {
    store.selectedEntities = []
  }
  hideContextMenus()
  
  // 分屏模式下设置焦点
  if (isSplitScreen.value) {
    setFocusPane('canvas')
  }
}
// 画布右键菜单
function handleCanvasRightClick(event: MouseEvent) {
  event.preventDefault()
  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.show = true
  contextMenu.value.type = 'CANVAS'

  if(isSplitScreen.value){
    currentFocusPane.value = 'canvas'
  }
}
// 实体右键菜单
function handleEntityRightClick(entity: Entity, event: MouseEvent) {
  event.preventDefault()

  store.selectedEntities = store.selectedEntities.filter(e => e.id !== entity.id)
  store.selectedEntities.push(entity)
  
  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.show = true
  contextMenu.value.type = 'ENTITY'
  contextMenu.value.targetId = entity.id

  if(isSplitScreen.value){
    currentFocusPane.value = 'canvas'
  }
}
// 选择变化
function handleSelectionChange(entities: Entity[]) {
  store.selectedEntities = entities
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
// 保存图表
function saveDiagram() {
  // 实现保存逻辑
  console.log('Save diagram')
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
  console.log('Export diagram')
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
  if(isSplitScreen.value && currentFocusPane.value === 'code') {
    handleZoomChange(Math.min(codeCanvasState.value.zoom * 1.2, codeCanvasState.value.MAX_ZOOM))
  }else{
    handleZoomChange(Math.min(currentCanvasState.value.zoom * 1.2, currentCanvasState.value.MAX_ZOOM))
  }
}
// 缩小
function zoomOut() {
  if(isSplitScreen.value && currentFocusPane.value === 'code') {
    handleZoomChange(Math.max(codeCanvasState.value.zoom / 1.2, codeCanvasState.value.MIN_ZOOM))
  }else{
    handleZoomChange(Math.max(currentCanvasState.value.zoom / 1.2, currentCanvasState.value.MIN_ZOOM))
  }
}
// 重置缩放
function resetZoom() {
  canvasRef.value?.resetZoom()
}
// 缩放变化
function handleZoomChange(level: number) {
  console.log('handleZoomChange', level)
  if(isSplitScreen.value && currentFocusPane.value === 'code') {
    codeCanvasState.value.zoom = Math.round(level * 100) / 100
    codeViewRef.value?.focus()
  }else{
    currentCanvasState.value.zoom = Math.round(level * 100) / 100
  }
}
// 平移变化
function handlePanChange(panX: number, panY: number) {
  if(isSplitScreen.value && currentFocusPane.value === 'code') {
    codeCanvasState.value.panX = panX
    codeCanvasState.value.panY = panY
  }else{
    currentCanvasState.value.panX = panX
    currentCanvasState.value.panY = panY
  }
}
function formatCode() {
  codeViewRef.value?.formatCode()
}
// 切换网格
function toggleGrid() {
  if(isSplitScreen.value && currentFocusPane.value === 'code') {
    codeCanvasState.value.showGrid = !codeCanvasState.value.showGrid
  }else{
    currentCanvasState.value.showGrid = !currentCanvasState.value.showGrid
  }
}
// 切换全屏
function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 染色实体背景
function colorEntity() {
  if (store.selectedEntities.length > 0) {
    colorPaletteType.value = 'background'
    currentColor.value = store.selectedEntities[0].backgroundColor || ''
    showColorPalette.value = true
    positionColorPalette()
  }
}

// 染色实体外框
function colorEntityBorder() {
  if (store.selectedEntities.length > 0) {
    colorPaletteType.value = 'border'
    currentColor.value = store.selectedEntities[0].borderColor || ''
    showColorPalette.value = true
    positionColorPalette()
  }
}

// 修改实体字体颜色
function colorEntityFont() {
  if (store.selectedEntities.length > 0) {
    colorPaletteType.value = 'font'
    currentColor.value = store.selectedEntities[0].fontColor || ''
    showColorPalette.value = true
    positionColorPalette()
  }
}
// ------------------------------ 工具栏方法 end------------------------------

// ------------------------------ 调色板方法 start------------------------------
// 隐藏调色板
function hideColorPalette(){
  showColorPalette.value = false
}

// 动态定位调色板
function positionColorPalette() {
  nextTick(() => {
    let toolbar: HTMLElement | null = null
    switch (colorPaletteType.value) {
      case 'background':
        toolbar = document.querySelector('.toolbar .color-entity') as HTMLElement
        break
      case 'border':
        toolbar = document.querySelector('.toolbar .color-entity-border') as HTMLElement
        break
      case 'font':
        toolbar = document.querySelector('.toolbar .color-entity-font') as HTMLElement
        break
    }
    if (toolbar) {
      const rect = toolbar.getBoundingClientRect()
      const container = document.querySelector('.color-palette') as HTMLElement
      if (container) {
        container.style.top = rect.bottom + 4 + 'px'
        container.style.left = rect.left + 'px'
      }
    }
  })
}

// 处理调色板颜色选择
function handleColorSelected(color: string) {
  if (store.selectedEntities.length > 0) {
    store.selectedEntities.forEach((entity, index) => {
      let updatedEntity = { ...entity }
      switch (colorPaletteType.value) {
        case 'background':
          updatedEntity.backgroundColor = color || undefined
          break
        case 'border':
          updatedEntity.borderColor = color || undefined
          break
        case 'font':
          updatedEntity.fontColor = color || undefined
          break
      }
      store.updateEntity(updatedEntity)
      // 直接更新selectedEntities中的对应实体
      store.selectedEntities[index] = updatedEntity
    })
  }
  showColorPalette.value = false
}

// 处理调色板重置
function handleColorReset() {
  if (store.selectedEntities.length > 0) {
    store.selectedEntities.forEach((entity, index) => {
      let updatedEntity = { ...entity }
      
      switch (colorPaletteType.value) {
        case 'background':
          updatedEntity.backgroundColor = undefined
          break
        case 'border':
          updatedEntity.borderColor = undefined
          break
        case 'font':
          updatedEntity.fontColor = undefined
          break
      }
      
      store.updateEntity(updatedEntity)
      store.selectedEntities[index] = updatedEntity
    })
  }
  showColorPalette.value = false
}
// ------------------------------ 调色板方法 end------------------------------

// ------------------------------ 复制粘贴方法 start------------------------------
// 复制实体(工具栏按钮和右键菜单)
function copyEntity(entityId?: string) {
  if (entityId) {
    const entity = store.entities.find(e => e.id === entityId)
    if (entity) {
      copiedEntities.value = [entity]
      canPaste.value = true
    }
  } else if (store.selectedEntities.length > 0) {
    // 深拷贝选中的实体
    copiedEntities.value = store.selectedEntities.map(entity => ({
      ...entity,
      fields: entity.fields.map(field => ({ ...field }))
    }))
    canPaste.value = true
  }
  hideContextMenus()
}
// 粘贴实体(工具栏按钮和右键菜单)
function pasteEntity() {
  if (copiedEntities.value.length > 0) {
    const offsetX = 20
    const offsetY = 20
    
    if(currentView.value?.datasourceIds && currentView.value?.datasourceIds.length > 0) {
      copiedEntities.value.forEach((entity) => {
        entity.datasourceId = currentView.value?.datasourceIds[0]
      })
    }

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
    store.selectedEntities = []
    copiedEntities.value = []
    canPaste.value = false
  }
  hideContextMenus()
}
// ------------------------------ 复制粘贴方法 end------------------------------

// ------------------------------ 右键菜单方法 start------------------------------
// 全选
function handleSelectAll(entities: Entity[]) {
  handleSelectionChange(entities)
  hideContextMenus()
}
// 隐藏右键菜单
function hideContextMenus() {
  contextMenu.value.show = false
}
// 视图右键菜单
function handleViewRightClick(event: MouseEvent, viewId: string) {
  event.preventDefault()
  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.type = 'VIEW'
  contextMenu.value.targetId = viewId
  contextMenu.value.show = true
}
// 从树形菜单显示右键菜单
function showContextMenuFromTree(event: MouseEvent, target: any, type: string) {
  event.preventDefault()
  event.stopPropagation()
  
  const entity = store.entities.find(e => e.id === target.id)
  if (entity) {
    if (!store.selectedEntities.some(e => e.id === entity.id)) {
      store.selectedEntities = [entity]
    }
  }
  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.type = type as 'DATASOURCE' | 'ENTITY' | 'CANVAS'
  contextMenu.value.targetId = target.id
  contextMenu.value.show = true
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
  currentDatasourceId.value = datasourceId
  showEntityModal.value = true
}
// 保存实体（实体编辑弹窗）
function handleEntitySave(entity: Entity) {
  try {
    if (editingEntity.value && editingEntity.value.id) {
      if(entity.datasourceId !== editingEntity.value.datasourceId){
        ChildEntitiesMoveToDatasource(entity, entity.datasourceId || '')
      }else{
        store.updateEntity(entity)
      }
    } else {
      store.addEntity(entity)
    }
    hideContextMenus()
    closeEntityModal()
  } catch (error) {
    errorHandler.handleBusinessError(error instanceof Error ? error.message : String(error))
  }
}
// 树节点双击
function handleDoubleClick(node: TreeNode) {
  if(node.type === TreeNodeType.ENTITY){
    handleEditEntity(node.id)
  }else if(node.type === TreeNodeType.DATASOURCE){
    handleEditDatasource(node.id)
  }
}
// 编辑实体（双击）
function handleEntityDoubleClick(entity: Entity) {
  handleEditEntity(entity.id)
  
  if(isSplitScreen.value){
    currentFocusPane.value = 'canvas'
  }
}
// 编辑实体（树形菜单点击）
function handleEditEntity(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  parentEntity.value = entity?.parentEntityId ? store.entities.find(e => e.id === entity?.parentEntityId) || null : null
  if (entity) {
    editingEntity.value = entity
    showEntityModal.value = true
  }
  hideContextMenus()
}
// 删除实体（树形菜单点击）
function handleDeleteEntity(entityId: string) {
  if (confirm($t('messages.deleteEntityConfirm'))) {
    store.deleteEntity(entityId)
  }
  hideContextMenus()
}
// 从树中选择实体（树形菜单点击）
function handleSelectEntityFromTree(entityId: string) {
  const entity = store.entities.find(e => e.id === entityId)
  if (entity) {
    store.selectedEntities = [entity]
  }
}
// 删除选中的实体
function deleteSelectedEntities() {
  if (store.selectedEntities.length > 0 && confirm($t('messages.deleteEntitiesConfirm'))) {
    store.selectedEntities.forEach(entity => {
      store.deleteEntity(entity.id)
    })
    store.selectedEntities = []
  }
  hideContextMenus()
}
// 获取当前实体的所有子实体ID（递归）
function ChildEntitiesMoveToDatasource(parentEntity: Entity, datasourceId: string) {
  if(parentEntity){
    parentEntity.datasourceId = datasourceId
    store.updateEntity(parentEntity)
    const children = store.entities.filter(e => e.parentEntityId === parentEntity.id)
    if(children.length > 0){
      // 递归获取子实体的子实体
      children.forEach(child => {
        ChildEntitiesMoveToDatasource(child, datasourceId)
      })
    }
  }
}
// 移动实体
function handleMoveEntity(source: TreeNode, target: TreeNode) {
  if (source && target) {
    const sourceEntity = store.entities.find(e => e.id === source.id)
    if(sourceEntity){
      if(target.type === TreeNodeType.ENTITY){
        sourceEntity.parentEntityId = target.id
        if(sourceEntity.datasourceId !== target.datasourceId){
          ChildEntitiesMoveToDatasource(sourceEntity, target.datasourceId || '')
        }else{
          store.updateEntity(sourceEntity)
        }
      }else if(target.type === TreeNodeType.DATASOURCE){
        sourceEntity.parentEntityId = undefined
        ChildEntitiesMoveToDatasource(sourceEntity, target.id)
      }
    }
  }
}
// 实体大小更新
function handleUpdateEntitySize(entity: Entity) {
  store.updateEntitySize(entity)
}
// 实体位置更新
function handleUpdateEntityPosition(entity: Entity) {
  const selectedEntity = store.selectedEntities.find(e => e.id === entity.id)
  if(selectedEntity){
    selectedEntity.x = entity.x
    selectedEntity.y = entity.y
  }
  store.updateEntityForDrag(entity)
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
  try {
    if (datasource.viewId === '') {
      // 如果数据源选择新视图，则确认默认视图是否包含该数据源，如果包含则删除，并添加到新视图
      const defaultView = store.views.find(v => v.id === 'default')
      if (defaultView) {
        defaultView.datasourceIds = defaultView.datasourceIds.filter(dsId => dsId !== datasource.id)
      }
      const newView = handleAddView(datasource)
      datasource.viewId = newView.id
    }else if (datasource.viewId === 'default') {
      const view = store.views.find(v => v.id === datasource.viewId)
      if (view && !view.datasourceIds.includes(datasource.id)) {
        addDatasourceToView(datasource, view)
      }
    }
    if (editingDatasource.value) {
      store.updateDatasource(datasource)
    } else {
      store.addDatasource(datasource)
    }
    showDatasourceModal.value = false
    editingDatasource.value = null
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : String(error)
    errorHandler.handleBusinessError(errorMessage)
  }
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
  hideContextMenus()
}
// ------------------------------ 数据源方法 end------------------------------

// ------------------------------ 关系方法 start------------------------------
// 创建关系
function createRelation() {
  if (store.selectedEntities.length === 2) {
    showRelationModal.value = true
  }
}

// 删除关系
function handleDeleteRelationship(relationId: string) {
  if (confirm($t('messages.deleteRelationConfirm'))) {
    store.deleteRelationship(relationId)
    hideContextMenus()
  }
}

// 删除关系
function handleRelationshipRightClick(event: MouseEvent, relation: Relationship) {
  event.preventDefault()

  contextMenu.value.x = event.clientX
  contextMenu.value.y = event.clientY
  contextMenu.value.targetId = relation.id
  contextMenu.value.type = 'RELATIONSHIP'
  contextMenu.value.show = true

  if(isSplitScreen.value){
    currentFocusPane.value = 'canvas'
  }
}

// 编辑关系
function editRelation(relation: Relationship){
  editingRelationship.value = relation
  showRelationModal.value = true
}

// 创建字段关系 
function createFieldRelation(fromEntityId: string, fromFieldId: string, toEntityId: string, toFieldId: string) {
  // 检查关系是否已存在
  if (isRelationExists(fromEntityId, fromFieldId, toEntityId, toFieldId)) {
    relationErrorMessage.value = $t('relation.errorRelationExists')
    showRelationError.value = true
    setTimeout(() => {
      showRelationError.value = false
    }, 3000)
    return
  }

  // 获取实体和字段信息
  const { fromEntity, fromField, toEntity, toField } = getEntityAndFieldInfo(fromEntityId, fromFieldId, toEntityId, toFieldId)
  
  // 验证字段兼容性
  if (!areFieldsCompatible(fromField, toField)) {
    relationErrorMessage.value = $t('relation.errorFieldTypeIncompatible')
    showRelationError.value = true
    setTimeout(() => {
      showRelationError.value = false
    }, 3000)
    return
  }

  // 创建关系名称
  const name = generateRelationName(fromEntity, toEntity)

  // 确定关系类型
  const category = determineRelationshipCategory(fromField, toField)

  // 创建并添加关系
  const relation = createRelationshipObject(fromEntity, fromField, toEntity, toField, name, category)
  store.addRelationship(relation)
}

// 检查关系是否已存在
function isRelationExists(fromEntityId: string, fromFieldId: string, toEntityId: string, toFieldId: string): boolean {
  return !!visibleRelationships.value.find(relation => 
    (fromEntityId === relation.fromEntityId && 
    fromFieldId === relation.fromFieldId && 
    toEntityId === relation.toEntityId && 
    toFieldId === relation.toFieldId) || 
    (fromEntityId === relation.toEntityId && 
    fromFieldId === relation.toFieldId && 
    toEntityId === relation.fromEntityId && 
    toFieldId === relation.fromFieldId))
}

function getEntityAndFieldInfo(fromEntityId: string, fromFieldId: string, toEntityId: string, toFieldId: string) {
  const fromEntity = visibleEntities.value.find(entity => entity.id === fromEntityId)
  const toEntity = visibleEntities.value.find(entity => entity.id === toEntityId)
  
  // 使用getAllParentFields获取所有字段（包括继承的字段）
  const fromAllFields = fromEntity ? getAllParentFields(store.entities, fromEntity.parentEntityId).concat(fromEntity.fields) : []
  const toAllFields = toEntity ? getAllParentFields(store.entities, toEntity.parentEntityId).concat(toEntity.fields) : []
  
  const fromField = fromAllFields.find(field => field.id === fromFieldId)
  const toField = toAllFields.find(field => field.id === toFieldId)
  
  return { fromEntity, fromField, toEntity, toField }
}

// 验证字段兼容性
function areFieldsCompatible(fromField: any, toField: any): boolean {
  console.log("FromField", fromField, fromField?.type, fromField?.length, fromField?.scale)
  console.log("ToField", toField, toField?.type, toField?.length, toField?.scale)
  return fromField && toField && 
         fromField.type === toField.type && 
         fromField?.length === toField?.length &&
         fromField?.scale === toField?.scale
}

// 生成关系名称
function generateRelationName(fromEntity: any, toEntity: any): string {
  const baseName = `${fromEntity?.name}_${toEntity?.name}_relation`
  const existingCount = visibleRelationships.value.filter(
    relationship => relationship.name?.startsWith(baseName)
  ).length
  return existingCount > 0 ? `${baseName}_${existingCount}` : baseName
}

// 确定关系类型
function determineRelationshipCategory(fromField: any, toField: any): RelationshipCategory {
  const isFromUnique = fromField.isPrimaryKey || fromField.isUnique
  const isToUnique = toField.isPrimaryKey || toField.isUnique

  if (isFromUnique) {
    return isToUnique ? RelationshipCategory.ONE_TO_ONE : RelationshipCategory.ONE_TO_MANY
  }
  return isToUnique ? RelationshipCategory.MANY_TO_ONE : RelationshipCategory.MANY_TO_MANY
}

// 创建关系对象
function createRelationshipObject(fromEntity: any, fromField: any, toEntity: any, toField: any, name: string, category: RelationshipCategory): Relationship {
  return {
    id: Date.now().toString(),
    name,
    datasourceId: fromEntity.datasourceId,
    fromEntityId: fromEntity.id,
    fromFieldId: fromField.id,
    toEntityId: toEntity.id,
    toFieldId: toField.id,
    type: RelationshipType.SOFT,
    category,
    cascadeCreate: false,
    cascadeDelete: CascadeOperation.NO_ACTION,
    cascadeUpdate: CascadeOperation.NO_ACTION
  }
}

// 保存关系（关系编辑弹窗）
function handleRelationSave(relation: Relationship) {
  try {
    if (editingRelationship.value && editingRelationship.value.id) {
      store.updateRelationship(relation)
    } else {
      store.addRelationship(relation)
    }
    hideContextMenus()
    closeRelationModal()
  } catch (error) {
    errorHandler.handleBusinessError(error instanceof Error ? error.message : String(error))
  }
}
// 删除关系
function handleDeleteRelation(relationId: string) {
  store.deleteRelationship(relationId)
}
// 关闭关系编辑模态框
function closeRelationModal() {
  showRelationModal.value = false
  editingRelationship.value = null
}
// ------------------------------ 关系方法 end------------------------------

// ------------------------------ 分屏方法 start------------------------------
// 切换分屏模式
function toggleSplitScreen() {
  isSplitScreen.value = !isSplitScreen.value
  if(isSplitScreen.value && activeViewId.value === 'code'){
    activeViewId.value = 'default'
  }
}

// 焦点管理方法
function setFocusPane(pane: 'canvas' | 'code') {
  currentFocusPane.value = pane
}

// 处理代码面板点击事件
function handleCodeClick() {
  if (isSplitScreen.value) {
    setFocusPane('code')
  }
}
// 分屏调节相关方法
function startSplitResize(e: MouseEvent) {
  e.preventDefault()
  isResizingSplit.value = true
  
  const startY = e.clientY
  const startHeight = splitTopHeight.value
  const containerRect = document.querySelector('.canvas-container')?.getBoundingClientRect()
  
  if (!containerRect) return
  
  const handleMouseMove = (e: MouseEvent) => {
    if (!isResizingSplit.value) return
    
    const deltaY = e.clientY - startY
    const containerHeight = containerRect.height
    const deltaPercent = (deltaY / containerHeight) * 100
    
    let newHeight = startHeight + deltaPercent
    newHeight = Math.max(20, Math.min(80, newHeight)) // 限制在20%-80%之间
    
    splitTopHeight.value = newHeight
  }
  
  const handleMouseUp = () => {
    isResizingSplit.value = false
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}
// ------------------------------ 分屏方法 end------------------------------

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
  isResizingSidebar.value = true
  const startX = e.clientX
  const startWidth = sidebarWidth.value
  function onMouseMove(ev: MouseEvent) {
    if (!isResizingSidebar.value) return
    const newWidth = Math.max(160, Math.min(500, startWidth - (ev.clientX - startX)))
    sidebarWidth.value = newWidth
  }
  function onMouseUp() {
    isResizingSidebar.value = false
    window.removeEventListener('mousemove', onMouseMove)
    window.removeEventListener('mouseup', onMouseUp)
  }
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}
// 调整右侧DatasourceTree底部高度（拖拽调整）
function startSidebarInnerResize(e: MouseEvent) {
  e.preventDefault()
  isResizingChatBox.value = true

  const startY = e.clientY
  const startHeight = sidebarBottomHeight.value
  const containerRect = document.querySelector('.sidebar-vertical')?.getBoundingClientRect()
  if (!containerRect) return

  const onMouseMove = (ev: MouseEvent) => {
    if (!isResizingChatBox.value) return

    const delta = ev.clientY - startY
    const containerHeight = containerRect.height
    const deltaPercent = (delta / containerHeight) * 100
    let newHeight = startHeight - deltaPercent
    newHeight = Math.max(20, Math.min(80, newHeight)) // 限制在20%-80%之间
    sidebarBottomHeight.value = newHeight
  }

  const onMouseUp = () => {
    isResizingChatBox.value = false
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

// 全局键盘事件
function handleKeyDown(event: KeyboardEvent) {
  // 检查事件目标是否为可编辑元素
  const target = event.target as HTMLElement
  const isEditableElement = target.tagName === 'INPUT' || 
                           target.tagName === 'TEXTAREA' || 
                           target.contentEditable === 'true' ||
                           target.closest('.tiptap-editor') !== null
  
  // 如果是可编辑元素，不处理全局快捷键
  if (isEditableElement) {
    return
  }
  event.preventDefault()
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
  font-family: var(--font-family-ui);
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
  background: #fff;
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
  z-index: 1000;
  position: relative;
  border-left: 1px solid #e1e4e8;
}
/* 右侧数据库树面板（顶部） */
.sidebar-top {
  width: 100%;
  min-height: 150px;
  overflow: auto;
  transition: height 0.1s;
}
/* 右侧数据库树面板（高度分割线） */
.sidebar-divider {
  width: 100%;
  height: 2px;
  min-height: 2px;
  background: #e1e4e8;
  cursor: row-resize;
  position: relative;
  z-index: 1000;
  transition: background 0.2s;
}
.dark-theme .sidebar-vertical {
  border-left: none
}
.dark-theme .sidebar-divider {
  background: #404040;
}
.sidebar-divider:hover,
.sidebar-divider.resizing {
  background: #007acc;
  height: 4px;
}
.dark-theme .sidebar-divider:hover,
.dark-theme .sidebar-divider.resizing {
  background: #7950ac;
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
/* 分屏模式样式 */
.canvas-container.split-screen {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.split-top {
  overflow: hidden;
  position: relative;
}
.split-bottom {
  overflow: hidden;
  position: relative;
}
.split-resize-handle {
  height: 2px;
  background: #e2e8f0;
  cursor: row-resize;
  position: relative;
  z-index: 10;
  transition: background 0.2s;
}
.split-resize-handle:hover,
.split-resize-handle.resizing {
  background: #007acc;
  height: 4px;
}

.dark-theme .split-resize-handle {
  background: #404040;
}

.dark-theme .split-resize-handle:hover,
.dark-theme .split-resize-handle.resizing {
  background: #7950ac;
}
/* 调整右侧数据库树面板宽度（拖拽调整） */
.resize-handle {
  position: absolute;
  left: 0;
  top: 0;
  width: 1px;
  height: 100%;
  cursor: col-resize;
  background: transparent;
  z-index: 2000;
  transition: background 0.2s;
}
.dark-theme .resize-handle {
  background: #404040;
}
.resize-handle:hover,
.resize-handle.resizing {
  background: #007acc;
  width: 4px;
}
.dark-theme .resize-handle:hover,
.dark-theme .resize-handle.resizing {
  background: #7950ac;
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
.dark-theme .relation-hint {
  background: #341757;
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
/* 关系创建错误提示 */
.relation-error {
  position: absolute;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  background: #d73a49;
  color: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
}
.dark-theme .relation-error {
  background: #8b2635;
}
.error-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}
.close-error-btn {
  background: transparent;
  color: #fff;
  border: none;
  padding: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}
.close-error-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}
.dark-theme .create-relation-btn {
  color: #341757;
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