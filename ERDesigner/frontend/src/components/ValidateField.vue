<template>
  <div class="form-group validate-field">
    <div class="field-wrapper">
      <input
        v-if="type === 'text'"
        ref="inputRef"
        :id="fieldId"
        :value="modelValue"
        :type="inputType"
        :placeholder="placeholder"
        :class="{ 'error': hasError }"
        @keyup.enter="handleEnter"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"/>
      <textarea
        v-else-if="type === 'textarea'"
        ref="inputRef"
        :id="fieldId"
        :value="modelValue"
        :placeholder="placeholder"
        :rows="rows"
        :class="{ 'error': hasError }"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"/>
    </div>
    <span v-if="errorMessage" class="error-message">{{ errorMessage }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useFieldError } from '@/utils/useFieldError'

interface Props {
  modelValue: string
  field: string
  component: string
  type?: 'text' | 'textarea'
  inputType?: string
  label?: string
  placeholder?: string
  required?: boolean
  rows?: number
}

const props = withDefaults(defineProps<Props>(), {
  type: 'text',
  inputType: 'text',
  required: false,
  rows: 3,
  options: () => []
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  'input': [value: string]
  'change': [value: string]
  'blur': [event: FocusEvent]
  'focus': [event: FocusEvent]
  'enter': [event: KeyboardEvent]
}>()

// 输入框引用
const inputRef = ref<HTMLInputElement | HTMLTextAreaElement | null>(null)

// 使用字段错误处理
const { getFieldError, hasFieldError, clearFieldError } = useFieldError(props.component)

// 计算属性
const fieldId = computed(() => `${props.field}`)
const errorMessage = computed(() => getFieldError(props.field))
const hasError = computed(() => hasFieldError(props.field))

// 事件处理
const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement | HTMLTextAreaElement
  emit('update:modelValue', target.value)
  emit('input', target.value)
  clearFieldError(props.field)
}

const handleBlur = (event: FocusEvent) => {
  emit('blur', event)
}

const handleFocus = (event: FocusEvent) => {
  emit('focus', event)
}

const handleEnter = (event: KeyboardEvent) => {
  emit('enter', event)
}

// 暴露focus方法和getBoundingClientRect方法给父组件
const focus = () => {
  if (inputRef.value) {
    inputRef.value.focus()
  }
}

const getBoundingClientRect = () => {
  if (inputRef.value) {
    return inputRef.value.getBoundingClientRect()
  }
  return null
}

defineExpose({
  focus,
  getBoundingClientRect
})
</script>

<style scoped>
.validate-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 18px;
}

.field-wrapper {
  position: relative;
}

.field-wrapper input,
.field-wrapper textarea,
.field-wrapper select {
  width: 100%;
  min-width: 0;
  box-sizing: border-box;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  transition: all 0.3s !important;
}

.field-wrapper input {
  font-size: 14px !important;
}

.field-wrapper textarea {
  font-size: 13px !important;
}

.field-wrapper input:focus,
.field-wrapper textarea:focus,
.field-wrapper select:focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
  outline: none;
}

.field-wrapper input.error,
.field-wrapper textarea.error,
.field-wrapper select.error {
  border-color: #ff4d4f;
  box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.2);
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  line-height: 1.4;
  margin-top: 2px;
}
</style>