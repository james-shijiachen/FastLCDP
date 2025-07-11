export type BugStatus = 'new' | 'open' | 'in-progress' | 'resolved' | 'closed' | 'rejected';
export type BugSeverity = 'blocker' | 'critical' | 'major' | 'minor' | 'trivial';
export interface Bug {
  id: number;
  title: string;
  description?: string;
  severity: BugSeverity;
  status: BugStatus;
  createdAt: string;
  relatedRequirementIds?: number[];
  relatedTaskIds?: number[];
} 