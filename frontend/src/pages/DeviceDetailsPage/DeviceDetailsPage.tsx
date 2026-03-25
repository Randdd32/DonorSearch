import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeft, Cpu, HardDrive, MemoryStick, Monitor, 
  Settings, Box, Fan, Volume2, ShieldAlert, CheckCircle2, Search
} from 'lucide-react';
import { useDeviceDetails } from '../../features/devices/hooks/useDeviceDetails';
import { useRunSearch } from '../../features/search/hooks/useRunSearch';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { Card } from '../../components/ui/Card/Card';
import { Badge } from '../../components/ui/Badge/Badge';
import { Button } from '../../components/ui/Button/Button';
import type { ExternalComponentDto, ExternalComponentCategory } from '../../types/integration';
import styles from './DeviceDetailsPage.module.css';

const categoryConfig: Record<string, { label: string; icon: React.ElementType }> = {
  CPU: { label: 'Процессоры', icon: Cpu },
  MOTHERBOARD: { label: 'Материнские платы', icon: Settings },
  MEMORY: { label: 'Оперативная память', icon: MemoryStick },
  STORAGE: { label: 'Накопители', icon: HardDrive },
  VIDEO_CARD: { label: 'Видеокарты', icon: Monitor },
  POWER_SUPPLY: { label: 'Блоки питания', icon: Box },
  CASE: { label: 'Корпуса', icon: Box },
  CASE_FAN: { label: 'Вентиляторы', icon: Fan },
  OPTICAL_DRIVE: { label: 'Оптические приводы', icon: HardDrive },
  EXPANSION_CARD: { label: 'Карты расширения', icon: Volume2 },
  MONITOR: { label: 'Мониторы', icon: Monitor },
  UNKNOWN: { label: 'Неизвестное оборудование', icon: ShieldAlert },
};

export const DeviceDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const { data: device, isLoading, isError } = useDeviceDetails(Number(id));
  const { mutate: runSearch, isPending: isSearching } = useRunSearch();

  if (isLoading) return <Spinner fullPage size={40} />;
  if (isError || !device) return <div className={styles.error}>Ошибка загрузки данных устройства.</div>;

  const groupedComponents = device.components.reduce((acc, comp) => {
    if (!acc[comp.category]) acc[comp.category] = [];
    acc[comp.category].push(comp);
    return acc;
  }, {} as Record<ExternalComponentCategory, ExternalComponentDto[]>);

  const handleSearchDonor = (adapterId: number) => {
    runSearch(
      { deviceId: device.externalId, adapterId },
      {
        onSuccess: (data) => {
          navigate(`/search/results/${data.sessionId}`);
        }
      }
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
            <p className={styles.subtitle}>{device.typeName} • {device.manufacturerName}</p>
          </div>
          <Badge variant={device.isWorking ? 'success' : 'danger'}>
            {device.isWorking ? 'Работает' : 'Неисправно'}
          </Badge>
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
            <span className={styles.infoLabel}>Расположение</span>
            <span className={styles.infoValue}>{device.locationPath}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Пользователь</span>
            <span className={styles.infoValue}>{device.ownerFullName}</span>
          </div>
        </div>
      </Card>

      <h2 className={styles.sectionTitle}>Компонентный состав</h2>

      <div className={styles.componentsLayout}>
        {Object.entries(groupedComponents).map(([category, components]) => {
          const config = categoryConfig[category] || categoryConfig['UNKNOWN'];
          const Icon = config.icon;

          return (
            <div key={category} className={styles.categorySection}>
              <h3 className={styles.categoryTitle}>
                <Icon size={18} />
                {config.label}
              </h3>
              
              <div className={styles.componentList}>
                {components.map(comp => {
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
                          <Button 
                            variant="secondary" 
                            onClick={() => navigate('/mappings')}
                          >
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
                          Подобрать донора
                        </Button>
                      </div>
                    </Card>
                  );
                })}
              </div>
            </div>
          );
        })}
        {Object.keys(groupedComponents).length === 0 && (
          <div className={styles.emptyComponents}>В этом устройстве нет зарегистрированных деталей.</div>
        )}
      </div>
    </div>
  );
};