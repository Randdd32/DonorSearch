export type UserRole = 'USER' | 'ADMIN' | 'SUPERADMIN';

export const ROLE_LABELS: Record<UserRole, string> = {
  SUPERADMIN: 'Суперадминистратор',
  ADMIN: 'Администратор',
  USER: 'Пользователь'
};

export interface AuthUser {
  username: string;
  role: UserRole;
}

export interface AuthResponseDto {
  accessToken: string;
  username: string;
  role: UserRole;
}

export interface ApiErrorResponse {
  error: string;
  message: string;
  details?: string;
  fieldErrors?: Record<string, string>;
}

export interface UserDto {
  id: number;
  username: string;
  role: UserRole;
  createdAt: string;
  updatedAt: string | null;
}