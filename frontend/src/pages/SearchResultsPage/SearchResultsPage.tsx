import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, SearchX, ChevronLeft, ChevronRight } from 'lucide-react';
import { useSearchResults } from '../../features/search/hooks/useSearchResults';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { EmptyState } from '../../components/ui/EmptyState/EmptyState';
import { DonorCard } from '../../features/search/components/DonorCard/DonorCard';
import { Button } from '../../components/ui/Button/Button';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import styles from './SearchResultsPage.module.css';

export const SearchResultsPage = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useSearchResults(sessionId!, {
    page,
    size: 10
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

          {/* Пагинация */}
          {data && data.totalPages > 1 && (
            <div className={styles.pagination}>
              <span className={styles.pageInfo}>
                Показано {data.items.length} из {data.totalItems} доноров
              </span>
              <div className={styles.paginationControls}>
                <Button 
                  variant="secondary" 
                  disabled={data.isFirst} 
                  onClick={() => setPage((p) => Math.max(0, p - 1))}
                >
                  <ChevronLeft size={18} /> Назад
                </Button>
                <span className={styles.pageNumber}>{data.currentPage + 1} / {data.totalPages}</span>
                <Button 
                  variant="secondary" 
                  disabled={data.isLast} 
                  onClick={() => setPage((p) => p + 1)}
                >
                  Вперед <ChevronRight size={18} />
                </Button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};