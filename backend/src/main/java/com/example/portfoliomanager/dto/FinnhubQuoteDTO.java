package com.example.portfoliomanager.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FinnhubQuoteDTO {

    @JsonProperty("currentPrice")
    @JsonAlias("c")
    private double currentPrice;

    @JsonProperty("change")
    @JsonAlias("d")
    private double change;

    @JsonProperty("percentChange")
    @JsonAlias("dp")
    private double percentChange;

    @JsonProperty("highPrice")
    @JsonAlias("h")
    private double highPrice;

    @JsonProperty("lowPrice")
    @JsonAlias("l")
    private double lowPrice;

    @JsonProperty("openPrice")
    @JsonAlias("o")
    private double openPrice;

    @JsonProperty("previousClose")
    @JsonAlias("pc")
    private double previousClose;

    @JsonProperty("timestamp")
    @JsonAlias("t")
    private long timestamp;

    public FinnhubQuoteDTO() {}

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getPercentChange() {
        return percentChange;
    }

    public void setPercentChange(double percentChange) {
        this.percentChange = percentChange;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(double highPrice) {
        this.highPrice = highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
