from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import numpy as np
import pandas as pd
import tensorflow as tf
import pickle
import os
#LOading Pre Trained Model Files .h5
class _CompatLSTM(tf.keras.layers.LSTM):
    def __init__(self, *args, **kwargs):
        kwargs.pop("time_major", None)
        super().__init__(*args, **kwargs)


app = FastAPI(title="Multi-Stock Price Forecasting API")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Load scalers at startup
SCALER_PATH = 'scalers.pkl'
DATA_PATH = 'stock_dataset.csv'

scalers = {}
models = {}

if os.path.exists(SCALER_PATH):
    with open(SCALER_PATH, 'rb') as f:
        scalers = pickle.load(f)

def get_model(ticker: str):
    ticker = ticker.upper()
    if ticker in models:
        return models[ticker]
    
    model_path = f'model_{ticker}.h5'
    if not os.path.exists(model_path):
        model_path = os.path.join('Model', f'model_{ticker}.h5')
    
    if os.path.exists(model_path):
        print(f"Loading model for {ticker} from {model_path}...")
        model = tf.keras.models.load_model(
            model_path, custom_objects={"LSTM": _CompatLSTM}
        )
        models[ticker] = model
        return model
    return None

class PredictionInput(BaseModel):
    ticker: str
    prices: list[float] 

@app.get("/")
def read_root():
    return {
        "message": "Multi-Stock Price Forecasting API with Confidence Score is running.",
        "supported_tickers": list(scalers.keys())
    }

def calculate_confidence(prediction, window_data):
    # Heuristic confidence score based on volatility and trend consistency
    # High volatility in the input window decreases confidence
    volatility = np.std(window_data) / np.mean(window_data)
    
    # Range of prediction (spread)
    pred_spread = (np.max(prediction) - np.min(prediction)) / np.mean(prediction)
    
    # Base confidence
    base_confidence = 0.95
    
    # Penalize high volatility and extreme spreads
    penalty = (volatility * 0.5) + (pred_spread * 0.2)
    confidence = max(0.5, min(0.99, base_confidence - penalty))
    
    return round(float(confidence), 2)

@app.post("/predict")
def predict(input_data: PredictionInput):
    ticker = input_data.ticker.upper()
    if ticker not in scalers:
        raise HTTPException(status_code=404, detail=f"No scaler found for ticker {ticker}")
    
    if len(input_data.prices) != 60:
        raise HTTPException(status_code=400, detail="Exactly 60 prices must be provided.")
    
    model = get_model(ticker)
    if model is None:
        raise HTTPException(status_code=500, detail=f"Model for {ticker} not found.")
    
    scaler = scalers[ticker]
    X_input = np.array(input_data.prices).reshape(-1, 1)
    scaled_input = scaler.transform(X_input)
    X = np.reshape(scaled_input, (1, 60, 1))
    
    prediction_scaled = model.predict(X)
    prediction = scaler.inverse_transform(prediction_scaled.reshape(7, 1))
    
    confidence_score = calculate_confidence(prediction, input_data.prices)
    
    return {
        "ticker": ticker,
        "forecast": prediction.flatten().tolist(),
        "confidence_score": confidence_score
    }

@app.get("/latest_predict/{ticker}")
def latest_predict(ticker: str):
    ticker = ticker.upper()
    if not os.path.exists(DATA_PATH):
        raise HTTPException(status_code=404, detail="Dataset not found.")
    
    df = pd.read_csv(DATA_PATH)
    ticker_df = df[df["Ticker"] == ticker].copy()
    if ticker_df.empty:
        raise HTTPException(status_code=404, detail=f"Ticker {ticker} not found.")

    ticker_df["Date"] = pd.to_datetime(ticker_df["Date"])
    ticker_df = ticker_df.sort_values("Date")
    
    last_60_days = ticker_df['Close'].tail(60).values
    if len(last_60_days) < 60:
        raise HTTPException(status_code=400, detail="Not enough data for this ticker.")
    
    model = get_model(ticker)
    if model is None or ticker not in scalers:
        raise HTTPException(status_code=500, detail=f"Model or Scaler for {ticker} not ready.")
    
    scaler = scalers[ticker]
    data = last_60_days.reshape(-1, 1)
    scaled_data = scaler.transform(data)
    X = np.reshape(scaled_data, (1, 60, 1))
    
    prediction_scaled = model.predict(X)
    prediction = scaler.inverse_transform(prediction_scaled.reshape(7, 1))
    
    confidence_score = calculate_confidence(prediction, last_60_days)
    
    last_date = ticker_df['Date'].iloc[-1]
    forecast_dates = [(last_date + pd.Timedelta(days=i+1)).strftime('%Y-%m-%d') for i in range(7)]
    
    return {
        "ticker": ticker,
        "last_observed_date": last_date.strftime('%Y-%m-%d'),
        "last_observed_price": float(last_60_days[-1]),
        "forecast": [
            {"date": d, "price": float(p)} for d, p in zip(forecast_dates, prediction.flatten().tolist())
        ],
        "confidence_score": confidence_score
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)
