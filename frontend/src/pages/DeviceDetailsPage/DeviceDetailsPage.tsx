import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, Cpu, HardDrive, MemoryStick, Monitor, 
  Fan, ShieldAlert, CheckCircle2, Search, CircuitBoard, 
  Zap, Disc3, Blocks, Gpu, PcCase, Snowflake
} from 'lucide-react';
import { useDeviceDetails } from '../../features/devices/hooks/useDeviceDetails';
import { useRunSearch } from '../../features/search/hooks/useRunSearch';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { Card } from '../../components/ui/Card/Card';
import { Badge } from '../../components/ui/Badge/Badge';
import { Button } from '../../components/ui/Button/Button';
import { getStateConfig, formatDateTime } from '../../utils/formatters';
import type { ExternalComponentDto, ExternalComponentCategory } from '../../types/integration';
import styles from './DeviceDetailsPage.module.css';

const categoryConfig: Record<string, { label: string; icon: React.ElementType }> = {
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

const ALL_CATEGORIES: ExternalComponentCategory[] =[
  'CPU', 'MOTHERBOARD', 'MEMORY', 'VIDEO_CARD', 'STORAGE', 'POWER_SUPPLY',
  'CASE', 'CASE_FAN', 'CPU_COOLER', 'OPTICAL_DRIVE', 'EXPANSION_CARD', 'MONITOR'
];

export const DeviceDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const { data: device, isLoading, isError } = useDeviceDetails(Number(id));
  const { mutate: runSearch, isPending: isSearching } = useRunSearch();

  if (isLoading) return <Spinner fullPage size={40} />;
  if (isError || !device) {
    return (
      <ErrorState 
        title="Ошибка загрузки данных"
        message="Не удалось получить информацию об устройстве. Возможно, оно было удалено или сервер недоступен."
        onAction={() => navigate(-1)}
        actionLabel="Вернуться назад"
      />
    );
  }

  const stateConfig = getStateConfig(device.lifeCycleState);

  const groupedComponents = device.components.reduce((acc, comp) => {
    if (!acc[comp.category]) acc[comp.category] = [];
    acc[comp.category].push(comp);
    return acc;
  }, {} as Record<ExternalComponentCategory, ExternalComponentDto[]>);

  const handleSearchDonor = (adapterId?: number, category?: ExternalComponentCategory) => {
    runSearch(
      { deviceId: device.externalId, adapterId, category },
      { onSuccess: (data) => navigate(`/search/results/${data.sessionId}`) }
    );
  };

  return (
    <div className={styles.container}>
      <Button variant="ghost" onClick={() => navigate(-1)} className={styles.backButton}>
        <ArrowLeft size={18} />
        Назад к списку
      </Button>

      <Card className={styles.headerCard}>
        <div className={styles.headerTop}>
           <div>
            <h1 className={styles.title}>{device.name}</h1>
            <p className={styles.subtitle}>
              {device.typeName} • {device.manufacturerName} {device.modelName ? `(${device.modelName})` : ''}
            </p>
          </div>
          <div className={styles.badgesWrapper}>
            <Badge variant={stateConfig.variant}>{stateConfig.label}</Badge>
            <Badge variant={device.isWorking ? 'success' : 'danger'}>
              {device.isWorking ? 'Исправно' : 'Неисправно'}
            </Badge>
          </div>
        </div>
        
        <div className={styles.infoGrid}>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Инвентарный номер</span>
            <span className={styles.infoValue}>{device.inventoryNumber || 'Н/Д'}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Серийный номер</span>
            <span className={styles.infoValue}>{device.serialNumber || 'Н/Д'}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Пользователь</span>
            <span className={styles.infoValue}>{device.ownerFullName}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Отдел</span>
            <span className={styles.infoValue}>{device.departmentName || 'Н/Д'}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Расположение</span>
            <span className={styles.infoValue}>{device.locationPath}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Дата поступления</span>
            <span className={styles.infoValue}>{formatDateTime(device.dateReceived)}</span>
          </div>
        </div>
      </Card>

      <h2 className={styles.sectionTitle}>Компонентный состав</h2>

      <div className={styles.componentsLayout}>
        {ALL_CATEGORIES.map(category => {
          const components = groupedComponents[category] || [];
          const config = categoryConfig[category] || categoryConfig['UNKNOWN'];
          const Icon = config.icon;

          return (
            <div key={category} className={styles.categorySection}>
              <h3 className={styles.categoryTitle}>
                <Icon size={18} />
                {config.label}
              </h3>
              
              <div className={styles.componentList}>
                {components.length > 0 ? (
                  components.map(comp => {
                    const isMapped = comp.mappedComponentId !== null;
                    return (
                      <Card key={comp.adapterId} className={styles.componentCard}>
                        <div className={styles.compInfo}>
                          <div className={styles.compNameRow}>
                            <span className={styles.compName}>{comp.externalName || 'Без названия'}</span>
                            {isMapped ? (
                              <span title="Деталь распознана">
                                <CheckCircle2 size={16} className={styles.iconSuccess} />
                              </span>
                            ) : (
                              <span title="Деталь не распознана в базе">
                                <ShieldAlert size={16} className={styles.iconWarning} />
                              </span>
                            )}
                          </div>
                          <span className={styles.compSub}>
                            SN: {comp.serialNumber || 'Н/Д'} • {comp.manufacturerName || 'Неизвестный производитель'}
                          </span>
                        </div>

                        <div className={styles.compActions}>
                          {!isMapped && (
                            <Button variant="secondary" onClick={() => navigate('/mappings')}>
                              Сопоставить
                            </Button>
                          )}
                          <Button 
                            variant="primary" 
                            disabled={isSearching}
                            isLoading={isSearching}
                            onClick={() => handleSearchDonor(comp.adapterId)}
                          >
                            <Search size={16} />
                            Подобрать замену
                          </Button>
                        </div>
                      </Card>
                    );
                  })
                ) : (
                  <Card className={styles.missingComponentCard}>
                    <span className={styles.missingText}>Нет информации об оборудовании</span>
                    <Button 
                      variant="ghost" 
                      disabled={isSearching}
                      isLoading={isSearching}
                      onClick={() => handleSearchDonor(undefined, category)}
                    >
                      <Search size={16} />
                      Подобрать
                    </Button>
                  </Card>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};