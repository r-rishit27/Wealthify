package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.dto.StockPredictionDTO;
import com.example.portfoliomanager.service.StockPredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/predictions")
public class PredictionController {

    private final StockPredictionService stockPredictionService;

    public PredictionController(StockPredictionService stockPredictionService) {
        this.stockPredictionService = stockPredictionService;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<StockPredictionDTO> getPrediction(@PathVariable String ticker) {
        StockPredictionDTO prediction = stockPredictionService.getPrediction(ticker);
        return ResponseEntity.ok(prediction);
    }

    @GetMapping("/supported-tickers")
    public ResponseEntity<Map<String, Object>> getSupportedTickers() {
        Set<String> tickers = stockPredictionService.getSupportedTickers();
        return ResponseEntity.ok(Map.of(
            "supportedTickers", tickers,
            "count", tickers.size()
        ));
    }

    @GetMapping("/check/{ticker}")
    public ResponseEntity<Map<String, Object>> checkTickerSupport(@PathVariable String ticker) {
        boolean supported = stockPredictionService.isTickerSupported(ticker);
        return ResponseEntity.ok(Map.of(
            "ticker", ticker.toUpperCase(),
            "supported", supported
        ));
    }
}
