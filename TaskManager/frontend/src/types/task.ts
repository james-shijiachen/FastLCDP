export type TaskStatus = 'todo' | 'in-progress' | 'done';
export interface Task {
  id: number;
  title: string;
  description?: string;
  priority: 'low' | 'medium' | 'high';
  status: TaskStatus;
  createdAt: string;
  dueAt?: string;
} 