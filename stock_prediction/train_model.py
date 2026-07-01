import pandas as pd
import numpy as np
import pickle
from sklearn.preprocessing import MinMaxScaler
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout, Bidirectional
import os

np.random.seed(42)

def train_lstm_model(csv_path):
    # Load data
    df = pd.read_csv(csv_path)
    tickers = df['Ticker'].unique()
    
    # Store scalers for each ticker
    scalers = {}
    
    for ticker in tickers:
        print(f"\n--- Training Improved Model for {ticker} ---")
        ticker_df = df[df['Ticker'] == ticker].copy()
        ticker_df['Date'] = pd.to_datetime(ticker_df['Date'])
        ticker_df = ticker_df.sort_values('Date')
        
        # Use Close price
        data = ticker_df.filter(['Close']).values
        
        # Scale data
        scaler = MinMaxScaler(feature_range=(0, 1))
        scaled_data = scaler.fit_transform(data)
        scalers[ticker] = scaler
        
        # Create sequences (Window: 60 days -> Forecast: 7 days)
        window_size = 60
        prediction_days = 7
        
        X, y = [], []
        for i in range(window_size, len(scaled_data) - prediction_days + 1):
            X.append(scaled_data[i-window_size:i, 0])
            y.append(scaled_data[i:i+prediction_days, 0])
            
        X, y = np.array(X), np.array(y)
        X = np.reshape(X, (X.shape[0], X.shape[1], 1))
        
        # Improved LSTM Architecture: Bidirectional LSTM + More Units
        model = Sequential([
            Bidirectional(LSTM(units=100, return_sequences=True), input_shape=(X.shape[1], 1)),
            Dropout(0.2),
            LSTM(units=100, return_sequences=False),
            Dropout(0.2),
            Dense(units=50, activation='relu'),
            Dense(units=prediction_days)
        ])
        
        model.compile(optimizer='adam', loss='huber_loss') # Huber loss is more robust to outliers
        
        # Train
        model.fit(X, y, batch_size=32, epochs=20, verbose=1)
        
        # Save Model for each ticker
        model.save(f'model_{ticker}.h5')
        print(f"Model for {ticker} saved.")

    # Save all scalers
    with open('scalers.pkl', 'wb') as f:
        pickle.dump(scalers, f)
    
    print("\nAll models and scalers saved successfully.")

if __name__ == "__main__":
    csv_file = r'c:\stock_prediction\stock_dataset.csv'
    if os.path.exists(csv_file):
        train_lstm_model(csv_file)
    else:
        print(f"File not found: {csv_file}")
