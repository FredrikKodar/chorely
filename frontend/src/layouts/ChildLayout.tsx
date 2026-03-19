import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { HomeIcon, ClipboardDocumentListIcon, CogIcon } from '@heroicons/react/24/outline';

const navigation = [
  { name: 'Dashboard', href: '/child/dashboard', icon: HomeIcon },
  { name: 'Tasks', href: '/child/tasks', icon: ClipboardDocumentListIcon },
];

export const ChildLayout: React.FC = () => {
  const { state, logout } = useAuth();
  const location = useLocation();

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      {/* Mobile header */}
      <div className="bg-emerald-600 dark:bg-emerald-800 pb-32">
        <header className="py-4 px-4 sm:px-6 lg:px-8 flex items-center justify-between">
          <div className="flex items-center">
            <h1 className="text-white text-xl font-semibold">My Tasks</h1>
          </div>
          <div className="flex items-center gap-4">
            <span className="text-white text-sm">
              Welcome, {state.user?.firstName || 'Child'}
            </span>
            <button
              onClick={logout}
              className="text-white hover:text-gray-200 text-sm flex items-center gap-1"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
              </svg>
              Sign out
            </button>
          </div>
        </header>
        
        {/* Desktop navigation */}
        <nav className="hidden md:flex md:flex-col md:space-y-1 px-4 sm:px-6 lg:px-8">
          {navigation.map((item) => (
            <Link
              key={item.name}
              to={item.href}
              className={`flex items-center px-3 py-2 text-sm font-medium rounded-md ${
                location.pathname === item.href
                  ? 'bg-emerald-700 text-white'
                  : 'text-emerald-100 hover:bg-emerald-700 hover:text-white'
              }`}
            >
              <item.icon className="w-5 h-5 mr-3" aria-hidden="true" />
              {item.name}
            </Link>
          ))}
        </nav>
      </div>

      {/* Main content */}
      <main className="-mt-32 pb-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow px-4 py-6 sm:px-6 lg:px-8">
            <Outlet />
          </div>
        </div>
      </main>

      {/* Bottom navigation for mobile */}
      <div className="fixed bottom-0 left-0 right-0 bg-white dark:bg-gray-800 border-t border-gray-200 dark:border-gray-700 md:hidden">
        <div className="flex justify-around items-center h-16">
          {navigation.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.href;

            return (
              <Link
                key={item.name}
                to={item.href}
                className={`flex flex-col items-center justify-center flex-1 h-full ${
                  isActive
                    ? 'text-emerald-600 dark:text-emerald-400'
                    : 'text-gray-700 dark:text-gray-300 hover:text-emerald-600 dark:hover:text-emerald-400'
                }`}
              >
                <Icon className="h-6 w-6" aria-hidden="true" />
                <span className="text-xs mt-1">{item.name}</span>
              </Link>
            );
          })}
        </div>
      </div>
    </div>
  );
};