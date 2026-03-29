import { X, RotateCcw } from 'lucide-react';
import { clsx } from 'clsx';
import { Button } from '../../../../components/ui/Button/Button';
import { SearchableSelect } from '../../../../components/ui/SearchableSelect/SearchableSelect';
import { Select } from '../../../../components/ui/Select/Select';
import { dictionaryService } from '../../../../services/dictionary.service';
import type { FilterValue } from '../../../../hooks/useUrlFilters';
import styles from '../../../filters/components/DeviceFilters/DeviceFilters.module.css';

interface DonorFiltersProps {
  isOpen: boolean;
  onClose: () => void;
  filters: Record<string, FilterValue>;
  updateFilters: (updates: Record<string, FilterValue>) => void;
  resetFilters: () => void;
}

export const DonorFilters = ({ isOpen, onClose, filters, updateFilters, resetFilters }: DonorFiltersProps) => {
  const hasDeviceManufacturers = Array.isArray(filters.deviceManufacturerIds) && filters.deviceManufacturerIds.length > 0;
  const hasBuildings = Array.isArray(filters.buildingIds) && filters.buildingIds.length > 0;
  const hasFloors = Array.isArray(filters.floorIds) && filters.floorIds.length > 0;

  const modelKey = `model-${(filters.deviceManufacturerIds as number[])?.join('-')}`;
  const floorKey = `floor-${(filters.buildingIds as number[])?.join('-')}`;
  const roomKey = `room-${(filters.floorIds as number[])?.join('-')}`;

  const isWorkingValue = filters.isWorking === undefined ? '' : filters.isWorking ? 'true' : 'false';

  const fetchComponentMfrs = (s?: string) => dictionaryService.manufacturers.fetchOptions(s);
  const fetchStates = (s?: string) => dictionaryService.states.fetchOptions(s);
  const fetchDeviceTypes = (s?: string) => dictionaryService.deviceTypes.fetchOptions(s);
  const fetchDepartments = (s?: string) => dictionaryService.departments.fetchOptions(s);
  
  const fetchDeviceMfrs = (s?: string) => dictionaryService.manufacturers.fetchOptions(s);
  const fetchModels = (s?: string) => dictionaryService.deviceModels.fetchOptions(s, filters.deviceManufacturerIds as number[]);
  
  const fetchBuildings = (s?: string) => dictionaryService.buildings.fetchOptions(s);
  const fetchFloors = (s?: string) => dictionaryService.floors.fetchOptions(s, filters.buildingIds as number[]);
  const fetchRooms = (s?: string) => dictionaryService.rooms.fetchOptions(s, filters.floorIds as number[]);

  return (
    <>
      <div className={clsx(styles.backdrop, {[styles.open]: isOpen })} onClick={onClose} />

      <div className={clsx(styles.sidebar, { [styles.open]: isOpen })}>
        <div className={styles.header}>
          <h3 className={styles.title}>Фильтры доноров</h3>
          <button onClick={onClose} className={styles.closeBtn}><X size={20} /></button>
        </div>

        <div className={styles.content}>
          <div className={styles.hierarchyGroup}>
            <h4 className={styles.hierarchyTitle}>Совместимость</h4>
            <div className={styles.filterGroup}>
              <label className={styles.label}>Максимальный штраф</label>
              <input 
                type="number" 
                className={styles.nativeSelect}
                placeholder="Например: 20"
                min="0"
                value={(filters.maxTotalPenalty as number) || ''}
                onChange={(e) => updateFilters({ maxTotalPenalty: e.target.value ? Number(e.target.value) : undefined })}
              />
            </div>
            <div className={styles.filterGroup}>
              <label className={styles.label}>Производитель искомой детали</label>
              <SearchableSelect
                isMulti
                value={filters.componentManufacturerIds as number[]}
                onChange={(val) => updateFilters({ componentManufacturerIds: val })}
                fetchOptions={fetchComponentMfrs}
                fetchByIds={(ids) => dictionaryService.manufacturers.fetchByIds(ids)}
                placeholder="Любой..."
              />
            </div>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Статус ПК-донора</label>
            <SearchableSelect
              isMulti
              value={filters.stateIds as number[]}
              onChange={(val) => updateFilters({ stateIds: val })}
              fetchOptions={fetchStates}
              fetchByIds={(ids) => dictionaryService.states.fetchByIds(ids)}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Работоспособность ПК-донора</label>
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
            <label className={styles.label}>Тип ПК-донора</label>
            <SearchableSelect
              isMulti
              value={filters.typeIds as number[]}
              onChange={(val) => updateFilters({ typeIds: val })}
              fetchOptions={fetchDeviceTypes}
              fetchByIds={(ids) => dictionaryService.deviceTypes.fetchByIds(ids)}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Отдел</label>
            <SearchableSelect
              isMulti
              value={filters.departmentIds as number[]}
              onChange={(val) => updateFilters({ departmentIds: val })}
              fetchOptions={fetchDepartments}
              fetchByIds={(ids) => dictionaryService.departments.fetchByIds(ids)}
            />
          </div>

          <div className={styles.hierarchyGroup}>
            <h4 className={styles.hierarchyTitle}>Производитель и Модель ПК</h4>
            <div className={styles.filterGroup}>
              <label className={styles.label}>Производитель</label>
              <SearchableSelect
                isMulti
                value={filters.deviceManufacturerIds as number[]}
                onChange={(val) => updateFilters({ deviceManufacturerIds: val, modelIds:[] })}
                fetchOptions={fetchDeviceMfrs}
                fetchByIds={(ids) => dictionaryService.manufacturers.fetchByIds(ids)}
              />
            </div>
            <div className={styles.filterGroup}>
              <label className={styles.label}>Модель</label>
              <SearchableSelect
                key={modelKey}
                isMulti
                isDisabled={!hasDeviceManufacturers}
                value={filters.modelIds as number[]}
                onChange={(val) => updateFilters({ modelIds: val })}
                fetchOptions={fetchModels}
                fetchByIds={(ids) => dictionaryService.deviceModels.fetchByIds(ids)}
                placeholder={hasDeviceManufacturers ? "Выберите модель..." : "Сначала выберите производителя"}
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
                onChange={(val) => updateFilters({ buildingIds: val, floorIds:[], roomIds:[] })}
                fetchOptions={fetchBuildings}
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
                fetchOptions={fetchFloors}
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
                fetchOptions={fetchRooms}
                fetchByIds={(ids) => dictionaryService.rooms.fetchByIds(ids)}
                placeholder={hasFloors ? "Выберите кабинет..." : "Сначала выберите этаж"}
              />
            </div>
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Дата поступления (От)</label>
            <input 
              type="datetime-local" 
              step="1"
              className={styles.nativeSelect}
              value={(filters.dateReceivedFrom as string) || ''}
              onChange={(e) => updateFilters({ dateReceivedFrom: e.target.value })}
            />
          </div>

          <div className={styles.filterGroup}>
            <label className={styles.label}>Дата поступления (До)</label>
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