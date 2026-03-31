import { Cpu, Snowflake, CircuitBoard, MemoryStick, HardDrive, Gpu, Zap, PcCase, Fan, Disc3, Blocks, Monitor, ShieldAlert } from 'lucide-react';
import type { ExternalComponentCategory } from '../types/integration';

export const COMPONENT_CATEGORY_CONFIG: Record<string, { label: string; icon: React.ElementType }> = {
  CPU: { label: 'Процессоры', icon: Cpu },
  CPU_COOLER: { label: 'Кулеры для процессоров', icon: Snowflake },
  MOTHERBOARD: { label: 'Материнские платы', icon: CircuitBoard },
  MEMORY: { label: 'Оперативная память', icon: MemoryStick },
  STORAGE: { label: 'Накопители', icon: HardDrive },
  VIDEO_CARD: { label: 'Видеокарты', icon: Gpu },
  POWER_SUPPLY: { label: 'Блоки питания', icon: Zap },
  CASE: { label: 'Корпуса', icon: PcCase },
  CASE_FAN: { label: 'Вентиляторы', icon: Fan },
  OPTICAL_DRIVE: { label: 'Оптические приводы', icon: Disc3 },
  EXPANSION_CARD: { label: 'Карты расширения', icon: Blocks },
  MONITOR: { label: 'Мониторы', icon: Monitor },
  UNKNOWN: { label: 'Неизвестное оборудование', icon: ShieldAlert }
};

export const COMPONENT_TYPE_OPTIONS: { value: ExternalComponentCategory; label: string }[] = 
  Object.entries(COMPONENT_CATEGORY_CONFIG)
    .filter(([key]) => key !== 'UNKNOWN')
    .map(([key, config]) => ({
      value: key as ExternalComponentCategory,
      label: config.label
    }));