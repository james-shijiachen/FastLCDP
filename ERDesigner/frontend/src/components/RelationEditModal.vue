<template>
  <div class="modal-overlay">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ $t('relation.createRelation') }}</h3>
        <button @click="$emit('close')" class="close-btn">×</button>
      </div>
      <div class="modal-body">
        <div class="entities-info">
          <div class="entity-card">
            <h4>{{ fromEntity?.name || $t('entity.entity1') }}</h4>
            <div class="entity-fields">
              <div 
                v-for="field in fromEntityFields" 
                :key="field.id"
                :ref="el => setFieldRef(fromEntity.id, field.id, el as HTMLElement)"
                class="field-option"
                :class="{ selected: formData.fromFieldId === field.id, disabled: hasRelation(fromEntity.id, field.id) || (toField && shouldDisableField(field, toField)) }"
                @click="hasRelation(fromEntity.id, field.id) || (toField && shouldDisableField(field, toField)) ? null : selectFromField(field.id)">
                <div class="field-left">
                  <span class="icon"><Icon :name="field.isPrimaryKey ? 'key' : field.isUnique ? 'unique' : ''" /></span>
                  <span class="field-name">{{ field.name }}</span>
                </div>
                <div class="field-right">
                  <span class="field-type" v-if="field.length && field.scale">{{ field.type }}({{ field.length }},{{ field.scale }})</span>
                  <span class="field-type" v-else-if="field.length">{{ field.type }}({{ field.length }})</span>
                  <span class="field-type" v-else>{{ field.type }}</span>
                </div>
              </div>
            </div>
          </div>
          <RelationLine
            v-for="relationship in allRelationShips"
            :key="relationship.id"
            :ENTITY_HEADER_HEIGHT="props.ENTITY_HEADER_HEIGHT"
            :FIELD_HEIGHT="props.FIELD_HEIGHT"
            :relationLineType="`editor`"
            :fieldRefs="fieldRefs"
            :relationship="relationship"
            :fieldUniqueCache="fieldUniqueCache"/>
          <div class="entity-card">
            <h4>{{ toEntity?.name || $t('entity.entity2') }}</h4>
            <div class="entity-fields">
              <div 
                v-for="field in toEntityFields" 
                :key="field.id"
                :ref="el => setFieldRef(toEntity.id, field.id, el as HTMLElement)"
                class="field-option"
                :class="{ selected: formData.toFieldId === field.id, disabled: hasRelation(toEntity.id, field.id) || (fromField && shouldDisableField(field, fromField)) }"
                @click="hasRelation(toEntity.id, field.id) || (fromField && shouldDisableField(field, fromField)) ? null : selectToField(field.id)">
                <div class="field-left">
                  <span class="icon"><Icon :name="field.isPrimaryKey ? 'key' : field.isUnique ? 'unique' : ''" /></span>
                  <span class="field-name">{{ field.name }}</span>
                </div>
                <div class="field-right">
                  <span class="field-type" v-if="field.length && field.scale">{{ field.type }}({{ field.length }},{{ field.scale }})</span>
                  <span class="field-type" v-else-if="field.length">{{ field.type }}({{ field.length }})</span>
                  <span class="field-type" v-else>{{ field.type }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <ValidateField
          v-model="formData.name"
          field="relation.name" 
          component="RelationEditModal"
          :label="$t('relation.name')"
          :placeholder="$t('relation.name')"
          @enter="handleSave"
          :required="true"/>
        <RadioButton 
          field="relation.type" 
          v-model="formData.relationType" 
          :label="$t('relation.type')"
          :options="relationshipTypeOptions" 
          component="RelationEditModal" 
          @change="handleRelationTypeChange"/>
        <div class="form-group">{{ $t('relation.cascadeUpdate') }}</div>
        <RadioButton 
          field="relation.cascadeUpdate" 
          v-model="formData.cascadeUpdate" 
          :label="$t('relation.cascadeUpdate')"
          :options="cascadeOperationOptions" 
          :disabled="formData.relationType === RelationshipType.SOFT"
          component="RelationEditModal" />
        <div class="form-group">{{ $t('relation.cascadeDelete') }}</div>
        <RadioButton 
          field="relation.cascadeDelete" 
          v-model="formData.cascadeDelete" 
          :label="$t('relation.cascadeDelete')"
          :options="cascadeOperationOptions" 
          :disabled="formData.relationType === RelationshipType.SOFT"
          component="RelationEditModal" />
        <div class="form-row">
          <label v-for="category in relationCategories" 
            :key="category.value"
            class="form-group relation-type-option"
            :class="{ selected: canSelectRelationCategories.includes(category) && formData.relationCategory === category.value, disabled: !canSelectRelationCategories.includes(category) }">
            <input type="radio" 
              :value="category.value" 
              v-model="formData.relationCategory"/>
            <div class="type-info">
              <div class="type-name">{{ $t(category.labelKey) }}</div>
              <div class="type-visual">{{ category.visual }}</div>
            </div>
          </label>
        </div>
      </div>
      <div class="modal-footer">
        <button @click="$emit('close')" class="btn btn-secondary">{{ $t('common.cancel') }}</button>
        <button @click="handleSave" class="btn btn-primary" :disabled="!canSave">
          {{ $t('relation.create') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { Entity, Relationship, CascadeOperation, Field } from '../types/entity'
import { RelationshipType, RelationshipCategory } from '../types/entity'
import { ValidateField, RadioButton, RelationLine } from '@/components'
import { getAllParentFields } from '@/utils/datasourceUtil'
import Icon from '@/components/Icon.vue'

interface Props {
  entities: Entity[]
  selectedEntities: Entity[]
  relationships: Relationship[]
  fieldUniqueCache: Record<string, boolean>
  ENTITY_HEADER_HEIGHT: number
  FIELD_HEIGHT: number
}

const props = defineProps<Props>()
const { t: $t } = useI18n()

const emit = defineEmits<{
  save: [relationship: Relationship]
  close: []
}>()

const formData = ref({
  id: Date.now().toString(),
  name: '',
  fromFieldId: '',
  toFieldId: '',
  cascadeUpdate: 'NO_ACTION' as CascadeOperation | '',
  cascadeDelete: 'NO_ACTION' as CascadeOperation | '',
  cascadeCreate: false,
  relationCategory: '' as RelationshipCategory | '',
  relationType: 'SOFT' as RelationshipType
})

const relationCategories = [{
    value: 'ONE_TO_ONE',
    labelKey: 'relation.oneToOne',
    visual: '1 ——— 1'
  },
  {
    value: 'ONE_TO_MANY',
    labelKey: 'relation.oneToMany',
    visual: '1 ——< ∞'
  },
  {
    value: 'MANY_TO_ONE',
    labelKey: 'relation.manyToOne',
    visual: '∞ >—— 1'
  },
  {
    value: 'MANY_TO_MANY',
    labelKey: 'relation.manyToMany',
    visual: '∞ >—< ∞'
  }
]

const fieldRefs = ref<Record<string, Record<string, HTMLElement>>>({})
const fromEntity = computed(() => props.selectedEntities[0])
const toEntity = computed(() => props.selectedEntities[1])
const allRelationShips = computed(() => {
  const allRelationShips = 
    props.relationships.filter(
      relationship => 
      (relationship.fromEntityId === fromEntity.value?.id && relationship.toEntityId === toEntity.value?.id) 
      || (relationship.fromEntityId === toEntity.value?.id && relationship.toEntityId === fromEntity.value?.id)
    )
  
  const newRelationship: Relationship = {
    id: formData.value.id,
    name: formData.value.name.trim(),
    fromEntityId: fromEntity.value.id,
    toEntityId: toEntity.value.id,
    fromFieldId: formData.value.fromFieldId,
    toFieldId: formData.value.toFieldId,
    type: formData.value.relationType as RelationshipType,
    category: formData.value.relationCategory as RelationshipCategory,
    datasourceId: fromEntity.value.datasourceId,
    x: 0,
    y: 0
  }
  return [...allRelationShips, newRelationship]
})

const fromEntityFields = computed(() => {
  if (!fromEntity.value) return []
  if (fromEntity.value.parentEntityId) {
    return [...getAllParentFields(props.entities, fromEntity.value.parentEntityId), ...fromEntity.value.fields]
  }
  return fromEntity.value.fields
})

const toEntityFields = computed(() => {
  if (!toEntity.value) return []
  if (toEntity.value.parentEntityId) {
    return [...getAllParentFields(props.entities, toEntity.value.parentEntityId), ...toEntity.value.fields]
  }
  return toEntity.value.fields
})
// 获取fromEntity选中的字段
const fromField = computed(() => fromEntityFields.value.find(field => field.id === formData.value.fromFieldId))
// 获取toEntity选中的字段
const toField = computed(() => toEntityFields.value.find(field => field.id === formData.value.toFieldId))

// 计算可以选中的关系类型
const canSelectRelationCategories = computed(() => {
  // 将复杂的关系类型判断逻辑拆分为多个辅助函数
  const getRelationCategoriesForBothFields = () => {
    if((fromField.value?.isPrimaryKey || fromField.value?.isUnique) && 
       (toField.value?.isPrimaryKey || toField.value?.isUnique)) {
      formData.value.relationCategory = RelationshipCategory.ONE_TO_ONE
      return relationCategories.filter(c => c.value === RelationshipCategory.ONE_TO_ONE)
    }
    if(fromField.value?.isPrimaryKey || fromField.value?.isUnique) {
      formData.value.relationCategory = RelationshipCategory.ONE_TO_MANY
      return relationCategories.filter(c => c.value === RelationshipCategory.ONE_TO_MANY)
    }
    if(toField.value?.isPrimaryKey || toField.value?.isUnique) {
      formData.value.relationCategory = RelationshipCategory.MANY_TO_ONE
      return relationCategories.filter(c => c.value === RelationshipCategory.MANY_TO_ONE)
    }
    formData.value.relationCategory = RelationshipCategory.MANY_TO_MANY
    return relationCategories.filter(c => c.value === RelationshipCategory.MANY_TO_MANY)
  }

  const getRelationCategoriesForSingleField = (field: Field | undefined, isFromField: boolean) => {
    if(!field) return relationCategories
    
    if(field.isPrimaryKey || field.isUnique) {
      formData.value.relationCategory = ''
      return relationCategories.filter(c => 
        isFromField ? 
          (c.value === RelationshipCategory.ONE_TO_ONE || c.value === RelationshipCategory.ONE_TO_MANY) :
          (c.value === RelationshipCategory.ONE_TO_ONE || c.value === RelationshipCategory.MANY_TO_ONE)
      )
    }
    
    formData.value.relationCategory = ''
    return relationCategories.filter(c => 
      isFromField ?
        (c.value === RelationshipCategory.MANY_TO_ONE || c.value === RelationshipCategory.MANY_TO_MANY) :
        (c.value === RelationshipCategory.ONE_TO_MANY || c.value === RelationshipCategory.MANY_TO_MANY)
    )
  }

  if(!fromField.value && !toField.value) return relationCategories
  if(fromField.value && toField.value) return getRelationCategoriesForBothFields()
  return getRelationCategoriesForSingleField(
    fromField.value || toField.value,
    Boolean(fromField.value)
  )
})

const relationshipTypeOptions = [ {
  value: 'SOFT',
  label: $t('relation.soft'),
  icon: 'soft-relation'
},
{
  value: 'HARD',
  label: $t('relation.hard'),
  icon: 'hard-relation'
}]

const cascadeOperationOptions = [{
    value: 'NO_ACTION',
    label: $t('relation.noAction')
  },
  {
    value: 'CASCADE',
    label: $t('relation.cascade')
  },
  {
    value: 'SET_NULL',
    label: $t('relation.setNull')
  },
  {
    value: 'SET_DEFAULT',
    label: $t('relation.setDefault')
  },
  {
    value: 'RESTRICT',
    label: $t('relation.restrict')
  }
]

// 计算是否可以保存
const canSave = computed(() => {
  return formData.value.name.trim() && 
         formData.value.fromFieldId && 
         formData.value.toFieldId &&
         fromEntity.value &&
         toEntity.value &&
         formData.value.relationType &&
         canSelectRelationCategories.value.find(category => category.value === formData.value.relationCategory)
})

// 监听fromEntity和toEntity的变化，如果变化了，则设置默认的name
watch([fromEntity, toEntity], ([from, to]) => {
  if (from && to && !formData.value.name) {
    if(allRelationShips.value.some(relationship => relationship.name === `${from.name}_${to.name}_relation`)) {
      const count = allRelationShips.value.filter(relationship => relationship.name?.startsWith(`${from.name}_${to.name}_relation`)).length
      formData.value.name = `${from.name}_${to.name}_relation_` + (count > 0 ? count : '')
    }else{
      formData.value.name = `${from.name}_${to.name}_relation`
    }
  }
}, { immediate: true })

function handleRelationTypeChange() {
  if(formData.value.relationType === 'SOFT') {
    formData.value.cascadeUpdate = ''
    formData.value.cascadeDelete = ''
    formData.value.cascadeCreate = false
  }
}

// 根据FromField和ToField的选择计算是否禁用字段
function shouldDisableField(field: Field, fromField: Field) {
  return field?.type !== fromField?.type || 
         field?.length !== fromField?.length || 
         field?.scale !== fromField?.scale
}

// 判断该字段是否已关联
function hasRelation(entityId: string, fieldId: string) {
  // 把新增的关系排除掉（新增的关系之所以要加到allRelationShips中，是因为需要把新增的关系连线也显示出来）
  let relationShips = allRelationShips.value
  if(!props.relationships.some(relationship => relationship.id === formData.value.id)) {
    relationShips = relationShips.filter(relationship => relationship.id !== formData.value.id)
  }
  return relationShips.some(
    relationship => 
      (relationship.fromFieldId === fieldId && relationship.fromEntityId === entityId) 
      || (relationship.toFieldId === fieldId && relationship.toEntityId === entityId)
  )
}

// 选择fromField
function selectFromField(fieldId: string) {
  // 如果点击的是已选中的字段，则取消选中
  if (formData.value.fromFieldId === fieldId) {
    formData.value.fromFieldId = ''
  } else {
    // 否则选中新字段
    formData.value.fromFieldId = fieldId
  }
}

// 选择toField
function selectToField(fieldId: string) {
  // 如果点击的是已选中的字段，则取消选中
  if (formData.value.toFieldId === fieldId) {
    formData.value.toFieldId = ''
  } else {
    // 否则选中新字段
    formData.value.toFieldId = fieldId
  }
}

// 保存关系
function handleSave() {
  if (!canSave.value || !fromEntity.value || !toEntity.value) return
  
  const relationship: Relationship = {
    id: formData.value.id,
    name: formData.value.name.trim(),
    fromEntityId: fromEntity.value.id,
    toEntityId: toEntity.value.id,
    fromFieldId: formData.value.fromFieldId,
    toFieldId: formData.value.toFieldId,
    type: formData.value.relationType as RelationshipType,
    category: formData.value.relationCategory as RelationshipCategory,
    datasourceId: fromEntity.value.datasourceId,
    x: 0,
    y: 0
  }
  emit('save', relationship)
}

// 设置字段引用
function setFieldRef(entityId: string, fieldId: string, el: HTMLElement | null) {
  if (el) {
    if(!fieldRefs.value[entityId]) {
      fieldRefs.value[entityId] = {}
    }
    fieldRefs.value[entityId][fieldId] = el
  }
}

</script>
<style scoped>
.modal-content {
  display: flex;
  flex-direction: column;
}
.modal-body {
  padding: 0px 30px 0px 30px;
}
.modal-footer {
  padding: 0px 30px 20px 30px;
}
.icon{
  width: 16px;
  height: 16px;
}
.entities-info {
  display: flex;
  max-height: 300px;
  min-height: 0;
  flex-direction: row;
  overflow-y: auto;
  flex-wrap: wrap;
  scrollbar-width: thin;
  align-items: flex-start;
  gap: 20px;
  padding: 20px;
  background: #f6f8fa;
  border-radius: 8px;
  margin-bottom: 20px;
}
.dark-theme .entities-info {
  background: #000000;
}
.entity-card {
  flex: 1;
  flex-shrink: 0;       /* 防止被压缩 */
  background: #fff;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  padding: 16px;
}
.entity-card h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #24292e;
  text-align: center;
}
.dark-theme .entity-card {
  background: #1e1e1e;
  border-color: #333333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
.dark-theme .entity-card h4 {
  color: #ffffff;
  font-weight: 500;
}
.field-option {
  padding: 8px 12px;
  border: 1px solid #e1e4e8;
  border-radius: 4px;
  margin-bottom: 4px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}
.field-left {
  display: flex;
  align-items: center;
  gap: 6px;
}
.field-right {
  display: flex;
  align-items: center;
}
.field-option.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  color: #999;
  background-color: #f5f5f5;
}
.field-option:hover {
  background: #f6f8fa;
  border-color: #0366d6;
}
.field-option.selected {
  background: #e6f7ff;
  border-color: #0366d6;
  color: #0366d6;
}
.dark-theme .field-option {
  border-color: #333333;
  background: #121212;
  color: #ffffff;
  transition: all 0.2s ease;
}
.dark-theme .field-option.disabled {
  color: #666;
  background-color: #2a2a2a;
}
.dark-theme .field-option:hover:not(.disabled) {
  background: #2c2c2c;
  border-color: #bb86fc;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}
.dark-theme .field-option.selected {
  background: #2c2c2c;
  border-color: #bb86fc;
  color: #bb86fc;
  box-shadow: 0 0 0 1px #bb86fc;
}
.relation-type-option {
  display: flex;
  align-items: center;
  padding: 16px;
  border: 2px solid #e1e4e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.relation-type-option:hover {
  border-color: #0366d6;
  background: #f6f8fa;
}
.relation-type-option.selected {
  border-color: #0366d6;
  background: #e6f7ff;
}
.relation-type-option input[type="radio"] {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}
.relation-type-option.disabled {
  cursor: not-allowed;
  opacity: 0.5;
  color: #999;
  background-color: #f5f5f5;
  pointer-events: none; /* 完全禁用点击 */
}
.dark-theme .relation-type-option.disabled {
  color: #666;
  background-color: #2a2a2a;
  opacity: 0.4;
}
.dark-theme .relation-type-option {
  border-color: #333333;
  background: #121212;
  transition: all 0.2s ease;
}
.dark-theme .relation-type-option:hover {
  border-color: #bb86fc;
  background: #2c2c2c;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.dark-theme .relation-type-option.selected {
  border-color: #bb86fc;
  background: #2c2c2c;
  box-shadow: 0 0 0 1px #bb86fc;
}
.type-info {
  text-align: center;
}
.type-name {
  font-size: 16px;
  font-weight: 600;
  color: #24292e;
  margin-bottom: 4px;
}
.type-visual {
  font-size: var(--font-size-base);
  font-family: var(--font-family-ui);
  color: #0366d6;
  font-weight: var(--font-weight-bold);
}
.dark-theme .type-name {
  color: #ffffff;
  font-weight: 500;
}
.dark-theme .type-visual {
  color: #bb86fc;
  font-weight: 600;
}
@media (max-width: var(--mobile-breakpoint)) {
  .entities-info {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
