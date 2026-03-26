import { AlertTriangle } from 'lucide-react';
import { clsx } from 'clsx';
import { Button } from '../Button/Button';
import styles from './ErrorState.module.css';

interface ErrorStateProps {
  title?: string;
  message?: string;
  onAction?: () => void;
  actionLabel?: string;
  className?: string;
}

export const ErrorState = ({
  title = 'Произошла ошибка',
  message = 'Что-то пошло не так при загрузке данных. Пожалуйста, попробуйте позже.',
  onAction,
  actionLabel = 'Вернуться назад',
  className,
}: ErrorStateProps) => {
  return (
    <div className={clsx(styles.container, className)}>
      <AlertTriangle size={48} className={styles.icon} />
      <h3 className={styles.title}>{title}</h3>
      <p className={styles.message}>{message}</p>
      {onAction && (
        <Button variant="secondary" onClick={onAction} className={styles.actionBtn}>
          {actionLabel}
        </Button>
      )}
    </div>
  );
};