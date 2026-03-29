import { SearchableSelect } from '../../../../components/ui/SearchableSelect/SearchableSelect';
import { Select } from '../../../../components/ui/Select/Select';
import { dictionaryService, type SelectOption } from '../../../../services/dictionary.service';
import type { FilterValue } from '../../../../hooks/useUrlFilters';
import type { useDictionaryFetchers } from '../../hooks/useDictionaryFetchers';
import styles from '../../styles/filterForms.module.css';

type DictionaryFetchers = ReturnType<typeof useDictionaryFetchers>;
type UpdateFiltersFn = (updates: Record<string, FilterValue>) => void;

interface MultiSelectProps {
  label: string;
  value: FilterValue;
  onChange: (val: number[] | null) => void;
  fetchOptions: (s?: string) => Promise<SelectOption[]>;
  fetchByIds: (ids: number[]) => Promise<SelectOption[]>;
  placeholder?: string;
}

export const MultiSelectFilter = ({ label, value, onChange, fetchOptions, fetchByIds, placeholder }: MultiSelectProps) => (
  <div className={styles.filterGroup}>
    <label className={styles.label}>{label}</label>
    <SearchableSelect
      isMulti
      value={value as number[]}
      onChange={(val) => onChange(val as number[])}
      fetchOptions={fetchOptions}
      fetchByIds={fetchByIds}
      placeholder={placeholder}
    />
  </div>
);

interface ManufacturerModelProps {
  manufacturerKey: string;
  filters: Record<string, FilterValue>;
  updateFilters: UpdateFiltersFn;
  fetchers: DictionaryFetchers;
  title?: string;
}

export const ManufacturerModelFilters = ({
  manufacturerKey, filters, updateFilters, fetchers, title = 'Производитель и модель'
}: ManufacturerModelProps) => {
  const manufacturers = filters[manufacturerKey] as number[];
  const hasManufacturers = manufacturers?.length > 0;
  const modelKey = `model-${manufacturers?.join('-')}`;

  return (
    <div className={styles.hierarchyGroup}>
      <h4 className={styles.hierarchyTitle}>{title}</h4>
      <MultiSelectFilter
        label="Производитель"
        value={manufacturers}
        onChange={(val) => updateFilters({ [manufacturerKey]: val, modelIds:[] })}
        fetchOptions={fetchers.fetchManufacturers}
        fetchByIds={(ids) => dictionaryService.manufacturers.fetchByIds(ids)}
      />
      <div className={styles.filterGroup}>
        <label className={styles.label}>Модель</label>
        <SearchableSelect
          key={modelKey}
          isMulti
          isDisabled={!hasManufacturers}
          value={filters.modelIds as number[]}
          onChange={(val) => updateFilters({ modelIds: val })}
          fetchOptions={fetchers.fetchModels}
          fetchByIds={(ids) => dictionaryService.deviceModels.fetchByIds(ids)}
          placeholder={hasManufacturers ? "Выберите модель..." : "Сначала выберите производителя"}
        />
      </div>
    </div>
  );
};

interface LocationFiltersProps {
  filters: Record<string, FilterValue>;
  updateFilters: UpdateFiltersFn;
  fetchers: DictionaryFetchers;
}

export const LocationFilters = ({ filters, updateFilters, fetchers }: LocationFiltersProps) => {
  const hasBuildings = (filters.buildingIds as number[])?.length > 0;
  const hasFloors = (filters.floorIds as number[])?.length > 0;

  return (
    <div className={styles.hierarchyGroup}>
      <h4 className={styles.hierarchyTitle}>Расположение</h4>
      <MultiSelectFilter
        label="Здание"
        value={filters.buildingIds}
        onChange={(val) => updateFilters({ buildingIds: val, floorIds:[], roomIds:[] })}
        fetchOptions={fetchers.fetchBuildings}
        fetchByIds={(ids) => dictionaryService.buildings.fetchByIds(ids)}
      />
      <div className={styles.filterGroup}>
        <label className={styles.label}>Этаж</label>
        <SearchableSelect
          key={`floor-${(filters.buildingIds as number[])?.join('-')}`}
          isMulti
          isDisabled={!hasBuildings}
          value={filters.floorIds as number[]}
          onChange={(val) => updateFilters({ floorIds: val, roomIds:[] })}
          fetchOptions={fetchers.fetchFloors}
          fetchByIds={(ids) => dictionaryService.floors.fetchByIds(ids)}
          placeholder={hasBuildings ? "Выберите этаж..." : "Сначала выберите здание"}
        />
      </div>
      <div className={styles.filterGroup}>
        <label className={styles.label}>Кабинет</label>
        <SearchableSelect
          key={`room-${(filters.floorIds as number[])?.join('-')}`}
          isMulti
          isDisabled={!hasFloors}
          value={filters.roomIds as number[]}
          onChange={(val) => updateFilters({ roomIds: val })}
          fetchOptions={fetchers.fetchRooms}
          fetchByIds={(ids) => dictionaryService.rooms.fetchByIds(ids)}
          placeholder={hasFloors ? "Выберите кабинет..." : "Сначала выберите этаж"}
        />
      </div>
    </div>
  );
};

interface StaticSelectProps {
  label: string;
  value: string | number | boolean | null;
  onChange: (val: string | number | boolean | null) => void;
  options: { value: string | number | boolean; label: string }[];
}

export const StaticSelectFilter = ({ label, value, onChange, options }: StaticSelectProps) => (
  <div className={styles.filterGroup}>
    <label className={styles.label}>{label}</label>
    <Select 
      value={value} 
      onChange={onChange} 
      options={options} 
      isSearchable={false} 
    />
  </div>
);

interface DateRangeProps {
  filters: Record<string, FilterValue>;
  updateFilters: UpdateFiltersFn;
}

export const DateRangeFilters = ({ filters, updateFilters }: DateRangeProps) => (
  <>
    <div className={styles.filterGroup}>
      <label className={styles.label}>Дата поступления (от)</label>
      <input type="datetime-local" step="1" className={styles.nativeInput}
        value={(filters.dateReceivedFrom as string) || ''}
        onChange={(e) => updateFilters({ dateReceivedFrom: e.target.value })}
      />
    </div>
    <div className={styles.filterGroup}>
      <label className={styles.label}>Дата поступления (до)</label>
      <input type="datetime-local" step="1" className={styles.nativeInput}
        value={(filters.dateReceivedTo as string) || ''}
        onChange={(e) => updateFilters({ dateReceivedTo: e.target.value })}
      />
    </div>
  </>
);