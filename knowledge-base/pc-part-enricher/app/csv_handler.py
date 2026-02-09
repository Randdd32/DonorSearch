import pandas as pd
import os

def load_csv(file_path):
    if not os.path.exists(file_path):
        raise FileNotFoundError(f'File {file_path} not found.')
    
    df = pd.read_csv(file_path)
    
    if 'part_url' not in df.columns:
        raise ValueError("Column 'part_url' is missing in the input CSV.")
        
    return df

def save_csv(data_rows, output_path):
    if not data_rows:
        print('No data to save.')
        return

    df = pd.DataFrame(data_rows)
    df = _fill_missing_counts(df)
    
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    df.to_csv(output_path, index=False)
    print(f'Saved to {output_path}')

def _fill_missing_counts(df):
    zero_fill_patterns = [
        'power_', 'output_', 'input_', 'header_', 
        'sata_', 'pci_', 'ext_', 'int_', 
        '_connectors', '_slots', '_bays', '_ports'
    ]
    
    cols_to_fill = [
        c for c in df.columns 
        if any(c.startswith(p) or c.endswith(p) for p in zero_fill_patterns)
    ]
    
    if cols_to_fill:
        df[cols_to_fill] = df[cols_to_fill].fillna(0)
        
        for col in cols_to_fill:
            try:
                df[col] = df[col].astype('Int64')
            except ValueError:
                pass
                
    return df