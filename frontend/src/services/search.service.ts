import { apiClient } from '../config/api';
import type { PageDto } from '../types/pagination';
import type { DonorResultDto } from '../types/search';

export interface GetSearchResultsParams {
  page?: number;
  size?: number;
  search?: string;
  // Добавить остальные фильтры!!!
}

export const searchService = {
  async runSearch(targetDeviceId: number, targetAdapterId: number): Promise<{ sessionId: string }> {
    const { data } = await apiClient.post<{ sessionId: string }>('/search/run', null, {
      params: { targetDeviceId, targetAdapterId }
    });
    return data;
  },

  async getSearchResults(sessionId: string, params: GetSearchResultsParams): Promise<PageDto<DonorResultDto>> {
    const { data } = await apiClient.get<PageDto<DonorResultDto>>(`/search/results/${sessionId}`, {
      params: {
        page: params.page || 0,
        size: params.size || 24,
        search: params.search || undefined,
      }
    });
    return data;
  }
};