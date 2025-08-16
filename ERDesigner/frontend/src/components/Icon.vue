<template>
  <component v-if="iconComponent" :class="['icon', name]" :is="iconComponent" v-bind="$attrs" />
</template>

<script setup lang="ts">
import { computed, defineAsyncComponent } from 'vue'

interface Props {
  name: string
}

const props = defineProps<Props>()

// 使用 defineAsyncComponent 实现懒加载的图标映射表
const iconMap: Record<string, any> = {
  'abstract-entity': defineAsyncComponent(() => import('@/icons/AbstractEntityIcon.vue')),
  'add-entity': defineAsyncComponent(() => import('@/icons/AddEntityIcon.vue')),
  'code-view': defineAsyncComponent(() => import('@/icons/CodeViewIcon.vue')),
  'color-entity-border': defineAsyncComponent(() => import('@/icons/ColorEntityBorderIcon.vue')),
  'color-entity-font': defineAsyncComponent(() => import('@/icons/ColorEntityFontIcon.vue')),
  'color-entity': defineAsyncComponent(() => import('@/icons/ColorEntityIcon.vue')),
  'copy': defineAsyncComponent(() => import('@/icons/CopyIcon.vue')),
  'database': defineAsyncComponent(() => import('@/icons/DatabaseIcon.vue')),
  'default-view': defineAsyncComponent(() => import('@/icons/DefaultViewIcon.vue')),
  'delete': defineAsyncComponent(() => import('@/icons/DeleteIcon.vue')),
  'document': defineAsyncComponent(() => import('@/icons/DocumentIcon.vue')),
  'drag-canvas': defineAsyncComponent(() => import('@/icons/DragCanvasIcon.vue')),
  'edit': defineAsyncComponent(() => import('@/icons/EditIcon.vue')),
  'elasticsearch': defineAsyncComponent(() => import('@/icons/ElasticSearchIcon.vue')),
  'export': defineAsyncComponent(() => import('@/icons/ExportDiagramIcon.vue')),
  'format-code': defineAsyncComponent(() => import('@/icons/FormatCodeIcon.vue')),
  'fullscreen': defineAsyncComponent(() => import('@/icons/FullscreenIcon.vue')),
  'grid': defineAsyncComponent(() => import('@/icons/GridIcon.vue')),
  'hide-grid': defineAsyncComponent(() => import('@/icons/HideGridIcon.vue')),
  'hard-relation': defineAsyncComponent(() => import('@/icons/HardRelationIcon.vue')),
  'import': defineAsyncComponent(() => import('@/icons/ImportDiagramIcon.vue')),
  'json': defineAsyncComponent(() => import('@/icons/JsonIcon.vue')),
  'key': defineAsyncComponent(() => import('@/icons/KeyIcon.vue')),
  'mongodb': defineAsyncComponent(() => import('@/icons/MongoDBIcon.vue')),
  'mysql': defineAsyncComponent(() => import('@/icons/MySQLIcon.vue')),
  'mariadb': defineAsyncComponent(() => import('@/icons/MariaDBIcon.vue')),
  'new-view': defineAsyncComponent(() => import('@/icons/NewViewIcon.vue')),
  'nosql': defineAsyncComponent(() => import('@/icons/NoSQLIcon.vue')),
  'oracle': defineAsyncComponent(() => import('@/icons/OracleIcon.vue')),
  'paste': defineAsyncComponent(() => import('@/icons/PasteIcon.vue')),
  'postgresql': defineAsyncComponent(() => import('@/icons/PostgreSQLIcon.vue')),
  'redis': defineAsyncComponent(() => import('@/icons/RedisIcon.vue')),
  'redo': defineAsyncComponent(() => import('@/icons/RedoIcon.vue')),
  'reset-zoom': defineAsyncComponent(() => import('@/icons/ResetZoomIcon.vue')),
  'save': defineAsyncComponent(() => import('@/icons/SaveDiagramIcon.vue')),
  'select-all': defineAsyncComponent(() => import('@/icons/SelectAllIcon.vue')),
  'selection-box': defineAsyncComponent(() => import('@/icons/SelectionBoxIcon.vue')),
  'soft-relation': defineAsyncComponent(() => import('@/icons/SoftRelationIcon.vue')),
  'sqlite': defineAsyncComponent(() => import('@/icons/SQLiteIcon.vue')),
  'sqlserver': defineAsyncComponent(() => import('@/icons/SQLServerIcon.vue')),
  'db2': defineAsyncComponent(() => import('@/icons/DB2Icon.vue')),
  'cubrid': defineAsyncComponent(() => import('@/icons/CubridIcon.vue')),
  'firebird': defineAsyncComponent(() => import('@/icons/FirebirdIcon.vue')),
  'cockroachdb': defineAsyncComponent(() => import('@/icons/CockroachdbIcon.vue')),
  'yugabyte': defineAsyncComponent(() => import('@/icons/YugabyteIcon.vue')),
  'duckdb': defineAsyncComponent(() => import('@/icons/DuckdbIcon.vue')),
  'polardb': defineAsyncComponent(() => import('@/icons/PolardbIcon.vue')),
  'oceanbase': defineAsyncComponent(() => import('@/icons/OceanbaseIcon.vue')),
  'tidb': defineAsyncComponent(() => import('@/icons/TidbIcon.vue')),
  'undo': defineAsyncComponent(() => import('@/icons/UndoIcon.vue')),
  'unique': defineAsyncComponent(() => import('@/icons/UniqueIcon.vue')),
  'required': defineAsyncComponent(() => import('@/icons/RequiredIcon.vue')),
  'view': defineAsyncComponent(() => import('@/icons/ViewIcon.vue')),
  'xml': defineAsyncComponent(() => import('@/icons/XMLIcon.vue')),
  'zoom-in': defineAsyncComponent(() => import('@/icons/ZoomInIcon.vue')),
  'zoom-out': defineAsyncComponent(() => import('@/icons/ZoomOutIcon.vue')),
  'expand': defineAsyncComponent(() => import('@/icons/ExpandIcon.vue')),
  'picture': defineAsyncComponent(() => import('@/icons/PictureIcon.vue')),
  'at': defineAsyncComponent(() => import('@/icons/AtIcon.vue')),
  'microphone': defineAsyncComponent(() => import('@/icons/MicrophoneIcon.vue')),
  'send-prompt': defineAsyncComponent(() => import('@/icons/SendPromptIcon.vue')),
  'sidebar': defineAsyncComponent(() => import('@/icons/SidebarIcon.vue')),
  'sidebar-visible': defineAsyncComponent(() => import('@/icons/SidebarVisibleIcon.vue')),
  'split-screen': defineAsyncComponent(() => import('@/icons/SplitScreenIcon.vue')),
  'split-screen-visible': defineAsyncComponent(() => import('@/icons/SplitScreenVisibleIcon.vue')),
  'github': defineAsyncComponent(() => import('@/icons/GithubIcon.vue'))
}

const iconComponent = computed(() => {
  return iconMap[props.name] || ''
})
</script>

<style scoped>
/* 图标样式 */
.icon {
  width: 100%;
  height: 100%;
  vertical-align: middle;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.code-view{
  width: 24px;
  height: 20px;
}

.default-view{
  width: 24px;
  height: 20px;
}

.key-icon {
  font-size: 15px;
  user-select: none;
  pointer-events: none;
}

.github {
  font-size: 16px;
  color: #24292e;
}
.dark-theme .github {
  color: #fff;
}
</style>