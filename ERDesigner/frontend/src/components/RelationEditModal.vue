<template>
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal" @click.stop>
      <div class="modal-header">
        <h3>创建关系</h3>
        <button @click="$emit('close')" class="close-btn">×</button>
      </div>
      
      <div class="modal-content">
        <div class="entities-info">
          <div class="entity-card">
            <h4>{{ fromEntity?.name || '实体1' }}</h4>
            <div class="entity-fields">
              <div 
                v-for="field in fromEntity?.fields" 
                :key="field.id"
                class="field-option"
                :class="{ selected: formData.fromFieldId === field.id }"
                @click="selectFromField(field.id)"
              >
                <span v-if="field.isPrimaryKey" class="key-icon">🔑</span>
                {{ field.name }} ({{ field.type }})
              </div>
            </div>
          </div>
          
          <div class="relation-arrow">
            <div class="arrow-line"></div>
            <div class="arrow-head"></div>
          </div>
          
          <div class="entity-card">
            <h4>{{ toEntity?.name || '实体2' }}</h4>
            <div class="entity-fields">
              <div 
                v-for="field in toEntity?.fields" 
                :key="field.id"
                class="field-option"
                :class="{ selected: formData.toFieldId === field.id }"
                @click="selectToField(field.id)"
              >
                <span v-if="field.isPrimaryKey" class="key-icon">🔑</span>
                {{ field.name }} ({{ field.type }})
              </div>
            </div>
          </div>
        </div>
        
        <div class="relation-config">
          <div class="form-group">
            <label>关系名称:</label>
            <input 
              v-model="formData.name" 
              placeholder="请输入关系名称"
            />
          </div>
          
          <div class="form-group">
            <label>关系类型:</label>
            <div class="relation-types">
              <label 
                v-for="type in relationTypes" 
                :key="type.value"
                class="relation-type-option"
                :class="{ selected: formData.relationType === type.value }"
              >
                <input 
                  type="radio" 
                  :value="type.value" 
                  v-model="formData.relationType"
                />
                <div class="type-info">
                  <div class="type-name">{{ type.label }}</div>
                  <div class="type-desc">{{ type.description }}</div>
                  <div class="type-visual">{{ type.visual }}</div>
                </div>
              </label>
            </div>
          </div>
        </div>
      </div>
        <button @click="$emit('close')" class="btn-secondary">取消</button>
        <button @click="handleSave" class="btn-primary" :disabled="!canSave">创建关系</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { Entity, Relation } from '../types/entity'

interface Props {
  entities: Entity[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [relation: Relation]
  close: []
}>()

const formData = ref({
  name: '',
  fromFieldId: '',
  toFieldId: '',
  relationType: 'one-to-many' as 'one-to-one' | 'one-to-many' | 'many-to-many'
})

const relationTypes = [
  {
    value: 'one-to-one',
    label: '一对一',
    description: '每个记录只能对应一个记录',
    visual: '1 ——— 1'
  },
  {
    value: 'one-to-many',
    label: '一对多',
    description: '一个记录可以对应多个记录',
    visual: '1 ——< ∞'
  },
  {
    value: 'many-to-many',
    label: '多对多',
    description: '多个记录可以对应多个记录',
    visual: '∞ >—< ∞'
  }
]

const fromEntity = computed(() => props.entities[0])
const toEntity = computed(() => props.entities[1])

const canSave = computed(() => {
  return formData.value.name.trim() && 
         formData.value.fromFieldId && 
         formData.value.toFieldId &&
         fromEntity.value &&
         toEntity.value
})

watch([fromEntity, toEntity], ([from, to]) => {
  if (from && to && !formData.value.name) {
    formData.value.name = `${from.name}_${to.name}`
  }
}, { immediate: true })

function selectFromField(fieldId: string) {
  formData.value.fromFieldId = fieldId
}

function selectToField(fieldId: string) {
  formData.value.toFieldId = fieldId
}

function handleSave() {
  if (!canSave.value || !fromEntity.value || !toEntity.value) return
  
  const relation: Relation = {
    id: Date.now().toString(),
    name: formData.value.name.trim(),
    fromEntityId: fromEntity.value.id,
    toEntityId: toEntity.value.id,
    fromFieldId: formData.value.fromFieldId,
    toFieldId: formData.value.toFieldId,
    relationType: formData.value.relationType
  }
  
  emit('save', relation)
}

function handleOverlayClick() {
  emit('close')
}
</script>
