import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, SearchX } from 'lucide-react';
import { useSearchResults } from '../../features/search/hooks/useSearchResults';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { EmptyState } from '../../components/ui/EmptyState/EmptyState';
import { DonorCard } from '../../features/search/components/DonorCard/DonorCard';
import { Button } from '../../components/ui/Button/Button';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import styles from './SearchResultsPage.module.css';

export const SearchResultsPage = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);

  const { data, isLoading, isError } = useSearchResults(sessionId!, {
    page,
    size: pageSize
  });

  if (isLoading) return <Spinner fullPage size={40} />;

  if (isError) {
    return (
      <ErrorState 
        title="Сессия истекла или не найдена"
        message="Пожалуйста, вернитесь к устройству и запустите поиск заново."
        onAction={() => navigate('/')}
        actionLabel="На главную"
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

      {data?.items.length === 0 ? (
        <EmptyState 
          icon={<SearchX size={48} />}
          title="Доноров не найдено"
          message="В базе нет устройств с подходящими совместимыми деталями для вашего оборудования."
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
              onPageChange={setPage}
              onPageSizeChange={(size) => {
                setPageSize(size);
                setPage(0);
              }}
            />
          )}
        </div>
      )}
    </div>
  );
};