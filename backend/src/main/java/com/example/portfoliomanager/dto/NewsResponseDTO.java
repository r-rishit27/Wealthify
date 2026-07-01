package com.example.portfoliomanager.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NewsResponseDTO {
    private List<NewsItemDTO> items;
    private LocalDateTime fetchedAt;

    public NewsResponseDTO() {}

    public NewsResponseDTO(List<NewsItemDTO> items, LocalDateTime fetchedAt) {
        this.items = items;
        this.fetchedAt = fetchedAt;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<NewsItemDTO> items;
        private LocalDateTime fetchedAt;

        public Builder items(List<NewsItemDTO> items) { this.items = items; return this; }
        public Builder fetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; return this; }

        public NewsResponseDTO build() {
            return new NewsResponseDTO(items, fetchedAt);
        }
    }

    // Getters and Setters
    public List<NewsItemDTO> getItems() { return items; }
    public void setItems(List<NewsItemDTO> items) { this.items = items; }

    public LocalDateTime getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(LocalDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
}
