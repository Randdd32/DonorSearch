import { 
  Server, 
  Monitor, 
  Laptop, 
  Box, 
  SquareTerminal, 
  Computer 
} from 'lucide-react';

interface DeviceIconProps {
  typeName?: string;
  size?: number;
  className?: string;
}

export const DeviceIcon = ({ typeName, size = 24, className }: DeviceIconProps) => {
  const iconProps = { size, className };
  
  switch (typeName) {
    case 'Ноутбук':
    case 'Нетбук':
      return <Laptop {...iconProps} />;
    case 'Моноблок':
      return <Monitor {...iconProps} />;
    case 'Терминал':
      return <SquareTerminal {...iconProps} />;
    case 'Сервер':
      return <Server {...iconProps} />;
    case 'Промышленный ПК':
      return <Computer {...iconProps} />;
    default:
      return <Box {...iconProps} />;
  }
};