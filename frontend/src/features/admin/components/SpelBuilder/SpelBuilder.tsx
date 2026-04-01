import { useRef } from 'react';
import { Play, Code } from 'lucide-react';
import { useRuleMetadata, useValidateExpression } from '../../hooks/useRuleBuilder';
import { Button } from '../../../../components/ui/Button/Button';
import { Badge } from '../../../../components/ui/Badge/Badge';
import { Spinner } from '../../../../components/ui/Spinner/Spinner';
import { COMPONENT_CATEGORY_CONFIG } from '../../../../config/componentTypes';
import type { ExternalComponentCategory } from '../../../../types/integration';
import styles from './SpelBuilder.module.css';

interface SpelBuilderProps {
  expression: string;
  onChange: (val: string) => void;
  targetTypes: ExternalComponentCategory[];
}

export const SpelBuilder = ({ expression, onChange, targetTypes }: SpelBuilderProps) => {
  const { data: meta, isLoading } = useRuleMetadata();
  const { mutate: validate, isPending: isValidating } = useValidateExpression();
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const insertText = (text: string) => {
    if (!textareaRef.current) return;
    const start = textareaRef.current.selectionStart;
    const end = textareaRef.current.selectionEnd;
    const newValue = expression.substring(0, start) + text + expression.substring(end);
    onChange(newValue);

    setTimeout(() => {
      if (textareaRef.current) {
        textareaRef.current.focus();
        textareaRef.current.setSelectionRange(start + text.length, start + text.length);
      }
    }, 0);
  };

  if (isLoading) return <Spinner size={30} />;
  if (!meta) return <div className={styles.error}>Ошибка загрузки метаданных конструктора</div>;

  return (
    <div className={styles.builderContainer}>
      <div className={styles.editorHeader}>
        <span className={styles.editorTitle}><Code size={16}/> Логическое выражение (SpEL)</span>
        <Button variant="secondary" onClick={() => validate(expression)} isLoading={isValidating} disabled={!expression.trim()}>
          <Play size={14} /> Проверить синтаксис
        </Button>
      </div>
      
      <textarea
        ref={textareaRef}
        value={expression}
        onChange={(e) => onChange(e.target.value)}
        className={styles.textarea}
        placeholder="Например: #ctx.getTotalPsuWattage() >= (#ctx.getTotalTdpW() * 1.3)"
        rows={4}
      />

      <div className={styles.helperPanels}>
        <div className={styles.panel}>
          <h4 className={styles.panelTitle}>Глобальные методы</h4>
          <div className={styles.tagList}>
            {meta.contextMethods.map(m => (
              <div key={m.methodSignature} className={styles.tagItem} onClick={() => insertText(`#ctx.${m.methodSignature}`)}>
                <span className={styles.tagCode}>#ctx.{m.methodSignature}</span>
                <span className={styles.tagDesc}>{m.description}</span>
                {m.throwsMissingDataException && <Badge variant="warning" className={styles.microBadge}>throws</Badge>}
              </div>
            ))}
          </div>
        </div>

        <div className={styles.panel}>
          <h4 className={styles.panelTitle}>
            Доступные поля 
            {targetTypes.length === 0 && <span className={styles.warnText}>(Выберите целевые типы оборудования)</span>}
          </h4>
          <div className={styles.tagList}>
            {targetTypes.map(type => (
              <div key={type} className={styles.typeGroup}>
                <div className={styles.typeGroupName}>{COMPONENT_CATEGORY_CONFIG[type]?.label || type}</div>
                {meta.componentFields[type]?.map(f => (
                  <div key={f.fieldPath} className={styles.tagItem} onClick={() => insertText(f.fieldPath)}>
                    <span className={styles.tagCode}>{f.fieldPath}</span>
                    <span className={styles.tagDesc}>{f.description}</span>
                  </div>
                ))}
                {(!meta.componentFields[type] || meta.componentFields[type].length === 0) && (
                  <span className={styles.tagDesc}>Нет доступных полей</span>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};