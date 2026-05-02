import { apiClient } from '../config/api';
import { API_ENDPOINTS } from '../config/constants';
import type { UserDto } from '../types/auth';

export const usersService = {
  async getMe(): Promise<UserDto> {
    const { data } = await apiClient.get<UserDto>(API_ENDPOINTS.USERS.ME);
    return data;
  }
};