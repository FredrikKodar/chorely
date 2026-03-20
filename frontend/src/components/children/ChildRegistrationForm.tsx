import React, { useState } from 'react';
import { authService } from '../../services/authService';
import { PrimaryButton, SecondaryButton } from '../ui/Button';
import { XMarkIcon } from '@heroicons/react/24/outline';

interface ChildRegistrationFormProps {
  onClose: () => void;
  onChildRegistered: () => void;
  isOpen: boolean;
}

export const ChildRegistrationForm: React.FC<ChildRegistrationFormProps> = ({ 
  onClose, 
  onChildRegistered, 
  isOpen 
}) => {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    firstName: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    // Client-side validation for numeric password
    if (!/^\d+$/.test(formData.password)) {
      setError('Password must contain numbers only');
      setLoading(false);
      return;
    }

    if (formData.password.length < 8 || formData.password.length > 20) {
      setError('Password must be 8-20 characters long');
      setLoading(false);
      return;
    }

    try {
      console.log('👶 Registering child with data:', formData);
      await authService.registerChild(formData);
      console.log('✅ Child registered successfully');
      onChildRegistered();
      onClose();
    } catch (err) {
      console.error('❌ Child registration failed:', err);
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl w-full max-w-md">
        <div className="flex items-center justify-between p-4 border-b border-gray-200 dark:border-gray-700">
          <h3 className="text-lg font-medium text-gray-900 dark:text-white">
            Register Child
          </h3>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-500 dark:text-gray-500 dark:hover:text-gray-400"
          >
            <XMarkIcon className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-4 space-y-4">
          <div>
            <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Child's First Name
            </label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
              placeholder="e.g., Emily"
            />
          </div>

          <div>
            <label htmlFor="username" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Username
            </label>
            <input
              type="text"
              id="username"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
              placeholder="e.g., emily2024"
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              minLength={8}
              maxLength={20}
              pattern="[0-9]*"
              title="Password must be numeric and 8-20 characters"
              className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 bg-white dark:bg-gray-800 text-gray-900 dark:text-white"
              placeholder="Numeric password (8-20 digits)"
            />
            <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
              Password must be numeric and 8-20 characters long
            </p>
          </div>

          {error && (
            <div className="bg-red-50 border-l-4 border-red-400 p-3">
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
          )}

          <div className="flex justify-end gap-3 pt-4">
            <SecondaryButton type="button" onClick={onClose} disabled={loading}>
              Cancel
            </SecondaryButton>
            <PrimaryButton type="submit" disabled={loading}>
              {loading ? 'Registering...' : 'Register Child'}
            </PrimaryButton>
          </div>
        </form>
      </div>
    </div>
  );
};