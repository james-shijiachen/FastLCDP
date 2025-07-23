<template>
  <div class="modal-overlay">
    <div class="modal-content" ref="modalRef" @click.stop @wheel.prevent="handleModalWheel">
      <div class="modal-header" @mousedown="onHeaderMousedown">
        <h3>{{ isEdit ? $t('entity.edit') : $t('entity.new') }}</h3>
        <div v-if="showHeaderName" class="entity-name-preview" :title="formData.name">
          {{ formData.name }}
        </div>
        <button @click="$emit('close')" class="close-btn">×</button>
      </div>
      <div class="modal-body" ref="modalContentRef" @scroll="handleModalScroll">
        <ValidateField
          ref="nameInputRef"
          v-model="formData.name"
          field="entity.name"
          component="EntityEditModal"
          :label="$t('entity.name')"
          :placeholder="$t('entity.name')"
          @enter="handleSave"
          @input="validateName"
          @blur="validateName"
          @focus="clearFieldError('entity.name')"
          :required="true"/>
        <RadioButton
          v-model="formData.entityType"
          field="entity.entityType"
          component="EntityEditModal"
          :label="$t('entity.type')"
          :options="[{ value: 'ENTITY', label: $t('entity.table'), icon: AddEntityIcon }, { value: 'ABSTRACT', label: $t('entity.abstract'), icon: AbstractEntityIcon }]"/>
        <RadioButton 
          v-model="formData.datasourceId" 
          field="entity.datasourceId" 
          component="EntityEditModal" 
          :label="$t('datasource.name')"
          :options="props.datasources.map(ds => ({ value: ds.id, label: ds.name, icon: iconMap[ds.type || DatasourceType.DATABASE]}))"/>
        <RadioButton 
          v-model="formData.parentEntityId" 
          field="entity.parentEntityId" 
          component="EntityEditModal" 
          :label="$t('entity.parent')"
          :options="[{ value: '', label: $t('entity.noParent') },
            ...availableParentEntities.map(entity => ({ value: entity.id, label: entity.name, icon: parentEntityIconMap[entity.entityType]}))]"/>
        <ValidateField
          v-model="formData.comment" 
          field="entity.comment" 
          type="textarea"
          component="EntityEditModal" 
          :label="$t('entity.comment')"
          :placeholder="$t('entity.comment')"
          @focus="clearFieldError('entity.comment')"/>
        <div class="fields-section">
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
                  <col style="width: 100px;" /> <!-- 来源 -->
              </colgroup>
              <thead class="fields-table-header">
                <tr>
                  <th></th>
                  <th>{{ $t('entity.fieldName') }} <span class="required">*</span></th>
                  <th>{{ $t('entity.dataType') }} <span class="required">*</span></th>
                  <th>{{ $t('entity.length') }}</th>
                  <th>{{ $t('entity.scale') }}</th>
                  <th>{{ $t('entity.primaryKey') }}</th>
                  <th>{{ $t('entity.required') }}</th>
                  <th>{{ $t('entity.unique') }}</th>
                  <th>{{ $t('entity.comment') }}</th>
                  <th>{{ $t('entity.source') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="allParentField && allParentField.length > 0" v-for="field in allParentField" :key="field.id">
                  <td></td>
                  <td><input :disabled="true" :value="field.name" /></td>
                  <td><input :disabled="true" :value="field.type" /></td>
                  <td><input :disabled="true" :value="field.length" /></td>
                  <td><input :disabled="true" :value="field.scale" /></td>
                  <td><input type="checkbox" :disabled="true" :checked="field.isPrimaryKey" /></td>
                  <td><input type="checkbox" :disabled="true" :checked="field.isRequired" /></td>
                  <td><input type="checkbox" :disabled="true" :checked="field.isUnique" /></td>
                  <td><textarea :disabled="true" :value="field.comment" /></td>
                  <td>&nbsp;{{ field.extended?.entityId ? field.extended.entityId + '.' + field.extended.fieldId : '' }}</td>
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
                  <td>&nbsp;{{ field.extended?.entityId ? field.extended.entityId + '.' + field.extended.fieldId : '' }}</td>
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
import { EntityType, DatasourceType } from '../types/entity'
import { useDraggableModal } from '@/utils/useDraggableModal'
import { ValidateField, RadioButton } from '@/components'
import { useFieldError } from '@/utils/useFieldError'
import AbstractEntityIcon from '@/assets/AbstractEntityIcon.vue'
import AddEntityIcon from '@/assets/AddEntityIcon.vue'
import DatabaseIcon from '@/assets/DatabaseIcon.vue'
import NoSQLIcon from '@/assets/NoSQLIcon.vue'
import DocumentIcon from '@/assets/DocumentIcon.vue'

const {clearFieldError, setFieldError, getFieldError } = useFieldError('EntityEditModal')
const { t: $t } = useI18n()
const { modalRef, onHeaderMousedown } = useDraggableModal()

const iconMap = {
  [DatasourceType.DATABASE]: DatabaseIcon,
  [DatasourceType.NOSQL]: NoSQLIcon,
  [DatasourceType.DOCUMENT]: DocumentIcon
}

const parentEntityIconMap = {
  [EntityType.ENTITY]: AddEntityIcon,
  [EntityType.ABSTRACT]: AbstractEntityIcon
}

interface Props {
  entity?: Entity | null
  entities: Entity[]
  parentEntity?: Entity | null
  datasources: Datasource[]
  currentDatasourceId?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [entity: Entity]
  close: []
}>()

const modalContentRef = ref<HTMLDivElement | null>(null)
const nameInputRef = ref<HTMLDivElement | null>(null)
const fieldsListWrapperRef = ref<HTMLDivElement | null>(null)
const showHeaderName = ref(false)

// 表单数据
const formData = ref({
  entityId: props.entity?.id || '',
  name: props.entity?.name || '',
  comment: props.entity?.comment || '',
  datasourceId: props.entity?.datasourceId || props.currentDatasourceId || '',
  entityType: props.entity?.entityType || 'ENTITY',
  parentEntityId: props.entity?.parentEntityId || props.parentEntity?.id || '',
  parentEntityName: props.parentEntity?.name || '',
  fields: props.entity?.fields || [
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
})

// 是否为编辑模式
const isEdit = computed(() => !!props.entity)

// 表单验证
const isValid = computed(() => {
  return getFieldError('entity.name') === '' && formData.value.datasourceId.length > 0 && formData.value.fields.every(field => field.name.trim().length > 0 && field.type.trim().length > 0)
})

function validateName() {
  if (!formData.value.name.trim()) {
    setFieldError('entity.name', $t('entity.nameRequired'))
  } else if (formData.value.name.length > 50) {
    setFieldError('entity.name', $t('entity.nameMaxLength'))
  } else if (!/^[a-zA-Z][a-zA-Z0-9_]*$/.test(formData.value.name)) {
    setFieldError('entity.name', $t('entity.namePattern'))
  } else {
    clearFieldError('entity.name')
  }
}

// 是否可以保存
const canSave = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.datasourceId.length > 0 && formData.value.fields.every(field => field.name.trim().length > 0 && field.type.trim().length > 0)
})

const allParentField = computed(() => {
  return props.parentEntity ? getAllParentFields(props.entities, props.parentEntity.id) : props.entity && props.entity.parentEntityId ? getAllParentFields(props.entities, props.entity.parentEntityId) : []
})

// 获取可选的父实体列表
const availableParentEntities = computed(() => {
  if (!formData.value.datasourceId) return []
  const currentEntityId = props.entity?.id || formData.value.entityId
  // 获取当前数据源下的所有实体
  const datasourceEntities = props.entities.filter(e => e.datasourceId === formData.value.datasourceId)
  // 过滤掉当前实体和其所有子实体，只保留抽象实体作为可选父实体
  return datasourceEntities.filter(entity => {
    return entity.id !== currentEntityId && // 排除当前实体
           !getChildEntityIds(datasourceEntities, currentEntityId).includes(entity.id) // 排除所有子实体
  })
})

// 获取当前实体的所有子实体ID（递归）
function getChildEntityIds(datasourceEntities: Entity[], parentId: string): string[] {
  const children = datasourceEntities.filter(e => e.parentEntityId === parentId)
  const childIds = children.map(c => c.id)
  
  // 递归获取子实体的子实体
  children.forEach(child => {
    childIds.push(...getChildEntityIds(datasourceEntities, child.id))
  })
  return childIds
}

// 监听数据源ID变化，重置父实体ID
watch(() => formData.value.datasourceId, (newDatasourceId, oldDatasourceId) => {
  if (newDatasourceId !== oldDatasourceId && formData.value.parentEntityId) {
    // 检查当前选择的父实体是否在新的可选列表中
    const isParentStillAvailable = availableParentEntities.value.some(entity => entity.id === formData.value.parentEntityId)
    
    if (!isParentStillAvailable) {
      // 如果当前选择的父实体不在新的可选列表中，则清空
      formData.value.parentEntityId = ''
      formData.value.parentEntityName = ''
    }
  }
})

// 递归获取所有父级字段
function getAllParentFields(entities: Entity[], parentEntityId: string | undefined): Field[] {
  const result: Field[] = [];
  let currentParentId = parentEntityId;
  while (currentParentId) {
    const parent = entities.find(e => e.id === currentParentId);
    if (parent) {
      
      // 设置来源信息，用于显示来源字段
      parent.fields.forEach(field => {
        field.extended = {
          entityId: parent.id,
          fieldId: field.id
        }
      })

      // 先递归上级，再加本级，保证顺序
      result.unshift(...parent.fields);
      currentParentId = parent.parentEntityId;
    } else {
      break;
    }
  }
  return result;
}

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
// 监听modal滚动
function handleModalScroll() {
  console.log("handleModalScroll1", modalContentRef.value, nameInputRef.value)
  if (!modalContentRef.value || !nameInputRef.value) return
  const modalRect = modalContentRef.value.getBoundingClientRect()
  const inputRect = (nameInputRef.value as any).getBoundingClientRect?.()
  console.log("handleModalScroll2", modalRect, inputRect)
  if (!inputRect) return
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
// 删除字段
function removeField(index: number) {
  formData.value.fields.splice(index, 1)
}
// 保存实体
function handleSave() {
  console.log("handleSave", canSave.value, isValid.value)
  if (!canSave.value) return
  if(isValid.value) {
    const entity: Entity = {
      id: props.entity?.id || Date.now().toString(),
      name: formData.value.name.trim(),
      comment: formData.value.comment.trim(),
      datasourceId: formData.value.datasourceId,
      entityType: formData.value.entityType === 'ABSTRACT' ? EntityType.ABSTRACT : EntityType.ENTITY,
      parentEntityId: formData.value.parentEntityId || undefined,
      fields: formData.value.fields.filter(f => f.name.trim()),
      x: props.entity?.x || 100,
      y: props.entity?.y || 100,
      width: props.entity?.width || 200,
      height: props.entity?.height || 60, // 高度将由ERCanvas自动计算
      backgroundColor: props.entity?.backgroundColor || '#ffffff',
      borderColor: props.entity?.borderColor || '#24292e'
    }
    console.log("handleSave", formData.value.parentEntityId, entity)
    emit('save', entity)
  }
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
.required {
  color: #ff4d4f;
  margin-left: 2px;
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
  resize: none !important;
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
