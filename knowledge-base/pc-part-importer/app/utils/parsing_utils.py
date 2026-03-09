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

def safe_int(val) -> int | None:
    """
    Безопасно преобразует значение в целое число.

    Возвращает None, если значение является NaN, None, пустой строкой
    или строкой 'nan'. Если преобразование невозможно, также
    возвращает None вместо выброса исключения.
    """
    if pd.isna(val) or val is None or str(val).strip().lower() == 'nan': 
        return None
    try: 
        return int(float(val))
    except ValueError: 
        return None

def safe_bool(val) -> bool:
    """
    Безопасно преобразует значение в логический тип (bool).

    Возвращает False, если значение является NaN, None или не относится
    к допустимым "истинным" значениям. Значение считается True, если
    после приведения к строке и нижнему регистру оно совпадает с одним
    из следующих: 'true', '1', 'yes', 'y'.
    """
    if pd.isna(val) or val is None: 
        return False
    return str(val).strip().lower() in ('true', '1', 'yes', 'y')

def clean_search_tokens(tokens: list) -> str:
    """
    Очищает список токенов для использования в поисковых строках.

    Приводит элементы к строке, удаляет лишние пробелы, переводит
    в нижний регистр и исключает пустые значения, None и 'nan'.
    Возвращает строку, где токены объединены через пробел.
    """
    clean = [str(t).strip().lower() for t in tokens if t and str(t).strip() and str(t).lower() not in ('none', 'nan')]
    return " ".join(clean)

def parse_range(val_str) -> tuple[int | None, int | None]:
    """
    Безопасно преобразует строку с диапазоном значений в кортеж из двух чисел.

    Ожидает строку, содержащую одно число или два числа, разделённых запятой.
    Если указано одно значение, оно используется как минимальное и максимальное.
    Возвращает (None, None), если значение является NaN, None, пустой строкой,
    'nan'/'none' или если преобразование в число невозможно.
    """
    if pd.isna(val_str) or val_str is None:
        return None, None
    s = str(val_str).strip()
    if s.lower() in ('nan', 'none', ''):
        return None, None
    
    parts =[p.strip() for p in s.split(',')]
    try:
        if len(parts) == 1:
            v = int(float(parts[0]))
            return v, v
        elif len(parts) >= 2:
            return int(float(parts[0])), int(float(parts[1]))
    except ValueError:
        pass
    return None, None