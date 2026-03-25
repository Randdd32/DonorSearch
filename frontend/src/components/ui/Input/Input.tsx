import type { InputHTMLAttributes, ReactNode } from 'react';
import { forwardRef } from 'react';
import { clsx } from 'clsx';
import styles from './Input.module.css';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  icon?: ReactNode;
  error?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className, icon, error, ...props }, ref) => {
    return (
      <div className={styles.wrapper}>
        <div className={styles.inputContainer}>
          {icon && <span className={styles.icon}>{icon}</span>}
          <input
            ref={ref}
            className={clsx(
              styles.input,
              { [styles.withIcon]: !!icon, [styles.hasError]: !!error },
              className
            )}
            {...props}
          />
        </div>
        {error && <span className={styles.errorMessage}>{error}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';