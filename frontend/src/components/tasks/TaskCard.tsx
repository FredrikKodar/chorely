import React from 'react';
import { TaskReadResponse } from '../../types/tasks';
import { TaskStatusBadge } from './TaskStatusBadge';
import { TaskToggleButton } from './TaskToggleButton';

interface TaskCardProps {
  task: TaskReadResponse;
  onClick: () => void;
  onToggle?: () => void;
  showToggle?: boolean;
}

export const TaskCard: React.FC<TaskCardProps> = ({ task, onClick, onToggle, showToggle = false }) => (
  <div
    className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg shadow-sm cursor-pointer hover:shadow-md transition-shadow"
    onClick={onClick}
  >
    <div className="p-4">
      <div className="flex justify-between items-start">
        <div>
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white">{task.title}</h3>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">{task.points} points</p>
        </div>
        <TaskStatusBadge status={task.status} />
      </div>
      <p className="mt-3 text-sm text-gray-600 dark:text-gray-400 line-clamp-2">{task.description}</p>
    </div>
    {showToggle && onToggle && (
      <div
        className="p-4 border-t border-gray-200 dark:border-gray-700"
        onClick={(e) => e.stopPropagation()}
      >
        <TaskToggleButton
          isCompleted={task.status === 'PENDING_APPROVAL'}
          onToggle={onToggle}
        />
      </div>
    )}
  </div>
);