export interface PageDto<T> {
  items: T[];
  itemsCount: number;
  currentPage: number;
  currentSize: number;
  totalPages: number;
  totalItems: number;
  isFirst: boolean;
  isLast: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}