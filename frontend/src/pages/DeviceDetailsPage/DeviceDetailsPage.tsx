import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, ShieldAlert, CheckCircle2, Search } from 'lucide-react';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { useDeviceDetails } from '../../features/devices/hooks/useDeviceDetails';
import { useRunSearch } from '../../features/search/hooks/useRunSearch';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { Card } from '../../components/ui/Card/Card';
import { Badge } from '../../components/ui/Badge/Badge';
import { Button } from '../../components/ui/Button/Button';
import { getStateConfig, formatDateTime } from '../../utils/formatters';
import type { ExternalComponentDto, ExternalComponentCategory } from '../../types/integration';
import { COMPONENT_CATEGORY_CONFIG } from '../../config/componentTypes';
import styles from './DeviceDetailsPage.module.css';

const ALL_CATEGORIES: ExternalComponentCategory[] =[
  'CPU', 'MOTHERBOARD', 'MEMORY', 'VIDEO_CARD', 'STORAGE', 'POWER_SUPPLY',
  'CASE', 'CASE_FAN', 'CPU_COOLER', 'OPTICAL_DRIVE', 'EXPANSION_CARD', 'MONITOR'
];

export const DeviceDetailsPage = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const { data: device, isLoading, isError } = useDeviceDetails(Number(id));
  
  useDocumentTitle(device ? device.name : 'Детали устройства'); 

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

            {device.isWorking === true && <Badge variant="success">Исправно</Badge>}
            {device.isWorking === false && <Badge variant="danger">Неисправно</Badge>}
            {device.isWorking === null && <Badge variant="default">Работоспособность неизвестна</Badge>}
          </div>
        </div>
        
        {device.organizationName && device.organizationName.toLowerCase() === 'не используется' && (
          <div className={styles.orgWarning}>
            <Badge variant="warning">Внимание: согласно данным об организации устройство не используется</Badge>
          </div>
        )}

        <div className={styles.infoGrid}>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Инвентарный номер</span>
            <span className={styles.infoValue}>{device.inventoryNumber || 'Н/Д'}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Серийный номер</span>
            <span className={styles.infoValue}>{device.serialNumber || 'Н/Д'}</span>
          </div>
          {device.modelProductNumber && (
             <div className={styles.infoItem}>
               <span className={styles.infoLabel}>Номер продукта (Product Number)</span>
               <span className={styles.infoValue}>{device.modelProductNumber}</span>
             </div>
          )}
          {device.assetTag && (
             <div className={styles.infoItem}>
               <span className={styles.infoLabel}>Тег имущества (Asset Tag)</span>
               <span className={styles.infoValue}>{device.assetTag}</span>
             </div>
          )}
          {device.code && (
             <div className={styles.infoItem}>
               <span className={styles.infoLabel}>Код</span>
               <span className={styles.infoValue}>{device.code}</span>
             </div>
          )}
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Стоимость</span>
            <span className={styles.infoValue}>
              {device.cost !== null 
                ? new Intl.NumberFormat('ru-RU', { style: 'currency', currency: 'RUB' }).format(device.cost) 
                : 'Не указана'}
            </span>
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
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Дата последнего опроса</span>
            <span className={styles.infoValue}>{formatDateTime(device.dateInquiry)}</span>
          </div>
          <div className={styles.infoItem}>
            <span className={styles.infoLabel}>Дата назначения</span>
            <span className={styles.infoValue}>{formatDateTime(device.appointmentDate)}</span>
          </div>
          {device.dateAnnuled && (
             <div className={styles.infoItem}>
               <span className={styles.infoLabel}>Дата аннулирования</span>
               <span className={styles.infoValue}>{formatDateTime(device.dateAnnuled)}</span>
             </div>
          )}
        </div>
      </Card>

      {(device.note || device.description || device.modelNote || device.pcComposition || device.ownershipNote) && (
        <Card className={styles.notesCard}>
          <h3 className={styles.cardTitle}>Дополнительная информация</h3>
          {device.description && (
            <div className={styles.noteBlock}>
              <span className={styles.noteLabel}>Описание:</span>
              <p className={styles.noteText}>{device.description}</p>
            </div>
          )}
          {device.note && (
            <div className={styles.noteBlock}>
              <span className={styles.noteLabel}>Примечание к устройству:</span>
              <p className={styles.noteText}>{device.note}</p>
            </div>
          )}
          {device.modelNote && (
            <div className={styles.noteBlock}>
              <span className={styles.noteLabel}>Примечание к модели:</span>
              <p className={styles.noteText}>{device.modelNote}</p>
            </div>
          )}
          {device.pcComposition && (
            <div className={styles.noteBlock}>
              <span className={styles.noteLabel}>Состав ПК (и периферия):</span>
              <p className={styles.noteText}>{device.pcComposition}</p>
            </div>
          )}
          {device.ownershipNote && (
            <div className={styles.noteBlock}>
              <span className={styles.noteLabel}>Движение и собственность:</span>
              <p className={styles.noteText}>{device.ownershipNote}</p>
            </div>
          )}
        </Card>
      )}

      <h2 className={styles.sectionTitle}>Компонентный состав</h2>

      <div className={styles.componentsLayout}>
        {ALL_CATEGORIES.map(category => {
          const components = groupedComponents[category] || [];
          const config = COMPONENT_CATEGORY_CONFIG[category] || COMPONENT_CATEGORY_CONFIG['UNKNOWN'];
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
                            <Button 
                              variant="secondary" 
                              onClick={() => {
                                const params = new URLSearchParams({
                                  externalName: comp.externalName,
                                  type: category
                                });
                                navigate(`/mappings/new?${params.toString()}`);
                              }}
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