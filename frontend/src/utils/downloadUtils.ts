/**
 * Утилита для сохранения файла на диск клиента из объекта Blob.
 * 
 * @param blob Объект Blob, полученный от API
 * @param filename Желаемое имя файла при скачивании
 */
export const saveBlobAsFile = (blob: Blob, filename: string): void => {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
};