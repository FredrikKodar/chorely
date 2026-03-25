import React, { createContext, useContext, useReducer, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';
import { userService } from '../services/userService';
import { setAuthToken, clearAuthToken } from '../services/api';
import { User } from '../types/auth';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}

type AuthAction =
  | { type: 'LOGIN_START' }
  | { type: 'LOGIN_SUCCESS'; payload: { user: User; token: string; expiresIn: number } }
  | { type: 'LOGIN_FAILURE'; payload: string }
  | { type: 'LOGOUT' }
  | { type: 'REGISTER_START' }
  | { type: 'REGISTER_SUCCESS' }
  | { type: 'REGISTER_FAILURE'; payload: string }
  | { type: 'UPDATE_USER'; payload: User };

const AuthContext = createContext<{
  state: AuthState;
  dispatch: React.Dispatch<AuthAction>;
  login: (email: string, password: string, isChildLogin?: boolean) => Promise<void>;
  logout: () => void;
  registerParent: (email: string, password: string) => Promise<void>;
  registerChild: (childData: any) => Promise<void>;
  refreshUser: () => Promise<void>;
}>({} as any);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [state, dispatch] = useReducer(authReducer, {
    user: null,
    token: null,
    isAuthenticated: false,
    loading: false,
    error: null,
  });

  const navigate = useNavigate();

  const parseRoleFromAuthResponse = (rolesString: string): 'PARENT' | 'CHILD' => {
    if (rolesString.includes('ROLE_PARENT')) return 'PARENT';
    if (rolesString.includes('ROLE_CHILD')) return 'CHILD';
    throw new Error('Unknown role in auth response');
  };

  const login = async (email: string, password: string, isChildLogin: boolean = false) => {
    dispatch({ type: 'LOGIN_START' });
    try {
      const authResponse = await authService.login(email, password);
      setAuthToken(authResponse.token);
      const role = parseRoleFromAuthResponse(authResponse.roles);
      const userResponse = await userService.getCurrentUser();
      const payload = {
        user: {
          ...userResponse,
          role: role
        },
        token: authResponse.token,
        expiresIn: authResponse.expiresIn
      };
      dispatch({ type: 'LOGIN_SUCCESS', payload: payload });
      const redirectPath = role === 'PARENT' ? '/parent/dashboard' : '/child/dashboard';
      navigate(redirectPath);
    } catch (error) {
      dispatch({ type: 'LOGIN_FAILURE', payload: error instanceof Error ? error.message : 'Login failed' });
      throw error;
    }
  };
  
  const refreshUser = async () => {
    if (!state.user?.role) return;
    const userResponse = await userService.getCurrentUser();
    dispatch({ type: 'UPDATE_USER', payload: { ...userResponse, role: state.user.role } });
  };

  const logout = () => {
    clearAuthToken();
    dispatch({ type: 'LOGOUT' });
    navigate('/login');
  };

  const registerParent = async (email: string, password: string, firstName: string, lastName: string) => {
    dispatch({ type: 'REGISTER_START' });
    try {
      await authService.registerParent(email, password, firstName, lastName);
      dispatch({ type: 'REGISTER_SUCCESS' });
    } catch (error) {
      dispatch({ type: 'REGISTER_FAILURE', payload: error instanceof Error ? error.message : 'Registration failed' });
      throw error;
    }
  };

  const registerChild = async (childData: any) => {
    dispatch({ type: 'REGISTER_START' });
    try {
      await authService.registerChild(childData);
      dispatch({ type: 'REGISTER_SUCCESS' });
    } catch (error) {
      dispatch({ type: 'REGISTER_FAILURE', payload: error instanceof Error ? error.message : 'Registration failed' });
      throw error;
    }
  };

  return (
    <AuthContext.Provider value={{ state, dispatch, login, logout, registerParent, registerChild, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);

const authReducer = (state: AuthState, action: AuthAction): AuthState => {
  switch (action.type) {
    case 'LOGIN_START':
      return { ...state, loading: true, error: null };
    case 'LOGIN_SUCCESS':
      return {
        ...state,
        user: action.payload.user,
        token: action.payload.token,
        isAuthenticated: true,
        loading: false,
      };
    case 'LOGOUT':
      return {
        user: null,
        token: null,
        isAuthenticated: false,
        loading: false,
        error: null,
      };
    case 'REGISTER_START':
      return { ...state, loading: true, error: null };
    case 'REGISTER_SUCCESS':
      return { ...state, loading: false, error: null };
    case 'REGISTER_FAILURE':
      return { ...state, loading: false, error: action.payload };
    case 'LOGIN_FAILURE':
      return { ...state, loading: false, error: action.payload };
    case 'UPDATE_USER':
      return { ...state, user: action.payload };
    default:
      return state;
  }
};