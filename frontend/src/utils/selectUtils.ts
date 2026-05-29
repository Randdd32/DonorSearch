/**
 * Функция для глобального перехвата скролла в компонентах react-select.
 * Закрывает выпадающее меню при скролле внешней области (например, модалки или страницы),
 * но оставляет его открытым при скролле внутри самого списка опций.
 */
export const closeSelectMenuOnScroll = (event: Event): boolean => {
  const target = event.target as HTMLElement;
  if (target?.id?.includes('listbox') || target?.className?.toString().includes('menu-list')) {
    return false;
  }
  return true;
};