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
  inventoryNumber: string;
  serialNumber: string;
  modelName: string;
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