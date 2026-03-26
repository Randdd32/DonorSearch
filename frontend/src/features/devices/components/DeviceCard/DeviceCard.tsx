import { Link } from 'react-router-dom';
import { Server, Monitor, Laptop, Box, MapPin, User, Hash, Building } from 'lucide-react';
import type { ExternalDeviceDto } from '../../../../types/integration';
import { Card } from '../../../../components/ui/Card/Card';
import { Badge } from '../../../../components/ui/Badge/Badge';
import { getStateConfig } from '../../../../utils/formatters';
import styles from './DeviceCard.module.css';

interface DeviceCardProps {
  device: ExternalDeviceDto;
}

const renderDeviceIcon = (typeName: string) => {
  const lower = typeName?.toLowerCase() || '';
  if (lower.includes('ноутбук')) return <Laptop size={24} />;
  if (lower.includes('монитор')) return <Monitor size={24} />;
  if (lower.includes('сервер') || lower.includes('системный блок') || lower.includes('пк')) return <Server size={24} />;
  return <Box size={24} />;
};

export const DeviceCard = ({ device }: DeviceCardProps) => {
  const stateConfig = getStateConfig(device.lifeCycleState);

  return (
    <Link to={`/devices/${device.externalId}`} className={styles.linkWrapper}>
      <Card isHoverable className={styles.cardContent}>
        <div className={styles.header}>
          <div className={styles.iconWrapper}>
            {renderDeviceIcon(device.typeName)}
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
            <Building size={16} />
            <span className={styles.truncate}>{device.departmentName || 'Отдел не указан'}</span>
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