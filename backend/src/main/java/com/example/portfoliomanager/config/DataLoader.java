package com.example.portfoliomanager.config;

import com.example.portfoliomanager.dto.FinnhubCandleDTO;
import com.example.portfoliomanager.entity.Asset;
import com.example.portfoliomanager.entity.AssetType;
import com.example.portfoliomanager.entity.MarketData;
import com.example.portfoliomanager.entity.Portfolio;
import com.example.portfoliomanager.repository.AssetRepository;
import com.example.portfoliomanager.repository.MarketDataRepository;
import com.example.portfoliomanager.repository.PortfolioRepository;
import com.example.portfoliomanager.service.FinnhubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final MarketDataRepository marketDataRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final FinnhubService finnhubService;
    private final String seedTickers;
    private final int historyDays;
    private final String candleResolution;

    public DataLoader(MarketDataRepository marketDataRepository,
                      PortfolioRepository portfolioRepository,
                      AssetRepository assetRepository,
                      FinnhubService finnhubService,
                      @Value("${finnhub.seed-tickers}") String seedTickers,
                      @Value("${finnhub.history-days}") int historyDays,
                      @Value("${finnhub.candle-resolution}") String candleResolution) {
        this.marketDataRepository = marketDataRepository;
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.finnhubService = finnhubService;
        this.seedTickers = seedTickers;
        this.historyDays = historyDays;
        this.candleResolution = candleResolution;
    }

    @Bean
    public CommandLineRunner loadSampleData() {
        return args -> {
            log.info("Loading sample data...");

            // Only load if database is empty
            if (portfolioRepository.count() == 0) {
                loadPortfoliosAndAssets();
            } else {
                log.info("Sample portfolios already exist, skipping portfolio creation");
            }

            // Load market data from Finnhub
            if (marketDataRepository.count() == 0) {
                loadFinnhubMarketData();
            } else {
                log.info("Market data already exists, skipping Finnhub data load");
            }

            log.info("Data loading complete!");
            log.info("Portfolios in DB: {}", portfolioRepository.count());
            log.info("Assets in DB: {}", assetRepository.count());
            log.info("Market data records in DB: {}", marketDataRepository.count());
        };
    }

    private void loadPortfoliosAndAssets() {
        log.info("Creating sample portfolios and assets...");

        // Create Growth Portfolio
        Portfolio growthPortfolio = createPortfolio(
                "Tech Growth Portfolio",
                "Aggressive growth stocks focused on technology sector",
                15000.00
        );

        // Create Dividend Portfolio
        Portfolio dividendPortfolio = createPortfolio(
                "Dividend Income Portfolio",
                "Stable dividend-paying stocks for passive income",
                25000.00
        );

        // Create Balanced Portfolio
        Portfolio balancedPortfolio = createPortfolio(
                "Balanced Portfolio",
                "Mix of growth and value stocks for moderate risk",
                20000.00
        );

        // Assets for Growth Portfolio
        createAsset(growthPortfolio.getId(), "AAPL", "Apple Inc.", AssetType.STOCK,
                100, 150.00, LocalDate.now().minusMonths(8));
        createAsset(growthPortfolio.getId(), "GOOG", "Alphabet Inc.", AssetType.STOCK,
                50, 140.00, LocalDate.now().minusMonths(6));
        createAsset(growthPortfolio.getId(), "MSFT", "Microsoft Corporation", AssetType.STOCK,
                75, 380.00, LocalDate.now().minusMonths(5));
        createAsset(growthPortfolio.getId(), "AMZN", "Amazon.com Inc.", AssetType.STOCK,
                40, 175.00, LocalDate.now().minusMonths(4));
        createAsset(growthPortfolio.getId(), "NVDA", "NVIDIA Corporation", AssetType.STOCK,
                30, 450.00, LocalDate.now().minusMonths(3));

        // Assets for Dividend Portfolio
        createAsset(dividendPortfolio.getId(), "AAPL", "Apple Inc.", AssetType.STOCK,
                200, 145.00, LocalDate.now().minusMonths(12));
        createAsset(dividendPortfolio.getId(), "MSFT", "Microsoft Corporation", AssetType.STOCK,
                150, 295.00, LocalDate.now().minusMonths(10));
        createAsset(dividendPortfolio.getId(), "META", "Meta Platforms Inc.", AssetType.STOCK,
                80, 320.00, LocalDate.now().minusMonths(7));
        createAsset(dividendPortfolio.getId(), "NFLX", "Netflix Inc.", AssetType.STOCK,
                25, 450.00, LocalDate.now().minusMonths(5));

        // Assets for Balanced Portfolio
        createAsset(balancedPortfolio.getId(), "AAPL", "Apple Inc.", AssetType.STOCK,
                60, 165.00, LocalDate.now().minusMonths(9));
        createAsset(balancedPortfolio.getId(), "GOOG", "Alphabet Inc.", AssetType.STOCK,
                35, 135.00, LocalDate.now().minusMonths(7));
        createAsset(balancedPortfolio.getId(), "AMZN", "Amazon.com Inc.", AssetType.STOCK,
                45, 180.00, LocalDate.now().minusMonths(6));
        createAsset(balancedPortfolio.getId(), "META", "Meta Platforms Inc.", AssetType.STOCK,
                30, 480.00, LocalDate.now().minusMonths(4));

        log.info("Created {} portfolios with assets", portfolioRepository.count());
    }

    private Portfolio createPortfolio(String name, String description, Double cashBalance) {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(UUID.randomUUID().toString());
        portfolio.setPortfolioName(name);
        portfolio.setDescription(description);
        portfolio.setBaseCurrency("USD");
        portfolio.setCashBalance(cashBalance);
        return portfolioRepository.save(portfolio);
    }

    private Asset createAsset(String portfolioId, String ticker, String name,
                              AssetType type, Integer quantity,
                              Double purchasePrice, LocalDate purchaseDate) {
        Asset asset = new Asset();
        asset.setId(UUID.randomUUID().toString());
        asset.setPortfolioId(portfolioId);
        asset.setTicker(ticker);
        asset.setAssetName(name);
        asset.setAssetType(type);
        asset.setQuantity(quantity);
        asset.setPurchasePrice(purchasePrice);
        asset.setPurchaseDate(purchaseDate);
        return assetRepository.save(asset);
    }

    private void loadFinnhubMarketData() {
        log.info("Loading Finnhub market data...");

        List<String> tickers = Arrays.stream(seedTickers.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        long to = Instant.now().getEpochSecond();
        long from = Instant.now().minus(historyDays, ChronoUnit.DAYS).getEpochSecond();

        int totalSaved = 0;
        for (String ticker : tickers) {
            try {
                FinnhubCandleDTO candles = finnhubService.getCandles(ticker, candleResolution, from, to);
                if (candles == null || candles.getStatus() == null || !"ok".equalsIgnoreCase(candles.getStatus())) {
                    log.warn("No candle data for {}, generating sample data", ticker);
                    int generated = generateSampleMarketData(ticker);
                    totalSaved += generated;
                    continue;
                }
                int saved = saveCandles(ticker, candles);
                totalSaved += saved;
                log.info("Saved {} records for {}", saved, ticker);
            } catch (Exception e) {
                log.warn("Error loading data for {}: {}, generating sample data", ticker, e.getMessage());
                int generated = generateSampleMarketData(ticker);
                totalSaved += generated;
            }
        }

        log.info("Market data load complete. Total records: {}", totalSaved);
    }

    private int saveCandles(String ticker, FinnhubCandleDTO candles) {
        if (candles.getTimestamps() == null || candles.getOpens() == null || candles.getHighs() == null
                || candles.getLows() == null || candles.getCloses() == null || candles.getVolumes() == null) {
            return 0;
        }

        int size = candles.getTimestamps().size();
        size = Math.min(size, candles.getOpens().size());
        size = Math.min(size, candles.getHighs().size());
        size = Math.min(size, candles.getLows().size());
        size = Math.min(size, candles.getCloses().size());
        size = Math.min(size, candles.getVolumes().size());

        int saved = 0;
        for (int i = 0; i < size; i++) {
            LocalDate date = Instant.ofEpochSecond(candles.getTimestamps().get(i))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            Optional<MarketData> existing = marketDataRepository.findByTickerAndDate(ticker, date);
            if (existing.isPresent()) {
                continue;
            }

            Double open = candles.getOpens().get(i);
            Double high = candles.getHighs().get(i);
            Double low = candles.getLows().get(i);
            Double close = candles.getCloses().get(i);
            Double volume = candles.getVolumes().get(i);
            if (open == null || high == null || low == null || close == null || volume == null) {
                continue;
            }

            MarketData data = new MarketData();
            data.setTicker(ticker);
            data.setDate(date);
            data.setOpenPrice(toMoney(open));
            data.setHighPrice(toMoney(high));
            data.setLowPrice(toMoney(low));
            data.setClosePrice(toMoney(close));
            data.setVolume(volume.longValue());
            marketDataRepository.save(data);
            saved++;
        }
        return saved;
    }

    private int generateSampleMarketData(String ticker) {
        log.info("Generating sample market data for {}...", ticker);

        // Base prices for different tickers
        double basePrice = switch (ticker) {
            case "AAPL" -> 175.50;
            case "GOOG" -> 145.00;
            case "MSFT" -> 415.00;
            case "AMZN" -> 185.00;
            case "META" -> 510.00;
            case "NFLX" -> 620.00;
            case "NVDA" -> 875.00;
            default -> 100.00;
        };

        LocalDate today = LocalDate.now();
        int saved = 0;

        // Create 30 days of historical data
        for (int day = 30; day >= 0; day--) {
            LocalDate date = today.minusDays(day);

            // Skip weekends
            if (date.getDayOfWeek().getValue() > 5) continue;

            // Check if data already exists
            if (marketDataRepository.findByTickerAndDate(ticker, date).isPresent()) {
                continue;
            }

            // Random price variation (-3% to +3%)
            double variation = (Math.random() - 0.5) * 0.06;
            double closePrice = basePrice * (1 + variation);
            double openPrice = closePrice * (1 + (Math.random() - 0.5) * 0.02);
            double highPrice = Math.max(closePrice, openPrice) * (1 + Math.random() * 0.01);
            double lowPrice = Math.min(closePrice, openPrice) * (1 - Math.random() * 0.01);

            MarketData data = new MarketData();
            data.setTicker(ticker);
            data.setDate(date);
            data.setOpenPrice(toMoney(openPrice));
            data.setHighPrice(toMoney(highPrice));
            data.setLowPrice(toMoney(lowPrice));
            data.setClosePrice(toMoney(closePrice));
            data.setVolume((long) (Math.random() * 50000000 + 10000000));
            marketDataRepository.save(data);
            saved++;

            // Update base price for next iteration
            basePrice = closePrice;
        }

        log.info("Generated {} sample records for {}", saved, ticker);
        return saved;
    }

    private BigDecimal toMoney(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
