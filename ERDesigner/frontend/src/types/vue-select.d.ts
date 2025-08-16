declare module 'vue-select' {
  import { DefineComponent } from 'vue'
  
  interface VueSelectProps {
    modelValue?: any
    options?: any[]
    reduce?: (option: any) => any
    label?: string
    value?: string
    placeholder?: string
    searchable?: boolean
    clearable?: boolean
    multiple?: boolean
    disabled?: boolean
    loading?: boolean
    filterable?: boolean
    resetOnOptionsChange?: boolean
    closeOnSelect?: boolean
    taggable?: boolean
    pushTags?: boolean
    createOption?: (option: any) => any
  }

  const VueSelect: DefineComponent<VueSelectProps>
  export default VueSelect
}