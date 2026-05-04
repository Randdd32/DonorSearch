import axios, { AxiosError, type InternalAxiosRequestConfig } from 'axios';
import toast from 'react-hot-toast';
import { useAuthStore } from '../store/authStore';
import { getBrowserFingerprint } from '../utils/fingerprint';
import { API_BASE_URL, API_ENDPOINTS } from './constants';
import type { ApiErrorResponse, AuthResponseDto } from '../types/auth';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 15000,
  withCredentials: true,
  paramsSerializer: (params) => {
    const searchParams = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (value === undefined || value === null || value === '') return;
      if (Array.isArray(value)) {
        value.forEach((v) => searchParams.append(key, String(v)));
      } else {
        searchParams.append(key, String(value));
      }
    });
    return searchParams.toString();
  }
});

let lastNetworkErrorToastTime = 0;
const NETWORK_ERROR_COOLDOWN_MS = 5000;

let isRefreshing = false;
let failedQueue: Array<{ resolve: (value: string | null) => void; reject: (reason?: unknown) => void }> =[];

const silentStatuses = [401, 403];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue =[];
};

const translateErrorMessage = (errorCode?: string, msg?: string): string => {
  const defaultMsg = 'Произошла непредвиденная ошибка сервера';
  if (!msg && !errorCode) return defaultMsg;

  switch (errorCode) {
    case 'DATA_INTEGRITY_VIOLATION':
      return 'Нарушение уникальности данных (запись уже существует или используется).';
    case 'DTO_VALIDATION_FAILED':
    case 'MALFORMED_JSON_OR_TYPE_MISMATCH':
      return 'Ошибка валидации. Проверьте правильность заполнения полей.';
    case 'RESOURCE_NOT_FOUND':
    case 'ENDPOINT_NOT_FOUND':
      return 'Запрашиваемый ресурс не найден.';
    case 'METHOD_NOT_ALLOWED':
      return 'Этот метод запроса не поддерживается сервером.';
  }

  if (msg) {
    if (msg.includes('Username already exists')) return 'Пользователь с таким логином уже существует.';
    if (msg.includes('Mapping for external name') && msg.includes('already exists')) return 'Связь для такого внешнего названия уже существует.';
    if (msg.includes('Compatibility rule with code') && msg.includes('already exists')) return 'Правило с таким кодом уже существует.';
    
    if (msg.includes('You cannot change your own role')) return 'Вы не можете изменить собственную системную роль.';
    if (msg.includes('Password does not meet security requirements')) return 'Пароль не соответствует требованиям безопасности (8-60 символов, заглавные/строчные латинские буквы, цифра и спецсимвол).';
    if (msg.includes('Nobody has permission to')) return 'У вас нет прав для выполнения действий над Суперадминистратором.';
    if (msg.includes('You do not have permission to')) return 'Недостаточно прав для выполнения этого действия.';
    if (msg.includes('Session has expired')) return 'Время сессии истекло. Пожалуйста, войдите заново.';
    if (msg.includes('Invalid session (fingerprint mismatch)')) return 'Сессия недействительна (возможно, выполнен вход с другого устройства).';

    return msg;
  }

  return defaultMsg;
};

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

interface RetryConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError<ApiErrorResponse>) => {
    const originalRequest = error.config as RetryConfig | undefined;

    const status = error.response?.status;
    const isNetworkError = !error.response && (error.code === 'ERR_NETWORK' || error.message === 'Network Error');
    const isGatewayError = status === 502 || status === 503 || status === 504;

    if (isNetworkError || isGatewayError) {
      const now = Date.now();
      if (now - lastNetworkErrorToastTime > NETWORK_ERROR_COOLDOWN_MS) {
        toast.error('Сервер недоступен. Проверьте подключение к сети или обратитесь к администратору.', {
          id: 'global-network-error'
        });
        lastNetworkErrorToastTime = now;
      }
      return Promise.reject(error);
    }

    if (status === 401 && originalRequest && !originalRequest._retry) {
      if (originalRequest.url?.includes(API_ENDPOINTS.AUTH.REFRESH)) {
        useAuthStore.getState().logout();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise<string | null>((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            if (token && originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`;
            }
            return apiClient(originalRequest);
          })
          .catch((err: unknown) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const fingerprint = getBrowserFingerprint();
        const { data } = await axios.post<AuthResponseDto>(
          `${API_BASE_URL}${API_ENDPOINTS.AUTH.REFRESH}`, 
          { fingerprint }, 
          { withCredentials: true }
        );
        
        useAuthStore.getState().setAuth(data.accessToken, { username: data.username, role: data.role });
        
        processQueue(null, data.accessToken);
        
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${data.accessToken}`;
        }
        
        return apiClient(originalRequest);
      } catch (refreshError: unknown) {
        processQueue(refreshError, null);
        useAuthStore.getState().logout();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    if (
      !silentStatuses.includes(status ?? 0) &&
      originalRequest &&
      !originalRequest.url?.includes(API_ENDPOINTS.COMPATIBILITY_RULES.VALIDATE_EXPRESSION) &&
      !originalRequest.url?.includes(API_ENDPOINTS.AUTH.LOGIN)
    ) {
      const errorCode = error.response?.data?.error;
      const message = error.response?.data?.message;
      toast.error(translateErrorMessage(errorCode, message));
    }

    return Promise.reject(error);
  }
);