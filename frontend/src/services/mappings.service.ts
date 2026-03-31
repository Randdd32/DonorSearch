import { apiClient } from '../config/api';
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

    const { data } = await apiClient.get<PageDto<IntegrationMappingDto>>('/mappings', { params: queryParams });
    return data;
  },

  async getById(id: number): Promise<IntegrationMappingDto> {
    const { data } = await apiClient.get<IntegrationMappingDto>(`/mappings/${id}`);
    return data;
  },

  async create(dto: CreateMappingDto): Promise<IntegrationMappingDto> {
    const { data } = await apiClient.post<IntegrationMappingDto>('/mappings', dto);
    return data;
  },

  async update(id: number, dto: IntegrationMappingDto): Promise<IntegrationMappingDto> {
    const { data } = await apiClient.put<IntegrationMappingDto>(`/mappings/${id}`, dto);
    return data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`/mappings/${id}`);
  }
};