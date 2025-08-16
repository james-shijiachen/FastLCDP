<template>
  <div class="modal-overlay">
    <div class="modal-content" ref="modalRef" @click="handleOverlayClick" @wheel.prevent="handleModalWheel">
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
          :options="[{ value: 'ENTITY', label: $t('entity.table'), icon: 'add-entity' }, { value: 'ABSTRACT', label: $t('entity.abstract'), icon: 'abstract-entity' }]"/>
        <RadioButton 
          v-model="formData.datasourceId" 
          field="entity.datasourceId" 
          component="EntityEditModal" 
          :label="$t('datasource.name')"
          :options="props.datasources.map(ds => ({ value: ds.id, label: ds.name, icon: iconMap[ds.category as keyof typeof iconMap || DatasourceType.DATABASE]}))"/>
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
                  <col style="width: 5vh;" />  <!-- 操作 -->
                  <col style="width: 15vh;" /> <!-- 字段名 -->
                  <col style="width: 15vh;" /> <!-- 类型 -->
                  <col style="width: 5vh;" /> <!-- 长度 -->
                  <col style="width: 5vh;" /> <!-- 精度 -->
                  <col style="width: 2vh;" />  <!-- 主键 -->
                  <col style="width: 2vh;" />  <!-- 自增 -->
                  <col style="width: 2vh;" />  <!-- 必填 -->
                  <col style="width: 2vh;" />  <!-- 唯一 -->
                  <col style="width: 25vh;" /> <!-- 注释 -->
                  <col style="width: 10vh;" /> <!-- 继承 -->
                  <col style="width: 12vh;" /> <!-- 关联 -->
              </colgroup>
              <thead class="fields-table-header">
                <tr>
                  <th class="sticky-column"></th>
                  <th class="sticky-column">{{ $t('entity.fieldName') }} <span class="required">*</span></th>
                  <th>{{ $t('entity.dataType') }} <span class="required">*</span></th>
                  <th>{{ $t('entity.length') }}</th>
                  <th>{{ $t('entity.scale') }}</th>
                  <th>{{ $t('entity.primaryKey') }}</th>
                  <th>{{ $t('entity.autoIncrement') }}</th>
                  <th>{{ $t('entity.required') }}</th>
                  <th>{{ $t('entity.unique') }}</th>
                  <th>{{ $t('entity.comment') }}</th>
                  <th>{{ $t('entity.extended') }}</th>
                  <th>{{ $t('entity.foreignKey') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr class="field-row" v-if="allParentField && allParentField.length > 0" v-for="field in allParentField" :key="field.id">
                  <td class="sticky-column"></td>
                  <td class="sticky-column"><input :disabled="true" :value="field.name" /></td>
                  <td><input :disabled="true" :value="field.type" /></td>
                  <td><input :disabled="true" :value="field.length" /></td>
                  <td><input :disabled="true" :value="field.scale" /></td>
                  <td><input type="checkbox" :disabled="true" :checked="field.isPrimaryKey" /></td>
                  <td><input type="checkbox" :disabled="true" :checked="field.isAutoIncrement" /></td>
                  <td><input type="checkbox" :disabled="true" :checked="field.isRequired" /></td>
                  <td><input type="checkbox" :disabled="true" :checked="field.isUnique" /></td>
                  <td><textarea :disabled="true" :value="field.comment" /></td>
                  <td>&nbsp;{{ field.extended === undefined || field.extended.entityId === '' ? '' 
                  : (entityNameCache[field.extended?.entityId] + '.' + fieldNameCache[field.extended?.fieldId]) }}</td>
                  <td class="td-foreign-key">
                    <button class="span-foreign-key" v-for="foreignKeyInfo in getForeignKeyInfoForParentField(field)">
                      <span class="text">
                        {{foreignKeyInfo}}
                      </span>
                      <span class="delete-btn" @click="removeForeignKeyByParentField(props.entity?.id || '', field.id)">X</span>
                    </button>
                  </td>
                </tr>
                <tr class="field-row" 
                    v-for="(field, index) in formData.fields" 
                    :key="field.id"
                    draggable="true"
                    @dragstart.stop="handleDragStart($event, index)"
                    @dragover="handleDragOver($event)"
                    @dragenter="handleDragEnter($event, index)"
                    @dragleave="handleDragLeave($event)"
                    @drop="handleDrop($event, index)"
                    @dragend="handleDragEnd()"
                    :class="{ 'dragging': draggedIndex === index, 'drag-over': dragOverIndex === index }">
                  <td class="td-actions sticky-column">
                    <button @click="addField(index)" class="add-btn" :title="$t('entity.addField')">+</button>
                    <button v-if="!field.foreignKey?.length" @click="removeField(index)" class="remove-btn" :title="$t('entity.removeField')">X</button>
                  </td>
                  <td class="sticky-column"><input :title="field.name" v-model="field.name" :placeholder="$t('entity.fieldName')" /></td>
                  <td>
                    <input v-if="field.foreignKey !== undefined" :disabled="true" :value="field.type" />
                    <VueSelect
                        v-else
                        :ref="(el: any) => { if (el) vueSelectRefs[index] = el }"
                        v-model="field.type"
                        :options="getDataTypeOptions()"
                        :clearable="false"
                        :searchable="false"
                        :append-to-body="true"
                        :style="{ width: calculateSelectWidth(field.type) + 'px' }"
                        @update:model-value="handleDataTypeChange(field)">
                      <template #option="{ label, commonly }">
                        <span :class="{ 'commonly-used': commonly, 'rarely-used': !commonly }">
                          {{ label }}
                        </span>
                      </template>
                    </VueSelect>
                  </td>
                  <td>
                    <input 
                      v-model="field.length" 
                      v-model.number="field.length" 
                      :style="!(field.foreignKey !== undefined || !needsLength(getColumnTypeOption(field.type))) ? { 'width': 'auto', 'min-width': '60px', 'max-width': '100px' } : {}"
                      :disabled="field.foreignKey !== undefined || !needsLength(getColumnTypeOption(field.type))" 
                      :max="getMaxLength(getColumnTypeOption(field.type))"
                      @keydown="handleNumberKeydown($event)"
                       @compositionstart="handleCompositionStart($event)"
                       @compositionend="handleCompositionEnd($event)"
                      @blur="handleLengthInput(field)"
                      type="number"
                      min="1" />
                  </td>
                  <td>
                    <input 
                      v-model="field.scale" 
                      v-model.number="field.scale" 
                      :style="{ 'width': '50px' }"
                      :disabled="field.foreignKey !== undefined || !needsScale(getColumnTypeOption(field.type))" 
                      :max="getMaxScale(getColumnTypeOption(field.type))"
                      @keydown="handleNumberKeydown($event)"
                      @compositionstart="handleCompositionStart($event)"
                      @compositionend="handleCompositionEnd($event)"
                      @blur="handleScaleInput(field)"
                      type="number"
                      min="0" />
                  </td>
                  <td><input type="checkbox" v-model="field.isPrimaryKey" @change="handlePrimaryKeyChange(field)"/></td>
                  <td><input type="checkbox" v-model="field.isAutoIncrement"/></td>
                  <td><input type="checkbox" v-model="field.isRequired" :disabled="field.isPrimaryKey" /></td>
                  <td><input type="checkbox" v-model="field.isUnique" :disabled="field.isPrimaryKey" /></td>
                  <td><textarea :title="field.comment" v-model="field.comment" rows="2"></textarea></td>
                  <td class="td-source-field">                    
                      {{ field.extended === undefined || field.extended.entityId === '' || field.extended.entityId === formData?.entityId ? '' 
                      : (entityNameCache[field.extended?.entityId] + '.' + fieldNameCache[field.extended?.fieldId]) }}
                  </td>
                  <td class="td-foreign-key">
                    <button class="span-foreign-key" v-for="(ref) in field.foreignKey">
                      <span class="text">
                        {{ ref.referencedEntityId === '' ? '' 
                        : (entityNameCache[ref.referencedEntityId] + '.' + fieldNameCache[ref.referencedFieldId]) }}
                      </span>
                      <span class="delete-btn" @click="removeForeignKeyByForeignKey(ref, index)">X</span>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button @click="$emit('close')" class="btn btn-secondary">{{ $t('common.cancel') }}</button>
        <button @click="handleSave" class="btn btn-primary" :disabled="!canSave">
          {{ isEdit ? $t('common.save') : $t('common.create') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import VueSelect from 'vue-select'
import 'vue-select/dist/vue-select.css'
import type { Entity, Field, Datasource, Relationship } from '../types/entity'
import { EntityType, DatasourceType, DatasourceCategory, ColumnTypeOption } from '../types/entity'
import {MysqlColumnType, SQLiteColumnType, PostgreSQLColumnType, DB2ColumnType, FirebirdColumnType, CockroachDBColumnType,
  SqlServerColumnType, OracleColumnType, DefaultDatabaseColumnType, MariaDBColumnType, DuckDBColumnType,
PolarDBColumnType, OceanBaseColumnType, TiDBColumnType, YugabyteColumnType, CubridColumnType} from '../types/columnTypes'
import { useDraggableModal } from '@/utils/useDraggableModal'
import { ValidateField, RadioButton } from '@/components'
import { useFieldError } from '@/utils/useFieldError'
import { getChildEntityIds, getAllParentFields } from '@/utils/datasourceUtil'
import { deepClone } from '@/utils/commons'


const {clearFieldError, setFieldError, getFieldError } = useFieldError('EntityEditModal')
const { t: $t } = useI18n()
const { modalRef, onHeaderMousedown } = useDraggableModal()
const vueSelectRefs = ref<typeof VueSelect[]>([])

const iconMap = {
  [DatasourceType.DATABASE]: 'database',
  [DatasourceCategory.SQLITE]: 'sqlite',
  [DatasourceCategory.MYSQL]: 'mysql',
  [DatasourceCategory.ORACLE]: 'oracle',
  [DatasourceCategory.POSTGRESQL]: 'postgresql',
  [DatasourceCategory.SQLSERVER]: 'sqlserver',
  [DatasourceCategory.REDIS]: 'redis',
  [DatasourceCategory.MONGODB]: 'mongodb',
  [DatasourceCategory.ELASTICSEARCH]: 'elasticsearch',
  [DatasourceCategory.JSON]: 'json',
  [DatasourceCategory.XML]: 'xml'
}

const parentEntityIconMap = {
  [EntityType.ENTITY]: 'add-entity',
  [EntityType.ABSTRACT]: 'abstract-entity'
}

interface Props {
  entity?: Entity | null
  entities: Entity[]
  entityNameCache: Record<string, string>
  fieldNameCache: Record<string, string>
  relationships: Relationship[]
  parentEntity?: Entity | null
  datasources: Datasource[]
  currentDatasourceId?: string
}

const props = defineProps<Props>()

const emit = defineEmits<{
  save: [entity: Entity]
  close: []
  deleteRelation: [relationshipId: string]
}>()

const modalContentRef = ref<HTMLDivElement | null>(null)
const nameInputRef = ref<InstanceType<typeof ValidateField> | null>(null)
const fieldsListWrapperRef = ref<HTMLDivElement | null>(null)
const currentParentEntity = ref<Entity | undefined>(props.parentEntity || undefined)
const showHeaderName = ref(false)

// 拖拽相关状态
const draggedIndex = ref<number | null>(null)
const dragOverIndex = ref<number | null>(null)
const autoScrollTimer = ref<NodeJS.Timeout | null>(null)

// 表单数据
const formData = ref({
  entityId: props.entity?.id || '',
  name: props.entity?.name || '',
  comment: props.entity?.comment || '',
  datasourceId: props.entity?.datasourceId || props.currentDatasourceId || '',
  entityType: props.entity?.entityType || 'ENTITY',
  parentEntityId: props.entity?.parentEntityId || props.parentEntity?.id || '',
  parentEntityName: props.parentEntity?.name || '',
  fields: props.entity?.fields ? deepClone(props.entity.fields) : [
            {
              entityId: props.entity?.id || '',
              id: Date.now().toString(),
              serialNo: 1,
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
  return getFieldError('entity.name') === '' && formData.value.datasourceId.length > 0 && formData.value.fields.every(field => {
    const fieldType = typeof field.type === 'string' ? field.type : (field.type as any)?.value || ''
    return field.name.trim().length > 0 && fieldType.trim().length > 0
  })
})

function validateName() {
  if (!formData.value.name.trim()) {
    setFieldError('entity.name', $t('entity.nameRequired'))
  } else if (formData.value.name.length > 50) {
    setFieldError('entity.name', $t('entity.nameMaxLength'))
  } else if (!/^[a-zA-Z]\w*$/.test(formData.value.name)) {
    setFieldError('entity.name', $t('entity.namePattern'))
  } else {
    clearFieldError('entity.name')
  }
}

// 是否可以保存
const canSave = computed(() => {
  return formData.value.name.trim().length > 0 && formData.value.datasourceId.length > 0 && formData.value.fields.every(field => {
    const fieldType = typeof field.type === 'string' ? field.type : (field.type as any)?.value || ''
    return field.name.trim().length > 0 && fieldType.trim().length > 0
  })
})

const allParentField = computed(() => {
  // 获取父实体字段
  if (currentParentEntity.value) {
    return getAllParentFields(props.entities, currentParentEntity.value.id)
  }
  if (props.entity && props.entity.parentEntityId) {
    return getAllParentFields(props.entities, props.entity.parentEntityId)
  }
  return []
})

// 主键变化时，唯一和必填都为true
function handlePrimaryKeyChange(field: Field) {
  if (field.isPrimaryKey) {
    field.isUnique = true
    field.isRequired = true
  }
}

// 获取当前数据源的数据类型选项
function getDataTypeOptions() {
  
  const currentDatasource = props.datasources.find(ds => ds.id === formData.value.datasourceId)

  if (currentDatasource?.category === DatasourceCategory.MYSQL) {
    return Object.values(MysqlColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.SQLITE) {
    return Object.values(SQLiteColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.POSTGRESQL) {
    return Object.values(PostgreSQLColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.ORACLE) {
    return Object.values(OracleColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.SQLSERVER) {
    return Object.values(SqlServerColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.MARIADB) {
    return Object.values(MariaDBColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.DB2) {
    return Object.values(DB2ColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.FIREBIRD) {
    return Object.values(FirebirdColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.COCKROACHDB) {
    return Object.values(CockroachDBColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.DUCKDB) {
    return Object.values(DuckDBColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.POLARDB) {
    return Object.values(PolarDBColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.OCEANBASE) {
    return Object.values(OceanBaseColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.TIDB) {
    return Object.values(TiDBColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.YUGABYTE) {
    return Object.values(YugabyteColumnType)
  }else if(currentDatasource?.category === DatasourceCategory.CUBRID) {
    return Object.values(CubridColumnType)
  }

  if(currentDatasource?.type === DatasourceType.DATABASE) {
    return Object.values(DefaultDatabaseColumnType)
  }
  
  // 如果没有找到数据源，返回默认数据库类型
  return Object.values(DefaultDatabaseColumnType)
}

// 根据数据类型获取对应的 ColumnTypeOption 对象
function getColumnTypeOption(dataType: string | ColumnTypeOption): ColumnTypeOption | undefined {
  // 如果已经是 ColumnTypeOption 对象，直接返回
  if (typeof dataType === 'object' && dataType !== null) {
    return dataType as ColumnTypeOption
  }
  // 如果是字符串，则查找对应的 ColumnTypeOption
  const currentDatasource = props.datasources.find(ds => ds.id === formData.value.datasourceId)
  
  if (currentDatasource?.category === DatasourceCategory.MYSQL) {
    return Object.values(MysqlColumnType).find(type => type.value === dataType)
  } else if (currentDatasource?.category === DatasourceCategory.POSTGRESQL) {
    return Object.values(PostgreSQLColumnType).find(type => type.value === dataType)
  } else if (currentDatasource?.category === DatasourceCategory.SQLITE) {
    return Object.values(SQLiteColumnType).find(type => type.value === dataType)
  } else if (currentDatasource?.category === DatasourceCategory.ORACLE) {
    return Object.values(OracleColumnType).find(type => type.value === dataType)
  } else if (currentDatasource?.category === DatasourceCategory.SQLSERVER) {
    return Object.values(SqlServerColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.MARIADB) {
    return Object.values(MariaDBColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.DB2) {
    return Object.values(DB2ColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.FIREBIRD) {
    return Object.values(FirebirdColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.COCKROACHDB) {
    return Object.values(CockroachDBColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.DUCKDB) {
    return Object.values(DuckDBColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.POLARDB) {
    return Object.values(PolarDBColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.OCEANBASE) {
    return Object.values(OceanBaseColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.TIDB) {
    return Object.values(TiDBColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.YUGABYTE) {
    return Object.values(YugabyteColumnType).find(type => type.value === dataType)
  }else if (currentDatasource?.category === DatasourceCategory.CUBRID) {
    return Object.values(CubridColumnType).find(type => type.value === dataType)
  }
  
  // 使用默认数据库类型
  return Object.values(DefaultDatabaseColumnType).find(type => type.value === dataType)
}

// 判断数据类型是否需要长度设置
function needsLength(columnTypeOption: ColumnTypeOption | undefined) {
  return columnTypeOption && 'maxLength' in columnTypeOption && columnTypeOption.maxLength !== undefined
}

// 判断数据类型是否需要精度设置
function needsScale(columnTypeOption: ColumnTypeOption | undefined) {
  return columnTypeOption && 'maxScale' in columnTypeOption && columnTypeOption.maxScale !== undefined
}

// 获取数据类型的最大长度
function getMaxLength(columnTypeOption: ColumnTypeOption | undefined) {
  return (columnTypeOption && 'maxLength' in columnTypeOption) ? columnTypeOption.maxLength : undefined
}

// 获取数据类型的最大精度
function getMaxScale(columnTypeOption: ColumnTypeOption | undefined) {
  return (columnTypeOption && 'maxScale' in columnTypeOption) ? columnTypeOption.maxScale : undefined
}

// 计算选择框的动态宽度
function calculateSelectWidth(fieldType: string | ColumnTypeOption): number {
  // 获取当前选中的数据类型选项
  const selectedOption = getColumnTypeOption(fieldType)
  const label = selectedOption?.label || (typeof fieldType === 'string' ? fieldType : '')
  
  // 基础宽度
  const baseWidth = 80
  // 每个字符的平均宽度（像素）
  const charWidth = 8
  // 计算文本宽度
  const textWidth = label.length * charWidth
  // 添加内边距和下拉箭头的空间
  const padding = 60
  
  // 计算总宽度，设置最小和最大宽度
  const calculatedWidth = Math.max(baseWidth, textWidth + padding)
  return Math.min(calculatedWidth, 200) // 最大宽度限制为200px
}

// 处理长度输入，限制最大值
function handleLengthInput(field: Field) {
  // 检查并清空非数字内容
  if (field.length !== undefined && field.length !== null) {
    const lengthStr = String(field.length)
    // 检查是否包含非数字字符（包括中文字符）
    if (!/^\d*$/.test(lengthStr) || /[\u4e00-\u9fa5]/.test(lengthStr)) {
      field.length = undefined
      return
    }
  }
  
  const maxLength = getMaxLength(getColumnTypeOption(field.type))
  if (maxLength !== undefined && field.length !== undefined && field.length > maxLength) {
    field.length = maxLength
  }
  
  // 如果有scale值，且length输入的值小于scale值，则将length值赋给scale（不允许scale大于length）
  if (field.scale !== undefined && field.length !== undefined && field.length < field.scale) {
    field.scale = field.length
  }
}

// 处理精度输入，限制最大值
function handleScaleInput(field: Field) {
  // 检查并清空非数字内容
  if (field.scale !== undefined && field.scale !== null) {
    const scaleStr = String(field.scale)
    // 检查是否包含非数字字符（包括中文字符）
    if (!/^\d*$/.test(scaleStr) || /[\u4e00-\u9fa5]/.test(scaleStr)) {
      field.scale = undefined
      return
    }
  }
  
  const maxScale = getMaxScale(getColumnTypeOption(field.type))
  if (maxScale !== undefined && field.scale !== undefined && field.scale > maxScale) {
    field.scale = maxScale
  }
  
  // 如果有length值，则scale必须小于或等于length值
  if (field.length !== undefined && field.scale !== undefined && field.scale > field.length) {
    field.scale = field.length
  }
}

// 输入法状态跟踪
const isComposing = ref(false)

// 处理数字键盘输入，只允许输入数字
function handleNumberKeydown(event: KeyboardEvent) {
  // 如果正在使用输入法，阻止所有输入
  if (isComposing.value) {
    event.preventDefault()
    return
  }
  
  // 允许的特殊键：退格、删除、Tab、Escape、Enter、方向键等
  const allowedKeys = [
    'Backspace', 'Delete', 'Tab', 'Escape', 'Enter',
    'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown',
    'Home', 'End'
  ]
  
  // 允许Ctrl+A, Ctrl+C, Ctrl+V, Ctrl+X等组合键
  if (event.ctrlKey || event.metaKey) {
    return
  }
  
  // 如果是允许的特殊键，直接通过
  if (allowedKeys.includes(event.key)) {
    return
  }
  
  // 只允许数字键0-9
  if (!/^[0-9]$/.test(event.key)) {
    event.preventDefault()
  }
}

// 处理输入法开始
function handleCompositionStart(event: CompositionEvent) {
  isComposing.value = true
}

// 处理输入法结束
function handleCompositionEnd(event: CompositionEvent) {
  if(isComposing.value){
    // 清除输入法产生的非数字内容（包括中文字符和拼音）
    const input = event.target as HTMLInputElement
    input.value = ""
  }
  isComposing.value = false
}

// 处理数据类型切换，调整 length 和 scale 值
function handleDataTypeChange(field: Field) {
  // 如果 field.type 是对象，提取其 value 属性作为字符串值
  if (typeof field.type === 'object' && field.type !== null && 'value' in field.type) {
    field.type = (field.type as any).value
  }
  
  const columnTypeOption = getColumnTypeOption(field.type)
  
  // 检查并调整 length 值
  const maxLength = getMaxLength(columnTypeOption)
  if (maxLength !== undefined) {
    // 如果有最大长度限制，且当前长度超过限制，则调整为最大值
    if (field.length !== undefined && field.length > maxLength) {
      field.length = maxLength
    }
  } else {
    // 如果没有长度限制，清空 length 值
    field.length = undefined
  }
  
  // 检查并调整 scale 值
  const maxScale = getMaxScale(columnTypeOption)
  if (maxScale !== undefined) {
    // 如果有最大精度限制，且当前精度超过限制，则调整为最大值
    if (field.scale !== undefined && field.scale > maxScale) {
      field.scale = maxScale
    }
  } else {
    // 如果没有精度限制，清空 scale 值
    field.scale = undefined
  }
}

function getForeignKeyInfoForParentField(field: Field) {
  let foreignKeyInfo: string[] = []
  props.relationships.forEach((r) => {
    if ((r.fromEntityId === props.entity?.id && r.fromFieldId === field.id)) {
      const toEntityName = props.entityNameCache[r.toEntityId as string] || ''
      const toFieldName = props.fieldNameCache[r.toFieldId as string] || ''
      foreignKeyInfo.push(`${toEntityName}.${toFieldName}`)
    }else if((r.toEntityId === props.entity?.id && r.toFieldId === field.id)){
      const fromEntityName = props.entityNameCache[r.fromEntityId as string] || ''
      const fromFieldName = props.fieldNameCache[r.fromFieldId as string] || ''
      foreignKeyInfo.push(`${fromEntityName}.${fromFieldName}`)
    }
  })
  return foreignKeyInfo
}

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

// 监听formData表单中的父实体ID变化，如果有切换则更新继承字段列表
watch(() => formData.value.parentEntityId, (newId) => {
    if (newId) {
      // 在 props.entities 中查找对应实体
      currentParentEntity.value = props.entities.find(e => e.id === newId) || undefined
    } else {
      currentParentEntity.value = undefined
    }
  }
)

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
  const containerFields = fieldsListWrapperRef.value;
  const containerModal = modalContentRef.value;
  
  if (!containerModal || !containerFields) return;
  
  // 获取表格容器的位置信息
  const fieldsRect = containerFields.getBoundingClientRect();
  
  // 判断是否在表格区域内滚动
  const isInTableArea = event.clientY >= fieldsRect.top && event.clientY <= fieldsRect.bottom;
  
  if (isInTableArea) {
    // 在表格区域内，优先处理表格的垂直滚动
    if (Math.abs(event.deltaY) > Math.abs(event.deltaX)) {
      // 垂直滚动：检查表格是否还能滚动
      const canScrollDown = containerFields.scrollTop < (containerFields.scrollHeight - containerFields.clientHeight);
      const canScrollUp = containerFields.scrollTop > 0;
      
      if ((event.deltaY > 0 && canScrollDown) || (event.deltaY < 0 && canScrollUp)) {
        // 表格可以滚动，让表格处理
        containerFields.scrollTop += event.deltaY;
      } else {
        // 表格不能滚动，让modal处理
        containerModal.scrollTop += event.deltaY;
      }
    } else {
      // 水平滚动：始终由表格处理
      containerFields.scrollLeft += event.deltaX;
    }
  } else {
    // 不在表格区域，由modal处理垂直滚动
    containerModal.scrollTop += event.deltaY;
    // 水平滚动仍由表格处理（如果有的话）
    if (Math.abs(event.deltaX) > 0) {
      containerFields.scrollLeft += event.deltaX;
    }
  }
}

// 监听modal滚动
function handleModalScroll() {
  if (!modalContentRef.value || !nameInputRef.value) return
  const modalRect = modalContentRef.value.getBoundingClientRect()
  const inputRect = nameInputRef.value.getBoundingClientRect()
  if (!inputRect) return
  // 判断输入框底部是否在 modal-content 顶部之上（即被滚动出去了）
  showHeaderName.value = inputRect.bottom < modalRect.top || inputRect.top > modalRect.bottom
}

// 添加字段
function addField(index: number) {
  // 计算新字段的 serialNo（使用小数值避免重新计算所有字段）
  const currentSerialNo = formData.value.fields[index].serialNo
  const nextSerialNo = index + 1 < formData.value.fields.length ? formData.value.fields[index + 1].serialNo : currentSerialNo + 1
  const newSerialNo = (currentSerialNo + nextSerialNo) / 2
  
  const newField: Field = {
    entityId: formData.value.entityId,
    id: Date.now().toString(),
    serialNo: newSerialNo,
    name: '',
    type: '',
    comment: '',
    isPrimaryKey: false,
    isRequired: false,
    isUnique: false
  }
  // 在指定位置的下一行插入新字段
  formData.value.fields.splice(index + 1, 0, newField)
}

// 删除字段
function removeField(index: number) {
  if(formData.value.fields.length >1 && confirm($t('messages.deleteFieldConfirm'))){
    formData.value.fields.splice(index, 1)
  }
}

// 删除外键引用
// 删除外键引用
function removeForeignKeyByForeignKey(foreignKeyRef: any, fieldIndex?: number ) {
  if(!confirm($t('messages.deleteForeignKeyConfirm'))) {
    return;
  }

  // 如果没有指定字段索引,直接删除关系
  if(fieldIndex === undefined) {
    emit('deleteRelation', foreignKeyRef.relationId);
    return;
  }

  const field = formData.value.fields[fieldIndex];
  if (!field.foreignKey?.length) {
    return;
  }

  // 查找并删除外键引用
  const refIndex = field.foreignKey.findIndex(ref => 
    ref.referencedEntityId === foreignKeyRef.referencedEntityId && 
    ref.referencedFieldId === foreignKeyRef.referencedFieldId
  );

  if (refIndex === -1) {
    return;
  }

  // 删除外键引用
  const relationId = field.foreignKey[refIndex].relationId;
  field.foreignKey.splice(refIndex, 1);

  // 删除关联实体的外键引用
  const foreignKey = props.entities.find(e => e.id === foreignKeyRef.referencedEntityId)?.fields.find(f => f.id === foreignKeyRef.referencedFieldId)?.foreignKey
  if(foreignKey && Array.isArray(foreignKey)){
    const refIndex = foreignKey.findIndex(ref => 
      ref.referencedEntityId === foreignKeyRef.referencedEntityId && 
      ref.referencedFieldId === foreignKeyRef.referencedFieldId
    )
    foreignKey.splice(refIndex, 1)
  }

  emit('deleteRelation', relationId);
}

// 删除外键引用
function removeForeignKeyByParentField(entityId: string, fieldId: string) {
  if(!confirm($t('messages.deleteForeignKeyConfirm'))) {
    return;
  }
  // 遍历关系列表找到对应的关系ID
  props.relationships.forEach(r => {
    if((r.fromEntityId === entityId && r.fromFieldId === fieldId) ||
    (r.toEntityId === entityId && r.toFieldId === fieldId)){
      emit('deleteRelation', r.id)
    }
  })
}

// 拖拽开始
function handleDragStart(event: DragEvent, index: number) {
  draggedIndex.value = index
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move'
    event.dataTransfer.setData('text/html', '')
  }
}

// 拖拽经过
function handleDragOver(event: DragEvent) {
  event.preventDefault()
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'move'
  }
  
  // 检测拖拽位置是否接近边界，触发自动滚动
  const container = modalContentRef.value
  if (!container) return
  
  const rect = container.getBoundingClientRect()
  const scrollThreshold = 50 // 距离边界50px时开始滚动
  const rowHeight = 50 // 表格行高度
  const scrollDelay = 200 // 滚动间隔时间(毫秒)
  
  // 清除之前的定时器
  if (autoScrollTimer.value) {
    clearInterval(autoScrollTimer.value)
    autoScrollTimer.value = null
  }
  
  // 检测垂直方向
  if (event.clientY - rect.top < scrollThreshold) {
    // 接近顶部边界，向上滚动
    autoScrollTimer.value = setInterval(() => {
      const newScrollTop = Math.max(0, container.scrollTop - rowHeight)
      if (newScrollTop === container.scrollTop) {
        // 已经到顶部，停止滚动
        if (autoScrollTimer.value) {
          clearInterval(autoScrollTimer.value)
          autoScrollTimer.value = null
        }
      } else {
        container.scrollTop = newScrollTop
      }
    }, scrollDelay)
  } else if (rect.bottom - event.clientY < scrollThreshold) {
    // 接近底部边界，向下滚动
    autoScrollTimer.value = setInterval(() => {
      const maxScrollTop = container.scrollHeight - container.clientHeight
      const newScrollTop = Math.min(maxScrollTop, container.scrollTop + rowHeight)
      if (newScrollTop === container.scrollTop) {
        // 已经到底部，停止滚动
        if (autoScrollTimer.value) {
          clearInterval(autoScrollTimer.value)
          autoScrollTimer.value = null
        }
      } else {
        container.scrollTop = newScrollTop
      }
    }, scrollDelay)
  }
}

// 拖拽进入
function handleDragEnter(event: DragEvent, index: number) {
  event.preventDefault()
  if (draggedIndex.value !== null && draggedIndex.value !== index) {
    dragOverIndex.value = index
  }
}

// 拖拽离开
function handleDragLeave(event: DragEvent) {
  // 只有当离开整个行时才清除dragOverIndex
  const rect = (event.currentTarget as HTMLElement).getBoundingClientRect()
  const x = event.clientX
  const y = event.clientY
  
  if (x < rect.left || x > rect.right || y < rect.top || y > rect.bottom) {
    dragOverIndex.value = null
  }
}

// 拖拽放置
function handleDrop(event: DragEvent, dropIndex: number) {
  event.preventDefault()
  
  if (draggedIndex.value === null || draggedIndex.value === dropIndex) {
    return
  }
  
  const draggedField = formData.value.fields[draggedIndex.value]
  const fields = [...formData.value.fields]
  
  // 移除被拖拽的字段
  fields.splice(draggedIndex.value, 1)
  
  // 在新位置插入字段
  const insertIndex = draggedIndex.value < dropIndex ? dropIndex - 1 : dropIndex
  fields.splice(insertIndex, 0, draggedField)
  
  // 更新字段数组
  formData.value.fields = fields
  
  // 计算新的 serialNo（使用小数值）
  const targetField = formData.value.fields[insertIndex]
  const prevField = formData.value.fields[insertIndex - 1]
  const nextField = formData.value.fields[insertIndex + 1]
  
  if (!prevField && !nextField) {
    // 如果是唯一字段
    targetField.serialNo = 1
  } else if (!prevField) {
    // 如果是第一个字段
    targetField.serialNo = nextField.serialNo - 0.5
  } else if (!nextField) {
    // 如果是最后一个字段
    targetField.serialNo = prevField.serialNo + 0.5
  } else {
    // 如果在中间
    targetField.serialNo = (prevField.serialNo + nextField.serialNo) / 2
  }
  
  dragOverIndex.value = null
}

// 拖拽结束
function handleDragEnd() {
  draggedIndex.value = null
  dragOverIndex.value = null
  
  // 清除自动滚动定时器
  if (autoScrollTimer.value) {
    clearInterval(autoScrollTimer.value)
    autoScrollTimer.value = null
  }
}

// 保存实体
function handleSave() {
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
      backgroundColor: props.entity?.backgroundColor,
      borderColor: props.entity?.borderColor,
      fontColor: props.entity?.fontColor
    }
    emit('save', entity)
  }
}

// 处理点击模态框内容区域
function handleOverlayClick(event: MouseEvent) {
  const target = event.target as HTMLElement
  // 检查点击目标是否是VueSelect组件或其子元素
  const isVueSelectElement = target.closest('.vs__dropdown-toggle') || 
                            target.closest('.vs__dropdown-menu') ||
                            target.closest('.vs__search') ||
                            target.classList.contains('vs__selected') ||
                            target.classList.contains('vs__option')
  
  // 如果点击的不是VueSelect相关元素，则关闭所有下拉菜单
  if (!isVueSelectElement) {
    vueSelectRefs.value.forEach((selectRef: any) => {
      if (selectRef && selectRef.open) {
        selectRef.open = false
      }
    })
  }
}

// 组件挂载后自动聚焦到name输入框
onMounted(() => {
  // 使用nextTick确保DOM已经渲染完成
  setTimeout(() => {
    if (nameInputRef.value) {
      nameInputRef.value.focus()
    }
  }, 100)
})
</script>

<style scoped>
.modal-content {
  width: min(600px, calc(100vw - 80px));
  min-width: 0; /* 允许在小屏下收缩，保证左右各20px间距 */
  min-height: 430px;
  max-height: 50vh;
  display: flex !important;
  flex-direction: column !important;
  margin: 0 auto; /* 居中，剩余空间自动分配到左右 */
}
.modal-header,
.modal-footer {
  flex-shrink: 0;
}
.modal-body {
  flex: 1 1 auto;
  overflow-y: auto;
  min-height: 0; /* 兼容性写法，防止flex子项溢出 */
  /* 隐藏滚动条但保持滚动功能 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.modal-body::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
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
  overflow: auto;
  max-width: 100%;
  flex: 1;
  min-height: 300px;
  max-height: 400px;
  position: relative;
  /* 当modal-body滚动到此处时，接管滚动控制 */
  border: 1px solid #e1e4e8;
  border-radius: 6px;
}

.dark-theme .fields-list-wrapper {
  border-color: #30363d;
}

/* 固定表头和字段名列 */
.fields-table {
  position: relative;
  min-width: 1200px; /* 让表格内容撑开，超出时滚动 */
  border-collapse: collapse;
}

.sticky-column {
  position: sticky;
  left: 0;
  background: #ffffff;
  z-index: 2;
}

.td-actions.sticky-column {
  left: 0;
  z-index: 4;
}

.sticky-column:nth-child(2) {
  left: 89px;
  z-index: 2;
  margin: 0px;
}

.dark-theme .fields-table-header th.sticky-column {
  background: #21262d;
}

.dark-theme .sticky-column,
.dark-theme .td-actions.sticky-column {
  background: #171717;
}

.fields-table-header th {
  background: #f6f8fa;
  position: sticky;
  top: 0;
  z-index: 1;
}

.fields-table-header th.sticky-column:first-child {
  z-index: 5;
}

.fields-table-header th.sticky-column:nth-child(2) {
  left: 89px;
  z-index: 3;
}
.fields-section {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  min-height: 0;
  max-height: 350px;
  flex: 1;
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

.fields-table-header {
  background: #f6f8fa !important;
  font-weight: 600;
  font-size: 15px;
}
.fields-table th,
.fields-table td {
  padding: 4px 8px;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 14px;
  height: 50px;
}
.fields-table th {
  background: #f6f8fa;
}
.dark-theme .fields-table-header th {
  background: #21262d;
}
.dark-theme .fields-table th {
  border-color: #30363d;
  background: #21262d;
  color: #ffffff;
}
.fields-table textarea {
  resize: none !important;
}
.fields-table input{
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.field-row:hover,
.field-row:hover .sticky-column {
  background: #eff6f9 !important;
  transition: background 0.2s;
}
.dark-theme .field-row:hover,
.dark-theme .field-row:hover .sticky-column {
  background: #2a3350 !important;
}

/* 拖拽样式 */
.field-row {
  cursor: move;
}
.field-row.dragging {
  opacity: 0.5;
  background: #e3f2fd !important;
}
.field-row.drag-over {
  border-top: 3px solid #2196f3;
}
.dark-theme .field-row.dragging {
  background: #2a3350 !important;
}
.dark-theme .field-row.drag-over {
  border-top: 3px solid #bb86fc;
}

/* 删除按钮 */
.remove-btn {
  background: #d73a49;
  color: #fff;
  border: none;
  padding: 6px 10px;
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
  background: #cf6679;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}
/* 操作按钮单元格 */
.td-actions {
  text-align: center;
  height: 50px;
  vertical-align: middle;
  padding: 4px 8px;
  box-sizing: border-box;
}
.td-actions button {
  margin: 0 4px;
}
/* 外键相关样式 */
.td-source-field,
.td-foreign-key {
  text-align: left !important;
  white-space: normal !important;
  word-wrap: break-word;
  max-width: 200px;
}
.td-foreign-key .span-foreign-key {
  width: 100%;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  border: none;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  margin-bottom: 4px;
  white-space: nowrap;
  flex-wrap: nowrap;
  min-width: 0;
}
.td-foreign-key .span-foreign-key .text {
  width: 80%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background-color: #7d7d7d;
  padding: 20px;
}
.td-foreign-key .span-foreign-key .delete-btn {
  width: 20%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  background-color: #d73a49;
}
.td-foreign-key .span-foreign-key .text:hover {
  background-color: #979797;
}
.td-foreign-key .span-foreign-key .delete-btn:hover {
  background-color: #bf2c3b;
}
.td-foreign-key .span-foreign-key:last-child {
  margin-bottom: 0;
}
.dark-theme .td-foreign-key .span-foreign-key .text {
  background-color: #3c3c3c;
}
.dark-theme .td-foreign-key .span-foreign-key .delete-btn {
  background-color: #952732;
}
.dark-theme .td-foreign-key .span-foreign-key .text:hover {
  background-color: #6c6c6c;
}
.dark-theme .td-foreign-key .span-foreign-key .delete-btn:hover {
  background-color: #bf2c3b;
}
/* 添加按钮 */
.add-btn {
  background: #66bb6a;
  color: #fff;
  border: none;
  padding: 4.5px 10px;
  border-radius: 4px;
  font-size: 15px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.add-btn:hover {
  background: #59a25d;
}
.dark-theme .add-btn {
  background: #5ca860;
  color: #000000;
}
.dark-theme .add-btn:hover {
  background: #4caf50;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

@media (max-width: var(--mobile-breakpoint)) {
}
</style>