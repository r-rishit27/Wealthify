package com.example.portfoliomanager.repository;

import com.example.portfoliomanager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {

    List<Transaction> findByPortfolioId(String portfolioId);
}

