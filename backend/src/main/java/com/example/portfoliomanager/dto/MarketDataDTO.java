package com.example.portfoliomanager.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MarketDataDTO {

    private Long id;
    private String ticker;
    private LocalDate date;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private Long volume;
    private BigDecimal change;
    private BigDecimal changePercent;

    public MarketDataDTO() {}

    public MarketDataDTO(Long id, String ticker, LocalDate date, BigDecimal openPrice,
                         BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice, 
                         Long volume, BigDecimal change, BigDecimal changePercent) {
        this.id = id;
        this.ticker = ticker;
        this.date = date;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.change = change;
        this.changePercent = changePercent;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String ticker;
        private LocalDate date;
        private BigDecimal openPrice;
        private BigDecimal highPrice;
        private BigDecimal lowPrice;
        private BigDecimal closePrice;
        private Long volume;
        private BigDecimal change;
        private BigDecimal changePercent;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder ticker(String ticker) { this.ticker = ticker; return this; }
        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder openPrice(BigDecimal openPrice) { this.openPrice = openPrice; return this; }
        public Builder highPrice(BigDecimal highPrice) { this.highPrice = highPrice; return this; }
        public Builder lowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; return this; }
        public Builder closePrice(BigDecimal closePrice) { this.closePrice = closePrice; return this; }
        public Builder volume(Long volume) { this.volume = volume; return this; }
        public Builder change(BigDecimal change) { this.change = change; return this; }
        public Builder changePercent(BigDecimal changePercent) { this.changePercent = changePercent; return this; }

        public MarketDataDTO build() {
            return new MarketDataDTO(id, ticker, date, openPrice, highPrice, lowPrice, 
                                     closePrice, volume, change, changePercent);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getOpenPrice() { return openPrice; }
    public void setOpenPrice(BigDecimal openPrice) { this.openPrice = openPrice; }

    public BigDecimal getHighPrice() { return highPrice; }
    public void setHighPrice(BigDecimal highPrice) { this.highPrice = highPrice; }

    public BigDecimal getLowPrice() { return lowPrice; }
    public void setLowPrice(BigDecimal lowPrice) { this.lowPrice = lowPrice; }

    public BigDecimal getClosePrice() { return closePrice; }
    public void setClosePrice(BigDecimal closePrice) { this.closePrice = closePrice; }

    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }

    public BigDecimal getChange() { return change; }
    public void setChange(BigDecimal change) { this.change = change; }

    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }
}
