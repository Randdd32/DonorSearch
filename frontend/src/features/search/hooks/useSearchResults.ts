import { useQuery } from '@tanstack/react-query';
import { searchService } from '../../../services/search.service';
import type { GetSearchResultsParams } from '../../../services/search.service';

export const useSearchResults = (sessionId: string, params: GetSearchResultsParams) => {
  return useQuery({
    queryKey: ['searchResults', sessionId, params],
    queryFn: () => searchService.getSearchResults(sessionId, params),
    enabled: !!sessionId,
    placeholderData: (prev) => prev,
  });
};