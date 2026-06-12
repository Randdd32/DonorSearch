import { dictionaryService } from '../../../services/dictionary.service';
import type { FilterValue } from '../../../hooks/useUrlFilters';

export const useDictionaryFetchers = (filters: Record<string, FilterValue>) => {
  return {
    fetchStates: (s?: string) => dictionaryService.states.fetchOptions(s),
    fetchDeviceTypes: (s?: string) => dictionaryService.deviceTypes.fetchOptions(s),
    fetchDepartments: (s?: string) => dictionaryService.departments.fetchOptions(s),
    fetchManufacturers: (s?: string) => dictionaryService.infraManufacturers.fetchOptions(s),
    fetchComponentManufacturers: (s?: string) => dictionaryService.infraManufacturers.fetchOptions(s),
    fetchModels: (s?: string) => {
      const parentId = (filters.manufacturerIds || filters.deviceManufacturerIds) as number[];
      return dictionaryService.deviceModels.fetchOptions(s, parentId);
    },
    fetchBuildings: (s?: string) => dictionaryService.buildings.fetchOptions(s),
    fetchFloors: (s?: string) => dictionaryService.floors.fetchOptions(s, filters.buildingIds as number[]),
    fetchRooms: (s?: string) => dictionaryService.rooms.fetchOptions(s, filters.floorIds as number[]),
  };
};

export type DictionaryFetchers = ReturnType<typeof useDictionaryFetchers>;