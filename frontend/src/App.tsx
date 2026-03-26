import { useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import { DashboardLayout } from './layouts/DashboardLayout/DashboardLayout';
import { useUiStore } from './store/uiStore';
import { DevicesPage } from './pages/DevicesPage/DevicesPage';
import { DeviceDetailsPage } from './pages/DeviceDetailsPage/DeviceDetailsPage';
import { ProfilePage } from './pages/ProfilePage/ProfilePage';
import { SearchResultsPage } from './pages/SearchResultsPage/SearchResultsPage';
import './styles/globals.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1
    },
  },
});

export const App = () => {
  const theme = useUiStore((state) => state.theme);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<DashboardLayout />}>
            <Route index element={<DevicesPage />} />
            <Route path="devices/:id" element={<DeviceDetailsPage />} /> 
            <Route path="search/results/:sessionId" element={<SearchResultsPage />} />
            <Route path="profile" element={<ProfilePage />} />
            <Route path="compatibility" element={<div>Правила совместимости</div>} />
            <Route path="mappings" element={<div>Нераспознанное оборудование</div>} />
          </Route>
        </Routes>
      </BrowserRouter>
      
      <Toaster 
        position="top-center" 
        toastOptions={{
          style: {
            background: 'var(--bg-surface)',
            color: 'var(--text-primary)',
            border: '1px solid var(--border-color)',
          }
        }}
      />
    </QueryClientProvider>
  );
};

export default App;
