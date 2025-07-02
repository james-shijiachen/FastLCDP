<template>
  <div class="modal-overlay" @click="handleOverlayClick" data-uid="EntityEditModal-overlay">
    <div class="modal" @click.stop data-uid="EntityEditModal-modal">
      <div class="modal-header" data-uid="EntityEditModal-header">
        <h3 data-uid="EntityEditModal-title">{{ isEdit ? $t('entity.edit') : $t('entity.new') }}</h3>
        <button @click="$emit('close')" class="close-btn" data-uid="EntityEditModal-btn-close">×</button>
      </div>
      
      <div class="modal-content" data-uid="EntityEditModal-content">
        <div class="form-row" data-uid="EntityEditModal-form-row">
          <div class="form-group" data-uid="EntityEditModal-form-group-db">
            <label>{{ $t('database.name') }}</label>
            <select v-model="formData.databaseId" :disabled="isEdit" data-uid="EntityEditModal-select-db">
              <option value="">{{ $t('entity.selectDatabase') }}</option>
              <option v-for="db in databases" :key="db.id" :value="db.id">
                {{ db.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group" data-uid="EntityEditModal-form-group-type">
            <label>{{ $t('entity.type') }}</label>
            <div class="radio-group" data-uid="EntityEditModal-radio-group-type">
              <label class="radio-label">
                <input type="radio" v-model="formData.entityType" value="table" data-uid="EntityEditModal-radio-table" />
                {{ $t('entity.table') }}
              </label>
              <label class="radio-label">
                <input type="radio" v-model="formData.entityType" value="abstract" data-uid="EntityEditModal-radio-abstract" />
                {{ $t('entity.abstract') }}
              </label>
            </div>
          </div>
        </div>
        
        <div class="form-group" data-uid="EntityEditModal-form-group-name">
          <label>{{ $t('entity.name') }} *:</label>
          <input 
            v-model="formData.name" 
            :placeholder="$t('entity.namePlaceholder')"
            @keyup.enter="handleSave"
            data-uid="EntityEditModal-input-name"
          />
        </div>
        
        <div class="form-group" data-uid="EntityEditModal-form-group-comment">
          <label>{{ $t('entity.comment') }}:</label>
          <textarea 
            v-model="formData.comment" 
            :placeholder="$t('entity.commentPlaceholder')" 
            rows="3"
            data-uid="EntityEditModal-input-comment"
          ></textarea>
        </div>
        
        <div class="form-group" v-if="formData.databaseId && availableParents.length > 0" data-uid="EntityEditModal-form-group-inherit">
          <label>{{ $t('entity.inheritance') }}:</label>
          <select v-model="formData.parentEntityId" data-uid="EntityEditModal-select-inherit">
            <option value="">{{ $t('entity.noInheritance') }}</option>
            <option v-for="parent in availableParents" :key="parent.id" :value="parent.id">
              {{ parent.name }}
            </option>
          </select>
          <small class="form-hint">{{ $t('entity.inheritanceNote') }}</small>
        </div>
        
        <div class="fields-section" data-uid="EntityEditModal-fields-section">
          <div class="section-header" data-uid="EntityEditModal-section-header">
            <h4>{{ $t('entity.fieldDefinition') }}</h4>
            <button type="button" @click="addField" class="btn btn-sm btn-primary" data-uid="EntityEditModal-btn-add-field">
              <span class="icon">+</span>
              {{ $t('entity.addField') }}
            </button>
          </div>
          
          <div class="fields-list" data-uid="EntityEditModal-fields-list">
            <div 
              v-for="(field, index) in formData.fields" 
              :key="field.id" 
              class="field-item"
              :data-uid="`EntityEditModal-field-item-${index}`"
            >
              <div class="field-row" data-uid="EntityEditModal-field-row">
                <div class="field-basic" data-uid="EntityEditModal-field-basic">
                  <div class="form-group" data-uid="EntityEditModal-field-group-name">
                    <label>{{ $t('entity.fieldName') }}:</label>
                    <input v-model="field.name" :placeholder="$t('entity.fieldName')" data-uid="EntityEditModal-field-input-name" />
                  </div>
                  <div class="form-group" data-uid="EntityEditModal-field-group-type">
                    <label>{{ $t('entity.dataType') }}:</label>
                    <select v-model="field.type" data-uid="EntityEditModal-field-select-type">
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
                
                <div class="field-options" data-uid="EntityEditModal-field-options">
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="field.isPrimaryKey" data-uid="EntityEditModal-field-checkbox-pk" />
                    <span>主键</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="field.isRequired" data-uid="EntityEditModal-field-checkbox-required" />
                    <span>必填</span>
                  </label>
                  <label class="checkbox-label">
                    <input type="checkbox" v-model="field.isUnique" data-uid="EntityEditModal-field-checkbox-unique" />
                    <span>唯一</span>
                  </label>
                </div>
                
                <div class="field-actions" data-uid="EntityEditModal-field-actions">
                  <button @click="removeField(index)" class="remove-btn" data-uid="EntityEditModal-btn-remove-field">删除</button>
                </div>
              </div>
              
              <div class="form-group" data-uid="EntityEditModal-field-group-comment">
                <label>注释:</label>
                <input v-model="field.comment" placeholder="字段注释" data-uid="EntityEditModal-field-input-comment" />
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer" data-uid="EntityEditModal-footer">
        <button @click="$emit('close')" class="btn btn-secondary" data-uid="EntityEditModal-btn-cancel">{{ $t('common.cancel') }}</button>
        <button @click="handleSave" class="btn btn-primary" :disabled="!isValid" data-uid="EntityEditModal-btn-save">
          {{ isEdit ? $t('common.save') : $t('common.create') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useERDiagramStore } from '../stores/erDiagram'
import type { Entity, Field, Database } from '../types/entity'

const { t } = useI18n()

interface Props {
  entity?: Entity | null
  databases: Database[]
  availableParents: Entity[]
  defaultDatabaseId?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [entity: Entity]
  close: []
}>()

// 表单数据
const formData = ref({
  name: '',
  comment: '',
  databaseId: '',
  entityType: 'entity' as 'entity' | 'abstract',
  parentEntityId: '',
  fields: [] as Field[]
})

// 计算属性
const isEdit = computed(() => !!props.entity)

const isValid = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.databaseId.length > 0
})

const canSave = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.databaseId.length > 0
})

// 监听props变化
watch(() => props.entity, (entity) => {
  if (entity) {
    formData.value = {
      name: entity.name,
      comment: entity.comment || '',
      databaseId: entity.databaseId,
      entityType: entity.entityType,
      parentEntityId: entity.parentEntityId || '',
      fields: JSON.parse(JSON.stringify(entity.fields))
    }
  } else {
    formData.value = {
      name: '',
      comment: '',
      databaseId: props.defaultDatabaseId || '',
      entityType: 'entity',
      parentEntityId: '',
      fields: [
        {
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
    databaseId: formData.value.databaseId,
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

function handleOverlayClick() {
  emit('close')
}
</script>

<style scoped>
/* EntityEditModal 特有样式 */
.modal {
  max-width: 600px;
  max-height: 80vh;
}

.form-hint {
  display: block;
  margin-top: 4px;
  font-size: 12px;
  color: #586069;
  font-style: italic;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
  margin: 0;
}

@media (max-width: 768px) {
  .modal {
    width: 95%;
    max-height: 90vh;
  }
  
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
