import { useState } from 'react';
import { Search, PackageOpen } from 'lucide-react';
import { useDevices } from '../../features/devices/hooks/useDevices';
import { useDebounce } from '../../hooks/useDebounce';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { EmptyState } from '../../components/ui/EmptyState/EmptyState';
import { Input } from '../../components/ui/Input/Input';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { DeviceCard } from '../../features/devices/components/DeviceCard/DeviceCard';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import styles from './DevicesPage.module.css';

export const DevicesPage = () => {
  const [searchValue, setSearchValue] = useState('');
  const debouncedSearch = useDebounce(searchValue, 500);
  
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(24);

  const { data, isLoading, isError } = useDevices({ 
    page: debouncedSearch !== searchValue ? 0 : page,
    size: pageSize,
    search: debouncedSearch || undefined
  });

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchValue(e.target.value);
    setPage(0);
  };

  if (isError) {
    return (
      <ErrorState 
        onAction={() => window.location.reload()} 
        actionLabel="Обновить страницу" 
      />
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div>
          <h1 className={styles.title}>Учетные единицы</h1>
          <p className={styles.subtitle}>База компьютерной техники и оборудования</p>
        </div>
        
        <div className={styles.filters}>
          <Input 
            icon={<Search size={18} />} 
            placeholder="Поиск по названию, инв. номеру или SN..." 
            value={searchValue}
            onChange={handleSearchChange}
            className={styles.searchInput}
          />
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
              onPageChange={setPage}
              onPageSizeChange={(size) => {
                setPageSize(size);
                setPage(0);
              }}
              pageSizeOptions={[6, 12, 24, 48, 96]}
            />
          )}
        </>
      )}
    </div>
  );
};