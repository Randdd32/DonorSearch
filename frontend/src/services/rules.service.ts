import { apiClient } from '../config/api';
import { API_ENDPOINTS } from '../config/constants';
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
    const { data } = await apiClient.get<PageDto<CompatibilityRuleDto>>(API_ENDPOINTS.COMPATIBILITY_RULES.BASE, { 
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
    const { data } = await apiClient.get<CompatibilityRuleDto>(API_ENDPOINTS.COMPATIBILITY_RULES.DETAILS(id));
    return data;
  },

  async create(dto: Partial<CompatibilityRuleDto>): Promise<CompatibilityRuleDto> {
    const { data } = await apiClient.post<CompatibilityRuleDto>(API_ENDPOINTS.COMPATIBILITY_RULES.BASE, dto);
    return data;
  },

  async update(id: number, dto: Partial<CompatibilityRuleDto>): Promise<CompatibilityRuleDto> {
    const { data } = await apiClient.put<CompatibilityRuleDto>(API_ENDPOINTS.COMPATIBILITY_RULES.DETAILS(id), dto);
    return data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(API_ENDPOINTS.COMPATIBILITY_RULES.DETAILS(id));
  },

  async getBuilderMetadata(): Promise<RuleBuilderMetadataDto> {
    const { data } = await apiClient.get<RuleBuilderMetadataDto>(API_ENDPOINTS.COMPATIBILITY_RULES.BUILDER_METADATA);
    return data;
  },

  async validateExpression(expression: string): Promise<void> {
    await apiClient.post(API_ENDPOINTS.COMPATIBILITY_RULES.VALIDATE_EXPRESSION, { expression });
  }
};