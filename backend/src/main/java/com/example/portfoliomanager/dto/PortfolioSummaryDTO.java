package com.example.portfoliomanager.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioSummaryDTO {
    private String portfolioId;
    private String portfolioName;
    private String baseCurrency;
    private BigDecimal totalValue;
    private BigDecimal cashBalance;
    private BigDecimal assetsValue;
    private BigDecimal totalGain;
    private BigDecimal totalGainPercent;
    private int assetCount;
    private int transactionCount;
    private List<AssetHolding> topHoldings;
    private List<AllocationItem> allocation;

    public PortfolioSummaryDTO() {}

    // Getters and Setters
    public String getPortfolioId() { return portfolioId; }
    public void setPortfolioId(String portfolioId) { this.portfolioId = portfolioId; }

    public String getPortfolioName() { return portfolioName; }
    public void setPortfolioName(String portfolioName) { this.portfolioName = portfolioName; }

    public String getBaseCurrency() { return baseCurrency; }
    public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public BigDecimal getCashBalance() { return cashBalance; }
    public void setCashBalance(BigDecimal cashBalance) { this.cashBalance = cashBalance; }

    public BigDecimal getAssetsValue() { return assetsValue; }
    public void setAssetsValue(BigDecimal assetsValue) { this.assetsValue = assetsValue; }

    public BigDecimal getTotalGain() { return totalGain; }
    public void setTotalGain(BigDecimal totalGain) { this.totalGain = totalGain; }

    public BigDecimal getTotalGainPercent() { return totalGainPercent; }
    public void setTotalGainPercent(BigDecimal totalGainPercent) { this.totalGainPercent = totalGainPercent; }

    public int getAssetCount() { return assetCount; }
    public void setAssetCount(int assetCount) { this.assetCount = assetCount; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }

    public List<AssetHolding> getTopHoldings() { return topHoldings; }
    public void setTopHoldings(List<AssetHolding> topHoldings) { this.topHoldings = topHoldings; }

    public List<AllocationItem> getAllocation() { return allocation; }
    public void setAllocation(List<AllocationItem> allocation) { this.allocation = allocation; }

    public static class AssetHolding {
        private String assetId;
        private String ticker;
        private String assetName;
        private String assetType;
        private BigDecimal quantity;
        private BigDecimal currentPrice;
        private BigDecimal totalValue;
        private BigDecimal gainLoss;
        private BigDecimal gainLossPercentage;
        private BigDecimal allocation;

        public AssetHolding() {}

        public String getAssetId() { return assetId; }
        public void setAssetId(String assetId) { this.assetId = assetId; }

        public String getTicker() { return ticker; }
        public void setTicker(String ticker) { this.ticker = ticker; }

        public String getAssetName() { return assetName; }
        public void setAssetName(String assetName) { this.assetName = assetName; }

        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }

        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

        public BigDecimal getCurrentPrice() { return currentPrice; }
        public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

        public BigDecimal getGainLoss() { return gainLoss; }
        public void setGainLoss(BigDecimal gainLoss) { this.gainLoss = gainLoss; }

        public BigDecimal getGainLossPercentage() { return gainLossPercentage; }
        public void setGainLossPercentage(BigDecimal gainLossPercentage) { this.gainLossPercentage = gainLossPercentage; }

        public BigDecimal getAllocation() { return allocation; }
        public void setAllocation(BigDecimal allocation) { this.allocation = allocation; }
    }

    public static class AllocationItem {
        private String assetType;
        private BigDecimal value;
        private BigDecimal percentage;

        public AllocationItem() {}

        public AllocationItem(String assetType, BigDecimal value, BigDecimal percentage) {
            this.assetType = assetType;
            this.value = value;
            this.percentage = percentage;
        }

        public String getAssetType() { return assetType; }
        public void setAssetType(String assetType) { this.assetType = assetType; }

        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }

        public BigDecimal getPercentage() { return percentage; }
        public void setPercentage(BigDecimal percentage) { this.percentage = percentage; }
    }
}
