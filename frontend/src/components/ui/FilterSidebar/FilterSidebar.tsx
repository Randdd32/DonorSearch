import type { ReactNode } from 'react';
import { X, RotateCcw } from 'lucide-react';
import { clsx } from 'clsx';
import { Button } from '../Button/Button';
import styles from './FilterSidebar.module.css';

interface FilterSidebarProps {
  title: string;
  isOpen: boolean;
  onClose: () => void;
  onReset: () => void;
  children: ReactNode;
}

export const FilterSidebar = ({ title, isOpen, onClose, onReset, children }: FilterSidebarProps) => {
  return (
    <>
      <div className={clsx(styles.backdrop, { [styles.open]: isOpen })} onClick={onClose} />
      <div className={clsx(styles.sidebar, { [styles.open]: isOpen })}>
        <div className={styles.header}>
          <h3 className={styles.title}>{title}</h3>
          <button onClick={onClose} className={styles.closeBtn}><X size={20} /></button>
        </div>

        <div className={styles.content}>
          {children}
        </div>

        <div className={styles.footer}>
          <Button variant="ghost" onClick={onReset} className={styles.resetBtn}>
            <RotateCcw size={16} /> Сбросить
          </Button>
          <Button onClick={onClose} className={styles.applyBtn}>
            Показать
          </Button>
        </div>
      </div>
    </>
  );
};