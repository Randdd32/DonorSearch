import { useMutation } from '@tanstack/react-query';
import { searchService } from '../../../services/search.service';
import type { ExternalComponentCategory } from '../../../types/integration';

interface RunSearchParams {
  deviceId: number;
  adapterId?: number;
  category?: ExternalComponentCategory;
}

export const useRunSearch = () => {
  return useMutation({
    mutationFn: ({ deviceId, adapterId, category }: RunSearchParams) => 
      searchService.runSearch(deviceId, adapterId, category)
  });
};