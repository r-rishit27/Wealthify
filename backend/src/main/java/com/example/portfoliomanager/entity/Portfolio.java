package com.example.portfoliomanager.entity;
import jakarta.persistence.*;



@Entity
public class Portfolio {

    @Id
    private String id;
    private String portfolioName;
    private String description;
    private String baseCurrency;
    private Double cashBalance;

    public Portfolio() {}

    public Portfolio(String id, String portfolioName, String description, String baseCurrency, Double cashBalance) {
        this.id = id;
        this.portfolioName = portfolioName;
        this.description = description;
        this.baseCurrency = baseCurrency;
        this.cashBalance = cashBalance;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(Double cashBalance) {
        this.cashBalance = cashBalance;
    }
}
