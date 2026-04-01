import type { ExternalComponentCategory } from './integration';

export interface CompatibilityRuleDto {
  id: number;
  ruleCode: string;
  ruleName: string;
  expression: string;
  errorMessage: string;
  isActive: boolean;
  targetComponentTypes: ExternalComponentCategory[];
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface FieldMetadataDto {
  fieldPath: string;
  dataType: string;
  description: string;
  isNullable: boolean;
}

export interface MethodMetadataDto {
  methodSignature: string;
  returnType: string;
  description: string;
  isNullable: boolean;
}

export interface RuleBuilderMetadataDto {
  contextProperties: Record<string, string>;
  contextMethods: MethodMetadataDto[];
  componentFields: Record<ExternalComponentCategory | string, FieldMetadataDto[]>;
}