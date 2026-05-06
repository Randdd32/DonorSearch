import { useState, useEffect, useRef } from 'react';
import { Download, RefreshCw } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { clsx } from 'clsx';
import { isAxiosError } from 'axios';
import { toast } from 'react-hot-toast/headless';
import { logsService } from '../../services/logs.service';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { saveBlobAsFile } from '../../utils/downloadUtils';
import { Button } from '../../components/ui/Button/Button';
import { Select } from '../../components/ui/Select/Select';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import styles from './LogsPage.module.css';

export const LogsPage = () => {
  useDocumentTitle('Системные логи');
  
  const [selectedFile, setSelectedFile] = useState<string>('');
  const [isDownloading, setIsDownloading] = useState(false);
  const terminalRef = useRef<HTMLDivElement>(null);

  const { data: files } = useQuery({
    queryKey: ['logFiles'],
    queryFn: logsService.getFiles
  });

  const { data: lines, isLoading, isFetching, refetch } = useQuery({
    queryKey: ['logTail', selectedFile],
    queryFn: () => logsService.getTail(selectedFile || undefined, 1000),
    refetchInterval: selectedFile === '' || selectedFile === 'application.log' ? 5000 : false
  });

  useEffect(() => {
    if (terminalRef.current) {
      terminalRef.current.scrollTop = terminalRef.current.scrollHeight;
    }
  }, [lines]);

  const fileOptions = files?.map(f => {
    const sizeMb = (f.sizeBytes / 1024 / 1024).toFixed(2);
    const date = new Date(f.lastModified).toLocaleDateString('ru-RU');
    return {
      value: f.filename,
      label: `${f.filename} (${sizeMb} MB) — ${date}`
    };
  }) || [];

  const handleDownload = async () => {
    try {
      setIsDownloading(true);
      const blob = await logsService.downloadLog(selectedFile || undefined);
      saveBlobAsFile(blob, selectedFile || 'application.log');
    } catch (e) {
      if (!isAxiosError(e)) {
        toast.error('Внутренняя ошибка при скачивании файла логов');
        console.error(e);
      }
    } finally {
      setIsDownloading(false);
    }
  };

  const getLineClass = (line: string) => {
    if (line.includes(' ERROR ')) return styles.logError;
    if (line.includes(' WARN ')) return styles.logWarn;
    if (line.includes(' INFO ')) return styles.logInfo;
    if (line.includes(' DEBUG ')) return styles.logDebug;
    return styles.logDefault;
  };

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.titleBlock}>
          <h1>События системы (логи)</h1>
          <p>Мониторинг работы алгоритмов и состояния сервера</p>
        </div>
        
        <div className={styles.controls}>
          <div className={styles.fileSelect}>
            <Select 
              value={selectedFile}
              onChange={(val) => setSelectedFile(val as string)}
              options={[{ value: '', label: 'Текущий лог (application.log)' }, ...fileOptions.filter(o => o.value !== 'application.log')]}
            />
          </div>
          
          <Button variant="secondary" onClick={() => refetch()} isLoading={isFetching}>
            <RefreshCw size={16} /> Обновить
          </Button>
          
          <Button onClick={handleDownload} isLoading={isDownloading}>
            <Download size={16} /> Скачать
          </Button>
        </div>
      </div>

      <div className={styles.terminalWrapper} ref={terminalRef}>
        {isLoading ? (
          <Spinner fullPage />
        ) : lines?.length === 0 ? (
          <div className={styles.logDefault}>Файл логов пуст.</div>
        ) : (
          lines?.map((line, idx) => (
            <div key={idx} className={clsx(styles.logLine, getLineClass(line))}>
              {line}
            </div>
          ))
        )}
      </div>
    </div>
  );
};