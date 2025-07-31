<template>
  <div
    v-if="show"
    class="context-menu"
    :style="{ left: x + 'px', top: y + 'px' }"
    @click.stop>
    <template v-if="type === 'CANVAS'">
      <div class="context-menu-item" @click="onCreateEntity">
        <span class="icon"><Icon name="add-entity"/></span>
        {{ $t('contextMenu.addEntity') }}
      </div>
      <div class="context-menu-separator"></div>
      <div class="context-menu-item" @click="onPaste" :class="{ disabled: !canPaste }">
        <span class="icon"><Icon name="paste"/></span>
        {{ $t('contextMenu.paste') }}
      </div>
      <div class="context-menu-item" @click="onSelectAll">
        <span class="icon"><Icon name="select-all"/></span>
        {{ $t('contextMenu.selectAll') }}
      </div>
    </template>
    <template v-else-if="type === 'ENTITY'">
      <div class="context-menu-item" @click="onEditEntity" :class="{ disabled: isMultiSelect }">
        <span class="icon"><Icon name="edit"/></span>
        {{ $t('contextMenu.edit') }}
      </div>
      <div class="context-menu-item" @click="onCopyEntity">
        <span class="icon"><Icon name="copy"/></span>
        {{ $t('contextMenu.copy') }}
      </div>
      <div class="context-menu-item" @click="onDeleteEntity">
        <span class="icon"><Icon name="delete"/></span>
        {{ $t('contextMenu.delete') }}
      </div>
    </template>
    <template v-else-if="type === 'DATASOURCE'">
      <div class="context-menu-item" @click="onEditDatasource">
        <span class="icon"><Icon name="edit"/></span>
        {{ $t('datasource.editDatasource') }}
      </div>
      <div class="context-menu-item" @click="onCreateEntityFromTree">
        <span class="icon"><Icon name="add-entity"/></span>
        {{ $t('datasource.addEntity') }}
      </div>
      <div class="context-menu-item" @click="onDeleteDatasource">
        <span class="icon"><Icon name="delete"/></span>
        {{ $t('datasource.deleteDatasource') }}
      </div>
    </template>
    <template v-else-if="type === 'VIEW'">
      <div class="context-menu-item" @click="onDeleteView">
        <span class="icon"><Icon name="delete"/></span>
        {{ $t('view.deleteView') }}
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { useI18n } from 'vue-i18n'
import type { Entity } from '../types/entity'
import Icon from '@/components/Icon.vue'

const props = defineProps<{
  show: boolean
  x: number
  y: number
  canPaste?: boolean
  type: 'CANVAS' | 'ENTITY' | 'DATASOURCE' | 'VIEW'
  targetId?: string
  entities?: Entity[]
  isMultiSelect?: boolean
}>()

const emit = defineEmits([
  'createEntity', 'paste', 'selectAll',
  'editEntity', 'copyEntity', 'deleteEntity',
  'editDatasource', 'deleteDatasource', 'createEntityFromTree',
  'deleteView'
])

const { t: $t } = useI18n()
function onCreateEntity() { emit('createEntity') }
function onPaste() { if (props.canPaste) emit('paste') }
function onSelectAll() { emit('selectAll', props.entities) }
function onEditEntity() { if (!props.isMultiSelect) emit('editEntity', props.targetId) }
function onCopyEntity() { emit('copyEntity', props.targetId) }
function onDeleteEntity() { emit('deleteEntity', props.targetId) }
function onEditDatasource() { emit('editDatasource', props.targetId) }
function onDeleteDatasource() { emit('deleteDatasource', props.targetId) }
function onCreateEntityFromTree() { emit('createEntityFromTree', props.targetId) }
function onDeleteView() { emit('deleteView', props.targetId) }
</script>

<style scoped>
.context-menu {
  position: fixed;
  z-index: 1200;
  min-width: 160px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.12);
  padding: 4px 0;
  font-size: 14px;
  color: #222;
}
.context-menu, .context-menu * {
  user-select: none;
  -webkit-user-select: none; /* Safari */
  -ms-user-select: none;     /* IE10+/Edge */
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
  width: 16px;
  height: 16px;
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
.dark-theme .context-menu-item.disabled,
.dark-theme .context-menu-item[disabled] {
  color: #777777;
  cursor: not-allowed;
}
.dark-theme .context-menu-separator {
  background: #404040;
}
</style> 