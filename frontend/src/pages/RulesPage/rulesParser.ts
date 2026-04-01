import { getBoolean, getString, getStringArray } from '../../hooks/useUrlFilters';

export const parseRuleFilters = (params: URLSearchParams) => ({
  isActive: getBoolean(params, 'isActive'),
  targetTypes: getStringArray(params, 'targetTypes'),
  createdAfter: getString(params, 'createdAfter'),
  createdBefore: getString(params, 'createdBefore'),
  updatedAfter: getString(params, 'updatedAfter'),
  updatedBefore: getString(params, 'updatedBefore'),
});

export type RuleFiltersType = ReturnType<typeof parseRuleFilters>;