import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, CheckCircle, Edit, Trash2, Filter } from 'lucide-react';
import { useUrlFilters } from '../../hooks/useUrlFilters';
import { useMappings, useMappingMutations } from '../../features/admin/hooks/useMappings';
import { parseMappingFilters } from './mappingsParser';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Badge } from '../../components/ui/Badge/Badge';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import { TableCard, Table, TableHead, TableRow, TableHeader, TableBody, TableCell } from '../../components/ui/Table/Table';
import { MappingFilters } from '../../features/admin/components/MappingFilters/MappingFilters';
import { formatDateTime } from '../../utils/formatters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import type { MappingConfidence } from '../../types/integration';
import styles from './MappingsPage.module.css';

const confidenceConfig: Record<MappingConfidence, { label: string, variant: 'success' | 'info' | 'warning' | 'danger' }> = {
  CONFIRMED: { label: 'Подтверждено', variant: 'success' },
  AUTO: { label: 'Авто (Высокая)', variant: 'info' },
  NEEDS_REVIEW: { label: 'Требует проверки', variant: 'warning' },
  BAD_MATCH: { label: 'Низкая уверенность', variant: 'danger' },
};

export const MappingsPage = () => {
  useDocumentTitle('Таблица сопоставления');
  const navigate = useNavigate();
  
  const { filters, updateFilters, resetFilters } = useUrlFilters(['updatedAt,desc'], parseMappingFilters);
  const [searchValue, setSearchValue] = useState(filters.search);
  const [isFiltersOpen, setIsFiltersOpen] = useState(false);

  const { data, isLoading } = useMappings(filters);
  const { confirmMutation, deleteMutation } = useMappingMutations();

  const handleSort = (field: string, isShiftPressed: boolean) => {
    let currentSort = [...filters.sort];
    const existingIndex = currentSort.findIndex(s => s.startsWith(field));
    let newDirection = 'asc';

    if (existingIndex >= 0) {
      const currentDir = currentSort[existingIndex].split(',')[1];
      newDirection = currentDir === 'asc' ? 'desc' : '';
    }

    const sortString = newDirection ? `${field},${newDirection}` : null;

    if (isShiftPressed) {
      if (existingIndex >= 0) {
        if (sortString) currentSort[existingIndex] = sortString;
        else currentSort.splice(existingIndex, 1);
      } else if (sortString) {
        currentSort.push(sortString);
      }
    } else {
      currentSort = sortString ? [sortString] : ['updatedAt,desc'];
    }
    updateFilters({ sort: currentSort, page: 0 });
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
                placeholder="Поиск по названию..." 
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
                <TableCell><Badge variant="default">{row.internalComponentType}</Badge></TableCell>
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
                      <button className={styles.actionBtn} onClick={() => confirmMutation.mutate({ id: row.id, componentId: row.internalComponentId })} title="Подтвердить">
                        <CheckCircle size={18} className={styles.iconSuccess} />
                      </button>
                    )}
                    <button className={styles.actionBtn} onClick={() => navigate(`/mappings/${row.id}`)} title="Редактировать">
                      <Edit size={18} className={styles.iconEdit} />
                    </button>
                    <button className={styles.actionBtn} onClick={() => window.confirm('Удалить маппинг?') && deleteMutation.mutate(row.id)} title="Удалить">
                      <Trash2 size={18} className={styles.iconDanger} />
                    </button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        
        <div className={styles.pagination}>
          <Pagination
            currentPage={data?.currentPage || 0} totalPages={data?.totalPages || 0}
            totalItems={data?.totalItems || 0} pageSize={data?.currentSize || 10}
            onPageChange={(p) => updateFilters({ page: p }, false)}
            onPageSizeChange={(s) => updateFilters({ size: s, page: 0 }, false)}
            pageSizeOptions={[5, 10, 20, 50]}
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
    </div>
  );
};