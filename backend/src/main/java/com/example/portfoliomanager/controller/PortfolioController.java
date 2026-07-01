package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.dto.PortfolioDTO;
import com.example.portfoliomanager.dto.PortfolioSummaryDTO;
import com.example.portfoliomanager.entity.Portfolio;
import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.service.PortfolioService;
import com.example.portfoliomanager.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private AssetService assetService;

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody Portfolio portfolio) {
        Portfolio created = portfolioService.createPortfolio(portfolio);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<PortfolioDTO>> getAllPortfolios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PortfolioDTO> portfolios = portfolioService.getAllPortfoliosAsDTO(pageable);
        return new ResponseEntity<>(portfolios, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PortfolioDTO> getPortfolioById(@PathVariable String id) {
        PortfolioDTO portfolio = portfolioService.getPortfolioDTOById(id);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<PortfolioSummaryDTO> getPortfolioSummary(@PathVariable String id) {
        PortfolioSummaryDTO summary = portfolioService.getPortfolioSummary(id);
        return ResponseEntity.ok(summary);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Portfolio> updatePortfolio(@PathVariable String id, @RequestBody Portfolio portfolioDetails) {
        try {
            Portfolio updated = portfolioService.updatePortfolio(id, portfolioDetails);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable String id) {
        portfolioService.deletePortfolio(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}/assets")
    public ResponseEntity<List<Asset>> getAssetsByPortfolio(@PathVariable String id) {
        List<Asset> assets = assetService.getAssetsByPortfolioId(id);
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }
}
