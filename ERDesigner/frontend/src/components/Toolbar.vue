<template>
  <div class="toolbar-wrapper" @wheel.prevent="handleModalWheel">
    <button class="scroll-btn left" @click="scrollLeft" v-show="showLeftScroll" :aria-label="$t('toolbar.scrollLeft')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
        <polyline points="15 18 9 12 15 6"/>
      </svg>
    </button>
    <div class="toolbar" ref="toolbarRef">
      <!-- 保存 -->
      <button @click="saveDiagram" :title="$t('toolbar.saveDiagram')" :aria-label="$t('toolbar.saveDiagram')">
        <Icon name="save" />
      </button>
      <!-- 导入-->
      <button @click="importDiagram" :title="$t('toolbar.importDiagram')" :aria-label="$t('toolbar.importDiagram')">
        <Icon name="import" />
      </button>
      <!-- 导出-->
      <button @click="exportDiagram" :title="$t('toolbar.exportDiagram')" :aria-label="$t('toolbar.exportDiagram')">
        <Icon name="export" />
      </button>
      <!-- 撤销 -->
      <div class="toolbar-separator"></div>
      <button @click="undo" :title="$t('toolbar.undo')" :aria-label="$t('toolbar.undo')">
        <Icon name="undo" />
      </button>
      <!-- 重做 -->
      <button @click="redo" :title="$t('toolbar.redo')" :aria-label="$t('toolbar.redo')">
        <Icon name="redo" />
      </button>
      <div class="toolbar-separator"></div>
      <!-- 放大 -->
      <button @click="zoomIn" :title="$t('toolbar.zoomIn')" :aria-label="$t('toolbar.zoomIn')">
        <Icon name="zoom-in" />
      </button>
      <!-- 百分比显示/设置 -->
      <button
          ref="zoomButtonRef"
          class="zoom-percentage"
          @click="toggleDropdown"
          @dblclick="enableInput"
          :title="$t('toolbar.setZoom')"
          :aria-label="$t('toolbar.setZoom')">

          <span class="zoom-text">
            <span v-if="!inputMode">{{ Math.round((zoomLevel ?? 1) * 100) }}%</span>
            <span v-else style="white-space: nowrap;">
              <input
                ref="zoomInputRef"
                v-model="inputValue"
                @keyup.enter="confirmInput"
                @blur="confirmInput"
                style="width: 35px; text-align: right; font-size: 12px;"/>%
            </span>
          </span>
        </button>
        <!-- 下拉框 -->
        <div v-if="dropdownVisible" :style="dropdownStyle" class="zoom-dropdown" @mousedown.prevent>
          <div
            v-for="option in zoomOptions"
            :key="option"
            class="zoom-option"
            @click="selectZoom(option)">
            {{ option }}%
          </div>
        </div>
      <!-- 缩小 -->
      <button @click="zoomOut" :title="$t('toolbar.zoomOut')" :aria-label="$t('toolbar.zoomOut')">
        <Icon name="zoom-out" />
      </button>
      <!-- 元素撑满 -->
      <button @click="resetZoom" :disabled="!isSplitScreen ? isCodeDesign : currentFocusPane !== 'canvas'" :title="$t('toolbar.fitToScreen')" :aria-label="$t('toolbar.fitToScreen')">
        <Icon name="reset-zoom" />
      </button>
      <!-- 拖拽画布 -->
      <button @click="dragCanvas" :disabled="!isSplitScreen ? isCodeDesign : currentFocusPane !== 'canvas'" :title="$t('toolbar.dragCanvas')" :aria-label="$t('toolbar.dragCanvas')">
        <Icon :name="isDragMode ? 'drag-canvas' : 'selection-box'" />
      </button>
      <div class="toolbar-separator"></div>
      <!-- 格式化代码 -->
      <button @click="formatCode" :disabled="!isSplitScreen ? !isCodeDesign : currentFocusPane !== 'code'" :title="$t('toolbar.formatCode')" :aria-label="$t('toolbar.formatCode')">
        <Icon name="format-code" />
      </button>
      <div class="toolbar-separator"></div>
      <!-- 新增数据源 -->
      <button @click="addDatasource" :disabled="!isSplitScreen ? isCodeDesign : currentFocusPane !== 'canvas'" :title="$t('toolbar.addDatasource')" :aria-label="$t('toolbar.addDatasource')">
        <Icon name="database" />
      </button>
      <!-- 插入实体 -->
      <button @click="addEntity" :disabled="!isSplitScreen ? isCodeDesign : currentFocusPane !== 'canvas'" :title="$t('toolbar.addEntity')" :aria-label="$t('toolbar.addEntity')">
        <Icon name="add-entity" />
      </button>
      <!-- 复制实体 -->
      <button @click="copyEntity" :disabled="!isSplitScreen ? isCodeDesign || !isSelectedEntity : currentFocusPane !== 'canvas' || !isSelectedEntity" :title="$t('toolbar.copyEntity')" :aria-label="$t('toolbar.copyEntity')">
        <Icon name="copy" />
      </button>
      <!-- 粘贴实体 -->
      <button @click="pasteEntity" :disabled="!isSplitScreen ? isCodeDesign || !canPaste : currentFocusPane !== 'canvas' || !canPaste" :title="$t('toolbar.pasteEntity')" :aria-label="$t('toolbar.pasteEntity')">
        <Icon name="paste" />
      </button>
      <!-- 删除实体 -->
      <button @click="deleteEntity" :disabled="!isSplitScreen ? isCodeDesign || !isSelectedEntity : currentFocusPane !== 'canvas' || !isSelectedEntity" :title="$t('toolbar.deleteEntity')" :aria-label="$t('toolbar.deleteEntity')">
        <Icon name="delete" />
      </button>
      <div class="toolbar-separator"></div>
      <!-- 实体染色 -->
      <button @click="colorEntity" :disabled="!isSplitScreen ? isCodeDesign || !isSelectedEntity : currentFocusPane !== 'canvas' || !isSelectedEntity" :title="$t('toolbar.colorEntity')" :aria-label="$t('toolbar.colorEntity')">
        <Icon name="color-entity" />
      </button>
      <!-- 实体外框染色 -->
      <button @click="colorEntityBorder" :disabled="!isSplitScreen ? isCodeDesign || !isSelectedEntity : currentFocusPane !== 'canvas' || !isSelectedEntity" :title="$t('toolbar.colorEntityBorder')" :aria-label="$t('toolbar.colorEntityBorder')">
        <Icon name="color-entity-border" />
      </button>
      <!-- 字体颜色调整 -->
      <button @click="changeEntityFontColor" :disabled="!isSplitScreen ? isCodeDesign || !isSelectedEntity : currentFocusPane !== 'canvas' || !isSelectedEntity" :title="$t('toolbar.changeFontColor')" :aria-label="$t('toolbar.changeFontColor')">
        <Icon name="color-entity-font" />
      </button>
      <!-- 网格显示/隐藏 -->
      <button @click="toggleGrid" :disabled="!isSplitScreen ? isCodeDesign : currentFocusPane !== 'canvas'" :title="$t('toolbar.toggleGrid')" :aria-label="$t('toolbar.toggleGrid')">
        <Icon name="grid" />
      </button>
      <!-- 全屏 -->
      <button @click="toggleFullscreen" :title="$t('toolbar.fullscreen')" :aria-label="$t('toolbar.fullscreen')">
        <Icon name="fullscreen" />
      </button>
      <!-- 右侧按钮组 -->
      <div style="margin-left: auto; display: flex; gap: 6px; align-items: center; justify-content: flex-end;">
        <!-- 分屏-->
        <button @click="$emit('toggleSplitScreen')" :title="$t('toolbar.toggleSplitScreen')" :aria-label="$t('toolbar.toggleSplitScreen')">
          <Icon :name="isSplitScreen ? 'split-screen-visible' : 'split-screen'" width="20" height="20" />
        </button>
        <!-- 侧边栏显示/隐藏 -->
        <button @click="$emit('toggleSidebar')" :title="$t('toolbar.toggleSidebar')" :aria-label="$t('toolbar.toggleSidebar')">
          <Icon :name="sidebarVisible ? 'sidebar-visible' : 'sidebar'" width="20" height="20" />
        </button>
      </div>
    </div>
    <button class="scroll-btn right" @click="scrollRight" v-show="showRightScroll" :aria-label="$t('toolbar.scrollRight')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
        <polyline points="9 6 15 12 9 18"/>
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, onMounted, nextTick, onBeforeUnmount, watch, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import Icon from '@/components/Icon.vue'

const emit = defineEmits([
  'newDiagram',
  'saveDiagram',
  'exportDiagram',
  'addDatasource',
  'addEntity',
  'undo',
  'redo',
  'zoomIn',
  'zoomOut',
  'setZoom',
  'resetZoom',
  'toggleGrid',
  'toggleFullscreen',
  'copyEntity',
  'deleteEntity',
  'importDiagram',
  'colorEntityBorder',
  'changeEntityFontColor',
  'colorEntity',
  'colorRelation',
  'pasteEntity',
  'dragCanvas',
  'formatCode',
  'toggleSplitScreen',
  'toggleSidebar'
])
const props = defineProps({
  zoomLevel: Number,
  canPaste: Boolean,
  isSelectedEntity: Boolean,
  activeViewId: String,
  isSplitScreen: Boolean,
  sidebarVisible: Boolean,
  currentFocusPane: String
})
const { t: $t } = useI18n()
function saveDiagram() { emit('saveDiagram') }
function exportDiagram() { emit('exportDiagram') }
function undo() { emit('undo') }
function redo() { emit('redo') }
function zoomIn() { emit('zoomIn') }
function zoomOut() { emit('zoomOut') }
function resetZoom() { emit('resetZoom') }
function toggleGrid() { emit('toggleGrid') }
function toggleFullscreen() { emit('toggleFullscreen') }
function setZoom(level: number) { emit('setZoom', level) }
function addDatasource() { emit('addDatasource') }
function addEntity() { emit('addEntity') }
function colorEntity() { emit('colorEntity') }
function copyEntity() { emit('copyEntity') }
function pasteEntity() { emit('pasteEntity') }
function deleteEntity() { emit('deleteEntity') }
function importDiagram() { emit('importDiagram') }
function colorEntityBorder() { emit('colorEntityBorder') }
function changeEntityFontColor() { emit('changeEntityFontColor') }
function formatCode() { emit('formatCode') }

function dragCanvas() { 
  isDragMode.value = !isDragMode.value
  emit('dragCanvas', isDragMode.value)
}

const toolbarRef = ref<HTMLElement | null>(null)
const showLeftScroll = ref(false)
const showRightScroll = ref(false)
const dropdownVisible = ref(false)
const inputMode = ref(false)
const inputValue = ref('')
const zoomInputRef = ref<HTMLInputElement | null>(null)
const zoomOptions = [50, 75, 100, 150, 200]
const zoomButtonRef = ref<HTMLElement | null>(null)
const dropdownStyle = ref({})
const isDragMode = ref(false)

const isCodeDesign = computed(() => props.activeViewId === 'code')

/* 百分比设置下拉框 */
function toggleDropdown() {
  if (inputMode.value) return
  dropdownVisible.value = !dropdownVisible.value
  if(dropdownVisible.value) {
    nextTick(() => {
      if (zoomButtonRef.value) {
        const rect = zoomButtonRef.value.getBoundingClientRect()
        dropdownStyle.value = {
          left: rect.left - 17 + 'px',
          top: rect.top - 30 + 'px'
        }
      }
    })
  }
}

/* 百分比设置下拉框选择 */
function selectZoom(val: number) {
  setZoom(val / 100)
  dropdownVisible.value = false
}

/* 百分比设置下拉框输入 */
function enableInput() {
  inputMode.value = true
  inputValue.value = Math.round((props.zoomLevel ?? 1) * 100).toString()
  dropdownVisible.value = false
  nextTick(() => {
    zoomInputRef.value?.focus()
    zoomInputRef.value?.select()
  })
}

/* 百分比设置下拉框输入确认 */
function confirmInput() {
  let val = parseInt(inputValue.value, 10)
  if (isNaN(val) || val < 10) val = 10
  if (val > 500) val = 500
  setZoom(val / 100)
  inputMode.value = false
}

/* 监听滚轮事件（屏蔽浏览器默认滚动） */
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const container = toolbarRef.value;
  if (container) {
    container.scrollLeft += event.deltaX; // 横向滚动
  }
}

/* 滚动按钮 */
function updateScrollBtns() {
  const el = toolbarRef.value
  if (!el) return
  // 放宽误差，避免浮点导致按钮不出现
  showLeftScroll.value = el.scrollLeft > 0
  showRightScroll.value = el.scrollLeft + el.clientWidth < el.scrollWidth
}

/* 滚动按钮左移 */
function scrollLeft() {
  const el = toolbarRef.value
  if (el) {
    el.scrollBy({ left: -100, behavior: 'smooth' })
    setTimeout(updateScrollBtns, 300)
  }
}

/* 滚动按钮右移 */
function scrollRight() {
  const el = toolbarRef.value
  if (el) {
    el.scrollBy({ left: 100, behavior: 'smooth' })
    setTimeout(updateScrollBtns, 300)
  }
}

/* 点击工具栏外部关闭下拉框和输入框 */
function handleClickOutside(event: MouseEvent) {
  if (toolbarRef.value && !toolbarRef.value.contains(event.target as Node)) {
    dropdownVisible.value = false
    inputMode.value = false
  }
}

/* 挂载 */
onMounted(() => {
  nextTick(updateScrollBtns)
  toolbarRef.value?.addEventListener('scroll', updateScrollBtns)
  window.addEventListener('resize', updateScrollBtns)
  document.addEventListener('click', handleClickOutside)
})

/* 卸载 */
onBeforeUnmount(() => {
  toolbarRef.value?.removeEventListener('scroll', updateScrollBtns)
  window.removeEventListener('resize', updateScrollBtns)
  document.removeEventListener('click', handleClickOutside)
})

/* 监听工具栏是否超出边界 */
watch(toolbarRef, updateScrollBtns)
</script>

<style scoped>
/* 工具栏 */
.toolbar-wrapper {
  position: relative; /* 相对定位 */
  width: 100%; /* 宽度100% */
}
.toolbar {
  display: flex; /* 使用flex布局 */
  flex-wrap: nowrap; /* 不允许换行 */
  gap: 15px;  /* 增加按钮之间的间距 */
  overflow-x: auto; /* 横向滚动 */
  background: #f5f7fa;
  border-bottom: 0.5px solid #d9dce1; 
  justify-content: flex-start;
  align-items: center; 
  width: 100%; 
  box-sizing: border-box; 
  height: var(--toolbar-height); 
  scrollbar-width: none; /* Firefox隐藏原生滚动条 */
  -ms-overflow-style: none; /* IE和Edge隐藏原生滚动条 */
  padding-left: 16px;
  padding-right: 16px;
}
.toolbar::-webkit-scrollbar {
  display: none; /* Chrome和Safari隐藏原生滚动条 */
}
/* 工具栏分割线 */
.toolbar-separator {
  width: 1px;
  min-width: 1px;
  height: 16px;
  min-height: 16px;
  background: #d8d8d8;
  flex-shrink: 0;
}
/* 滚动按钮 */
.scroll-btn {
  position: absolute;
  top: 0;
  bottom: 0;
  height: 100%;
  width: var(--toolbar-icon-size);
  background: #dedede;
  border: none;
  z-index: 2;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  color: #409eff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: background 0.2s;
}
.scroll-btn.left { left: 0; }
.scroll-btn.right { right: 0; }

/* 工具栏按钮 */
.toolbar button {
  padding: 0;
  border: none;
  background: none;
  color: #212121;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.15s, color 0.15s;
}
.toolbar button, .zoom-percentage {
  width: var(--toolbar-icon-size); /* 增加按钮宽度 */
  height: var(--toolbar-icon-size); /* 增加按钮高度 */
  min-width: var(--toolbar-icon-size); /* 增加按钮宽度 */
  min-height: var(--toolbar-icon-size); /* 增加按钮高度 */
  font-size: 14px; /* 增加按钮字体大小 */   
  padding: 0;
}
.toolbar button:hover {
  transform: scale(1.12);
  color: #409eff;
  background: none;
}
.toolbar button:disabled svg {
  opacity: 0.4;         /* 变淡 */
  filter: grayscale(1); /* 变灰 */
  cursor: not-allowed;  /* 禁用手势 */
}
.toolbar button:disabled {
  background: none;  /* 可选：按钮背景变淡 */
  color: #aaa;          /* 可选：文字变灰 */
  border-color: none;   /* 可选：边框变淡 */
}
/* 百分比显示/设置 */
.zoom-percentage {
  display: flex;
  align-items: center;
  background: none;
  border: none;
  color: #222;
  cursor: pointer;
  margin-right: 15px;
}
/* 百分比显示/设置文本 */
.zoom-text {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #222;
}
.zoom-dropdown {
  position: absolute;
  background: #fff;
  border: 1px solid #e1e4e8;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  z-index: 100;
  min-width: 70px;
  margin-top: 2px;
}
.dark-theme .zoom-dropdown {
  color: #2f2f2f;
}
.zoom-option {
  padding: 6px 16px;
  cursor: pointer;
}
.zoom-option:hover {
  background: #f5f7fa;
}

/* 暗色主题 */
.dark-theme .toolbar {
  background: #030303;
  border-bottom: 0.5px solid #333333; 
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.2);
}
.dark-theme .toolbar-separator {
  background: #3d3d3d;
}
.dark-theme .toolbar button, .dark-theme .zoom-percentage, .dark-theme .zoom-text {
  color: #f4f3f3;
}
.dark-theme .toolbar button:hover{
  transform: scale(1.12);
  color: #409eff;
  background: none;
}

/* 768px以下移动端样式 */
@media (max-width: var(--mobile-breakpoint)) {
  .toolbar {
    flex-wrap: nowrap;     /* 移动端允许换行 */
    min-height: var(--toolbar-height);
    gap: 15px; /* 移动端工具栏按钮间距 */
  }
  .toolbar button, .zoom-percentage, .scroll-btn {
    font-size: 14px; /* 增加按钮字体大小 */   
    padding: 0;
  }
}
</style>