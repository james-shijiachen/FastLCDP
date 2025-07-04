<template>
  <div class="modal-overlay" @click="handleOverlayClick" data-uid="DatabaseEditModal-overlay">
    <div class="db-modal-content" @click.stop data-uid="DatabaseEditModal-content">
      <div class="db-modal-header" data-uid="DatabaseEditModal-header">
        <h3 class="db-modal-title" data-uid="DatabaseEditModal-title">{{ isEdit ? $t('database.editDatabase') : $t('database.newDatabase') }}</h3>
        <button class="db-modal-close-btn" @click="$emit('close')" data-uid="DatabaseEditModal-btn-close">×</button>
      </div>
      
      <div class="db-modal-body" data-uid="DatabaseEditModal-body">
        <div class="db-modal-form-group form-group" data-uid="DatabaseEditModal-form-group-name">
          <label for="database-name">{{ $t('database.name') }} *</label>
          <input 
            id="database-name"
            v-model="formData.name" 
            type="text" 
            :placeholder="$t('database.namePlaceholder')"
            :class="{ 'error': errors.name }"
            data-uid="DatabaseEditModal-input-name"
          />
          <span v-if="errors.name" class="error-message" data-uid="DatabaseEditModal-error-name">{{ errors.name }}</span>
        </div>
        
        <div class="db-modal-form-group form-group" data-uid="DatabaseEditModal-form-group-desc">
          <label for="database-description">{{ $t('database.description') }}</label>
          <textarea 
            id="database-description"
            v-model="formData.description" 
            :placeholder="$t('database.descriptionPlaceholder')"
            rows="3"
            data-uid="DatabaseEditModal-input-desc"
          ></textarea>
        </div>
      </div>
      
      <div class="db-modal-footer" data-uid="DatabaseEditModal-footer">
        <button class="btn btn-secondary" @click="$emit('close')" data-uid="DatabaseEditModal-btn-cancel">{{ $t('common.cancel') }}</button>
        <button class="btn btn-primary" @click="handleSave" :disabled="!isValid" data-uid="DatabaseEditModal-btn-save">
          {{ isEdit ? $t('common.save') : $t('common.create') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { Database } from '../types/entity'

const { t } = useI18n()

interface Props {
  database?: Database | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [database: Database]
  close: []
}>()

// 表单数据
const formData = ref({
  name: '',
  description: ''
})

// 错误信息
const errors = ref({
  name: ''
})

// 是否为编辑模式
const isEdit = computed(() => !!props.database)

// 表单验证
const isValid = computed(() => {
  return formData.value.name.trim() !== '' && !errors.value.name
})

// 验证数据库名称
function validateName() {
  if (!formData.value.name.trim()) {
    errors.value.name = t('database.nameRequired')
  } else if (formData.value.name.length > 50) {
    errors.value.name = t('database.nameMaxLength')
  } else if (!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(formData.value.name)) {
    errors.value.name = t('database.namePattern')
  } else {
    errors.value.name = ''
  }
}

// 处理保存
function handleSave() {
  validateName()
  
  if (!isValid.value) {
    return
  }
  
  const database: Database = {
    id: props.database?.id || Date.now().toString(),
    name: formData.value.name.trim(),
    description: formData.value.description.trim(),
    createdAt: props.database?.createdAt || new Date()
  }
  
  emit('save', database)
}

// 处理遮罩层点击
function handleOverlayClick() {
  emit('close')
}

// 监听props变化，初始化表单数据
watch(() => props.database, (newDatabase) => {
  if (newDatabase) {
    formData.value = {
      name: newDatabase.name,
      description: newDatabase.description || ''
    }
  } else {
    formData.value = {
      name: '',
      description: ''
    }
  }
  
  // 清除错误信息
  errors.value = {
    name: ''
  }
}, { immediate: true })
</script>
