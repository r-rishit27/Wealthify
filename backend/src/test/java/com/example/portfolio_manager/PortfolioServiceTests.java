package com.example.portfolio_manager;

import com.example.portfoliomanager.entity.Portfolio;
import com.example.portfoliomanager.repository.PortfolioRepository;
import com.example.portfoliomanager.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTests {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    private Portfolio testPortfolio;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio(
                "1",
                "Test Portfolio",
                "Test Description",
                "USD",
                10000.0
        );
    }

    @Test
    void testCreatePortfolioWithId() {
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);

        Portfolio result = portfolioService.createPortfolio(testPortfolio);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Test Portfolio", result.getPortfolioName());
        assertEquals(10000.0, result.getCashBalance());
        verify(portfolioRepository, times(1)).save(testPortfolio);
    }

    @Test
    void testCreatePortfolioWithoutId() {
        Portfolio portfolio = new Portfolio(
                null,
                "Test Portfolio",
                "Test Description",
                "USD",
                10000.0
        );

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);

        Portfolio result = portfolioService.createPortfolio(portfolio);

        assertNotNull(result);
        assertNotNull(result.getId());
        verify(portfolioRepository, times(1)).save(portfolio);
    }

    @Test
    void testGetAllPortfolios() {
        Portfolio portfolio2 = new Portfolio(
                "2",
                "Test Portfolio 2",
                "Test Description 2",
                "EUR",
                5000.0
        );

        List<Portfolio> portfolios = Arrays.asList(testPortfolio, portfolio2);
        when(portfolioRepository.findAll()).thenReturn(portfolios);

        List<Portfolio> result = portfolioService.getAllPortfolios();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Portfolio", result.get(0).getPortfolioName());
        assertEquals("Test Portfolio 2", result.get(1).getPortfolioName());
        verify(portfolioRepository, times(1)).findAll();
    }

    @Test
    void testGetPortfolioById() {
        when(portfolioRepository.findById("1")).thenReturn(Optional.of(testPortfolio));

        Optional<Portfolio> result = portfolioService.getPortfolioById("1");

        assertTrue(result.isPresent());
        assertEquals("Test Portfolio", result.get().getPortfolioName());
        verify(portfolioRepository, times(1)).findById("1");
    }

    @Test
    void testGetPortfolioByIdNotFound() {
        when(portfolioRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Portfolio> result = portfolioService.getPortfolioById("999");

        assertFalse(result.isPresent());
        verify(portfolioRepository, times(1)).findById("999");
    }

    @Test
    void testUpdatePortfolio() {
        Portfolio updatedDetails = new Portfolio(
                null,
                "Updated Portfolio",
                "Updated Description",
                "GBP",
                15000.0
        );

        when(portfolioRepository.findById("1")).thenReturn(Optional.of(testPortfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(testPortfolio);

        Portfolio result = portfolioService.updatePortfolio("1", updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Portfolio", result.getPortfolioName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("GBP", result.getBaseCurrency());
        assertEquals(15000.0, result.getCashBalance());
        verify(portfolioRepository, times(1)).findById("1");
        verify(portfolioRepository, times(1)).save(any(Portfolio.class));
    }

    @Test
    void testUpdatePortfolioNotFound() {
        Portfolio updatedDetails = new Portfolio(
                null,
                "Updated Portfolio",
                "Updated Description",
                "GBP",
                15000.0
        );

        when(portfolioRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            portfolioService.updatePortfolio("999", updatedDetails);
        });

        verify(portfolioRepository, times(1)).findById("999");
        verify(portfolioRepository, never()).save(any(Portfolio.class));
    }

    @Test
    void testDeletePortfolio() {
        doNothing().when(portfolioRepository).deleteById("1");

        portfolioService.deletePortfolio("1");

        verify(portfolioRepository, times(1)).deleteById("1");
    }

    @Test
    void testGetAllPortfoliosEmpty() {
        when(portfolioRepository.findAll()).thenReturn(Arrays.asList());

        List<Portfolio> result = portfolioService.getAllPortfolios();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(portfolioRepository, times(1)).findAll();
    }
}
