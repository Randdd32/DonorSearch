import { Link } from 'react-router-dom';
import { Server, Monitor, MapPin, User, Hash } from 'lucide-react';
import type { ExternalDeviceDto, ExternalDeviceState } from '../../../../types/integration';
import { Card } from '../../../../components/ui/Card/Card';
import { Badge } from '../../../../components/ui/Badge/Badge';
import styles from './DeviceCard.module.css';

interface DeviceCardProps {
  device: ExternalDeviceDto;
}

const getStateConfig = (state: ExternalDeviceState) => {
  switch (state) {
    case 'IN_USE': return { label: 'Используется', variant: 'success' as const };
    case 'STORAGE': return { label: 'На хранении', variant: 'info' as const };
    case 'REPAIR': return { label: 'В ремонте', variant: 'warning' as const };
    case 'UNACCOUNTED': return { label: 'Неучтенное', variant: 'warning' as const };
    case 'WRITTEN_OFF': return { label: 'Списано', variant: 'danger' as const };
    default: return { label: 'Неизвестно', variant: 'default' as const };
  }
};

export const DeviceCard = ({ device }: DeviceCardProps) => {
  const isServer = device.typeName?.toLowerCase().includes('сервер');
  const Icon = isServer ? Server : Monitor;
  const stateConfig = getStateConfig(device.lifeCycleState);

  return (
    <Link to={`/devices/${device.externalId}`} className={styles.linkWrapper}>
      <Card isHoverable className={styles.cardContent}>
        <div className={styles.header}>
          <div className={styles.iconWrapper}>
            <Icon size={24} />
          </div>
          <div className={styles.titleInfo}>
            <h3 className={styles.name}>{device.name || 'Без названия'}</h3>
            <span className={styles.type}>{device.typeName || 'Тип не указан'}</span>
          </div>
          <Badge variant={stateConfig.variant} className={styles.badge}>
            {stateConfig.label}
          </Badge>
        </div>

        <div className={styles.details}>
          <div className={styles.detailRow}>
            <Hash size={16} />
            <span>Инв. №: {device.inventoryNumber || 'Н/Д'}</span>
          </div>
          <div className={styles.detailRow}>
            <User size={16} />
            <span className={styles.truncate}>{device.ownerFullName}</span>
          </div>
          <div className={styles.detailRow}>
            <MapPin size={16} />
            <span className={styles.truncate} title={device.locationPath}>
              {device.locationPath}
            </span>
          </div>
        </div>
      </Card>
    </Link>
  );
};