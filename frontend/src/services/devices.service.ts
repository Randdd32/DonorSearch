import { apiClient } from '../config/api';
import type { PageDto } from '../types/pagination';
import type { ExternalDeviceDto } from '../types/integration';

export interface GetDevicesParams {
  page?: number;
  size?: number;
  search?: string;
  // Позже добавить фильтры!!!
}

export const devicesService = {
  async getDevices(params: GetDevicesParams): Promise<PageDto<ExternalDeviceDto>> {
    const { data } = await apiClient.get<PageDto<ExternalDeviceDto>>('/infra/devices', {
      params: {
        page: params.page || 0,
        size: params.size || 20,
        search: params.search || undefined,
      },
    });
    return data;
  },

  async getDeviceDetails(id: number): Promise<ExternalDeviceDto> {
    const { data } = await apiClient.get<ExternalDeviceDto>(`/infra/devices/${id}`);
    return data;
  }
};