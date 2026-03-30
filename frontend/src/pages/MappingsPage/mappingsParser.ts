import { getString } from '../../hooks/useUrlFilters';
import type { MappingConfidence, ExternalComponentCategory } from '../../types/integration';

export const parseMappingFilters = (params: URLSearchParams) => ({
  confidence: params.get('confidence') as MappingConfidence || undefined,
  componentType: params.get('componentType') as ExternalComponentCategory || undefined,
  createdAfter: getString(params, 'createdAfter'),
  createdBefore: getString(params, 'createdBefore'),
  updatedAfter: getString(params, 'updatedAfter'),
  updatedBefore: getString(params, 'updatedBefore'),
});

export type MappingFiltersType = ReturnType<typeof parseMappingFilters>;