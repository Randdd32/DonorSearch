import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { useDictionaryFetchers } from '../../../filters/hooks/useDictionaryFetchers';
import { MultiSelectFilter, ManufacturerModelFilters, LocationFilters, StaticSelectFilter, DateRangeFilters } from '../../../filters/components/FilterBlocks/FilterBlocks';
import { dictionaryService } from '../../../../services/dictionary.service';
import type { FilterValue } from '../../../../hooks/useUrlFilters';

interface DeviceFiltersProps {
  isOpen: boolean; 
  onClose: () => void; 
  filters: Record<string, FilterValue>;
  updateFilters: (u: Record<string, FilterValue>) => void; 
  resetFilters: () => void;
}

export const DeviceFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: DeviceFiltersProps) => {
  const fetchers = useDictionaryFetchers(filters);
  const isWorkingValue = filters.isWorking === undefined ? '' : filters.isWorking ? 'true' : 'false';

  return (
    <FilterSidebar title="Фильтры" isOpen={isOpen} onClose={onClose} onReset={resetFilters}>
      <MultiSelectFilter 
        label="Статус (Жизненный цикл)" 
        value={filters.stateIds} 
        onChange={(v) => updateFilters({ stateIds: v })} 
        fetchOptions={fetchers.fetchStates} 
        fetchByIds={(ids) => dictionaryService.states.fetchByIds(ids)} 
      />
      <StaticSelectFilter 
        label="Работоспособность" 
        value={isWorkingValue} 
        onChange={(val: string) => updateFilters({ isWorking: val === '' ? undefined : val === 'true' })} 
        options={[{ value: '', label: 'Все устройства' }, { value: 'true', label: 'Только исправные' }, { value: 'false', label: 'Только неисправные' }]} 
      />
      <MultiSelectFilter 
        label="Тип оборудования" 
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
        manufacturerKey="manufacturerIds" 
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