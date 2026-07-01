package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.StockQuoteDTO;
import com.example.portfoliomanager.dto.CompanyProfileDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FinnhubService Unit Tests")
class FinnhubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FinnhubService finnhubService;

    @Test
    @DisplayName("Should get stock quote for AAPL")
    void testGetStockQuoteAAPL() {
        Map<String, Object> quoteResponse = new HashMap<>();
        quoteResponse.put("c", 150.0);
        quoteResponse.put("h", 152.0);
        quoteResponse.put("l", 148.0);

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(quoteResponse);

        StockQuoteDTO result = finnhubService.getStockQuote("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertEquals(BigDecimal.valueOf(150.0), result.getCurrentPrice());
    }

    @Test
    @DisplayName("Should normalize ticker to uppercase")
    void testGetStockQuoteNormalizesCase() {
        Map<String, Object> quoteResponse = new HashMap<>();
        quoteResponse.put("c", 150.0);

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(quoteResponse);

        StockQuoteDTO result = finnhubService.getStockQuote("aapl");

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
    }

    @Test
    @DisplayName("Should return null on error fetching quote")
    void testGetStockQuoteError() {
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenThrow(new RuntimeException("API Error"));

        StockQuoteDTO result = finnhubService.getStockQuote("AAPL");

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when quote response is null")
    void testGetStockQuoteNullResponse() {
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(null);

        StockQuoteDTO result = finnhubService.getStockQuote("AAPL");

        assertNull(result);
    }

    @Test
    @DisplayName("Should get company profile")
    void testGetCompanyProfile() {
        Map<String, Object> profileResponse = new HashMap<>();
        profileResponse.put("name", "Apple Inc.");
        profileResponse.put("country", "US");
        profileResponse.put("currency", "USD");
        profileResponse.put("exchange", "NASDAQ");

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(profileResponse);

        CompanyProfileDTO result = finnhubService.getCompanyProfile("AAPL");

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertEquals("Apple Inc.", result.getName());
    }

    @Test
    @DisplayName("Should return null on company profile error")
    void testGetCompanyProfileError() {
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenThrow(new RuntimeException("API Error"));

        CompanyProfileDTO result = finnhubService.getCompanyProfile("INVALID");

        assertNull(result);
    }

    @Test
    @DisplayName("Should get quotes for multiple symbols")
    void testGetQuotesMultiple() {
        Map<String, Object> quoteResponse = new HashMap<>();
        quoteResponse.put("c", 150.0);

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(quoteResponse);

        List<String> symbols = Arrays.asList("AAPL", "MSFT", "GOOG");
        List<StockQuoteDTO> result = finnhubService.getQuotes(symbols);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should search stocks")
    void testSearchStocks() {
        List<Map<String, Object>> searchResults = new ArrayList<>();
        Map<String, Object> result1 = new HashMap<>();
        result1.put("symbol", "AAPL");
        searchResults.add(result1);

        Map<String, Object> response = new HashMap<>();
        response.put("result", searchResults);

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(response);

        List<Map<String, Object>> result = finnhubService.searchStocks("Apple");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should return empty list on search error")
    void testSearchStocksError() {
        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenThrow(new RuntimeException("API Error"));

        List<Map<String, Object>> result = finnhubService.searchStocks("Test");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should handle missing fields in quote response")
    void testGetStockQuoteMissingFields() {
        Map<String, Object> minimalResponse = new HashMap<>();
        minimalResponse.put("c", 150.0);

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(minimalResponse);

        StockQuoteDTO result = finnhubService.getStockQuote("AAPL");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150.0), result.getCurrentPrice());
    }

    @Test
    @DisplayName("Should set timestamp on stock quote")
    void testGetStockQuoteSetsTimestamp() {
        Map<String, Object> quoteResponse = new HashMap<>();
        quoteResponse.put("c", 150.0);

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(quoteResponse);

        StockQuoteDTO result = finnhubService.getStockQuote("AAPL");

        assertNotNull(result);
        assertTrue(result.getTimestamp() > 0);
    }

    @Test
    @DisplayName("Should handle BigDecimal conversion from Number")
    void testBigDecimalConversion() {
        Map<String, Object> response = new HashMap<>();
        response.put("c", 150);
        response.put("d", 1);
        response.put("dp", 0.67);

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(response);

        StockQuoteDTO result = finnhubService.getStockQuote("AAPL");

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(150.0), result.getCurrentPrice());
    }
}
