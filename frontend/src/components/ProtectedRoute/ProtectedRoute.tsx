import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';
import { ErrorState } from '../ui/ErrorState/ErrorState';
import type { UserRole } from '../../types/auth';

interface ProtectedRouteProps {
  allowedRoles?: UserRole[];
}

export const ProtectedRoute = ({ allowedRoles }: ProtectedRouteProps) => {
  const { isAuthenticated, user } = useAuthStore();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (allowedRoles && user && !allowedRoles.includes(user.role)) {
    return (
      <ErrorState 
        title="Доступ запрещен (403)"
        message="У вас нет прав для просмотра этой страницы."
        actionLabel="Вернуться на главную"
        onAction={() => window.location.href = '/'}
      />
    );
  }

  return <Outlet />;
};