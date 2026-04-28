
import type { CommonFilters } from '../../../../hooks/useUrlFilters';
import type { DeviceFiltersType } from '../../../../pages/DevicesPage/devicesParser';
import { FilterSidebar } from '../../../../components/ui/FilterSidebar/FilterSidebar';
import { useDictionaryFetchers } from '../../../filters/hooks/useDictionaryFetchers';
import { MultiSelectFilter, ManufacturerModelFilters, LocationFilters, 
  StaticSelectFilter, DateRangeFilters, NumberRangeFilter } from '../../../filters/components/FilterBlocks/FilterBlocks';
import { dictionaryService } from '../../../../services/dictionary.service';

interface DeviceFiltersProps {
  isOpen: boolean; 
  onClose: () => void; 
  filters: CommonFilters & DeviceFiltersType;
  updateFilters: (updates: Partial<CommonFilters & DeviceFiltersType>) => void; 
  resetFilters: () => void;
}

export const DeviceFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: DeviceFiltersProps) => {
  const fetchers = useDictionaryFetchers(filters);
  const isWorkingValue = filters.isWorking === undefined ? '' : filters.isWorking ? 'true' : 'false';

  return (
    <FilterSidebar title="Фильтры" isOpen={isOpen} onClose={onClose} onReset={resetFilters}>
      <MultiSelectFilter 
        label="Статус (Жизненный цикл)" 
        value={filters.stateIds as string[]} 
        onChange={(v) => updateFilters({ stateIds: v as string[] })} 
        fetchOptions={fetchers.fetchStates} 
        fetchByIds={(ids) => dictionaryService.states.fetchByIds(ids as string[])} 
      />
      <StaticSelectFilter 
        label="Работоспособность" 
        value={isWorkingValue} 
        onChange={(val) => updateFilters({ isWorking: val === '' || val === null ? undefined : val === 'true' })}  
        options={[{ value: '', label: 'Все устройства' }, { value: 'true', label: 'Только исправные' }, { value: 'false', label: 'Только неисправные' }]} 
      />
      <MultiSelectFilter 
        label="Тип оборудования" 
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