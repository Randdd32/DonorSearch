import { ArrowDownAZ } from 'lucide-react';
import { clsx } from 'clsx';
import { DropdownSelect } from '../DropdownSelect/DropdownSelect';
import styles from './SortSelect.module.css';

interface SortSelectProps {
  value: string;
  onChange: (value: string) => void;
  options: { value: string; label: string }[];
  className?: string;
}

export const SortSelect = ({ value, onChange, options, className }: SortSelectProps) => {
  return (
    <div  className={clsx(styles.container, className)}>
      <DropdownSelect 
        value={value} 
        onChange={(val) => onChange(String(val))} 
        options={options} 
        icon={<ArrowDownAZ size={18} />}
        className={styles.dropdown}
      />
    </div>
  );
};