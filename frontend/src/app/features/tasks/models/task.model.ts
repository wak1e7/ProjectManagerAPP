export type TaskStatus = 'TODO' | 'REVIEW' | 'IN_PROGRESS' | 'DONE';

export interface Task {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  dueDate?: string;
  assigneeIds?: number[];
}