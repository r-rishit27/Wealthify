package com.example.portfoliomanager.entity;


import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Asset {

    @Id
    private String id;
    private String portfolioId;
    private String ticker;
    private String assetName;

    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    private Integer quantity;
    private Double purchasePrice;
    private LocalDate purchaseDate;
    private String notes;

    public Asset() {}

    public Asset(String id, String portfolioId, String ticker, String assetName, AssetType assetType, Integer quantity, Double purchasePrice, LocalDate purchaseDate, String notes) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.ticker = ticker;
        this.assetName = assetName;
        this.assetType = assetType;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.purchaseDate = purchaseDate;
        this.notes = notes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}