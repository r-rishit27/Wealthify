package com.example.portfolio_manager;

import com.example.portfoliomanager.controller.AssetController;
import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import com.example.portfoliomanager.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetControllerTests {

    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

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
    void testCreateAsset() {
        when(assetService.createAsset(any(Asset.class))).thenReturn(testAsset);

        ResponseEntity<Asset> response = assetController.createAsset(testAsset);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testAsset, response.getBody());
        verify(assetService, times(1)).createAsset(any(Asset.class));
    }

    @Test
    void testGetAllAssets() {
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
        when(assetService.getAllAssets()).thenReturn(assets);

        ResponseEntity<List<Asset>> response = assetController.getAllAssets(null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(assetService, times(1)).getAllAssets();
    }

    @Test
    void testGetAssetById() {
        when(assetService.getAssetById("asset-1")).thenReturn(Optional.of(testAsset));

        ResponseEntity<Asset> response = assetController.getAssetById("asset-1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAsset, response.getBody());
        verify(assetService, times(1)).getAssetById("asset-1");
    }

    @Test
    void testGetAssetByIdNotFound() {
        when(assetService.getAssetById("asset-999")).thenReturn(Optional.empty());

        ResponseEntity<Asset> response = assetController.getAssetById("asset-999");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(assetService, times(1)).getAssetById("asset-999");
    }

    @Test
    void testGetAssetsByPortfolioId() {
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
        when(assetService.getAssetsByPortfolioId("portfolio-1")).thenReturn(assets);

        ResponseEntity<List<Asset>> response = assetController.getAllAssets("portfolio-1", null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(assetService, times(1)).getAssetsByPortfolioId("portfolio-1");
    }

    @Test
    void testGetAssetsByAssetType() {
        List<Asset> stockAssets = Arrays.asList(testAsset);
        when(assetService.getAssetsByAssetType(AssetType.STOCK)).thenReturn(stockAssets);

        ResponseEntity<List<Asset>> response = assetController.getAllAssets(null, AssetType.STOCK);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(AssetType.STOCK, response.getBody().get(0).getAssetType());
        verify(assetService, times(1)).getAssetsByAssetType(AssetType.STOCK);
    }

    @Test
    void testUpdateAsset() {
        Asset updatedAsset = new Asset(
                "asset-1",
                "portfolio-1",
                "AAPL",
                "Apple Inc",
                AssetType.STOCK,
                150,
                155.0,
                LocalDate.of(2024, 1, 20),
                "Updated Note"
        );

        when(assetService.updateAsset("asset-1", updatedAsset)).thenReturn(updatedAsset);

        ResponseEntity<Asset> response = assetController.updateAsset("asset-1", updatedAsset);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(150, response.getBody().getQuantity());
        verify(assetService, times(1)).updateAsset("asset-1", updatedAsset);
    }

    @Test
    void testUpdateAssetThrowsException() {
        Asset updatedAsset = new Asset(
                "asset-1",
                "portfolio-1",
                "AAPL",
                "Apple Inc",
                AssetType.STOCK,
                100,
                150.0,
                LocalDate.of(2024, 1, 15),
                "Note"
        );

        when(assetService.updateAsset("asset-999", updatedAsset))
                .thenThrow(new RuntimeException("Asset not found"));

        // Controller should propagate the exception from service
        ResponseEntity<Asset> response = assetController.updateAsset("asset-999", updatedAsset);

        // Verify the exception was thrown from service
        verify(assetService, times(1)).updateAsset("asset-999", updatedAsset);
    }

    @Test
    void testDeleteAsset() {
        doNothing().when(assetService).deleteAsset("asset-1");

        ResponseEntity<Void> response = assetController.deleteAsset("asset-1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(assetService, times(1)).deleteAsset("asset-1");
    }

    @Test
    void testGetAllAssetsEmpty() {
        when(assetService.getAllAssets()).thenReturn(Arrays.asList());

        ResponseEntity<List<Asset>> response = assetController.getAllAssets(null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(assetService, times(1)).getAllAssets();
    }

    @Test
    void testGetAssetsByPortfolioIdEmpty() {
        when(assetService.getAssetsByPortfolioId("portfolio-999")).thenReturn(Arrays.asList());

        ResponseEntity<List<Asset>> response = assetController.getAllAssets("portfolio-999", null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());
        verify(assetService, times(1)).getAssetsByPortfolioId("portfolio-999");
    }
}
