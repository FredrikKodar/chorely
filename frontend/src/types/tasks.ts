export type TaskStatus = 'ASSIGNED' | 'PENDING_APPROVAL' | 'APPROVED';

export interface TaskReadResponse {
  id: number;
  title: string;
  description: string;
  points: number;
  status: TaskStatus;
  created: string; // ISO string
  updated: string; // ISO string
}

export interface TaskCreationRequest {
  title: string;
  description: string;
  points: number;
}

export interface TaskUpdateRequest {
  title?: string;
  description?: string;
  points?: number;
  status?: TaskStatus;
}

export interface TaskSavedResponse {
  id: number;
  title: string;
  description: string;
  points: number;
}