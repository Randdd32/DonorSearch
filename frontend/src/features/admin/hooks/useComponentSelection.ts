import { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../../config/api';
import { useDebounce } from '../../../hooks/useDebounce';
import { getComponentEndpoint } from '../../../utils/componentEndpoints';
import type { PageDto } from '../../../types/pagination';

export const useComponentSelection = (componentType: string, isOpen: boolean) => {
  const[page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sort, setSort] = useState<string[]>(['id,desc']);
  const[search, setSearch] = useState('');
  const [filters, setFilters] = useState<Record<string, string | number | boolean | number[]>>({});

  const debouncedSearch = useDebounce(search, 500);
  const endpoint = getComponentEndpoint(componentType);

  const queryParams = useMemo(() => {
    const params = new URLSearchParams();
    params.append('page', String(page));
    params.append('size', String(size));
    if (debouncedSearch) params.append('search', debouncedSearch);
    sort.forEach(s => params.append('sort', s));
    
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        if (Array.isArray(value)) value.forEach(v => params.append(key, String(v)));
        else params.append(key, String(value));
      }
    });
    return params;
  },[page, size, sort, debouncedSearch, filters]);

  const { data, isLoading } = useQuery({
    queryKey:['components-modal', endpoint, queryParams.toString()],
    queryFn: async () => {
      if (!endpoint) return null;
      const response = await apiClient.get<PageDto<Record<string, unknown>>>(endpoint, { params: queryParams });
      return response.data;
    },
    enabled: isOpen && !!endpoint,
  });

  const handleSort = (field: string, isShiftPressed: boolean) => {
    let currentSort =[...sort];
    const existingIndex = currentSort.findIndex(s => s.startsWith(field));
    let newDirection = 'asc';

    if (existingIndex >= 0) {
      const currentDir = currentSort[existingIndex].split(',')[1];
      newDirection = currentDir === 'asc' ? 'desc' : '';
    }

    const sortString = newDirection ? `${field},${newDirection}` : null;

    if (isShiftPressed) {
      if (existingIndex >= 0) {
        if (sortString) currentSort[existingIndex] = sortString;
        else currentSort.splice(existingIndex, 1);
      } else if (sortString) currentSort.push(sortString);
    } else {
      currentSort = sortString ? [sortString] : ['id,desc'];
    }
    
    setSort(currentSort);
    setPage(0);
  };

  const updateFilter = (key: string, value: string | number | boolean | number[] | null | undefined) => {
    setFilters(prev => {
      const next = { ...prev };
      if (value === null || value === undefined || value === '' || (Array.isArray(value) && value.length === 0)) {
        delete next[key];
      } else {
        next[key] = value;
      }
      return next;
    });
    setPage(0);
  };

  const resetFilters = () => {
    setFilters({});
    setSearch('');
    setPage(0);
  };

  return {
    state: { page, size, sort, search, filters },
    data,
    isLoading,
    handlers: { setPage, setSize, setSearch, handleSort, updateFilter, resetFilters }
  };
};