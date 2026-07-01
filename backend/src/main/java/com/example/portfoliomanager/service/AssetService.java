package com.example.portfoliomanager.service;

import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import com.example.portfoliomanager.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public Asset createAsset(Asset asset) {
        if (asset.getId() == null || asset.getId().isEmpty()) {
            asset.setId(UUID.randomUUID().toString());
        }
        return assetRepository.save(asset);
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Optional<Asset> getAssetById(String id) {
        return assetRepository.findById(id);
    }

    public List<Asset> getAssetsByPortfolioId(String portfolioId) {
        return assetRepository.findByPortfolioId(portfolioId);
    }

    public List<Asset> getAssetsByAssetType(AssetType assetType) {
        return assetRepository.findByAssetType(assetType);
    }

    public Asset updateAsset(String id, Asset assetDetails) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setPortfolioId(assetDetails.getPortfolioId());
        asset.setTicker(assetDetails.getTicker());
        asset.setAssetName(assetDetails.getAssetName());
        asset.setAssetType(assetDetails.getAssetType());
        asset.setQuantity(assetDetails.getQuantity());
        asset.setPurchasePrice(assetDetails.getPurchasePrice());
        asset.setPurchaseDate(assetDetails.getPurchaseDate());
        asset.setNotes(assetDetails.getNotes());
        return assetRepository.save(asset);
    }

    public void deleteAsset(String id) {
        assetRepository.deleteById(id);
    }
}
