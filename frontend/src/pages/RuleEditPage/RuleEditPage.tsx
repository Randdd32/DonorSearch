import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Save } from 'lucide-react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { AxiosError } from 'axios';
import toast from 'react-hot-toast';
import ReactSelect, { type MultiValue } from 'react-select';
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

  const[ruleCode, setRuleCode] = useState(originalData?.ruleCode || '');
  const[ruleName, setRuleName] = useState(originalData?.ruleName || '');
  const [description, setDescription] = useState(originalData?.description || '');
  const [errorMessage, setErrorMessage] = useState(originalData?.errorMessage || '');
  const [isActive, setIsActive] = useState<boolean>(originalData?.isActive ?? true);
  const[targetTypes, setTargetTypes] = useState<ExternalComponentCategory[]>(originalData?.targetComponentTypes || []);
  const[expression, setExpression] = useState(originalData?.expression || '');

  const saveMutation = useMutation({
    mutationFn: async () => {
      if (!ruleCode.trim() || ruleCode.length > 100) throw new Error('Код правила обязателен и не более 100 символов');
      if (!ruleName.trim() || ruleName.length > 200) throw new Error('Название правила обязательно и не более 200 символов');
      if (!errorMessage.trim()) throw new Error('Текст ошибки обязателен');
      if (!expression.trim()) throw new Error('Выражение SpEL не может быть пустым');
      if (targetTypes.length === 0) throw new Error('Выберите минимум один целевой тип оборудования');

      const dto = {
        ruleCode: ruleCode.trim(),
        ruleName: ruleName.trim(),
        description: description.trim() || undefined,
        errorMessage: errorMessage.trim(),
        isActive,
        targetComponentTypes: targetTypes,
        expression: expression.trim(),
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
      if (e instanceof AxiosError) {
        toast.error(e.response?.data?.message || 'Ошибка валидации полей (сервер)');
      } else {
        toast.error(e.message || 'Ошибка сохранения');
      }
    }
  });

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
          
          <div className={styles.field}>
            <label className={styles.label}>Код правила <span className={styles.req}>*</span></label>
            <Input value={ruleCode} onChange={(e) => setRuleCode(e.target.value)} placeholder="Например: GPU_LENGTH_LIMIT" />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Читаемое название <span className={styles.req}>*</span></label>
            <Input value={ruleName} onChange={(e) => setRuleName(e.target.value)} placeholder="Например: Ограничение длины видеокарты" />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Целевые типы оборудования <span className={styles.req}>*</span></label>
            <ReactSelect
              isMulti
              options={COMPONENT_TYPE_OPTIONS}
              value={COMPONENT_TYPE_OPTIONS.filter(opt => targetTypes.includes(opt.value))}
              onChange={(selected: MultiValue<{ value: ExternalComponentCategory; label: string }>) => {
                setTargetTypes(selected ? selected.map(s => s.value) :[]);
              }}
              styles={getSelectStyles()}
              placeholder="Выберите типы..."
              noOptionsMessage={() => 'Не найдено'}
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Сообщение об ошибке<span className={styles.req}>*</span></label>
            <Input value={errorMessage} onChange={(e) => setErrorMessage(e.target.value)} placeholder="Видеокарта не поместится в корпус" />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Описание правила (опционально)</label>
            <textarea 
              value={description} onChange={(e) => setDescription(e.target.value)}
              className={clsx(styles.nativeInput, styles.textareaInput)} 
              placeholder="Краткое пояснение логики правила..."
            />
          </div>

          <div className={styles.field}>
            <label className={styles.label}>Статус активности</label>
            <Select 
              value={isActive ? 'true' : 'false'}
              onChange={(val) => setIsActive(val === 'true')}
              options={[{ value: 'true', label: 'Включено (проверяется)' }, { value: 'false', label: 'Отключено (игнорируется)' }]}
              isSearchable={false}
            />
          </div>
        </Card>

        <div className={styles.rightColumnWide}>
          <Card className={styles.flushCard}>
            <SpelBuilder expression={expression} onChange={setExpression} targetTypes={targetTypes} />
          </Card>

          {!isNew && originalData && (
            <Card className={styles.metaCard}>
              <div className={styles.metaGrid}>
                <div className={styles.metaItem}><span className={styles.metaLabel}>ID</span><span className={styles.metaValue}>{originalData.id}</span></div>
                <div className={styles.metaItem}><span className={styles.metaLabel}>Создано</span><span className={styles.metaValue}>{formatDateTime(originalData.createdAt)}</span></div>
                <div className={styles.metaItem}><span className={styles.metaLabel}>Обновлено</span><span className={styles.metaValue}>{formatDateTime(originalData.updatedAt)}</span></div>
              </div>
            </Card>
          )}

          <div className={styles.actionsClean}>
            <Button variant="secondary" onClick={() => navigate('/compatibility')}>Отмена</Button>
            <Button onClick={() => saveMutation.mutate()} isLoading={saveMutation.isPending}>
              <Save size={16} /> {isNew ? 'Создать правило' : 'Сохранить изменения'}
            </Button>
          </div>
        </div>

      </div>
    </div>
  );
};