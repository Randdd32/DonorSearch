import { User, Shield, Calendar, LogOut } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { ROLE_LABELS } from '../../types/auth';
import { useAuthStore } from '../../store/authStore';
import { authService } from '../../services/auth.service';
import { usersService } from '../../services/users.service';
import { formatDateTime } from '../../utils/formatters';
import styles from './ProfilePage.module.css';

export const ProfilePage = () => {
  useDocumentTitle('Мой профиль');
  const navigate = useNavigate();
  const { logout } = useAuthStore();

  const { data: profile, isLoading, isError } = useQuery({
    queryKey: ['userProfile'],
    queryFn: () => usersService.getMe()
  });

  const handleLogout = async () => {
    try {
      await authService.logout();
    } finally {
      logout();
      navigate('/login');
    }
  };

  if (isLoading) return <Spinner fullPage size={40} />;
  
  if (isError || !profile) {
    return <ErrorState message="Не удалось загрузить данные профиля." />;
  }

  const roleLabel = profile ? ROLE_LABELS[profile.role] : '';
  const accountTypeLabel = (profile?.role === 'ADMIN' || profile?.role === 'SUPERADMIN') 
    ? 'Аккаунт администратора' 
    : 'Аккаунт пользователя';

  return (
    <div className={styles.container}>
      <div>
        <h1 className={styles.title}>Профиль пользователя</h1>
        <p className={styles.subtitle}>Данные вашей учетной записи</p>
      </div>

      <Card className={styles.profileCard}>
        <div className={styles.avatarSection}>
          <div className={styles.avatarLarge}>{profile.username.charAt(0).toUpperCase()}</div>
          <div>
            <h2 className={styles.avatarName}>{profile.username}</h2>
            <span className={styles.avatarRole}>{accountTypeLabel}</span>
          </div>
        </div>

        <div className={styles.infoList}>
          <div className={styles.infoRow}>
            <div className={styles.labelWrapper}>
              <User size={18} className={styles.icon} />
              <span className={styles.label}>Логин:</span>
            </div>
            <span className={styles.value}>{profile.username}</span>
          </div>

          <div className={styles.infoRow}>
            <div className={styles.labelWrapper}>
              <Shield size={18} className={styles.icon} />
              <span className={styles.label}>Роль:</span>
            </div>
            <span className={styles.value}>{roleLabel}</span>
          </div>

          <div className={styles.infoRow}>
            <div className={styles.labelWrapper}>
              <Calendar size={18} className={styles.icon} />
              <span className={styles.label}>Дата создания:</span>
            </div>
            <span className={styles.value}>{formatDateTime(profile.createdAt)}</span>
          </div>
        </div>

        <div className={styles.actions}>
          <Button variant="danger" onClick={handleLogout}>
            <LogOut size={16} />
            Выйти из аккаунта
          </Button>
        </div>
      </Card>
    </div>
  );
};