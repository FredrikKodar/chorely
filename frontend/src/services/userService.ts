import { api, handleApiError } from './api';
import { UserResponse, UserIdAndFirstNameResponse } from '../types/user';

export const userService = {
  getCurrentUser: async (): Promise<UserResponse> => {
    try {
      const response = await api.get('/users/me');
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  getUserById: async (userId: number): Promise<UserResponse> => {
    try {
      const response = await api.get(`/users/${userId}`);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  getChildren: async (): Promise<UserIdAndFirstNameResponse[]> => {
    try {
      const response = await api.get('/users/children');
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  updateChild: async (childId: number, data: { firstName: string }): Promise<UserIdAndFirstNameResponse> => {
    try {
      const response = await api.patch(`/users/children/${childId}`, data);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  }
};