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
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 2C6.48 2 2 6.58 2 12.26c0 4.5 2.87 8.32 6.84 9.67.5.09.68-.22.68-.48 0-.24-.01-.87-.01-1.7-2.78.62-3.37-1.36-3.37-1.36-.45-1.18-1.1-1.5-1.1-1.5-.9-.63.07-.62.07-.62 1 .07 1.53 1.05 1.53 1.05.89 1.56 2.34 1.11 2.91.85.09-.66.35-1.11.63-1.37-2.22-.26-4.56-1.14-4.56-5.07 0-1.12.39-2.03 1.03-2.75-.1-.26-.45-1.3.1-2.7 0 0 .84-.28 2.75 1.05A9.38 9.38 0 0 1 12 6.84c.85.004 1.71.12 2.51.35 1.91-1.33 2.75-1.05 2.75-1.05.55 1.4.2 2.44.1 2.7.64.72 1.03 1.63 1.03 2.75 0 3.94-2.34 4.81-4.57 5.07.36.32.68.94.68 1.9 0 1.37-.01 2.47-.01 2.8 0 .26.18.58.69.48A10.01 10.01 0 0 0 22 12.26C22 6.58 17.52 2 12 2Z" fill="#fff"/>
          </svg>
        </a>
        <div class="top-toolbar-group">
          <input
            type="checkbox"
            class="checkbox"
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

function toggleSidebar() { emit('toggleSidebar') }
function changeLanguage() {
  locale.value = currentLocale.value
  emit('changeLanguage', currentLocale.value)
}
function toggleTheme() { emit('toggleTheme') }

function selectLanguage(lang: string) {
  currentLocale.value = lang
  changeLanguage()
  showDropdown.value = false
}

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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
.dark-theme .top-toolbar {
  background: #1e1e1e;
  border-bottom: 1px solid #333333;
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
.checkbox {
  opacity: 0;
  width: 0;
  height: 0;
  position: absolute;
}
.checkbox:checked + .switch {
  background-color: #34C759;
}
.checkbox:checked + .switch .slider {
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