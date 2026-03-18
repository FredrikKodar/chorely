import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';

// Temporary page components for development
const Login = () => <div className="p-4">Login Page</div>;
const Register = () => <div className="p-4">Register Page</div>;
const ParentLayout = () => <div className="p-4">Parent Layout</div>;
const ChildLayout = () => <div className="p-4">Child Layout</div>;
const ParentDashboard = () => <div className="p-4">Parent Dashboard</div>;
const ChildDashboard = () => <div className="p-4">Child Dashboard</div>;
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