/**
 * Вычисляет новое состояние массива сортировки с поддержкой мульти-сортировки (Shift).
 * 
 * @param currentSort Текущий массив сортировок (например, ['createdAt,desc'])
 * @param field Поле, по которому кликнули
 * @param isShiftPressed Был ли зажат Shift при клике
 * @returns Новый массив сортировок
 */
export const toggleSort = (currentSort: string[], field: string, isShiftPressed: boolean): string[] => {
  const sortArray = [...currentSort];
  const existingIndex = sortArray.findIndex(s => s.startsWith(field));
  let newDirection = 'asc';

  if (existingIndex >= 0) {
    const currentDir = sortArray[existingIndex].split(',')[1];
    newDirection = currentDir === 'asc' ? 'desc' : 'asc';
  }

  const sortString = `${field},${newDirection}`;

  if (isShiftPressed) {
    if (existingIndex >= 0) {
      sortArray[existingIndex] = sortString;
    } else {
      sortArray.push(sortString);
    }
  } else {
    return [sortString];
  }

  return sortArray;
};