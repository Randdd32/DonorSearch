import { getNumberArray, getBoolean, getString, getNumber } from '../../hooks/useUrlFilters';

export const parseDonorFilters = (params: URLSearchParams) => ({
  maxTotalPenalty: params.get('maxTotalPenalty') ? Number(params.get('maxTotalPenalty')) : undefined,
  componentManufacturerIds: getNumberArray(params, 'componentManufacturerIds'),
  stateIds: getNumberArray(params, 'stateIds'),
  isWorking: getBoolean(params, 'isWorking'),
  typeIds: getNumberArray(params, 'typeIds'),
  departmentIds: getNumberArray(params, 'departmentIds'),
  deviceManufacturerIds: getNumberArray(params, 'deviceManufacturerIds'),
  modelIds: getNumberArray(params, 'modelIds'), 
  buildingIds: getNumberArray(params, 'buildingIds'),
  floorIds: getNumberArray(params, 'floorIds'),
  roomIds: getNumberArray(params, 'roomIds'),
  dateReceivedFrom: getString(params, 'dateReceivedFrom'),
  dateReceivedTo: getString(params, 'dateReceivedTo'),
  minCost: getNumber(params, 'minCost'),
  maxCost: getNumber(params, 'maxCost'),
  dateInquiryFrom: getString(params, 'dateInquiryFrom'),
  dateInquiryTo: getString(params, 'dateInquiryTo'),
  appointmentDateFrom: getString(params, 'appointmentDateFrom'),
  appointmentDateTo: getString(params, 'appointmentDateTo')
});

export type DonorFiltersType = ReturnType<typeof parseDonorFilters>;