import React from 'react';
import { TaskReadResponse } from '../../types/tasks';
import { TaskApprovalButtons } from './TaskApprovalButtons';

interface PendingApprovalListProps {
  tasks: TaskReadResponse[];
}

export const PendingApprovalList: React.FC<PendingApprovalListProps> = ({ tasks }) => (
  <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-4">
    <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
      Pending Approval
    </h3>
    
    {tasks.length === 0 ? (
      <div className="text-center py-4">
        <p className="text-sm text-gray-500 dark:text-gray-400">No tasks pending approval</p>
      </div>
    ) : (
      <div className="space-y-4">
        {tasks.map((task) => (
          <div key={task.id} className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900 dark:text-white truncate">
                {task.title}
              </p>
              <p className="text-sm text-gray-500 dark:text-gray-400">
                {task.points} points • {task.status}
              </p>
            </div>
            <div className="ml-4">
              <TaskApprovalButtons
                onApprove={() => console.log('Approve task:', task.id)}
                onReject={() => console.log('Reject task:', task.id)}
              />
            </div>
          </div>
        ))}
      </div>
    )}
  </div>
);