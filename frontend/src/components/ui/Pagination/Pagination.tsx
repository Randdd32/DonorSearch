import { ChevronLeft, ChevronRight, MoreHorizontal } from 'lucide-react';
import { clsx } from 'clsx';
import styles from './Pagination.module.css';

interface PaginationProps {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
  pageSizeOptions?: number[];
  className?: string;
}

export const Pagination = ({
  currentPage,
  totalPages,
  totalItems,
  pageSize,
  onPageChange,
  onPageSizeChange,
  pageSizeOptions =[10, 20, 50, 100],
  className,
}: PaginationProps) => {
  if (totalItems === 0) return null;

  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalItems);

  const getPageNumbers = () => {
    const pages: (number | string)[] =[];
    if (totalPages <= 7) {
      for (let i = 0; i < totalPages; i++) pages.push(i);
    } else {
      if (currentPage <= 3) {
        pages.push(0, 1, 2, 3, 4, '...', totalPages - 1);
      } else if (currentPage >= totalPages - 4) {
        pages.push(0, '...', totalPages - 5, totalPages - 4, totalPages - 3, totalPages - 2, totalPages - 1);
      } else {
        pages.push(0, '...', currentPage - 1, currentPage, currentPage + 1, '...', totalPages - 1);
      }
    }
    return pages;
  };

  return (
    <div className={clsx(styles.container, className)}>
      <div className={styles.pagesRow}>
        <button
          className={styles.navBtn}
          disabled={currentPage === 0}
          onClick={() => onPageChange(currentPage - 1)}
        >
          <ChevronLeft size={20} />
        </button>

        {getPageNumbers().map((page, index) =>
          page === '...' ? (
            <div key={`dots-${index}`} className={styles.dots}>
              <MoreHorizontal size={18} />
            </div>
          ) : (
            <button
              key={page}
              className={clsx(styles.pageBtn, {[styles.active]: currentPage === page })}
              onClick={() => onPageChange(page as number)}
            >
              {(page as number) + 1}
            </button>
          )
        )}

        <button
          className={styles.navBtn}
          disabled={currentPage >= totalPages - 1}
          onClick={() => onPageChange(currentPage + 1)}
        >
          <ChevronRight size={20} />
        </button>
      </div>

      <div className={styles.infoRow}>
        {onPageSizeChange ? (
          <div className={styles.sizeSelector}>
            <select
              value={pageSize}
              onChange={(e) => onPageSizeChange(Number(e.target.value))}
              className={styles.select}
            >
              {pageSizeOptions.map(opt => (
                <option key={opt} value={opt}>{opt}</option>
              ))}
            </select>
          </div>
        ) : <div />}

        <div className={styles.stats}>
          Результаты: {startItem} - {endItem} из {totalItems}
        </div>
      </div>
    </div>
  );
};