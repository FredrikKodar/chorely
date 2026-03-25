import React from 'react';
import { TaskCard } from './TaskCard';
import { TaskReadResponse } from '../../types/tasks';

interface TaskListProps {
  tasks: TaskReadResponse[];
  showToggle?: boolean;
  onTaskToggle?: (taskId: number) => void;
}

export const TaskList: React.FC<TaskListProps> = ({ tasks, showToggle = false, onTaskToggle }) => (
  <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-4">
    {tasks.length === 0 ? (
      <div className="text-center py-8">
        <p className="text-gray-500 dark:text-gray-400">No tasks found</p>
      </div>
    ) : (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {tasks.map((task) => (
          <TaskCard
            key={task.id}
            task={task}
            onToggle={onTaskToggle ? () => onTaskToggle(task.id) : undefined}
            showToggle={showToggle}
          />
        ))}
      </div>
    )}
  </div>
);