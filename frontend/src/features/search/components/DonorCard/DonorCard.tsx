import { AlertTriangle, Info, CheckCircle, Hash, User, Building, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';
import type { DonorResultDto, WarningSeverity } from '../../../../types/search';
import { getStateConfig } from '../../../../utils/formatters';
import { Card } from '../../../../components/ui/Card/Card';
import { Badge } from '../../../../components/ui/Badge/Badge';
import { DeviceIcon } from '../../../devices/components/DeviceIcon/DeviceIcon';
import styles from './DonorCard.module.css';

interface DonorCardProps {
  result: DonorResultDto;
}

const severityConfig: Record<WarningSeverity, { color: string; label: string }> = {
  CRITICAL: { color: 'var(--status-critical)', label: 'Критично' },
  HIGH: { color: 'var(--status-high)', label: 'Высокий риск' },
  MEDIUM: { color: 'var(--status-medium)', label: 'Средний риск' },
  LOW: { color: 'var(--status-low)', label: 'Низкий риск' },
  INFO: { color: 'var(--status-info)', label: 'Информация' },
};

export const DonorCard = ({ result }: DonorCardProps) => {
  const device = result.donorDevice;
  const stateConfig = getStateConfig(device.lifeCycleState);

  return (
    <Card className={styles.card}>
      <div className={styles.header}>
        <div className={styles.deviceInfo}>
          <div className={styles.iconWrapper}>
            <DeviceIcon typeName={device.typeName} size={24} />
          </div>
          <div className={styles.deviceMeta}>
            <div className={styles.deviceHeaderRow}>
              <Link to={`/devices/${device.externalId}`} className={styles.deviceName} target="_blank">
                {device.name || 'Без названия'}
              </Link>
              <Badge variant={stateConfig.variant}>{stateConfig.label}</Badge>
            </div>
            
            <div className={styles.deviceSubGrid}>
              <div className={styles.subItem}><Hash size={14} /> {device.inventoryNumber || 'Н/Д'}</div>
              <div className={styles.subItem}><User size={14} /> {device.ownerFullName || 'Неизвестно'}</div>
              <div className={styles.subItem}><Building size={14} /> {device.departmentName || 'Отдел не указан'}</div>
              <div className={styles.subItem}><MapPin size={14} /> {device.locationPath}</div>
            </div>
          </div>
        </div>

        <div className={styles.scoreSection}>
          <div className={styles.scoreLabel}>Штраф совместимости</div>
          <Badge variant={result.totalPenalty === 0 ? 'success' : result.totalPenalty > 20 ? 'danger' : 'warning'} className={styles.scoreBadge}>
            {result.totalPenalty === 0 ? 'Идеально (0)' : `${result.totalPenalty} очков`}
          </Badge>
        </div>
      </div>

      <div className={styles.componentsSection}>
        <h4 className={styles.sectionTitle}>Найденные совместимые детали:</h4>
        
        <div className={styles.componentsList}>
          {result.compatibleComponents.map((comp, idx) => (
            <div key={idx} className={styles.componentItem}>
              <div className={styles.compHeader}>
                <span className={styles.compName}>{comp.externalInfo.externalName}</span>
                {comp.componentWarnings.length === 0 ? (
                  <span className={styles.perfectMatch}>
                    <CheckCircle size={14} /> Подходит идеально
                  </span>
                ) : (
                  <span className={styles.hasWarnings}>
                    <AlertTriangle size={14} /> Найдено предупреждений: {comp.componentWarnings.length}
                  </span>
                )}
              </div>

              {comp.componentWarnings.length > 0 && (
                <div className={styles.warningsList}>
                  {comp.componentWarnings.map((warning, wIdx) => {
                    const sevConfig = severityConfig[warning.severity];
                    return (
                      <div 
                        key={wIdx} 
                        className={styles.warningItem} 
                        style={{ borderLeftColor: sevConfig.color, backgroundColor: `color-mix(in srgb, ${sevConfig.color} 10%, transparent)` }}
                      >
                        <Info size={16} color={sevConfig.color} className={styles.warningIcon} />
                        <div>
                          <div className={styles.warningSeverity} style={{ color: sevConfig.color }}>
                            {sevConfig.label}
                          </div>
                          <div className={styles.warningMessage}>{warning.message}</div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </Card>
  );
};