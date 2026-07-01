package com.example.portfoliomanager.repository;

import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {
    List<Asset> findByPortfolioId(String portfolioId);
    List<Asset> findByAssetType(AssetType assetType);
}

