export type ExternalDeviceState = 
  | 'WRITTEN_OFF' 
  | 'STORAGE' 
  | 'UNACCOUNTED' 
  | 'REPAIR' 
  | 'IN_USE' 
  | 'UNKNOWN';

export type ExternalComponentCategory = 
  | 'CPU' | 'CPU_COOLER' | 'MOTHERBOARD' | 'VIDEO_CARD' | 'MEMORY' 
  | 'STORAGE' | 'OPTICAL_DRIVE' | 'POWER_SUPPLY' | 'CASE' 
  | 'CASE_FAN' | 'EXPANSION_CARD' | 'MONITOR' | 'UNKNOWN';

export interface ExternalComponentDto {
  adapterId: number;
  categoryId: number;
  externalName: string;
  category: ExternalComponentCategory;
  manufacturerName: string;
  serialNumber: string;
  mappedComponentId: number | null;
}

export interface ExternalDeviceDto {
  externalId: number;
  name: string;
  inventoryNumber: string | null;
  serialNumber: string | null;
  note: string | null;
  assetTag: string | null;
  code: string | null;
  description: string | null;
  modelName: string;
  modelProductNumber: string | null;
  modelNote: string | null;
  manufacturerName: string;
  typeName: string;
  lifeCycleState: ExternalDeviceState;
  ownerFullName: string;
  departmentName: string;
  locationPath: string;
  dateReceived: string; // ISO 8601 string
  isWorking: boolean;
  components: ExternalComponentDto[];
}

export type MappingConfidence = 'AUTO' | 'NEEDS_REVIEW' | 'BAD_MATCH' | 'CONFIRMED';

export interface IntegrationMappingDto {
  id: number;
  externalName: string;
  internalComponentId: number;
  internalComponentName: string;
  internalComponentType: ExternalComponentCategory;
  internalComponentSearchName: string;
  confidence: MappingConfidence;
  createdAt: string;
  updatedAt: string;
}

export interface CreateMappingDto {
  externalName: string;
  internalComponentId: number;
  confidence: MappingConfidence;
}

export interface UpdateMappingDto {
  internalComponentId: number;
  confidence: MappingConfidence;
}