export interface ThemePreferences {
  colorScheme: 'indigo' | 'emerald';
  themeMode: 'light' | 'dark';
}

export const DEFAULT_THEME: ThemePreferences = {
  colorScheme: 'indigo',
  themeMode: 'light'
};
