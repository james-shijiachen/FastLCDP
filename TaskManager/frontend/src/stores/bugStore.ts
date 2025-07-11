import { defineStore } from 'pinia';
import type { Bug } from '../types/bug';

function mockBugs(): Bug[] {
  return [
    { id: 1, title: '任务无法保存', description: '点击保存无响应', severity: 'blocker', status: 'open', createdAt: '2024-06-01', relatedTaskIds: [1] },
    { id: 2, title: '需求页面报错', description: '打开需求管理页面报500', severity: 'major', status: 'resolved', createdAt: '2024-06-02', relatedRequirementIds: [2] },
    { id: 3, title: '任务优先级显示异常', description: '优先级显示为undefined', severity: 'minor', status: 'new', createdAt: '2024-06-03' },
  ];
}

export const useBugStore = defineStore('bug', {
  state: () => ({
    bugs: mockBugs() as Bug[],
    nextId: 4,
  }),
  actions: {
    addBug(bug: Omit<Bug, 'id' | 'createdAt'>) {
      this.bugs.push({
        ...bug,
        id: this.nextId++,
        createdAt: new Date().toISOString().slice(0, 10),
      });
    },
    updateBug(id: number, update: Partial<Bug>) {
      const idx = this.bugs.findIndex(b => b.id === id);
      if (idx !== -1) {
        this.bugs[idx] = { ...this.bugs[idx], ...update };
      }
    },
    deleteBug(id: number) {
      this.bugs = this.bugs.filter(b => b.id !== id);
    },
  },
}); 