package com.example.portfolio_manager;

import com.example.portfoliomanager.controller.PortfolioController;
import com.example.portfoliomanager.dto.PortfolioDTO;
import com.example.portfoliomanager.entity.Portfolio;
import com.example.portfoliomanager.exception.ResourceNotFoundException;
import com.example.portfoliomanager.service.PortfolioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioControllerTests {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private PortfolioController portfolioController;

    private Portfolio testPortfolio;
    private PortfolioDTO testPortfolioDTO;

    @BeforeEach
    void setUp() {
        testPortfolio = new Portfolio(
                "1",
                "Test Portfolio",
                "Test Description",
                "USD",
                10000.0
        );
        testPortfolioDTO = new PortfolioDTO();
        testPortfolioDTO.setPortfolioId("1");
        testPortfolioDTO.setPortfolioName("Test Portfolio");
        testPortfolioDTO.setDescription("Test Description");
        testPortfolioDTO.setBaseCurrency("USD");
        testPortfolioDTO.setTotalValue(BigDecimal.valueOf(10000.0));
    }

    @Test
    void testCreatePortfolio() {
        when(portfolioService.createPortfolio(any(Portfolio.class))).thenReturn(testPortfolio);

        ResponseEntity<Portfolio> response = portfolioController.createPortfolio(testPortfolio);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testPortfolio, response.getBody());
        verify(portfolioService, times(1)).createPortfolio(any(Portfolio.class));
    }

    @Test
    void testGetAllPortfolios() {
        PortfolioDTO dto2 = new PortfolioDTO();
        dto2.setPortfolioId("2");
        dto2.setPortfolioName("Portfolio 2");
        dto2.setDescription("Description 2");
        dto2.setBaseCurrency("EUR");
        List<PortfolioDTO> dtos = Arrays.asList(testPortfolioDTO, dto2);
        Page<PortfolioDTO> page = new PageImpl<>(dtos);
        when(portfolioService.getAllPortfoliosAsDTO(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<PortfolioDTO>> response = portfolioController.getAllPortfolios(0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getContent().size());
        verify(portfolioService, times(1)).getAllPortfoliosAsDTO(any(Pageable.class));
    }

    @Test
    void testGetPortfolioById() {
        when(portfolioService.getPortfolioDTOById("1")).thenReturn(testPortfolioDTO);

        ResponseEntity<PortfolioDTO> response = portfolioController.getPortfolioById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1", response.getBody().getPortfolioId());
        assertEquals("Test Portfolio", response.getBody().getPortfolioName());
        verify(portfolioService, times(1)).getPortfolioDTOById("1");
    }

    @Test
    void testGetPortfolioByIdNotFound() {
        when(portfolioService.getPortfolioDTOById("999"))
                .thenThrow(new ResourceNotFoundException("Portfolio not found with id: 999"));

        assertThrows(ResourceNotFoundException.class, () -> portfolioController.getPortfolioById("999"));
        verify(portfolioService, times(1)).getPortfolioDTOById("999");
    }

    @Test
    void testUpdatePortfolio() {
        Portfolio updatedPortfolio = new Portfolio(
                "1",
                "Updated Portfolio",
                "Updated Description",
                "GBP",
                15000.0
        );

        when(portfolioService.updatePortfolio("1", updatedPortfolio)).thenReturn(updatedPortfolio);

        ResponseEntity<Portfolio> response = portfolioController.updatePortfolio("1", updatedPortfolio);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Updated Portfolio", response.getBody().getPortfolioName());
        verify(portfolioService, times(1)).updatePortfolio("1", updatedPortfolio);
    }

    @Test
    void testUpdatePortfolioThrowsException() {
        Portfolio updatedPortfolio = new Portfolio(
                "1",
                "Updated Portfolio",
                "Updated Description",
                "GBP",
                15000.0
        );

        when(portfolioService.updatePortfolio("999", updatedPortfolio))
                .thenThrow(new RuntimeException("Portfolio not found"));

        // Controller should propagate the exception from service
        ResponseEntity<Portfolio> response = portfolioController.updatePortfolio("999", updatedPortfolio);

        // Verify the exception was thrown from service
        verify(portfolioService, times(1)).updatePortfolio("999", updatedPortfolio);
    }

    @Test
    void testDeletePortfolio() {
        doNothing().when(portfolioService).deletePortfolio("1");

        ResponseEntity<Void> response = portfolioController.deletePortfolio("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(portfolioService, times(1)).deletePortfolio("1");
    }

    @Test
    void testGetAllPortfoliosEmpty() {
        Page<PortfolioDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        when(portfolioService.getAllPortfoliosAsDTO(any(Pageable.class))).thenReturn(emptyPage);

        ResponseEntity<Page<PortfolioDTO>> response = portfolioController.getAllPortfolios(0, 20);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());
        verify(portfolioService, times(1)).getAllPortfoliosAsDTO(any(Pageable.class));
    }
}
