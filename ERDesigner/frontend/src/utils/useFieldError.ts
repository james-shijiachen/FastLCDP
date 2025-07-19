import { ref, onMounted, onUnmounted } from 'vue'

export interface FieldError {
  field: string
  message: string
  component?: string
}

export function useFieldError(componentName: string) {
  const fieldErrors = ref<Record<string, string>>({})

  // 监听字段错误事件
  const handleFieldError = (event: CustomEvent) => {
    const { field, message, component } = event.detail as FieldError
    
    // 只处理当前组件的错误
    if (component === componentName) {
      fieldErrors.value[field] = message
    }
  }

  // 清除字段错误
  const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
      delete fieldErrors.value[field]
    }
  }

  // 清除所有字段错误
  const clearAllFieldErrors = () => {
    fieldErrors.value = {}
  }

  // 设置字段错误
  const setFieldError = (field: string, message: string) => {
    fieldErrors.value[field] = message
  }

  // 获取字段错误
  const getFieldError = (field: string): string => {
    return fieldErrors.value[field] || ''
  }

  // 检查字段是否有错误
  const hasFieldError = (field: string): boolean => {
    return !!fieldErrors.value[field]
  }

  // 检查是否有任何错误
  const hasAnyError = (): boolean => {
    return Object.keys(fieldErrors.value).length > 0
  }

  onMounted(() => {
    window.addEventListener('field-error', handleFieldError as EventListener)
  })

  onUnmounted(() => {
    window.removeEventListener('field-error', handleFieldError as EventListener)
  })

  return {
    fieldErrors,
    clearFieldError,
    clearAllFieldErrors,
    setFieldError,
    getFieldError,
    hasFieldError,
    hasAnyError
  }
} 