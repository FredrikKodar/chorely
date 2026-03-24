import { api, handleApiError } from './api';
import { AuthResponse } from '../types/auth';

export const authService = {
  login: async (email: string, password: string): Promise<AuthResponse> => {
    try {
      console.log('📡 Sending login request to:', api.defaults.baseURL + '/auth/login');
      const response = await api.post('/auth/login', { email, password });
      console.log('📥 Login response:', response.data);
      return response.data;
    } catch (error) {
      console.error('💥 Login API error:', error);
      return handleApiError(error);
    }
  },
  
  registerParent: async (email: string, password: string, firstName: string, lastName: string): Promise<string> => {
    try {
      const response = await api.post('/auth/register', { email, password, firstName, lastName });
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  },
  
  registerChild: async (childData: {
    username: string;
    password: string;
    firstName: string;
  }): Promise<string> => {
    try {
      const response = await api.post('/auth/register-child', childData);
      return response.data;
    } catch (error) {
      return handleApiError(error);
    }
  }
};