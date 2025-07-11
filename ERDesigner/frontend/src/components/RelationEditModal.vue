<template>
  <div class="modal-overlay">
    <div class="modal-content" @click.stop>
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
import type { Entity, Relationship } from '../types/entity'

interface Props {
  entities: Entity[]
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [relationship: Relationship]
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
  
  const relationship: Relationship = {
    id: Date.now().toString(),
    name: formData.value.name.trim(),
    fromEntityId: fromEntity.value.id,
    toEntityId: toEntity.value.id,
    fromFieldId: formData.value.fromFieldId,
    toFieldId: formData.value.toFieldId,
    type: formData.value.relationType
  }
  
  emit('save', relationship)
}

</script>
<style scoped>
/* 实体相关样式 */

.entity-card {
  flex: 1;
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
  gap: 6px;
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

/* 关系相关样式 */
.relation-config {
  background: #fff;
  border: 1px solid #e1e4e8;
  border-radius: 8px;
  padding: 20px;
}

.relation-types {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.relation-type-option {
  display: block;
  padding: 16px;
  border: 2px solid #e1e4e8;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
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

.type-info {
  text-align: center;
}

.type-name {
  font-size: 16px;
  font-weight: 600;
  color: #24292e;
  margin-bottom: 4px;
}

.type-desc {
  font-size: 12px;
  color: #586069;
  margin-bottom: 8px;
}

.type-visual {
  font-size: 14px;
  font-family: monospace;
  color: #0366d6;
  font-weight: bold;
}

.entity-field {
  padding: 1px 4px;
  font-size: 7px;
  max-height: 200px;
  overflow-y: auto;
}
.entities-info {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding: 20px;
  background: #f6f8fa;
  border-radius: 8px;
}
.relation-arrow {
  display: flex;
  align-items: center;
  position: relative;
  min-width: 60px;
}
.arrow-line {
  width: 40px;
  height: 2px;
  background: #586069;
}
.arrow-head {
  width: 0;
  height: 0;
  border-left: 8px solid #586069;
  border-top: 4px solid transparent;
  border-bottom: 4px solid transparent;
}

@media (max-width: var(--mobile-breakpoint)) {
  .entities-info {
    flex-direction: column;
    gap: 16px;
  }
  .relation-arrow {
    transform: rotate(90deg);
  }
  .relation-types {
    grid-template-columns: 1fr;
  }
}

/* 暗色主题 - 关系相关 */
.dark-theme .entity-card {
  background: #1e1e1e;
  border-color: #333333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.dark-theme .entity-card h4 {
  color: #ffffff;
  font-weight: 500;
}

.dark-theme .field-option {
  border-color: #333333;
  background: #121212;
  color: #ffffff;
  transition: all 0.2s ease;
}

.dark-theme .field-option:hover {
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

.dark-theme .relation-config {
  background: #1e1e1e;
  border-color: #333333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
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

.dark-theme .type-name {
  color: #ffffff;
  font-weight: 500;
}

.dark-theme .type-desc {
  color: #e1e4e8;
}

.dark-theme .type-visual {
  color: #bb86fc;
  font-weight: 600;
}
</style>
