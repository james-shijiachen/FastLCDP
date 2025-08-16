<template>
  <div class="color-palette" v-if="visible" @click.stop>
    <div class="palette-content">
      <!-- 预设颜色 -->
      <div class="color-section">
        <h4>{{ $t('colorPalette.presetColors') }}</h4>
        <div class="color-grid">
          <div 
            v-for="color in presetColors" 
            :key="color"
            class="color-item"
            :class="{ active: selectedColor === color }"
            :style="{ backgroundColor: color }"
            @click="selectColor(color)"
            :title="color">
            <svg v-if="selectedColor === color" width="12" height="12" viewBox="0 0 16 16" fill="white">
              <path d="M13.854 3.646a.5.5 0 0 1 0 .708l-7 7a.5.5 0 0 1-.708 0l-3.5-3.5a.5.5 0 1 1 .708-.708L6.5 10.293l6.646-6.647a.5.5 0 0 1 .708 0z"/>
            </svg>
          </div>
        </div>
      </div>
      
      <!-- 自定义颜色 -->
      <div class="color-section">
        <h4>{{ $t('colorPalette.customColor') }}</h4>
        <div class="custom-color-input">
          <input 
            type="color" 
            v-model="customColor"
            @input="selectColor(customColor)"
            class="color-picker">
          <input 
            type="text" 
            v-model="customColor"
            @input="selectColor(customColor)"
            @keyup.enter="selectColor(customColor)">
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="palette-actions">
        <button @click="resetColor" class="btn btn-secondary">
          {{ $t('colorPalette.reset') }}
        </button>
        <button @click="applyColor" class="btn btn-primary" :disabled="!selectedColor">
          {{ $t('colorPalette.apply') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

interface Props {
  visible: boolean
  currentColor?: string
}

interface Emits {
  (e: 'close'): void
  (e: 'colorSelected', color: string): void
  (e: 'reset'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 预设颜色
const presetColors = [
  '#ffffff', '#f8f9fa', '#e9ecef', '#dee2e6', '#ced4da', '#adb5bd',
  '#6c757d', '#495057', '#343a40', '#212529', '#000000',
  '#fff3cd', '#ffeaa7', '#fdcb6e', '#e17055', '#d63031',
  '#fd79a8', '#e84393', '#a29bfe', '#6c5ce7', '#74b9ff',
  '#0984e3', '#00b894', '#00cec9', '#55a3ff', '#81ecec',
  '#a7f3d0', '#6ee7b7', '#34d399', '#10b981', '#059669',
  '#047857', '#065f46', '#064e3b', '#fef3c7', '#fde68a',
  '#fcd34d', '#f59e0b', '#d97706', '#b45309', '#92400e',
  '#78350f', '#451a03'
]

const selectedColor = ref<string>(props.currentColor || '')
const customColor = ref<string>(props.currentColor || '')

// 监听当前颜色变化
watch(() => props.currentColor, (newColor) => {
  console.log('currentColor', props.currentColor, newColor, selectedColor.value, customColor.value)
  if (newColor) {
    selectedColor.value = newColor
    customColor.value = newColor
  }else{
    selectedColor.value = ''
    customColor.value = ''
  }
})

// 选择颜色
function selectColor(color: string) {
  selectedColor.value = color
  customColor.value = color
}

// 应用颜色
function applyColor() {
  if (customColor.value) {
    emit('colorSelected', customColor.value)
  }
}

// 重置颜色
function resetColor() {
  selectedColor.value = ''
  customColor.value = '#ffffff'
  emit('reset')
}
</script>

<style scoped>
/* 调色板容器样式 */
.color-palette {
  position: absolute;
  z-index: 1001;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 16px;
  min-width: 280px;
  max-width: 320px;
  margin-top: 4px;
}

.dark-theme .color-palette {
  color: #ffffff;
  background: #2a2a2a;
  border: 1px solid #404040;
}

.palette-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
  font-size: 14px;
}

.color-section {
  margin-bottom: 16px;
}

.color-section h4 {
  margin: 0 0 8px 0;
  font-size: 12px;
  font-weight: 500;
  color: #6b7280;
}

.dark-theme .color-section h4 {
  color: #a0aec0;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(11, 1fr);
  gap: 4px;
}

.color-item {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #e1e5e9;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  position: relative;
}

.color-item:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.color-item.active {
  border-color: #0366d6;
  border-width: 3px;
}
.dark-theme .color-item.active {
  border-color: #bb86fc;
}

.custom-color-input {
  display: flex;
  gap: 8px;
  align-items: center;
}

.color-picker {
  width: 40px;
  height: 32px;
  border-radius: 4px;
  cursor: pointer;
  background: none;
}

.palette-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>