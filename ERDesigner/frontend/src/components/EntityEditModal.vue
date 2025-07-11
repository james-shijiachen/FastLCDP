<template>
  <div class="modal-overlay">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? $t('entity.edit') : $t('entity.new') }}</h3>
        <button @click="$emit('close')" class="close-btn">×</button>
      </div>
        <div class="form-row">
          <div class="form-group">
            <label>{{ $t('datasource.name') }}</label>
            <select v-model="formData.datasourceId" :disabled="isEdit">
              <option value="">{{ $t('entity.selectDatasource') }}</option>
              <option v-for="ds in datasources" :key="ds.id" :value="ds.id">
                {{ ds.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label>{{ $t('entity.type') }}</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="formData.entityType" value="table" />
                {{ $t('entity.table') }}
              </label>
              <label class="radio-label">
                <input type="radio" v-model="formData.entityType" value="abstract" />
                {{ $t('entity.abstract') }}
              </label>
            </div>
          </div>
        </div>
        
        <div class="form-group">
          <label>{{ $t('entity.name') }} *:</label>
          <input 
            v-model="formData.name" 
            :placeholder="$t('entity.namePlaceholder')"
            @keyup.enter="handleSave"
          />
        </div>
        
        <div class="form-group">
          <label>{{ $t('entity.comment') }}:</label>
          <textarea 
            v-model="formData.comment" 
            :placeholder="$t('entity.commentPlaceholder')" 
            rows="3"
          ></textarea>
        </div>
        
        <div class="form-group" v-if="formData.datasourceId && availableParents.length > 0">
          <label>{{ $t('entity.inheritance') }}:</label>
          <select v-model="formData.parentEntityId">
            <option value="">{{ $t('entity.noInheritance') }}</option>
            <option v-for="parent in availableParents" :key="parent.id" :value="parent.id">
              {{ parent.name }}
            </option>
          </select>
          <small class="form-hint">{{ $t('entity.inheritanceNote') }}</small>
        </div>
        
        <div class="fields-section">
          <div class="section-header">
            <h4>{{ $t('entity.fieldDefinition') }}</h4>
            <button type="button" @click="addField" class="btn btn-primary">
              <span class="icon">+</span>
              {{ $t('entity.addField') }}
            </button>
          </div>
          
          <div class="fields-list">
            <div 
              v-for="(field, index) in formData.fields" 
              :key="field.id" 
              class="field-item"
            >
              <div class="field-row">
                <div class="field-basic">
                  <div class="form-group">
                    <label>{{ $t('entity.fieldName') }}:</label>
                    <input v-model="field.name" :placeholder="$t('entity.fieldName')" />
                  </div>
                  <div class="form-group">
                    <label>{{ $t('entity.dataType') }}:</label>
                    <select v-model="field.type">
                      <option value="INT">INT</option>
                      <option value="BIGINT">BIGINT</option>
                      <option value="VARCHAR(50)">VARCHAR(50)</option>
                      <option value="VARCHAR(255)">VARCHAR(255)</option>
                      <option value="TEXT">TEXT</option>
                      <option value="DECIMAL(10,2)">DECIMAL(10,2)</option>
                      <option value="DATETIME">DATETIME</option>
                      <option value="DATE">DATE</option>
                      <option value="TIMESTAMP">TIMESTAMP</option>
                      <option value="BOOLEAN">BOOLEAN</option>
                    </select>
                  </div>
                </div>
                
                <div class="field-options">
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="field.isPrimaryKey" />
                    <span>主键</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="field.isRequired" />
                    <span>必填</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="field.isUnique" />
                    <span>唯一</span>
                  </label>
                </div>
                
                <div class="field-actions">
                  <button @click="removeField(index)" class="remove-btn">删除</button>
                </div>
              </div>
              
              <div class="form-group">
                <label>注释:</label>
                <input v-model="field.comment" placeholder="字段注释" />
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="$emit('close')" class="btn btn-secondary">{{ $t('common.cancel') }}</button>
          <button @click="handleSave" class="btn btn-primary" :disabled="!isValid">
            {{ isEdit ? $t('common.save') : $t('common.create') }}
          </button>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { Entity, Field, Datasource } from '../types/entity'

const { t } = useI18n()

interface Props {
  entity?: Entity | null
  datasources: Datasource[]
  availableParents: Entity[]
  defaultDatasourceId?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [entity: Entity]
  close: []
}>()

// 表单数据
const formData = ref({
  entityId: '',
  name: '',
  comment: '',
  datasourceId: '',
  entityType: 'entity' as 'entity' | 'abstract',
  parentEntityId: '',
  fields: [] as Field[]
})

// 计算属性
const isEdit = computed(() => !!props.entity)

const isValid = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.datasourceId.length > 0
})

const canSave = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.datasourceId.length > 0
})

// 监听props变化
watch(() => props.entity, (entity) => {
  if (entity) {
    formData.value = {
      entityId: entity.id,
      name: entity.name,
      comment: entity.comment || '',
      datasourceId: entity.datasourceId,
      entityType: entity.entityType,
      parentEntityId: entity.parentEntityId || '',
      fields: JSON.parse(JSON.stringify(entity.fields))
    }
  } else {
    formData.value = {
      entityId: '',
      name: '',
      comment: '',
      datasourceId: props.defaultDatasourceId || '',
      entityType: 'entity',
      parentEntityId: '',
      fields: [
        {
          entityId: '',
          id: Date.now().toString(),
          name: 'id',
          type: 'INT',
          comment: '主键',
          isPrimaryKey: true,
          isRequired: true,
          isUnique: true
        }
      ]
    }
  }
}, { immediate: true })

// 方法
function addField() {
  const newField: Field = {
    entityId: formData.value.entityId,
    id: Date.now().toString(),
    name: '',
    type: 'VARCHAR(50)',
    comment: '',
    isPrimaryKey: false,
    isRequired: false,
    isUnique: false
  }
  formData.value.fields.push(newField)
}

function removeField(index: number) {
  formData.value.fields.splice(index, 1)
}

function handleSave() {
  if (!canSave.value) return
  
  const entity: Entity = {
    id: props.entity?.id || Date.now().toString(),
    name: formData.value.name.trim(),
    comment: formData.value.comment.trim(),
    datasourceId: formData.value.datasourceId,
    entityType: formData.value.entityType,
    parentEntityId: formData.value.parentEntityId || undefined,
    fields: formData.value.fields.filter(f => f.name.trim()),
    x: props.entity?.x || 100,
    y: props.entity?.y || 100,
    width: props.entity?.width || 200,
    height: props.entity?.height || 60, // 高度将由ERCanvas自动计算
    backgroundColor: props.entity?.backgroundColor || '#ffffff',
    borderColor: props.entity?.borderColor || '#24292e'
  }
  
  emit('save', entity)
}
</script>

<style scoped>
.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-bottom: 1px solid #f6f8fa;
}

.field-row:last-child {
  border-bottom: none;
}

.field-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 80px;
}

.field-basic {
  flex: 1;
  display: flex;
  gap: 12px;
}

.field-basic .form-group {
  flex: 1;
  margin-bottom: 0;
}

@media (max-width: var(--mobile-breakpoint)) {
  .field-row {
    flex-direction: column;
    gap: 12px;
  }
  .field-basic {
    flex-direction: column;
  }
  .field-options {
    flex-direction: row;
    flex-wrap: wrap;
  }
}

</style>
