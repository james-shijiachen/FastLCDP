<template>
  <header class="top-toolbar" @wheel.prevent="handleModalWheel">
      <!-- 移动端菜单按钮 -->
      <button 
        class="top-toolbar-btn" 
        @click="toggleSidebar" 
        v-if="isMobile"
        :title="$t('toolbar.menu')">
        <span class="icon">☰</span>
      </button>
      <a href="https://www.fastlcdp.com" target="_blank" rel="noopener" class="logo-icon">
        <img src="@/assets/logo.png" alt="logo"/>
      </a>
      <div class="header-right">
        <div class="language-switcher" ref="langSwitcher">
          <button class="flag-btn" @click="showDropdown = !showDropdown">
            <span class="flag">{{ currentFlag }}</span>
          </button>
          <div v-if="showDropdown" class="lang-dropdown">
            <div class="lang-option" @click="selectLanguage('en')">
              English
            </div>
            <div class="lang-option" @click="selectLanguage('zh')">
              中文
            </div>
          </div>
        </div>
        <a href="https://github.com/james-shijiachen/FastLCDP" target="_blank" class="github-link" title="GitHub">
          <Icon name="github" style="width: 30px; height: 30px;" />
        </a>
        <div class="top-toolbar-group">
          <input
            type="checkbox"
            class="theme-btn"
            id="checkbox"
            :checked="isDarkTheme"
            @change="toggleTheme"
          >
          <label class="switch" for="checkbox">
            <span class="slider"></span>
          </label>
        </div>
      </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import Icon from '@/components/Icon.vue'

const emit = defineEmits([
  'toggleSidebar',
  'changeLanguage',
  'toggleTheme',
])
const props = defineProps<{
  isMobile: boolean
  isDarkTheme: boolean
  currentLocale: string
}>()
const currentLocale = ref(props.currentLocale)
const { t: $t, locale } = useI18n()
const showDropdown = ref(false)
const langSwitcher = ref<HTMLElement | null>(null)
const flagMap: Record<string, string> = { en: '🇺🇸', zh: '🇨🇳' }
const currentFlag = computed(() => flagMap[currentLocale.value] || '🇺🇸')

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
}

/* 切换侧边栏显示 */
function toggleSidebar() { emit('toggleSidebar') }
/* 切换语言 */
function changeLanguage() {
  locale.value = currentLocale.value
  emit('changeLanguage', currentLocale.value)
}
/* 切换主题 */
function toggleTheme() { emit('toggleTheme') }
/* 选择语言 */
function selectLanguage(lang: string) {
  currentLocale.value = lang
  changeLanguage()
  showDropdown.value = false
}
/* 点击工具栏外部关闭下拉框和输入框 */
function handleClickOutside(event: MouseEvent) {
  if (langSwitcher.value && !langSwitcher.value.contains(event.target as Node)) {
    showDropdown.value = false
  }
}
// 自动根据本地时间切换主题（仅首次加载）
onMounted(() => {
  const hour = new Date().getHours()
  // 7:00-18:50为白天，其余为夜晚
  const isNight = hour < 7 || hour > 18 || (hour === 18 && new Date().getMinutes() > 50)
  if (isNight !== props.isDarkTheme) {
    emit('toggleTheme')
  }
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 顶部工具栏 */
.top-toolbar {
  width: 100%;
  height: var(--topbar-height);
  margin: 0;
  padding: 0 12px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  /*background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); */
  background: #f5f7fa;
  border-bottom: 0.5px solid #d9dce1; 
  color: #fff;
}
.dark-theme .top-toolbar {
  background: #030303;
  border-bottom: 0.5px solid #333333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
}
.header-right {
  display: flex;
  align-items: center;
  margin-left: auto;
  gap: 12px;
}

/* 顶部logo */
.logo-icon img {
  background:fixed; 
  border: none;
  box-shadow: none;
  display: block;
  width: 160px;
  height: 28px;
  margin-left: 10px;
  margin-right: 10px;
}

/* 移动端菜单按钮 */
.top-toolbar-btn {
  margin: 0;
  padding: 2px 4px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 20px;
  color: #fff;
  transition: color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 语言切换 */
.language-switcher {
  position: relative;
}
.lang-dropdown {
  position: absolute;
  top: 110%;
  left: 0;
  background: #fff;
  color: #333;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  z-index: 1200;
  min-width: 70px;
  padding: 4px 0;
}
.lang-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  cursor: pointer;
  font-size: 15px;
  padding-left: 12px;
  padding-right: 12px;
}
.lang-option:hover {
  background: #f0f0f0;
}
.flag {
  font-size: 30px;
}
.flag-btn {
  background: none;
  border: none;
  font-size: 22px;
  cursor: pointer;
  padding: 4px 8px;
}

/* 主题切换按钮 */
.top-toolbar-group {
  width: 45px;
  height: 25px;
  position: relative;
  margin-left: 5px;
}
.theme-btn {
  opacity: 0;
  width: 0;
  height: 0;
  position: absolute;
}
.theme-btn:checked + .switch {
  background-color: #34C759;
}
.theme-btn:checked + .switch .slider {
  left: calc(50% - 27px/2 + 10px);
  top: calc(50% - 27px/2);
}
.switch {
  width: 100%;
  height: 100%;
  display: block;
  background-color: #e9e9eb;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease-out;
}
.slider {
  width: 25px;
  height: 25px;
  position: absolute;
  left: calc(50% - 25px/2 - 10px);
  top: calc(50% - 25px/2);
  border-radius: 50%;
  background: #FFFFFF;
  box-shadow: 0px 3px 8px rgba(0, 0, 0, 0.15), 0px 3px 1px rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease-out;
  cursor: pointer;
}

/* github链接 */
.github-link {
  display: flex;
  align-items: center;
  color: #ffffff;
  transition: opacity 0.2s;
}
.github-link:hover {
  opacity: 0.7;
}

/* 响应式样式 */
@media (max-width: var(--mobile-breakpoint)) {
  .top-toolbar {
    padding: 8px 12px;
    min-height: var(--topbar-height);
    flex-wrap: nowrap;
    gap: 8px;
  }
  .logo-icon img {
    min-width: 160px;
    min-height: 28px;
  }
  .language-switcher {
    margin-left: 8px;
    margin-right: 8px;
  }
  .language-switcher select {
    padding: 4px 8px;
    font-size: 12px;
  }
}
</style>