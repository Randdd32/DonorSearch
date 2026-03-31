import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { AuditDateFilters, StaticSelectFilter } from '../../../filters/components/FilterBlocks/FilterBlocks';
import { Select } from '../../../../components/ui/Select/Select';
import type { CommonFilters } from '../../../../hooks/useUrlFilters';
import type { MappingFiltersType } from '../../../../pages/MappingsPage/mappingsParser';
import type { MappingConfidence, ExternalComponentCategory } from '../../../../types/integration';
import formStyles from '../../../filters/styles/filterForms.module.css';

interface MappingFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: CommonFilters & MappingFiltersType;
  updateFilters: (updates: Partial<CommonFilters & MappingFiltersType>) => void;
  resetFilters: () => void;
}

const COMPONENT_TYPE_OPTIONS =[
  { value: 'CPU', label: 'Процессор' },
  { value: 'CPU_COOLER', label: 'Кулер процессора' },
  { value: 'MOTHERBOARD', label: 'Материнская плата' },
  { value: 'MEMORY', label: 'Оперативная память' },
  { value: 'VIDEO_CARD', label: 'Видеокарта' },
  { value: 'STORAGE', label: 'Накопитель' },
  { value: 'POWER_SUPPLY', label: 'Блок питания' },
  { value: 'CASE', label: 'Корпус' },
  { value: 'CASE_FAN', label: 'Вентилятор' },
  { value: 'OPTICAL_DRIVE', label: 'Оптический привод' },
  { value: 'EXPANSION_CARD', label: 'Карта расширения' },
  { value: 'MONITOR', label: 'Монитор' },
];

export const MappingFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: MappingFiltersProps) => {
  return (
    <FilterSidebar title="Фильтры" isOpen={isOpen} onClose={onClose} onReset={resetFilters}>
      <StaticSelectFilter 
        label="Уверенность (Confidence)"
        value={filters.confidence || ''}
        onChange={(val) => updateFilters({ confidence: (val as MappingConfidence) || undefined })}
        options={[
          { value: '', label: 'Все' },
          { value: 'CONFIRMED', label: 'Подтверждено' },
          { value: 'AUTO', label: 'Авто (Высокая)' },
          { value: 'NEEDS_REVIEW', label: 'Требует проверки' },
          { value: 'BAD_MATCH', label: 'Низкая уверенность' },
        ]}
      />
      <div className={formStyles.filterGroup}>
        <label className={formStyles.label}>Тип компонента</label>
        <Select 
          value={filters.componentType || ''}
          onChange={(val) => updateFilters({ componentType: (val as ExternalComponentCategory) || undefined })}
          options={[{ value: '', label: 'Все типы' }, ...COMPONENT_TYPE_OPTIONS]}
          isSearchable={true}
          placeholder="Выберите тип..."
        />
      </div>
       <AuditDateFilters filters={filters} updateFilters={updateFilters} />
    </FilterSidebar>
  );
};