import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { transactionService, CreateTransactionRequest } from "@/services/transactionService";
import { usePortfolios } from "@/hooks/usePortfolios";
import { TRANSACTION_TYPES, CURRENCIES } from "@/utils/constants";
import { useToast } from "@/hooks/use-toast";

interface TradeDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  symbol: string;
  currentPrice: number;
}

export const TradeDialog = ({ open, onOpenChange, symbol, currentPrice }: TradeDialogProps) => {
  const { data: portfoliosData, isLoading: portfoliosLoading } = usePortfolios();
  const portfolios = portfoliosData?.content ?? [];
  const primaryPortfolioId = portfolios[0]?.portfolioId ?? "";

  const [transactionType, setTransactionType] = useState<string>(TRANSACTION_TYPES.BUY);
  const [quantity, setQuantity] = useState<string>("1");
  const [price, setPrice] = useState<string>(currentPrice ? String(currentPrice) : "");
  const [currency, setCurrency] = useState<string>("USD");

  // Sync price when dialog opens or stock/price changes
  useEffect(() => {
    if (open && currentPrice != null && currentPrice > 0) {
      setPrice(String(currentPrice));
    }
  }, [open, symbol, currentPrice]);

  const { toast } = useToast();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: async () => {
      if (!primaryPortfolioId) {
        throw new Error("No portfolio found. Please create a portfolio first.");
      }

      const qty = Math.floor(Number(quantity));
      const pr = Number(price);

      if (!qty || qty <= 0 || !pr || pr <= 0) {
        throw new Error("Quantity and price must be positive numbers.");
      }

      const payload: CreateTransactionRequest = {
        portfolioId: primaryPortfolioId,
        ticker: symbol,
        transactionType,
        quantity: qty,
        price: pr,
        amount: qty * pr,
        currency,
      };

      return transactionService.create(payload);
    },
    onSuccess: () => {
      toast({
        title: "Trade executed",
        description: `${transactionType} order for ${quantity} ${symbol} recorded successfully.`,
      });

      // Refresh transactions, portfolio list, and summary so UI stays in sync
      if (primaryPortfolioId) {
        queryClient.invalidateQueries({ queryKey: ["transactions", primaryPortfolioId] });
        queryClient.invalidateQueries({ queryKey: ["portfolioSummary", primaryPortfolioId] });
      }
      queryClient.invalidateQueries({ queryKey: ["portfolios"] });

      onOpenChange(false);
    },
    onError: (error: any) => {
      toast({
        title: "Trade failed",
        description: error?.message || "Unable to execute trade. Please try again.",
        variant: "destructive",
      });
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutation.mutate();
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Trade {symbol}</DialogTitle>
          <DialogDescription>
            Record a BUY or SELL transaction for your primary portfolio.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">
          {!portfoliosLoading && portfolios.length === 0 && (
            <div className="rounded-lg border border-amber-500/50 bg-amber-500/10 text-amber-700 dark:text-amber-400 px-4 py-3 text-sm">
              You need at least one portfolio to record a trade. Create one from the Portfolios or Dashboard page first.
            </div>
          )}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label>Type</Label>
              <Select value={transactionType} onValueChange={setTransactionType}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value={TRANSACTION_TYPES.BUY}>Buy</SelectItem>
                  <SelectItem value={TRANSACTION_TYPES.SELL}>Sell</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <Label>Quantity</Label>
              <Input
                type="number"
                min="0"
                step="1"
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
              />
            </div>

            <div className="space-y-2">
              <Label>Price</Label>
              <Input
                type="number"
                min="0"
                step="0.01"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
              />
            </div>

            <div className="space-y-2">
              <Label>Currency</Label>
              <Select value={currency} onValueChange={setCurrency}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {CURRENCIES.map((c) => (
                    <SelectItem key={c} value={c}>
                      {c}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <DialogFooter className="mt-4">
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={mutation.isLoading || !!portfoliosLoading || !primaryPortfolioId || portfolios.length === 0}
            >
              {mutation.isLoading ? "Submitting..." : "Submit Trade"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

