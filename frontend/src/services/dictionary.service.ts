import { apiClient } from '../config/api';
import type { PageDto } from '../types/pagination';

interface NamedDictionaryDto {
  id: number;
  name: string;
}

export interface SelectOption {
  value: number;
  label: string;
}

const createDictService = (basePath: string) => ({
  fetchOptions: async (search?: string, parentIds?: number[]): Promise<SelectOption[]> => {
    const { data } = await apiClient.get<PageDto<NamedDictionaryDto>>(`/infra/dictionaries/${basePath}`, {
      params: { search, parentIds: parentIds?.join(',') || undefined, size: 50 }
    });
    return data.items.map(i => ({ value: i.id, label: i.name }));
  },
  
  fetchByIds: async (ids: number[]): Promise<SelectOption[]> => {
    if (!ids.length) return [];
    const { data } = await apiClient.get<NamedDictionaryDto[]>(`/infra/dictionaries/${basePath}/ids`, {
      params: { ids: ids.join(',') }
    });
    return data.map(i => ({ value: i.id, label: i.name }));
  }
});

export const dictionaryService = {
  buildings: createDictService('buildings'),
  floors: createDictService('floors'),
  rooms: createDictService('rooms'),
  manufacturers: createDictService('manufacturers'),
  deviceTypes: createDictService('device-types'),
  deviceModels: createDictService('device-models'),
  departments: createDictService('departments'),
  states: createDictService('states'),
};