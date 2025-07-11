import { defineStore } from 'pinia';
import type { Task } from '../types/task';

function mockTasks(): Task[] {
  return [
    { id: 1, title: '初始化项目', description: '完成项目结构搭建', priority: 'high', status: 'done', createdAt: '2024-06-01' },
    { id: 2, title: '设计数据结构', description: '梳理任务/需求/bug数据结构', priority: 'medium', status: 'in-progress', createdAt: '2024-06-02' },
    { id: 3, title: '实现Backlog页面', description: '开发任务池页面', priority: 'high', status: 'todo', createdAt: '2024-06-03' },
  ];
}

export const useTaskStore = defineStore('task', {
  state: () => ({
    tasks: mockTasks() as Task[],
    nextId: 4,
  }),
  actions: {
    addTask(task: Omit<Task, 'id' | 'createdAt'>) {
      this.tasks.push({
        ...task,
        id: this.nextId++,
        createdAt: new Date().toISOString().slice(0, 10),
      });
    },
    updateTask(id: number, update: Partial<Task>) {
      const idx = this.tasks.findIndex(t => t.id === id);
      if (idx !== -1) {
        this.tasks[idx] = { ...this.tasks[idx], ...update };
      }
    },
    deleteTask(id: number) {
      this.tasks = this.tasks.filter(t => t.id !== id);
    },
  },
}); 