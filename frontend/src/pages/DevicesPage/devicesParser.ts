import { getNumberArray, getBoolean, getString } from '../../hooks/useUrlFilters';

export const parseDeviceFilters = (params: URLSearchParams) => ({
  stateIds: getNumberArray(params, 'stateIds'),
  departmentIds: getNumberArray(params, 'departmentIds'),
  manufacturerIds: getNumberArray(params, 'manufacturerIds'),
  typeIds: getNumberArray(params, 'typeIds'),
  modelIds: getNumberArray(params, 'modelIds'),
  buildingIds: getNumberArray(params, 'buildingIds'),
  floorIds: getNumberArray(params, 'floorIds'),
  roomIds: getNumberArray(params, 'roomIds'),
  isWorking: getBoolean(params, 'isWorking'),
  dateReceivedFrom: getString(params, 'dateReceivedFrom'),
  dateReceivedTo: getString(params, 'dateReceivedTo'),
});

export type DeviceFiltersType = ReturnType<typeof parseDeviceFilters>;