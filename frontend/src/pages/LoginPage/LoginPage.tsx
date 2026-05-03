import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { LogIn } from 'lucide-react';
import toast from 'react-hot-toast';
import { isAxiosError } from 'axios';
import { authService } from '../../services/auth.service';
import { useAuthStore } from '../../store/authStore';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { PASSWORD_REGEX } from '../../config/constants';
import { Card } from '../../components/ui/Card/Card';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import styles from './LoginPage.module.css';

export const LoginPage = () => {
  useDocumentTitle('Вход в систему');
  
  const navigate = useNavigate();
  const location = useLocation();
  const setAuth = useAuthStore((state) => state.setAuth);

  const [username, setUsername] = useState('');
  const[password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const from = location.state?.from?.pathname || '/';

  const handleLogin = async (e: React.SyntheticEvent) => {
    e.preventDefault();
    
    if (username.length < 2 || username.length > 100) {
      toast.error('Логин должен содержать от 2 до 100 символов');
      return;
    }
    
    if (!PASSWORD_REGEX.test(password)) {
      toast.error(
        'Неверный формат пароля. Проверьте правильность ввода.\nПароль должен содержать от 8 до 60 символов, минимум одну заглавную и строчную латинские буквы, одну цифру и один спецсимвол (!@#$%^&*_=+-).'
      );
      return;
    }

    try {
      setIsLoading(true);
      const data = await authService.login(username, password);
      
      setAuth(data.accessToken, { username: data.username, role: data.role });
      navigate(from, { replace: true });
    } catch (error: unknown) {
      console.error('Ошибка входа', error);
      
      if (isAxiosError(error)) {
        const status = error.response?.status;
        if (status === 400 || status === 401 || status === 404) {
          toast.error('Неверный логин или пароль');
        } else {
          toast.error('Произошла ошибка при подключении к серверу');
        }
      } else {
        toast.error('Неизвестная ошибка');
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <Card className={styles.card}>
        <div className={styles.header}>
          <h1 className={styles.title}>DonorSearch</h1>
          <p className={styles.subtitle}>Войдите в свою учетную запись</p>
        </div>

        <form onSubmit={handleLogin} className={styles.form}>
          <div className={styles.field}>
            <label className={styles.label}>Логин</label>
            <Input 
              type="text" 
              placeholder="Введите логин" 
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={isLoading}
              autoFocus
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Пароль</label>
            <Input 
              type="password" 
              placeholder="•••••••••••••••••••" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={isLoading}
            />
          </div>

          <Button type="submit" className={styles.submitBtn} isLoading={isLoading}>
            <LogIn size={18} />
            Войти
          </Button>
        </form>
      </Card>
    </div>
  );
};