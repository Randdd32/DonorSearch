import { useState } from 'react';
import { Search, PackageOpen, ChevronLeft, ChevronRight } from 'lucide-react';
import { useDevices } from '../../features/devices/hooks/useDevices';
import { useDebounce } from '../../hooks/useDebounce';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { DeviceCard } from '../../features/devices/components/DeviceCard/DeviceCard';
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

  const handlePageSizeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setPageSize(Number(e.target.value));
    setPage(0);
  };

  if (isError) {
    return <div className={styles.error}>Ошибка при загрузке данных. Проверьте соединение с сервером.</div>;
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
        <div className={styles.emptyState}>
          <PackageOpen size={64} className={styles.emptyIcon} />
          <h3>Оборудование не найдено</h3>
          <p>По вашему запросу ничего не нашлось. Попробуйте изменить параметры поиска.</p>
        </div>
      ) : (
        <>
          <div className={styles.grid}>
            {data?.items.map((device) => (
              <DeviceCard key={device.externalId} device={device} />
            ))}
          </div>
          
          {data && (
            <div className={styles.pagination}>
              <div className={styles.pageSizeSelector}>
                <span>Показывать:</span>
                <select 
                  className={styles.select} 
                  value={pageSize} 
                  onChange={handlePageSizeChange}
                >
                  <option value={6}>6</option>
                  <option value={12}>12</option>
                  <option value={24}>24</option>
                  <option value={48}>48</option>
                </select>
                <span className={styles.pageInfo}>
                  из {data.totalItems} записей
                </span>
              </div>

              {data.totalPages > 1 && (
                <div className={styles.paginationControls}>
                  <Button variant="secondary" disabled={data.isFirst} onClick={() => setPage((p) => Math.max(0, p - 1))}>
                    <ChevronLeft size={18} /> Назад
                  </Button>
                  <span className={styles.pageNumber}>{data.currentPage + 1} / {data.totalPages}</span>
                  <Button variant="secondary" disabled={data.isLast} onClick={() => setPage((p) => p + 1)}>
                    Вперед <ChevronRight size={18} />
                  </Button>
                </div>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};