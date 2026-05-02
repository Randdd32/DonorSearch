import { apiClient } from '../config/api';
import { API_ENDPOINTS } from '../config/constants';
import type { PageDto } from '../types/pagination';
import type { ExternalDeviceDto } from '../types/integration';
import type { CommonFilters } from '../hooks/useUrlFilters';

export interface GetDevicesParams extends CommonFilters {
  stateIds?: string[];      
  departmentIds?: string[]; 
  typeIds?: string[];      
  manufacturerIds?: number[];
  modelIds?: number[];
  buildingIds?: number[];
  floorIds?: number[];
  roomIds?: number[];
  isWorking?: boolean;
  dateReceivedFrom?: string;
  dateReceivedTo?: string;
  dateInquiryFrom?: string;
  dateInquiryTo?: string;
  appointmentDateFrom?: string;
  appointmentDateTo?: string;
  minCost?: number;
  maxCost?: number;
}

export const devicesService = {
  async getDevices(params: GetDevicesParams): Promise<PageDto<ExternalDeviceDto>> {
    const queryParams: Record<string, string | string[] | number | boolean | undefined> = {
      page: params.page || 0,
      size: params.size || 24,
      search: params.search,
      sort: params.sort,
      stateIds: params.stateIds?.join(','),
      departmentIds: params.departmentIds?.join(','),
      manufacturerIds: params.manufacturerIds?.join(','),
      typeIds: params.typeIds?.join(','),
      modelIds: params.modelIds?.join(','),
      buildingIds: params.buildingIds?.join(','),
      floorIds: params.floorIds?.join(','),
      roomIds: params.roomIds?.join(','),
      isWorking: params.isWorking,
      minCost: params.minCost,
      maxCost: params.maxCost,
      dateReceivedFrom: params.dateReceivedFrom ? new Date(params.dateReceivedFrom).toISOString() : undefined,
      dateReceivedTo: params.dateReceivedTo ? new Date(params.dateReceivedTo).toISOString() : undefined,
      dateInquiryFrom: params.dateInquiryFrom ? new Date(params.dateInquiryFrom).toISOString() : undefined,
      dateInquiryTo: params.dateInquiryTo ? new Date(params.dateInquiryTo).toISOString() : undefined,
      appointmentDateFrom: params.appointmentDateFrom ? new Date(params.appointmentDateFrom).toISOString() : undefined,
      appointmentDateTo: params.appointmentDateTo ? new Date(params.appointmentDateTo).toISOString() : undefined,
    };

    const { data } = await apiClient.get<PageDto<ExternalDeviceDto>>(API_ENDPOINTS.INFRA.DEVICES, {
      params: queryParams
    });
    return data;
  },

  async getDeviceDetails(id: number): Promise<ExternalDeviceDto> {
    const { data } = await apiClient.get<ExternalDeviceDto>(API_ENDPOINTS.INFRA.DEVICE_DETAILS(id));
    return data;
  }
};