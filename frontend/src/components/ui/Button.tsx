import React from 'react';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  size?: 'sm' | 'md' | 'lg';
}

export const PrimaryButton: React.FC<ButtonProps> = ({ children, size = 'md', className = '', ...props }) => (
  <button
    className={`inline-flex items-center justify-center border border-transparent font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 dark:focus:ring-offset-gray-800 disabled:opacity-50 disabled:cursor-not-allowed
      ${size === 'sm' ? 'px-2.5 py-1.5 text-xs' : ''}
      ${size === 'md' ? 'px-4 py-2 text-sm' : ''}
      ${size === 'lg' ? 'px-6 py-3 text-base' : ''}
      ${className}
    `}
    {...props}
  >
    {children}
  </button>
);

export const SecondaryButton: React.FC<ButtonProps> = ({ children, size = 'md', className = '', ...props }) => (
  <button
    className={`inline-flex items-center justify-center border border-gray-300 dark:border-gray-500 font-medium rounded-md shadow-sm text-gray-700 dark:text-gray-100 bg-white dark:bg-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 dark:focus:ring-offset-gray-800 disabled:opacity-50 disabled:cursor-not-allowed
      ${size === 'sm' ? 'px-2.5 py-1.5 text-xs' : ''}
      ${size === 'md' ? 'px-4 py-2 text-sm' : ''}
      ${size === 'lg' ? 'px-6 py-3 text-base' : ''}
      ${className}
    `}
    {...props}
  >
    {children}
  </button>
);

export const DangerButton: React.FC<ButtonProps> = ({ children, size = 'md', className = '', ...props }) => (
  <button
    className={`inline-flex items-center justify-center border border-transparent font-medium rounded-md shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 dark:focus:ring-offset-gray-800 disabled:opacity-50 disabled:cursor-not-allowed
      ${size === 'sm' ? 'px-2.5 py-1.5 text-xs' : ''}
      ${size === 'md' ? 'px-4 py-2 text-sm' : ''}
      ${size === 'lg' ? 'px-6 py-3 text-base' : ''}
      ${className}
    `}
    {...props}
  >
    {children}
  </button>
);