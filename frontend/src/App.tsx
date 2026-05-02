import { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import { useUiStore } from './store/uiStore';
import { useAuthStore } from './store/authStore';
import { Spinner } from './components/ui/Spinner/Spinner';
import { DashboardLayout } from './layouts/DashboardLayout/DashboardLayout';
import { ProtectedRoute } from './components/ProtectedRoute/ProtectedRoute';
import { DevicesPage } from './pages/DevicesPage/DevicesPage';
import { DeviceDetailsPage } from './pages/DeviceDetailsPage/DeviceDetailsPage';
import { ProfilePage } from './pages/ProfilePage/ProfilePage';
import { SearchResultsPage } from './pages/SearchResultsPage/SearchResultsPage';
import { MappingsPage } from './pages/MappingsPage/MappingsPage';
import { MappingEditPage } from './pages/MappingEditPage/MappingEditPage';
import { RulesPage } from './pages/RulesPage/RulesPage';
import { RuleEditPage } from './pages/RuleEditPage/RuleEditPage';
import { LoginPage } from './pages/LoginPage/LoginPage';
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
  const { isInitialized, checkAuth } = useAuthStore();

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  if (!isInitialized) {
    return <Spinner fullPage size={48} />;
  }

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<DashboardLayout />}>
              <Route index element={<DevicesPage />} />
              <Route path="devices/:id" element={<DeviceDetailsPage />} /> 
              <Route path="search/results/:sessionId" element={<SearchResultsPage />} />
              <Route path="profile" element={<ProfilePage />} />

              <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'SUPERADMIN']} />}>
                <Route path="compatibility" element={<RulesPage />} />
                <Route path="compatibility/:id" element={<RuleEditPage />} />
                <Route path="mappings" element={<MappingsPage />} />
                <Route path="mappings/:id" element={<MappingEditPage />} />
                {/* <Route path="users" element={<UsersPage />} /> В будущем */}
              </Route>
            </Route>
          </Route>
          
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
      
      <Toaster 
        position="top-center" 
        toastOptions={{
          style: {
            background: 'var(--bg-surface)',
            color: 'var(--text-primary)',
            border: '1px solid var(--border-color)',
            maxWidth: '500px',          
            wordBreak: 'break-word',    
            whiteSpace: 'pre-wrap',
            padding: '12px 16px'
          }
        }}
      />
    </QueryClientProvider>
  );
};

export default App;
