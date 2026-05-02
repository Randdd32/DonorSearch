import { create } from 'zustand';
import axios from 'axios';
import { getBrowserFingerprint } from '../utils/fingerprint';
import { API_BASE_URL, API_ENDPOINTS } from '../config/constants';
import type { AuthUser, AuthResponseDto } from '../types/auth';

interface AuthState {
  user: AuthUser | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isInitialized: boolean;
  isAdmin: boolean;
  
  setAuth: (token: string, user: AuthUser) => void;
  logout: () => void;
  checkAuth: () => Promise<void>;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: null,
  isAuthenticated: false,
  isInitialized: false,
  isAdmin: false,

  setAuth: (token, user) => set({ 
    accessToken: token, 
    user, 
    isAuthenticated: true,
    isAdmin: user.role === 'ADMIN' || user.role === 'SUPERADMIN'
  }),
  
  logout: () => set({ 
    accessToken: null, 
    user: null, 
    isAuthenticated: false, 
    isAdmin: false 
  }),

  checkAuth: async () => {
    try {
      const fingerprint = getBrowserFingerprint();
      const { data } = await axios.post<AuthResponseDto>(
        `${API_BASE_URL}${API_ENDPOINTS.AUTH.REFRESH}`, 
        { fingerprint }, 
        { withCredentials: true }
      );
      
      set({ 
        accessToken: data.accessToken, 
        user: { username: data.username, role: data.role }, 
        isAuthenticated: true, 
        isInitialized: true,
        isAdmin: data.role === 'ADMIN' || data.role === 'SUPERADMIN'
      });
    } catch {
      set({ 
        accessToken: null, 
        user: null, 
        isAuthenticated: false, 
        isInitialized: true,
        isAdmin: false
      });
    }
  }
}));