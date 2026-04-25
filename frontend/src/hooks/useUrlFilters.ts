import { useSearchParams } from 'react-router-dom';

export type FilterValue = string | number | boolean | (string | number)[] | null | undefined;

export const getNumberArray = (searchParams: URLSearchParams, key: string): number[] => {
  const val = searchParams.get(key);
  return val ? val.split(',').map(Number).filter(n => !isNaN(n)) :[];
};

export const getNumber = (searchParams: URLSearchParams, key: string): number | undefined => {
  const val = searchParams.get(key);
  return val ? Number(val) : undefined;
};

export const getBoolean = (searchParams: URLSearchParams, key: string): boolean | undefined => {
  const val = searchParams.get(key);
  return val === 'true' ? true : val === 'false' ? false : undefined;
};

export const getString = (searchParams: URLSearchParams, key: string): string => {
  return searchParams.get(key) || '';
};

export const getStringArray = (searchParams: URLSearchParams, key: string): string[] => {
  const val = searchParams.get(key);
  return val ? val.split(',').filter(Boolean) :[];
};

export interface CommonFilters {
  [key: string]: FilterValue;
  page?: number;
  size?: number;
  search?: string;
  sort: string[];
}

export const useUrlFilters = <T extends Record<string, FilterValue>>(
  defaultSort: string | string[],
  parseExtraFilters: (params: URLSearchParams) => T
) => {
  const [searchParams, setSearchParams] = useSearchParams();

  const commonFilters: CommonFilters = {
    page: Number(searchParams.get('page')) || 0,
    size: Number(searchParams.get('size')) || 12,
    search: searchParams.get('search') || '',
    sort: searchParams.getAll('sort').length > 0 ? searchParams.getAll('sort') : (Array.isArray(defaultSort) ? defaultSort :[defaultSort]),
  };

  const filters = { ...commonFilters, ...parseExtraFilters(searchParams) };

  const updateFilters = (updates: Partial<CommonFilters & T>, resetPage = true) => {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      
      Object.entries(updates).forEach(([key, value]) => {
        next.delete(key); 
        if (value === null || value === '' || value === undefined || (Array.isArray(value) && value.length === 0)) {
          return; 
        }
        if (Array.isArray(value) && key === 'sort') {
          value.forEach(v => next.append(key, String(v)));
        } else if (Array.isArray(value)) {
          next.set(key, value.join(','));
        } else {
          next.set(key, String(value));
        }
      });
      
      if (resetPage && updates.page === undefined) next.set('page', '0');
      return next;
    });
  };

  const resetFilters = () => setSearchParams(new URLSearchParams());

  return { filters, updateFilters, resetFilters };
};