package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.TransactionDTO;
import com.example.portfoliomanager.entity.Transaction;
import com.example.portfoliomanager.entity.TransactionStatus;
import com.example.portfoliomanager.entity.TransactionType;
import com.example.portfoliomanager.exception.ValidationException;
import com.example.portfoliomanager.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Unit Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionDTO validTransactionDTO;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        validTransactionDTO = new TransactionDTO();
        validTransactionDTO.setPortfolioId("portfolio-123");
        validTransactionDTO.setTicker("AAPL");
        validTransactionDTO.setTransactionType(TransactionType.BUY);
        validTransactionDTO.setQuantity(100);
        validTransactionDTO.setPrice(150.0);
        validTransactionDTO.setCurrency("USD");

        testTransaction = new Transaction();
        testTransaction.setTransactionId("txn-123");
        testTransaction.setPortfolioId("portfolio-123");
        testTransaction.setTicker("AAPL");
        testTransaction.setTransactionType(TransactionType.BUY);
        testTransaction.setQuantity(100);
        testTransaction.setPrice(150.0);
        testTransaction.setAmount(15000.0);
        testTransaction.setCurrency("USD");
        testTransaction.setStatus(TransactionStatus.COMPLETED);
        testTransaction.setTransactionDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should retrieve transactions by portfolio ID")
    void testGetByPortfolioId() {
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId("txn-456");
        transaction2.setPortfolioId("portfolio-123");
        transaction2.setTicker("MSFT");

        List<Transaction> transactions = Arrays.asList(testTransaction, transaction2);
        when(transactionRepository.findByPortfolioId("portfolio-123")).thenReturn(transactions);

        List<TransactionDTO> result = transactionService.getByPortfolio("portfolio-123");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
        assertEquals("MSFT", result.get(1).getTicker());
        verify(transactionRepository, times(1)).findByPortfolioId("portfolio-123");
    }

    @Test
    @DisplayName("Should return empty list for null portfolio ID")
    void testGetByPortfolioIdNull() {
        List<TransactionDTO> result = transactionService.getByPortfolio(null);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(transactionRepository, never()).findByPortfolioId(any());
    }

    @Test
    @DisplayName("Should return empty list for empty portfolio ID")
    void testGetByPortfolioIdEmpty() {
        List<TransactionDTO> result = transactionService.getByPortfolio("   ");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(transactionRepository, never()).findByPortfolioId(any());
    }

    @Test
    @DisplayName("Should create transaction with valid data")
    void testCreateTransactionWithValidData() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionDTO result = transactionService.create(validTransactionDTO);

        assertNotNull(result);
        assertEquals("portfolio-123", result.getPortfolioId());
        assertEquals("AAPL", result.getTicker());
        assertEquals(TransactionType.BUY, result.getTransactionType());
        assertEquals(100, result.getQuantity());
        assertEquals(150.0, result.getPrice());
        assertEquals(15000.0, result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create transaction with calculated amount")
    void testCreateTransactionWithCalculatedAmount() {
        validTransactionDTO.setAmount(null); // Amount should be calculated
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionDTO result = transactionService.create(validTransactionDTO);

        assertNotNull(result);
        assertEquals(15000.0, result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should create transaction with default USD currency")
    void testCreateTransactionWithDefaultCurrency() {
        validTransactionDTO.setCurrency(null);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionDTO result = transactionService.create(validTransactionDTO);

        assertNotNull(result);
        assertEquals("USD", result.getCurrency());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should set transaction status to COMPLETED")
    void testCreateTransactionStatusCompleted() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionDTO result = transactionService.create(validTransactionDTO);

        assertNotNull(result);
        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
    }

    @Test
    @DisplayName("Should throw validation exception for null request")
    void testCreateTransactionNullRequest() {
        assertThrows(ValidationException.class, () -> transactionService.create(null));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for null portfolio ID")
    void testCreateTransactionNullPortfolioId() {
        validTransactionDTO.setPortfolioId(null);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for empty portfolio ID")
    void testCreateTransactionEmptyPortfolioId() {
        validTransactionDTO.setPortfolioId("   ");

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for null ticker")
    void testCreateTransactionNullTicker() {
        validTransactionDTO.setTicker(null);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for empty ticker")
    void testCreateTransactionEmptyTicker() {
        validTransactionDTO.setTicker("   ");

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for null transaction type")
    void testCreateTransactionNullType() {
        validTransactionDTO.setTransactionType(null);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for null quantity")
    void testCreateTransactionNullQuantity() {
        validTransactionDTO.setQuantity(null);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for negative quantity")
    void testCreateTransactionNegativeQuantity() {
        validTransactionDTO.setQuantity(-10);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for zero quantity")
    void testCreateTransactionZeroQuantity() {
        validTransactionDTO.setQuantity(0);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for null price")
    void testCreateTransactionNullPrice() {
        validTransactionDTO.setPrice(null);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for negative price")
    void testCreateTransactionNegativePrice() {
        validTransactionDTO.setPrice(-100.0);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should throw validation exception for zero price")
    void testCreateTransactionZeroPrice() {
        validTransactionDTO.setPrice(0.0);

        assertThrows(ValidationException.class, () -> transactionService.create(validTransactionDTO));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should handle exception and return empty list")
    void testGetByPortfolioIdException() {
        when(transactionRepository.findByPortfolioId("portfolio-123")).thenThrow(new RuntimeException("Database error"));

        List<TransactionDTO> result = transactionService.getByPortfolio("portfolio-123");

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should handle empty transaction list")
    void testGetByPortfolioIdEmptyList() {
        when(transactionRepository.findByPortfolioId("portfolio-123")).thenReturn(Arrays.asList());

        List<TransactionDTO> result = transactionService.getByPortfolio("portfolio-123");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(transactionRepository, times(1)).findByPortfolioId("portfolio-123");
    }

    @Test
    @DisplayName("Should set transaction date on creation")
    void testCreateTransactionSetsDate() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionDTO result = transactionService.create(validTransactionDTO);

        assertNotNull(result);
        assertNotNull(result.getTransactionDate());
    }

    @Test
    @DisplayName("Should generate transaction ID on creation")
    void testCreateTransactionGeneratesId() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionDTO result = transactionService.create(validTransactionDTO);

        assertNotNull(result);
        assertNotNull(result.getTransactionId());
    }
}
