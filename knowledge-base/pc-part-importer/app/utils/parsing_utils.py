import pandas as pd

def parse_separated_string(value, separator: str = '|') -> list[str]:
    """
    Разбивает строку по заданному разделителю (по умолчанию '|') 
    и возвращает чистый список. Игнорирует NaN и пустые значения.
    """
    if pd.isna(value) or not value:
        return []
    
    val_str = str(value).strip()
    if val_str.lower() == 'nan' or not val_str:
        return []
        
    return [item.strip() for item in val_str.split(separator) if item.strip()]