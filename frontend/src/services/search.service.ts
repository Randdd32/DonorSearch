import { apiClient } from '../config/api';
import type { PageDto } from '../types/pagination';
import type { DonorResultDto } from '../types/search';
import type { ExternalComponentCategory } from '../types/integration';
import type { CommonFilters } from '../hooks/useUrlFilters';

export interface GetSearchResultsParams extends CommonFilters {
  stateIds?: number[];
  departmentIds?: number[];
  deviceManufacturerIds?: number[];
  typeIds?: number[];
  modelIds?: number[];
  buildingIds?: number[];
  floorIds?: number[];
  roomIds?: number[];
  isWorking?: boolean;
  dateReceivedFrom?: string;
  dateReceivedTo?: string;
  componentManufacturerIds?: number[];
  maxTotalPenalty?: number;
}

const mapFiltersToQueryParams = (params: GetSearchResultsParams) => ({
  search: params.search,
  stateIds: params.stateIds?.join(','),
  departmentIds: params.departmentIds?.join(','),
  deviceManufacturerIds: params.deviceManufacturerIds?.join(','),
  typeIds: params.typeIds?.join(','),
  modelIds: params.modelIds?.join(','),
  buildingIds: params.buildingIds?.join(','),
  floorIds: params.floorIds?.join(','),
  roomIds: params.roomIds?.join(','),
  componentManufacturerIds: params.componentManufacturerIds?.join(','),
  maxTotalPenalty: params.maxTotalPenalty,
  isWorking: params.isWorking,
  dateReceivedFrom: params.dateReceivedFrom ? new Date(params.dateReceivedFrom).toISOString() : undefined,
  dateReceivedTo: params.dateReceivedTo ? new Date(params.dateReceivedTo).toISOString() : undefined
});

export const searchService = {
  async runSearch(params: { targetDeviceId: number; targetAdapterId?: number; category?: ExternalComponentCategory })
  : Promise<{ sessionId: string }> {
    const { data } = await apiClient.post<{ sessionId: string }>('/search/run', null, { params });
    return data;
  },

  async getSearchResults(sessionId: string, params: GetSearchResultsParams): Promise<PageDto<DonorResultDto>> {
    const queryParams = {
      page: params.page || 0,
      size: params.size || 12,
      sort: params.sort,
      ...mapFiltersToQueryParams(params)
    };

    const { data } = await apiClient.get<PageDto<DonorResultDto>>(`/search/results/${sessionId}`, {
      params: queryParams
    });
    return data;
  },

  async exportPdf(sessionId: string, params: GetSearchResultsParams): Promise<Blob> {
    const queryParams = mapFiltersToQueryParams(params);

    const { data } = await apiClient.get<Blob>(`/search/results/${sessionId}/export/pdf`, {
      params: queryParams,
      responseType: 'blob'
    });
    return data;
  }
};