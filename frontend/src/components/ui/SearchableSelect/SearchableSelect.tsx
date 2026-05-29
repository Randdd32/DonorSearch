import AsyncSelect from 'react-select/async';
import { useState, useEffect } from 'react';
import debounce from 'lodash.debounce';
import { getSelectStyles } from '../../../utils/selectStyles';
import { closeSelectMenuOnScroll } from '../../../utils/selectUtils';
import type { MultiValue, SingleValue } from 'react-select';
import type { SelectOption } from '../../../services/dictionary.service';

interface SearchableSelectProps {
  value: number | string | (number | string)[] | null;
  onChange: (value: number | string | (number | string)[] | null) => void;
  fetchOptions: (search?: string) => Promise<SelectOption[]>;
  fetchByIds: (ids: (number | string)[]) => Promise<SelectOption[]>;
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
      const isValueEmpty = isMulti 
        ? (!value || (value as (number | string)[]).length === 0) 
        : (!value && value !== 0 && value !== '');
        
      if (isValueEmpty) {
        setSelectedOptions(isMulti ? [] : null);
        return;
      }
      
      setIsLoadingInitial(true);
      try {
        const idsToFetch = isMulti ? (value as (number | string)[]) : [value as number | string];
        const fetched = await fetchByIds(idsToFetch);
        setSelectedOptions(isMulti ? fetched : (fetched.length > 0 ? fetched[0] : null));
      } catch {
        setSelectedOptions(isMulti ? [] : null);
      } finally {
        setIsLoadingInitial(false);
      }
    };
    loadInitial();
  }, [value, isMulti, fetchByIds]);

  const loadOptions = debounce((inputValue: string, callback: (options: SelectOption[]) => void) => {
    fetchOptions(inputValue)
      .then(options => callback(options))
      .catch(() => {
        callback([]);
      });
  }, 300);

  const handleChange = (selected: unknown) => {
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
      styles={getSelectStyles()}
      noOptionsMessage={() => 'Элементы не найдены'}
      isClearable={true}
      menuPortalTarget={document.body}
      menuPosition="fixed"
      closeMenuOnScroll={closeSelectMenuOnScroll}
    />
  );
};