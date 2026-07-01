package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.dto.FinnhubCandleDTO;
import com.example.portfoliomanager.dto.FinnhubQuoteDTO;
import com.example.portfoliomanager.service.FinnhubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finnhub")
public class FinnhubController {

    private final FinnhubService finnhubService;

    public FinnhubController(FinnhubService finnhubService) {
        this.finnhubService = finnhubService;
    }

    @GetMapping("/quote")
    public FinnhubQuoteDTO quote(@RequestParam String symbol) {
        return finnhubService.getQuote(symbol);
    }

    @GetMapping("/candles")
    public FinnhubCandleDTO candles(
            @RequestParam String symbol,
            @RequestParam String resolution,
            @RequestParam long from,
            @RequestParam long to
    ) {
        return finnhubService.getCandles(symbol, resolution, from, to);
    }
}
