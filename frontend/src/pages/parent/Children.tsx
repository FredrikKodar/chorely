import React, { useEffect, useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { userService } from '../../services/userService';
import { UserIdAndFirstNameResponse } from '../../types/user';
import { PrimaryButton, SecondaryButton } from '../../components/ui/Button';
import { PlusIcon, UserCircleIcon, PencilIcon } from '@heroicons/react/24/outline';
import { ChildRegistrationForm } from '../../components/children/ChildRegistrationForm';
import { ChildEditForm } from '../../components/children/ChildEditForm';

export const ChildrenList: React.FC = () => {
  const { state } = useAuth();
  const navigate = useNavigate();
  const [children, setChildren] = useState<UserIdAndFirstNameResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isChildFormOpen, setIsChildFormOpen] = useState(false);
  const [isEditFormOpen, setIsEditFormOpen] = useState(false);
  const [selectedChild, setSelectedChild] = useState<UserIdAndFirstNameResponse | null>(null);

  const fetchChildren = async () => {
    try {
      setLoading(true);
      const data = await userService.getChildren();
      setChildren(data);
    } catch (error) {
      setError(error instanceof Error ? error.message : 'Failed to load children');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (state.user) {
      fetchChildren();
    }
  }, [state.user]);

  const handleAddChild = () => {
    setIsChildFormOpen(true);
  };

  const handleChildRegistered = () => {
    fetchChildren();
  };

  // Cache children data in localStorage for use in ChildTasks page
  useEffect(() => {
    if (children.length > 0) {
      localStorage.setItem('childrenCache', JSON.stringify(children));
    }
  }, [children]);

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
    <div className="bg-white dark:bg-gray-800 border border-gray-200 dark:border-gray-700 rounded-lg p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
          My Children
        </h2>
        <PrimaryButton onClick={handleAddChild}>
          <PlusIcon className="w-5 h-5 mr-2" />
          Add Child
        </PrimaryButton>
      </div>

      {children.length === 0 ? (
        <div className="text-center py-12">
          <UserCircleIcon className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-white">
            No children registered
          </h3>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Get started by adding your first child.
          </p>
          <div className="mt-6">
            <PrimaryButton onClick={handleAddChild}>
              <PlusIcon className="w-5 h-5 mr-2" />
              Add First Child
            </PrimaryButton>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {children.map((child) => (
            <div 
              key={child.id} 
              className="bg-gray-50 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <div className="flex items-center">
                <div className="w-12 h-12 rounded-full bg-indigo-100 dark:bg-indigo-900/20 flex items-center justify-center mr-3">
                  <UserCircleIcon className="w-6 h-6 text-indigo-600 dark:text-indigo-400" />
                </div>
                <div>
                  <h3 className="font-medium text-gray-900 dark:text-white">
                    {child.firstName}
                  </h3>
                  <p className="text-sm text-gray-500 dark:text-gray-400">
                    ID: {child.id}
                  </p>
                </div>
              </div>
              <div className="mt-4 flex gap-2">
                <SecondaryButton onClick={() => navigate(`/parent/children/${child.id}/tasks`)}>
                  View Tasks
                </SecondaryButton>
                <SecondaryButton onClick={() => {
                  setSelectedChild(child);
                  setIsEditFormOpen(true);
                }}>
                  <PencilIcon className="w-4 h-4 mr-2" />
                  Edit
                </SecondaryButton>
              </div>
            </div>
          ))}
        </div>
      )}

    <ChildRegistrationForm
      isOpen={isChildFormOpen}
      onClose={() => setIsChildFormOpen(false)}
      onChildRegistered={handleChildRegistered}
    />
    
    <ChildEditForm
      isOpen={isEditFormOpen}
      onClose={() => setIsEditFormOpen(false)}
      child={selectedChild}
      onChildUpdated={handleChildRegistered}
    />
    </div>
  );
};