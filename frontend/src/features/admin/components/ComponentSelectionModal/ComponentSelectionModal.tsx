import type { ReactNode } from 'react'; 
import type { FilterDef, ColumnDef } from './ComponentTableConfig';
import { useState } from 'react';
import { X, Search, CheckCircle, Filter } from 'lucide-react';
import { Input } from '../../../../components/ui/Input/Input';
import { Button } from '../../../../components/ui/Button/Button';
import { Pagination } from '../../../../components/ui/Pagination/Pagination';
import { TableCard, Table, TableHead, TableRow, TableHeader, TableBody, TableCell } from '../../../../components/ui/Table/Table';
import { COMPONENT_REGISTRY } from './ComponentTableConfig';
import { useComponentSelection } from '../../hooks/useComponentSelection';
import { DynamicFilterField } from './DynamicFilterField';
import type { ExternalComponentCategory } from '../../../../types/integration';
import styles from './ComponentSelectionModal.module.css';

interface ComponentSelectionModalProps {
  isOpen: boolean;
  onClose: () => void;
  componentType: string;
  selectedId: number | null;
  selectedName: string | null;
  onSelect: (id: number, name: string) => void;
}

export const ComponentSelectionModal = ({
  isOpen, onClose, componentType, selectedId, selectedName, onSelect
}: ComponentSelectionModalProps) => {
  const [showFilters, setShowFilters] = useState(false);
  const config = COMPONENT_REGISTRY[componentType as ExternalComponentCategory] || COMPONENT_REGISTRY.DEFAULT;
  
  const { state, data, isLoading, handlers } = useComponentSelection(componentType, isOpen);

  if (!isOpen) return null;

  return (
    <>
      <div className={styles.backdrop} onClick={onClose} />
      <div className={styles.modal}>
        <div className={styles.header}>
          <div>
            <h3 className={styles.title}>Выбор компонента БД</h3>
            <p className={styles.subtitle}>Категория: {componentType}</p>
          </div>
          <button className={styles.closeBtn} onClick={onClose}><X size={20} /></button>
        </div>

        <div className={styles.toolbar}>
          <div className={styles.searchBar}>
            <Input 
              icon={<Search size={18} />} placeholder="Поиск по названию..." 
              value={state.search} onChange={(e) => handlers.setSearch(e.target.value)} onClear={() => handlers.setSearch('')}
            />
          </div>
          <Button variant={showFilters ? 'primary' : 'secondary'} onClick={() => setShowFilters(!showFilters)}>
            <Filter size={18} /> Фильтры
          </Button>
        </div>

        {showFilters && config.filters.length > 0 && (
          <div className={styles.filtersPanel}>
            {config.filters.map((f: FilterDef) => {
              const val = f.type === 'range' 
                ? { min: state.filters[f.rangeMinKey!], max: state.filters[f.rangeMaxKey!] }
                : state.filters[f.key];
              return <DynamicFilterField key={f.key} filterDef={f} value={val} onChange={handlers.updateFilter} />;
            })}
            <div className={styles.filtersActions}>
              <Button variant="ghost" onClick={handlers.resetFilters}>Сбросить фильтры</Button>
            </div>
          </div>
        )}

        <div className={styles.content}>
          {selectedId && (
            <div className={styles.selectedBanner}>
              <CheckCircle size={18} className={styles.successIcon} />
              <div>
                <span className={styles.selectedLabel}>Текущий выбор:</span>
                <span className={styles.selectedName}>{selectedName}</span>
                <span className={styles.selectedId}>(ID: {selectedId})</span>
              </div>
            </div>
          )}

          <TableCard isLoading={isLoading}>
            <Table>
              <TableHead>
                <TableRow>
                  {config.columns.map((col: ColumnDef) => (
                    <TableHeader key={col.key} sortField={col.sortable ? col.key : undefined} currentSort={state.sort} onSort={handlers.handleSort}>
                      {col.label}
                    </TableHeader>
                  ))}
                  <TableHeader>Действие</TableHeader>
                </TableRow>
              </TableHead>
              <TableBody>
                {data?.items.map(row => {
                  const isSelected = (row.id as number) === selectedId;
                  return (
                    <TableRow key={row.id as number}>
                      {config.columns.map(col => (
                        <TableCell key={col.key} className={col.key === 'id' ? styles.muted : ''}>
                          {col.render ? col.render(row) : (row[col.key] as ReactNode)}
                        </TableCell>
                      ))}
                      <TableCell>
                        <Button variant={isSelected ? 'secondary' : 'primary'} onClick={() => { onSelect(row.id as number, row.name as string); onClose(); }} disabled={isSelected}>
                          {isSelected ? 'Выбрано' : 'Выбрать'}
                        </Button>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
            
            {data && data.totalItems > 0 && (
              <div className={styles.pagination}>
                <Pagination currentPage={data.currentPage} totalPages={data.totalPages} totalItems={data.totalItems} pageSize={data.currentSize} onPageChange={handlers.setPage} onPageSizeChange={(s) => { handlers.setSize(s); handlers.setPage(0); }} pageSizeOptions={[10, 20, 50]} placement="top" />
              </div>
            )}
          </TableCard>
        </div>
      </div>
    </>
  );
};