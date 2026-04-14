import { useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowLeft, Save, Search, CheckCircle } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';
import { mappingsService } from '../../services/mappings.service';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Select } from '../../components/ui/Select/Select';
import { Card } from '../../components/ui/Card/Card';
import { Badge } from '../../components/ui/Badge/Badge';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { ComponentSelectionModal } from '../../features/admin/components/ComponentSelectionModal/ComponentSelectionModal';
import { formatDateTime } from '../../utils/formatters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { COMPONENT_TYPE_OPTIONS } from '../../config/componentTypes';
import type { ExternalComponentCategory, IntegrationMappingDto, MappingConfidence } from '../../types/integration';
import styles from '../../styles/layouts/editPageLayout.module.css';

export const MappingEditPage = () => {
  const { id } = useParams<{ id: string }>();
  const isNew = id === 'new';
  
  useDocumentTitle(isNew ? 'Новое сопоставление' : 'Редактирование маппинга');

  const { data: originalData, isLoading, isError } = useQuery({
    queryKey: ['mapping', id],
    queryFn: () => mappingsService.getById(Number(id)),
    enabled: !isNew,
  });

  if (isLoading) return <Spinner fullPage size={40} />;
  if (isError) return <ErrorState title="Ошибка загрузки" message="Не удалось получить данные маппинга." />;

  return (
    <MappingForm 
      key={isNew ? 'new' : originalData?.id} 
      isNew={isNew} 
      id={id!} 
      originalData={originalData} 
    />
  );
};

interface MappingFormProps {
  isNew: boolean;
  id: string;
  originalData?: IntegrationMappingDto;
}

const MappingForm = ({ isNew, id, originalData }: MappingFormProps) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [searchParams] = useSearchParams();

  const[externalName, setExternalName] = useState(
    originalData?.externalName || (isNew ? searchParams.get('externalName') || '' : '')
  );
  const [componentType, setComponentType] = useState<ExternalComponentCategory | ''>(
    originalData?.internalComponentType || (isNew ? (searchParams.get('type') as ExternalComponentCategory) || '' : '')
  );
  const [componentId, setComponentId] = useState<number | null>(originalData?.internalComponentId || null);
  const [componentName, setComponentName] = useState<string>(originalData?.internalComponentName || '');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const confidence = originalData?.confidence || 'CONFIRMED' as MappingConfidence;
  const searchName = originalData?.internalComponentSearchName;

  const saveMutation = useMutation({
    mutationFn: async () => {
      const trimmedName = externalName.trim();
      if (!trimmedName) throw new Error('Внешнее название обязательно');
      if (!componentId) throw new Error('Необходимо выбрать деталь из базы');

      if (isNew) {
        return mappingsService.create({
          externalName: trimmedName,
          internalComponentId: componentId,
          confidence: 'CONFIRMED',
        });
      } else {
        return mappingsService.update(Number(id), {
          ...originalData!,
          externalName: trimmedName,
          internalComponentId: componentId,
          confidence: 'CONFIRMED',
        });
      }
    },
    onSuccess: () => {
      toast.success(isNew ? 'Сопоставление создано' : 'Сопоставление обновлено');
      queryClient.invalidateQueries({ queryKey: ['mappings'] });
      queryClient.invalidateQueries({ queryKey: ['mapping', id] }); 
      navigate('/mappings');
    },
    onError: (e: Error | AxiosError<{ message: string }>) => {
      if (!(e instanceof AxiosError)) {
        toast.error(e.message || 'Ошибка сохранения');
      }
    }
  });

  const handleTypeChange = (val: string | number | boolean | null) => {
    const newType = (val ? String(val) : '') as ExternalComponentCategory | '';
    setComponentType(newType);
    
    if (newType !== originalData?.internalComponentType) {
      setComponentId(null);
      setComponentName('');
    } else {
      setComponentId(originalData.internalComponentId);
      setComponentName(originalData.internalComponentName);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <Button variant="ghost" onClick={() => navigate('/mappings')} className={styles.backBtn}>
          <ArrowLeft size={16} /> Назад
        </Button>
        <div>
          <h1 className={styles.title}>{isNew ? 'Новая связь оборудования' : 'Редактирование маппинга'}</h1>
          <p className={styles.subtitle}>Свяжите строку из ИнфраМенеджера с нашей эталонной базой</p>
        </div>
      </div>

      <div className={styles.content}>
        <Card className={styles.formCard}>
          <h3 className={styles.cardTitle}>Настройки маппинга</h3>
          
          <div className={styles.field}>
            <label className={styles.label}>Внешнее название (Из системы учета) <span className={styles.req}>*</span></label>
            <Input 
              value={externalName}
              onChange={(e) => isNew && setExternalName(e.target.value)}
              disabled={!isNew}
              placeholder="Например: Kingston DDR4 16GB"
            />
            {!isNew && <p className={styles.hint}>Внешнее название нельзя изменить после создания.</p>}
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Категория комплектующего <span className={styles.req}>*</span></label>
            <Select 
              value={componentType}
              onChange={handleTypeChange}
              options={COMPONENT_TYPE_OPTIONS}
              placeholder="Выберите тип..."
              isSearchable={true}
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Компонент из базы данных <span className={styles.req}>*</span></label>
            <div className={styles.componentSelector}>
              <div className={styles.selectedComponentBox}>
                {componentId ? (
                  <div className={styles.compSelected}>
                    <CheckCircle size={18} className={styles.successIcon} />
                    <div className={styles.compText}>
                      <span className={styles.cName}>{componentName}</span>
                      <span className={styles.cId}>ID: {componentId}</span>
                    </div>
                  </div>
                ) : (
                  <span className={styles.compEmpty}>Деталь не выбрана</span>
                )}
              </div>
              <Button onClick={() => setIsModalOpen(true)} disabled={!componentType} className={styles.selectBtn}>
                <Search size={16} /> Выбрать
              </Button>
            </div>
            {!componentType && <p className={styles.hint}>Сначала выберите категорию.</p>}
          </div>

          <div className={styles.actions}>
            <Button variant="secondary" onClick={() => navigate('/mappings')}>Отмена</Button>
            <Button onClick={() => saveMutation.mutate()} isLoading={saveMutation.isPending} disabled={!externalName || !componentId}>
              <Save size={16} /> {isNew ? 'Создать связь' : 'Подтвердить и сохранить'}
            </Button>
          </div>
        </Card>

        {!isNew && originalData && (
          <div className={styles.metaColumn}>
            <Card className={styles.metaCard}>
              <h3 className={styles.cardTitle}>Системная информация</h3>
              
              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>ID записи</span>
                <span className={styles.metaValue}>{originalData.id}</span>
              </div>

              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>Текущий статус</span>
                <div>
                  <Badge variant={confidence === 'CONFIRMED' ? 'success' : confidence === 'AUTO' ? 'info' : 'warning'}>
                    {confidence}
                  </Badge>
                </div>
              </div>

              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>Поисковое имя БД</span>
                <span className={styles.metaValue} style={{ fontSize: '13px', fontFamily: 'monospace' }}>
                  {searchName || '—'}
                </span>
              </div>

              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>Создано</span>
                <span className={styles.metaValue}>{formatDateTime(originalData.createdAt)}</span>
              </div>

              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>Обновлено</span>
                <span className={styles.metaValue}>{formatDateTime(originalData.updatedAt)}</span>
              </div>
            </Card>
          </div>
        )}
      </div>

      {isModalOpen && componentType && (
        <ComponentSelectionModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          componentType={componentType}
          selectedId={componentId}
          selectedName={componentName}
          onSelect={(selectId, selectName) => {
            setComponentId(selectId);
            setComponentName(selectName);
          }}
        />
      )}
    </div>
  );
};