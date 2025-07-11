<template>
  <div class="requirements-view">
    <el-card>
      <div class="header">
        <h2>需求管理</h2>
        <el-button type="primary" @click="openAdd">新建需求</el-button>
      </div>
      <el-table :data="requirements" style="width: 100%; margin-top: 16px;">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="priority" label="优先级" width="100" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="120" />
        <el-table-column label="关联任务" width="180">
          <template #default="{ row }">
            <el-tag v-for="tid in row.relatedTaskIds || []" :key="tid" size="small" style="margin-right:4px;">任务{{ tid }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="openDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 新建/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新建需求' : '编辑需求'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority">
            <el-option label="高" value="high" />
            <el-option label="中" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="新建" value="new" />
            <el-option label="评审中" value="review" />
            <el-option label="进行中" value="in-progress" />
            <el-option label="已完成" value="done" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联任务">
          <el-select v-model="form.relatedTaskIds" multiple filterable placeholder="选择任务">
            <el-option v-for="task in allTasks" :key="task.id" :label="task.title" :value="task.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>
    <!-- 删除确认 -->
    <el-dialog v-model="deleteVisible" title="确认删除" width="300px">
      <span>确定要删除该需求吗？</span>
      <template #footer>
        <el-button @click="deleteVisible = false">取消</el-button>
        <el-button type="danger" @click="onDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRequirementStore } from '../stores/requirementStore';
import { useTaskStore } from '../stores/taskStore';
import type { Requirement } from '../types/requirement';

const requirementStore = useRequirementStore();
const requirements = requirementStore.requirements;
const allTasks = useTaskStore().tasks;

const dialogVisible = ref(false);
const dialogMode = ref<'add' | 'edit'>('add');
const form = reactive<Partial<Requirement>>({});
const editId = ref<number | null>(null);

function openAdd() {
  dialogMode.value = 'add';
  Object.assign(form, { title: '', description: '', priority: 'medium', status: 'new', relatedTaskIds: [] });
  dialogVisible.value = true;
}
function openEdit(row: Requirement) {
  dialogMode.value = 'edit';
  Object.assign(form, row);
  editId.value = row.id;
  dialogVisible.value = true;
}
function onSubmit() {
  if (dialogMode.value === 'add') {
    requirementStore.addRequirement(form as Omit<Requirement, 'id' | 'createdAt'>);
  } else if (dialogMode.value === 'edit' && editId.value !== null) {
    requirementStore.updateRequirement(editId.value, form);
  }
  dialogVisible.value = false;
}

const deleteVisible = ref(false);
const deleteId = ref<number | null>(null);
function openDelete(row: Requirement) {
  deleteId.value = row.id;
  deleteVisible.value = true;
}
function onDelete() {
  if (deleteId.value !== null) {
    requirementStore.deleteRequirement(deleteId.value);
  }
  deleteVisible.value = false;
}
</script>

<style scoped>
.requirements-view {
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
</style> 