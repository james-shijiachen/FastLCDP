import { ref, onMounted, onUnmounted } from 'vue'

export interface FieldError {
  field: string
  message: string
  component?: string
}

export function useFieldError(componentName: string) {
  const fieldErrors = ref<Record<string, string>>({})

  // Listen to field error events
  const handleFieldError = (event: CustomEvent) => {
    const { field, message, component } = event.detail as FieldError
    
    // Only handle errors for current component
    if (component === componentName) {
      fieldErrors.value[field] = message
    }
  }

  // Clear field error
  const clearFieldError = (field: string) => {
    if (fieldErrors.value[field]) {
      delete fieldErrors.value[field]
    }
  }

  // Clear all field errors
  const clearAllFieldErrors = () => {
    fieldErrors.value = {}
  }

  // Set field error
  const setFieldError = (field: string, message: string) => {
    fieldErrors.value[field] = message
  }

  // Get field error
  const getFieldError = (field: string): string => {
    return fieldErrors.value[field] || ''
  }

  // Check if field has error
  const hasFieldError = (field: string): boolean => {
    return !!fieldErrors.value[field]
  }

  // Check if there are any errors
  const hasAnyError = (): boolean => {
    return Object.keys(fieldErrors.value).length > 0
  }

  // Listen to field error events on mount
  onMounted(() => {
    document.addEventListener('fieldError', handleFieldError as EventListener)
  })

  // Remove event listener on unmount
  onUnmounted(() => {
    document.removeEventListener('fieldError', handleFieldError as EventListener)
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