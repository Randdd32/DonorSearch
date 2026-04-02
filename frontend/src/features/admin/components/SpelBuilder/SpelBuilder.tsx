import { useRef, useState } from 'react';
import { Play, Code, Settings2, Info } from 'lucide-react';
import { clsx } from 'clsx';
import { useRuleMetadata, useValidateExpression } from '../../hooks/useRuleBuilder';
import { Button } from '../../../../components/ui/Button/Button';
import { Badge } from '../../../../components/ui/Badge/Badge';
import { Spinner } from '../../../../components/ui/Spinner/Spinner';
import { COMPONENT_CATEGORY_CONFIG } from '../../../../config/componentTypes';
import type { ExternalComponentCategory } from '../../../../types/integration';
import styles from './SpelBuilder.module.css';

const COMMON_OPERATORS =[
  { code: '.size()', desc: 'Количество элементов коллекции' },
  { code: '.isEmpty()', desc: 'Проверка списка на пустоту' },
  { code: '.contains(x)', desc: 'Содержит ли элемент x' },
  { code: '?', desc: 'Безопасный вызов (защита от null)' },
  { code: '.?[ ]', desc: 'Фильтрация коллекции по условию' },
  { code: '==', desc: 'Равно' },
  { code: '!=', desc: 'Не равно' },
  { code: '<=', desc: 'Меньше или равно' },
  { code: '>=', desc: 'Больше или равно' },
  { code: 'and', desc: 'Логическое И' },
  { code: 'or', desc: 'Логическое ИЛИ' },
];

interface SpelBuilderProps {
  expression: string;
  onChange: (val: string) => void;
  targetTypes: ExternalComponentCategory[];
}

export const SpelBuilder = ({ expression, onChange, targetTypes }: SpelBuilderProps) => {
  const { data: meta, isLoading } = useRuleMetadata();
  const { mutate: validate, isPending: isValidating } = useValidateExpression();
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const [useGlobalCtx, setUseGlobalCtx] = useState(true);
  const[typePrefixes, setTypePrefixes] = useState<Record<string, boolean>>({});

  const insertText = (text: string) => {
    if (!textareaRef.current) return;
    const start = textareaRef.current.selectionStart;
    const end = textareaRef.current.selectionEnd;
    onChange(expression.substring(0, start) + text + expression.substring(end));
    setTimeout(() => {
      textareaRef.current?.focus();
      textareaRef.current?.setSelectionRange(start + text.length, start + text.length);
    }, 0);
  };

  const insertField = (type: string, fieldPath: string) => {
    let res = '';
    if (useGlobalCtx) res += '#ctx.';
    const contextProp = meta?.contextProperties[type]?.propertyName || type.toLowerCase();
    if (typePrefixes[type]) res += contextProp + '.';
    insertText(res + fieldPath);
  };

  if (isLoading) return <div className={styles.loader}><Spinner size={30} /></div>;
  if (!meta) return <div className={styles.warnText}>Ошибка загрузки метаданных</div>;

  return (
    <div className={styles.builderContainer}>
      
      <div className={styles.editorHeader}>
        <span className={styles.editorTitle}><Code size={16}/> Логическое выражение (SpEL)</span>
        <Button variant="secondary" onClick={() => validate(expression)} isLoading={isValidating} disabled={!expression.trim()}>
          <Play size={14} /> Проверить синтаксис
        </Button>
      </div>
      
      <div className={styles.textareaWrapper}>
        <textarea
          ref={textareaRef} value={expression} onChange={(e) => onChange(e.target.value)}
          className={styles.textarea} rows={4}
          placeholder="Например: #ctx.getTotalPsuWattage() >= (#ctx.getTotalTdpW() * 1.3)"
        />
      </div>

      <div className={styles.prefixSettings}>
        <Settings2 size={16} className={styles.settingsIcon} />
        <label className={styles.checkboxLabel}>
          <input type="checkbox" checked={useGlobalCtx} onChange={(e) => setUseGlobalCtx(e.target.checked)} />
          Добавлять <code>#ctx.</code> (к полям)
        </label>
        <div className={styles.divider} />
        {targetTypes.map((t: ExternalComponentCategory) => {
          const contextPropName = meta.contextProperties[t]?.propertyName || t.toLowerCase();
          return (
            <label key={t} className={styles.checkboxLabel}>
              <input 
                type="checkbox" 
                checked={!!typePrefixes[t]} 
                onChange={(e) => setTypePrefixes(p => ({ ...p, [t]: e.target.checked }))} 
              />
              Префикс <code>{contextPropName}.</code>
            </label>
          );
        })}
      </div>

      <div className={styles.helperPanels}>
        <h4 className={clsx(styles.panelHeader, styles.head1)}>Глобальные методы</h4>
        
        <h4 className={clsx(styles.panelHeader, styles.head2)}>
          Доступные поля 
          {targetTypes.length === 0 && <span className={styles.warnText}>(Выберите типы)</span>}
        </h4>
        
        <h4 className={clsx(styles.panelHeader, styles.head3)}>
          Операторы 
          <a href="https://docs.spring.io/spring-framework/reference/core/expressions.html" target="_blank" rel="noreferrer" title="Документация SpEL">
            <Info size={14} />
          </a>
        </h4>

        <div className={clsx(styles.panelScrollArea, styles.content1)}>
          <div className={styles.tagList}>
            {meta.contextMethods.map(m => (
              <div key={m.methodSignature} className={styles.tagItem} onClick={() => insertText(`#ctx.${m.methodSignature}`)}>
                <span className={styles.tagCode}>#ctx.{m.methodSignature}</span>
                <span className={styles.tagDesc}>{m.description}</span>
                <div className={styles.tagFooter}>
                  <span className={styles.typeBadge}>{m.returnType}</span>
                  {m.throwsMissingDataException && <Badge variant="warning" className={styles.microBadge}>throws Exception</Badge>}
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className={clsx(styles.panelScrollArea, styles.content2)}>
          <div className={styles.tagList}>
            {targetTypes.map((type: ExternalComponentCategory) => (
              <div key={type} className={styles.typeGroup}>
                <div className={styles.typeGroupName}>{COMPONENT_CATEGORY_CONFIG[type]?.label || type}</div>
                {meta.componentFields[type]?.map(f => (
                  <div key={f.fieldPath} className={styles.tagItem} onClick={() => insertField(type, f.fieldPath)}>
                    <span className={styles.tagCode}>{f.fieldPath}</span>
                    <span className={styles.tagDesc}>{f.description}</span>
                    <div className={styles.tagFooter}>
                      <span className={styles.typeBadge}>{f.dataType}</span>
                      {f.isNullable && <span className={styles.nullableText}>* может быть null</span>}
                    </div>
                  </div>
                ))}
                {(!meta.componentFields[type] || meta.componentFields[type].length === 0) && (
                  <span className={styles.tagDesc}>Нет доступных полей</span>
                )}
              </div>
            ))}
          </div>
        </div>

        <div className={clsx(styles.panelScrollArea, styles.content3)}>
          <div className={styles.tagList}>
            {COMMON_OPERATORS.map(op => (
              <div key={op.code} className={styles.tagItem} onClick={() => insertText(op.code)}>
                <span className={styles.tagCode}>{op.code}</span>
                <span className={styles.tagDesc}>{op.desc}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};