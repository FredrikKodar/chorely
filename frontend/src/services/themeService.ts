import { ThemePreferences, DEFAULT_THEME } from '../types/theme';

const THEME_STORAGE_KEY = 'themePreferences';

// Get current theme preferences from session storage
export const getThemePreferences = (): ThemePreferences => {
  if (typeof window !== 'undefined') {
    const savedPreferences = sessionStorage.getItem(THEME_STORAGE_KEY);
    if (savedPreferences) {
      try {
        const parsed = JSON.parse(savedPreferences);
        const validColorSchemes = ['indigo', 'emerald'];
        const validThemeModes = ['light', 'dark'];
      
        if (
          validColorSchemes.includes(parsed.colorScheme) &&
          validThemeModes.includes(parsed.themeMode)
        ) {
          return parsed;
        }      
      } catch (error) {
        console.error('Failed to parse theme preferences:', error);
      }
    }
  }
  return DEFAULT_THEME;
};

// Save theme preferences to session storage
export const saveThemePreferences = (preferences: ThemePreferences): void => {
  if (typeof window !== 'undefined') {
    sessionStorage.setItem(THEME_STORAGE_KEY, JSON.stringify(preferences));
  }
};

// Apply theme to document using CSS variables
export const applyTheme = (preferences: ThemePreferences): void => {
  const root = document.documentElement;
  
if (preferences.colorScheme === 'indigo') {
    // Light mode: indigo-600, indigo-700, indigo-400
    const primaryLight = '#6366f1';
    const primaryDark = '#4f46e5';
    const primaryLighter = '#818cf8';
    
    if (preferences.themeMode === 'dark') {
      // Dark mode: swap hierarchy - lighter primary, original dark
      root.style.setProperty('--primary-color', primaryLighter); // indigo-400 (lighter)
      root.style.setProperty('--primary-dark', primaryLight);   // indigo-600 (original)
      root.style.setProperty('--primary-light', primaryDark);   // indigo-700
    } else {
      // Light mode: normal hierarchy
      root.style.setProperty('--primary-color', primaryLight);  // indigo-600
      root.style.setProperty('--primary-dark', primaryDark);    // indigo-700
      root.style.setProperty('--primary-light', primaryLighter); // indigo-400
    }
  } else {
    // Emerald scheme
    const primaryLight = '#059669';
    const primaryDark = '#047857';
    const primaryLighter = '#34d399';
    
    if (preferences.themeMode === 'dark') {
      // Dark mode: swap hierarchy
      root.style.setProperty('--primary-color', primaryLighter); // emerald-400 (lighter)
      root.style.setProperty('--primary-dark', primaryLight);   // emerald-600 (original)
      root.style.setProperty('--primary-light', primaryDark);   // emerald-700
    } else {
      // Light mode: normal hierarchy
      root.style.setProperty('--primary-color', primaryLight);  // emerald-600
      root.style.setProperty('--primary-dark', primaryDark);    // emerald-700
      root.style.setProperty('--primary-light', primaryLighter); // emerald-400
    }
  }
};

// Initialize theme from saved preferences or use default
export const initializeTheme = (): ThemePreferences => {
  const preferences = getThemePreferences();
  applyTheme(preferences);
  return preferences;
};
