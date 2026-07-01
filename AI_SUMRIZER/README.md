# AI Portfolio Sumrizer

Minimalist API for AI-powered portfolio recommendations using Gemini 2.5 Flash.

## Setup

1. **Install Dependencies**:
   ```bash
   pip install -r requirements.txt
   ```

2. **Configure API Key**:
   Add your Gemini API key to `.env`:
   ```env
   GEMINI_API_KEY=your_actual_api_key_here
   ```

3. **Run**:
   ```bash
   python app.py
   ```

## API Usage

### Endpoint
- **URL**: `/recommend`
- **Method**: `GET`
- **Query Parameter**: `portfolio_json` (A URL-encoded JSON string representing the portfolio)

### Sample Usage
`GET http://localhost:8000/recommend?portfolio_json={"total_investment":100,"portfolio":[{"asset":"AAPL","percentage":40,"investment_value":40,"quantity":0.20},{"asset":"GOOG","percentage":30,"investment_value":30,"quantity":0.015},{"asset":"GOLD","percentage":20,"investment_value":20,"quantity":0.01},{"asset":"US10Y","percentage":10,"investment_value":10,"quantity":0.10}]}`

### Sample Portfolio JSON Structure
```json
{
  "total_investment": 100,
  "portfolio": [
    {
      "asset": "AAPL",
      "percentage": 40,
      "investment_value": 40,
      "quantity": 0.20
    },
    {
      "asset": "GOOG",
      "percentage": 30,
      "investment_value": 30,
      "quantity": 0.015
    },
    {
      "asset": "GOLD",
      "percentage": 20,
      "investment_value": 20,
      "quantity": 0.01
    },
    {
      "asset": "US10Y",
      "percentage": 10,
      "investment_value": 10,
      "quantity": 0.10
    }
  ]
}
```
