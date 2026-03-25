import { create } from 'zustand';

type Theme = 'light' | 'dark';

interface UiState {
  theme: Theme;
  isSidebarOpen: boolean;
  toggleTheme: () => void;
  toggleSidebar: () => void;
  setTheme: (theme: Theme) => void;
}

const getInitialTheme = (): Theme => {
  const savedTheme = localStorage.getItem('app-theme') as Theme;
  if (savedTheme) return savedTheme;
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

export const useUiStore = create<UiState>((set) => ({
  theme: getInitialTheme(),
  isSidebarOpen: true,
  
  toggleTheme: () => set((state) => {
    const newTheme = state.theme === 'light' ? 'dark' : 'light';
    localStorage.setItem('app-theme', newTheme);
    document.documentElement.setAttribute('data-theme', newTheme);
    return { theme: newTheme };
  }),
  
  setTheme: (theme) => set(() => {
    localStorage.setItem('app-theme', theme);
    document.documentElement.setAttribute('data-theme', theme);
    return { theme };
  }),

  toggleSidebar: () => set((state) => ({ isSidebarOpen: !state.isSidebarOpen }))
}));