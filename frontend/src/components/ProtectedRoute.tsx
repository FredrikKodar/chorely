import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export const ProtectedRoute: React.FC<{
  requiredRole?: 'PARENT' | 'CHILD';
  children?: React.ReactNode;
}> = ({ requiredRole, children }) => {
  const { state } = useAuth();

  if (!state.isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && state.user?.role !== requiredRole) {
    const redirectPath = state.user?.role === 'PARENT' 
      ? '/parent/dashboard' 
      : '/child/dashboard';
    return <Navigate to={redirectPath} replace />;
  }

  return children ? <>{children}</> : <Outlet />;
};