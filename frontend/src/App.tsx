import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';

// Import real page components
import { Login } from './pages/auth/Login';
import { Register } from './pages/auth/Register';
import { ParentDashboard } from './pages/parent/Dashboard';
import { ChildDashboard } from './pages/child/Dashboard';

// Layout components
import { ParentLayout } from './layouts/ParentLayout';
import { ChildLayout } from './layouts/ChildLayout';
const NotFound = () => <div className="p-4">404 Not Found</div>;

function AppRoutes() {
  const { state } = useAuth();

  return (
    <Routes>
      {/* Public routes */}
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      
      {/* Protected routes */}
      <Route element={<ProtectedRoute requiredRole="PARENT" />}>
        <Route path="/parent/*" element={<ParentLayout />}>
          <Route path="dashboard" element={<ParentDashboard />} />
          <Route path="children" element={<div>Children List</div>} />
          <Route path="tasks" element={<div>Task Management</div>} />
          <Route path="history" element={<div>Points History</div>} />
        </Route>
      </Route>
      
      <Route element={<ProtectedRoute requiredRole="CHILD" />}>
        <Route path="/child/*" element={<ChildLayout />}>
          <Route path="dashboard" element={<ChildDashboard />} />
          <Route path="tasks" element={<div>Child Tasks</div>} />
          <Route path="tasks/:id" element={<div>Task Detail</div>} />
        </Route>
      </Route>
      
      {/* Redirect based on auth state */}
      <Route
        path="/"
        element={state.isAuthenticated ? (
          state.user?.role === 'PARENT' ? (
            <Navigate to="/parent/dashboard" />
          ) : (
            <Navigate to="/child/dashboard" />
          )
        ) : (
          <Navigate to="/login" />
        )}
      />
      
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
          <AppRoutes />
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;