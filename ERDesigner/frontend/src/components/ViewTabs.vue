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
          @dblclick="$emit('editView', view)"
        >
          {{ view.name }}
        </div>
        <button class="add-view-btn" @click="$emit('addView')">+</button>
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
const emit = defineEmits(['update:activeViewId', 'addView', 'editView'])

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
  if (tabsContainer.value) {
    tabsContainer.value.scrollBy({ left: -100, behavior: 'smooth' })
    setTimeout(checkScroll, 300)
  }
}
function scrollRight() {
  if (tabsContainer.value) {
    tabsContainer.value.scrollBy({ left: 100, behavior: 'smooth' })
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
  padding: 4px 16px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-right: 4px;
  white-space: nowrap;
  flex-shrink: 0;
  font-size: 14px;
}
.view-tab.active {
  border-bottom: 2px solid #409eff;
  font-weight: bold;
  background: #fff;
}
.add-view-btn {
  margin-right: 6px;
  font-size: 18px;
  cursor: pointer;
  background: none;
  border: none;
  flex-shrink: 0;
  margin-right: 8px;
}
.add-view-btn:hover {
  transform: scale(1.2);
  color: #409eff;
  background: none;
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
</style>