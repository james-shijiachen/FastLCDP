<template>
  <div class="modal-overlay">
    <div class="modal-content" @click.stop @wheel.prevent="handleModalWheel">
      <div class="modal-header">
        <h3 class="modal-title">{{ isEdit ? $t('datasource.editDatasource') : $t('datasource.newDatasource') }}</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="form-row">
          <div class="form-group">
            <label for="datasource-name">{{ $t('datasource.name') }} *</label>
            <input 
              id="datasource-name"
              v-model="formData.name"
              type="text"
              :placeholder="$t('datasource.namePlaceholder')"
              @keyup.enter="handleSave"
              @change="validateName"
              :class="{ 'error': errors.name }"/>
              <span v-if="errors.name" class="error-message">{{ $t(errors.name) }}</span>
          </div>
          <div class="form-group">
            <label for="datasource-type">{{ $t('datasource.type') }} *</label>
            <select id="datasource-type" v-model="formData.type">
              <option value="">{{ $t('datasource.selectType') }}</option>
              <option value="DATABASE">{{ $t('datasource.database') }}</option>
              <option value="NOSQL">{{ $t('datasource.nosql') }}</option>
              <option value="DOCUMENT">{{ $t('datasource.document') }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="datasource-category">{{ $t('datasource.category') }} *</label>
            <select id="datasource-category" v-model="formData.category">
              <option value="">{{ $t('datasource.selectCategory') }}</option>
              <option value="MYSQL">MySQL</option>
              <option value="ORACLE">Oracle</option>
              <option value="POSTGRESQL">PostgreSQL</option>
              <option value="SQLSERVER">SQL Server</option>
              <option value="REDIS">Redis</option>
              <option value="JSON">JSON</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label for="datasource-description">{{ $t('datasource.description') }}</label>
          <textarea 
            id="datasource-description"
            v-model="formData.description" 
            :placeholder="$t('datasource.descriptionPlaceholder')"
            rows="3"
          ></textarea>
        </div>
      </div>
      <div class="modal-footer">
        <button class="btn btn-secondary" @click="$emit('close')">{{ $t('common.cancel') }}</button>
        <button class="btn btn-primary" @click="handleSave" :disabled="!isValid">
          {{ isEdit ? $t('common.save') : $t('common.create') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { Datasource } from '../types/entity'
import { DatasourceType, Category } from '../types/entity'

const { t: $t } = useI18n()

interface Props {
  datasource?: Datasource | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [datasource: Datasource]
  close: []
}>()

// 表单数据
const formData = ref({
  name: '',
  type: '' as 'DATABASE' | 'NOSQL' | 'DOCUMENT',
  category: '' as 'MYSQL' | 'ORACLE' | 'POSTGRESQL' | 'SQLSERVER' | 'REDIS' | 'JSON' | 'XML',
  description: ''
})

// 错误信息
const errors = ref({
  name: ''
})

// 是否为编辑模式
const isEdit = computed(() => !!props.datasource)

// 表单验证
const isValid = computed(() => {
  return formData.value.name.trim() !== '' && !errors.value.name
})

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
}

// 验证数据库名称
function validateName() {
  if (!formData.value.name.trim()) {
    errors.value.name = $t('datasource.nameRequired')
  } else if (formData.value.name.length > 50) {
    errors.value.name = $t('datasource.nameMaxLength')
  } else if (!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(formData.value.name)) {
    errors.value.name = $t('datasource.namePattern')
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
  const datasource: Datasource = {
    id: props.datasource?.id || Date.now().toString(),
    name: formData.value.name.trim(),
    type: formData.value.type as DatasourceType,
    category: formData.value.category as Category,
    description: formData.value.description.trim(),
    createdTime: props.datasource?.createdTime || new Date()
  }
  emit('save', datasource)
}

// 监听props变化，初始化表单数据
watch(() => props.datasource, (newDatasource) => {
  if (newDatasource) {
    formData.value = {
      name: newDatasource.name,
      type: newDatasource.type || 'DATABASE',
      category: newDatasource.category || 'MYSQL',
      description: newDatasource.description || ''
    }
  } else {
    formData.value = {
      name: '',
      type: '' as 'DATABASE' | 'NOSQL' | 'DOCUMENT',
      category: '' as 'MYSQL' | 'ORACLE' | 'POSTGRESQL' | 'SQLSERVER' | 'REDIS' | 'JSON' | 'XML',
      description: ''
    }
  }
  
  // 清除错误信息
  errors.value = {
    name: ''
  }
}, { immediate: true })
</script>
<style scoped>
.modal-content {
  width: 550px;
  min-width: 320px;
  height: 350px;
  box-sizing: border-box;
}
.form-group {
  display: flex;
  flex-direction: column; /* 如果label在上方，column即可 */
  gap: 6px;
  margin-bottom: 18px;
}
.form-group label {
  white-space: nowrap;
}
.form-group input, .form-group textarea {
  width: 100%;
  min-width: 0;
  box-sizing: border-box;
}
</style>