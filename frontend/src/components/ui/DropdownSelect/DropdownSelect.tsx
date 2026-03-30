import { useState, useRef, useEffect } from 'react';
import { ChevronDown } from 'lucide-react';
import { clsx } from 'clsx';
import styles from './DropdownSelect.module.css';

interface Option {
  value: string | number;
  label: string;
}

interface DropdownSelectProps {
  value: string | number;
  onChange: (value: string | number) => void;
  options: Option[];
  icon?: React.ReactNode;
  className?: string;
  placement?: 'top' | 'bottom';
}

export const DropdownSelect = ({ value, onChange, options, icon, className, placement = 'bottom' }: DropdownSelectProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  },[]);

  const selectedLabel = options.find((opt) => String(opt.value) === String(value))?.label;

  const handleSelect = (val: string | number) => {
    onChange(val);
    setIsOpen(false);
  };

  return (
    <div className={clsx(styles.container, className)} ref={containerRef}>
      <button 
        type="button"
        className={clsx(styles.trigger, { [styles.open]: isOpen })}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className={styles.triggerContent}>
          {icon && <span className={styles.icon}>{icon}</span>}
          <span className={styles.selectedText}>{selectedLabel}</span>
        </div>
        <ChevronDown size={16} className={clsx(styles.chevron, { 
          [styles.rotatedUp]: isOpen && placement === 'bottom',
          [styles.rotatedDown]: isOpen && placement === 'top',
          [styles.defaultTop]: placement === 'top' && !isOpen 
        })} />
      </button>

      {isOpen && (
        <div className={clsx(styles.menu, styles[placement])}>
          <div className={styles.optionsList}>
            {options.map((opt) => (
              <button
                key={opt.value}
                type="button"
                className={clsx(styles.option, { [styles.selected]: String(opt.value) === String(value) })}
                onClick={() => handleSelect(opt.value)}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};