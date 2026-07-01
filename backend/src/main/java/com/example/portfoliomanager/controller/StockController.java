package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.dto.StockQuoteDTO;
import com.example.portfoliomanager.dto.CompanyProfileDTO;
import com.example.portfoliomanager.service.FinnhubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final FinnhubService finnhubService;

    public StockController(FinnhubService finnhubService) {
        this.finnhubService = finnhubService;
    }

    @GetMapping("/{symbol}/quote")
    public ResponseEntity<StockQuoteDTO> getQuote(@PathVariable String symbol) {
        StockQuoteDTO quote = finnhubService.getStockQuote(symbol);
        if (quote == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(quote);
    }

    @GetMapping("/{symbol}/profile")
    public ResponseEntity<CompanyProfileDTO> getCompanyProfile(@PathVariable String symbol) {
        CompanyProfileDTO profile = finnhubService.getCompanyProfile(symbol);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/quotes")
    public ResponseEntity<List<StockQuoteDTO>> getQuotes(@RequestParam List<String> symbols) {
        List<StockQuoteDTO> quotes = finnhubService.getQuotes(symbols);
        return ResponseEntity.ok(quotes);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchStocks(@RequestParam String query) {
        List<Map<String, Object>> results = finnhubService.searchStocks(query);
        return ResponseEntity.ok(results);
    }
}
