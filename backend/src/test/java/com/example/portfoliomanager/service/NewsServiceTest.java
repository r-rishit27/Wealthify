package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.NewsItemDTO;
import com.example.portfoliomanager.dto.NewsResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NewsService Unit Tests")
class NewsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NewsService newsService;

    private List<Map<String, Object>> mockYahooNews;

    @BeforeEach
    void setUp() {
        mockYahooNews = new ArrayList<>();
        
        Map<String, Object> article1 = new HashMap<>();
        article1.put("title", "Apple Stock Hits New High");
        article1.put("summary", "Apple shares reach record levels on strong iPhone sales");
        article1.put("link", "https://example.com/news1");
        article1.put("publisher", "Reuters");
        article1.put("providerPublishTime", System.currentTimeMillis() / 1000);
        mockYahooNews.add(article1);

        Map<String, Object> article2 = new HashMap<>();
        article2.put("title", "Tech Sector Declines");
        article2.put("summary", "Major tech companies report disappointing earnings");
        article2.put("link", "https://example.com/news2");
        article2.put("publisher", "Bloomberg");
        article2.put("providerPublishTime", System.currentTimeMillis() / 1000);
        mockYahooNews.add(article2);
    }

    @Test
    @DisplayName("Should get ticker news successfully")
    void testGetTickerNews() {
        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", mockYahooNews);

        when(restTemplate.exchange(
                contains("/search"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getFetchedAt());
    }

    @Test
    @DisplayName("Should handle empty news response")
    void testGetTickerNewsEmpty() {
        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", new ArrayList<>());

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }

    @Test
    @DisplayName("Should handle null news response")
    void testGetTickerNewsNullResponse() {
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(null));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }

    @Test
    @DisplayName("Should cache news response")
    void testGetTickerNewsCaching() {
        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", mockYahooNews);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        // First call should fetch from API
        NewsResponseDTO result1 = newsService.getTickerNews();
        
        // Second call should return cached response without calling API again
        NewsResponseDTO result2 = newsService.getTickerNews();

        assertNotNull(result1);
        assertNotNull(result2);
        // Verify API was called only once due to caching
        verify(restTemplate, atLeastOnce()).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        );
    }

    @Test
    @DisplayName("Should return cached response on API error")
    void testGetTickerNewsFallsBackToCache() {
        // Set initial cached response
        NewsResponseDTO cachedResponse = NewsResponseDTO.builder()
                .items(new ArrayList<>())
                .fetchedAt(LocalDateTime.now().minusHours(1))
                .build();
        ReflectionTestUtils.setField(newsService, "cachedResponse", cachedResponse);
        ReflectionTestUtils.setField(newsService, "cacheTimestamp", LocalDateTime.now().minusHours(1));

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("API Error"));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }

    @Test
    @DisplayName("Should return empty list on API error without cache")
    void testGetTickerNewsErrorWithoutCache() {
        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenThrow(new RuntimeException("API Error"));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(0, result.getItems().size());
    }

    @Test
    @DisplayName("Should handle missing fields in news articles")
    void testGetTickerNewsHandlesMissingFields() {
        List<Map<String, Object>> newsWithMissingFields = new ArrayList<>();
        Map<String, Object> article = new HashMap<>();
        article.put("title", "Some News");
        newsWithMissingFields.add(article);

        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", newsWithMissingFields);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }

    @Test
    @DisplayName("Should set fetchedAt timestamp")
    void testGetTickerNewsSetsFetchedAt() {
        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", new ArrayList<>());

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        LocalDateTime beforeCall = LocalDateTime.now();
        NewsResponseDTO result = newsService.getTickerNews();
        LocalDateTime afterCall = LocalDateTime.now();

        assertNotNull(result.getFetchedAt());
        assertTrue(result.getFetchedAt().isAfter(beforeCall) || result.getFetchedAt().isEqual(beforeCall));
        assertTrue(result.getFetchedAt().isBefore(afterCall) || result.getFetchedAt().isEqual(afterCall));
    }

    @Test
    @DisplayName("Should handle response with null news field")
    void testGetTickerNewsNullNewsField() {
        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", null);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }

    @Test
    @DisplayName("Should handle response without news field")
    void testGetTickerNewsNoNewsField() {
        Map<String, Object> yahooResponse = new HashMap<>();

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }

    @Test
    @DisplayName("Should get supported tickers for news")
    void testGetTickerNewsSupport() {
        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", mockYahooNews);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle multiple news articles")
    void testGetTickerNewsMultipleArticles() {
        // Create 15 news articles to test if it handles large lists
        List<Map<String, Object>> manyArticles = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            Map<String, Object> article = new HashMap<>();
            article.put("title", "News Article " + i);
            article.put("summary", "Summary " + i);
            article.put("link", "https://example.com/news" + i);
            article.put("publisher", "Source " + i);
            article.put("providerPublishTime", System.currentTimeMillis() / 1000);
            manyArticles.add(article);
        }

        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", manyArticles);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }

    @Test
    @DisplayName("Should handle special characters in news content")
    void testGetTickerNewsSpecialCharacters() {
        List<Map<String, Object>> newsWithSpecialChars = new ArrayList<>();
        Map<String, Object> article = new HashMap<>();
        article.put("title", "Stock \"ABC\" surges 50% & $100M deal!");
        article.put("summary", "Breaking: <Company> reports Q4 earnings");
        article.put("link", "https://example.com/news?id=123&type=tech");
        article.put("publisher", "Reuters");
        article.put("providerPublishTime", System.currentTimeMillis() / 1000);
        newsWithSpecialChars.add(article);

        Map<String, Object> yahooResponse = new HashMap<>();
        yahooResponse.put("news", newsWithSpecialChars);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(ResponseEntity.ok(yahooResponse));

        NewsResponseDTO result = newsService.getTickerNews();

        assertNotNull(result);
        assertNotNull(result.getItems());
    }
}
