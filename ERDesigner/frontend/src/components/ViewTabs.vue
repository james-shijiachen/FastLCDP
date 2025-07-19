<template>
    <div class="view-tabs-wrapper" @wheel.prevent="handleModalWheel">
      <button class="scroll-btn left" @click="scrollLeft" v-show="showLeftScroll">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
          <polyline points="15 18 9 12 15 6"/>
        </svg>
      </button>
      <div class="view-tabs" ref="tabsContainer">
        <div
          v-for="view in views"
          :key="view.id"
          :class="['view-tab', { active: view.id === activeViewId }]"
          @click="$emit('update:activeViewId', view.id)"
        >
          {{ view.name }}
          <!-- 删除视图按钮(非默认视图) -->
          <button
            v-if="view.id !== 'default'"
            class="delete-btn"
            @click.stop="$emit('deleteView', view.id)"
            title="删除视图">×</button>
        </div>
      </div>
      <button class="scroll-btn right" @click="scrollRight" v-show="showRightScroll">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="square" stroke-linejoin="miter">
          <polyline points="9 6 15 12 9 18"/>
        </svg>
      </button>
    </div>
  </template>
<script setup lang="ts">
import { ref, onMounted, watch, nextTick, onBeforeUnmount } from 'vue'
import type { View } from '../types/entity'

const props = defineProps<{
  views: View[]
  activeViewId: string
}>()
const emit = defineEmits(['update:activeViewId', 'deleteView'])

const tabsContainer = ref<HTMLDivElement | null>(null)
const showLeftScroll = ref(false)
const showRightScroll = ref(false)

/* 监听滚轮事件（屏蔽浏览器默认滚动） */
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const container = tabsContainer.value;
  if (container) {
    container.scrollLeft += event.deltaX; // 横向滚动
  }
}
function checkScroll() {
  const el = tabsContainer.value
  if (el) {
    showLeftScroll.value = el.scrollLeft > 0
    showRightScroll.value = el.scrollLeft + el.clientWidth < el.scrollWidth
  }
}
function scrollLeft() {
  const el = tabsContainer.value
  if (el) {
    el.scrollBy({ left: -100, behavior: 'smooth' })
    setTimeout(checkScroll, 300)
  }
}
function scrollRight() {
  const el = tabsContainer.value
  if (el) {
    el.scrollBy({ left: 100, behavior: 'smooth' })
    setTimeout(checkScroll, 300)
  }
}

/* 挂载 */
onMounted(() => {
  nextTick(checkScroll)
  tabsContainer.value?.addEventListener('scroll', checkScroll)
  window.addEventListener('resize', checkScroll)
})

/* 卸载 */
onBeforeUnmount(() => {
  tabsContainer.value?.removeEventListener('scroll', checkScroll)
  window.removeEventListener('resize', checkScroll)
})

/* 监听视图标签容器是否超出边界 */
watch(tabsContainer, checkScroll)
</script>
  
<style scoped>
.view-tabs-wrapper {
  display: flex;
  align-items: center;
  background: #f6f8fa;
  border-bottom: 1px solid #e1e4e8;
  position: relative;
  height: 28px;
  overflow: hidden;
}
.view-tabs-wrapper, .view-tabs-wrapper * {
  user-select: none;
  -webkit-user-select: none; /* Safari */
  -ms-user-select: none;     /* IE10+/Edge */
}
.view-tabs {
  display: flex;
  align-items: center;
  overflow-x: auto;
  flex: 1;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE 10+ */
}
.view-tabs::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}
.view-tab {
  padding: 4px 24px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-right: 4px;
  white-space: nowrap;
  flex-shrink: 0;
  font-size: 14px;
  border-radius: 4px 4px 0 0;
}
.view-tab.active {
  border-bottom: 2px solid #409eff;
  font-weight: bold;
  background: #fff;
}
.scroll-btn {
  width: var(--toolbar-icon-size);
  height: 100%;
  background: #dedede;
  border: none;
  cursor: pointer;
  color: #409eff;
  z-index: 1;
}
.scroll-btn.left {
  border-right: 1px solid #e1e4e8;
}
.scroll-btn.right {
  border-left: 1px solid #e1e4e8;
}

/* 暗色主题 */
.dark-theme .view-tabs-wrapper {
  background: #1e1e1e;
  border-right: 1px solid #333333;
  border-bottom: 0.5px solid #333333; 
  box-shadow: 2px 0 4px rgba(0, 0, 0, 0.2);
}
.dark-theme .view-tab.active {
  background: #404040;
}
.dark-theme .view-tab.active {
  border-bottom: 2px solid #bb86fc;
}
</style>