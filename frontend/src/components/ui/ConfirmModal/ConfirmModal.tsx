import { AlertTriangle, X } from 'lucide-react';
import { clsx } from 'clsx';
import { Button } from '../Button/Button';
import styles from './ConfirmModal.module.css';

interface ConfirmModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  confirmLabel?: string;
  variant?: 'danger' | 'primary';
  isLoading?: boolean;
}

export const ConfirmModal = ({
  isOpen, 
  onClose, 
  onConfirm, 
  title, 
  message, 
  confirmLabel = 'Подтвердить', 
  variant = 'danger', 
  isLoading 
}: ConfirmModalProps) => {
  if (!isOpen) return null;

  return (
    <>
      <div className={styles.backdrop} onClick={onClose} />
      <div className={styles.modal}>
        <button className={styles.closeBtn} onClick={onClose} disabled={isLoading}>
          <X size={20} />
        </button>
        
        <div className={styles.content}>
          <div className={clsx(styles.iconWrapper, styles[variant])}>
            <AlertTriangle size={24} />
          </div>
          <div className={styles.textSide}>
            <h3 className={styles.title}>{title}</h3>
            <p className={styles.message}>{message}</p>
          </div>
        </div>

        <div className={styles.footer}>
          <Button variant="ghost" onClick={onClose} disabled={isLoading}>
            Отмена
          </Button>
          <Button variant={variant} onClick={onConfirm} isLoading={isLoading}>
            {confirmLabel}
          </Button>
        </div>
      </div>
    </>
  );
};