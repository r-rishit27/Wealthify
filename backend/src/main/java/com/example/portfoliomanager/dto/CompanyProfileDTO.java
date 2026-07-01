package com.example.portfoliomanager.dto;

import java.math.BigDecimal;

public class CompanyProfileDTO {
    private String symbol;
    private String name;
    private String country;
    private String currency;
    private String exchange;
    private String industry;
    private String logo;
    private BigDecimal marketCap;
    private String weburl;

    public CompanyProfileDTO() {}

    public CompanyProfileDTO(String symbol, String name, String country, String currency,
                             String exchange, String industry, String logo, 
                             BigDecimal marketCap, String weburl) {
        this.symbol = symbol;
        this.name = name;
        this.country = country;
        this.currency = currency;
        this.exchange = exchange;
        this.industry = industry;
        this.logo = logo;
        this.marketCap = marketCap;
        this.weburl = weburl;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String symbol;
        private String name;
        private String country;
        private String currency;
        private String exchange;
        private String industry;
        private String logo;
        private BigDecimal marketCap;
        private String weburl;

        public Builder symbol(String symbol) { this.symbol = symbol; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder country(String country) { this.country = country; return this; }
        public Builder currency(String currency) { this.currency = currency; return this; }
        public Builder exchange(String exchange) { this.exchange = exchange; return this; }
        public Builder industry(String industry) { this.industry = industry; return this; }
        public Builder logo(String logo) { this.logo = logo; return this; }
        public Builder marketCap(BigDecimal marketCap) { this.marketCap = marketCap; return this; }
        public Builder weburl(String weburl) { this.weburl = weburl; return this; }

        public CompanyProfileDTO build() {
            return new CompanyProfileDTO(symbol, name, country, currency, exchange, 
                                         industry, logo, marketCap, weburl);
        }
    }

    // Getters and Setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }

    public String getWeburl() { return weburl; }
    public void setWeburl(String weburl) { this.weburl = weburl; }
}
