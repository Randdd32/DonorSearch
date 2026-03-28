import { useSearchParams } from 'react-router-dom';

const getNumberArray = (searchParams: URLSearchParams, key: string): number[] => {
  const val = searchParams.get(key);
  return val ? val.split(',').map(Number).filter(n => !isNaN(n)) :[];
};

export type FilterValue = string | number | boolean | (string | number)[] | null | undefined;

export const useUrlFilters = () => {
  const[searchParams, setSearchParams] = useSearchParams();

  const filters = {
    page: Number(searchParams.get('page')) || 0,
    size: Number(searchParams.get('size')) || 12,
    search: searchParams.get('search') || '',
    sort: searchParams.get('sort') || 'id,desc',
    
    stateIds: getNumberArray(searchParams, 'stateIds'),
    departmentIds: getNumberArray(searchParams, 'departmentIds'),
    manufacturerIds: getNumberArray(searchParams, 'manufacturerIds'),
    typeIds: getNumberArray(searchParams, 'typeIds'),
    modelIds: getNumberArray(searchParams, 'modelIds'),
    buildingIds: getNumberArray(searchParams, 'buildingIds'),
    floorIds: getNumberArray(searchParams, 'floorIds'),
    roomIds: getNumberArray(searchParams, 'roomIds'),
    componentManufacturerIds: getNumberArray(searchParams, 'componentManufacturerIds'),
    maxTotalPenalty: searchParams.get('maxTotalPenalty') ? Number(searchParams.get('maxTotalPenalty')) : undefined,
    
    isWorking: searchParams.get('isWorking') === 'true' ? true : searchParams.get('isWorking') === 'false' ? false : undefined,
    
    dateReceivedFrom: searchParams.get('dateReceivedFrom') || '',
    dateReceivedTo: searchParams.get('dateReceivedTo') || '',
  };

  const updateFilters = (updates: Record<string, FilterValue>, resetPage = true) => {
    setSearchParams((prev) => {
      const next = new URLSearchParams(prev);
      
      Object.entries(updates).forEach(([key, value]) => {
        if (value === null || value === '' || value === undefined || (Array.isArray(value) && value.length === 0)) {
          next.delete(key);
        } else {
          next.set(key, Array.isArray(value) ? value.join(',') : String(value));
        }
      });
      
      if (resetPage && updates.page === undefined) {
        next.set('page', '0');
      }
      
      return next;
    });
  };

  const resetFilters = () => {
    setSearchParams(new URLSearchParams());
  };

  return { filters, updateFilters, resetFilters };
};