export type RequirementStatus = 'new' | 'review' | 'in-progress' | 'done' | 'closed';
export interface Requirement {
  id: number;
  title: string;
  description?: string;
  priority: 'low' | 'medium' | 'high';
  status: RequirementStatus;
  createdAt: string;
  relatedTaskIds?: number[];
  relatedBugIds?: number[];
} 