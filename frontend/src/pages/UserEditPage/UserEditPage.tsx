import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save, AlertTriangle } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';
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
import { PASSWORD_REGEX } from '../../config/constants';
import type { UserRole, UserDto } from '../../types/auth';
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

  const [username, setUsername] = useState(originalData?.username || '');
  const [password, setPassword] = useState('');
  const[role, setRole] = useState<UserRole>(originalData?.role || 'USER');

  const isSelf = currentUser?.username === originalData?.username;
  const isSuperAdmin = currentUser?.role === 'SUPERADMIN';

  const saveMutation = useMutation({
    mutationFn: async () => {
      const trimmedName = username.trim();
      if (isNew && (trimmedName.length < 2 || trimmedName.length > 100)) {
        throw new Error('Логин должен содержать от 2 до 100 символов');
      }

      if (isNew && !password) throw new Error('Для нового пользователя необходимо задать пароль');
      if (password && !PASSWORD_REGEX.test(password)) {
        throw new Error('Неверный формат пароля');
      }

      if (isNew) {
        return usersService.create({ username: trimmedName, password, role });
      } else {
        return usersService.update(Number(id), { password: password || undefined, role });
      }
    },
    onSuccess: () => {
      toast.success(isNew ? 'Пользователь создан' : 'Данные обновлены');
      queryClient.invalidateQueries({ queryKey: ['users'] });
      
      if (isSelf && password.length > 0) {
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
          
          <div className={styles.field}>
            <label className={styles.label}>Логин {isNew && <span className={styles.req}>*</span>}</label>
            <Input 
              value={username}
              onChange={(e) => isNew && setUsername(e.target.value)}
              disabled={!isNew}
              placeholder="Введите логин (от 2 до 100 символов)"
            />
            {!isNew && <p className={styles.hint}>Логин нельзя изменить после создания.</p>}
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Пароль {isNew && <span className={styles.req}>*</span>}</label>
            <Input 
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder={isNew ? 'Задайте безопасный пароль' : 'Введите новый пароль, чтобы изменить текущий'}
            />
            <p className={styles.hint}>
              От 8 до 60 символов, как минимум одна заглавная и одна строчная латинская буква, цифра, спецсимвол (!@#$%^&*_=+-).
            </p>
            {isSelf && password.length > 0 && (
              <div className={styles.warningBanner}>
                <AlertTriangle size={18} />
                Внимание: смена пароля приведет к завершению всех активных сессий. Вам придется заново войти в систему.
              </div>
            )}
          </div>

          {(!isSelf && isSuperAdmin) && (
            <div className={styles.field}>
              <label className={styles.label}>Системная роль <span className={styles.req}>*</span></label>
              <Select 
                value={role}
                onChange={(val) => setRole(val as UserRole)}
                options={roleOptions}
                isSearchable={false}
              />
            </div>
          )}

          <div className={styles.actions}>
            <Button variant="secondary" onClick={() => navigate('/users')}>Отмена</Button>
            <Button onClick={() => saveMutation.mutate()} isLoading={saveMutation.isPending}>
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