import { apiClient } from '../config/api';
import type { PageDto } from '../types/pagination';
import type { IntegrationMappingDto, CreateMappingDto, UpdateMappingDto, MappingConfidence, ExternalComponentCategory } from '../types/integration';
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
    const queryParams = new URLSearchParams();
    
    queryParams.append('page', String(params.page));
    queryParams.append('size', String(params.size));
    if (params.search) queryParams.append('search', params.search);
    if (params.confidence) queryParams.append('confidence', params.confidence);
    if (params.componentType) queryParams.append('componentType', params.componentType);
    if (params.createdAfter) queryParams.append('createdAfter', params.createdAfter);
    if (params.createdBefore) queryParams.append('createdBefore', params.createdBefore);
    if (params.updatedAfter) queryParams.append('updatedAfter', params.updatedAfter);
    if (params.updatedBefore) queryParams.append('updatedBefore', params.updatedBefore);
    
    params.sort.forEach(s => queryParams.append('sort', s));

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

  async update(id: number, dto: UpdateMappingDto): Promise<IntegrationMappingDto> {
    const { data } = await apiClient.put<IntegrationMappingDto>(`/mappings/${id}`, dto);
    return data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`/mappings/${id}`);
  }
};