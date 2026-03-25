import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { taskService } from '../../services/taskService';
import { TaskReadResponse } from '../../types/tasks';
import { SecondaryButton } from '../../components/ui/Button';
import { XMarkIcon } from '@heroicons/react/24/outline';
import { TaskStatusBadge } from '../../components/tasks/TaskStatusBadge';
import { TaskToggleButton } from '../../components/tasks/TaskToggleButton';

export const TaskDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { state } = useAuth();
  const [task, setTask] = useState<TaskReadResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchTask = async () => {
      try {
        if (!id) throw new Error('Task ID is required');
        
        setLoading(true);
        const data = await taskService.getTaskById(parseInt(id));
        setTask(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load task');
      } finally {
        setLoading(false);
      }
    };
    
    fetchTask();
  }, [id]);

  const handleToggleStatus = async () => {
    if (!task) return;
    
    try {
      const updatedTask = await taskService.toggleStatus(task.id);
      setTask(updatedTask);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update task');
    }
  };

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

  if (!task) {
    return (
      <div className="text-center py-12">
        <XMarkIcon className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-white">
          Task not found
        </h3>
        <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
          The requested task does not exist.
        </p>
      </div>
    );
  }

  return (
    <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-6">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
          Task Details
        </h2>
        <button
          onClick={() => navigate('/child/dashboard')}
          className="text-gray-400 hover:text-gray-500 dark:text-gray-500 dark:hover:text-gray-400"
        >
          <XMarkIcon className="w-6 h-6" />
        </button>
      </div>

      <div className="space-y-6">
        <div>
          <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-2">
            {task.title}
          </h3>
          <div className="flex items-center gap-2">
            <span className="text-sm text-gray-500 dark:text-gray-400">
              {task.points} points
            </span>
            <TaskStatusBadge status={task.status} />
          </div>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-900 dark:text-white mb-1">
            Description
          </h4>
          <p className="text-gray-600 dark:text-gray-400">
            {task.description || 'No description provided'}
          </p>
        </div>

        <div>
          <h4 className="text-sm font-medium text-gray-900 dark:text-white mb-1">
            Status
          </h4>
          <div className="flex items-center gap-3">
            <TaskStatusBadge status={task.status} />
            {state.user?.role === 'CHILD' && task.status !== 'APPROVED' && (
              <TaskToggleButton
                isCompleted={task.status === 'PENDING_APPROVAL'}
                onToggle={handleToggleStatus}
              />
            )}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <h4 className="text-sm font-medium text-gray-900 dark:text-white mb-1">
              Created
            </h4>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {new Date(task.created).toLocaleString()}
            </p>
          </div>
          <div>
            <h4 className="text-sm font-medium text-gray-900 dark:text-white mb-1">
              Last Updated
            </h4>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {new Date(task.updated).toLocaleString()}
            </p>
          </div>
        </div>

        <div className="flex gap-2 pt-4">
          <SecondaryButton onClick={() => navigate('/child/dashboard')}>
            Back to Tasks
          </SecondaryButton>
        </div>
      </div>
    </div>
  );
};