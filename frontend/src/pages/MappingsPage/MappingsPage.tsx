import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, CheckCircle, Edit, Trash2, Filter } from 'lucide-react';
import { toggleSort } from '../../utils/tableUtils';
import { useUrlFilters } from '../../hooks/useUrlFilters';
import { useMappings, useMappingMutations } from '../../features/admin/hooks/useMappings';
import { parseMappingFilters } from './mappingsParser';
import { formatDateTime } from '../../utils/formatters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { confidenceConfig } from '../../config/confidenceStatuses';
import { COMPONENT_CATEGORY_CONFIG } from '../../config/componentTypes';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Badge } from '../../components/ui/Badge/Badge';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import { TableCard, Table, TableHead, TableRow, TableHeader, TableBody, TableCell } from '../../components/ui/Table/Table';
import { MappingFilters } from '../../features/admin/components/MappingFilters/MappingFilters';
import { ConfirmModal } from '../../components/ui/ConfirmModal/ConfirmModal';
import styles from '../../styles/layouts/tablePageLayout.module.css';

export const MappingsPage = () => {
  useDocumentTitle('Таблица сопоставления');
  const navigate = useNavigate();
  
  const { filters, updateFilters, resetFilters } = useUrlFilters(['updatedAt,desc'], parseMappingFilters);
  const [searchValue, setSearchValue] = useState(filters.search);
  const [isFiltersOpen, setIsFiltersOpen] = useState(false);

  const [deleteId, setDeleteId] = useState<number | null>(null);

  const { data, isLoading } = useMappings(filters);
  const { confirmMutation, deleteMutation } = useMappingMutations();

  const handleSort = (field: string, isShiftPressed: boolean) => {
    const newSort = toggleSort(filters.sort as string[], field, isShiftPressed);
    updateFilters({ sort: newSort, page: 0 });
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.titleBlock}>
          <h1 className={styles.title}>Таблица сопоставления</h1>
          <p className={styles.subtitle}>Связь внешних названий с базой комплектующих</p>
        </div>

        <div className={styles.controlsArea}>
          <div className={styles.toolbar}>
            <div className={styles.searchBar}>
              <Input 
                icon={<Search size={18} />} 
                placeholder="Поиск записей..." 
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && updateFilters({ search: searchValue })}
                onClear={() => { setSearchValue(''); updateFilters({ search: '' }); }}
              />
            </div>
            <Button onClick={() => navigate('/mappings/new')}>
              <Plus size={18} /> Добавить
            </Button>
          </div>
          
          <Button variant="secondary" onClick={() => setIsFiltersOpen(true)} className={styles.filterBtn}>
            <Filter size={18} />
            <span>Фильтры</span>
          </Button>
        </div>
      </div>

      <TableCard isLoading={isLoading}>
        <Table>
          <TableHead>
            <TableRow>
              <TableHeader sortField="id" currentSort={filters.sort} onSort={handleSort}>ID</TableHeader>
              <TableHeader sortField="externalName" currentSort={filters.sort} onSort={handleSort}>Внешнее имя</TableHeader>
              <TableHeader sortField="internalComponentType" currentSort={filters.sort} onSort={handleSort}>Тип</TableHeader>
              <TableHeader sortField="internalComponentName" currentSort={filters.sort} onSort={handleSort}>Сопоставленный компонент</TableHeader>
              <TableHeader sortField="confidence" currentSort={filters.sort} onSort={handleSort}>Статус</TableHeader>
              <TableHeader sortField="updatedAt" currentSort={filters.sort} onSort={handleSort}>Обновлено</TableHeader>
              <TableHeader>Действия</TableHeader>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.items.map(row => (
              <TableRow key={row.id}>
                <TableCell className={styles.muted}>{row.id}</TableCell>
                <TableCell className={styles.bold}>{row.externalName}</TableCell>
                <TableCell>
                  <Badge variant="default">
                    {COMPONENT_CATEGORY_CONFIG[row.internalComponentType]?.label || row.internalComponentType}
                  </Badge>
                </TableCell>
                <TableCell>
                  <div className={styles.compCell}>
                    <span className={styles.compName}>{row.internalComponentName}</span>
                    <span className={styles.compId}>ID: {row.internalComponentId}</span>
                  </div>
                </TableCell>
                <TableCell>
                  <Badge variant={confidenceConfig[row.confidence].variant}>
                    {confidenceConfig[row.confidence].label}
                  </Badge>
                </TableCell>
                <TableCell className={styles.muted}>{formatDateTime(row.updatedAt)}</TableCell>
                <TableCell>
                  <div className={styles.actions}>
                    {row.confidence !== 'CONFIRMED' && (
                      <button 
                        className={styles.actionBtn} 
                        onClick={() => confirmMutation.mutate({ 
                          id: row.id,
                          dto: { ...row, confidence: 'CONFIRMED' } 
                        })}
                        title="Подтвердить"
                      >
                        <CheckCircle size={18} className={styles.iconSuccess} />
                      </button>
                    )}
                    <button className={styles.actionBtn} onClick={() => navigate(`/mappings/${row.id}`)} title="Редактировать">
                      <Edit size={18} className={styles.iconEdit} />
                    </button>
                    <button 
                      className={styles.actionBtn} 
                      onClick={() => setDeleteId(row.id)}
                      title="Удалить"
                    >
                      <Trash2 size={18} className={styles.iconDanger} />
                    </button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        
        <div className={styles.paginationWrapper}>
          <Pagination
            currentPage={data?.currentPage || 0} totalPages={data?.totalPages || 0}
            totalItems={data?.totalItems || 0} pageSize={data?.currentSize || 10}
            onPageChange={(p) => updateFilters({ page: p }, false)}
            onPageSizeChange={(s) => updateFilters({ size: s, page: 0 }, false)}
            pageSizeOptions={[6, 12, 24, 48, 96]}
          />
        </div>
      </TableCard>

      <MappingFilters 
        isOpen={isFiltersOpen} 
        onClose={() => setIsFiltersOpen(false)} 
        filters={filters}
        updateFilters={updateFilters}
        resetFilters={resetFilters}
      />

      <ConfirmModal 
        isOpen={deleteId !== null}
        onClose={() => setDeleteId(null)}
        onConfirm={() => {
          if (deleteId) deleteMutation.mutate(deleteId);
          setDeleteId(null);
        }}
        title="Удалить сопоставление?"
        message="Это действие нельзя отменить. Внешнее название снова станет нераспознанным."
        confirmLabel="Удалить"
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};