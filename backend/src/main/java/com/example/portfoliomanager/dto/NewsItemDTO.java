package com.example.portfoliomanager.dto;

import java.time.LocalDateTime;

public class NewsItemDTO {
    private String title;
    private String url;
    private String source;
    private LocalDateTime publishedAt;
    private String symbol;
    private NewsDirection direction;
    private String summary;

    public enum NewsDirection {
        UP,    // 📈
        DOWN,  // 📉
        NEUTRAL
    }

    public NewsItemDTO() {}

    public NewsItemDTO(String title, String url, String source, LocalDateTime publishedAt, 
                      String symbol, NewsDirection direction, String summary) {
        this.title = title;
        this.url = url;
        this.source = source;
        this.publishedAt = publishedAt;
        this.symbol = symbol;
        this.direction = direction;
        this.summary = summary;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title;
        private String url;
        private String source;
        private LocalDateTime publishedAt;
        private String symbol;
        private NewsDirection direction;
        private String summary;

        public Builder title(String title) { this.title = title; return this; }
        public Builder url(String url) { this.url = url; return this; }
        public Builder source(String source) { this.source = source; return this; }
        public Builder publishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; return this; }
        public Builder symbol(String symbol) { this.symbol = symbol; return this; }
        public Builder direction(NewsDirection direction) { this.direction = direction; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }

        public NewsItemDTO build() {
            return new NewsItemDTO(title, url, source, publishedAt, symbol, direction, summary);
        }
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public NewsDirection getDirection() { return direction; }
    public void setDirection(NewsDirection direction) { this.direction = direction; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
