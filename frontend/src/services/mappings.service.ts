import { apiClient } from '../config/api';
import { API_ENDPOINTS } from '../config/constants';
import type { PageDto } from '../types/pagination';
import type { IntegrationMappingDto, CreateMappingDto, MappingConfidence, ExternalComponentCategory } from '../types/integration';
import type { CommonFilters } from '../hooks/useUrlFilters';

export interface GetMappingsParams extends CommonFilters {
  confidence?: MappingConfidence;
  componentType?: ExternalComponentCategory;
  createdAfter?: string;
  createdBefore?: string;
  updatedAfter?: string;
  updatedBefore?: string;
}

export const mappingsService = {
  async getMappings(params: GetMappingsParams): Promise<PageDto<IntegrationMappingDto>> {
    const queryParams: Record<string, string | string[] | number | boolean | undefined> = {
      page: params.page || 0,
      size: params.size || 10,
      search: params.search,
      sort: params.sort,
      confidence: params.confidence,
      componentType: params.componentType,
      createdAfter: params.createdAfter ? new Date(params.createdAfter).toISOString() : undefined,
      createdBefore: params.createdBefore ? new Date(params.createdBefore).toISOString() : undefined,
      updatedAfter: params.updatedAfter ? new Date(params.updatedAfter).toISOString() : undefined,
      updatedBefore: params.updatedBefore ? new Date(params.updatedBefore).toISOString() : undefined,
    };

    const { data } = await apiClient.get<PageDto<IntegrationMappingDto>>(API_ENDPOINTS.MAPPINGS.BASE, { params: queryParams });
    return data;
  },

  async getById(id: number): Promise<IntegrationMappingDto> {
    const { data } = await apiClient.get<IntegrationMappingDto>(API_ENDPOINTS.MAPPINGS.DETAILS(id));
    return data;
  },

  async create(dto: CreateMappingDto): Promise<IntegrationMappingDto> {
    const { data } = await apiClient.post<IntegrationMappingDto>(API_ENDPOINTS.MAPPINGS.BASE, dto);
    return data;
  },

  async update(id: number, dto: IntegrationMappingDto): Promise<IntegrationMappingDto> {
    const { data } = await apiClient.put<IntegrationMappingDto>(API_ENDPOINTS.MAPPINGS.DETAILS(id), dto);
    return data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(API_ENDPOINTS.MAPPINGS.DETAILS(id));
  }
};