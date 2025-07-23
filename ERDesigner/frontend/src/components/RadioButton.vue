<template>
  <div class="radio-inputs" ref="radioInputs">
    <template v-for="(option, index) in options" :key="option.value">
      <label class="radio" :ref="el => { if (option.value === modelValue) checkedRadio = el as HTMLElement }">
        <input
        :name="field"
        :value="option.value"
        type="radio"
        :checked="option.value === modelValue"
        @change="emit('update:modelValue', option.value)"
        />
        <span class="radio-item">
          <component v-if="option.icon && typeof option.icon !== 'string'" :is="option.icon" class="radio-icon" />
          <img v-else-if="option.icon" :src="option.icon" class="radio-icon" />
          {{ option.label }}
        </span>
      </label>
      <span v-if="index < options.length - 1" class="divider">|</span>
    </template>
    <span v-if="options.length === 0" class="no-data">{{ props.label }}</span>
    <span v-if="errorMessage" class="error-message">{{ errorMessage }}</span>
  </div>
</template>

<script setup lang="ts">
import { watchEffect, computed, onMounted, onBeforeUnmount, ref, nextTick, watch, type Component } from 'vue'
import { useFieldError } from '@/utils/useFieldError'

interface Option {
  value: string
  label: string
  icon?: Component | string // 可以是图片路径或SVG组件
}

interface Props {
  field: string
  modelValue: string
  component: string
  label: string
  options?: Option[]
}

const props = withDefaults(defineProps<Props>(), {
  field: '',
  component: '',
  options: () => []
})

const emit = defineEmits(['update:modelValue'])

// 使用字段错误处理
const { getFieldError} = useFieldError(props.component)

const radioInputs = ref<HTMLElement | null>(null)
const checkedRadio = ref<HTMLElement | null>(null)
const errorMessage = computed(() => getFieldError(props.field))

// 滚动到选中项
const scrollToChecked = () => {
  nextTick(() => {
    if (checkedRadio.value && radioInputs.value) {
      checkedRadio.value.scrollIntoView({
        behavior: 'auto', // 或 'smooth'
        block: 'nearest',
        inline: 'center'
      })
    }
  })
}

// 如果 modelValue 变化，也自动滚动
watch(() => props.modelValue, scrollToChecked)

// 自动选中第一个
watchEffect(() => {
  if ((props.modelValue === undefined || props.modelValue === null) && props.options.length > 0) {
    emit('update:modelValue', props.options[0].value)
  }
})

onMounted(() => {
  scrollToChecked()
  const el = radioInputs.value
  if (!el) return
  const onWheel = (e: WheelEvent) => {
    // 优先用 deltaX（横向滑动），否则用 deltaY（纵向滚轮）
    if (Math.abs(e.deltaX) > Math.abs(e.deltaY)) {
      if (e.deltaX !== 0) {
        el.scrollLeft += e.deltaX
        e.preventDefault()
      }
    } else if (e.deltaY !== 0) {
      el.scrollLeft += e.deltaY
      e.preventDefault()
    }
  }
  el.addEventListener('wheel', onWheel, { passive: false })
  onBeforeUnmount(() => {
    el.removeEventListener('wheel', onWheel)
  })
})
</script>

<style scoped>
  .radio-inputs {
    position: relative !important;
    display: flex !important;
    border-radius: 4px !important;
    background-color: #ffffff !important;
    box-sizing: border-box !important;
    padding: 0.25rem !important;
    width: 100% !important;
    min-width: 0;
    height: 40px;
    font-size: 14px !important;
    transition: clip-path 0.3s ease !important;
    border: 1px solid #d9d9d9 !important;
    margin-bottom: 18px;
    align-items: center;
    justify-content: flex-start;
    overflow-x: auto;
    scrollbar-width: none; /* Firefox隐藏原生滚动条 */
    -ms-overflow-style: none; /* IE和Edge隐藏原生滚动条 */
  }

  
  .radio-inputs::-webkit-scrollbar {
    display: none; /* Chrome和Safari隐藏原生滚动条 */
  }

  .dark-theme .radio-inputs {
    background-color: black !important;
    border: 1px solid #555555 !important;
  }

  .divider {
    flex: 0 0 auto;
    align-self: center;
    color: #ccc;
    margin: 0 8px;
  }

  .radio-inputs .radio {
    flex: 1 0 auto; /* 防止label被压缩 */
    min-width: 100px;     /* 可选：每个选项最小宽度 */
    text-align: center !important;
    white-space: nowrap !important;
  }

  .radio-inputs .radio input {
    display: none !important;
  }

  .radio-inputs .radio .radio-item {
    display: flex !important;
    cursor: pointer !important;
    align-items: center !important;
    justify-content: center !important;
    border-radius: 4px !important;
    border: none !important;
    padding: 0.5rem 0.5rem !important;
    margin: 0 0.5rem !important;
    color: #000000 !important;
    transition: 
      background-color 0.5s ease, 
      font-weight 0.5s ease ;
  }

  .dark-theme .radio-inputs .radio-item {
    color: #f7f6f5 !important;
  }

  .radio-inputs input:checked ~ .radio-item {
    background-color: #4d4a45 !important;
    font-weight: 600 !important;
    color: #fff !important;
  }
  
  .dark-theme .radio-inputs input:checked ~ .radio-item {
    background-color: #341757 !important;
  }

  .radio-inputs input:checked ~ .radio {
    clip-path: polygon(0% 0%, 100% 0%, 90% 100%, 10% 100%) !important;
  }

  .no-data {
    width: 100%;
    text-align: center;
    color: #ccc;
    font-size: 14px;
    line-height: 1.4;
    margin-top: 2px;
  }
  .dark-theme .no-data {
    color: #494949;
  }

  .radio-icon {
    width: 18px;
    height: 18px;
    vertical-align: middle;
    margin-right: 4px;
    color: #666; /* 默认颜色 */
  }

  /* SVG 组件颜色控制 */
  .radio-item svg {
    color: inherit;
  }

  .radio-inputs input:checked ~ .radio-item .radio-icon {
    color: #fff; /* 选中时颜色 */
  }

  .dark-theme .radio-icon {
    color: #c2c1be; /* 深色主题颜色 */
  }

  .error-message {
    color: #ff4d4f;
    font-size: 12px;
    line-height: 1.4;
    margin-top: 2px;
    text-align: center;
  }
</style>
