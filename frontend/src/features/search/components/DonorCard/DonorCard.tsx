import { AlertTriangle, Info, CheckCircle, Hash, User, Building, MapPin } from 'lucide-react';
import { Link } from 'react-router-dom';
import { clsx } from 'clsx'; // Импортируем clsx
import type { DonorResultDto, WarningSeverity } from '../../../../types/search';
import { getStateConfig, formatPluralPoints } from '../../../../utils/formatters';
import { Card } from '../../../../components/ui/Card/Card';
import { Badge } from '../../../../components/ui/Badge/Badge';
import { DeviceIcon } from '../../../devices/components/DeviceIcon/DeviceIcon';
import styles from './DonorCard.module.css';

interface DonorCardProps {
  result: DonorResultDto;
}

const severityConfig: Record<WarningSeverity, { boxClass: string; textClass: string; label: string }> = {
  CRITICAL: { boxClass: styles.warningCritical, textClass: styles.textCritical, label: 'Критично' },
  HIGH: { boxClass: styles.warningHigh, textClass: styles.textHigh, label: 'Высокий риск' },
  MEDIUM: { boxClass: styles.warningMedium, textClass: styles.textMedium, label: 'Средний риск' },
  LOW: { boxClass: styles.warningLow, textClass: styles.textLow, label: 'Низкий риск' },
  INFO: { boxClass: styles.warningInfo, textClass: styles.textInfo, label: 'Информация' },
};

export const DonorCard = ({ result }: DonorCardProps) => {
  const device = result.donorDevice;
  const stateConfig = getStateConfig(device.lifeCycleState);

  return (
    <Card className={styles.card}>
      <div className={styles.header}>
        <div className={styles.deviceInfo}>
          <div className={styles.iconWrapper}>
            <DeviceIcon typeName={device.typeName} size={24} className={styles.deviceIcon} />
          </div>
          <div className={styles.deviceMeta}>
            <div className={styles.deviceHeaderRow}>
              <Link to={`/devices/${device.externalId}`} className={styles.deviceName} target="_blank">
                {device.name || 'Без названия'}
              </Link>
              <Badge variant={stateConfig.variant}>{stateConfig.label}</Badge>
            </div>
            
            <div className={styles.deviceSubGrid}>
              <div className={styles.subItem}>
                <Hash size={14} className={styles.iconShrink} /> 
                <span className={styles.truncate}>{device.inventoryNumber || 'Н/Д'}</span>
              </div>
              <div className={styles.subItem} title={device.ownerFullName}>
                <User size={14} className={styles.iconShrink} /> 
                <span className={styles.truncate}>{device.ownerFullName || 'Неизвестно'}</span>
              </div>
              <div className={styles.subItem} title={device.departmentName}>
                <Building size={14} className={styles.iconShrink} /> 
                <span className={styles.truncate}>{device.departmentName || 'Отдел не указан'}</span>
              </div>
              <div className={styles.subItem} title={device.locationPath}>
                <MapPin size={14} className={styles.iconShrink} /> 
                <span className={styles.truncate}>{device.locationPath}</span>
              </div>
            </div>
          </div>
        </div>

        <div className={styles.scoreSection}>
          <div className={styles.scoreLabel}>Итоговый штраф</div>
          <Badge 
            variant={result.totalPenalty === 0 ? 'success' : result.totalPenalty > 20 ? 'danger' : 'warning'} 
            className={styles.scoreBadge}>
            {result.totalPenalty === 0 ? 'Идеально (0)' : formatPluralPoints(result.totalPenalty)}
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
                        className={clsx(styles.warningItem, sevConfig.boxClass)} 
                      >
                        <Info size={16} className={clsx(styles.warningIcon, sevConfig.textClass)} />
                        <div>
                          <div className={clsx(styles.warningSeverity, sevConfig.textClass)}>
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