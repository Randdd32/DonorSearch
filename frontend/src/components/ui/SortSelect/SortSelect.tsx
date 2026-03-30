import { ArrowDownAZ } from 'lucide-react';
import { DropdownSelect } from '../DropdownSelect/DropdownSelect';
import styles from './SortSelect.module.css';

interface SortSelectProps {
  value: string;
  onChange: (value: string) => void;
  options: { value: string; label: string }[];
}

export const SortSelect = ({ value, onChange, options }: SortSelectProps) => {
  return (
    <div className={styles.container}>
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