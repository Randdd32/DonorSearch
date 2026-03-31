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

const createDictService = (basePath: string, isInfra = false) => {
  const prefix = isInfra ? '/infra/dictionaries' : '/dictionaries';
  return {
    fetchOptions: async (search?: string, parentIds?: number[]): Promise<SelectOption[]> => {
      const { data } = await apiClient.get<PageDto<NamedDictionaryDto>>(`${prefix}/${basePath}`, {
        params: { search, parentIds: parentIds?.join(',') || undefined, size: 50 }
      });
      return data.items.map(i => ({ value: i.id, label: i.name }));
    },
    fetchByIds: async (ids: number[]): Promise<SelectOption[]> => {
      if (!ids.length) return[];
      const { data } = await apiClient.get<NamedDictionaryDto[]>(`${prefix}/${basePath}/ids`, {
        params: { ids: ids.join(',') }
      });
      return data.map(i => ({ value: i.id, label: i.name }));
    }
  };
};

export const dictionaryService = {
  buildings: createDictService('buildings', true),
  floors: createDictService('floors', true),
  rooms: createDictService('rooms', true),
  infraManufacturers: createDictService('manufacturers', true),
  deviceTypes: createDictService('device-types', true),
  deviceModels: createDictService('device-models', true),
  departments: createDictService('departments', true),
  states: createDictService('states', true),

  hwManufacturers: createDictService('manufacturers', false),
  colors: createDictService('colors', false),
  cpuSockets: createDictService('cpu-sockets', false),
  microarchitectures: createDictService('microarchitectures', false),
  integratedGraphics: createDictService('integrated-graphics', false),
  ramFormFactors: createDictService('ram-form-factors', false),
  memoryTypes: createDictService('memory-types', false),
  motherboardFormFactors: createDictService('motherboard-form-factors', false),
  gpuChipsets: createDictService('gpu-chipsets', false),
  caseTypes: createDictService('case-types', false),
  sidePanels: createDictService('side-panels', false),
  frontPanelUsbs: createDictService('front-panel-usb-types', false),
  powerSupplyTypes: createDictService('power-supply-types', false),
  efficiencyRatings: createDictService('efficiency-ratings', false),
  modularTypes: createDictService('modular-types', false),
  storageTypes: createDictService('storage-types', false),
  storageFormFactors: createDictService('storage-form-factors', false),
  storageInterfaces: createDictService('storage-interfaces', false),
  fanConnectors: createDictService('fan-connectors', false),
  monitorResolutions: createDictService('monitor-resolutions', false),
  panelTypes: createDictService('panel-types', false),
  aspectRatios: createDictService('aspect-ratios', false),
  opticalDriveFormFactors: createDictService('optical-drive-form-factors', false),
  expansionInterfaces: createDictService('expansion-interfaces', false),
  audioChipsets: createDictService('audio-chipsets', false),
  wirelessProtocols: createDictService('wireless-protocols', false),
};

export type DictionaryName = keyof typeof dictionaryService;