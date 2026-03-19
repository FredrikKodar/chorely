import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { userService } from '../../services/userService';
import { taskService } from '../../services/taskService';
import { UserDashboardCard } from '../../components/dashboard/UserDashboardCard';
import { TaskList } from '../../components/tasks/TaskList';
import { QuickStats } from '../../components/dashboard/QuickStats';
import { PendingApprovalList } from '../../components/tasks/PendingApprovalList';
import { TaskReadResponse } from '../../types/tasks';

export const ParentDashboard: React.FC = () => {
  const { state } = useAuth();
  const [tasks, setTasks] = useState<TaskReadResponse[]>([]);
  const [pendingTasks, setPendingTasks] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        
        // Fetch user tasks
        const tasksData = await taskService.getTasks();
        setTasks(tasksData);
        
        // Fetch pending approval tasks
        const pendingData = await taskService.getPendingApproval();
        setPendingTasks(pendingData);
        
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };
    
    if (state.user) {
      fetchData();
    }
  }, [state.user]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border-l-4 border-red-400 p-4">
        <div className="flex">
          <div className="flex-shrink-0">
            <svg className="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
          </div>
          <div className="ml-3">
            <p className="text-sm text-red-700">
              {error}
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2">
          {state.user && (
            <UserDashboardCard user={state.user} className="mb-6" />
          )}
          <TaskList tasks={tasks} title="Recent Tasks" />
        </div>
        <div className="space-y-6">
          {state.user && (
            <QuickStats user={state.user} />
          )}
          <PendingApprovalList tasks={pendingTasks} />
        </div>
      </div>
    </div>
  );
};