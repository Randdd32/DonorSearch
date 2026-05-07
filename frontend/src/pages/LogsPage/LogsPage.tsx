import { useState, useEffect, useRef, useMemo } from 'react';
import { Download, RefreshCw, Search } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { clsx } from 'clsx';
import { isAxiosError } from 'axios';
import { toast } from 'react-hot-toast/headless';
import { logsService } from '../../services/logs.service';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { saveBlobAsFile } from '../../utils/downloadUtils';
import { Button } from '../../components/ui/Button/Button';
import { Select } from '../../components/ui/Select/Select';
import { Input } from '../../components/ui/Input/Input';
import { Spinner } from '../../components/ui/Spinner/Spinner';
import styles from './LogsPage.module.css';

export const LogsPage = () => {
  useDocumentTitle('Системные логи');
  
  const [selectedFile, setSelectedFile] = useState<string>('');
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [isDownloading, setIsDownloading] = useState(false);
  const terminalRef = useRef<HTMLDivElement>(null);

  const { data: files } = useQuery({
    queryKey: ['logFiles'],
    queryFn: logsService.getFiles
  });

  const { data: lines, isLoading, isFetching, refetch } = useQuery({
    queryKey: ['logTail', selectedFile],
    queryFn: () => logsService.getTail(selectedFile || undefined, 1000),
    refetchInterval: selectedFile === '' || selectedFile === 'application.log' ? 60000 : false
  });

  const filteredLines = useMemo(() => {
    if (!lines) return [];
    if (!searchTerm.trim()) return lines;
    const lowerSearch = searchTerm.toLowerCase();
    return lines.filter(line => line.toLowerCase().includes(lowerSearch));
  }, [lines, searchTerm]);

  useEffect(() => {
    if (terminalRef.current && !searchTerm) {
      terminalRef.current.scrollTop = terminalRef.current.scrollHeight;
    }
  }, [lines, searchTerm]);

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

  const highlightText = (text: string, highlight: string) => {
    if (!highlight.trim()) return text;
    const parts = text.split(new RegExp(`(${highlight})`, 'gi'));
    return parts.map((part, i) => 
      part.toLowerCase() === highlight.toLowerCase() ? (
        <span key={i} className={styles.highlight}>{part}</span>
      ) : part
    );
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
        
        <div className={styles.controlsArea}>
          <div className={styles.searchContainer}>
            <Input 
              icon={<Search size={16} />}
              placeholder="Поиск по тексту..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onClear={() => setSearchTerm('')}
            />
          </div>

          <div className={styles.actionRow}>
            <div className={styles.fileSelect}>
              <Select 
                value={selectedFile}
                onChange={(val) => {
                  setSelectedFile(val as string);
                  setSearchTerm('');
                }}
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
      </div>

      <div className={styles.terminalWrapper} ref={terminalRef}>
        {isLoading ? (
          <Spinner fullPage />
        ) : filteredLines.length === 0 ? (
          <div className={styles.logDefault}>
            {searchTerm ? 'По вашему запросу ничего не найдено.' : 'Файл логов пуст.'}
          </div>
        ) : (
          filteredLines.map((line, idx) => (
            <div key={idx} className={clsx(styles.logLine, getLineClass(line))}>
              {highlightText(line, searchTerm)}
            </div>
          ))
        )}
      </div>
    </div>
  );
};