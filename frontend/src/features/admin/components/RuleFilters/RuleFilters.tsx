import ReactSelect from 'react-select';
import type { MultiValue } from 'react-select';
import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { AuditDateFilters, StaticSelectFilter } from '../../../filters/components/FilterBlocks/FilterBlocks';
import { COMPONENT_TYPE_OPTIONS } from '../../../../config/componentTypes';
import { getSelectStyles } from '../../../../utils/selectStyles';
import type { CommonFilters } from '../../../../hooks/useUrlFilters';
import type { RuleFiltersType } from '../../../../pages/RulesPage/rulesParser';
import formStyles from '../../../filters/styles/filterForms.module.css';

interface RuleFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: CommonFilters & RuleFiltersType;
  updateFilters: (updates: Partial<CommonFilters & RuleFiltersType>) => void;
  resetFilters: () => void;
}

export const RuleFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: RuleFiltersProps) => {
  const isActiveValue = filters.isActive === undefined ? '' : filters.isActive ? 'true' : 'false';

  return (
    <FilterSidebar title="Фильтры" isOpen={isOpen} onClose={onClose} onReset={resetFilters}>
      <StaticSelectFilter 
        label="Статус активности"
        value={isActiveValue}
        onChange={(val: string | number | boolean | null) => updateFilters({ isActive: val === '' || val === null ? undefined : val === 'true' })}
        options={[
          { value: '', label: 'Все правила' },
          { value: 'true', label: 'Только активные' },
          { value: 'false', label: 'Отключенные' }
        ]}
      />
      <div className={formStyles.filterGroup}>
        <label className={formStyles.label}>Применяется к типам оборудования</label>
        <ReactSelect
          isMulti
          options={COMPONENT_TYPE_OPTIONS}
          value={COMPONENT_TYPE_OPTIONS.filter(opt => (filters.targetTypes ||[]).includes(opt.value))}
          onChange={(selected: MultiValue<{ value: string; label: string }>) => {
              const values = selected ? selected.map(s => s.value) : [];
              updateFilters({ targetTypes: values });
          }}
          styles={getSelectStyles()}
          placeholder="Выберите типы..."
          noOptionsMessage={() => 'Типы не найдены'}
        />
      </div>
      <AuditDateFilters filters={filters} updateFilters={updateFilters} />
    </FilterSidebar>
  );
};