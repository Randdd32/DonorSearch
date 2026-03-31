import { SearchableSelect } from '../../../../components/ui/SearchableSelect/SearchableSelect';
import { Select } from '../../../../components/ui/Select/Select';
import { dictionaryService } from '../../../../services/dictionary.service';
import type { FilterDef } from './ComponentTableConfig';
import styles from './ComponentSelectionModal.module.css';

type FilterValueType = string | number | boolean | number[] | null | undefined;

interface DynamicFilterFieldProps {
  filterDef: FilterDef;
  value: unknown;
   onChange: (key: string, val: FilterValueType) => void; 
}

export const DynamicFilterField = ({ filterDef, value, onChange }: DynamicFilterFieldProps) => {
   if (filterDef.type === 'dictionary') {
    const dict = dictionaryService[filterDef.dictName];
    return (
      <div className={styles.filterGroup}>
        <label className={styles.filterLabel}>{filterDef.label}</label>
        <SearchableSelect
          isMulti
          value={value as number[]}
          onChange={(val) => onChange(filterDef.key, val)}
          fetchOptions={(s) => dict.fetchOptions(s)}
          fetchByIds={(ids) => dict.fetchByIds(ids)}
        />
      </div>
    );
  }

  if (filterDef.type === 'static') {
    return (
      <div className={styles.filterGroup}>
        <label className={styles.filterLabel}>{filterDef.label}</label>
        <Select
          value={value as string}
          onChange={(val) => onChange(filterDef.key, val)}
          options={[{ value: '', label: 'Все' }, ...filterDef.options]}
          isSearchable={false}
        />
      </div>
    );
  }

  if (filterDef.type === 'boolean') {
    return (
      <div className={styles.filterGroup}>
        <label className={styles.filterLabel}>{filterDef.label}</label>
        <Select
          value={value === undefined ? '' : value ? 'true' : 'false'}
          onChange={(val) => onChange(filterDef.key, val === '' ? undefined : val === 'true')}
          options={[{ value: '', label: 'Все' }, { value: 'true', label: 'Да' }, { value: 'false', label: 'Нет' }]}
          isSearchable={false}
        />
      </div>
    );
  }

  if (filterDef.type === 'range') {
    const rangeValue = (value as { min?: number, max?: number }) || {};
    return (
      <div className={styles.filterGroup}>
        <label className={styles.filterLabel}>{filterDef.label}</label>
        <div className={styles.rangeInputs}>
          {filterDef.rangeMinKey && (
            <input 
              type="number" placeholder="От" className={styles.nativeInput}
              value={rangeValue.min || ''} 
              onChange={(e) => onChange(filterDef.rangeMinKey!, e.target.value ? Number(e.target.value) : undefined)} 
            />
          )}
          {filterDef.rangeMaxKey && (
            <input 
              type="number" placeholder="До" className={styles.nativeInput}
              value={rangeValue.max || ''} 
              onChange={(e) => onChange(filterDef.rangeMaxKey!, e.target.value ? Number(e.target.value) : undefined)} 
            />
          )}
        </div>
      </div>
    );
  }

  if (filterDef.type === 'number') {
    return (
      <div className={styles.filterGroup}>
        <label className={styles.filterLabel}>{filterDef.label}</label>
        <input 
          type="number" 
          className={styles.nativeInput}
          placeholder="Точное значение"
          value={(value as number) || ''} 
          onChange={(e) => onChange(filterDef.exactKey, e.target.value ? Number(e.target.value) : undefined)} 
        />
      </div>
    );
  }

  return null;
};