import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, AlertTriangle } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';
import { useForm, Controller, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { clsx } from 'clsx';
import { useAuthStore } from '../../store/authStore';
import { usersService } from '../../services/users.service';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Select } from '../../components/ui/Select/Select';
import { Card } from '../../components/ui/Card/Card';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { formatDateTime } from '../../utils/formatters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import type { UserDto } from '../../types/auth';
import { getUserSchema, type UserFormValues } from './userSchema';
import styles from '../../styles/layouts/editPageLayout.module.css';

export const UserEditPage = () => {
  const { id } = useParams<{ id: string }>();
  const isNew = id === 'new';
  useDocumentTitle(isNew ? 'Новый пользователь' : 'Редактирование пользователя');

  const { data: originalData, isLoading, isError } = useQuery({
    queryKey: ['user', id],
    queryFn: () => usersService.getById(Number(id)),
    enabled: !isNew
  });

  if (isLoading) return <Spinner fullPage size={40} />;
  if (isError) return <ErrorState message="Не удалось получить данные пользователя." />;

  return <UserForm key={isNew ? 'new' : originalData?.id} isNew={isNew} id={id!} originalData={originalData} />;
};

interface UserFormProps {
  isNew: boolean;
  id: string;
  originalData?: UserDto;
}

const UserForm = ({ isNew, id, originalData }: UserFormProps) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user: currentUser, logout } = useAuthStore();

  const isSelf = currentUser?.username === originalData?.username;
  const isSuperAdmin = currentUser?.role === 'SUPERADMIN';

  const {
    register,
    handleSubmit,
    control,
    formState: { errors }
  } = useForm<UserFormValues>({
    resolver: zodResolver(getUserSchema(isNew)),
    defaultValues: {
      username: originalData?.username || '',
      password: '',
      role: originalData?.role || 'USER',
    },
    mode: 'onTouched',
  });

  const passwordValue = useWatch({ control, name: 'password' });

  const saveMutation = useMutation({
    mutationFn: async (data: UserFormValues) => {
      if (isNew) {
        return usersService.create({ 
          username: data.username, 
          password: data.password!, 
          role: data.role 
        });
      } else {
        return usersService.update(Number(id), { 
          password: data.password || undefined, 
          role: data.role 
        });
      }
    },
    onSuccess: () => {
      toast.success(isNew ? 'Пользователь создан' : 'Данные обновлены');
      queryClient.invalidateQueries({ queryKey: ['users'] });
      
      if (isSelf && passwordValue && passwordValue.length > 0) {
        logout(); 
        navigate('/login', { replace: true });
        toast('Вы были выведены из системы в связи со сменой пароля.', { icon: '🔒' });
      } else {
        navigate('/users');
      }
    },
    onError: (e: Error | AxiosError<{ message: string }>) => {
      if (!(e instanceof AxiosError)) {
        toast.error(e.message || 'Ошибка валидации');
      }
    }
  });

  if (!isNew && originalData) {
    if (!isSuperAdmin && originalData.role !== 'USER' && !isSelf) {
      return (
        <ErrorState 
          title="Доступ запрещен (403)"
          message="У вас нет прав для редактирования этого профиля."
          actionLabel="Назад к списку"
          onAction={() => navigate('/users')}
        />
      );
    }
  }

  const roleOptions = isSuperAdmin 
    ?[
        { value: 'USER', label: 'Пользователь (USER)' },
        { value: 'ADMIN', label: 'Администратор (ADMIN)' }
      ]
    :[];

  const onSubmit = (data: UserFormValues) => {
    saveMutation.mutate(data);
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Button variant="ghost" onClick={() => navigate('/users')} className={styles.backBtn}>
          <ArrowLeft size={16} /> Назад
        </Button>
        <div>
          <h1 className={styles.title}>{isNew ? 'Добавление пользователя' : 'Редактирование профиля'}</h1>
          <p className={styles.subtitle}>Управление доступом к системе DonorSearch</p>
        </div>
      </div>

      <div className={styles.content}>
        <Card className={styles.formCard}>
          <h3 className={styles.cardTitle}>Учетные данные</h3>
          
          <form id="user-form" onSubmit={handleSubmit(onSubmit)} className={styles.formContents}>
            <div className={styles.field}>
              <label className={styles.label}>Логин {isNew && <span className={styles.req}>*</span>}</label>
              <Input 
                {...register('username')}
                disabled={!isNew}
                placeholder="Введите логин (от 2 до 100 символов)"
                error={errors.username?.message}
              />
              {!isNew && <p className={styles.hint}>Логин нельзя изменить после создания.</p>}
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Пароль {isNew && <span className={styles.req}>*</span>}</label>
              <Input 
                {...register('password')}
                type="password"
                placeholder={isNew ? 'Задайте безопасный пароль' : 'Введите новый пароль, чтобы изменить текущий'}
                error={errors.password?.message}
              />
              {isSelf && passwordValue && passwordValue.length > 0 && (
                <div className={styles.warningBanner}>
                  <AlertTriangle size={18} />
                  Внимание: смена пароля приведет к завершению всех активных сессий.
                </div>
              )}
            </div>

            {(!isSelf && isSuperAdmin) && (
              <div className={styles.field}>
                <label className={styles.label}>Системная роль <span className={styles.req}>*</span></label>
                <Controller
                  name="role"
                  control={control}
                  render={({ field }) => (
                    <Select 
                      value={field.value}
                      onChange={field.onChange}
                      options={roleOptions}
                      isSearchable={false}
                    />
                  )}
                />
                {errors.role && <span className={clsx(styles.hint, styles.req)}>{errors.role.message}</span>}
              </div>
            )}
          </form>

          <div className={styles.actions}>
            <Button variant="secondary" onClick={() => navigate('/users')}>Отмена</Button>
            <Button 
              type="submit" 
              form="user-form" 
              isLoading={saveMutation.isPending}
            >
              <Save size={16} /> {isNew ? 'Создать аккаунт' : 'Сохранить изменения'}
            </Button>
          </div>
        </Card>

        {!isNew && originalData && (
          <div className={styles.metaColumn}>
            <Card className={styles.metaCard}>
              <h3 className={styles.cardTitle}>Системная информация</h3>
              
              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>ID</span>
                <span className={styles.metaValue}>{originalData.id}</span>
              </div>
              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>Создан</span>
                <span className={styles.metaValue}>{formatDateTime(originalData.createdAt)}</span>
              </div>
              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>Обновлен</span>
                <span className={styles.metaValue}>{formatDateTime(originalData.updatedAt)}</span>
              </div>
            </Card>
          </div>
        )}
      </div>
    </div>
  );
};