import type { StylesConfig } from 'react-select';

export const getSelectStyles = <OptionType, IsMulti extends boolean>(): StylesConfig<OptionType, IsMulti> => ({
  control: (base, state) => ({
    ...base,
    backgroundColor: 'var(--bg-surface)',
    borderColor: state.isFocused ? 'var(--primary-color)' : 'var(--border-color)',
    boxShadow: state.isFocused ? '0 0 0 1px color-mix(in srgb, var(--primary-color) 20%, transparent)' : 'none',
    '&:hover': { borderColor: 'var(--primary-color)' },
    minHeight: '40px',
    borderRadius: 'var(--radius-md)',
    cursor: 'pointer',
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
    backgroundColor: state.isSelected 
      ? 'var(--primary-color)' 
      : state.isFocused 
        ? 'var(--bg-hover)' 
        : 'transparent',
    color: state.isSelected ? '#fff' : 'var(--text-primary)',
    cursor: 'pointer',
    '&:active': { backgroundColor: 'var(--primary-hover)' },
  }),
  singleValue: (base) => ({
    ...base,
    color: 'var(--text-primary)',
  }),
  multiValue: (base) => ({
    ...base,
    backgroundColor: 'var(--bg-hover)',
    border: '1px solid var(--border-color)',
    borderRadius: '4px',
  }),
  multiValueLabel: (base) => ({
    ...base,
    color: 'var(--text-primary)',
  }),
  multiValueRemove: (base) => ({
    ...base,
    color: 'var(--text-muted)',
    cursor: 'pointer',
    '&:hover': {
      backgroundColor: 'var(--status-critical)',
      color: '#fff',
    },
  }),
  input: (base) => ({
    ...base,
    color: 'var(--text-primary)',
  })
});