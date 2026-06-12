import type { ReactNode } from 'react';
import { clsx } from 'clsx';
import styles from './EmptyState.module.css';

interface EmptyStateProps {
  icon: ReactNode;
  title: string;
  message: string;
  action?: ReactNode;
  className?: string;
}

export const EmptyState = ({ icon, title, message, action, className }: EmptyStateProps) => {
  return (
    <div className={clsx(styles.container, className)}>
      <div className={styles.iconWrapper}>{icon}</div>
      <h3 className={styles.title}>{title}</h3>
      <p className={styles.message}>{message}</p>
      {action && <div className={styles.actionWrapper}>{action}</div>}
    </div>
  );
};