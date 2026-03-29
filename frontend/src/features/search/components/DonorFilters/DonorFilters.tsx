import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { useDictionaryFetchers } from '../../../filters/hooks/useDictionaryFetchers';
import { MultiSelectFilter, ManufacturerModelFilters, LocationFilters, StaticSelectFilter, DateRangeFilters } from '../../../filters/components/FilterBlocks/FilterBlocks';
import { dictionaryService } from '../../../../services/dictionary.service';
import type { FilterValue } from '../../../../hooks/useUrlFilters';
import styles from '../../../filters/styles/filterForms.module.css';

interface DonorFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: Record<string, FilterValue>;
  updateFilters: (u: Record<string, FilterValue>) => void;
  resetFilters: () => void;
}

export const DonorFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: DonorFiltersProps) => {
  const fetchers = useDictionaryFetchers(filters);
  const isWorkingValue = filters.isWorking === undefined ? '' : filters.isWorking ? 'true' : 'false';

  return (
    <FilterSidebar title="Фильтры доноров" isOpen={isOpen} onClose={onClose} onReset={resetFilters}>
      <div className={styles.hierarchyGroup}>
        <h4 className={styles.hierarchyTitle}>Совместимость</h4>
        <div className={styles.filterGroup}>
          <label className={styles.label}>Максимальный штраф</label>
          <input 
            type="number" 
            className={styles.nativeInput} 
            placeholder="Например: 20" 
            value={(filters.maxTotalPenalty as number) || ''} 
            onChange={(e) => updateFilters({ maxTotalPenalty: e.target.value ? Number(e.target.value) : undefined })} 
          />
        </div>
        <MultiSelectFilter 
          label="Производитель искомой детали" 
          value={filters.componentManufacturerIds} 
          onChange={(v) => updateFilters({ componentManufacturerIds: v })} 
          fetchOptions={fetchers.fetchManufacturers} 
          fetchByIds={(ids) => dictionaryService.manufacturers.fetchByIds(ids)} 
        />
      </div>
      <MultiSelectFilter 
        label="Статус ПК-донора" 
        value={filters.stateIds} 
        onChange={(v) => updateFilters({ stateIds: v })} 
        fetchOptions={fetchers.fetchStates} 
        fetchByIds={(ids) => dictionaryService.states.fetchByIds(ids)} 
      />
      <StaticSelectFilter 
        label="Работоспособность ПК-донора" 
        value={isWorkingValue} 
        onChange={(val) => updateFilters({ isWorking: val === '' || val === null ? undefined : val === 'true' })} 
        options={[{ value: '', label: 'Все устройства' }, { value: 'true', label: 'Только исправные' }, { value: 'false', label: 'Только неисправные' }]} 
      />
      <MultiSelectFilter 
        label="Тип ПК-донора" 
        value={filters.typeIds} 
        onChange={(v) => updateFilters({ typeIds: v })} 
        fetchOptions={fetchers.fetchDeviceTypes} 
        fetchByIds={(ids) => dictionaryService.deviceTypes.fetchByIds(ids)} 
      />
      <MultiSelectFilter 
        label="Отдел" 
        value={filters.departmentIds} 
        onChange={(v) => updateFilters({ departmentIds: v })} 
        fetchOptions={fetchers.fetchDepartments} 
        fetchByIds={(ids) => dictionaryService.departments.fetchByIds(ids)} 
      />
      <ManufacturerModelFilters 
        title="Производитель и Модель ПК" 
        manufacturerKey="deviceManufacturerIds" 
        filters={filters} 
        updateFilters={updateFilters} 
        fetchers={fetchers} 
      />
      <LocationFilters 
        filters={filters} 
        updateFilters={updateFilters} 
        fetchers={fetchers} 
      />
      <DateRangeFilters 
        filters={filters} 
        updateFilters={updateFilters} 
      />
    </FilterSidebar>
  );
};