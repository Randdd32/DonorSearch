import type { ReactNode } from 'react';
import { ArrowUp, ArrowDown, ArrowUpDown } from 'lucide-react';
import { clsx } from 'clsx';
import { Spinner } from '../Spinner/Spinner';
import styles from './Table.module.css';

export const TableCard = ({ children, isLoading }: { children: ReactNode; isLoading?: boolean }) => (
  <div className={styles.tableCard}>
    {isLoading ? (
      <Spinner fullPage size={40} />
    ) : (
      <div className={styles.tableWrapper}>
        <div className={styles.tableContent}>
          {children}
        </div>
      </div>
    )}
  </div>
);

export const Table = ({ children, className }: { children: ReactNode; className?: string }) => (
  <table className={clsx(styles.table, className)}>{children}</table>
);
export const TableHead = ({ children }: { children: ReactNode }) => <thead>{children}</thead>;
export const TableBody = ({ children }: { children: ReactNode }) => <tbody>{children}</tbody>;
export const TableRow = ({ children, className, onClick }: 
  { children: ReactNode; className?: string; onClick?: () => void }) => (
  <tr className={clsx(styles.row, className)} onClick={onClick}>{children}</tr>
);

interface ThProps {
  children: ReactNode;
  sortField?: string;
  currentSort?: string[];
  onSort?: (field: string, isShiftPressed: boolean) => void;
  className?: string;
}

export const TableHeader = ({ children, sortField, currentSort, onSort, className }: ThProps) => {
  const getIcon = () => {
    if (!sortField || !currentSort) return null;
    const sortStr = currentSort.find(s => s.startsWith(sortField));
    if (!sortStr) return <ArrowUpDown size={14} className={styles.sortIconIdle} />;
    return sortStr.endsWith('asc') ? <ArrowUp size={14} className={styles.sortIconActive} /> : <ArrowDown size={14} className={styles.sortIconActive} />;
  };

  return (
    <th
      className={clsx({ [styles.sortable]: !!sortField }, className)}
      onClick={(e) => sortField && onSort && onSort(sortField, e.shiftKey)}
    >
      {children} {getIcon()}
    </th>
  );
};

export const TableCell = ({ children, className }: { children: ReactNode; className?: string }) => (
  <td className={className}>{children}</td>
);