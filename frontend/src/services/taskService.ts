import { api, handleApiError } from './api';
import { TaskReadResponse, TaskCreationRequest, TaskUpdateRequest, TaskSavedResponse } from '../types/tasks';

export const taskService = {
  getTasks: async (): Promise<TaskReadResponse[]> => {
    try {
      const response = await api.get('/tasks');
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  getTasksByUserId: async (userId: number): Promise<TaskReadResponse[]> => {
    try {
      const response = await api.get(`/tasks/user/${userId}`);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  getTaskById: async (taskId: number): Promise<TaskReadResponse> => {
    try {
      const response = await api.get(`/tasks/${taskId}`);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  createTask: async (taskData: TaskCreationRequest): Promise<TaskSavedResponse> => {
    try {
      const response = await api.post('/tasks', taskData);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  createTaskForChild: async (childId: number, taskData: TaskCreationRequest): Promise<TaskSavedResponse> => {
    try {
      console.log('📤 Sending task creation request for child:', childId);
      console.log('📄 Task data:', taskData);
      const response = await api.post(`/tasks/${childId}`, taskData);
      console.log('📥 Task creation response:', response.data);
      return response.data;
    } catch (error) {
      console.error('💥 Task creation API error:', error);
      return handleApiError(error);
    }
  },
  
  updateTask: async (taskId: number, taskData: TaskUpdateRequest): Promise<TaskReadResponse> => {
    try {
      const response = await api.patch(`/tasks/${taskId}`, taskData);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  toggleStatus: async (taskId: number): Promise<TaskReadResponse> => {
    try {
      const response = await api.patch(`/tasks/${taskId}/toggle-status-child`);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  approveTask: async (taskId: number): Promise<TaskReadResponse> => {
    try {
      console.log('👍 Approving task:', taskId);
      const response = await api.patch(`/tasks/${taskId}/approve`);
      console.log('✅ Task approved:', response.data);
      return response.data;
    } catch (error) {
      console.error('❌ Approval failed:', error);
      return handleApiError(error);
    }
  },
  
  getPendingApproval: async (): Promise<TaskReadResponse[]> => {
    try {
      const response = await api.get('/tasks/pending-approval');
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  rejectTask: async (taskId: number): Promise<TaskReadResponse> => {
    try {
      const response = await api.patch(`/tasks/${taskId}`, {
        status: 'ASSIGNED'
      });
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  }
};