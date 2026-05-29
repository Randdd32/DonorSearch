import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';
import ReactSelect, { type MultiValue } from 'react-select';
import { useForm, Controller, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { clsx } from 'clsx';
import { rulesService } from '../../services/rules.service';
import { Input } from '../../components/ui/Input/Input';
import { Button } from '../../components/ui/Button/Button';
import { Select } from '../../components/ui/Select/Select';
import { Card } from '../../components/ui/Card/Card';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import { ErrorState } from '../../components/ui/ErrorState/ErrorState';
import { SpelBuilder } from '../../features/admin/components/SpelBuilder/SpelBuilder';
import { formatDateTime } from '../../utils/formatters';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { getSelectStyles } from '../../utils/selectStyles';
import { COMPONENT_TYPE_OPTIONS } from '../../config/componentTypes';
import type { ExternalComponentCategory } from '../../types/integration';
import type { CompatibilityRuleDto } from '../../types/compatibility';
import { ruleSchema, type RuleFormValues } from './ruleSchema';
import styles from '../../styles/layouts/editPageLayout.module.css';

export const RuleEditPage = () => {
  const { id } = useParams<{ id: string }>();
  const isNew = id === 'new';
  useDocumentTitle(isNew ? 'Новое правило' : 'Редактирование правила');

  const { data: originalData, isLoading, isError } = useQuery({
    queryKey: ['rule', id],
    queryFn: () => rulesService.getById(Number(id)),
    enabled: !isNew,
  });

  if (isLoading) return <Spinner fullPage size={40} />;
  if (isError) return <ErrorState title="Ошибка загрузки" message="Не удалось получить данные правила." />;

  return <RuleForm key={isNew ? 'new' : originalData?.id} isNew={isNew} id={id!} originalData={originalData} />;
};

interface RuleFormProps {
  isNew: boolean;
  id: string;
  originalData?: CompatibilityRuleDto;
}

const RuleForm = ({ isNew, id, originalData }: RuleFormProps) => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const {
    register,
    handleSubmit,
    control,
    formState: { errors }
  } = useForm<RuleFormValues>({
    resolver: zodResolver(ruleSchema),
    defaultValues: {
      ruleCode: originalData?.ruleCode || '',
      ruleName: originalData?.ruleName || '',
      description: originalData?.description || '',
      errorMessage: originalData?.errorMessage || '',
      isActive: originalData?.isActive ?? true,
      targetTypes: originalData?.targetComponentTypes || [],
      expression: originalData?.expression || '',
    },
    mode: 'onTouched'
  });

  const targetTypesValue = useWatch({ control, name: 'targetTypes' }) as ExternalComponentCategory[];

  const saveMutation = useMutation({
    mutationFn: async (data: RuleFormValues) => {
      const dto = {
        ruleCode: data.ruleCode.trim(),
        ruleName: data.ruleName.trim(),
        description: data.description?.trim() || undefined,
        errorMessage: data.errorMessage.trim(),
        isActive: data.isActive,
        targetComponentTypes: data.targetTypes as ExternalComponentCategory[],
        expression: data.expression.trim(),
      };

      return isNew ? rulesService.create(dto) : rulesService.update(Number(id), dto);
    },
    onSuccess: () => {
      toast.success(isNew ? 'Правило создано' : 'Правило обновлено');
      queryClient.invalidateQueries({ queryKey:['rules'] });
      queryClient.invalidateQueries({ queryKey: ['rule', id] });
      navigate('/compatibility');
    },
    onError: (e: Error | AxiosError<{ message: string }>) => {
      if (!(e instanceof AxiosError)) {
        toast.error(e.message || 'Ошибка валидации');
      }
    }
  });

  const onSubmit = (data: RuleFormValues) => {
    saveMutation.mutate(data);
  };

  return (
    <div className={clsx(styles.container, styles.containerWide)}>
      <div className={styles.header}>
        <Button variant="ghost" onClick={() => navigate('/compatibility')} className={styles.backBtn}>
          <ArrowLeft size={16} /> Назад
        </Button>
        <div>
          <h1 className={styles.title}>{isNew ? 'Новое правило' : 'Редактирование правила'}</h1>
          <p className={styles.subtitle}>Системные ограничения и логика совместимости</p>
        </div>
      </div>

      <div className={styles.content}>
        <Card className={clsx(styles.formCard, styles.leftColumn)}>
          <h3 className={styles.cardTitle}>Базовые настройки</h3>
          
          <form id="rule-form" onSubmit={handleSubmit(onSubmit)} className={styles.formContents}>
            <div className={styles.field}>
              <label className={styles.label}>Код правила <span className={styles.req}>*</span></label>
              <Input 
                {...register('ruleCode')}
                placeholder="Например: GPU_LENGTH_LIMIT" 
                error={errors.ruleCode?.message}
              />
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Читаемое название <span className={styles.req}>*</span></label>
              <Input 
                {...register('ruleName')}
                placeholder="Например: Ограничение длины видеокарты" 
                error={errors.ruleName?.message}
              />
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Целевые типы оборудования <span className={styles.req}>*</span></label>
              <Controller
                name="targetTypes"
                control={control}
                render={({ field }) => (
                  <ReactSelect
                    isMulti
                    options={COMPONENT_TYPE_OPTIONS}
                    value={COMPONENT_TYPE_OPTIONS.filter(opt => field.value.includes(opt.value))}
                    onChange={(selected: MultiValue<{ value: ExternalComponentCategory; label: string }>) => {
                      field.onChange(selected ? selected.map(s => s.value) : []);
                    }}
                    styles={getSelectStyles(!!errors.targetTypes)}
                    placeholder="Выберите типы..."
                    noOptionsMessage={() => 'Не найдено'}
                  />
                )}
              />
              {errors.targetTypes && <span className={styles.errorMessage}>{errors.targetTypes.message}</span>}
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Сообщение об ошибке <span className={styles.req}>*</span></label>
              <Input 
                {...register('errorMessage')}
                placeholder="Видеокарта не поместится в корпус" 
                error={errors.errorMessage?.message}
              />
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Описание правила (опционально)</label>
              <textarea 
                {...register('description')}
                className={clsx(styles.nativeInput, styles.textareaInput)} 
                placeholder="Краткое пояснение логики правила..."
              />
            </div>

            <div className={styles.field}>
              <label className={styles.label}>Статус активности</label>
              <Controller
                name="isActive"
                control={control}
                render={({ field }) => (
                  <Select 
                    value={field.value ? 'true' : 'false'}
                    onChange={(val) => field.onChange(val === 'true')}
                    options={[{ value: 'true', label: 'Включено (проверяется)' }, { value: 'false', label: 'Отключено (игнорируется)' }]}
                    isSearchable={false}
                  />
                )}
              />
            </div>
          </form>
        </Card>

        <div className={styles.rightColumnWide}>
          <Card className={styles.flushCard}>
            <Controller
              name="expression"
              control={control}
              render={({ field }) => (
                <>
                  <SpelBuilder 
                    expression={field.value} 
                    onChange={field.onChange} 
                    targetTypes={targetTypesValue || []} 
                  />
                  {errors.expression && (
                    <div className={styles.formErrorInline}>
                      {errors.expression.message}
                    </div>
                  )}
                </>
              )}
            />
          </Card>

          {!isNew && originalData && (
            <Card className={styles.metaCard}>
              <h3 className={styles.cardTitle}>Системная информация</h3> 
              <div className={styles.metaGrid}>
                <div className={styles.metaItem}><span className={styles.metaLabel}>ID</span><span className={styles.metaValue}>{originalData.id}</span></div>
                <div className={styles.metaItem}><span className={styles.metaLabel}>Создано</span><span className={styles.metaValue}>{formatDateTime(originalData.createdAt)}</span></div>
                <div className={styles.metaItem}><span className={styles.metaLabel}>Обновлено</span><span className={styles.metaValue}>{formatDateTime(originalData.updatedAt)}</span></div>
              </div>
            </Card>
          )}

          <div className={styles.actionsClean}>
            <Button variant="secondary" onClick={() => navigate('/compatibility')}>Отмена</Button>
            <Button 
              type="submit" 
              form="rule-form" 
              isLoading={saveMutation.isPending}
            >
              <Save size={16} /> {isNew ? 'Создать правило' : 'Сохранить изменения'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
};