import { api, handleApiError } from './api';
import { UserResponse, UserIdAndFirstNameResponse } from '../types/user';

export const userService = {
  getCurrentUser: async (): Promise<UserResponse> => {
    try {
      console.log('📡 Fetching current user from:', api.defaults.baseURL + '/users/me');
      const response = await api.get('/users/me');
      console.log('📥 User data:', response.data);
      return response.data;
    } catch (error) {
      console.error('💥 User API error:', error);
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
  }
};