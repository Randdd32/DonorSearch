import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Edit, Trash2, Filter } from 'lucide-react';
import { useUrlFilters } from '../../hooks/useUrlFilters';
import { useRules, useRuleMutations } from '../../features/admin/hooks/useRules';
import { parseRuleFilters } from './rulesParser';
import { formatDateTime } from '../../utils/formatters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { COMPONENT_CATEGORY_CONFIG } from '../../config/componentTypes';
import { toggleSort } from '../../utils/tableUtils';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Badge } from '../../components/ui/Badge/Badge';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import { TableCard, Table, TableHead, TableRow, TableHeader, TableBody, TableCell } from '../../components/ui/Table/Table';
import { RuleFilters } from '../../features/admin/components/RuleFilters/RuleFilters';
import { ConfirmModal } from '../../components/ui/ConfirmModal/ConfirmModal';
import styles from '../../styles/layouts/tablePageLayout.module.css';

export const RulesPage = () => {
  useDocumentTitle('Правила совместимости');
  const navigate = useNavigate();
  
  const { filters, updateFilters, resetFilters } = useUrlFilters(['updatedAt,desc'], parseRuleFilters);
  const [searchValue, setSearchValue] = useState(filters.search);

  const [isFiltersOpen, setIsFiltersOpen] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);

  const { data, isLoading } = useRules(filters);
  const { deleteMutation } = useRuleMutations();

  const handleSort = (field: string, isShiftPressed: boolean) => {
    const newSort = toggleSort(filters.sort as string[], field, isShiftPressed);
    updateFilters({ sort: newSort, page: 0 });
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.titleBlock}>
          <h1 className={styles.title}>Правила совместимости</h1>
          <p className={styles.subtitle}>Управление логикой SpEL и системными ограничениями</p>
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
            <Button onClick={() => navigate('/compatibility/new')}>
              <Plus size={18} /> Добавить
            </Button>
          </div>
          <Button variant="secondary" onClick={() => setIsFiltersOpen(true)}>
              <Filter size={18} />
              <span>Фильтры</span>
          </Button>
        </div>
      </div>

      <TableCard isLoading={isLoading}>
        <Table>
          <TableHead>
            <TableRow>
              <TableHeader sortField="id" currentSort={filters.sort as string[]} onSort={handleSort}>ID</TableHeader>
              <TableHeader sortField="ruleCode" currentSort={filters.sort as string[]} onSort={handleSort}>Код правила</TableHeader>
              <TableHeader sortField="ruleName" currentSort={filters.sort as string[]} onSort={handleSort}>Название</TableHeader>
              <TableHeader sortField="isActive" currentSort={filters.sort as string[]} onSort={handleSort}>Статус</TableHeader>
              <TableHeader>Применяется к</TableHeader>
              <TableHeader sortField="updatedAt" currentSort={filters.sort as string[]} onSort={handleSort}>Обновлено</TableHeader>
              <TableHeader>Действия</TableHeader>
            </TableRow>
          </TableHead>
          <TableBody>
            {data?.items.map(row => (
              <TableRow key={row.id}>
                <TableCell className={styles.muted}>{row.id}</TableCell>
                <TableCell className={styles.codeCell}>
                  {row.ruleCode}
                </TableCell>
                <TableCell className={styles.bold}>{row.ruleName}</TableCell>
                <TableCell>
                  <Badge variant={row.isActive ? 'success' : 'danger'}>
                    {row.isActive ? 'Активно' : 'Отключено'}
                  </Badge>
                </TableCell>
                <TableCell>
                   <div className={styles.targetTypesWrapper}>
                    {row.targetComponentTypes.map(t => (
                      <Badge key={t} variant="default">
                        {COMPONENT_CATEGORY_CONFIG[t]?.label || t}
                      </Badge>
                    ))}
                  </div>
                </TableCell>
                <TableCell className={styles.muted}>{formatDateTime(row.updatedAt)}</TableCell>
                <TableCell>
                  <div className={styles.actions}>
                    <button className={styles.actionBtn} onClick={() => navigate(`/compatibility/${row.id}`)} title="Редактировать">
                      <Edit size={18} className={styles.iconEdit} />
                    </button>
                    <button className={styles.actionBtn} onClick={() => setDeleteId(row.id)} title="Удалить">
                      <Trash2 size={18} className={styles.iconDanger} />
                    </button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        
        {data && data.totalItems > 0 && (
          <div className={styles.paginationWrapper}>
            <Pagination
              currentPage={data.currentPage} totalPages={data.totalPages}
              totalItems={data.totalItems} pageSize={data.currentSize}
              onPageChange={(p) => updateFilters({ page: p }, false)}
              onPageSizeChange={(s) => updateFilters({ size: s, page: 0 }, false)}
              pageSizeOptions={[6, 12, 24, 48, 96]}
            />
          </div>
        )}
      </TableCard>

      <RuleFilters 
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
        title="Удалить правило?" 
        message="Это действие нельзя отменить. Будет отключена проверка совместимости."
        confirmLabel="Удалить"
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
};