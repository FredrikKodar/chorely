export interface AuthResponse {
  token: string;
  expiresIn: number;
  roles: string; // e.g., "[ROLE_PARENT]" or "[ROLE_CHILD]"
}

export interface User {
  id: number;
  email: string;
  role: 'PARENT' | 'CHILD';
  firstName: string;
  lastName: string;
  currentPoints: number;
  totalPoints: number;
  numTasksOpen: number;
  numTasksCompleted: number;
  numTasksTotal: number;
}

export interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}

export type AuthAction =
  | { type: 'LOGIN_START' }
  | { type: 'LOGIN_SUCCESS'; payload: { user: User; token: string; expiresIn: number } }
  | { type: 'LOGIN_FAILURE'; payload: string }
  | { type: 'LOGOUT' }
  | { type: 'REGISTER_START' }
  | { type: 'REGISTER_SUCCESS' }
  | { type: 'REGISTER_FAILURE'; payload: string };