import { useMutation } from '@tanstack/react-query';
import { searchService } from '../../../services/search.service';

interface RunSearchParams {
  deviceId: number;
  adapterId: number;
}

export const useRunSearch = () => {
  return useMutation({
    mutationFn: ({ deviceId, adapterId }: RunSearchParams) => 
      searchService.runSearch(deviceId, adapterId),
  });
};