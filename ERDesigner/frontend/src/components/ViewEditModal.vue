<template>
    <div class="modal-overlay" @click.stop @wheel.prevent="handleModalWheel">
        <div class="modal-content">
        <div class="modal-header">
            <h3>{{ isEdit ? '编辑视图' : '新建视图' }}</h3>
            <button class="close-btn" @click="$emit('close')">×</button>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label>视图名称 *</label>
                <input v-model="formData.name" placeholder="请输入视图名称" />
            </div>
            <div class="form-group">
                <label>选择数据库 *</label>
                <select v-model="formData.datasourceIds" multiple>
                    <option v-for="ds in datasources" :key="ds.id" :value="ds.id">{{ ds.name }}</option>
                </select>
            </div>
        </div>
        <div class="modal-footer">
            <button class="btn btn-secondary" @click="$emit('close')">取消</button>
            <button class="btn btn-primary" @click="handleSave" :disabled="!formData.name || formData.datasourceIds.length === 0">保存</button>
        </div>
        </div>
    </div>
</template>
  
<script setup lang="ts">
import { ref, watch } from 'vue'
import type { Datasource, View } from '../types/entity'

const props = defineProps<{
    datasources: Datasource[]
    view?: View | null
}>()
const emit = defineEmits<{
    (e: 'save', view: { name: string, datasourceIds: string[] }): void
    (e: 'close'): void
}>()

const isEdit = ref(false)
const formData = ref<{ name: string; datasourceIds: string[] }>({
  name: '',
  datasourceIds: []
})

watch(() => props.view, (v) => {
if (v) {
    isEdit.value = true
    formData.value = {
    name: v.name,
    datasourceIds: v.datasources.map(ds => ds.id)
    }
} else {
    isEdit.value = false
    formData.value = { name: '', datasourceIds: [] }
}
}, { immediate: true })

// 监听滚轮事件（屏蔽浏览器默认滚动）
function handleModalWheel(event: WheelEvent) {
  event.stopPropagation();
}

function handleSave() {
    emit('save', { ...formData.value })
}
</script>
  
<style scoped>
/* 简单样式，可根据你的UI规范调整 */
.modal-overlay { 
    position: fixed; 
    left:0; top:0; 
    right:0; bottom:0; 
    background:rgba(0,0,0,0.2); 
    display:flex; 
    align-items:center; 
    justify-content:center; 
    z-index:9999; 
}
.modal-content { 
    background:#fff; 
    border-radius:8px; 
    width:350px; 
    padding:24px; 
}
.modal-header { 
    display:flex; 
    justify-content:space-between; 
    align-items:center; 
}
.close-btn { 
    background:none; 
    border:none; 
    font-size:20px; 
    cursor:pointer; 
}
.form-group { 
    margin-bottom:16px; 
}
.form-group label { 
    display:block; 
    margin-bottom:4px; 
}
.form-group input, 
.form-group select { 
    width:100%; 
    padding:6px; 
}
.modal-footer { 
    display:flex; 
    justify-content:flex-end; 
    gap:8px; 
}
</style>