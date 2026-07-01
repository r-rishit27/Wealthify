package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.dto.TransactionDTO;
import com.example.portfoliomanager.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<TransactionDTO>> getByPortfolio(@PathVariable String portfolioId) {
        try {
            List<TransactionDTO> transactions = transactionService.getByPortfolio(portfolioId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> create(@RequestBody TransactionDTO request) {
        TransactionDTO created = transactionService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}

