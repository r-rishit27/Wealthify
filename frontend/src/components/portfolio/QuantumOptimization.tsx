import { useQuery } from '@tanstack/react-query';
import { BarChart, Bar, XAxis, YAxis, ResponsiveContainer, Tooltip, Cell } from 'recharts';
import { Zap, RefreshCw, TrendingUp, TrendingDown } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import { cn } from '@/lib/utils';
import { quantumOptimizationService, PortfolioRequest, OptimizationResponse } from '@/services/quantumOptimizationService';
import { PortfolioSummary, AssetHolding } from '@/services/portfolioService';
import { toast } from 'sonner';

interface QuantumOptimizationProps {
  portfolioSummary: PortfolioSummary | null;
  isLoading?: boolean;
}

interface ComparisonData {
  asset: string;
  current: number;
  optimized: number;
  change: number;
  entanglement_score: number;
  rationale: string;
}

export const QuantumOptimization = ({ portfolioSummary, isLoading: summaryLoading }: QuantumOptimizationProps) => {
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
    queryKey: ['quantum-optimization', portfolioSummary?.portfolioId],
    queryFn: () => quantumOptimizationService.optimize(request!),
    enabled: !!request && !summaryLoading,
    retry: 1,
  });

  if (error) toast.error('Quantum optimization unavailable.');
  if (summaryLoading || !portfolioSummary) {
    return (
      <section className="bg-card border border-border rounded-xl p-5">
        <h2 className="text-lg font-semibold mb-4">Quantum Optimization</h2>
        <Skeleton className="h-[320px] w-full" />
      </section>
    );
  }
  if (!request || request.portfolio.length === 0) {
    return (
      <section className="bg-card border border-border rounded-xl p-5">
        <h2 className="text-lg font-semibold mb-4">Quantum Optimization</h2>
        <div className="text-center py-8 text-muted-foreground">No holdings to optimize.</div>
      </section>
    );
  }

  const comparisonData: ComparisonData[] = request.portfolio.map((asset) => {
    const opt = data?.portfolio.find((p) => p.asset === asset.asset);
    const current = asset.percentage;
    const optimized = opt?.weight_assignment ?? current;
    return {
      asset: asset.asset,
      current,
      optimized,
      change: optimized - current,
      entanglement_score: opt?.entanglement_score ?? 0,
      rationale: opt?.rationale ?? '',
    };
  });

  return (
    <section className="bg-card border border-border rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Zap className="w-5 h-5 text-primary" />
          <h2 className="text-lg font-semibold">Quantum Optimization</h2>
          {data && (
            <span className="text-xs px-2 py-1 bg-success-light text-success rounded-full font-medium">
              {data.result}
            </span>
          )}
        </div>
        <Button variant="ghost" size="sm" onClick={() => refetch()} disabled={isLoading} className="gap-2">
          <RefreshCw className={cn('w-4 h-4', isLoading && 'animate-spin')} />
          Optimize
        </Button>
      </div>

      {isLoading ? (
        <Skeleton className="h-[320px] w-full" />
      ) : error || !data ? (
        <div className="text-center py-8 text-muted-foreground">Unable to optimize. Showing current allocation.</div>
      ) : (
        <>
          <div className="mb-6 p-4 bg-muted/30 rounded-lg border border-border">
            <p className="text-sm text-muted-foreground">{data.transmutation_summary}</p>
          </div>
          <div className="mb-6">
            <h3 className="text-sm font-medium mb-4">Weight comparison</h3>
            <ResponsiveContainer width="100%" height={240}>
              <BarChart data={comparisonData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                <XAxis dataKey="asset" axisLine={false} tickLine={false} tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 11 }} />
                <YAxis axisLine={false} tickLine={false} tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 11 }} tickFormatter={(v) => `${v}%`} />
                <Tooltip
                  content={({ active, payload }) =>
                    active && payload?.[0] ? (
                      <div className="bg-foreground/90 text-background px-3 py-2 rounded-lg shadow-lg text-xs">
                        <p className="font-medium">{payload[0].payload.asset}</p>
                        <p>Current: {payload[0].payload.current.toFixed(1)}%</p>
                        <p>Optimized: {payload[0].payload.optimized.toFixed(1)}%</p>
                      </div>
                    ) : null
                  }
                />
                <Bar dataKey="current" name="Current" radius={[4, 4, 0, 0]} fill="hsl(var(--muted-foreground))" opacity={0.6} />
                <Bar dataKey="optimized" name="Optimized" radius={[4, 4, 0, 0]}>
                  {comparisonData.map((entry, i) => (
                    <Cell key={i} fill={entry.change >= 0 ? 'hsl(var(--success))' : 'hsl(var(--destructive))'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
          <div className="space-y-3">
            {comparisonData.map((item) => (
              <Card key={item.asset} className="border-border bg-muted/20">
                <CardContent className="p-4">
                  <div className="flex items-start justify-between">
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <span className="font-semibold">{item.asset}</span>
                        <span className="text-xs px-2 py-0.5 rounded-full bg-muted text-muted-foreground">
                          Entanglement: {(item.entanglement_score * 100).toFixed(1)}%
                        </span>
                      </div>
                      <p className="text-xs text-muted-foreground">{item.rationale}</p>
                    </div>
                    <div className="text-right flex items-center gap-2">
                      <span className="text-sm text-muted-foreground">{item.current.toFixed(1)}%</span>
                      {item.change !== 0 && (item.change > 0 ? <TrendingUp className="w-4 h-4 text-success" /> : <TrendingDown className="w-4 h-4 text-destructive" />)}
                      <span className={cn('text-sm font-semibold tabular-nums', item.change >= 0 ? 'text-success' : 'text-destructive')}>
                        {item.optimized.toFixed(1)}%
                      </span>
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </>
      )}
    </section>
  );
};
