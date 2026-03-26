import type { ExternalDeviceDto, ExternalComponentDto } from './integration';

export type WarningSeverity = 'INFO' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface DonorWarningDto {
  message: string;
  severity: WarningSeverity;
}

export interface CompatibleComponentDto {
  externalInfo: ExternalComponentDto;
  componentPenalty: number;
  componentWarnings: DonorWarningDto[];
}

export interface DonorResultDto {
  donorDevice: ExternalDeviceDto;
  devicePenalty: number;
  totalPenalty: number;
  compatibleComponents: CompatibleComponentDto[];
}