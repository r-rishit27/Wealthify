
import requests
import json

url = "http://127.0.0.1:8000/optimize_portfolio"

payload = {
  "total_investment": 1000,
  "portfolio": [
    {
      "asset": "AAPL",
      "percentage": 45,
      "investment_value": 450,
      "quantity": 3
    },
    {
      "asset": "GOOG",
      "percentage": 30,
      "investment_value": 300,
      "quantity": 1
    },
    {
      "asset": "MSFT",
      "percentage": 15,
      "investment_value": 150,
      "quantity": 1
    },
    {
      "asset": "AMZN",
      "percentage": 10,
      "investment_value": 100,
      "quantity": 0.5
    }
  ]
}

try:
    print("Sending optimization request...")
    response = requests.post(url, json=payload)
    
    if response.status_code == 200:
        print("Optimization Successful!")
        print(json.dumps(response.json(), indent=2))
    else:
        print(f"Error {response.status_code}: {response.text}")
        
except Exception as e:
    print(f"Request failed: {e}")
