import { X, RotateCcw } from 'lucide-react';
import { clsx } from 'clsx';
import { Button } from '../../../../components/ui/Button/Button';
import { SearchableSelect } from '../../../../components/ui/SearchableSelect/SearchableSelect';
import { Select } from '../../../../components/ui/Select/Select';
import { dictionaryService } from '../../../../services/dictionary.service';
import type { FilterValue } from '../../../../hooks/useUrlFilters';
import styles from './DeviceFilters.module.css';

interface DeviceFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: Record<string, FilterValue>;
  updateFilters: (updates: Record<string, FilterValue>) => void;
  resetFilters: () => void;
}

export const DeviceFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: DeviceFiltersProps) => {
  const hasManufacturers = Array.isArray(filters.manufacturerIds) && filters.manufacturerIds.length > 0;
  const hasBuildings = Array.isArray(filters.buildingIds) && filters.buildingIds.length > 0;
  const hasFloors = Array.isArray(filters.floorIds) && filters.floorIds.length > 0;

  const modelKey = `model-${(filters.manufacturerIds as number[])?.join('-')}`;
  const floorKey = `floor-${(filters.buildingIds as number[])?.join('-')}`;
  const roomKey = `room-${(filters.floorIds as number[])?.join('-')}`;

  const isWorkingValue = filters.isWorking === undefined ? '' : filters.isWorking ? 'true' : 'false';

  return (
    <>
      <div className={clsx(styles.backdrop, { [styles.open]: isOpen })} onClick={onClose} />

      <div className={clsx(styles.sidebar, { [styles.open]: isOpen })}>
        <div className={styles.header}>
          <h3 className={styles.title}>Фильтры</h3>
          <button onClick={onClose} className={styles.closeBtn}><X size={20} /></button>
        </div>

        <div className={styles.content}>
          
          <div className={styles.filterGroup}>
            <label className={styles.label}>Статус (Жизненный цикл)</label>
            <SearchableSelect
              isMulti
              value={filters.stateIds as number[]}
              onChange={(val) => updateFilters({ stateIds: val })}
              fetchOptions={(s) => dictionaryService.states.fetchOptions(s)}
              fetchByIds={(ids) => dictionaryService.states.fetchByIds(ids)}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Работоспособность</label>
            <Select
              value={isWorkingValue}
              onChange={(val) => updateFilters({ isWorking: val === '' ? undefined : val === 'true' })}
              options={[
                { value: '', label: 'Все устройства' },
                { value: 'true', label: 'Только исправные' },
                { value: 'false', label: 'Только неисправные' }
              ]}
              isSearchable={false}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Тип оборудования</label>
            <SearchableSelect
              isMulti
              value={filters.typeIds as number[]}
              onChange={(val) => updateFilters({ typeIds: val })}
              fetchOptions={(s) => dictionaryService.deviceTypes.fetchOptions(s)}
              fetchByIds={(ids) => dictionaryService.deviceTypes.fetchByIds(ids)}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Отдел</label>
            <SearchableSelect
              isMulti
              value={filters.departmentIds as number[]}
              onChange={(val) => updateFilters({ departmentIds: val })}
              fetchOptions={(s) => dictionaryService.departments.fetchOptions(s)}
              fetchByIds={(ids) => dictionaryService.departments.fetchByIds(ids)}
            />
          </div>

          <div className={styles.hierarchyGroup}>
            <h4 className={styles.hierarchyTitle}>Производитель и Модель</h4>
            
            <div className={styles.filterGroup}>
              <label className={styles.label}>Производитель</label>
              <SearchableSelect
                isMulti
                value={filters.manufacturerIds as number[]}
                onChange={(val) => updateFilters({ manufacturerIds: val, modelIds:[] })}
                fetchOptions={(s) => dictionaryService.manufacturers.fetchOptions(s)}
                fetchByIds={(ids) => dictionaryService.manufacturers.fetchByIds(ids)}
              />
            </div>
            
            <div className={styles.filterGroup}>
              <label className={styles.label}>Модель</label>
              <SearchableSelect
                key={modelKey}
                isMulti
                isDisabled={!hasManufacturers}
                value={filters.modelIds as number[]}
                onChange={(val) => updateFilters({ modelIds: val })}
                fetchOptions={(s) => dictionaryService.deviceModels.fetchOptions(s, filters.manufacturerIds as number[])}
                fetchByIds={(ids) => dictionaryService.deviceModels.fetchByIds(ids)}
                placeholder={hasManufacturers ? "Выберите модель..." : "Сначала выберите производителя"}
              />
            </div>
          </div>

          <div className={styles.hierarchyGroup}>
            <h4 className={styles.hierarchyTitle}>Расположение</h4>
            
            <div className={styles.filterGroup}>
              <label className={styles.label}>Здание</label>
              <SearchableSelect
                isMulti
                value={filters.buildingIds as number[]}
                onChange={(val) => updateFilters({ buildingIds: val, floorIds: [], roomIds:[] })}
                fetchOptions={(s) => dictionaryService.buildings.fetchOptions(s)}
                fetchByIds={(ids) => dictionaryService.buildings.fetchByIds(ids)}
              />
            </div>
            
            <div className={styles.filterGroup}>
              <label className={styles.label}>Этаж</label>
              <SearchableSelect
                key={floorKey}
                isMulti
                isDisabled={!hasBuildings}
                value={filters.floorIds as number[]}
                onChange={(val) => updateFilters({ floorIds: val, roomIds:[] })}
                fetchOptions={(s) => dictionaryService.floors.fetchOptions(s, filters.buildingIds as number[])}
                fetchByIds={(ids) => dictionaryService.floors.fetchByIds(ids)}
                placeholder={hasBuildings ? "Выберите этаж..." : "Сначала выберите здание"}
              />
            </div>
            
            <div className={styles.filterGroup}>
              <label className={styles.label}>Кабинет</label>
              <SearchableSelect
                key={roomKey}
                isMulti
                isDisabled={!hasFloors}
                value={filters.roomIds as number[]}
                onChange={(val) => updateFilters({ roomIds: val })}
                fetchOptions={(s) => dictionaryService.rooms.fetchOptions(s, filters.floorIds as number[])}
                fetchByIds={(ids) => dictionaryService.rooms.fetchByIds(ids)}
                placeholder={hasFloors ? "Выберите кабинет..." : "Сначала выберите этаж"}
              />
            </div>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Дата поступления (от)</label>
            <input 
              type="datetime-local" 
              step="1"
              className={styles.nativeSelect}
              value={(filters.dateReceivedFrom as string) || ''}
              onChange={(e) => updateFilters({ dateReceivedFrom: e.target.value })}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Дата поступления (до)</label>
            <input 
              type="datetime-local" 
              step="1"
              className={styles.nativeSelect}
              value={(filters.dateReceivedTo as string) || ''}
              onChange={(e) => updateFilters({ dateReceivedTo: e.target.value })}
            />
          </div>

        </div>

        <div className={styles.footer}>
          <Button variant="ghost" onClick={resetFilters} className={styles.resetBtn}>
            <RotateCcw size={16} />
            Сбросить
          </Button>
          <Button onClick={onClose} className={styles.applyBtn}>
            Показать
          </Button>
        </div>
      </div>
    </>
  );
};