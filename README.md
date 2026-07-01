# Portfolio Manager - Full Stack Application

A comprehensive portfolio management system with real-time market data, AI-powered stock predictions, quantum-inspired portfolio optimization, and intelligent portfolio recommendations.

## 🚀 Features

- **Portfolio Management**: Create, manage, and track multiple investment portfolios
- **Real-time Market Data**: Live stock quotes and historical data via Finnhub API
- **AI Stock Predictions**: 7-day price forecasts using Bidirectional LSTM models
- **Quantum Portfolio Optimization**: QUBO-based portfolio rebalancing using Simulated Annealing
- **AI Portfolio Summarizer**: Gemini-powered portfolio analysis and recommendations
- **Transaction Tracking**: Record and monitor buy/sell transactions
- **Interactive Dashboard**: Modern React UI with charts and visualizations
- **RESTful API**: Comprehensive backend API with Swagger documentation

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17+** (for Spring Boot backend)
- **Node.js 18+** and **npm** (for React frontend)
- **Python 3.9+** (for ML and Quantum services)
- **MySQL 8.0+** (or use H2 for development)
- **Maven 3.6+** (or use the included Maven wrapper)
- **Git** (for cloning the repository)

## 🏗️ Project Structure

```
hsbc-bitbucket/
├── backend/                 # Spring Boot REST API
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml             # Maven dependencies
├── frontend/               # React + TypeScript + Vite
│   ├── src/               # React components and pages
│   └── package.json       # Node dependencies
├── stock_prediction/       # ML Prediction Service (FastAPI)
│   ├── main.py            # FastAPI application
│   ├── train_model.py     # Model training script
│   └── Model/             # Trained LSTM models
├── Quantum/                # Quantum Optimization Service (FastAPI)
│   ├── main.py            # FastAPI application
│   └── qubo_solver.py     # QUBO optimization logic
├── AI_SUMRIZER/           # AI Summarizer Service (Flask)
│   ├── app.py             # Flask application
│   └── .env               # Gemini API key (create this)
├── run-all.sh             # Script to run all services
└── README.md              # This file
```

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone https://bitbucket.org/r-rishit27/runtime-terror.git
cd runtime-terror
```

### 2. Database Setup

#### Option A: MySQL (Recommended for Production)

```bash
# Start MySQL and create database
mysql -u root -p

# In MySQL console:
CREATE DATABASE portfolio_manager;
CREATE USER 'portfolio_user'@'localhost' IDENTIFIED BY 'Portfolio@123';
GRANT ALL PRIVILEGES ON portfolio_manager.* TO 'portfolio_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

Update `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_manager
spring.datasource.username=portfolio_user
spring.datasource.password=Portfolio@123
```


### 3. Backend Setup (Spring Boot)

```bash
cd backend

# Make Maven wrapper executable (Linux/Mac)
chmod +x mvnw

# Install dependencies and run
./mvnw clean install
./mvnw spring-boot:run

# Or use the provided script
bash run.sh
```

The backend will start on **http://localhost:8080**

**API Documentation**: Visit **http://localhost:8080/swagger-ui.html** for interactive API docs

**Configuration**: Edit `src/main/resources/application.properties` to configure:
- Database connection
- Finnhub API key (for real-time market data)
- Prediction service URL

### 4. Frontend Setup (React)

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
```

The frontend will start on **http://localhost:5173**

### 5. Stock Prediction Service Setup (Python/FastAPI)

```bash
cd stock_prediction

# Install Python dependencies
pip install fastapi uvicorn tensorflow pandas numpy scikit-learn h5py

# Train models (optional - pre-trained models are included)
python train_model.py

# Start the service
python main.py
# Or: uvicorn main:app --reload --port 8000
```

The service will start on **http://localhost:8000**

**Endpoints**:
- `GET /` - Health check and supported tickers
- `POST /predict` - Get 7-day forecast for a ticker
- `GET /latest_predict/{ticker}` - Auto-predict using latest 60 days

### 6. Quantum Optimization Service Setup (Python/FastAPI)

```bash
cd Quantum

# Install dependencies
pip install -r requirements.txt

# Start the service
uvicorn main:app --reload --port 8001
# Or: python -m uvicorn main:app --host 0.0.0.0 --port 8001
```

The service will start on **http://localhost:8001**

**Endpoints**:
- `POST /optimize_portfolio` - Optimize portfolio using QUBO algorithm
- `GET /docs` - Interactive API documentation

### 7. AI Summarizer Service Setup (Python/Flask)

```bash
cd AI_SUMRIZER

# Install dependencies
pip install -r requirements.txt

# Create .env file with your Gemini API key
echo "GEMINI_API_KEY=your_actual_api_key_here" > .env

# Start the service
python app.py
```

The service will start on **http://localhost:8002**

**Get Gemini API Key**: Visit [Google AI Studio](https://makersuite.google.com/app/apikey)

**Endpoints**:
- `GET /recommend?portfolio_json={...}` - Get AI portfolio recommendations

## 🚀 Running the Application

### Quick Start (All Services)

Use the provided script to start all services at once:

```bash
# Make script executable
chmod +x run-all.sh

# Run all services
./run-all.sh
```

This will start:
- **Backend**: http://localhost:8080
- **Stock Prediction**: http://localhost:8000
- **Quantum Optimization**: http://localhost:8001
- **AI Summarizer**: http://localhost:8002
- **Frontend**: http://localhost:5173

Press `Ctrl+C` to stop all services.

### Manual Start (Individual Services)

#### Terminal 1: Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### Terminal 2: Stock Prediction Service
```bash
cd stock_prediction
python main.py
```

#### Terminal 3: Quantum Service
```bash
cd Quantum
uvicorn main:app --reload --port 8001
```

#### Terminal 4: AI Summarizer
```bash
cd AI_SUMRIZER
python app.py
```

#### Terminal 5: Frontend
```bash
cd frontend
npm run dev
```

## 📚 API Documentation

### Backend API (Spring Boot)

**Base URL**: `http://localhost:8080/api/v1`

**Swagger UI**: http://localhost:8080/swagger-ui.html

#### Portfolio APIs
- `POST /portfolios` - Create a new portfolio
- `GET /portfolios` - List all portfolios (paginated)
- `GET /portfolios/{id}` - Get portfolio details
- `GET /portfolios/{id}/summary` - Get portfolio summary with calculations
- `PUT /portfolios/{id}` - Update portfolio
- `DELETE /portfolios/{id}` - Delete portfolio
- `GET /portfolios/search?name={name}` - Search portfolios

#### Asset APIs
- `POST /assets` - Add asset to portfolio
- `GET /assets` - List all assets
- `GET /assets/{id}` - Get asset details
- `GET /assets/portfolio/{portfolioId}` - Get assets by portfolio
- `PUT /assets/{id}` - Update asset
- `DELETE /assets/{id}` - Delete asset
- `POST /assets/update-prices` - Update asset prices from market data

#### Transaction APIs
- `POST /transactions` - Create transaction
- `GET /transactions` - List transactions (paginated)
- `GET /transactions/{id}` - Get transaction details
- `GET /transactions/portfolio/{portfolioId}` - Get transactions by portfolio
- `DELETE /transactions/{id}` - Delete transaction

#### Market Data APIs
- `GET /market-data/{ticker}/latest` - Get latest price
- `GET /market-data/{ticker}/history` - Get price history
- `GET /market-data/tickers` - List all tracked tickers
- `POST /market-data` - Add market data
- `POST /market-data/batch` - Batch add market data

#### Stock APIs (Finnhub Integration)
- `GET /stocks/{symbol}/quote` - Get real-time quote
- `GET /stocks/{symbol}/profile` - Get company profile
- `GET /stocks/quotes?symbols=AAPL,GOOG` - Get multiple quotes
- `GET /stocks/search?query=apple` - Search stocks

#### Prediction APIs
- `GET /predictions/{ticker}` - Get 7-day price forecast
- `GET /predictions/supported-tickers` - List supported tickers
- `GET /predictions/check/{ticker}` - Check if ticker is supported

### Stock Prediction Service API

**Base URL**: `http://localhost:8000`

- `GET /` - Health check and supported tickers
- `POST /predict` - Predict with custom 60-day input
- `GET /latest_predict/{ticker}` - Auto-predict using latest data

**Example Request**:
```bash
curl http://localhost:8000/latest_predict/AAPL
```

### Quantum Optimization API

**Base URL**: `http://localhost:8001`

- `POST /optimize_portfolio` - Optimize portfolio using QUBO
- `GET /docs` - Interactive API documentation

**Example Request**:
```bash
curl -X POST http://localhost:8001/optimize_portfolio \
  -H "Content-Type: application/json" \
  -d '{
    "total_investment": 10000,
    "portfolio": [
      {"asset": "AAPL", "percentage": 45, "investment_value": 4500, "quantity": 3},
      {"asset": "GOOG", "percentage": 30, "investment_value": 3000, "quantity": 1}
    ]
  }'
```

### AI Summarizer API

**Base URL**: `http://localhost:8002`

- `GET /recommend?portfolio_json={...}` - Get AI recommendations

**Example Request**:
```bash
curl "http://localhost:8002/recommend?portfolio_json=%7B%22total_investment%22%3A10000%2C%22portfolio%22%3A%5B%7B%22asset%22%3A%22AAPL%22%2C%22percentage%22%3A50%7D%5D%7D"
```

## 🧪 Testing

### Backend Tests

```bash
cd backend
./mvnw test
```

### Frontend Tests

```bash
cd frontend
npm test
```

### API Testing

Use Swagger UI at http://localhost:8080/swagger-ui.html or tools like Postman/curl.

## 🔧 Configuration

### Backend Configuration

Edit `backend/src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_manager
spring.datasource.username=your_username
spring.datasource.password=your_password

# Finnhub API (Get free key at https://finnhub.io)
finnhub.api-key=your_finnhub_api_key

# Prediction Service
prediction.service-url=http://localhost:8000

# Server Port
server.port=8080
```

### Frontend Configuration

Edit `frontend/src/services/api.ts` to change API base URL if needed.

### Environment Variables

- **AI_SUMRIZER/.env**: `GEMINI_API_KEY=your_key`
- **Finnhub API Key**: Get free key at https://finnhub.io

## 🐛 Troubleshooting

### Backend Issues

**Port 8080 already in use**:
```bash
# Find and kill process
lsof -ti:8080 | xargs kill -9
```

**Database connection error**:
- Verify MySQL is running: `mysql -u root -p`
- Check credentials in `application.properties`
- Ensure database exists: `SHOW DATABASES;`

**Maven build fails**:
```bash
cd backend
./mvnw clean install -U
```

### Frontend Issues

**Port 5173 already in use**:
```bash
# Kill process or change port in vite.config.ts
lsof -ti:5173 | xargs kill -9
```

**npm install fails**:
```bash
rm -rf node_modules package-lock.json
npm install
```

### Python Service Issues

**Module not found**:
```bash
pip install -r requirements.txt
```

**Port already in use**:
```bash
# Find and kill process
lsof -ti:8000 | xargs kill -9  # For stock_prediction
lsof -ti:8001 | xargs kill -9  # For Quantum
lsof -ti:8002 | xargs kill -9  # For AI_SUMRIZER
```

**TensorFlow/Keras compatibility**:
- Ensure Python 3.9+ is used
- Models are compatible with TensorFlow 2.x

## 📦 Dependencies

### Backend (Java/Spring Boot)
- Spring Boot 4.0.2
- Spring Data JPA
- MySQL Connector
- SpringDoc OpenAPI (Swagger)
- Lombok

### Frontend (React/TypeScript)
- React 18
- Vite
- TypeScript
- Tailwind CSS
- React Query
- React Router
- Recharts
- Shadcn/UI components

### Python Services
- **Stock Prediction**: FastAPI, TensorFlow, Pandas, NumPy
- **Quantum**: FastAPI, NumPy, Pandas, yfinance, dimod, dwave-neal
- **AI Summarizer**: Flask, google-generativeai, python-dotenv

## 🎯 Usage Examples

### Create a Portfolio

```bash
curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "Content-Type: application/json" \
  -d '{
    "portfolioName": "Tech Portfolio",
    "description": "Technology stocks",
    "baseCurrency": "USD"
  }'
```

### Add an Asset

```bash
curl -X POST http://localhost:8080/api/v1/assets \
  -H "Content-Type: application/json" \
  -d '{
    "portfolioId": 1,
    "ticker": "AAPL",
    "assetName": "Apple Inc.",
    "assetType": "STOCK",
    "quantity": 10,
    "purchasePrice": 150.00
  }'
```
### Get Stock Quote

```bash
curl http://localhost:8080/api/v1/stocks/AAPL/quote
```

### Get Price Prediction

```bash
curl http://localhost:8080/api/v1/predictions/AAPL
```
---
## General FAQ'S Regarding AI & Quantum Services 
### Q1)What is QUBO-Based Portfolio Optimization?

QUBO-based portfolio optimization converts the traditional risk–return optimization problem into a **binary energy minimization problem**.

- Asset weights are encoded using **binary variables**
- Risk, return, and constraints are embedded into a single **loss (energy) function**
- The optimal portfolio corresponds to the **minimum-energy (ground-state) solution**

This formulation is compatible with **quantum annealers** and **quantum-inspired solvers**.
### Q2)QUBO vs Classical Portfolio Optimization

### Classical Methods (e.g., `scipy.optimize`, SLSQP, BFGS)
- Designed for **continuous, smooth, convex problems**
- Sensitive to **initial conditions**
- Can get stuck in **local minima**
- Poor support for **integer constraints** (whole shares, fixed units)
- Treats assets as independent variables with coefficients

### QUBO-Based Method
- Designed for **discrete, combinatorial problems**
- Explores a **non-convex energy landscape**
- Naturally handles **binary and integer constraints**
- Models asset relationships as **pairwise interactions (graph-based)**
- Searches for the **global minimum** using annealing
 
### Q3)QUBO Loss Function (Energy Function)

The QUBO loss function combines **return maximization**, **risk minimization**, and **constraint enforcement** into a single equation:

### Q4) What is “Entanglement” in This Context?
### Components
- **Return Term**  
  Rewards portfolios with higher expected returns.
- **Risk Term**  
  Penalizes portfolios with high covariance between assets.
- **Constraint Penalty**  
  Enforces rules like:
  - total allocation = 100%
  - no invalid weight combinations

The optimizer minimizes this energy to find the best portfolio.

### Entanglement Score (Quantum-Inspired Metric)
The `entanglement_score` measures how strongly one asset is **statistically coupled** with the rest of the portfolio.

### How it is computed
- Derived from the **covariance matrix**
- Calculated as the **average covariance** of an asset with all others

### Interpretation
- **Low entanglement score** → Asset is relatively independent (good for diversification)
- **High entanglement score** → Asset moves closely with others (higher systemic risk)
- **Negative score** → Asset may act as a hedge

### Q5) Why is LSTM used in stock prediction instead of traditional ML models?
Stock prices are **sequential and time-dependent**.  
LSTM (Long Short-Term Memory) networks are designed to:
- Learn **long-term dependencies**
- Capture **trends, momentum, and volatility**
- Handle **non-linear market behavior**

Traditional models cannot retain historical context effectively.


### Q6)  Why does prediction use only the last 60 days?
The last **60 trading days** represent the **most relevant market context**.  
Using older data during inference can:
- Introduce outdated patterns
- Reduce responsiveness to current trends

The model learns from long history but predicts using **recent behavior**.
### Q7)What is the confidence score?
The confidence score is a **reliability indicator** (ranging from **0.50 to 0.99**) that estimates how trustworthy a prediction is, based on:
- **Recent price volatility** (market stability)
- **Stability of the predicted trend** (model consistency)

**Interpretation of confidence ranges:**
- **0.85 – 0.99 (High Confidence):**  
  Market behavior is stable and the predicted trend is smooth; the forecast is considered reliable.
- **0.70 – 0.84 (Medium Confidence):**  
  Moderate volatility or minor fluctuations in prediction; results should be used with caution.
- **0.50 – 0.69 (Low Confidence):**  
  High volatility or unstable predictions; forecast uncertainty is elevated.

The confidence score helps users make **risk-aware decisions** by indicating when predictions should be trusted less due to market or model uncertainty.

It does **not** guarantee profit.

### Q8) How is the confidence score calculated?
The confidence score combines:
- **Historical volatility** of the last 60 days (market risk)
- **Prediction spread** of the 7-day forecast (model uncertainty)
Higher volatility or unstable predictions reduce confidence.
---
## 📝 Notes

- The application uses sample data on first run (via DataLoader)
- Pre-trained models are included for: AAPL, GOOG, MSFT, AMZN, META, NFLX
- Finnhub API has rate limits on free tier
- Quantum optimization uses Simulated Annealing (not actual quantum hardware)
- AI Summarizer requires valid Gemini API key

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is part of a team development exercise.

## 🔗 Additional Resources

- [Backend Design Guide](./Backend_Design_Guide.md)
- [Database Setup Guide](./Database_Setup.md)
- [Quick API Guide](./Quick_API_Guide.md)
- [Quantum Innovation Documentation](./Quantum_Innovation.md)
- [Quantum Service README](./Quantum/README.md)
- [AI Summarizer README](./AI_SUMRIZER/README.md)

---

**Happy Coding! 🚀**
