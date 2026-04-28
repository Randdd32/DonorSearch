import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { useDictionaryFetchers } from '../../../filters/hooks/useDictionaryFetchers';
import { MultiSelectFilter, ManufacturerModelFilters, LocationFilters, 
  StaticSelectFilter, DateRangeFilters, NumberRangeFilter } from '../../../filters/components/FilterBlocks/FilterBlocks';
import { dictionaryService } from '../../../../services/dictionary.service';
import type { DonorFiltersType } from '../../../../pages/SearchResultsPage/donorParser';
import type { CommonFilters } from '../../../../hooks/useUrlFilters';
import styles from '../../../filters/styles/filterForms.module.css';

interface DonorFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: CommonFilters & DonorFiltersType;
  updateFilters: (updates: Partial<CommonFilters & DonorFiltersType>) => void;
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
          value={filters.componentManufacturerIds as number[]} 
          onChange={(v) => updateFilters({ componentManufacturerIds: v as number[] })} 
          fetchOptions={fetchers.fetchManufacturers} 
          fetchByIds={(ids) => dictionaryService.infraManufacturers.fetchByIds(ids as number[])} 
        />
      </div>
      <MultiSelectFilter 
        label="Статус ПК-донора" 
        value={filters.stateIds as string[]} 
        onChange={(v) => updateFilters({ stateIds: v as string[] })} 
        fetchOptions={fetchers.fetchStates} 
        fetchByIds={(ids) => dictionaryService.states.fetchByIds(ids as string[])} 
      />
      <StaticSelectFilter 
        label="Работоспособность ПК-донора" 
        value={isWorkingValue} 
        onChange={(val) => updateFilters({ isWorking: val === '' || val === null ? undefined : val === 'true' })} 
        options={[{ value: '', label: 'Все устройства' }, { value: 'true', label: 'Только исправные' }, { value: 'false', label: 'Только неисправные' }]} 
      />
      <MultiSelectFilter 
        label="Тип ПК-донора" 
        value={filters.typeIds as string[]} 
        onChange={(v) => updateFilters({ typeIds: v as string[] })} 
        fetchOptions={fetchers.fetchDeviceTypes} 
        fetchByIds={(ids) => dictionaryService.deviceTypes.fetchByIds(ids as string[])} 
      />
      <MultiSelectFilter 
        label="Отдел" 
        value={filters.departmentIds as string[]} 
        onChange={(v) => updateFilters({ departmentIds: v as string[] })} 
        fetchOptions={fetchers.fetchDepartments} 
        fetchByIds={(ids) => dictionaryService.departments.fetchByIds(ids as string[])} 
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
      <NumberRangeFilter 
        label="Стоимость (₽)" 
        minKey="minCost" maxKey="maxCost" 
        filters={filters} updateFilters={updateFilters} 
      />
      <DateRangeFilters 
        label="Дата поступления" 
        fromKey="dateReceivedFrom" toKey="dateReceivedTo" 
        filters={filters} updateFilters={updateFilters} 
      />
      <DateRangeFilters 
        label="Дата последнего опроса" 
        fromKey="dateInquiryFrom" toKey="dateInquiryTo" 
        filters={filters} updateFilters={updateFilters} 
      />
      <DateRangeFilters 
        label="Дата назначения" 
        fromKey="appointmentDateFrom" toKey="appointmentDateTo" 
        filters={filters} updateFilters={updateFilters} 
      />
    </FilterSidebar>
  );
};