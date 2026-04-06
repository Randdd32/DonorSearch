import type { InputHTMLAttributes, ReactNode } from 'react';
import { forwardRef } from 'react';
import { clsx } from 'clsx';
import { X } from 'lucide-react';
import styles from './Input.module.css';

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  icon?: ReactNode;
  error?: string;
  onClear?: () => void;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ className, icon, error, onClear, value, placeholder, title, ...props }, ref) => {
    const showClearButton = onClear && value && String(value).length > 0;
    const computedTitle = !value && placeholder ? placeholder : title;

    return (
      <div className={styles.wrapper}>
        <div className={styles.inputContainer}>
          {icon && <span className={styles.icon}>{icon}</span>}
          
          <input
            ref={ref}
            value={value}
            placeholder={placeholder}
            title={computedTitle}
            className={clsx(
              styles.input,
              { 
                [styles.withIcon]: !!icon, 
                [styles.hasError]: !!error,
                [styles.withClear]: showClearButton
              },
              className
            )}
            {...props}
          />

          {showClearButton && (
            <button 
              type="button" 
              onClick={onClear} 
              className={styles.clearButton}
              title="Очистить текст"
            >
              <X size={16} />
            </button>
          )}
        </div>
        {error && <span className={styles.errorMessage}>{error}</span>}
      </div>
    );
  }
);

Input.displayName = 'Input';