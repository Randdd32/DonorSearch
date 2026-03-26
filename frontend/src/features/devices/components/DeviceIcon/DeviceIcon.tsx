import { Server, Monitor, Laptop, Box } from 'lucide-react';

interface DeviceIconProps {
  typeName?: string;
  size?: number;
}

export const DeviceIcon = ({ typeName, size = 24 }: DeviceIconProps) => {
  const lower = typeName?.toLowerCase() || '';
  
  if (lower.includes('ноутбук')) return <Laptop size={size} />;
  if (lower.includes('монитор')) return <Monitor size={size} />;
  if (lower.includes('сервер') || lower.includes('системный блок') || lower.includes('пк')) return <Server size={size} />;
  
  return <Box size={size} />;
};