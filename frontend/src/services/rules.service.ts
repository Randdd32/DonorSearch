import { apiClient } from '../config/api';
import type { PageDto } from '../types/pagination';
import type { CompatibilityRuleDto, RuleBuilderMetadataDto } from '../types/compatibility';
import type { CommonFilters } from '../hooks/useUrlFilters';

export interface GetRulesParams extends CommonFilters {
  isActive?: boolean;
  targetTypes?: string[];
  createdAfter?: string;
  createdBefore?: string;
  updatedAfter?: string;
  updatedBefore?: string;
}

export const rulesService = {
  async getRules(params: GetRulesParams): Promise<PageDto<CompatibilityRuleDto>> {
    const { data } = await apiClient.get<PageDto<CompatibilityRuleDto>>('/compatibility-rules', { 
      params: {
        ...params,
        targetTypes: params.targetTypes?.join(','),
        createdAfter: params.createdAfter ? new Date(params.createdAfter).toISOString() : undefined,
        createdBefore: params.createdBefore ? new Date(params.createdBefore).toISOString() : undefined,
        updatedAfter: params.updatedAfter ? new Date(params.updatedAfter).toISOString() : undefined,
        updatedBefore: params.updatedBefore ? new Date(params.updatedBefore).toISOString() : undefined,
      } 
    });
    return data;
  },

  async getById(id: number): Promise<CompatibilityRuleDto> {
    const { data } = await apiClient.get<CompatibilityRuleDto>(`/compatibility-rules/${id}`);
    return data;
  },

  async create(dto: Partial<CompatibilityRuleDto>): Promise<CompatibilityRuleDto> {
    const { data } = await apiClient.post<CompatibilityRuleDto>('/compatibility-rules', dto);
    return data;
  },

  async update(id: number, dto: Partial<CompatibilityRuleDto>): Promise<CompatibilityRuleDto> {
    const { data } = await apiClient.put<CompatibilityRuleDto>(`/compatibility-rules/${id}`, dto);
    return data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(`/compatibility-rules/${id}`);
  },

  async getBuilderMetadata(): Promise<RuleBuilderMetadataDto> {
    const { data } = await apiClient.get<RuleBuilderMetadataDto>('/compatibility-rules/builder-metadata');
    return data;
  },

  async validateExpression(expression: string): Promise<void> {
    await apiClient.post('/compatibility-rules/validate-expression', { expression });
  }
};