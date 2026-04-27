import { getNumberArray, getStringArray, getBoolean, getString, getNumber } from '../../hooks/useUrlFilters';

export const parseDonorFilters = (params: URLSearchParams) => ({
  maxTotalPenalty: getNumber(params, 'maxTotalPenalty'),
  componentManufacturerIds: getNumberArray(params, 'componentManufacturerIds'),
  stateIds: getStringArray(params, 'stateIds'),
  typeIds: getStringArray(params, 'typeIds'),
  departmentIds: getStringArray(params, 'departmentIds'),
  deviceManufacturerIds: getNumberArray(params, 'deviceManufacturerIds'),
  modelIds: getNumberArray(params, 'modelIds'),
  buildingIds: getNumberArray(params, 'buildingIds'),
  floorIds: getNumberArray(params, 'floorIds'),
  roomIds: getNumberArray(params, 'roomIds'),
  isWorking: getBoolean(params, 'isWorking'),
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