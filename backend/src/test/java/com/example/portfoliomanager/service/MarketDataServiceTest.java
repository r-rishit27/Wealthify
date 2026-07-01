package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.MarketDataDTO;
import com.example.portfoliomanager.entity.MarketData;
import com.example.portfoliomanager.exception.ResourceNotFoundException;
import com.example.portfoliomanager.repository.MarketDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MarketDataService Unit Tests")
class MarketDataServiceTest {

    @Mock
    private MarketDataRepository marketDataRepository;

    @InjectMocks
    private MarketDataService marketDataService;

    private MarketData testMarketData;

    @BeforeEach
    void setUp() {
        testMarketData = new MarketData();
        testMarketData.setId(1L);
        testMarketData.setTicker("AAPL");
        testMarketData.setDate(LocalDate.now());
        testMarketData.setOpenPrice(BigDecimal.valueOf(150.0));
        testMarketData.setHighPrice(BigDecimal.valueOf(152.0));
        testMarketData.setLowPrice(BigDecimal.valueOf(148.0));
        testMarketData.setClosePrice(BigDecimal.valueOf(151.5));
        testMarketData.setVolume(1000000L);
    }

    @Test
    @DisplayName("Should create market data")
    void testCreateMarketData() {
        when(marketDataRepository.save(any(MarketData.class))).thenReturn(testMarketData);

        MarketData result = marketDataService.createMarketData(testMarketData);

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(BigDecimal.valueOf(151.5), result.getClosePrice());
        verify(marketDataRepository, times(1)).save(any(MarketData.class));
    }

    @Test
    @DisplayName("Should retrieve all market data")
    void testGetAllMarketData() {
        MarketData marketData2 = new MarketData();
        marketData2.setId(2L);
        marketData2.setTicker("MSFT");

        List<MarketData> dataList = Arrays.asList(testMarketData, marketData2);
        when(marketDataRepository.findAll()).thenReturn(dataList);

        List<MarketData> result = marketDataService.getAllMarketData();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(marketDataRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve market data by ID")
    void testGetMarketDataById() {
        when(marketDataRepository.findById(1L)).thenReturn(Optional.of(testMarketData));

        Optional<MarketData> result = marketDataService.getMarketDataById(1L);

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getTicker());
        verify(marketDataRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when market data not found by ID")
    void testGetMarketDataByIdNotFound() {
        when(marketDataRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<MarketData> result = marketDataService.getMarketDataById(999L);

        assertFalse(result.isPresent());
        verify(marketDataRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should retrieve market data by ticker and date")
    void testGetByTickerAndDate() {
        when(marketDataRepository.findByTickerAndDate("AAPL", LocalDate.now()))
                .thenReturn(Optional.of(testMarketData));

        Optional<MarketData> result = marketDataService.getByTickerAndDate("AAPL", LocalDate.now());

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getTicker());
        verify(marketDataRepository, times(1)).findByTickerAndDate("AAPL", LocalDate.now());
    }

    @Test
    @DisplayName("Should retrieve market data by ticker")
    void testGetByTicker() {
        MarketData marketData2 = new MarketData();
        marketData2.setId(2L);
        marketData2.setTicker("AAPL");
        marketData2.setDate(LocalDate.now().minusDays(1));

        List<MarketData> dataList = Arrays.asList(testMarketData, marketData2);
        when(marketDataRepository.findByTicker("AAPL")).thenReturn(dataList);

        List<MarketData> result = marketDataService.getByTicker("AAPL");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d.getTicker().equals("AAPL")));
        verify(marketDataRepository, times(1)).findByTicker("AAPL");
    }

    @Test
    @DisplayName("Should retrieve market data by ticker and date range")
    void testGetByTickerAndDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        List<MarketData> dataList = Arrays.asList(testMarketData);
        when(marketDataRepository.findByTickerAndDateBetween("AAPL", startDate, endDate))
                .thenReturn(dataList);

        List<MarketData> result = marketDataService.getByTickerAndDateRange("AAPL", startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(marketDataRepository, times(1)).findByTickerAndDateBetween("AAPL", startDate, endDate);
    }

    @Test
    @DisplayName("Should update market data successfully")
    void testUpdateMarketData() {
        MarketData updatedDetails = new MarketData();
        updatedDetails.setTicker("AAPL");
        updatedDetails.setDate(LocalDate.now());
        updatedDetails.setOpenPrice(BigDecimal.valueOf(155.0));
        updatedDetails.setHighPrice(BigDecimal.valueOf(157.0));
        updatedDetails.setLowPrice(BigDecimal.valueOf(153.0));
        updatedDetails.setClosePrice(BigDecimal.valueOf(156.0));
        updatedDetails.setVolume(1500000L);

        MarketData expectedResult = new MarketData();
        expectedResult.setId(1L);
        expectedResult.setTicker("AAPL");
        expectedResult.setDate(LocalDate.now());
        expectedResult.setOpenPrice(BigDecimal.valueOf(155.0));
        expectedResult.setHighPrice(BigDecimal.valueOf(157.0));
        expectedResult.setLowPrice(BigDecimal.valueOf(153.0));
        expectedResult.setClosePrice(BigDecimal.valueOf(156.0));
        expectedResult.setVolume(1500000L);

        when(marketDataRepository.findById(1L)).thenReturn(Optional.of(testMarketData));
        when(marketDataRepository.save(any(MarketData.class))).thenReturn(expectedResult);

        MarketData result = marketDataService.updateMarketData(1L, updatedDetails);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(156.0), result.getClosePrice());
        assertEquals(1500000L, result.getVolume());
        verify(marketDataRepository, times(1)).findById(1L);
        verify(marketDataRepository, times(1)).save(any(MarketData.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent market data")
    void testUpdateMarketDataNotFound() {
        MarketData updatedDetails = new MarketData();
        when(marketDataRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> marketDataService.updateMarketData(999L, updatedDetails));
        verify(marketDataRepository, never()).save(any(MarketData.class));
    }

    @Test
    @DisplayName("Should delete market data by ID")
    void testDeleteMarketData() {
        marketDataService.deleteMarketData(1L);

        verify(marketDataRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should get latest price for ticker")
    void testGetLatestPrice() {
        when(marketDataRepository.findMostRecentByTicker("AAPL"))
                .thenReturn(Optional.of(testMarketData));

        MarketDataDTO result = marketDataService.getLatestPrice("aapl");

        assertNotNull(result);
        assertEquals("AAPL", result.getTicker());
        assertEquals(BigDecimal.valueOf(151.5), result.getClosePrice());
    }

    @Test
    @DisplayName("Should throw exception when latest price not found")
    void testGetLatestPriceNotFound() {
        when(marketDataRepository.findMostRecentByTicker("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> marketDataService.getLatestPrice("INVALID"));
    }

    @Test
    @DisplayName("Should get all tickers")
    void testGetAllTickers() {
        List<String> tickers = Arrays.asList("AAPL", "MSFT", "GOOG");
        when(marketDataRepository.findAllTickers()).thenReturn(tickers);

        List<String> result = marketDataService.getAllTickers();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("AAPL"));
        verify(marketDataRepository, times(1)).findAllTickers();
    }

    @Test
    @DisplayName("Should get market data history")
    void testGetMarketDataHistory() {
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        List<MarketData> dataList = Arrays.asList(testMarketData);
        when(marketDataRepository.findByTickerAndDateBetweenOrderByDateAsc("AAPL", startDate, endDate))
                .thenReturn(dataList);

        List<MarketDataDTO> result = marketDataService.getMarketDataHistory("aapl", startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
    }

    @Test
    @DisplayName("Should get market data by ticker with pagination")
    void testGetMarketDataByTicker() {
        Pageable pageable = PageRequest.of(0, 10);
        List<MarketData> content = Arrays.asList(testMarketData);
        Page<MarketData> page = new PageImpl<>(content, pageable, 1);

        when(marketDataRepository.findByTicker("AAPL", pageable)).thenReturn(page);

        Page<MarketDataDTO> result = marketDataService.getMarketDataByTicker("AAPL", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("AAPL", result.getContent().get(0).getTicker());
    }

    @Test
    @DisplayName("Should get latest prices for multiple tickers")
    void testGetLatestPrices() {
        MarketData marketData2 = new MarketData();
        marketData2.setId(2L);
        marketData2.setTicker("MSFT");
        marketData2.setDate(LocalDate.now());
        marketData2.setOpenPrice(BigDecimal.valueOf(295.0));
        marketData2.setHighPrice(BigDecimal.valueOf(305.0));
        marketData2.setLowPrice(BigDecimal.valueOf(290.0));
        marketData2.setClosePrice(BigDecimal.valueOf(300.0));
        marketData2.setVolume(1000000L);

        when(marketDataRepository.findMostRecentByTicker("AAPL"))
                .thenReturn(Optional.of(testMarketData));
        when(marketDataRepository.findMostRecentByTicker("MSFT"))
                .thenReturn(Optional.of(marketData2));

        List<String> tickers = Arrays.asList("AAPL", "MSFT");
        List<MarketDataDTO> result = marketDataService.getLatestPrices(tickers);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should filter null results when getting latest prices for multiple tickers")
    void testGetLatestPricesFiltersNull() {
        when(marketDataRepository.findMostRecentByTicker("AAPL"))
                .thenReturn(Optional.of(testMarketData));
        when(marketDataRepository.findMostRecentByTicker("INVALID"))
                .thenReturn(Optional.empty());

        List<String> tickers = Arrays.asList("AAPL", "INVALID");
        List<MarketDataDTO> result = marketDataService.getLatestPrices(tickers);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should calculate change percentage in DTO")
    void testMarketDataDTOCalculatesChangePercent() {
        MarketData marketData = new MarketData();
        marketData.setId(1L);
        marketData.setTicker("AAPL");
        marketData.setOpenPrice(BigDecimal.valueOf(100.0));
        marketData.setClosePrice(BigDecimal.valueOf(110.0));

        when(marketDataRepository.findMostRecentByTicker("AAPL"))
                .thenReturn(Optional.of(marketData));

        MarketDataDTO result = marketDataService.getLatestPrice("AAPL");

        assertNotNull(result);
        assertTrue(result.getChangePercent().doubleValue() > 0);
    }

    @Test
    @DisplayName("Should handle market data with zero open price")
    void testMarketDataWithZeroOpenPrice() {
        MarketData marketData = new MarketData();
        marketData.setId(1L);
        marketData.setTicker("TEST");
        marketData.setOpenPrice(BigDecimal.ZERO);
        marketData.setClosePrice(BigDecimal.valueOf(100.0));

        when(marketDataRepository.findMostRecentByTicker("TEST"))
                .thenReturn(Optional.of(marketData));

        MarketDataDTO result = marketDataService.getLatestPrice("TEST");

        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getChangePercent());
    }
}
