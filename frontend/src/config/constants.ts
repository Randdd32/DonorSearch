export const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*_=+-]).{8,60}$/;

export const API_BASE_URL = '/api/v1';

const AUTH_BASE = '/auth';
const USERS_BASE = '/users';
const COMPONENTS_BASE = '/components';
const INFRA_BASE = '/infra';
const DICT_BASE = '/dictionaries';
const MAPPINGS_BASE = '/mappings';
const RULES_BASE = '/compatibility-rules';
const SEARCH_BASE = '/search';

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: `${AUTH_BASE}/login`,
    REFRESH: `${AUTH_BASE}/refresh`,
    LOGOUT: `${AUTH_BASE}/logout`,
  },
  USERS: {
    BASE: USERS_BASE,
    ME: `${USERS_BASE}/me`,
    DETAILS: (id: number | string) => `${USERS_BASE}/${id}`,
    REVOKE_SESSIONS: (id: number | string) => `${USERS_BASE}/${id}/revoke-sessions`,
  },
  COMPONENTS: {
    CPU: `${COMPONENTS_BASE}/cpus`,
    CPU_COOLER: `${COMPONENTS_BASE}/cpu-coolers`,
    MOTHERBOARD: `${COMPONENTS_BASE}/motherboards`,
    MEMORY: `${COMPONENTS_BASE}/ram`,
    VIDEO_CARD: `${COMPONENTS_BASE}/video-cards`,
    STORAGE: `${COMPONENTS_BASE}/storages`,
    POWER_SUPPLY: `${COMPONENTS_BASE}/power-supplies`,
    CASE: `${COMPONENTS_BASE}/cases`,
    CASE_FAN: `${COMPONENTS_BASE}/case-fans`,
    OPTICAL_DRIVE: `${COMPONENTS_BASE}/optical-drives`,
    EXPANSION_CARD: `${COMPONENTS_BASE}/expansion-cards`,
    MONITOR: `${COMPONENTS_BASE}/monitors`
  },
  INFRA: {
    DEVICES: `${INFRA_BASE}/devices`,
    DEVICE_DETAILS: (id: number | string) => `${INFRA_BASE}/devices/${id}`,
    DICTIONARIES: `${INFRA_BASE}${DICT_BASE}`,
  },
  DICTIONARIES: {
    BASE: DICT_BASE,
  },
  MAPPINGS: {
    BASE: MAPPINGS_BASE,
    DETAILS: (id: number | string) => `${MAPPINGS_BASE}/${id}`,
  },
  COMPATIBILITY_RULES: {
    BASE: RULES_BASE,
    DETAILS: (id: number | string) => `${RULES_BASE}/${id}`,
    BUILDER_METADATA: `${RULES_BASE}/builder-metadata`,
    VALIDATE_EXPRESSION: `${RULES_BASE}/validate-expression`,
  },
  SEARCH: {
    RUN: `${SEARCH_BASE}/run`,
    RESULTS: (sessionId: string) => `${SEARCH_BASE}/results/${sessionId}`,
    EXPORT_PDF: (sessionId: string) => `${SEARCH_BASE}/results/${sessionId}/export/pdf`,
  }
} as const;