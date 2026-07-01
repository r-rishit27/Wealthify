package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.MarketDataDTO;
import com.example.portfoliomanager.entity.MarketData;
import com.example.portfoliomanager.exception.ResourceNotFoundException;
import com.example.portfoliomanager.repository.MarketDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MarketDataService {

    @Autowired
    private MarketDataRepository marketDataRepository;

    public MarketData createMarketData(MarketData marketData) {
        return marketDataRepository.save(marketData);
    }

    public List<MarketData> getAllMarketData() {
        return marketDataRepository.findAll();
    }

    public Optional<MarketData> getMarketDataById(Long id) {
        return marketDataRepository.findById(id);
    }

    public Optional<MarketData> getByTickerAndDate(String ticker, LocalDate date) {
        return marketDataRepository.findByTickerAndDate(ticker, date);
    }

    public List<MarketData> getByTicker(String ticker) {
        return marketDataRepository.findByTicker(ticker);
    }

    public List<MarketData> getByTickerAndDateRange(String ticker, LocalDate start, LocalDate end) {
        return marketDataRepository.findByTickerAndDateBetween(ticker, start, end);
    }

    public MarketData updateMarketData(Long id, MarketData details) {
        MarketData data = marketDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market data not found"));
        data.setTicker(details.getTicker());
        data.setDate(details.getDate());
        data.setOpenPrice(details.getOpenPrice());
        data.setHighPrice(details.getHighPrice());
        data.setLowPrice(details.getLowPrice());
        data.setClosePrice(details.getClosePrice());
        data.setVolume(details.getVolume());
        return marketDataRepository.save(data);
    }

    public void deleteMarketData(Long id) {
        marketDataRepository.deleteById(id);
    }

    // New methods for frontend compatibility

    @Transactional(readOnly = true)
    public MarketDataDTO getLatestPrice(String ticker) {
        MarketData marketData = marketDataRepository.findMostRecentByTicker(ticker.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("MarketData not found for ticker: " + ticker));
        return mapToDTO(marketData);
    }

    @Transactional(readOnly = true)
    public List<String> getAllTickers() {
        return marketDataRepository.findAllTickers();
    }

    @Transactional(readOnly = true)
    public List<MarketDataDTO> getMarketDataHistory(String ticker, LocalDate startDate, LocalDate endDate) {
        List<MarketData> data = marketDataRepository.findByTickerAndDateBetweenOrderByDateAsc(
                ticker.toUpperCase(), startDate, endDate);

        return data.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MarketDataDTO> getMarketDataByTicker(String ticker, Pageable pageable) {
        return marketDataRepository.findByTicker(ticker.toUpperCase(), pageable)
                .map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public List<MarketDataDTO> getLatestPrices(List<String> tickers) {
        return tickers.stream()
                .map(ticker -> marketDataRepository.findMostRecentByTicker(ticker.toUpperCase()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to map entity to DTO
    private MarketDataDTO mapToDTO(MarketData marketData) {
        BigDecimal change = marketData.getClosePrice().subtract(marketData.getOpenPrice());
        BigDecimal changePercent = marketData.getOpenPrice().compareTo(BigDecimal.ZERO) > 0
                ? change.divide(marketData.getOpenPrice(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return MarketDataDTO.builder()
                .id(marketData.getId())
                .ticker(marketData.getTicker())
                .date(marketData.getDate())
                .openPrice(marketData.getOpenPrice())
                .highPrice(marketData.getHighPrice())
                .lowPrice(marketData.getLowPrice())
                .closePrice(marketData.getClosePrice())
                .volume(marketData.getVolume())
                .change(change)
                .changePercent(changePercent)
                .build();
    }
}