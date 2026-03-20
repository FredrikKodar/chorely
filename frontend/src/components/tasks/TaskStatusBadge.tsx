import React from 'react';
import { TaskStatus } from '../../types/tasks';

export const TaskStatusBadge: React.FC<{ status: TaskStatus }> = ({ status }) => {
  const statusConfig = {
    ASSIGNED: { 
      text: 'Assigned', 
      color: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200' 
    },
    PENDING_APPROVAL: { 
      text: 'Pending', 
      color: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200' 
    },
    APPROVED: { 
      text: 'Approved', 
      color: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' 
    }
  };

  // Add safety check for undefined status
  const config = statusConfig[status] || statusConfig.ASSIGNED;

  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.color}`}>
      {config.text}
    </span>
  );
};