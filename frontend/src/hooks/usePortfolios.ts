import { useQuery } from '@tanstack/react-query';
import { portfolioService, Portfolio, PortfolioSummary } from '@/services/portfolioService';

export const usePortfolios = () => {
  return useQuery({
    queryKey: ['portfolios'],
    queryFn: () => portfolioService.getAll(),
  });
};

/**
 * Convenience hook that returns the \"primary\" portfolio for the demo user.
 * Today this is simply the first portfolio in the list.
 */
export const usePrimaryPortfolioId = () => {
  return useQuery({
    queryKey: ['primaryPortfolioId'],
    queryFn: async () => {
      const result = await portfolioService.getAll();
      return result.content[0]?.portfolioId ?? null;
    },
  });
};

export const usePortfolio = (portfolioId: string) => {
  return useQuery({
    queryKey: ['portfolio', portfolioId],
    queryFn: () => portfolioService.getById(portfolioId),
    enabled: !!portfolioId,
  });
};

export const usePortfolioSummary = (portfolioId: string) => {
  return useQuery({
    queryKey: ['portfolioSummary', portfolioId],
    queryFn: () => portfolioService.getSummary(portfolioId),
    enabled: !!portfolioId,
  });
};
