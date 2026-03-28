import { User, Shield, Calendar, LogOut } from 'lucide-react';
import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import styles from './ProfilePage.module.css';

export const ProfilePage = () => {
  return (
    <div className={styles.container}>
      <div>
        <h1 className={styles.title}>Профиль пользователя</h1>
        <p className={styles.subtitle}>Данные вашей учетной записи</p>
      </div>

      <Card className={styles.profileCard}>
        <div className={styles.avatarSection}>
          <div className={styles.avatarLarge}>A</div>
          <div>
            <h2 className={styles.avatarName}>Admin</h2>
            <span className={styles.avatarRole}>Аккаунт администратора</span>
          </div>
        </div>

        <div className={styles.infoList}>
          <div className={styles.infoRow}>
            <div className={styles.labelWrapper}>
              <User size={18} className={styles.icon} />
              <span className={styles.label}>Логин:</span>
            </div>
            <span className={styles.value}>Admin</span>
          </div>

          <div className={styles.infoRow}>
            <div className={styles.labelWrapper}>
              <Shield size={18} className={styles.icon} />
              <span className={styles.label}>Роль:</span>
            </div>
            <span className={styles.value}>Суперадминистратор</span>
          </div>

          <div className={styles.infoRow}>
            <div className={styles.labelWrapper}>
              <Calendar size={18} className={styles.icon} />
              <span className={styles.label}>Дата создания:</span>
            </div>
            <span className={styles.value}>10.01.2023</span>
          </div>
        </div>

        <div className={styles.actions}>
          <Button variant="danger" onClick={() => window.location.href = '/'}>
            <LogOut size={16} />
            Выйти из аккаунта
          </Button>
        </div>
      </Card>
    </div>
  );
};