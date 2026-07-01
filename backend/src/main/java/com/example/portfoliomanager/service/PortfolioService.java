package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.PortfolioDTO;
import com.example.portfoliomanager.dto.PortfolioSummaryDTO;
import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.Portfolio;
import com.example.portfoliomanager.exception.ResourceNotFoundException;
import com.example.portfoliomanager.repository.AssetRepository;
import com.example.portfoliomanager.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AssetRepository assetRepository;

    public Portfolio createPortfolio(Portfolio portfolio) {
        if (portfolio.getId() == null || portfolio.getId().isEmpty()) {
            portfolio.setId(UUID.randomUUID().toString());
        }
        return portfolioRepository.save(portfolio);
    }

    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    public Page<PortfolioDTO> getAllPortfoliosAsDTO(Pageable pageable) {
        Page<Portfolio> portfolioPage = portfolioRepository.findAll(pageable);
        List<PortfolioDTO> dtos = portfolioPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, portfolioPage.getTotalElements());
    }

    public Page<Portfolio> getAllPortfolios(Pageable pageable) {
        return portfolioRepository.findAll(pageable);
    }

    public Optional<Portfolio> getPortfolioById(String id) {
        return portfolioRepository.findById(id);
    }

    public PortfolioDTO getPortfolioDTOById(String id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        return mapToDTO(portfolio);
    }

    private PortfolioDTO mapToDTO(Portfolio portfolio) {
        List<Asset> assets = assetRepository.findByPortfolioId(portfolio.getId());
        
        BigDecimal cashBalance = BigDecimal.valueOf(portfolio.getCashBalance() != null ? portfolio.getCashBalance() : 0);
        BigDecimal assetsValue = BigDecimal.ZERO;
        
        for (Asset asset : assets) {
            int qty = asset.getQuantity() != null ? asset.getQuantity() : 0;
            double price = asset.getPurchasePrice() != null ? asset.getPurchasePrice() : 0;
            assetsValue = assetsValue.add(BigDecimal.valueOf(qty * price));
        }
        
        BigDecimal totalValue = cashBalance.add(assetsValue);
        
        PortfolioDTO dto = new PortfolioDTO();
        dto.setPortfolioId(portfolio.getId());
        dto.setPortfolioName(portfolio.getPortfolioName());
        dto.setDescription(portfolio.getDescription());
        dto.setBaseCurrency(portfolio.getBaseCurrency());
        dto.setCashBalance(cashBalance);
        dto.setAssetsValue(assetsValue);
        dto.setTotalValue(totalValue);
        dto.setAssetCount(assets.size());
        
        return dto;
    }

    public Portfolio updatePortfolio(String id, Portfolio portfolioDetails) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(() -> new RuntimeException("Portfolio not found"));
        portfolio.setPortfolioName(portfolioDetails.getPortfolioName());
        portfolio.setDescription(portfolioDetails.getDescription());
        portfolio.setBaseCurrency(portfolioDetails.getBaseCurrency());
        portfolio.setCashBalance(portfolioDetails.getCashBalance());
        return portfolioRepository.save(portfolio);
    }

    public void deletePortfolio(String id) {
        portfolioRepository.deleteById(id);
    }

    public PortfolioSummaryDTO getPortfolioSummary(String portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));

        List<Asset> assets = assetRepository.findByPortfolioId(portfolioId);

        PortfolioSummaryDTO summary = new PortfolioSummaryDTO();
        summary.setPortfolioId(portfolio.getId());
        summary.setPortfolioName(portfolio.getPortfolioName());
        summary.setBaseCurrency(portfolio.getBaseCurrency());
        summary.setCashBalance(BigDecimal.valueOf(portfolio.getCashBalance() != null ? portfolio.getCashBalance() : 0));

        // Calculate assets value and gains
        BigDecimal assetsValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        Map<String, BigDecimal> allocationByType = new HashMap<>();

        List<PortfolioSummaryDTO.AssetHolding> holdings = new ArrayList<>();

        for (Asset asset : assets) {
            int qty = asset.getQuantity() != null ? asset.getQuantity() : 0;
            double purchasePrice = asset.getPurchasePrice() != null ? asset.getPurchasePrice() : 0;
            // Use purchase price as current price for now (could be enhanced with market data)
            double currentPrice = purchasePrice;
            
            BigDecimal quantity = BigDecimal.valueOf(qty);
            BigDecimal currentPriceBD = BigDecimal.valueOf(currentPrice);
            BigDecimal purchasePriceBD = BigDecimal.valueOf(purchasePrice);

            BigDecimal currentValue = quantity.multiply(currentPriceBD);
            BigDecimal cost = quantity.multiply(purchasePriceBD);
            BigDecimal gain = currentValue.subtract(cost);
            BigDecimal gainPercent = cost.compareTo(BigDecimal.ZERO) > 0 
                    ? gain.divide(cost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            assetsValue = assetsValue.add(currentValue);
            totalCost = totalCost.add(cost);

            // Add to allocation by type
            String type = asset.getAssetType() != null ? asset.getAssetType().name() : "OTHER";
            allocationByType.merge(type, currentValue, BigDecimal::add);

            // Create holding
            PortfolioSummaryDTO.AssetHolding holding = new PortfolioSummaryDTO.AssetHolding();
            holding.setAssetId(asset.getId());
            holding.setTicker(asset.getTicker());
            holding.setAssetName(asset.getAssetName());
            holding.setAssetType(type);
            holding.setQuantity(quantity);
            holding.setCurrentPrice(currentPriceBD);
            holding.setTotalValue(currentValue);
            holding.setGainLoss(gain);
            holding.setGainLossPercentage(gainPercent);
            holdings.add(holding);
        }

        // Calculate total value
        BigDecimal totalValue = assetsValue.add(summary.getCashBalance());
        summary.setTotalValue(totalValue);
        summary.setAssetsValue(assetsValue);

        // Calculate total gain
        BigDecimal totalGain = assetsValue.subtract(totalCost);
        BigDecimal totalGainPercent = totalCost.compareTo(BigDecimal.ZERO) > 0
                ? totalGain.divide(totalCost, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        summary.setTotalGain(totalGain);
        summary.setTotalGainPercent(totalGainPercent);

        summary.setAssetCount(assets.size());
        summary.setTransactionCount(0); // No transactions in current model

        // Calculate allocation percentages
        final BigDecimal finalTotalValue = totalValue;
        for (PortfolioSummaryDTO.AssetHolding holding : holdings) {
            BigDecimal allocation = finalTotalValue.compareTo(BigDecimal.ZERO) > 0
                    ? holding.getTotalValue().divide(finalTotalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            holding.setAllocation(allocation);
        }

        // Sort by value and take top 5
        holdings.sort((a, b) -> b.getTotalValue().compareTo(a.getTotalValue()));
        summary.setTopHoldings(holdings.stream().limit(5).collect(Collectors.toList()));

        // Build allocation list including CASH
        List<PortfolioSummaryDTO.AllocationItem> allocation = allocationByType.entrySet().stream()
                .map(e -> {
                    BigDecimal percentage = finalTotalValue.compareTo(BigDecimal.ZERO) > 0
                            ? e.getValue().divide(finalTotalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                            : BigDecimal.ZERO;
                    return new PortfolioSummaryDTO.AllocationItem(e.getKey(), e.getValue(), percentage);
                })
                .collect(Collectors.toList());
        
        // Add CASH to allocation if there's cash balance
        if (summary.getCashBalance().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal cashPercentage = finalTotalValue.compareTo(BigDecimal.ZERO) > 0
                    ? summary.getCashBalance().divide(finalTotalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            allocation.add(new PortfolioSummaryDTO.AllocationItem("CASH", summary.getCashBalance(), cashPercentage));
        }
        
        summary.setAllocation(allocation);

        return summary;
    }
}
