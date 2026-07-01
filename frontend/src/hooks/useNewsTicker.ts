import { useQuery } from '@tanstack/react-query';
import { newsService } from '@/services/newsService';

const CACHE_DURATION_MS = 60 * 60 * 1000; // 60 minutes

export const useNewsTicker = () => {
  return useQuery({
    queryKey: ['newsTicker'],
    queryFn: () => newsService.getTickerNews(),
    staleTime: CACHE_DURATION_MS,
    gcTime: CACHE_DURATION_MS,
    refetchInterval: CACHE_DURATION_MS,
    retry: 2,
  });
};
