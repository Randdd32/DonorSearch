import { getString, getStringArray } from '../../hooks/useUrlFilters';
import type { UserRole } from '../../types/auth';

export const parseUserFilters = (params: URLSearchParams) => ({
  roles: getStringArray(params, 'roles') as UserRole[],
  createdAfter: getString(params, 'createdAfter'),
  createdBefore: getString(params, 'createdBefore'),
  updatedAfter: getString(params, 'updatedAfter'),
  updatedBefore: getString(params, 'updatedBefore')
});

export type UserFiltersType = ReturnType<typeof parseUserFilters>;