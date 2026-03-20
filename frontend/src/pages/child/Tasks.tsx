import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { taskService } from '../../services/taskService';
import { TaskReadResponse } from '../../types/tasks';
import { TaskList } from '../../components/tasks/TaskList';

export const ChildTasks: React.FC = () => {
  const { state } = useAuth();
  const [tasks, setTasks] = useState<TaskReadResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchTasks = async () => {
    const tasksData = await taskService.getTasks();
    setTasks(tasksData);
  };

  const handleTaskToggle = async (taskId: number) => {
    try {
      await taskService.toggleStatus(taskId);
      await fetchTasks();
    } catch (err) {
      console.error('Error toggling task status:', err);
      setError(err instanceof Error ? err.message : 'Failed to update task status');
    }
  };

  useEffect(() => {
    const loadTasks = async () => {
      try {
        setLoading(true);
        await fetchTasks();
      } catch (err) {
        console.error('Error loading tasks:', err);
        setError(err instanceof Error ? err.message : 'Failed to load tasks');
      } finally {
        setLoading(false);
      }
    };

    if (state.user) {
      loadTasks();
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
            <p className="text-sm text-red-700 dark:text-red-300">
              {error}
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
        My Tasks
      </h2>

      {tasks.length === 0 ? (
        <div className="text-center py-12">
          <div className="mx-auto h-12 w-12 text-gray-400" aria-hidden="true">
            <svg className="w-full h-full" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-white">
            No tasks assigned
          </h3>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Your parent hasn't assigned any tasks to you yet.
          </p>
        </div>
      ) : (
        <TaskList
          tasks={tasks}
          title="My Tasks"
          showToggle={true}
          onTaskToggle={handleTaskToggle}
        />
      )}
    </div>
  );
};
