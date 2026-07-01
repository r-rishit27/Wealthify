package com.example.portfoliomanager.dto;

import java.util.List;

public class StockPredictionDTO {

    private String ticker;
    private String lastObservedDate;
    private Double lastObservedPrice;
    private List<ForecastPoint> forecast;
    private Double confidenceScore;

    public StockPredictionDTO() {}

    public StockPredictionDTO(String ticker, String lastObservedDate, Double lastObservedPrice,
                              List<ForecastPoint> forecast, Double confidenceScore) {
        this.ticker = ticker;
        this.lastObservedDate = lastObservedDate;
        this.lastObservedPrice = lastObservedPrice;
        this.forecast = forecast;
        this.confidenceScore = confidenceScore;
    }

    // Getters and Setters
    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }

    public String getLastObservedDate() { return lastObservedDate; }
    public void setLastObservedDate(String lastObservedDate) { this.lastObservedDate = lastObservedDate; }

    public Double getLastObservedPrice() { return lastObservedPrice; }
    public void setLastObservedPrice(Double lastObservedPrice) { this.lastObservedPrice = lastObservedPrice; }

    public List<ForecastPoint> getForecast() { return forecast; }
    public void setForecast(List<ForecastPoint> forecast) { this.forecast = forecast; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public static class ForecastPoint {
        private String date;
        private Double price;

        public ForecastPoint() {}

        public ForecastPoint(String date, Double price) {
            this.date = date;
            this.price = price;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
    }
}
