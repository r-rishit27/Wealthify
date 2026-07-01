package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.dto.MarketDataDTO;
import com.example.portfoliomanager.entity.MarketData;
import com.example.portfoliomanager.service.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market-data")
public class MarketDataController {

    @Autowired
    private MarketDataService marketDataService;

    @PostMapping
    public ResponseEntity<MarketData> create(@RequestBody MarketData marketData) {
        MarketData created = marketDataService.createMarketData(marketData);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MarketData>> getAll(
            @RequestParam(required = false) String ticker,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end
    ) {
        if (ticker != null && start != null && end != null) {
            return new ResponseEntity<>(marketDataService.getByTickerAndDateRange(ticker, start, end), HttpStatus.OK);
        }
        if (ticker != null) {
            return new ResponseEntity<>(marketDataService.getByTicker(ticker), HttpStatus.OK);
        }
        return new ResponseEntity<>(marketDataService.getAllMarketData(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarketData> getById(@PathVariable Long id) {
        return marketDataService.getMarketDataById(id)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-date")
    public ResponseEntity<MarketData> getByTickerAndDate(
            @RequestParam String ticker,
            @RequestParam LocalDate date
    ) {
        return marketDataService.getByTickerAndDate(ticker, date)
                .map(data -> new ResponseEntity<>(data, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarketData> update(@PathVariable Long id, @RequestBody MarketData details) {
        try {
            MarketData updated = marketDataService.updateMarketData(id, details);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        marketDataService.deleteMarketData(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // New endpoints for frontend compatibility

    @GetMapping("/tickers")
    public ResponseEntity<List<String>> getAllTickers() {
        List<String> tickers = marketDataService.getAllTickers();
        return ResponseEntity.ok(tickers);
    }

    @GetMapping("/{ticker}/latest")
    public ResponseEntity<MarketDataDTO> getLatestPrice(@PathVariable String ticker) {
        MarketDataDTO data = marketDataService.getLatestPrice(ticker);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{ticker}/history")
    public ResponseEntity<List<MarketDataDTO>> getMarketDataHistory(
            @PathVariable String ticker,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<MarketDataDTO> data = marketDataService.getMarketDataHistory(ticker, startDate, endDate);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{ticker}/data")
    public ResponseEntity<Page<MarketDataDTO>> getMarketDataByTicker(
            @PathVariable String ticker,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MarketDataDTO> data = marketDataService.getMarketDataByTicker(ticker, pageable);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<MarketDataDTO>> getLatestPrices(@RequestParam List<String> tickers) {
        List<MarketDataDTO> data = marketDataService.getLatestPrices(tickers);
        return ResponseEntity.ok(data);
    }
}