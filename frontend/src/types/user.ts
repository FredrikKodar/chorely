export interface UserIdAndFirstNameResponse {
  id: number;
  firstName: string;
}

export interface UserResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  currentPoints: number;
  totalPoints: number;
  numTasksOpen: number;
  numTasksCompleted: number;
  numTasksTotal: number;
}