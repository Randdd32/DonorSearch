import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, SearchX, Filter, Search, Download, Printer } from 'lucide-react';
import toast from 'react-hot-toast';
import { isAxiosError } from 'axios';
import { searchService } from '../../services/search.service';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
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
import { parseDonorFilters } from './donorParser';
import { DonorFilters } from '../../features/search/components/DonorFilters/DonorFilters';
import styles from './SearchResultsPage.module.css';

export const SearchResultsPage = () => {
  useDocumentTitle('Результаты подбора');
  
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();
  
  const { filters, updateFilters, resetFilters } = useUrlFilters('totalPenalty,asc', parseDonorFilters);
  const debouncedSearch = useDebounce(filters.search as string, 500);
  const[isFiltersOpen, setIsFiltersOpen] = useState(false);

  const { data, isLoading, isError } = useSearchResults(sessionId!, {
    ...filters,
    search: debouncedSearch || undefined
  });

  const[isExporting, setIsExporting] = useState(false);
  const[isPrinting, setIsPrinting] = useState(false);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    updateFilters({ search: e.target.value });
  };

  const handleExportPdf = async () => {
    try {
      setIsExporting(true);
      const blob = await searchService.exportPdf(sessionId!, filters);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `donor-report.pdf`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (e) {
      if (!isAxiosError(e)) {
        toast.error('Внутренняя ошибка при скачивании PDF отчета');
      }
    } finally {
      setIsExporting(false);
    }
  };

  const handlePrint = async () => {
    try {
      setIsPrinting(true);
      const blob = await searchService.exportPdf(sessionId!, filters);
      const url = URL.createObjectURL(blob);
      const win = window.open(url);
      if (win) {
        win.onload = () => {
          win.focus();
          win.print();
        };
      }
    } catch (e) {
      if (!isAxiosError(e)) {
        toast.error('Внутренняя ошибка при подготовке документа к печати');
      }
    } finally {
      setIsPrinting(false);
    }
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
        <div className={styles.titleBlock}>
          <Button variant="ghost" onClick={() => navigate(-1)} className={styles.backButton}>
            <ArrowLeft size={16} /> Назад
          </Button>
          <h1 className={styles.title}>Результаты подбора доноров</h1>
          <p className={styles.subtitle}>По умолчанию, список отсортирован по релевантности (меньше штрафов — лучше)</p>
        </div>

        <div className={styles.controlsArea}>
          <div className={styles.toolbarTop}>
            <div className={styles.searchBar}>
              <Input 
                icon={<Search size={18} />} 
                placeholder="Поиск по названию донора, инв. номеру или SN..." 
                value={filters.search as string}
                onChange={handleSearchChange}
                onClear={() => updateFilters({ search: '' })}
              />
            </div>

            <div className={styles.actionButtons}>
              <Button 
                variant="secondary" 
                onClick={handleExportPdf} 
                isLoading={isExporting}
                title="Скачать PDF отчет"
              >
                <Download size={18} />
                <span className={styles.outputBtnText}>PDF</span>
              </Button>

              <Button 
                variant="secondary" 
                onClick={handlePrint} 
                isLoading={isPrinting}
                title="Распечатать результаты"
              >
                <Printer size={18} />
                <span className={styles.outputBtnText}>Печать</span>
              </Button>
            </div>
          </div>

          <div className={styles.toolbarBottom}>
            <SortSelect
              className={styles.sortSelect}
              value={filters.sort[0] || ''}  
              onChange={(val) => updateFilters({ sort: [val] })}
              options={[
                { value: 'totalPenalty,asc', label: 'По релевантности (лучшие)' },
                { value: 'totalPenalty,desc', label: 'По релевантности (худшие)' },
                { value: 'dateReceived,desc', label: 'Сначала новые ПК' },
                { value: 'dateReceived,asc', label: 'Сначала старые ПК' },
                { value: 'name,asc', label: 'По названию (А-Я)' },
                { value: 'inventoryNumber,asc', label: 'По инв. номеру' }
              ]}
            />

            <Button variant="secondary" onClick={() => setIsFiltersOpen(true)}>
              <Filter size={18} />
              <span className={styles.filterBtnText}>Фильтры</span>
            </Button>
          </div>
        </div>
      </div>

      {data?.items.length === 0 ? (
        <EmptyState 
          icon={<SearchX size={48} />}
          title="Доноров не найдено"
          message="С учетом текущих фильтров мы не нашли подходящих доноров. Попробуйте сбросить фильтры."
        />
      ) : (
        <div className={styles.resultsWrapper}>
          <div className={styles.resultsGrid}>
            {data?.items.map((result, idx) => (
              <DonorCard key={`${result.donorDevice.externalId}-${idx}`} result={result} />
            ))}
          </div>

          {data && data.totalItems > 0 && (
            <Pagination
              className={styles.paginationSpacing}
              currentPage={data.currentPage}
              totalPages={data.totalPages}
              totalItems={data.totalItems}
              pageSize={data.currentSize}
              onPageChange={(p) => updateFilters({ page: p }, false)}
              onPageSizeChange={(s) => updateFilters({ size: s, page: 0 }, false)}
              pageSizeOptions={[6, 12, 24, 48]}
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