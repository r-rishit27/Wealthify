package com.example.portfoliomanager.service;

import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import com.example.portfoliomanager.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssetService Unit Tests")
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    private Asset testAsset;

    @BeforeEach
    void setUp() {
        testAsset = new Asset();
        testAsset.setId("asset-123");
        testAsset.setPortfolioId("portfolio-123");
        testAsset.setTicker("AAPL");
        testAsset.setAssetName("Apple Inc.");
        testAsset.setAssetType(AssetType.STOCK);
        testAsset.setQuantity(100);
        testAsset.setPurchasePrice(150.0);
        testAsset.setPurchaseDate(LocalDate.now());
        testAsset.setNotes("Test stock purchase");
    }

    @Test
    @DisplayName("Should create asset with generated UUID if ID is null")
    void testCreateAssetWithNullId() {
        Asset assetToCreate = new Asset();
        assetToCreate.setPortfolioId("portfolio-123");
        assetToCreate.setTicker("MSFT");
        assetToCreate.setAssetName("Microsoft");
        assetToCreate.setAssetType(AssetType.STOCK);
        assetToCreate.setQuantity(50);
        assetToCreate.setPurchasePrice(300.0);

        when(assetRepository.save(any(Asset.class))).thenReturn(testAsset);

        Asset result = assetService.createAsset(assetToCreate);

        assertNotNull(result);
        assertEquals("asset-123", result.getId());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    @DisplayName("Should create asset with provided ID")
    void testCreateAssetWithProvidedId() {
        Asset assetToCreate = new Asset();
        assetToCreate.setId("custom-id");
        assetToCreate.setPortfolioId("portfolio-123");
        assetToCreate.setTicker("GOOG");
        assetToCreate.setAssetName("Google");
        assetToCreate.setAssetType(AssetType.STOCK);
        assetToCreate.setQuantity(25);
        assetToCreate.setPurchasePrice(2800.0);

        when(assetRepository.save(any(Asset.class))).thenReturn(assetToCreate);

        Asset result = assetService.createAsset(assetToCreate);

        assertNotNull(result);
        assertEquals("custom-id", result.getId());
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    @DisplayName("Should retrieve all assets")
    void testGetAllAssets() {
        Asset asset2 = new Asset();
        asset2.setId("asset-456");
        asset2.setTicker("MSFT");

        List<Asset> assets = Arrays.asList(testAsset, asset2);
        when(assetRepository.findAll()).thenReturn(assets);

        List<Asset> result = assetService.getAllAssets();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
        assertEquals("MSFT", result.get(1).getTicker());
        verify(assetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve asset by ID")
    void testGetAssetById() {
        when(assetRepository.findById("asset-123")).thenReturn(Optional.of(testAsset));

        Optional<Asset> result = assetService.getAssetById("asset-123");

        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getTicker());
        verify(assetRepository, times(1)).findById("asset-123");
    }

    @Test
    @DisplayName("Should return empty when asset not found by ID")
    void testGetAssetByIdNotFound() {
        when(assetRepository.findById("non-existent")).thenReturn(Optional.empty());

        Optional<Asset> result = assetService.getAssetById("non-existent");

        assertFalse(result.isPresent());
        verify(assetRepository, times(1)).findById("non-existent");
    }

    @Test
    @DisplayName("Should retrieve assets by portfolio ID")
    void testGetAssetsByPortfolioId() {
        Asset asset2 = new Asset();
        asset2.setId("asset-456");
        asset2.setPortfolioId("portfolio-123");
        asset2.setTicker("MSFT");

        List<Asset> assets = Arrays.asList(testAsset, asset2);
        when(assetRepository.findByPortfolioId("portfolio-123")).thenReturn(assets);

        List<Asset> result = assetService.getAssetsByPortfolioId("portfolio-123");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getPortfolioId().equals("portfolio-123")));
        verify(assetRepository, times(1)).findByPortfolioId("portfolio-123");
    }

    @Test
    @DisplayName("Should retrieve assets by asset type")
    void testGetAssetsByAssetType() {
        Asset bondAsset = new Asset();
        bondAsset.setId("asset-bond-123");
        bondAsset.setAssetType(AssetType.BOND);

        List<Asset> stocks = Arrays.asList(testAsset);
        when(assetRepository.findByAssetType(AssetType.STOCK)).thenReturn(stocks);

        List<Asset> result = assetService.getAssetsByAssetType(AssetType.STOCK);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AssetType.STOCK, result.get(0).getAssetType());
        verify(assetRepository, times(1)).findByAssetType(AssetType.STOCK);
    }

    @Test
    @DisplayName("Should update asset successfully")
    void testUpdateAsset() {
        Asset updatedDetails = new Asset();
        updatedDetails.setPortfolioId("portfolio-123");
        updatedDetails.setTicker("AAPL");
        updatedDetails.setAssetName("Apple Inc. Updated");
        updatedDetails.setAssetType(AssetType.STOCK);
        updatedDetails.setQuantity(150);
        updatedDetails.setPurchasePrice(155.0);
        updatedDetails.setPurchaseDate(LocalDate.now());
        updatedDetails.setNotes("Updated notes");

        Asset expectedResult = new Asset();
        expectedResult.setId("asset-123");
        expectedResult.setPortfolioId("portfolio-123");
        expectedResult.setTicker("AAPL");
        expectedResult.setAssetName("Apple Inc. Updated");
        expectedResult.setAssetType(AssetType.STOCK);
        expectedResult.setQuantity(150);
        expectedResult.setPurchasePrice(155.0);
        expectedResult.setPurchaseDate(LocalDate.now());
        expectedResult.setNotes("Updated notes");

        when(assetRepository.findById("asset-123")).thenReturn(Optional.of(testAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(expectedResult);

        Asset result = assetService.updateAsset("asset-123", updatedDetails);

        assertNotNull(result);
        assertEquals(150, result.getQuantity());
        assertEquals("Apple Inc. Updated", result.getAssetName());
        verify(assetRepository, times(1)).findById("asset-123");
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent asset")
    void testUpdateAssetNotFound() {
        Asset updatedDetails = new Asset();
        when(assetRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> assetService.updateAsset("non-existent", updatedDetails));
        verify(assetRepository, times(1)).findById("non-existent");
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    @DisplayName("Should delete asset by ID")
    void testDeleteAsset() {
        assetService.deleteAsset("asset-123");

        verify(assetRepository, times(1)).deleteById("asset-123");
    }

    @Test
    @DisplayName("Should handle empty asset list")
    void testGetAllAssetsEmpty() {
        when(assetRepository.findAll()).thenReturn(Arrays.asList());

        List<Asset> result = assetService.getAllAssets();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(assetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update all asset fields")
    void testUpdateAssetAllFields() {
        Asset updatedDetails = new Asset();
        updatedDetails.setPortfolioId("portfolio-999");
        updatedDetails.setTicker("AMZN");
        updatedDetails.setAssetName("Amazon");
        updatedDetails.setAssetType(AssetType.ETF);
        updatedDetails.setQuantity(200);
        updatedDetails.setPurchasePrice(180.0);
        updatedDetails.setPurchaseDate(LocalDate.of(2024, 1, 15));
        updatedDetails.setNotes("New notes");

        when(assetRepository.findById("asset-123")).thenReturn(Optional.of(testAsset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Asset result = assetService.updateAsset("asset-123", updatedDetails);

        assertEquals("portfolio-999", result.getPortfolioId());
        assertEquals("AMZN", result.getTicker());
        assertEquals("Amazon", result.getAssetName());
        assertEquals(AssetType.ETF, result.getAssetType());
        assertEquals(200, result.getQuantity());
        assertEquals(180.0, result.getPurchasePrice());
        assertEquals("New notes", result.getNotes());
    }
}
