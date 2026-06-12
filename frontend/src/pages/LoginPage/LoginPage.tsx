import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { LogIn } from 'lucide-react';
import toast from 'react-hot-toast';
import { isAxiosError } from 'axios';
import { useForm, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { authService } from '../../services/auth.service';
import { useAuthStore } from '../../store/authStore';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { Card } from '../../components/ui/Card/Card';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { loginSchema, type LoginFormValues } from './loginSchema';
import styles from './LoginPage.module.css';

export const LoginPage = () => {
  useDocumentTitle('Вход в систему');
  
  const navigate = useNavigate();
  const location = useLocation();
  const setAuth = useAuthStore((state) => state.setAuth);

  const [isLoading, setIsLoading] = useState(false);
  const from = location.state?.from?.pathname || '/';

  const { 
    register, 
    handleSubmit, 
    control,
    setValue,
    formState: { errors } 
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      username: '',
      password: '',
    },
    mode: 'onTouched'
  });

  const usernameValue = useWatch({ control, name: 'username' });

  const onSubmit = async (data: LoginFormValues) => {
    try {
      setIsLoading(true);
      const res = await authService.login(data.username, data.password);
      
      setAuth(res.accessToken, { username: res.username, role: res.role });
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

        <form onSubmit={handleSubmit(onSubmit)} className={styles.form}>
          <div className={styles.field}>
            <label className={styles.label}>Логин</label>
            <Input 
              {...register('username')}
              type="text" 
              placeholder="Введите логин" 
              disabled={isLoading}
              autoFocus
              error={errors.username?.message}
              value={usernameValue}
              onClear={() => setValue('username', '', { shouldValidate: true })}
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Пароль</label>
            <Input 
              {...register('password')}
              type="password" 
              placeholder="•••••••••••••••••••" 
              disabled={isLoading}
              error={errors.password?.message}
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