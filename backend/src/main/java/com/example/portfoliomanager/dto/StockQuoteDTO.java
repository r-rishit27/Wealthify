package com.example.portfoliomanager.dto;

import java.math.BigDecimal;

public class StockQuoteDTO {
    private String symbol;
    private BigDecimal currentPrice;
    private BigDecimal change;
    private BigDecimal changePercent;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal openPrice;
    private BigDecimal previousClose;
    private Long timestamp;

    public StockQuoteDTO() {}

    public StockQuoteDTO(String symbol, BigDecimal currentPrice, BigDecimal change, BigDecimal changePercent,
                         BigDecimal highPrice, BigDecimal lowPrice, BigDecimal openPrice, 
                         BigDecimal previousClose, Long timestamp) {
        this.symbol = symbol;
        this.currentPrice = currentPrice;
        this.change = change;
        this.changePercent = changePercent;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openPrice = openPrice;
        this.previousClose = previousClose;
        this.timestamp = timestamp;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String symbol;
        private BigDecimal currentPrice;
        private BigDecimal change;
        private BigDecimal changePercent;
        private BigDecimal highPrice;
        private BigDecimal lowPrice;
        private BigDecimal openPrice;
        private BigDecimal previousClose;
        private Long timestamp;

        public Builder symbol(String symbol) { this.symbol = symbol; return this; }
        public Builder currentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; return this; }
        public Builder change(BigDecimal change) { this.change = change; return this; }
        public Builder changePercent(BigDecimal changePercent) { this.changePercent = changePercent; return this; }
        public Builder highPrice(BigDecimal highPrice) { this.highPrice = highPrice; return this; }
        public Builder lowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; return this; }
        public Builder openPrice(BigDecimal openPrice) { this.openPrice = openPrice; return this; }
        public Builder previousClose(BigDecimal previousClose) { this.previousClose = previousClose; return this; }
        public Builder timestamp(Long timestamp) { this.timestamp = timestamp; return this; }

        public StockQuoteDTO build() {
            return new StockQuoteDTO(symbol, currentPrice, change, changePercent, 
                                     highPrice, lowPrice, openPrice, previousClose, timestamp);
        }
    }

    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }

    public BigDecimal getChange() { return change; }
    public void setChange(BigDecimal change) { this.change = change; }

    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getPreviousClose() { return previousClose; }
    public void setPreviousClose(BigDecimal previousClose) { this.previousClose = previousClose; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
