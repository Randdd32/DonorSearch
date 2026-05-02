export type UserRole = 'USER' | 'ADMIN' | 'SUPERADMIN';

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