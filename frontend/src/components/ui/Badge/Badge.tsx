import type { ReactNode } from 'react';
import { clsx } from 'clsx';
import styles from './Badge.module.css';

interface BadgeProps {
  children: ReactNode;
  variant?: 'info' | 'success' | 'warning' | 'danger' | 'default';
  className?: string;
}

export const Badge = ({ children, variant = 'default', className }: BadgeProps) => {
  return (
    <span className={clsx(styles.badge, styles[variant], className)}>
      {children}
    </span>
  );
};