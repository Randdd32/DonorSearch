import type { ExternalDeviceState } from '../types/integration';

export const formatDateTime = (dateString?: string): string => {
  if (!dateString) return 'Н/Д';
  const date = new Date(dateString);
  return date.toLocaleString('ru-RU', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  }).replace(',', '');
};

export const getStateConfig = (state: ExternalDeviceState) => {
  switch (state) {
    case 'IN_USE': return { label: 'В использовании', variant: 'success' as const };
    case 'STORAGE': return { label: 'На хранении', variant: 'info' as const };
    case 'REPAIR': return { label: 'В ремонте', variant: 'warning' as const };
    case 'UNACCOUNTED': return { label: 'Неучтенное', variant: 'warning' as const };
    case 'WRITTEN_OFF': return { label: 'Списано', variant: 'danger' as const };
    default: return { label: 'Неизвестно', variant: 'default' as const };
  }
};
