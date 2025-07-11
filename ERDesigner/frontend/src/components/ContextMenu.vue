<template>
  <div
    v-if="show"
    class="context-menu"
    :style="{ left: x + 'px', top: y + 'px' }"
    @click.stop>
    <template v-if="type === 'canvas'">
      <div class="context-menu-item" @click="onAddEntity">
        <span class="icon">📦</span>
        {{ $t('contextMenu.addEntity') }}
      </div>
      <div class="context-menu-separator"></div>
      <div class="context-menu-item" @click="onPaste" :class="{ disabled: !canPaste }">
        <span class="icon">📋</span>
        {{ $t('contextMenu.paste') }}
      </div>
      <div class="context-menu-item" @click="onSelectAll">
        <span class="icon">⊞</span>
        {{ $t('contextMenu.selectAll') }}
      </div>
    </template>
    <template v-else-if="type === 'entity'">
      <div class="context-menu-item" @click="onEditEntity">
        <span class="icon">✏️</span>
        {{ $t('contextMenu.edit') }}
      </div>
      <div class="context-menu-item" @click="onCopyEntity">
        <span class="icon">📄</span>
        {{ $t('contextMenu.copy') }}
      </div>
      <div class="context-menu-item" @click="onDeleteEntity">
        <span class="icon">🗑️</span>
        {{ $t('contextMenu.delete') }}
      </div>
      <div class="context-menu-separator"></div>
      <div class="context-menu-item" @click="onBringToFront">
        <span class="icon">⬆️</span>
        {{ $t('contextMenu.bringToFront') }}
      </div>
      <div class="context-menu-item" @click="onSendToBack">
        <span class="icon">⬇️</span>
        {{ $t('contextMenu.sendToBack') }}
      </div>
    </template>
    <template v-else-if="type === 'datasource'">
      <div class="context-menu-item" @click="onEditDatasource">
        <span class="icon">✏️</span>
        {{ $t('datasource.editDatasource') }}
      </div>
      <div class="context-menu-item" @click="onCreateEntity">
        <span class="icon">➕</span>
        {{ $t('datasource.addEntity') }}
      </div>
      <div class="context-menu-item danger" @click="onDeleteDatasource">
        <span class="icon">🗑️</span>
        {{ $t('datasource.deleteDatasource') }}
      </div>
    </template>
    <template v-else-if="type === 'db-entity'">
      <div class="context-menu-item" @click="onEditDbEntity">
        <span class="icon">✏️</span>
        {{ $t('entity.editEntity') }}
      </div>
      <div class="context-menu-item danger" @click="onDeleteDbEntity">
        <span class="icon">🗑️</span>
        {{ $t('entity.deleteEntity') }}
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { useI18n } from 'vue-i18n'
const props = defineProps<{
  show: boolean
  x: number
  y: number
  canPaste?: boolean
  type: 'canvas' | 'entity' | 'datasource' | 'db-entity'
  target?: any
}>()
const emit = defineEmits([
  'addEntity', 'paste', 'selectAll',
  'editEntity', 'copyEntity', 'deleteEntity', 'bringToFront', 'sendToBack',
  'editDatasource', 'deleteDatasource', 'createEntity', 'editDbEntity', 'deleteDbEntity'
])
const { t: $t } = useI18n()
function onAddEntity() { emit('addEntity') }
function onPaste() { if (props.canPaste) emit('paste') }
function onSelectAll() { emit('selectAll') }
function onEditEntity() { emit('editEntity') }
function onCopyEntity() { emit('copyEntity') }
function onDeleteEntity() { emit('deleteEntity') }
function onBringToFront() { emit('bringToFront') }
function onSendToBack() { emit('sendToBack') }
function onEditDatasource() { emit('editDatasource', props.target?.id) }
function onDeleteDatasource() { emit('deleteDatasource', props.target?.id) }
function onCreateEntity() { emit('createEntity', props.target?.id) }
function onEditDbEntity() { emit('editDbEntity', props.target?.id) }
function onDeleteDbEntity() { emit('deleteDbEntity', props.target?.id) }
</script>

<style scoped>
.context-menu {
  position: fixed;
  z-index: 1000;
  min-width: 120px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  padding: 4px 0;
  font-size: 14px;
  color: #222;
}
.context-menu-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.context-menu-item:hover {
  background: #f5f7fa;
}
.context-menu-item:hover:not(.disabled) {
  background: #f5f7fa;
}
.context-menu-item.disabled,
.context-menu-item[disabled] {
  color: #959da5;
  cursor: not-allowed;
}
.context-menu-item.danger {
  color: #d73a49;
}
.context-menu-item.danger:hover {
  background: #ffeef0;
}
.context-menu-separator {
  height: 1px;
  background: #e1e4e8;
  margin: 4px 0;
}
.icon {
  margin-right: 8px;
}
/* 暗色主题 */
.dark-theme .context-menu {
  background: #2a2a2a;
  border: 1px solid #404040;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}
.dark-theme .context-menu-item {
  color: #ffffff;
}
.dark-theme .context-menu-item:hover {
  background: #333333;
}
.dark-theme .context-menu-item[disabled] {
  color: #777777;
  cursor: not-allowed;
}
.dark-theme .context-menu-separator {
  background: #404040;
}
</style> 