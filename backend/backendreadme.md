This repository contains a **Spring Boot backend** for a Portfolio Manager application.  
It manages portfolios, assets, and market data, and integrates with **Finnhub** to fetch live stock data.

---

# 1) Root Folder (Repository Level)

runtime-terror/ ├─ .idea/                 # IntelliJ project settings (local only) ├─ .mvn/                  # Maven wrapper files ├─ backend/               # Spring Boot backend ├─ .gitattributes ├─ .gitignore ├─ backend_design.md      # Architecture/design notes ├─ Database_Setup.md      # DB setup instructions ├─ Quick_API_Guide.md     # Example API usage ├─ README.md              # Main project README ├─ stock_dataset.csv      # Legacy dataset (not used now)

**Important:**  
`stock_dataset.csv` was earlier used for static data. We now use **Finnhub** and do **not** rely on this file.

---

# 2) Backend Folder Overview

backend/ ├─ pom.xml                 # Maven dependencies + build settings ├─ src/ │  ├─ main/ │  │  ├─ java/ │  │  │  └─ com/example/portfoliomanager/ │  │  │     ├─ config/ │  │  │     ├─ controller/ │  │  │     ├─ dto/ │  │  │     ├─ entity/ │  │  │     ├─ exception/ │  │  │     ├─ repository/ │  │  │     ├─ service/ │  │  │     └─ PortfolioManagerApplication.java │  │  └─ resources/ │  │     └─ application.properties │  └─ test/

---

# 3) Configuration Files (`config/`)

### `DataLoader.java`
**Purpose:**  
Runs at app startup (profiles `h2` or `mysql`) and pulls **Finnhub candles** for configured tickers.

**Flow:**
1. Reads tickers from `application.properties` (`finnhub.seed-tickers`).
2. Calls `FinnhubService.getCandles(...)`.
3. Converts Finnhub candles into `MarketData` rows.
4. Saves into DB (skips duplicates using ticker + date).

---

### `FinnhubConfig.java`
**Purpose:**  
Provides a Spring `RestTemplate` bean for HTTP calls to Finnhub.

---

### `OpenApiConfig.java`
**Purpose:**  
Configures Swagger/OpenAPI documentation metadata:
- API name  
- description  
- version  
- server URL (port 8081)

Swagger UI is available at:
http://localhost:8081/swagger-ui.html

---

### `WebConfig.java`
**Purpose:**  
Enables CORS for `/api/**` so browser apps and Postman can call APIs without CORS errors.

---

# 4) Controllers (`controller/`)
These define REST endpoints.

### `AssetController.java`
CRUD endpoints for assets:
- `POST /api/v1/assets`
- `GET /api/v1/assets`
- `GET /api/v1/assets/{id}`
- `PUT /api/v1/assets/{id}`
- `DELETE /api/v1/assets/{id}`

---

### `PortfolioController.java`
CRUD endpoints for portfolios:
- `POST /api/v1/portfolios`
- `GET /api/v1/portfolios`
- `GET /api/v1/portfolios/{id}`
- `PUT /api/v1/portfolios/{id}`
- `DELETE /api/v1/portfolios/{id}`

---

### `MarketDataController.java`
Reads stored market data from DB:
- `GET /api/v1/market-data`
- `GET /api/v1/market-data/{id}`
- `GET /api/v1/market-data?{ticker=...}`
- `GET /api/v1/market-data/by-date?ticker=...&date=...`
- `GET /api/v1/market-data?ticker=...&start=...&end=...`

---

### `FinnhubController.java`
**Direct Finnhub test endpoints:**
- `GET /api/v1/finnhub/quote?symbol=AAPL`
- `GET /api/v1/finnhub/candles?symbol=AAPL&resolution=D&from=...&to=...`

These call Finnhub live (not stored in DB).

---

# 5) Services (`service/`)

### `FinnhubService.java`
Calls Finnhub APIs:
- `/quote` → current price
- `/stock/candle` → OHLCV history

Uses `RestTemplate` and your API key from `application.properties`.

---

### `MarketDataService.java`
CRUD for MarketData:
- create  
- read  
- update  
- delete  
- find by ticker/date

---

### `AssetService.java`
Handles asset logic and persistence.

---

### `PortfolioService.java`
Handles portfolio logic and persistence.

---

# 6) Repositories (`repository/`)
JPA repositories for database access.

- `AssetRepository`
- `PortfolioRepository`
- `MarketDataRepository`

`MarketDataRepository` has special finders:
- `findByTicker(...)`
- `findByTickerAndDate(...)`
- `findByTickerAndDateBetween(...)`

---

# 7) Entities (`entity/`)
Database tables mapped to Java classes:

### `Portfolio.java`
Fields:
- `id`
- `portfolioName`
- `description`
- `baseCurrency`
- `cashBalance`

---

### `Asset.java`
Fields:
- `id`
- `portfolioId`
- `ticker`
- `assetName`
- `assetType`
- `quantity`
- `purchasePrice`
- `purchaseDate`
- `notes`

---

### `MarketData.java`
Fields:
- `id`
- `ticker`
- `date`
- `openPrice`
- `highPrice`
- `lowPrice`
- `closePrice`
- `volume`
- `createdAt`

---

# 8) DTOs (`dto/`)
Used for clean API responses.

### `FinnhubQuoteDTO`
Fields:
- `currentPrice`
- `change`
- `percentChange`
- `highPrice`
- `lowPrice`
- `openPrice`
- `previousClose`
- `timestamp`

---

### `FinnhubCandleDTO`
Fields:
- `status`
- `timestamps`
- `opens`
- `highs`
- `lows`
- `closes`
- `volumes`

---

### `MarketDataDTO`
Simple data transfer object for MarketData.

---

# 9) Exceptions (`exception/`)
Centralized error handling.

- `GlobalExceptionHandler`  
- `ErrorResponse`  
- `ResourceNotFoundException`  
- `DuplicateResourceException`  
- `ValidationException`

---

# 10) Application Configuration (`application.properties`)
Key settings:

server.port=8081
DB
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_manager spring.datasource.username=root spring.datasource.password=password
Finnhub
finnhub.base-url=https://finnhub.io/api/v1 finnhub.api-key=YOUR_FINNHUB_API_KEY finnhub.seed-tickers=AAPL,MSFT,GOOG,AMZN finnhub.history-days=30 finnhub.candle-resolution=D

---

# 11) Finnhub Integration – How It Works

### Startup (DataLoader)
- Reads tickers
- Calls `/stock/candle`
- Stores results into `market_data`

### Live Queries
- `/api/v1/finnhub/quote` → current snapshot
- `/api/v1/finnhub/candles` → historical candles

### Stored Data
- `/api/v1/market-data` endpoints return saved DB records

---

# 12) Common Endpoints To Test

**Finnhub live:**
- `GET http://localhost:8081/api/v1/finnhub/quote?symbol=AAPL`
- `GET http://localhost:8081/api/v1/finnhub/candles?symbol=AAPL&resolution=D&from=...&to=...`

**Stored MarketData:**
- `GET http://localhost:8081/api/v1/market-data`
- `GET http://localhost:8081/api/v1/market-data?ticker=AAPL`

---

# 13) Notes
- Do not commit your Finnhub API key to a public repo.
- If you change the server port, update OpenAPI config and Postman URLs.
- DataLoader runs only under profiles `h2` or `mysql`.