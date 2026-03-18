import axios from 'axios';

// API base URL - configured via environment variables
const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api';

export const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Module-level variable for token storage
let authToken: string | null = null;

// Function to set token from AuthProvider
export const setAuthToken = (token: string | null) => {
  authToken = token;
};

// Add JWT token to all requests automatically
api.interceptors.request.use((config) => {
  if (authToken) {
    config.headers.Authorization = `Bearer ${authToken}`;
  }
  return config;
});

// Clear token on 401 errors
export const clearAuthToken = () => {
  authToken = null;
};

// Handle common error responses
export const handleApiError = (error: any): never => {
  if (error.response) {
    switch (error.response.status) {
      case 401:
        clearAuthToken();
        throw new Error('UNAUTHORIZED');
      case 403:
        throw new Error('Forbidden - You do not have permission');
      case 404:
        throw new Error('Resource not found');
      case 400:
        throw new Error(error.response.data.message || 'Bad request');
      default:
        throw new Error(error.response.data.message || 'API error occurred');
    }
  } else if (error.request) {
    throw new Error('No response received from server');
  } else {
    throw new Error('Request setup error');
  }
};

// Example component-level 401 handling
export const useApiErrorHandler = (navigate: any) => {
  return (error: any) => {
    if (error.message === 'UNAUTHORIZED') {
      clearAuthToken();
      navigate('/login');
    }
    throw error;
  };
};