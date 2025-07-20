<template>
  <div class="modal-overlay">
    <div class="modal-content" ref="modalRef" @click.stop @wheel.prevent="handleModalWheel">
      <div class="modal-header" @mousedown="onHeaderMousedown">
        <h3 class="modal-title">{{ isEdit ? $t('datasource.editDatasource') : $t('datasource.newDatasource') }}</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <div class="form-row">
          <ValidateField
            v-model="formData.name"
            field="datasource.name"
            component="DatasourceEditModal"
            :label="$t('datasource.name')"
            :placeholder="$t('datasource.namePlaceholder')"
            @enter="handleSave"
            @input="validateName"
            @blur="validateName"
            @focus="clearFieldError('datasource.name')"
            :required="true"/>
          <div class="form-group">
            <label for="datasource-view">{{ $t('datasource.view') }} *</label>
            <select id="datasource-view" v-model="formData.viewId">
              <option v-if="formData.viewId === 'default' || formData.viewId === ''" value="default">{{ $t('datasource.defaultView') }}</option>
              <option v-else :value="formData.viewId">{{ formData.name }}</option>
              <option v-if="formData.viewId === 'default' || formData.viewId === ''" value="">{{ $t('datasource.newView') }}</option>
            </select>
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
import { ref, computed} from 'vue'
import { useI18n } from 'vue-i18n'
import type { Datasource } from '../types/entity'
import { DatasourceType, Category } from '../types/entity'
import { useDraggableModal } from '@/utils/useDraggableModal'
import { ValidateField } from '@/components'
import { useFieldError } from '@/utils/useFieldError'

const {clearFieldError, setFieldError, getFieldError } = useFieldError('DatasourceEditModal')
const { t: $t } = useI18n()
const { modalRef, onHeaderMousedown } = useDraggableModal()

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
  name: props.datasource?.name || '',
  type: props.datasource?.type || '',
  viewId: props.datasource?.viewId || 'default',
  category: props.datasource?.category || '',
  description: props.datasource?.description || ''
})

// 是否为编辑模式
const isEdit = computed(() => !!props.datasource)

// 表单验证
const isValid = computed(() => {
  return formData.value.name.trim() !== '' && !getFieldError('datasource.name')
})

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
}

// 验证数据库名称
function validateName() {
  if (!formData.value.name.trim()) {
    setFieldError('datasource.name', $t('datasource.nameRequired'))
  } else if (formData.value.name.length > 50) {
    setFieldError('datasource.name', $t('datasource.nameMaxLength'))
  } else if (!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(formData.value.name)) {
    setFieldError('datasource.name', $t('datasource.namePattern'))
  } else {
    clearFieldError('datasource.name')
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
    viewId: formData.value.viewId,
    type: formData.value.type as DatasourceType,
    category: formData.value.category as Category,
    description: formData.value.description.trim()
  }
  emit('save', datasource)
}
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

/* 错误样式 */
.form-group input.error,
.form-group textarea.error,
.form-group select.error {
  border-color: #ff4d4f;
  box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.2);
}

.error-message {
  color: #ff4d4f;
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.4;
}
</style>