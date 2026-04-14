import { Server, Monitor, Laptop, Box } from 'lucide-react';

interface DeviceIconProps {
  typeName?: string;
  size?: number;
  className?: string;
}

export const DeviceIcon = ({ typeName, size = 24, className }: DeviceIconProps) => {
  const lower = typeName?.toLowerCase() || '';
  const iconProps = { size, className };
  
  if (lower.includes('ноутбук')) return <Laptop {...iconProps} />;
  if (lower.includes('монитор')) return <Monitor {...iconProps} />;
  if (lower.includes('сервер') || lower.includes('системный блок') || lower.includes('пк')) return <Server {...iconProps} />;
  
  return <Box {...iconProps} />;
};