import type { ExternalComponentCategory } from '../types/integration';

export const getComponentEndpoint = (type: ExternalComponentCategory | string): string => {
  const map: Record<string, string> = {
    CPU: '/components/cpus',
    CPU_COOLER: '/components/cpu-coolers',
    MOTHERBOARD: '/components/motherboards',
    MEMORY: '/components/ram',
    VIDEO_CARD: '/components/video-cards',
    STORAGE: '/components/storages',
    POWER_SUPPLY: '/components/power-supplies',
    CASE: '/components/cases',
    CASE_FAN: '/components/case-fans',
    OPTICAL_DRIVE: '/components/optical-drives',
    EXPANSION_CARD: '/components/expansion-cards',
    MONITOR: '/components/monitors'
  };
  return map[type] || '';
};