import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { taskService } from '../../services/taskService';
import { TaskReadResponse } from '../../types/tasks';
import { PrimaryButton, SecondaryButton } from '../../components/ui/Button';
import { PlusIcon, ArrowLeftIcon } from '@heroicons/react/24/outline';
import { TaskList } from '../../components/tasks/TaskList';
import { TaskForm } from '../../components/tasks/TaskForm';

export const ChildTaskManagement: React.FC = () => {
  const { childId } = useParams<{ childId: string }>();
  const navigate = useNavigate();
  const { state } = useAuth();
  const [tasks, setTasks] = useState<TaskReadResponse[]>([]);
  const [child, setChild] = useState<{id: number; firstName: string} | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isTaskFormOpen, setIsTaskFormOpen] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(false);

  // Try to get child info from localStorage cache (set by ChildrenList)
  useEffect(() => {
    if (childId) {
      const cachedChildren = localStorage.getItem('childrenCache');
      if (cachedChildren) {
        try {
          const parsedChildren = JSON.parse(cachedChildren);
          const foundChild = parsedChildren.find((c: any) => c.id === parseInt(childId));
          if (foundChild) {
            setChild(foundChild);
          } else {
            setChild({ id: parseInt(childId), firstName: `Child ${childId}` });
          }
        } catch (e) {
          console.warn('Could not parse cached children:', e);
          setChild({ id: parseInt(childId), firstName: `Child ${childId}` });
        }
      } else {
        setChild({ id: parseInt(childId), firstName: `Child ${childId}` });
      }
    }
  }, [childId]);

  useEffect(() => {
    const fetchTasks = async () => {
      try {
        setLoading(true);
        
        // Use the new endpoint to get tasks for specific child
        if (childId) {
          const tasksData = await taskService.getTasksByUserId(parseInt(childId));
          setTasks(tasksData);
        }
        
      } catch (err) {
        console.error('Error loading child tasks:', err);
        setError(err instanceof Error ? err.message : 'Failed to load child tasks');
      } finally {
        setLoading(false);
      }
    };
    
    if (state.user && childId) {
      fetchTasks();
    }
  }, [state.user, childId, refreshTrigger]);

  const handleCreateTask = () => {
    setIsTaskFormOpen(true);
  };

  const handleTaskCreated = (newTask: any) => {
    setTasks([...tasks, newTask]);
    // Also trigger a refresh to ensure we have the latest data
    setRefreshTrigger(!refreshTrigger);
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

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <button
            onClick={() => navigate('/parent/children')}
            className="flex items-center gap-2 text-indigo-600 dark:text-indigo-400 hover:text-indigo-800 dark:hover:text-indigo-300"
          >
            <ArrowLeftIcon className="w-5 h-5" />
            Back to Children
          </button>
          <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
            {child ? `${child.firstName}'s Tasks` : 'Child Tasks'}
          </h2>
        </div>
        <PrimaryButton onClick={handleCreateTask}>
          <PlusIcon className="w-5 h-5 mr-2" />
          Create Task
        </PrimaryButton>
      </div>

      <TaskList tasks={tasks} />
      
      <TaskForm
        isOpen={isTaskFormOpen}
        onClose={() => setIsTaskFormOpen(false)}
        onTaskCreated={handleTaskCreated}
        childId={childId ? parseInt(childId) : undefined}
      />
    </div>
  );
};