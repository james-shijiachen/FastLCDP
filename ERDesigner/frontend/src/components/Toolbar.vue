<template>
  <div class="toolbar-wrapper" @wheel.prevent="handleModalWheel">
    <button class="scroll-btn left" @click="scrollLeft" v-show="showLeftScroll" :aria-label="$t('toolbar.scrollLeft')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
        <polyline points="15 18 9 12 15 6"/>
      </svg>
    </button>
    <div class="toolbar" ref="toolbarRef">
      <!-- 新建图表 -->
      <button @click="newDiagram" :title="$t('toolbar.newDiagram')" :aria-label="$t('toolbar.newDiagram')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M4 4h12l4 4v12a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V4z"/>
          <polyline points="16 4 16 8 20 8"/>
          <line x1="8" y1="13" x2="16" y2="13"/>
          <line x1="8" y1="17" x2="16" y2="17"/>
          <line x1="8" y1="9" x2="10" y2="9"/>
        </svg>
      </button>
      <!-- 保存 -->
      <button @click="saveDiagram" :title="$t('toolbar.saveDiagram')" :aria-label="$t('toolbar.saveDiagram')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
          <polyline points="17 21 17 13 7 13 7 21"/>
          <polyline points="7 3 7 8 15 8"/>
        </svg>
      </button>
      <!-- 导入-->
      <button @click="importDiagram" :title="$t('toolbar.importDiagram')" :aria-label="$t('toolbar.importDiagram')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 4v16M4 12h16"/>
        </svg>
      </button>
      <!-- 导出-->
      <button @click="exportDiagram" :title="$t('toolbar.exportDiagram')" :aria-label="$t('toolbar.exportDiagram')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="18" height="18" rx="2"/>
          <path d="M12 8v8"/>
          <path d="M8 16l4 4 4-4"/>
        </svg>
      </button>
      <!-- 撤销 -->
      <div class="toolbar-separator"></div>
      <button @click="undo" :title="$t('toolbar.undo')" :aria-label="$t('toolbar.undo')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M9 19l-7-7 7-7"/>
          <path d="M22 12H4"/>
        </svg>
      </button>
      <!-- 重做 -->
      <button @click="redo" :title="$t('toolbar.redo')" :aria-label="$t('toolbar.redo')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M15 5l7 7-7 7"/>
          <path d="M2 12h20"/>
        </svg>
      </button>
      <div class="toolbar-separator"></div>
      <!-- 放大 -->
      <button @click="zoomIn" :title="$t('toolbar.zoomIn')" :aria-label="$t('toolbar.zoomIn')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          <line x1="11" y1="8" x2="11" y2="14"/>
          <line x1="8" y1="11" x2="14" y2="11"/>
        </svg>
      </button>
      <!-- 百分比显示/设置 -->
      <button
          ref="zoomButtonRef"
          class="zoom-percentage"
          @click="toggleDropdown"
          @dblclick="enableInput"
          :title="$t('toolbar.setZoom')"
          :aria-label="$t('toolbar.setZoom')">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <text x="4" y="18" font-size="14">%</text>
          </svg>
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
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="11" cy="11" r="8"/>
          <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          <line x1="8" y1="11" x2="14" y2="11"/>
        </svg>
      </button>
      <!-- 元素撑满 -->
      <button @click="resetZoom" :title="$t('toolbar.fitToScreen')" :aria-label="$t('toolbar.fitToScreen')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="9 15 9 21 3 21"/>
          <polyline points="15 15 15 21 21 21"/>
          <polyline points="9 9 9 3 3 3"/>
          <polyline points="15 9 15 3 21 3"/>
        </svg>
      </button>
      <div class="toolbar-separator"></div>
      <!-- 插入实体 -->
      <button @click="addEntity" :title="$t('toolbar.addEntity')" :aria-label="$t('toolbar.addEntity')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="7" width="18" height="13" rx="2"/>
          <path d="M16 3v4M8 3v4M3 10h18"/>
        </svg>
      </button>
      <!-- 复制实体 -->
      <button @click="copyEntity" :disabled="!isSelectedEntity" :title="$t('toolbar.copyEntity')" :aria-label="$t('toolbar.copyEntity')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="9" y="9" width="13" height="13" rx="2" />
          <rect x="3" y="3" width="13" height="13" rx="2" />
        </svg>
      </button>
      <!-- 粘贴实体 -->
      <button @click="pasteEntity" :disabled="!canPaste" :title="$t('toolbar.pasteEntity')" :aria-label="$t('toolbar.pasteEntity')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="9" y="9" width="13" height="13" rx="2"/>
          <path d="M5 15V5a2 2 0 0 1 2-2h10"/>
          <path d="M15 2v4a1 1 0 0 1-1 1h-4a1 1 0 0 1-1-1V2"/>
        </svg>
      </button>
      <!-- 删除实体 -->
      <button @click="deleteEntity" :disabled="!isSelectedEntity" :title="$t('toolbar.deleteEntity')" :aria-label="$t('toolbar.deleteEntity')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="3 6 5 6 21 6"/>
          <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6"/>
          <path d="M10 11v6"/>
          <path d="M14 11v6"/>
          <path d="M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/>
        </svg>
      </button>
      <div class="toolbar-separator"></div>
      <!-- 实体染色 -->
      <button @click="colorEntity" :disabled="!isSelectedEntity" :title="$t('toolbar.colorEntity')" :aria-label="$t('toolbar.colorEntity')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M19.4 15a2 2 0 1 1-2.8 2.8"/>
          <path d="M4 19.5V17a2 2 0 0 1 2-2h1"/>
          <path d="M16 8l-8 8"/>
          <path d="M9 7l8 8"/>
          <path d="M7 9l-4 4a2 2 0 0 0 0 2.8l4 4a2 2 0 0 0 2.8 0l8-8a2 2 0 0 0 0-2.8l-4-4a2 2 0 0 0-2.8 0l-4 4"/>
        </svg>
      </button>
      <!-- 实体外框染色 -->
      <button @click="colorEntityBorder" :disabled="!isSelectedEntity" :title="$t('toolbar.colorEntityBorder')" :aria-label="$t('toolbar.colorEntityBorder')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 12m-1 0a1 1 0 1 0 2 0a1 1 0 1 0 -2 0"/>
          <path d="M12 12m-9 0a9 9 0 1 0 18 0a9 9 0 1 0 -18 0"/>
        </svg>
      </button>
      <!-- 连线染色 -->
      <button @click="colorRelation" :title="$t('toolbar.colorRelation')" :aria-label="$t('toolbar.colorRelation')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="4" y1="20" x2="20" y2="4"/>
          <circle cx="4" cy="20" r="2"/>
          <circle cx="20" cy="4" r="2"/>
        </svg>
      </button>
      <!-- 网格显示/隐藏 -->
      <button @click="toggleGrid" :title="$t('toolbar.toggleGrid')" :aria-label="$t('toolbar.toggleGrid')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="3" width="18" height="18" rx="2"/>
          <path d="M3 9h18M3 15h18M9 3v18M15 3v18"/>
        </svg>
      </button>
      <!-- 全屏 -->
      <button @click="toggleFullscreen" :title="$t('toolbar.fullscreen')" :aria-label="$t('toolbar.fullscreen')">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <polyline points="4 9 4 4 9 4"/>
          <polyline points="20 9 20 4 15 4"/>
          <polyline points="20 15 20 20 15 20"/>
          <polyline points="4 15 4 20 9 20"/>
        </svg>
      </button>
      <!-- 侧边栏显示/隐藏 -->
      <button @click="toggleSidebar" :title="$t('toolbar.toggleSidebar')" :aria-label="$t('toolbar.toggleSidebar')" style="margin-left: auto;">
        <svg v-if="sidebarVisible" width="32" height="24" viewBox="0 0 32 24" fill="none">
          <rect x="3" y="4" width="17" height="24" stroke="currentColor" stroke-width="2" fill="none"/>
          <rect x="22" y="4" width="7" height="24" stroke="currentColor" stroke-width="2" fill="currentColor"/>
        </svg>
        <svg v-else width="32" height="24" viewBox="0 0 32 24" fill="none">
          <rect x="3" y="4" width="17" height="24" stroke="currentColor" stroke-width="2" fill="none"/>
          <rect x="22" y="4" width="7" height="24" stroke="currentColor" stroke-width="2" fill="none"/>
        </svg>
      </button>
    </div>
    <button class="scroll-btn right" @click="scrollRight" v-show="showRightScroll" :aria-label="$t('toolbar.scrollRight')">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
        <polyline points="9 6 15 12 9 18"/>
      </svg>
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, onMounted, nextTick, onBeforeUnmount, watch } from 'vue'
import { useI18n } from 'vue-i18n'
const emit = defineEmits([
  'toggleSidebar',
  'newDiagram',
  'saveDiagram',
  'exportDiagram',
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
  'colorEntity',
  'colorRelation',
  'pasteEntity'
])
const props = defineProps({
  sidebarVisible: Boolean,
  zoomLevel: Number,
  canPaste: Boolean,
  isSelectedEntity: Boolean
})
const { t: $t } = useI18n()
function toggleSidebar() { emit('toggleSidebar') }
function newDiagram() { emit('newDiagram') }
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
function addEntity() { emit('addEntity') }
function colorEntity() { emit('colorEntity') }
function colorRelation() { emit('colorRelation') }
function copyEntity() { emit('copyEntity') }
function pasteEntity() { emit('pasteEntity') }
function deleteEntity() { emit('deleteEntity') }
function importDiagram() { emit('importDiagram') }
function colorEntityBorder() { emit('colorEntityBorder') }

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
  gap: 2px;
  background: none;
  border: none;
  color: #222;
  cursor: pointer;
  margin-right: 18px;
}
/* 百分比显示/设置文本 */
.zoom-text {
  min-width: 18px;
  text-align: right;
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