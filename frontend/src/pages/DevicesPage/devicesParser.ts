import { getNumberArray, getStringArray, getBoolean, getString, getNumber } from '../../hooks/useUrlFilters';

export const parseDeviceFilters = (params: URLSearchParams) => ({
  stateIds: getStringArray(params, 'stateIds'),          
  departmentIds: getStringArray(params, 'departmentIds'),
  typeIds: getStringArray(params, 'typeIds'),            
  manufacturerIds: getNumberArray(params, 'manufacturerIds'), 
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

export type DeviceFiltersType = ReturnType<typeof parseDeviceFilters>;