<template>
  <div class="board-view">
    <h2>任务看板</h2>
    <div class="board-columns">
      <div class="board-column" v-for="status in statusList" :key="status.value">
        <div class="board-column-header">{{ status.label }}</div>
        <draggable
          :list="getTasksByStatus(status.value as TaskStatus)"
          group="tasks"
          @end="onDragEnd(status.value as TaskStatus)"
          item-key="id"
        >
          <template #item="{ element }">
            <el-card class="task-card" shadow="hover">
              <div class="task-title">{{ element.title }}</div>
              <div class="task-desc">{{ element.description }}</div>
              <div class="task-meta">
                <el-tag size="small" :type="priorityType(element.priority)">{{ priorityLabel(element.priority) }}</el-tag>
                <span class="task-date">{{ element.createdAt }}</span>
              </div>
            </el-card>
          </template>
        </draggable>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useTaskStore } from '../stores/taskStore';
import type { Task, TaskStatus } from '../types/task';
import draggable from 'vuedraggable';

const taskStore = useTaskStore();
const statusList = [
  { value: 'todo', label: '待办' },
  { value: 'in-progress', label: '进行中' },
  { value: 'done', label: '已完成' },
];

function getTasksByStatus(status: TaskStatus) {
  return taskStore.tasks.filter(t => t.status === status);
}

function priorityLabel(priority: Task['priority']) {
  if (priority === 'high') return '高';
  if (priority === 'medium') return '中';
  return '低';
}
function priorityType(priority: Task['priority']) {
  if (priority === 'high') return 'danger';
  if (priority === 'medium') return 'warning';
  return '';
}

function onDragEnd(newStatus: TaskStatus) {
  // 拖拽后自动更新所有该列任务的状态
  // vuedraggable已自动更新顺序，这里只需同步状态
  setTimeout(() => {
    taskStore.tasks.forEach(task => {
      if (getTasksByStatus(newStatus).some(t => t.id === task.id)) {
        if (task.status !== newStatus) {
          taskStore.updateTask(task.id, { status: newStatus });
        }
      }
    });
  }, 0);
}
</script>

<style scoped>
.board-view {
  padding: 24px;
}
.board-columns {
  display: flex;
  gap: 24px;
  margin-top: 16px;
}
.board-column {
  flex: 1;
  background: #f8f9fb;
  border-radius: 8px;
  padding: 12px;
  min-width: 260px;
  min-height: 400px;
}
.board-column-header {
  font-weight: bold;
  margin-bottom: 12px;
  text-align: center;
}
.task-card {
  margin-bottom: 12px;
}
.task-title {
  font-weight: 500;
  font-size: 16px;
}
.task-desc {
  color: #888;
  font-size: 13px;
  margin: 4px 0 8px 0;
}
.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}
</style> 