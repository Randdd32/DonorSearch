import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { AuditDateFilters, StaticSelectFilter } from '../../../filters/components/FilterBlocks/FilterBlocks';
import type { CommonFilters } from '../../../../hooks/useUrlFilters';
import type { UserFiltersType } from '../../../../pages/UsersPage/usersParser';
import type { UserRole } from '../../../../types/auth';

interface UserFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: CommonFilters & UserFiltersType;
  updateFilters: (updates: Partial<CommonFilters & UserFiltersType>) => void;
  resetFilters: () => void;
}

export const UserFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: UserFiltersProps) => {
  return (
    <FilterSidebar title="Фильтры" isOpen={isOpen} onClose={onClose} onReset={resetFilters}>
      <StaticSelectFilter 
        label="Роль пользователя"
        value={filters.roles?.[0] || ''}
        onChange={(val) => updateFilters({ roles: val ? [val as UserRole] : [] })}
        options={[
          { value: '', label: 'Все роли' },
          { value: 'SUPERADMIN', label: 'Суперадминистраторы' },
          { value: 'ADMIN', label: 'Администраторы' },
          { value: 'USER', label: 'Обычные пользователи' }
        ]}
      />
      <AuditDateFilters filters={filters} updateFilters={updateFilters} />
    </FilterSidebar>
  );
};