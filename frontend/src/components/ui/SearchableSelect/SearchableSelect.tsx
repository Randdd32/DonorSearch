import AsyncSelect from 'react-select/async';
import { useState, useEffect } from 'react';
import debounce from 'lodash.debounce';
import toast from 'react-hot-toast';
import type { StylesConfig, MultiValue, SingleValue } from 'react-select';
import type { SelectOption } from '../../../services/dictionary.service';

interface SearchableSelectProps {
  value: number | number[] | null;
  onChange: (value: number | number[] | null) => void;
  fetchOptions: (search?: string) => Promise<SelectOption[]>;
  fetchByIds: (ids: number[]) => Promise<SelectOption[]>;
  isMulti?: boolean;
  placeholder?: string;
  className?: string;
  isDisabled?: boolean;
}

export const SearchableSelect = ({
  value, onChange, fetchOptions, fetchByIds,
  isMulti = false, placeholder = 'Выберите значение...', className, isDisabled = false
}: SearchableSelectProps) => {
  const [selectedOptions, setSelectedOptions] = useState<SelectOption | SelectOption[] | null>(null);
  const [isLoadingInitial, setIsLoadingInitial] = useState(false);

  useEffect(() => {
    const loadInitial = async () => {
      const isValueEmpty = isMulti ? (!value || (value as number[]).length === 0) : (!value && value !== 0);
      if (isValueEmpty) {
        setSelectedOptions(isMulti ?[] : null);
        return;
      }
      setIsLoadingInitial(true);
      try {
        const idsToFetch = isMulti ? (value as number[]) :[value as number];
        const fetched = await fetchByIds(idsToFetch);
        setSelectedOptions(isMulti ? fetched : (fetched.length > 0 ? fetched[0] : null));
      } catch (error) {
        const msg = error instanceof Error ? error.message : 'Неизвестная ошибка';
        toast.error('Ошибка при загрузке значений: ' + msg);
        setSelectedOptions(isMulti ?[] : null);
      } finally {
        setIsLoadingInitial(false);
      }
    };
    loadInitial();
  }, [value, isMulti, fetchByIds]);

  const loadOptions = debounce((inputValue: string, callback: (options: SelectOption[]) => void) => {
    fetchOptions(inputValue)
      .then(options => callback(options))
      .catch(e => {
        toast.error('Ошибка поиска: ' + e?.message);
        callback([]);
      });
  }, 300);

  const handleChange = (selected: MultiValue<SelectOption> | SingleValue<SelectOption> | null) => {
    setSelectedOptions(selected as SelectOption | SelectOption[] | null);
    
    if (isMulti) {
      const multiSelected = selected as MultiValue<SelectOption>;
      if (!multiSelected || multiSelected.length === 0) {
        onChange([]);
      } else {
        onChange(multiSelected.map(item => item.value));
      }
    } else {
      const singleSelected = selected as SingleValue<SelectOption>;
      onChange(singleSelected ? singleSelected.value : null);
    }
  };

  const customStyles: StylesConfig<SelectOption, boolean> = {
    control: (base, state) => ({
      ...base,
      backgroundColor: 'var(--bg-surface)',
      borderColor: state.isFocused ? 'var(--primary-color)' : 'var(--border-color)',
      boxShadow: state.isFocused ? '0 0 0 1px var(--primary-color)' : 'none',
      '&:hover': { borderColor: 'var(--primary-color)' },
      minHeight: '40px',
      borderRadius: 'var(--radius-md)',
    }),
    menu: (base) => ({
      ...base,
      backgroundColor: 'var(--bg-surface)',
      border: '1px solid var(--border-color)',
      boxShadow: 'var(--shadow-md)',
      zIndex: 50,
    }),
    option: (base, state) => ({
      ...base,
      backgroundColor: state.isSelected ? 'var(--primary-color)' : state.isFocused ? 'var(--bg-hover)' : 'transparent',
      color: state.isSelected ? '#fff' : 'var(--text-primary)',
      cursor: 'pointer',
      '&:active': { backgroundColor: 'var(--primary-hover)' },
    }),
    singleValue: (base) => ({ ...base, color: 'var(--text-primary)' }),
    multiValue: (base) => ({
      ...base,
      backgroundColor: 'var(--bg-hover)',
      border: '1px solid var(--border-color)',
      borderRadius: '4px',
    }),
    multiValueLabel: (base) => ({ ...base, color: 'var(--text-primary)' }),
    multiValueRemove: (base) => ({
      ...base,
      color: 'var(--text-muted)',
      cursor: 'pointer',
      '&:hover': { backgroundColor: 'var(--status-critical)', color: '#fff' },
    }),
    input: (base) => ({ ...base, color: 'var(--text-primary)' }),
  };

  return (
    <AsyncSelect
      className={className}
      value={selectedOptions}
      onChange={handleChange}
      loadOptions={loadOptions}
      defaultOptions={true}
      isMulti={isMulti}
      placeholder={placeholder}
      isDisabled={isDisabled || isLoadingInitial}
      isLoading={isLoadingInitial}
      styles={customStyles}
      noOptionsMessage={() => 'Элементы не найдены'}
      isClearable={true}
    />
  );
};