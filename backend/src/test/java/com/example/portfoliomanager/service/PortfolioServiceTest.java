package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.PortfolioDTO;
import com.example.portfoliomanager.dto.PortfolioSummaryDTO;
import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import com.example.portfoliomanager.entity.Portfolio;
import com.example.portfoliomanager.exception.ResourceNotFoundException;
import com.example.portfoliomanager.repository.AssetRepository;
import com.example.portfoliomanager.repository.PortfolioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PortfolioService Unit Tests")
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;
    private Asset testAsset;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio();
        testPortfolio.setId("portfolio-123");
        testPortfolio.setPortfolioName("My Portfolio");
        testPortfolio.setDescription("Test portfolio");
        testPortfolio.setBaseCurrency("USD");
        testPortfolio.setCashBalance(10000.0);

        testAsset = new Asset();
        testAsset.setId("asset-123");
        testAsset.setPortfolioId("portfolio-123");
        testAsset.setTicker("AAPL");
        testAsset.setAssetName("Apple Inc.");
        testAsset.setAssetType(AssetType.STOCK);
        testAsset.setQuantity(100);
        testAsset.setPurchasePrice(150.0);
        testAsset.setPurchaseDate(LocalDate.now());
    }

    @Test
    @DisplayName("Should create portfolio with generated UUID if ID is null")
    void testCreatePortfolioWithNullId() {
        Portfolio portfolioToCreate = new Portfolio();
        portfolioToCreate.setPortfolioName("New Portfolio");
        portfolioToCreate.setDescription("A new portfolio");
        portfolioToCreate.setBaseCurrency("USD");
        portfolioToCreate.setCashBalance(5000.0);

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);

        Portfolio result = portfolioService.createPortfolio(portfolioToCreate);

        assertNotNull(result);
        assertEquals("portfolio-123", result.getId());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    @DisplayName("Should create portfolio with provided ID")
    void testCreatePortfolioWithProvidedId() {
        Portfolio portfolioToCreate = new Portfolio();
        portfolioToCreate.setId("custom-portfolio-id");
        portfolioToCreate.setPortfolioName("Custom Portfolio");
        portfolioToCreate.setDescription("Custom portfolio");
        portfolioToCreate.setBaseCurrency("EUR");
        portfolioToCreate.setCashBalance(8000.0);

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolioToCreate);

        Portfolio result = portfolioService.createPortfolio(portfolioToCreate);

        assertNotNull(result);
        assertEquals("custom-portfolio-id", result.getId());
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    @DisplayName("Should retrieve all portfolios")
    void testGetAllPortfolios() {
        Portfolio portfolio2 = new Portfolio();
        portfolio2.setId("portfolio-456");
        portfolio2.setPortfolioName("Portfolio 2");

        List<Portfolio> portfolios = Arrays.asList(testPortfolio, portfolio2);
        when(portfolioRepository.findAll()).thenReturn(portfolios);

        List<Portfolio> result = portfolioService.getAllPortfolios();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("My Portfolio", result.get(0).getPortfolioName());
        verify(portfolioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve all portfolios with pagination")
    void testGetAllPortfoliosWithPagination() {
        Portfolio portfolio2 = new Portfolio();
        portfolio2.setId("portfolio-456");
        portfolio2.setPortfolioName("Portfolio 2");

        Pageable pageable = PageRequest.of(0, 10);
        List<Portfolio> content = Arrays.asList(testPortfolio, portfolio2);
        Page<Portfolio> page = new PageImpl<>(content, pageable, 2);

        when(portfolioRepository.findAll(pageable)).thenReturn(page);

        Page<Portfolio> result = portfolioService.getAllPortfolios(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(portfolioRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should retrieve portfolio by ID")
    void testGetPortfolioById() {
        when(portfolioRepository.findById("portfolio-123")).thenReturn(Optional.of(testPortfolio));

        Optional<Portfolio> result = portfolioService.getPortfolioById("portfolio-123");

        assertTrue(result.isPresent());
        assertEquals("My Portfolio", result.get().getPortfolioName());
        verify(portfolioRepository, times(1)).findById("portfolio-123");
    }

    @Test
    @DisplayName("Should return empty when portfolio not found by ID")
    void testGetPortfolioByIdNotFound() {
        when(portfolioRepository.findById("non-existent")).thenReturn(Optional.empty());

        Optional<Portfolio> result = portfolioService.getPortfolioById("non-existent");

        assertFalse(result.isPresent());
        verify(portfolioRepository, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("Should retrieve portfolio DTO by ID")
    void testGetPortfolioDTOById() {
        when(portfolioRepository.findById("portfolio-123")).thenReturn(Optional.of(testPortfolio));
        when(assetRepository.findByPortfolioId("portfolio-123")).thenReturn(Arrays.asList(testAsset));

        PortfolioDTO result = portfolioService.getPortfolioDTOById("portfolio-123");

        assertNotNull(result);
        assertEquals("portfolio-123", result.getPortfolioId());
        assertEquals("My Portfolio", result.getPortfolioName());
        assertEquals(1, result.getAssetCount());
        verify(portfolioRepository, times(1)).findById("portfolio-123");
    }

    @Test
    @DisplayName("Should throw exception when portfolio DTO not found")
    void testGetPortfolioDTOByIdNotFound() {
        when(portfolioRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.getPortfolioDTOById("non-existent"));
    }

    @Test
    @DisplayName("Should update portfolio successfully")
    void testUpdatePortfolio() {
        Portfolio updatedDetails = new Portfolio();
        updatedDetails.setPortfolioName("Updated Portfolio");
        updatedDetails.setDescription("Updated description");
        updatedDetails.setBaseCurrency("EUR");
        updatedDetails.setCashBalance(15000.0);

        Portfolio expectedResult = new Portfolio();
        expectedResult.setId("portfolio-123");
        expectedResult.setPortfolioName("Updated Portfolio");
        expectedResult.setDescription("Updated description");
        expectedResult.setBaseCurrency("EUR");
        expectedResult.setCashBalance(15000.0);

        when(portfolioRepository.findById("portfolio-123")).thenReturn(Optional.of(testPortfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(expectedResult);

        Portfolio result = portfolioService.updatePortfolio("portfolio-123", updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Portfolio", result.getPortfolioName());
        assertEquals("EUR", result.getBaseCurrency());
        assertEquals(15000.0, result.getCashBalance());
        verify(portfolioRepository, times(1)).findById("portfolio-123");
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent portfolio")
    void testUpdatePortfolioNotFound() {
        Portfolio updatedDetails = new Portfolio();
        when(portfolioRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> portfolioService.updatePortfolio("non-existent", updatedDetails));
        verify(portfolioRepository, never()).save(any(Portfolio.class));
    }

    @Test
    @DisplayName("Should delete portfolio by ID")
    void testDeletePortfolio() {
        portfolioService.deletePortfolio("portfolio-123");

        verify(portfolioRepository, times(1)).deleteById("portfolio-123");
    }

    @Test
    @DisplayName("Should get portfolio summary with assets")
    void testGetPortfolioSummary() {
        when(portfolioRepository.findById("portfolio-123")).thenReturn(Optional.of(testPortfolio));
        when(assetRepository.findByPortfolioId("portfolio-123")).thenReturn(Arrays.asList(testAsset));

        PortfolioSummaryDTO result = portfolioService.getPortfolioSummary("portfolio-123");

        assertNotNull(result);
        assertEquals("portfolio-123", result.getPortfolioId());
        assertEquals("My Portfolio", result.getPortfolioName());
        assertEquals(1, result.getAssetCount());
        assertEquals(BigDecimal.valueOf(10000.0), result.getCashBalance());
        verify(portfolioRepository, times(1)).findById("portfolio-123");
        verify(assetRepository, times(1)).findByPortfolioId("portfolio-123");
    }

    @Test
    @DisplayName("Should throw exception when portfolio summary not found")
    void testGetPortfolioSummaryNotFound() {
        when(portfolioRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.getPortfolioSummary("non-existent"));
    }

    @Test
    @DisplayName("Should retrieve all portfolios as DTO with pagination")
    void testGetAllPortfoliosAsDTOWithPagination() {
        Portfolio portfolio2 = new Portfolio();
        portfolio2.setId("portfolio-456");
        portfolio2.setPortfolioName("Portfolio 2");
        portfolio2.setBaseCurrency("USD");
        portfolio2.setCashBalance(5000.0);

        Pageable pageable = PageRequest.of(0, 10);
        List<Portfolio> content = Arrays.asList(testPortfolio, portfolio2);
        Page<Portfolio> page = new PageImpl<>(content, pageable, 2);

        when(portfolioRepository.findAll(pageable)).thenReturn(page);
        when(assetRepository.findByPortfolioId("portfolio-123")).thenReturn(Arrays.asList(testAsset));
        when(assetRepository.findByPortfolioId("portfolio-456")).thenReturn(Arrays.asList());

        Page<PortfolioDTO> result = portfolioService.getAllPortfoliosAsDTO(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(portfolioRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should handle portfolio with no assets")
    void testGetPortfolioSummaryWithNoAssets() {
        when(portfolioRepository.findById("portfolio-123")).thenReturn(Optional.of(testPortfolio));
        when(assetRepository.findByPortfolioId("portfolio-123")).thenReturn(Arrays.asList());

        PortfolioSummaryDTO result = portfolioService.getPortfolioSummary("portfolio-123");

        assertNotNull(result);
        assertEquals(0, result.getAssetCount());
        assertEquals(BigDecimal.valueOf(0), result.getAssetsValue());
        assertEquals(BigDecimal.valueOf(10000.0), result.getTotalValue());
    }

    @Test
    @DisplayName("Should handle portfolio with multiple assets")
    void testGetPortfolioSummaryWithMultipleAssets() {
        Asset asset2 = new Asset();
        asset2.setId("asset-456");
        asset2.setPortfolioId("portfolio-123");
        asset2.setTicker("GOOG");
        asset2.setAssetName("Google");
        asset2.setAssetType(AssetType.STOCK);
        asset2.setQuantity(50);
        asset2.setPurchasePrice(2800.0);

        when(portfolioRepository.findById("portfolio-123")).thenReturn(Optional.of(testPortfolio));
        when(assetRepository.findByPortfolioId("portfolio-123")).thenReturn(Arrays.asList(testAsset, asset2));

        PortfolioSummaryDTO result = portfolioService.getPortfolioSummary("portfolio-123");

        assertNotNull(result);
        assertEquals(2, result.getAssetCount());
        assertTrue(result.getTopHoldings().size() <= 5);
    }

    @Test
    @DisplayName("Should handle empty portfolio list")
    void testGetAllPortfoliosEmpty() {
        when(portfolioRepository.findAll()).thenReturn(Arrays.asList());

        List<Portfolio> result = portfolioService.getAllPortfolios();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(portfolioRepository, times(1)).findAll();
    }
}
