package com.example.portfolio_manager;

import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import com.example.portfoliomanager.repository.AssetRepository;
import com.example.portfoliomanager.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTests {

    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    private Asset testAsset;

    @BeforeEach
    void setUp() {
        testAsset = new Asset(
                "asset-1",
                "portfolio-1",
                "AAPL",
                "Apple Inc",
                AssetType.STOCK,
                100,
                150.0,
                LocalDate.of(2024, 1, 15),
                "Tech Stock"
        );
    }

    @Test
    void testCreateAssetWithId() {
        when(assetRepository.save(any(Asset.class))).thenReturn(testAsset);

        Asset result = assetService.createAsset(testAsset);

        assertNotNull(result);
        assertEquals("asset-1", result.getId());
        assertEquals("AAPL", result.getTicker());
        assertEquals("Apple Inc", result.getAssetName());
        assertEquals(AssetType.STOCK, result.getAssetType());
        assertEquals(100, result.getQuantity());
        verify(assetRepository, times(1)).save(testAsset);
    }

    @Test
    void testCreateAssetWithoutId() {
        Asset asset = new Asset(
                null,
                "portfolio-1",
                "MSFT",
                "Microsoft",
                AssetType.STOCK,
                50,
                300.0,
                LocalDate.of(2024, 2, 20),
                "Tech Stock"
        );

        when(assetRepository.save(any(Asset.class))).thenReturn(asset);

        Asset result = assetService.createAsset(asset);

        assertNotNull(result);
        assertNotNull(result.getId());
        verify(assetRepository, times(1)).save(asset);
    }

    @Test
    void testGetAllAssets() {
        Asset asset2 = new Asset(
                "asset-2",
                "portfolio-1",
                "GOOGL",
                "Alphabet Inc",
                AssetType.STOCK,
                25,
                2800.0,
                LocalDate.of(2024, 3, 10),
                "Tech Stock"
        );

        List<Asset> assets = Arrays.asList(testAsset, asset2);
        when(assetRepository.findAll()).thenReturn(assets);

        List<Asset> result = assetService.getAllAssets();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getTicker());
        assertEquals("GOOGL", result.get(1).getTicker());
        verify(assetRepository, times(1)).findAll();
    }

    @Test
    void testGetAssetById() {
        when(assetRepository.findById("asset-1")).thenReturn(Optional.of(testAsset));

        Optional<Asset> result = assetService.getAssetById("asset-1");

        assertTrue(result.isPresent());
        assertEquals("Apple Inc", result.get().getAssetName());
        verify(assetRepository, times(1)).findById("asset-1");
    }

    @Test
    void testGetAssetByIdNotFound() {
        when(assetRepository.findById("asset-999")).thenReturn(Optional.empty());

        Optional<Asset> result = assetService.getAssetById("asset-999");

        assertFalse(result.isPresent());
        verify(assetRepository, times(1)).findById("asset-999");
    }

    @Test
    void testGetAssetsByPortfolioId() {
        Asset asset2 = new Asset(
                "asset-2",
                "portfolio-1",
                "MSFT",
                "Microsoft",
                AssetType.STOCK,
                50,
                300.0,
                LocalDate.of(2024, 2, 20),
                "Tech Stock"
        );

        List<Asset> assets = Arrays.asList(testAsset, asset2);
        when(assetRepository.findByPortfolioId("portfolio-1")).thenReturn(assets);

        List<Asset> result = assetService.getAssetsByPortfolioId("portfolio-1");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getPortfolioId().equals("portfolio-1")));
        verify(assetRepository, times(1)).findByPortfolioId("portfolio-1");
    }

    @Test
    void testGetAssetsByAssetType() {
        Asset cryptoAsset = new Asset(
                "asset-3",
                "portfolio-1",
                "BTC",
                "Bitcoin",
                AssetType.CRYPTO,
                1,
                45000.0,
                LocalDate.of(2024, 4, 5),
                "Cryptocurrency"
        );

        List<Asset> cryptoAssets = Arrays.asList(cryptoAsset);
        when(assetRepository.findByAssetType(AssetType.CRYPTO)).thenReturn(cryptoAssets);

        List<Asset> result = assetService.getAssetsByAssetType(AssetType.CRYPTO);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AssetType.CRYPTO, result.get(0).getAssetType());
        verify(assetRepository, times(1)).findByAssetType(AssetType.CRYPTO);
    }

    @Test
    void testUpdateAsset() {
        Asset updatedDetails = new Asset(
                null,
                "portfolio-2",
                "AAPL",
                "Apple Inc",
                AssetType.STOCK,
                150,
                155.0,
                LocalDate.of(2024, 1, 20),
                "Updated Note"
        );

        when(assetRepository.findById("asset-1")).thenReturn(Optional.of(testAsset));
        when(assetRepository.save(any(Asset.class))).thenReturn(testAsset);

        Asset result = assetService.updateAsset("asset-1", updatedDetails);

        assertNotNull(result);
        assertEquals("portfolio-2", result.getPortfolioId());
        assertEquals(150, result.getQuantity());
        assertEquals(155.0, result.getPurchasePrice());
        verify(assetRepository, times(1)).findById("asset-1");
        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void testUpdateAssetNotFound() {
        Asset updatedDetails = new Asset(
                null,
                "portfolio-1",
                "AAPL",
                "Apple Inc",
                AssetType.STOCK,
                100,
                150.0,
                LocalDate.of(2024, 1, 15),
                "Note"
        );

        when(assetRepository.findById("asset-999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            assetService.updateAsset("asset-999", updatedDetails);
        });

        verify(assetRepository, times(1)).findById("asset-999");
        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void testDeleteAsset() {
        doNothing().when(assetRepository).deleteById("asset-1");

        assetService.deleteAsset("asset-1");

        verify(assetRepository, times(1)).deleteById("asset-1");
    }

    @Test
    void testGetAllAssetsEmpty() {
        when(assetRepository.findAll()).thenReturn(Arrays.asList());

        List<Asset> result = assetService.getAllAssets();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(assetRepository, times(1)).findAll();
    }

    @Test
    void testGetAssetsByPortfolioIdEmpty() {
        when(assetRepository.findByPortfolioId("portfolio-999")).thenReturn(Arrays.asList());

        List<Asset> result = assetService.getAssetsByPortfolioId("portfolio-999");

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(assetRepository, times(1)).findByPortfolioId("portfolio-999");
    }

    @Test
    void testAssetProperties() {
        assertEquals("asset-1", testAsset.getId());
        assertEquals("portfolio-1", testAsset.getPortfolioId());
        assertEquals("AAPL", testAsset.getTicker());
        assertEquals("Apple Inc", testAsset.getAssetName());
        assertEquals(AssetType.STOCK, testAsset.getAssetType());
        assertEquals(100, testAsset.getQuantity());
        assertEquals(150.0, testAsset.getPurchasePrice());
        assertEquals(LocalDate.of(2024, 1, 15), testAsset.getPurchaseDate());
        assertEquals("Tech Stock", testAsset.getNotes());
    }
}
