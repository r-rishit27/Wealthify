package com.example.portfoliomanager.repository;

import com.example.portfoliomanager.entity.MarketData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {
    List<MarketData> findByTicker(String ticker);
    
    Page<MarketData> findByTicker(String ticker, Pageable pageable);
    
    List<MarketData> findByTickerAndDateBetween(String ticker, LocalDate start, LocalDate end);
    
    List<MarketData> findByTickerAndDateBetweenOrderByDateAsc(String ticker, LocalDate start, LocalDate end);
    
    Optional<MarketData> findByTickerAndDate(String ticker, LocalDate date);
    
    @Query("SELECT m FROM MarketData m WHERE m.ticker = :ticker ORDER BY m.date DESC LIMIT 1")
    Optional<MarketData> findMostRecentByTicker(String ticker);
    
    @Query("SELECT DISTINCT m.ticker FROM MarketData m ORDER BY m.ticker")
    List<String> findAllTickers();
    
    boolean existsByTickerAndDate(String ticker, LocalDate date);
}