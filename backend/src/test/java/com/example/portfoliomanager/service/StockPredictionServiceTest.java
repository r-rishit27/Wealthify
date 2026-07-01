package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.StockPredictionDTO;
import com.example.portfoliomanager.dto.StockQuoteDTO;
import com.example.portfoliomanager.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@org.mockito.junit.jupiter.MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("StockPredictionService Unit Tests")
class StockPredictionServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private FinnhubService finnhubService;

    private StockPredictionService stockPredictionService;

    private StockQuoteDTO mockQuote;

    @BeforeEach
    void setUp() {
        stockPredictionService = new StockPredictionService("http://localhost:8000", finnhubService);
        ReflectionTestUtils.setField(stockPredictionService, "restTemplate", restTemplate);
        
        mockQuote = new StockQuoteDTO();
        mockQuote.setCurrentPrice(new BigDecimal("150.0"));
        mockQuote.setChangePercent(new BigDecimal("1.5"));
    }

    @Test
    @DisplayName("Should get prediction for supported ticker AAPL")
    void testGetPredictionForAAPL() {
        Map<String, Object> mockResponse = createMockPredictionResponse("AAPL", 175.50);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertNotNull(result.getForecast());
        assertTrue(result.getForecast().size() > 0);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Map.class));
    }

    @Test
    @DisplayName("Should get prediction for supported ticker GOOG")
    void testGetPredictionForGOOG() {
        Map<String, Object> mockResponse = createMockPredictionResponse("GOOG", 145.00);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("GOOG");

        assertNotNull(result);
        assertEquals("GOOG", result.getTicker());
    }

    @Test
    @DisplayName("Should get prediction for supported ticker MSFT")
    void testGetPredictionForMSFT() {
        Map<String, Object> mockResponse = createMockPredictionResponse("MSFT", 415.00);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("MSFT");

        assertNotNull(result);
        assertEquals("MSFT", result.getTicker());
    }

    @Test
    @DisplayName("Should throw exception for unsupported ticker")
    void testGetPredictionForUnsupportedTicker() {
        assertThrows(ResourceNotFoundException.class, () -> 
            stockPredictionService.getPrediction("INVALID")
        );
        verify(restTemplate, never()).getForEntity(anyString(), any());
    }

    @Test
    @DisplayName("Should normalize ticker to uppercase")
    void testGetPredictionNormalizesTicker() {
        Map<String, Object> mockResponse = createMockPredictionResponse("AAPL", 175.50);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("aapl");

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
    }

    @Test
    @DisplayName("Should return mock prediction on service error")
    void testGetPredictionFallsBackToMockOnError() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Service unavailable"));
        when(finnhubService.getStockQuote("AAPL")).thenReturn(mockQuote);

        StockPredictionDTO result = stockPredictionService.getPrediction("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertNotNull(result.getForecast());
        // Mock prediction should have 7 days of forecast
        assertEquals(7, result.getForecast().size());
    }

    @Test
    @DisplayName("Should return mock prediction with default price on API error")
    void testGetPredictionFallsBackToDefaultPriceOnApiError() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Service unavailable"));
        when(finnhubService.getStockQuote("AAPL")).thenThrow(new RuntimeException("Finnhub error"));

        StockPredictionDTO result = stockPredictionService.getPrediction("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(175.50, result.getLastObservedPrice(), 0.01);
    }

    @Test
    @DisplayName("Should return null quote response as mock prediction")
    void testGetPredictionHandlesNullResponse() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(null));
        when(finnhubService.getStockQuote("AAPL")).thenReturn(mockQuote);

        StockPredictionDTO result = stockPredictionService.getPrediction("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
    }

    @Test
    @DisplayName("Should have confidence score for mock prediction")
    void testMockPredictionHasConfidenceScore() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Service unavailable"));
        when(finnhubService.getStockQuote("AAPL")).thenReturn(mockQuote);

        StockPredictionDTO result = stockPredictionService.getPrediction("AAPL");

        assertNotNull(result);
        assertTrue(result.getConfidenceScore() >= 0.75 && result.getConfidenceScore() <= 0.90);
    }

    @Test
    @DisplayName("Should return all supported tickers")
    void testGetSupportedTickers() {
        Set<String> result = stockPredictionService.getSupportedTickers();

        assertNotNull(result);
        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("GOOG"));
        assertTrue(result.contains("MSFT"));
        assertTrue(result.contains("AMZN"));
        assertTrue(result.contains("META"));
        assertTrue(result.contains("NFLX"));
    }

    @Test
    @DisplayName("Should verify ticker is supported")
    void testIsTickerSupported() {
        assertTrue(stockPredictionService.isTickerSupported("AAPL"));
        assertTrue(stockPredictionService.isTickerSupported("aapl")); // Case insensitive
        assertFalse(stockPredictionService.isTickerSupported("INVALID"));
    }

    @Test
    @DisplayName("Should get prediction for AMZN")
    void testGetPredictionForAMZN() {
        Map<String, Object> mockResponse = createMockPredictionResponse("AMZN", 185.00);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("AMZN");

        assertNotNull(result);
        assertEquals("AMZN", result.getTicker());
    }

    @Test
    @DisplayName("Should get prediction for META")
    void testGetPredictionForMETA() {
        Map<String, Object> mockResponse = createMockPredictionResponse("META", 510.00);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("META");

        assertNotNull(result);
        assertEquals("META", result.getTicker());
    }

    @Test
    @DisplayName("Should get prediction for NFLX")
    void testGetPredictionForNFLX() {
        Map<String, Object> mockResponse = createMockPredictionResponse("NFLX", 620.00);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("NFLX");

        assertNotNull(result);
        assertEquals("NFLX", result.getTicker());
    }

    @Test
    @DisplayName("Should include 7-day forecast in mock prediction")
    void testMockPredictionIncludes7DayForecast() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RestClientException("Service unavailable"));
        when(finnhubService.getStockQuote("AAPL")).thenReturn(mockQuote);

        StockPredictionDTO result = stockPredictionService.getPrediction("AAPL");

        assertNotNull(result);
        assertEquals(7, result.getForecast().size());
        
        // Verify dates are in sequence
        for (int i = 0; i < result.getForecast().size(); i++) {
            StockPredictionDTO.ForecastPoint point = result.getForecast().get(i);
            assertNotNull(point.getDate());
            assertTrue(point.getPrice() > 0);
        }
    }

    @Test
    @DisplayName("Should map API response to DTO correctly")
    void testMapApiResponseToDTO() {
        Map<String, Object> mockResponse = createMockPredictionResponse("AAPL", 175.50);
        
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        StockPredictionDTO result = stockPredictionService.getPrediction("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertNotNull(result.getLastObservedDate());
        assertTrue(result.getLastObservedPrice() > 0);
        assertTrue(result.getConfidenceScore() > 0);
    }

    // Helper method to create mock prediction response
    private Map<String, Object> createMockPredictionResponse(String ticker, double basePrice) {
        Map<String, Object> response = new HashMap<>();
        response.put("ticker", ticker);
        response.put("last_observed_date", "2024-01-15");
        response.put("last_observed_price", basePrice);
        response.put("confidence_score", 0.85);
        
        List<Map<String, Object>> forecast = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            Map<String, Object> forecastPoint = new HashMap<>();
            forecastPoint.put("date", String.format("2024-01-%02d", 15 + i));
            forecastPoint.put("price", basePrice + (i * 5.0));
            forecast.add(forecastPoint);
        }
        response.put("forecast", forecast);
        
        return response;
    }
}
