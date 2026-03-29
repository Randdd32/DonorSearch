import { Select } from '../Select/Select'; // Наш новый компонент
import styles from './SortSelect.module.css';

interface SortSelectProps {
  value: string;
  onChange: (value: string) => void;
  options: { value: string; label: string }[];
}

export const SortSelect = ({ value, onChange, options }: SortSelectProps) => {
  return (
    <div className={styles.container}>
      <Select 
        value={value} 
        onChange={onChange} 
        options={options} 
        isSearchable={false}
      />
    </div>
  );
};