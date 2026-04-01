import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../../../config/api';
import { useDebounce } from '../../../hooks/useDebounce';
import { getComponentEndpoint } from '../../../utils/componentEndpoints';
import type { PageDto } from '../../../types/pagination';
import { toggleSort } from '../../../utils/tableUtils';

export const useComponentSelection = (componentType: string, isOpen: boolean, selectedId: number | null) => {
  const[page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sort, setSort] = useState<string[]>(['id,desc']);
  const[search, setSearch] = useState('');
  const [filters, setFilters] = useState<Record<string, string | number | boolean | number[]>>({});

  const debouncedSearch = useDebounce(search, 500);
  const endpoint = getComponentEndpoint(componentType);

  const queryParams: Record<string, string | number | boolean | string[] | undefined> = {
    page,
    size,
    search: debouncedSearch || undefined,
    sort: sort.length > 0 ? sort : undefined,
  };

  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      queryParams[key] = value as string | number | boolean | string[];
    }
  });

  const { data, isLoading } = useQuery({
    queryKey:['components-modal', endpoint, queryParams],
    queryFn: async () => {
      if (!endpoint) return null;
      const response = await apiClient.get<PageDto<Record<string, unknown>>>(endpoint, { params: queryParams });
      return response.data;
    },
    enabled: isOpen && !!endpoint,
  });

  const { data: selectedItem } = useQuery({
    queryKey:['components-modal-selected', endpoint, selectedId],
    queryFn: async () => {
      if (!endpoint || !selectedId) return null;
      const response = await apiClient.get<PageDto<Record<string, unknown>>>(endpoint, {
        params: { search: String(selectedId), size: 1 }
      });
      return response.data.items.find(i => i.id === selectedId) || null;
    },
    enabled: isOpen && !!endpoint && !!selectedId
  });

  const handleSort = (field: string, isShiftPressed: boolean) => {
    const newSort = toggleSort(sort as string[], field, isShiftPressed);
    setSort(newSort);
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
    selectedItem,
    isLoading,
    handlers: { setPage, setSize, setSearch, handleSort, updateFilter, resetFilters }
  };
};