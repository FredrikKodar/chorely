import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { ThemePreferences, DEFAULT_THEME } from '../types/theme';
import { getThemePreferences, saveThemePreferences, applyTheme, initializeTheme } from '../services/themeService';

interface ThemeContextType {
  theme: ThemePreferences;
  updateTheme: (updates: Partial<ThemePreferences>) => void;
  resetTheme: () => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
const [theme, setTheme] = useState<ThemePreferences>(() => initializeTheme());

  const updateTheme = (updates: Partial<ThemePreferences>) => {
    const newTheme = { ...theme, ...updates };
    setTheme(newTheme);
    saveThemePreferences(newTheme);
    applyTheme(newTheme);
  };

  const resetTheme = () => {
    setTheme(DEFAULT_THEME);
    saveThemePreferences(DEFAULT_THEME);
    applyTheme(DEFAULT_THEME);
  };

  return (
    <ThemeContext.Provider value={{ theme, updateTheme, resetTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};
