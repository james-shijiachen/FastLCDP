import { defineStore } from 'pinia';
import type { Requirement } from '../types/requirement';

function mockRequirements(): Requirement[] {
  return [
    { id: 1, title: '用户可以创建任务', description: '支持任务的增删改查', priority: 'high', status: 'done', createdAt: '2024-06-01', relatedTaskIds: [1,3] },
    { id: 2, title: '任务支持优先级', description: '任务可设置高/中/低优先级', priority: 'medium', status: 'in-progress', createdAt: '2024-06-02', relatedTaskIds: [2] },
    { id: 3, title: '需求与任务关联', description: '需求可关联多个任务', priority: 'medium', status: 'new', createdAt: '2024-06-03' },
  ];
}

export const useRequirementStore = defineStore('requirement', {
  state: () => ({
    requirements: mockRequirements() as Requirement[],
    nextId: 4,
  }),
  actions: {
    addRequirement(req: Omit<Requirement, 'id' | 'createdAt'>) {
      this.requirements.push({
        ...req,
        id: this.nextId++,
        createdAt: new Date().toISOString().slice(0, 10),
      });
    },
    updateRequirement(id: number, update: Partial<Requirement>) {
      const idx = this.requirements.findIndex(r => r.id === id);
      if (idx !== -1) {
        this.requirements[idx] = { ...this.requirements[idx], ...update };
      }
    },
    deleteRequirement(id: number) {
      this.requirements = this.requirements.filter(r => r.id !== id);
    },
  },
}); 