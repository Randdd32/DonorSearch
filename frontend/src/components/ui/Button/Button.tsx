import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { clsx } from 'clsx';
import styles from './Button.module.css';
import { Loader2 } from 'lucide-react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  isLoading?: boolean;
}

export const Button = ({ 
  children, 
  variant = 'primary', 
  isLoading, 
  className, 
  disabled, 
  ...props 
}: ButtonProps) => {
  return (
    <button
      className={clsx(styles.button, styles[variant], className)}
      disabled={isLoading || disabled}
      {...props}
    >
      {isLoading && <Loader2 className={styles.spinner} size={16} />}
      {children}
    </button>
  );
};