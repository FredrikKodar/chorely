import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { ThemeProvider } from './context/ThemeContext';

// Import real page components
import { Login } from './pages/auth/Login';
import { Register } from './pages/auth/Register';
import { ParentDashboard } from './pages/parent/Dashboard';
import { ChildDashboard } from './pages/child/Dashboard';
import { ChildTasks } from './pages/child/Tasks';
import { ChildrenList } from './pages/parent/Children';
import { ChildTaskManagement } from './pages/parent/ChildTasks';
import { TaskManagement } from './pages/parent/Tasks';
import { PointsHistory } from './pages/parent/History';
import { TaskDetail } from './pages/child/TaskDetail';
import ThemeSettings from './pages/settings/ThemeSettings';
import { SettingsLayout } from './layouts/SettingsLayout';

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
          <Route path="children" element={<ChildrenList />} />
          <Route path="children/:childId/tasks" element={<ChildTaskManagement />} />
          <Route path="tasks" element={<TaskManagement />} />
          <Route path="history" element={<PointsHistory />} />
        </Route>
      </Route>
      
      <Route element={<ProtectedRoute requiredRole="CHILD" />}>
        <Route path="/child/*" element={<ChildLayout />}>
          <Route path="dashboard" element={<ChildDashboard />} />
          <Route path="tasks" element={<ChildTasks />} />
          <Route path="tasks/:id" element={<TaskDetail />} />
        </Route>
      </Route>

      {/* Settings routes - accessible to both roles */}
      <Route element={<ProtectedRoute />}>
        <Route path="/settings" element={<SettingsLayout />}>
          <Route index element={<ThemeSettings />} />
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
        <ThemeProvider>
          <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
            <AppRoutes />
          </div>
        </ThemeProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
