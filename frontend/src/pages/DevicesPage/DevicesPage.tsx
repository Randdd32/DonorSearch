import { useState } from 'react';
import { Filter, Search, PackageOpen } from 'lucide-react';
import { useDevices } from '../../features/devices/hooks/useDevices';
import { useDebounce } from '../../hooks/useDebounce';
import { useUrlFilters } from '../../hooks/useUrlFilters';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { EmptyState } from '../../components/ui/EmptyState/EmptyState';
import { Input } from '../../components/ui/Input/Input';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { DeviceCard } from '../../features/devices/components/DeviceCard/DeviceCard';
import { SortSelect } from '../../components/ui/SortSelect/SortSelect';
import { DeviceFilters } from '../../features/devices/components/DeviceFilters/DeviceFilters';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import { Button } from '../../components/ui/Button/Button';
import styles from './DevicesPage.module.css';

export const DevicesPage = () => {
  const { filters, updateFilters, resetFilters } = useUrlFilters('dateReceived,desc');
  const debouncedSearch = useDebounce(filters.search as string, 500);
  const [isFiltersOpen, setIsFiltersOpen] = useState(false);

  const { data, isLoading, isError } = useDevices({ 
    ...filters,
    search: debouncedSearch || undefined
  });

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateFilters({ search: e.target.value });
  };

  if (isError) {
    return <ErrorState onAction={() => window.location.reload()} actionLabel="Обновить страницу" />;
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          <h1 className={styles.title}>Учетные единицы</h1>
          <p className={styles.subtitle}>База компьютерной техники и оборудования</p>
        </div>
        
        <div className={styles.toolbar}>
          <div className={styles.searchBar}>
            <Input 
              icon={<Search size={18} />} 
              placeholder="Поиск по названию, инв. номеру или SN..." 
              value={filters.search as string}
              onChange={handleSearchChange}
            />
          </div>

          <SortSelect 
            value={filters.sort as string} 
            onChange={(val) => updateFilters({ sort: val })} 
            options={[
              { value: 'dateReceived,desc', label: 'Сначала новые' },
              { value: 'dateReceived,asc', label: 'Сначала старые' },
              { value: 'name,asc', label: 'По названию (А-Я)' },
              { value: 'inventoryNumber,asc', label: 'По инвентарному номеру' }
            ]}
          />

          <Button variant="secondary" onClick={() => setIsFiltersOpen(true)} className={styles.filterBtn}>
            <Filter size={18} />
            <span className={styles.filterBtnText}>Фильтры</span>
          </Button>
        </div>
      </div>

      {isLoading ? (
        <Spinner fullPage size={40} />
      ) : data?.items.length === 0 ? (
        <EmptyState 
          icon={<PackageOpen size={64} />}
          title="Оборудование не найдено"
          message="По вашему запросу ничего не нашлось. Попробуйте изменить параметры поиска."
        />
      ) : (
        <>
          <div className={styles.grid}>
            {data?.items.map((device) => (
              <DeviceCard key={device.externalId} device={device} />
            ))}
          </div>
          
          {data && data.totalItems > 0 && (
            <Pagination
              currentPage={data.currentPage}
              totalPages={data.totalPages}
              totalItems={data.totalItems}
              pageSize={data.currentSize}
              onPageChange={(p) => updateFilters({ page: p }, false)}
              onPageSizeChange={(s) => updateFilters({ size: s, page: 0 }, false)}
              pageSizeOptions={[6, 12, 24, 48, 96]}
            />
          )}
        </>
      )}

      <DeviceFilters 
        isOpen={isFiltersOpen} 
        onClose={() => setIsFiltersOpen(false)} 
        filters={filters}
        updateFilters={updateFilters}
        resetFilters={resetFilters}
      />
    </div>
  );
};