package com.example.portfoliomanager.service;

import com.example.portfoliomanager.dto.TransactionDTO;
import com.example.portfoliomanager.entity.Transaction;
import com.example.portfoliomanager.entity.TransactionStatus;
import com.example.portfoliomanager.entity.TransactionType;
import com.example.portfoliomanager.exception.ValidationException;
import com.example.portfoliomanager.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionDTO> getByPortfolio(String portfolioId) {
        if (portfolioId == null || portfolioId.trim().isEmpty()) {
            return List.of();
        }
        try {
            return transactionRepository.findByPortfolioId(portfolioId)
                    .stream()
                    .map(this::toDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public TransactionDTO create(TransactionDTO request) {
        validateCreateRequest(request);

        Transaction tx = new Transaction();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setPortfolioId(request.getPortfolioId());
        tx.setTicker(request.getTicker());
        tx.setTransactionType(request.getTransactionType());
        tx.setQuantity(request.getQuantity());
        tx.setPrice(request.getPrice());

        if (request.getAmount() != null) {
            tx.setAmount(request.getAmount());
        } else if (request.getQuantity() != null && request.getPrice() != null) {
            tx.setAmount(request.getQuantity() * request.getPrice());
        }

        tx.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setTransactionDate(LocalDateTime.now());

        Transaction saved = transactionRepository.save(tx);
        return toDTO(saved);
    }

    private void validateCreateRequest(TransactionDTO request) {
        if (request == null) {
            throw new ValidationException("Request body is required");
        }
        if (request.getPortfolioId() == null || request.getPortfolioId().trim().isEmpty()) {
            throw new ValidationException("portfolioId is required");
        }
        if (request.getTicker() == null || request.getTicker().trim().isEmpty()) {
            throw new ValidationException("ticker is required");
        }
        if (request.getTransactionType() == null) {
            throw new ValidationException("transactionType is required (BUY or SELL)");
        }
        if (request.getTransactionType() != TransactionType.BUY && request.getTransactionType() != TransactionType.SELL) {
            throw new ValidationException("transactionType must be BUY or SELL");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ValidationException("quantity must be a positive integer");
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new ValidationException("price must be a positive number");
        }
    }

    private TransactionDTO toDTO(Transaction tx) {
        if (tx == null) {
            return null;
        }
        TransactionDTO dto = new TransactionDTO();
        dto.setTransactionId(tx.getTransactionId());
        dto.setPortfolioId(tx.getPortfolioId());
        dto.setTicker(tx.getTicker());
        dto.setTransactionType(tx.getTransactionType());
        dto.setQuantity(tx.getQuantity());
        dto.setPrice(tx.getPrice());
        dto.setAmount(tx.getAmount());
        dto.setCurrency(tx.getCurrency());
        dto.setStatus(tx.getStatus());
        dto.setTransactionDate(tx.getTransactionDate());
        return dto;
    }
}

