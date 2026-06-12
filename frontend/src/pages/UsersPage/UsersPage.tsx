import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Edit, Trash2, ShieldOff, Filter } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { clsx } from 'clsx';
import toast from 'react-hot-toast';
import { useAuthStore } from '../../store/authStore';
import { usersService } from '../../services/users.service';
import { useUrlFilters } from '../../hooks/useUrlFilters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { toggleSort } from '../../utils/tableUtils';
import { parseUserFilters } from './usersParser';
import { formatDateTime } from '../../utils/formatters';
import { ROLE_LABELS } from '../../types/auth';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Badge } from '../../components/ui/Badge/Badge';
import { Pagination } from '../../components/ui/Pagination/Pagination';
import { TableCard, Table, TableHead, TableRow, TableHeader, TableBody, TableCell } from '../../components/ui/Table/Table';
import { ConfirmModal } from '../../components/ui/ConfirmModal/ConfirmModal';
import { UserFilters } from '../../features/admin/components/UserFilters/UserFilters';
import styles from '../../styles/layouts/tablePageLayout.module.css';

export const UsersPage = () => {
  useDocumentTitle('Управление пользователями');
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const currentUser = useAuthStore((state) => state.user);
  
  const { filters, updateFilters, resetFilters } = useUrlFilters(['updatedAt,desc'], parseUserFilters);
  const [searchValue, setSearchValue] = useState(filters.search);
  const [isFiltersOpen, setIsFiltersOpen] = useState(false);

  const[deleteId, setDeleteId] = useState<number | null>(null);
  const [revokeId, setRevokeId] = useState<number | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['users', filters],
    queryFn: () => usersService.getUsers(filters),
    placeholderData: (prev) => prev
  });

  const { data: myProfile } = useQuery({
    queryKey: ['users', 'me'],
    queryFn: () => usersService.getMe()
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => usersService.delete(id),
    onSuccess: () => {
      toast.success('Пользователь успешно удален');
      queryClient.invalidateQueries({ queryKey: ['users'] });
    }
  });

  const revokeMutation = useMutation({
    mutationFn: (id: number) => usersService.revokeSessions(id),
    onSuccess: () => toast.success('Сессии пользователя успешно отозваны')
  });

  const handleSort = (field: string, isShiftPressed: boolean) => {
    const newSort = toggleSort(filters.sort as string[], field, isShiftPressed);
    updateFilters({ sort: newSort, page: 0 });
  };

  const displayItems = useMemo(() => {
    const items = data?.items || [];
    if (!myProfile) return items;

    const filtered = items.filter(u => u.username !== myProfile.username);

    return [myProfile, ...filtered];
  }, [data?.items, myProfile]);

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.titleBlock}>
          <h1 className={styles.title}>Пользователи</h1>
          <p className={styles.subtitle}>Управление учетными записями и правами доступа</p>
        </div>

        <div className={styles.controlsArea}>
          <div className={styles.toolbar}>
            <div className={styles.searchBar}>
              <Input 
                icon={<Search size={18} />} 
                placeholder="Поиск по логину..." 
                value={searchValue as string}
                onChange={(e) => setSearchValue(e.target.value)}
                onKeyDown={(e) => e.key === 'Enter' && updateFilters({ search: searchValue })}
                onClear={() => { setSearchValue(''); updateFilters({ search: '' }); }}
              />
            </div>
            <Button onClick={() => navigate('/users/new')}>
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
              <TableHeader sortField="username" currentSort={filters.sort as string[]} onSort={handleSort}>Логин</TableHeader>
              <TableHeader sortField="role" currentSort={filters.sort as string[]} onSort={handleSort}>Роль</TableHeader>
              <TableHeader sortField="createdAt" currentSort={filters.sort as string[]} onSort={handleSort}>Создан</TableHeader>
              <TableHeader sortField="updatedAt" currentSort={filters.sort as string[]} onSort={handleSort}>Обновлен</TableHeader>
              <TableHeader>Действия</TableHeader>
            </TableRow>
          </TableHead>
          <TableBody>
            {displayItems.map(row => {
              const isSelf = row.username === currentUser?.username;
              const canManage = isSelf || (currentUser?.role === 'SUPERADMIN' 
                ? row.role !== 'SUPERADMIN' 
                : row.role === 'USER');

              return (
                <TableRow 
                  key={`user-${row.id}`} 
                  className={clsx({[styles.selfRow]: isSelf })}
                >
                  <TableCell className={styles.muted}>{row.id}</TableCell>
                  <TableCell className={styles.bold}>
                    <div className={styles.nameWithBadge}>
                      {row.username}
                      {isSelf && <Badge variant="info">Вы</Badge>}
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant={row.role === 'SUPERADMIN' ? 'danger' : row.role === 'ADMIN' ? 'warning' : 'default'}>
                      {ROLE_LABELS[row.role]}
                    </Badge>
                  </TableCell>
                  <TableCell className={styles.muted}>{formatDateTime(row.createdAt)}</TableCell>
                  <TableCell className={styles.muted}>{formatDateTime(row.updatedAt)}</TableCell>
                  <TableCell>
                    <div className={styles.actions}>
                      {(canManage && !isSelf) && (
                        <button 
                          className={styles.actionBtn} 
                          onClick={() => setRevokeId(row.id)} 
                          title="Отозвать все сессии"
                        >
                          <ShieldOff size={18} className={styles.iconDanger} />
                        </button>
                      )}
                      
                      {canManage && (
                        <button className={styles.actionBtn} onClick={() => navigate(`/users/${row.id}`)} title="Редактировать">
                          <Edit size={18} className={styles.iconEdit} />
                        </button>
                      )}
                      
                      {canManage && !isSelf && (
                        <button className={styles.actionBtn} onClick={() => setDeleteId(row.id)} title="Удалить">
                          <Trash2 size={18} className={styles.iconDanger} />
                        </button>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              );
            })}
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

      <UserFilters 
        isOpen={isFiltersOpen} onClose={() => setIsFiltersOpen(false)} 
        filters={filters} updateFilters={updateFilters} resetFilters={resetFilters}
      />

      <ConfirmModal 
        isOpen={deleteId !== null} onClose={() => setDeleteId(null)}
        onConfirm={() => { if (deleteId) deleteMutation.mutate(deleteId); setDeleteId(null); }}
        title="Удалить пользователя?" 
        message="Пользователь потеряет доступ к системе, а все его сессии будут принудительно завершены."
        confirmLabel="Удалить" variant="danger" isLoading={deleteMutation.isPending}
      />
      <ConfirmModal 
        isOpen={revokeId !== null} onClose={() => setRevokeId(null)}
        onConfirm={() => { if (revokeId) revokeMutation.mutate(revokeId); setRevokeId(null); }}
        title="Отозвать сессии?" 
        message="Пользователь будет разлогинен на всех устройствах. Ему придется заново ввести логин и пароль."
        confirmLabel="Отозвать сессии" variant="danger" isLoading={revokeMutation.isPending}
      />
    </div>
  );
};