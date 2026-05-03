import { apiClient } from '../config/api';
import { API_ENDPOINTS } from '../config/constants';
import type { PageDto } from '../types/pagination';
import type { UserDto, UserRole } from '../types/auth';
import type { CommonFilters } from '../hooks/useUrlFilters';

export interface GetUsersParams extends CommonFilters {
  roles?: UserRole[];
  createdAfter?: string;
  createdBefore?: string;
  updatedAfter?: string;
  updatedBefore?: string;
}

export interface UserCreateDto {
  username: string;
  password: string;
  role: UserRole;
}

export interface UserUpdateDto {
  password?: string;
  role: UserRole;
}

export const usersService = {
  async getMe(): Promise<UserDto> {
    const { data } = await apiClient.get<UserDto>(API_ENDPOINTS.USERS.ME);
    return data;
  },

  async getUsers(params: GetUsersParams): Promise<PageDto<UserDto>> {
    const queryParams = {
      page: params.page || 0,
      size: params.size || 10,
      search: params.search,
      sort: params.sort,
      roles: params.roles?.join(','),
      createdAfter: params.createdAfter ? new Date(params.createdAfter).toISOString() : undefined,
      createdBefore: params.createdBefore ? new Date(params.createdBefore).toISOString() : undefined,
      updatedAfter: params.updatedAfter ? new Date(params.updatedAfter).toISOString() : undefined,
      updatedBefore: params.updatedBefore ? new Date(params.updatedBefore).toISOString() : undefined
    };

    const { data } = await apiClient.get<PageDto<UserDto>>(API_ENDPOINTS.USERS.BASE, { params: queryParams });
    return data;
  },

  async getById(id: number): Promise<UserDto> {
    const { data } = await apiClient.get<UserDto>(API_ENDPOINTS.USERS.DETAILS(id));
    return data;
  },

  async create(dto: UserCreateDto): Promise<UserDto> {
    const { data } = await apiClient.post<UserDto>(API_ENDPOINTS.USERS.BASE, dto);
    return data;
  },

  async update(id: number, dto: UserUpdateDto): Promise<UserDto> {
    const { data } = await apiClient.put<UserDto>(API_ENDPOINTS.USERS.DETAILS(id), dto);
    return data;
  },

  async delete(id: number): Promise<void> {
    await apiClient.delete(API_ENDPOINTS.USERS.DETAILS(id));
  },

  async revokeSessions(id: number): Promise<void> {
    await apiClient.post(API_ENDPOINTS.USERS.REVOKE_SESSIONS(id));
  }
};