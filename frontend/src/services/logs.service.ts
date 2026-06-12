import { apiClient } from '../config/api';
import { API_ENDPOINTS } from '../config/constants';

export interface LogFileDto {
  filename: string;
  sizeBytes: number;
  lastModified: string;
}

export const logsService = {
  async getFiles(): Promise<LogFileDto[]> {
    const { data } = await apiClient.get<LogFileDto[]>(API_ENDPOINTS.LOGS.FILES);
    return data;
  },

  async getTail(filename?: string, lines: number = 1000): Promise<string[]> {
    const { data } = await apiClient.get<{ lines: string[] }>(API_ENDPOINTS.LOGS.TAIL, {
      params: { filename, lines }
    });
    return data.lines;
  },
  
  async downloadLog(filename?: string): Promise<Blob> {
    const { data } = await apiClient.get<Blob>(API_ENDPOINTS.LOGS.DOWNLOAD, {
      params: { filename },
      responseType: 'blob'
    });
    return data;
  }
};