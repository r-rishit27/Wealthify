package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.NewsItemDTO;
import com.example.portfoliomanager.dto.NewsResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NewsService {

    private static final Logger log = LoggerFactory.getLogger(NewsService.class);
    private static final long CACHE_DURATION_HOURS = 1;
    private static final int MAX_NEWS_ITEMS = 10;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String geminiApiKey;
    private final String yahooEndpoint;

    // In-memory cache
    private NewsResponseDTO cachedResponse;
    private LocalDateTime cacheTimestamp;

    public NewsService(
            RestTemplate restTemplate,
            @Value("${news.gemini.api-key}") String geminiApiKey,
            @Value("${news.yahoo.endpoint}") String yahooEndpoint) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
        this.geminiApiKey = geminiApiKey;
        this.yahooEndpoint = yahooEndpoint;
    }

    public NewsResponseDTO getTickerNews() {
        // Check cache validity
        if (cachedResponse != null && cacheTimestamp != null) {
            long hoursSinceFetch = java.time.Duration.between(cacheTimestamp, LocalDateTime.now()).toHours();
            if (hoursSinceFetch < CACHE_DURATION_HOURS) {
                log.debug("Returning cached news data (fetched {} hours ago)", hoursSinceFetch);
                return cachedResponse;
            }
        }

        try {
            // Fetch raw news from Yahoo Finance
            List<Map<String, Object>> yahooNews = fetchYahooNews();
            
            if (yahooNews.isEmpty()) {
                log.warn("No news fetched from Yahoo Finance");
                return getCachedOrEmpty();
            }

            // Call Gemini to process and format news
            List<NewsItemDTO> processedNews = processWithGemini(yahooNews);

            // Build response
            NewsResponseDTO response = NewsResponseDTO.builder()
                    .items(processedNews)
                    .fetchedAt(LocalDateTime.now())
                    .build();

            // Update cache
            cachedResponse = response;
            cacheTimestamp = LocalDateTime.now();

            log.info("Successfully fetched and processed {} news items", processedNews.size());
            return response;

        } catch (Exception e) {
            log.error("Error fetching news: {}", e.getMessage(), e);
            return getCachedOrEmpty();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchYahooNews() {
        try {
            // Yahoo Finance search endpoint for stock market news
            String url = yahooEndpoint + "?q=stock%20market&quotesCount=0&newsCount=50";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.set("User-Agent", "Mozilla/5.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Object newsObj = body.get("news");
                
                if (newsObj instanceof List) {
                    return (List<Map<String, Object>>) newsObj;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching Yahoo Finance news: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<NewsItemDTO> processWithGemini(List<Map<String, Object>> yahooNews) {
        try {
            // Prepare prompt for Gemini
            String prompt = buildGeminiPrompt(yahooNews);
            
            // Call Gemini API
            String geminiResponse = callGeminiAPI(prompt);
            
            // Parse Gemini's JSON response
            return parseGeminiResponse(geminiResponse);
            
        } catch (Exception e) {
            log.error("Error processing news with Gemini: {}", e.getMessage(), e);
            // Fallback: convert Yahoo news directly without Gemini processing
            return convertYahooNewsDirectly(yahooNews);
        }
    }

    private String buildGeminiPrompt(List<Map<String, Object>> yahooNews) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a financial news analyzer. Analyze the following stock market news articles ");
        prompt.append("and return a JSON array with exactly ").append(MAX_NEWS_ITEMS).append(" items. ");
        prompt.append("Each item should have: title, url, source, publishedAt (ISO format), symbol (if mentioned), ");
        prompt.append("direction (UP, DOWN, or NEUTRAL based on whether the news suggests stock prices going up or down), ");
        prompt.append("and summary (1-2 sentences).\n\n");
        prompt.append("Select the most relevant and impactful stock market news.\n\n");
        prompt.append("News articles:\n");
        
        for (int i = 0; i < Math.min(yahooNews.size(), 30); i++) {
            Map<String, Object> article = yahooNews.get(i);
            prompt.append(i + 1).append(". ");
            if (article.get("title") != null) {
                prompt.append("Title: ").append(article.get("title")).append("\n");
            }
            if (article.get("summary") != null) {
                prompt.append("Summary: ").append(article.get("summary")).append("\n");
            }
            if (article.get("publisher") != null) {
                prompt.append("Source: ").append(article.get("publisher")).append("\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("\nReturn ONLY a valid JSON array, no other text. Format:\n");
        prompt.append("[{\"title\":\"...\",\"url\":\"...\",\"source\":\"...\",\"publishedAt\":\"...\",");
        prompt.append("\"symbol\":\"...\",\"direction\":\"UP|DOWN|NEUTRAL\",\"summary\":\"...\"},...]");
        
        return prompt.toString();
    }

    private String callGeminiAPI(String prompt) throws Exception {
        String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + geminiApiKey;
        
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);
        content.put("parts", Collections.singletonList(part));
        requestBody.put("contents", Collections.singletonList(content));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(geminiUrl, HttpMethod.POST, entity, Map.class);
        
        if (response.getBody() != null) {
            Map<String, Object> body = response.getBody();
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> contentMap = (Map<String, Object>) candidate.get("content");
                if (contentMap != null) {
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
        }
        
        throw new Exception("Invalid response from Gemini API");
    }

    @SuppressWarnings("unchecked")
    private List<NewsItemDTO> parseGeminiResponse(String geminiResponse) {
        List<NewsItemDTO> newsItems = new ArrayList<>();
        
        try {
            // Extract JSON from Gemini response (it might have markdown code blocks)
            String jsonStr = geminiResponse.trim();
            if (jsonStr.startsWith("```json")) {
                jsonStr = jsonStr.substring(7);
            }
            if (jsonStr.startsWith("```")) {
                jsonStr = jsonStr.substring(3);
            }
            if (jsonStr.endsWith("```")) {
                jsonStr = jsonStr.substring(0, jsonStr.length() - 3);
            }
            jsonStr = jsonStr.trim();
            
            JsonNode rootNode = objectMapper.readTree(jsonStr);
            
            if (rootNode.isArray()) {
                for (JsonNode itemNode : rootNode) {
                    try {
                        NewsItemDTO newsItem = NewsItemDTO.builder()
                                .title(getTextValue(itemNode, "title"))
                                .url(getTextValue(itemNode, "url"))
                                .source(getTextValue(itemNode, "source"))
                                .publishedAt(parseDateTime(getTextValue(itemNode, "publishedAt")))
                                .symbol(getTextValue(itemNode, "symbol"))
                                .direction(parseDirection(getTextValue(itemNode, "direction")))
                                .summary(getTextValue(itemNode, "summary"))
                                .build();
                        
                        if (newsItem.getTitle() != null && !newsItem.getTitle().isEmpty()) {
                            newsItems.add(newsItem);
                        }
                    } catch (Exception e) {
                        log.warn("Error parsing news item: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            throw new RuntimeException("Failed to parse Gemini response", e);
        }
        
        return newsItems;
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        return fieldNode != null && fieldNode.isTextual() ? fieldNode.asText() : null;
    }

    private LocalDateTime parseDateTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            // Try ISO format first
            return LocalDateTime.parse(dateStr.replace("Z", "").replace("+00:00", ""));
        } catch (Exception e) {
            try {
                // Try other common formats
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                return LocalDateTime.parse(dateStr, formatter);
            } catch (Exception e2) {
                log.warn("Could not parse date: {}", dateStr);
                return LocalDateTime.now();
            }
        }
    }

    private NewsItemDTO.NewsDirection parseDirection(String directionStr) {
        if (directionStr == null) {
            return NewsItemDTO.NewsDirection.NEUTRAL;
        }
        String upper = directionStr.toUpperCase();
        if (upper.contains("UP") || upper.equals("UP")) {
            return NewsItemDTO.NewsDirection.UP;
        } else if (upper.contains("DOWN") || upper.equals("DOWN")) {
            return NewsItemDTO.NewsDirection.DOWN;
        }
        return NewsItemDTO.NewsDirection.NEUTRAL;
    }

    @SuppressWarnings("unchecked")
    private List<NewsItemDTO> convertYahooNewsDirectly(List<Map<String, Object>> yahooNews) {
        List<NewsItemDTO> newsItems = new ArrayList<>();
        
        for (int i = 0; i < Math.min(yahooNews.size(), MAX_NEWS_ITEMS); i++) {
            Map<String, Object> article = yahooNews.get(i);
            try {
                NewsItemDTO newsItem = NewsItemDTO.builder()
                        .title((String) article.getOrDefault("title", ""))
                        .url((String) article.getOrDefault("link", ""))
                        .source((String) article.getOrDefault("publisher", "Yahoo Finance"))
                        .publishedAt(parseYahooDate(article.get("providerPublishTime")))
                        .symbol(null)
                        .direction(NewsItemDTO.NewsDirection.NEUTRAL)
                        .summary((String) article.getOrDefault("summary", ""))
                        .build();
                
                if (newsItem.getTitle() != null && !newsItem.getTitle().isEmpty()) {
                    newsItems.add(newsItem);
                }
            } catch (Exception e) {
                log.warn("Error converting Yahoo news item: {}", e.getMessage());
            }
        }
        
        return newsItems;
    }

    private LocalDateTime parseYahooDate(Object dateObj) {
        if (dateObj == null) {
            return LocalDateTime.now();
        }
        try {
            if (dateObj instanceof Number) {
                long timestamp = ((Number) dateObj).longValue();
                return LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochSecond(timestamp),
                    ZoneId.systemDefault()
                );
            }
        } catch (Exception e) {
            log.warn("Could not parse Yahoo date: {}", dateObj);
        }
        return LocalDateTime.now();
    }

    private NewsResponseDTO getCachedOrEmpty() {
        if (cachedResponse != null) {
            log.info("Returning cached news due to error");
            return cachedResponse;
        }
        return NewsResponseDTO.builder()
                .items(new ArrayList<>())
                .fetchedAt(LocalDateTime.now())
                .build();
    }
}
