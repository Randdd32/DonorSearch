import { ArrowDownAZ } from 'lucide-react';
import styles from './SortSelect.module.css';

interface SortSelectProps {
  value: string;
  onChange: (value: string) => void;
  options: { value: string; label: string }[];
}

export const SortSelect = ({ value, onChange, options }: SortSelectProps) => {
  return (
    <div className={styles.container}>
      <ArrowDownAZ size={18} className={styles.icon} />
      <select 
        value={value} 
        onChange={(e) => onChange(e.target.value)} 
        className={styles.select}
      >
        {options.map(opt => (
          <option key={opt.value} value={opt.value}>{opt.label}</option>
        ))}
      </select>
    </div>
  );
};