import ReactSelect, { type SingleValue } from 'react-select';
import { getSelectStyles } from '../../../utils/selectStyles';

interface Option {
  value: string | number | boolean;
  label: string;
}

interface SelectProps {
  value: string | number | boolean | null;
  onChange: (value: string | number | boolean | null) => void;
  options: Option[];
  placeholder?: string;
  className?: string;
  isDisabled?: boolean;
  isSearchable?: boolean;
  hasError?: boolean;
}

export const Select = ({ 
  value, onChange, options, placeholder, className, isDisabled, isSearchable = false, hasError = false 
}: SelectProps) => {
  const selectedOption = options.find(opt => String(opt.value) === String(value)) || null;

  return (
    <ReactSelect
      value={selectedOption}
      onChange={(selected: unknown) => {
        const opt = selected as SingleValue<Option>;
        onChange(opt ? opt.value : null);
      }}
      options={options}
      styles={getSelectStyles(hasError)}
      placeholder={placeholder}
      className={className}
      isDisabled={isDisabled}
      isSearchable={isSearchable}
      noOptionsMessage={() => 'Элементы не найдены'}
      menuPortalTarget={document.body}
      menuPosition="fixed"
    />
  );
};