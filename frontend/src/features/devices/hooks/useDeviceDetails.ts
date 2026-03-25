import { useQuery } from '@tanstack/react-query';
import { devicesService } from '../../../services/devices.service';

export const useDeviceDetails = (id: number) => {
  return useQuery({
    queryKey: ['device', id],
    queryFn: () => devicesService.getDeviceDetails(id),
    enabled: !!id,
  });
};