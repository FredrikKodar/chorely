import React from 'react';
import { ClockIcon } from '@heroicons/react/24/outline';

export const PointsHistory: React.FC = () => {
  return (
    <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-6">
      <div className="text-center py-12">
        <div className="mx-auto w-16 h-16 rounded-full bg-gray-100 dark:bg-gray-700 flex items-center justify-center mb-4">
          <ClockIcon className="w-8 h-8 text-gray-400 dark:text-gray-500" />
        </div>
        <h3 className="text-lg font-medium text-gray-900 dark:text-white">
          Points History
        </h3>
        <p className="mt-2 text-sm text-gray-500 dark:text-gray-400">
          This feature requires a backend endpoint to fetch historical points data.
        </p>
        <p className="mt-1 text-xs text-gray-400 dark:text-gray-500">
          Endpoint: GET /api/users/{id}/points-history
        </p>
      </div>
    </div>
  );
};