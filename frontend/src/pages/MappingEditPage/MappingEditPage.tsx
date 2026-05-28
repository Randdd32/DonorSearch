import { useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import { ArrowLeft, Save, Search, CheckCircle } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';
import { useForm, Controller, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { clsx } from 'clsx';
import { mappingsService } from '../../services/mappings.service';
import { formatDateTime } from '../../utils/formatters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { COMPONENT_TYPE_OPTIONS } from '../../config/componentTypes';
import { confidenceConfig } from '../../config/confidenceStatuses';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Select } from '../../components/ui/Select/Select';
import { Card } from '../../components/ui/Card/Card';
import { Badge } from '../../components/ui/Badge/Badge';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { ComponentSelectionModal } from '../../features/admin/components/ComponentSelectionModal/ComponentSelectionModal';
import type { IntegrationMappingDto, MappingConfidence } from '../../types/integration';
import { mappingSchema, type MappingFormValues } from './mappingSchema';
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

  const [isModalOpen, setIsModalOpen] = useState(false);

  const {
    register,
    handleSubmit,
    control,
    setValue,
    formState: { errors }
  } = useForm<MappingFormValues>({
    resolver: zodResolver(mappingSchema),
    defaultValues: {
      externalName: originalData?.externalName || (isNew ? searchParams.get('externalName') || '' : ''),
      componentType: originalData?.internalComponentType || (isNew ? searchParams.get('type') || '' : ''),
      componentId: originalData?.internalComponentId || undefined,
      componentName: originalData?.internalComponentName || '',
    },
    mode: 'onTouched'
  });

  const externalName = useWatch({ control, name: 'externalName' });
  const componentType = useWatch({ control, name: 'componentType' });
  const componentId = useWatch({ control, name: 'componentId' });
  const componentName = useWatch({ control, name: 'componentName' });

  const confidence = originalData?.confidence || 'CONFIRMED' as MappingConfidence;
  const searchName = originalData?.internalComponentSearchName;

  const saveMutation = useMutation({
    mutationFn: async (data: MappingFormValues) => {
      const trimmedName = data.externalName.trim();
      const safeComponentId = data.componentId!;

      if (isNew) {
        return mappingsService.create({
          externalName: trimmedName,
          internalComponentId: safeComponentId,
          confidence: 'CONFIRMED',
        });
      } else {
        return mappingsService.update(Number(id), {
          ...originalData!,
          externalName: trimmedName,
          internalComponentId: safeComponentId,
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

  const onSubmit = (data: MappingFormValues) => {
    saveMutation.mutate(data);
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
          
          <form id="mapping-form" onSubmit={handleSubmit(onSubmit)} className={styles.formContents}>
            <div className={styles.field}>
              <label className={styles.label}>Внешнее название (из системы учета) <span className={styles.req}>*</span></label>
              <Input 
                {...register('externalName')}
                disabled={!isNew}
                placeholder="Например: Kingston DDR4 16GB"
                error={errors.externalName?.message}
              />
              {!isNew && <p className={styles.hint}>Внешнее название нельзя изменить после создания.</p>}
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Категория комплектующего <span className={styles.req}>*</span></label>
              <Controller
                name="componentType"
                control={control}
                render={({ field }) => (
                  <Select 
                    value={field.value}
                    onChange={(val) => {
                      const newType = val ? String(val) : '';
                      field.onChange(newType);
                      
                      if (newType !== originalData?.internalComponentType) {
                        setValue('componentId', undefined, { shouldValidate: true });
                        setValue('componentName', '');
                      } else {
                        setValue('componentId', originalData.internalComponentId, { shouldValidate: true });
                        setValue('componentName', originalData.internalComponentName);
                      }
                    }}
                    options={COMPONENT_TYPE_OPTIONS}
                    placeholder="Выберите тип..."
                    isSearchable={true}
                  />
                )}
              />
              {errors.componentType && <span className={clsx(styles.hint, styles.req)}>{errors.componentType.message}</span>}
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Компонент из базы данных <span className={styles.req}>*</span></label>
              <div className={styles.componentSelector}>
                <div className={clsx(styles.selectedComponentBox, { [styles.hasError]: errors.componentId })}>
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
                <Button 
                  type="button" 
                  onClick={() => setIsModalOpen(true)} 
                  disabled={!componentType} 
                  className={styles.selectBtn}
                >
                  <Search size={16} /> Выбрать
                </Button>
              </div>
              {!componentType && <p className={styles.hint}>Сначала выберите категорию.</p>}
              {errors.componentId && <span className={clsx(styles.hint, styles.req)}>{errors.componentId.message}</span>}
            </div>
          </form>

          <div className={styles.actions}>
            <Button variant="secondary" onClick={() => navigate('/mappings')}>Отмена</Button>
            <Button 
              type="submit" 
              form="mapping-form" 
              isLoading={saveMutation.isPending} 
              disabled={!externalName || !componentId}
            >
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
                  <Badge variant={confidenceConfig[confidence].variant}>
                    {confidenceConfig[confidence].label}
                  </Badge>
                </div>
              </div>

              <div className={styles.metaItem}>
                <span className={styles.metaLabel}>Поисковое имя БД</span>
                <span className={clsx(styles.metaValue, styles.monospaceMeta)}>
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
          selectedId={componentId || null}
          selectedName={componentName || null}
          onSelect={(selectId, selectName) => {
            setValue('componentId', selectId, { shouldValidate: true, shouldDirty: true });
            setValue('componentName', selectName);
          }}
        />
      )}
    </div>
  );
};