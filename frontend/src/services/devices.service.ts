import { apiClient } from '../config/api';
import type { PageDto } from '../types/pagination';
import type { ExternalDeviceDto } from '../types/integration';

export interface GetDevicesParams {
  page?: number;
  size?: number;
  search?: string;
  sort?: string;
  stateIds?: number[];
  departmentIds?: number[];
  manufacturerIds?: number[];
  typeIds?: number[];
  modelIds?: number[];
  buildingIds?: number[];
  floorIds?: number[];
  roomIds?: number[];
  isWorking?: boolean;
  dateReceivedFrom?: string;
  dateReceivedTo?: string;
}

export const devicesService = {
  async getDevices(params: GetDevicesParams): Promise<PageDto<ExternalDeviceDto>> {
    const queryParams: Record<string, string | number | boolean | undefined> = {
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
      dateReceivedFrom: params.dateReceivedFrom ? new Date(params.dateReceivedFrom).toISOString() : undefined,
      dateReceivedTo: params.dateReceivedTo ? new Date(params.dateReceivedTo).toISOString() : undefined,
    };

    const { data } = await apiClient.get<PageDto<ExternalDeviceDto>>('/infra/devices', {
      params: queryParams
    });
    return data;
  },

  async getDeviceDetails(id: number): Promise<ExternalDeviceDto> {
    const { data } = await apiClient.get<ExternalDeviceDto>(`/infra/devices/${id}`);
    return data;
  }
};