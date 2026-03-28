import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, SearchX, Filter, Search } from 'lucide-react';
import { useSearchResults } from '../../features/search/hooks/useSearchResults';
import { DonorCard } from '../../features/search/components/DonorCard/DonorCard';
import { Button } from '../../components/ui/Button/Button';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { EmptyState } from '../../components/ui/EmptyState/EmptyState';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import { Input } from '../../components/ui/Input/Input';
import { SortSelect } from '../../components/ui/SortSelect/SortSelect';
import { useUrlFilters } from '../../hooks/useUrlFilters';
import { useDebounce } from '../../hooks/useDebounce';
import { DonorFilters } from '../../features/search/components/DonorFilters/DonorFilters';
import styles from './SearchResultsPage.module.css';

export const SearchResultsPage = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();
  
  const { filters, updateFilters, resetFilters } = useUrlFilters('totalPenalty,asc');
  const debouncedSearch = useDebounce(filters.search as string, 500);
  const[isFiltersOpen, setIsFiltersOpen] = useState(false);

  const { data, isLoading, isError } = useSearchResults(sessionId!, {
    ...filters,
    search: debouncedSearch || undefined
  });

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateFilters({ search: e.target.value });
  };

  if (isLoading) return <Spinner fullPage size={40} />;

  if (isError) {
    return (
      <ErrorState 
        title="Сессия истекла или не найдена"
        message="Пожалуйста, вернитесь к устройству и запустите поиск заново."
        onAction={() => navigate(-1)}
        actionLabel="Вернуться к устройству"
      />
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Button variant="ghost" onClick={() => navigate(-1)} className={styles.backButton}>
          <ArrowLeft size={18} />
          Назад
        </Button>
        <div>
          <h1 className={styles.title}>Результаты подбора доноров</h1>
          <p className={styles.subtitle}>Список отсортирован по релевантности (меньше штрафов — лучше)</p>
        </div>
      </div>

      <div className={styles.toolbar}>
        <div className={styles.searchBar}>
          <Input 
            icon={<Search size={18} />} 
            placeholder="Поиск по названию донора, инв. номеру или SN..." 
            value={filters.search as string}
            onChange={handleSearchChange}
          />
        </div>

        <SortSelect 
          value={filters.sort as string} 
          onChange={(val) => updateFilters({ sort: val })} 
          options={[
            { value: 'totalPenalty,asc', label: 'По релевантности (лучшие)' },
            { value: 'totalPenalty,desc', label: 'По релевантности (худшие)' },
            { value: 'dateReceived,desc', label: 'Сначала новые ПК' },
            { value: 'dateReceived,asc', label: 'Сначала старые ПК' },
            { value: 'name,asc', label: 'По названию (А-Я)' },
            { value: 'inventoryNumber,asc', label: 'По инвентарному номеру' }
          ]}
        />

        <Button variant="secondary" onClick={() => setIsFiltersOpen(true)} className={styles.filterBtn}>
          <Filter size={18} />
          <span className={styles.filterBtnText}>Фильтры</span>
        </Button>
      </div>

      {data?.items.length === 0 ? (
        <EmptyState 
          icon={<SearchX size={48} />}
          title="Доноров не найдено"
          message="С учетом текущих фильтров мы не нашли подходящих доноров. Попробуйте сбросить фильтры."
        />
      ) : (
        <div className={styles.resultsList}>
          {data?.items.map((result, idx) => (
            <DonorCard key={`${result.donorDevice.externalId}-${idx}`} result={result} />
          ))}

          {data && data.totalItems > 0 && (
            <Pagination
              currentPage={data.currentPage}
              totalPages={data.totalPages}
              totalItems={data.totalItems}
              pageSize={data.currentSize}
              onPageChange={(p) => updateFilters({ page: p }, false)}
              onPageSizeChange={(s) => updateFilters({ size: s, page: 0 }, false)}
              pageSizeOptions={[5, 10, 20, 50]}
            />
          )}
        </div>
      )}

      <DonorFilters 
        isOpen={isFiltersOpen} 
        onClose={() => setIsFiltersOpen(false)} 
        filters={filters}
        updateFilters={updateFilters}
        resetFilters={resetFilters}
      />
    </div>
  );
};