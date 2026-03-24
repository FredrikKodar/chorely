import React from 'react';
import { useTheme } from '../../context/ThemeContext';
import { CogIcon } from '@heroicons/react/24/outline';

const ThemeSettings: React.FC = () => {
  const { theme, updateTheme, resetTheme } = useTheme();

  const handleColorSchemeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    updateTheme({ colorScheme: e.target.value as 'indigo' | 'emerald' });
  };

  const handleThemeModeChange = (mode: 'light' | 'dark') => {
    updateTheme({ themeMode: mode });
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center space-x-3">
        <CogIcon className="h-6 w-6 text-gray-500 dark:text-gray-400" />
        <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">Theme Settings</h2>
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-6 space-y-6">
        <div>
          <label htmlFor="colorScheme" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            Color Scheme
          </label>
          <select
            id="colorScheme"
            value={theme.colorScheme}
            onChange={handleColorSchemeChange}
            className="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 dark:border-gray-600 focus:outline-none focus:ring-primary focus:border-primary dark:focus:ring-primary-light dark:focus:border-primary-light sm:text-sm rounded-md bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100"
          >
            <option value="indigo">Indigo</option>
            <option value="emerald">Emerald</option>
          </select>
        </div>

        <div>
          <fieldset className="space-y-4">
            <legend className="text-sm font-medium text-gray-700 dark:text-gray-300">
              Theme Mode
            </legend>
            <div className="space-y-2">
              <div className="flex items-center">
                <input
                  id="theme-light"
                  name="theme-mode"
                  type="radio"
                  checked={theme.themeMode === 'light'}
                  onChange={() => handleThemeModeChange('light')}
                  className="focus:ring-primary h-4 w-4 text-primary border-gray-300 dark:border-gray-600"
                />
                <label htmlFor="theme-light" className="ml-3 block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Light
                </label>
              </div>
              <div className="flex items-center">
                <input
                  id="theme-dark"
                  name="theme-mode"
                  type="radio"
                  checked={theme.themeMode === 'dark'}
                  onChange={() => handleThemeModeChange('dark')}
                  className="focus:ring-primary h-4 w-4 text-primary border-gray-300 dark:border-gray-600"
                />
                <label htmlFor="theme-dark" className="ml-3 block text-sm font-medium text-gray-700 dark:text-gray-300">
                  Dark
                </label>
              </div>
            </div>
          </fieldset>
        </div>

        <div className="flex space-x-3">
          <button
            type="button"
            onClick={resetTheme}
            className="inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary dark:focus:ring-primary-light"
          >
            Reset to Default
          </button>
          <button
            type="button"
            disabled
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary dark:focus:ring-primary-light"
          >
            Save Preferences
          </button>
        </div>
      </div>
    </div>
  );
};

export default ThemeSettings;
