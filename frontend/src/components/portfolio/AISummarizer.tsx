import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Sparkles, RefreshCw, Loader2 } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { aiSummarizerService, PortfolioRequest } from '@/services/aiSummarizerService';
import { PortfolioSummary, AssetHolding } from '@/services/portfolioService';
import { toast } from 'sonner';

interface AISummarizerProps {
  portfolioSummary: PortfolioSummary | null;
  isLoading?: boolean;
}

export const AISummarizer = ({ portfolioSummary, isLoading: summaryLoading }: AISummarizerProps) => {
  const [refreshKey, setRefreshKey] = useState(0);
  const getRequest = (): PortfolioRequest | null => {
    if (!portfolioSummary?.topHoldings?.length) return null;
    return {
      total_investment: Number(portfolioSummary.totalValue) || 0,
      portfolio: portfolioSummary.topHoldings.map((h: AssetHolding) => ({
        asset: h.ticker,
        percentage: Number(h.allocation) || 0,
        investment_value: Number(h.totalValue) || 0,
        quantity: Number(h.quantity) || 0,
      })),
    };
  };

  const request = getRequest();
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['ai-summarizer', portfolioSummary?.portfolioId, refreshKey],
    queryFn: () => aiSummarizerService.getRecommendation(request!),
    enabled: !!request && !summaryLoading,
    retry: 1,
  });

  const handleRefresh = () => {
    setRefreshKey((k) => k + 1);
    refetch();
    toast.info('Regenerating AI recommendations...');
  };

  if (summaryLoading || !portfolioSummary) {
    return (
      <section className="bg-card border border-border rounded-xl p-5">
        <h2 className="text-lg font-semibold mb-4">AI Portfolio Analysis</h2>
        <Skeleton className="h-[180px] w-full" />
      </section>
    );
  }
  if (!request || request.portfolio.length === 0) {
    return (
      <section className="bg-card border border-border rounded-xl p-5">
        <h2 className="text-lg font-semibold mb-4">AI Portfolio Analysis</h2>
        <div className="text-center py-8 text-muted-foreground">No holdings for analysis.</div>
      </section>
    );
  }

  return (
    <section className="bg-card border border-border rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Sparkles className="w-5 h-5 text-primary" />
          <h2 className="text-lg font-semibold">AI Portfolio Analysis</h2>
          {data && <span className="text-xs px-2 py-1 bg-primary/20 text-primary rounded-full font-medium">{data.model_used}</span>}
        </div>
        <Button variant="ghost" size="sm" onClick={handleRefresh} disabled={isLoading} className="gap-2">
          {isLoading ? <Loader2 className="w-4 h-4 animate-spin" /> : <RefreshCw className="w-4 h-4" />}
          Refresh
        </Button>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/4" />
        </div>
      ) : error ? (
        <Card className="border-destructive/50 bg-destructive-light/10">
          <CardContent className="p-4">
            <p className="text-sm text-destructive mb-2">Unable to generate AI recommendations</p>
            <p className="text-xs text-muted-foreground">{error instanceof Error ? error.message : 'Try again later.'}</p>
          </CardContent>
        </Card>
      ) : data ? (
        <Card className="border-border bg-gradient-to-br from-card to-muted/20">
          <CardContent className="p-6">
            <p className="text-sm leading-relaxed text-foreground whitespace-pre-wrap">{data.summary}</p>
            <div className="mt-4 pt-4 border-t border-border flex justify-between">
              <span className="text-xs text-muted-foreground">Status: <span className={cn('font-medium', data.status === 'success' ? 'text-success' : '')}>{data.status}</span></span>
            </div>
          </CardContent>
        </Card>
      ) : null}
    </section>
  );
};
