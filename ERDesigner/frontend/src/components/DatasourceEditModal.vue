<template>
  <div class="modal-overlay">
    <div class="modal-content" ref="modalRef" @click.stop @wheel.prevent="handleModalWheel">
      <div class="modal-header" @mousedown="onHeaderMousedown">
        <h3 class="modal-title">{{ isEdit ? $t('datasource.editDatasource') : $t('datasource.newDatasource') }}</h3>
        <button class="close-btn" @click="$emit('close')">×</button>
      </div>
      <div class="modal-body">
        <ValidateField
          v-model="formData.name"
          field="datasource.name"
          component="DatasourceEditModal"
          :label="$t('datasource.name')"
          :placeholder="$t('datasource.name')"
          @enter="handleSave"
          @input="validateName"
          @blur="validateName"
          @focus="clearFieldError('datasource.name')"
          :required="true"/>
        <RadioButton 
          field="datasource.viewId" 
          v-model="formData.viewId" 
          :label="$t('datasource.view')"
          :options="viewOptions" 
          component="DatasourceEditModal" />
        <RadioButton 
          field="datasource.type" 
          v-model="formData.type" 
          :label="$t('datasource.type')"
          :options="typeOptions" 
          component="DatasourceEditModal" />
        <RadioButton 
          field="datasource.category" 
          v-model="formData.category" 
          :label="$t('datasource.category')"
          :options="categoryOptions" 
          component="DatasourceEditModal" />
        <RadioButton 
          field="datasource.version" 
          v-model="formData.version" 
          :label="$t('datasource.version')"
          :options="versionOptions" 
          component="DatasourceEditModal" />
        <ValidateField 
          v-model="formData.description"
          field="datasource.description"
          type="textarea"
          component="DatasourceEditModal"
          :label="$t('datasource.description')"
          :placeholder="$t('datasource.description')"/>
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
import { DatasourceType, DatasourceCategory, DatasourceVersion } from '../types/entity'
import { useDraggableModal } from '@/utils/useDraggableModal'
import { ValidateField, RadioButton } from '@/components'
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
  type: props.datasource?.type || 'DATABASE',
  viewId: props.datasource?.viewId || 'default',
  category: props.datasource?.category || 'MYSQL',
  version: props.datasource?.version || 'MYSQL_5_X',
  description: props.datasource?.description || ''
})

// 是否为编辑模式
const isEdit = computed(() => !!props.datasource)

// 表单验证
const isValid = computed(() => {
  return formData.value.name.trim() !== '' && !getFieldError('datasource.name')
})

const viewOptions = computed(() => {
  const viewId = formData.value.viewId;
  let options: { value: string, label: string, icon?: string }[] = [];
  if(viewId === 'default' || viewId === ''){
    options = [
      { value: 'default', label: $t('datasource.defaultView'), icon: 'default-view' },
      ({ value: '', label: $t('datasource.newView'), icon: 'new-view' })
    ];
  }else{
    options = [
      { value: formData.value.viewId, label: formData.value.name, icon: 'view' }
    ];
  }
  
  return options;
})

const typeOptions = computed(() => {
  const options = [
    { value: 'DATABASE', label: $t('datasource.database'), icon: 'database' },
    { value: 'NOSQL', label: $t('datasource.nosql'), icon: 'nosql' },
    { value: 'DOCUMENT', label: $t('datasource.document'), icon: 'document' }
  ];
  return options;
})

const categoryOptions = computed(() => {
  const type = formData.value.type;
  let options;
  if(type === 'DATABASE' || type === ''){
    options = [
    { value: 'SQLITE', label: $t('datasource.sqlite'), icon: 'sqlite' },
    { value: 'MYSQL', label: $t('datasource.mysql'), icon: 'mysql' },
    { value: 'ORACLE', label: $t('datasource.oracle'), icon: 'oracle' },
    { value: 'POSTGRESQL', label: $t('datasource.postgresql'), icon: 'postgresql' },
    { value: 'SQLSERVER', label: $t('datasource.sqlserver'), icon: 'sqlserver' }
    ];
  }else if(type === 'NOSQL'){
    options = [
      { value: 'REDIS', label: $t('datasource.redis'), icon: 'redis' },
      { value: 'MONGODB', label: $t('datasource.mongo'), icon: 'mongodb' },
      { value: 'ELASTICSEARCH', label: $t('datasource.elasticsearch'), icon: 'elasticsearch' }
    ];
  }else if(type === 'DOCUMENT'){
    options = [
      { value: 'JSON', label: $t('datasource.json'), icon: 'json' },
      { value: 'XML', label: $t('datasource.xml'), icon: 'xml' }
    ];
  }
  return options;
})

const versionOptions = computed(() => {
  const type = formData.value.type;
  const category = formData.value.category;
  let options;
  if(type === 'DATABASE' && category === 'MYSQL'){
    options = [
      { value: 'MYSQL_5_X', label: $t('datasource.mysql5'), icon: 'mysql' },
      { value: 'MYSQL_8_X', label: $t('datasource.mysql8'), icon: 'mysql' },
  ]}else if(type === 'DATABASE' && category === 'ORACLE'){
    options = [
      { value: 'ORACLE_11_X', label: $t('datasource.oracle11'), icon: 'oracle' },
      { value: 'ORACLE_12_X', label: $t('datasource.oracle12'), icon: 'oracle' },
      { value: 'ORACLE_19_X', label: $t('datasource.oracle19'), icon: 'oracle' },
      { value: 'ORACLE_21_X', label: $t('datasource.oracle21'), icon: 'oracle' },
      { value: 'ORACLE_23_X', label: $t('datasource.oracle23'), icon: 'oracle' },
      { value: 'ORACLE_25_X', label: $t('datasource.oracle25'), icon: 'oracle' },
  ]}else if(type === 'DATABASE' && category === 'POSTGRESQL'){
    options = [
      { value: 'POSTGRESQL_10_X', label: $t('datasource.postgresql10'), icon: 'postgresql' },
      { value: 'POSTGRESQL_11_X', label: $t('datasource.postgresql11'), icon: 'postgresql' },
      { value: 'POSTGRESQL_12_X', label: $t('datasource.postgresql12'), icon: 'postgresql' },
      { value: 'POSTGRESQL_13_X', label: $t('datasource.postgresql13'), icon: 'postgresql' },
      { value: 'POSTGRESQL_14_X', label: $t('datasource.postgresql14'), icon: 'postgresql' },
      { value: 'POSTGRESQL_15_X', label: $t('datasource.postgresql15'), icon: 'postgresql' },
      { value: 'POSTGRESQL_16_X', label: $t('datasource.postgresql16'), icon: 'postgresql' },
  ]}else if(type === 'DATABASE' && category === 'SQLSERVER'){
    options = [
      { value: 'SQLSERVER_2008_X', label: $t('datasource.sqlserver2008'), icon: 'sqlserver' },
      { value: 'SQLSERVER_2012_X', label: $t('datasource.sqlserver2012'), icon: 'sqlserver' },
      { value: 'SQLSERVER_2014_X', label: $t('datasource.sqlserver2014'), icon: 'sqlserver' },
      { value: 'SQLSERVER_2016_X', label: $t('datasource.sqlserver2016'), icon: 'sqlserver' },
      { value: 'SQLSERVER_2017_X', label: $t('datasource.sqlserver2017'), icon: 'sqlserver' },
      { value: 'SQLSERVER_2019_X', label: $t('datasource.sqlserver2019'), icon: 'sqlserver' },
      { value: 'SQLSERVER_2022_X', label: $t('datasource.sqlserver2022'), icon: 'sqlserver' }
  ]}
  return options;
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
    version: formData.value.version as DatasourceVersion,
    category: formData.value.category as DatasourceCategory,
    description: formData.value.description.trim()
  }
  emit('save', datasource)
}
</script>
<style scoped>
.modal-content {
  width: 550px;
  min-width: 100px;
  height: 500px;
  box-sizing: border-box;
  /* 可选：让宽度不超过屏幕 */
  max-width: 95vw;
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
.form-group input, 
.form-group textarea {
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

/* 移动端自适应 */
@media (max-width: var(--mobile-breakpoint)) {
  .modal-content {
    width: 98vw;
    min-width: 0;
    height: auto; /* 可选：高度自适应内容 */
    padding: 18px 0px;
  }
}
</style>