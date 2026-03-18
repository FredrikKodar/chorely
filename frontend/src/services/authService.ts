import { api, handleApiError } from './api';
import { AuthResponse } from '../types/auth';

export const authService = {
  login: async (email: string, password: string): Promise<AuthResponse> => {
    try {
      const response = await api.post('/auth/login', { email, password });
      return response.data;
    } catch (error) {
      handleApiError(error);
    }
  },
  
  registerParent: async (email: string, password: string): Promise<string> => {
    try {
      const response = await api.post('/auth/register', { email, password });
      return response.data;
    } catch (error) {
      handleApiError(error);
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
      handleApiError(error);
    }
  }
};