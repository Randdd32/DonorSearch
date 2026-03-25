import { useQuery } from '@tanstack/react-query';
import { devicesService } from '../../../services/devices.service';
import type { GetDevicesParams } from '../../../services/devices.service';

export const useDevices = (params: GetDevicesParams) => {
  return useQuery({
    queryKey:['devices', params],
    queryFn: () => devicesService.getDevices(params),
    placeholderData: (previousData) => previousData,
  });
};