<template>
  <div class="modal-overlay">
    <div class="modal-content" @click.stop @wheel.prevent="handleModalWheel">
      <div class="modal-header">
        <h3>{{ isEdit ? $t('entity.edit') : $t('entity.new') }}</h3>
        <div v-if="showHeaderName" class="entity-name-preview" :title="formData.name">
          {{ formData.name }}
        </div>
        <button @click="$emit('close')" class="close-btn">×</button>
      </div>
      <div class="modal-body" ref="modalContentRef" @scroll="handleModalScroll">
        <div class="form-row">
          <div class="form-group">
            <label>{{ $t('datasource.name') }}</label>
            <select v-model="formData.datasourceId" :disabled="isEdit">
              <option v-for="ds in datasources" :key="ds.id" :value="ds.id">
                {{ ds.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label>{{ $t('entity.type') }}</label>
            <div class="radio-group">
              <label class="radio-label">
                <input type="radio" v-model="formData.entityType" value="entity" />
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
          <input ref="nameInputRef"
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
          </div>
          <div class="fields-list-wrapper" ref="fieldsListWrapperRef">
            <table class="fields-table">
              <colgroup>
                  <col style="width: 50px;" />  <!-- 操作 -->
                  <col style="width: 200px;" /> <!-- 字段名 -->
                  <col style="width: 150px;" /> <!-- 类型 -->
                  <col style="width: 80px;" /> <!-- 长度 -->
                  <col style="width: 80px;" /> <!-- 精度 -->
                  <col style="width: 20px;" />  <!-- 主键 -->
                  <col style="width: 20px;" />  <!-- 必填 -->
                  <col style="width: 20px;" />  <!-- 唯一 -->
                  <col style="width: 300px;" /> <!-- 注释 -->
              </colgroup>
              <thead class="fields-table-header">
                <tr>
                  <th></th>
                  <th>{{ $t('entity.fieldName') }} *</th>
                  <th>{{ $t('entity.dataType') }} *</th>
                  <th>{{ $t('entity.length') }}</th>
                  <th>{{ $t('entity.scale') }}</th>
                  <th>{{ $t('entity.primaryKey') }}</th>
                  <th>{{ $t('entity.required') }}</th>
                  <th>{{ $t('entity.unique') }}</th>
                  <th>{{ $t('entity.comment') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="parentFields && parentFields.length > 0" v-for="field in parentFields" :key="field.id">
                  <td></td>
                  <td>{{ field.name }}</td>
                  <td>{{ field.type }}</td>
                  <td>{{ field.length }}</td>
                  <td>{{ field.scale }}</td>
                  <td>{{ field.isPrimaryKey ? '🔑' : '' }}</td>
                  <td>{{ field.isRequired ? '✔' : '' }}</td>
                  <td>{{ field.isUnique ? '✔' : '' }}</td>
                  <td>{{ field.comment }}</td>
                  <td></td>
                </tr>
                <tr v-for="(field, index) in formData.fields" :key="field.id">
                  <td><button @click="removeField(index)" class="remove-btn" :title="$t('entity.removeField')">X</button></td>
                  <td><input :title="field.name" v-model="field.name" :placeholder="$t('entity.fieldName')" /></td>
                  <td>
                    <select v-model="field.type">
                      <option value="INT">INT</option>
                      <option value="BIGINT">BIGINT</option>
                      <option value="VARCHAR">VARCHAR</option>
                      <option value="TEXT">TEXT</option>
                      <option value="DECIMAL">DECIMAL</option>
                      <option value="DATETIME">DATETIME</option>
                      <option value="BOOLEAN">BOOLEAN</option>
                    </select>
                  </td>
                  <td><input v-model="field.length" v-model.number="field.length" /></td>
                  <td><input v-model="field.scale" v-model.number="field.scale" /></td>
                  <td><input type="checkbox" v-model="field.isPrimaryKey" /></td>
                  <td><input type="checkbox" v-model="field.isRequired" /></td>
                  <td><input type="checkbox" v-model="field.isUnique" /></td>
                  <td><textarea :title="field.comment" v-model="field.comment" rows="2"></textarea></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" @click="addField" class="btn btn-primary">{{ $t('entity.addField') }}</button>
        <button @click="$emit('close')" class="btn btn-secondary">{{ $t('common.cancel') }}</button>
        <button @click="handleSave" class="btn btn-primary" :disabled="!canSave">
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
import { EntityType } from '../types/entity'

const { t } = useI18n()

interface Props {
  entity?: Entity | null
  parentEntity?: Entity | null
  datasources: Datasource[]
  currentDatasourceId?: string
  availableParents: Entity[]
  parentFields?: Field[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [entity: Entity]
  close: []
}>()

const modalContentRef = ref<HTMLDivElement | null>(null)
const nameInputRef = ref<HTMLInputElement | null>(null)
const fieldsListWrapperRef = ref<HTMLDivElement | null>(null)
const showHeaderName = ref(false)

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

// 是否为编辑模式
const isEdit = computed(() => !!props.entity)

// 表单验证
const isValid = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.datasourceId.length > 0 && formData.value.fields.every(field => field.name.trim().length > 0 && field.type.trim().length > 0)
})

// 是否可以保存
const canSave = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.datasourceId.length > 0 && formData.value.fields.every(field => field.name.trim().length > 0 && field.type.trim().length > 0)
})

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const containerFields = fieldsListWrapperRef.value;
  const containerModal = modalContentRef.value;
  if (containerModal) {
    containerModal.scrollTop += event.deltaY; // 纵向滚动
  }
  if (containerFields) {
    containerFields.scrollLeft += event.deltaX; // 横向滚动
  }
}

// 监听props变化
watch(() => props.entity, (entity) => {
  console.log('entity', entity, props.parentEntity)
  if (entity) {
    formData.value = {
      entityId: entity.id,
      name: entity.name,
      comment: entity.comment || '',
      datasourceId: entity.datasourceId,
      entityType: entity.entityType === EntityType.ABSTRACT ? 'abstract' : 'entity',
      parentEntityId: entity.parentEntityId || '',
      fields: JSON.parse(JSON.stringify(entity.fields))
    }
  } else {
    formData.value = {
      entityId: '',
      name: '',
      comment: '',
      datasourceId: props.currentDatasourceId || '',
      entityType: 'entity',
      parentEntityId: props.parentEntity?.id || '',
      fields: [
        {
          entityId: '',
          id: Date.now().toString(),
          name: '',
          type: '',
          comment: '',
          isPrimaryKey: false,
          isRequired: false,
          isUnique: false
        }
      ]
    }
  }
}, { immediate: true })

// 监听modal滚动
function handleModalScroll() {
  if (!modalContentRef.value || !nameInputRef.value) return
  const modalRect = modalContentRef.value.getBoundingClientRect()
  const inputRect = nameInputRef.value.getBoundingClientRect()
  // 判断输入框底部是否在 modal-content 顶部之上（即被滚动出去了）
  showHeaderName.value = inputRect.bottom < modalRect.top || inputRect.top > modalRect.bottom
}

// 添加字段
function addField() {
  const newField: Field = {
    entityId: formData.value.entityId,
    id: Date.now().toString(),
    name: '',
    type: '',
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
    entityType: formData.value.entityType === 'abstract' ? EntityType.ABSTRACT : EntityType.ENTITY,
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
.modal-content {
  width: 600px;
  min-width: 400px;
  min-height: 50vh;
  max-height: 50vh;
  display: flex !important;
  flex-direction: column !important;
}
.modal-header,
.modal-footer {
  flex-shrink: 0;
}
.modal-body {
  flex: 1 1 auto;
  overflow-y: auto;
  min-height: 0; /* 兼容性写法，防止flex子项溢出 */
}
.entity-name-preview {
  margin-top: 4px;
  font-size: 15px;
  color: #888;
  word-break: break-all;
  max-width: 100%;
  white-space: normal;
}

/* 字段相关样式 */
.fields-list-wrapper {
  overflow-x: auto;
  max-width: 100%;
}
.fields-section {
  margin-top: 16px;
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.section-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #24292e;
}
.dark-theme .section-header h4 {
  color: #ffffff;
  font-weight: 500;
}

/* 字段表格 */
.fields-table {
  min-width: 900px; /* 让表格内容撑开，超出时滚动 */
  border-collapse: collapse;
}
.fields-table-header {
  background: #f6f8fa !important;
  font-weight: 600;
  font-size: 15px;
}
.fields-table th,
.fields-table td {
  border: 1px solid #e1e4e8;
  padding: 4px 8px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.fields-table th {
  background: #f6f8fa;
}
.fields-table textarea {
  resize: none;
}
.fields-table input{
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 删除按钮 */
.remove-btn {
  background: #d73a49;
  color: #fff;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.remove-btn:hover {
  background: #cb2431;
}
.dark-theme .remove-btn {
  background: #cf6679;
  color: #000000;
}
.dark-theme .remove-btn:hover {
  background: #e91e63;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

@media (max-width: var(--mobile-breakpoint)) {

    
}
</style>
