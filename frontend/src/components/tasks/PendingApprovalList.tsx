import React from 'react';
import { TaskReadResponse } from '../../types/tasks';
import { TaskApprovalButtons } from './TaskApprovalButtons';
import { taskService } from '../../services/taskService';

interface PendingApprovalListProps {
  tasks: TaskReadResponse[];
  onTaskUpdated: (task: TaskReadResponse) => void;
}

export const PendingApprovalList: React.FC<PendingApprovalListProps> = ({ tasks, onTaskUpdated }) => (
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
                onApprove={async () => {
                  try {
                    const updatedTask = await taskService.approveTask(task.id);
                    onTaskUpdated(updatedTask);
                  } catch (err) {
                    console.error('Approval failed:', err);
                  }
                }}
                onReject={async () => {
                  try {
                    const updatedTask = await taskService.updateTask(task.id, { status: 'ASSIGNED' });
                    onTaskUpdated(updatedTask);
                  } catch (err) {
                    console.error('Rejection failed:', err);
                  }
                }}
              />
            </div>
          </div>
        ))}
      </div>
    )}
  </div>
);