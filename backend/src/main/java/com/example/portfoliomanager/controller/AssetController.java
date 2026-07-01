package com.example.portfoliomanager.controller;

import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import com.example.portfoliomanager.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        Asset created = assetService.createAsset(asset);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets(
            @RequestParam(required = false) String portfolioId,
            @RequestParam(required = false) AssetType assetType) {
        List<Asset> assets;
        if (portfolioId != null) {
            assets = assetService.getAssetsByPortfolioId(portfolioId);
        } else if (assetType != null) {
            assets = assetService.getAssetsByAssetType(assetType);
        } else {
            assets = assetService.getAllAssets();
        }
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable String id) {
        return assetService.getAssetById(id)
                .map(asset -> new ResponseEntity<>(asset, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/portfolio/{portfolioId}")
    public ResponseEntity<List<Asset>> getAssetsByPortfolio(@PathVariable String portfolioId) {
        List<Asset> assets = assetService.getAssetsByPortfolioId(portfolioId);
        return new ResponseEntity<>(assets, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable String id, @RequestBody Asset assetDetails) {
        try {
            Asset updated = assetService.updateAsset(id, assetDetails);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable String id) {
        assetService.deleteAsset(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}