<template>
  <aside class="property-panel" :class="{ 'property-panel-hidden': hidden }">
    <div class="panel-header">
      <h3>{{ title }}</h3>
      <button @click="$emit('close')" class="close-btn">×</button>
    </div>
    <div class="panel-content">
      <div v-if="selectedEntities.length === 1" class="entity-properties">
        <div class="property-section">
          <h4>{{ t('panel.basicInfo') }}</h4>
          <div class="form-group">
            <label>{{ t('entity.name') }}:</label>
            <input v-model="selectedEntities[0].name" @input="updateEntity" />
          </div>
          <div class="form-group">
            <label>{{ t('entity.comment') }}:</label>
            <textarea v-model="selectedEntities[0].comment" @input="updateEntity" rows="2"></textarea>
          </div>
        </div>
        <div class="property-section">
          <h4>{{ t('panel.fields') }} ({{ selectedEntities[0].fields.length }})</h4>
          <div class="fields-list">
            <div v-for="field in selectedEntities[0].fields" :key="field.id" class="property-field-row">
              <div class="property-field-info">
                <div class="field-name">
                  <span v-if="field.isPrimaryKey" class="key-icon">🔑</span>
                  {{ field.name }}
                </div>
                <div class="field-type">{{ field.type }}</div>
              </div>
              <button @click="editField(field)" class="edit-field-btn">✏️</button>
            </div>
          </div>
          <button @click="addField" class="add-field-btn">+ {{ t('field.add') }}</button>
        </div>
        <div class="property-section">
          <h4>{{ t('panel.style') }}</h4>
          <div class="form-group">
            <label>{{ t('panel.backgroundColor') }}:</label>
            <input type="color" v-model="selectedEntities[0].backgroundColor" @input="updateEntity" />
          </div>
          <div class="form-group">
            <label>{{ t('panel.borderColor') }}:</label>
            <input type="color" v-model="selectedEntities[0].borderColor" @input="updateEntity" />
          </div>
        </div>
      </div>
      <div v-else-if="selectedEntities.length > 1" class="multi-selection">
        <h4>{{ t('panel.multiSelection') }} ({{ selectedEntities.length }} {{ t('panel.entities') }})</h4>
        <button @click="alignLeft" class="align-btn">{{ t('alignment.alignLeft') }}</button>
        <button @click="alignCenter" class="align-btn">{{ t('alignment.alignCenter') }}</button>
        <button @click="alignRight" class="align-btn">{{ t('alignment.alignRight') }}</button>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import type { Entity } from '../types/entity'

const props = defineProps<{
  title: string
  hidden?: boolean
  selectedEntities: Entity[]
  t: Function
  updateEntity: () => void
  editField: (field: any) => void
  addField: () => void
  alignLeft: () => void
  alignCenter: () => void
  alignRight: () => void
}>()
const { selectedEntities, t, updateEntity, editField, addField, alignLeft, alignCenter, alignRight } = props
</script>

<style scoped>
.property-panel {
  width: var(--property-panel-width);
  background: #fff;
  border-left: 1px solid #e4e7ed;
  height: 100%;
  display: flex;
  position:relative;
  right: 0;
  flex-direction: column;
  transition: transform 0.2s;
}
.property-panel-hidden {
  display: none;
}
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #e4e7ed;
}
.panel-content {
  flex: 1;
  overflow: auto;
  padding: 16px;
}
.entity-properties {
  padding: 16px;
}

.property-section {
  margin-bottom: 24px;
}

.property-section h4 {
  margin: 0 0 12px 0;
  font-size: 13px;
  font-weight: 600;
  color: #24292e;
}

.property-field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-bottom: 1px solid #f6f8fa;
}

.property-field-row:last-child {
  border-bottom: none;
}

.property-field-info {
  flex: 1;
}

.add-field-btn {
  /* Add appropriate styles for add field button */
}
.multi-selection {
  /* Add appropriate styles for multi-selection section */
}
.align-btn {
  /* Add appropriate styles for align button */
}
</style> 