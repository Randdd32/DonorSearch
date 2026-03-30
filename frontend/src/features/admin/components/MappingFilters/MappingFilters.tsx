import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { DateRangeFilters, StaticSelectFilter } from '../../../filters/components/FilterBlocks/FilterBlocks';
import { Select } from '../../../../components/ui/Select/Select';
import type { CommonFilters } from '../../../../hooks/useUrlFilters';
import type { MappingFiltersType } from '../../../../pages/MappingsPage/mappingsParser';
import type { MappingConfidence, ExternalComponentCategory } from '../../../../types/integration';
import styles from './MappingFilters.module.css';

interface MappingFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: CommonFilters & MappingFiltersType;
  updateFilters: (updates: Partial<CommonFilters & MappingFiltersType>) => void;
  resetFilters: () => void;
}

const COMPONENT_TYPE_OPTIONS: { value: ExternalComponentCategory; label: string }[] = [
  { value: 'CPU', label: 'Процессор' },
  { value: 'MOTHERBOARD', label: 'Материнская плата' },
  { value: 'MEMORY', label: 'Оперативная память' },
  { value: 'VIDEO_CARD', label: 'Видеокарта' },
  { value: 'STORAGE', label: 'Накопитель' },
  { value: 'POWER_SUPPLY', label: 'Блок питания' },
  { value: 'CASE', label: 'Корпус' },
  { value: 'CASE_FAN', label: 'Вентилятор' },
  { value: 'OPTICAL_DRIVE', label: 'Привод' },
  { value: 'EXPANSION_CARD', label: 'Карта расширения' },
  { value: 'MONITOR', label: 'Монитор' }
];

export const MappingFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: MappingFiltersProps) => {
  return (
    <FilterSidebar title="Фильтры маппинга" isOpen={isOpen} onClose={onClose} onReset={resetFilters}>
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

      <div className={styles.section}>
        <label className={styles.label}>Тип компонента</label>
        <Select 
          value={filters.componentType || ''}
          onChange={(val) => updateFilters({ componentType: (val as ExternalComponentCategory) || undefined })}
          options={[{ value: '', label: 'Все типы' }, ...COMPONENT_TYPE_OPTIONS]}
          isSearchable={true}
          placeholder="Выберите тип..."
        />
      </div>

      <div className={styles.section}>
        <h4 className={styles.sectionTitle}>Даты (Создание / Изменение)</h4>
        <DateRangeFilters filters={filters} updateFilters={updateFilters} />
      </div>
    </FilterSidebar>
  );
};