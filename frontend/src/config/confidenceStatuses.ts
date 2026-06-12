import type { MappingConfidence } from '../types/integration';

export const confidenceConfig: Record<MappingConfidence, { label: string, variant: 'success' | 'info' | 'warning' | 'danger' }> = {
  CONFIRMED: { label: 'Подтверждено', variant: 'success' },
  AUTO: { label: 'Авто (>=90%)', variant: 'info' },
  NEEDS_REVIEW: { label: 'Требует проверки (60-89%)', variant: 'warning' },
  BAD_MATCH: { label: 'Низкая уверенность (<60%)', variant: 'danger' },
};