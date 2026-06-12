import { Loader2 } from 'lucide-react';
import { clsx } from 'clsx';
import styles from './Spinner.module.css';

interface SpinnerProps {
  size?: number;
  className?: string;
  fullPage?: boolean;
}

export const Spinner = ({ size = 24, className, fullPage = false }: SpinnerProps) => {
  const spinner = <Loader2 size={size} className={clsx(styles.spinner, className)} />;

  if (fullPage) {
    return <div className={styles.fullPage}>{spinner}</div>;
  }

  return spinner;
};