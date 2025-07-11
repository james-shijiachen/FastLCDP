<template>
  <div class="bugs-view">
    <el-card>
      <div class="header">
        <h2>Bug管理</h2>
        <el-button type="primary" @click="openAdd">新建Bug</el-button>
      </div>
      <el-table :data="bugs" style="width: 100%; margin-top: 16px;">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="severity" label="严重程度" width="100" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="120" />
        <el-table-column label="关联需求" width="180">
          <template #default="{ row }">
            <el-tag v-for="rid in row.relatedRequirementIds || []" :key="rid" size="small" style="margin-right:4px;">需求{{ rid }}</el-tag>
          </template>
        </el-table-column>
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
    <el-dialog v-model="dialogVisible" :title="dialogMode === 'add' ? '新建Bug' : '编辑Bug'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="form.severity">
            <el-option label="阻断" value="blocker" />
            <el-option label="严重" value="critical" />
            <el-option label="主要" value="major" />
            <el-option label="次要" value="minor" />
            <el-option label="提示" value="trivial" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="新建" value="new" />
            <el-option label="打开" value="open" />
            <el-option label="处理中" value="in-progress" />
            <el-option label="已解决" value="resolved" />
            <el-option label="已关闭" value="closed" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联需求">
          <el-select v-model="form.relatedRequirementIds" multiple filterable placeholder="选择需求">
            <el-option v-for="req in allRequirements" :key="req.id" :label="req.title" :value="req.id" />
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
      <span>确定要删除该Bug吗？</span>
      <template #footer>
        <el-button @click="deleteVisible = false">取消</el-button>
        <el-button type="danger" @click="onDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useBugStore } from '../stores/bugStore';
import { useRequirementStore } from '../stores/requirementStore';
import { useTaskStore } from '../stores/taskStore';
import type { Bug } from '../types/bug';

const bugStore = useBugStore();
const bugs = bugStore.bugs;
const allRequirements = useRequirementStore().requirements;
const allTasks = useTaskStore().tasks;

const dialogVisible = ref(false);
const dialogMode = ref<'add' | 'edit'>('add');
const form = reactive<Partial<Bug>>({});
const editId = ref<number | null>(null);

function openAdd() {
  dialogMode.value = 'add';
  Object.assign(form, { title: '', description: '', severity: 'major', status: 'new', relatedRequirementIds: [], relatedTaskIds: [] });
  dialogVisible.value = true;
}
function openEdit(row: Bug) {
  dialogMode.value = 'edit';
  Object.assign(form, row);
  editId.value = row.id;
  dialogVisible.value = true;
}
function onSubmit() {
  if (dialogMode.value === 'add') {
    bugStore.addBug(form as Omit<Bug, 'id' | 'createdAt'>);
  } else if (dialogMode.value === 'edit' && editId.value !== null) {
    bugStore.updateBug(editId.value, form);
  }
  dialogVisible.value = false;
}

const deleteVisible = ref(false);
const deleteId = ref<number | null>(null);
function openDelete(row: Bug) {
  deleteId.value = row.id;
  deleteVisible.value = true;
}
function onDelete() {
  if (deleteId.value !== null) {
    bugStore.deleteBug(deleteId.value);
  }
  deleteVisible.value = false;
}
</script>

<style scoped>
.bugs-view {
  padding: 24px;
  max-width: 1100px;
  margin: 0 auto;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
</style> 