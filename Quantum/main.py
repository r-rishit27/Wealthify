from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional, Any
import uvicorn
from qubo_solver import QuboPortfolioOptimizer

app = FastAPI(
    title="Quantum Portfolio Optimization API",
    description="API for optimizing investment portfolios using QUBO (Quadratic Unconstrained Binary Optimization) and Simulated Annealing.",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class PortfolioItem(BaseModel):
    asset: str
    percentage: float
    investment_value: float
    quantity: float

class PortfolioRequest(BaseModel):
    total_investment: float
    portfolio: List[PortfolioItem]

@app.post("/optimize_portfolio", summary="Optimize Portfolio", tags=["Optimization"])
def optimize_portfolio(request: PortfolioRequest):
    """
    Accepts a portfolio configuration and returns a rebalanced portfolio optimized using Quantum-inspired algorithms.
    """
    try:
        # Ensure numeric types
        data = request.dict()
        data['total_investment'] = float(data.get('total_investment', 0))
        for item in data.get('portfolio', []):
            item['percentage'] = float(item.get('percentage', 0))
            item['investment_value'] = float(item.get('investment_value', 0))
            item['quantity'] = float(item.get('quantity', 0))
        
        optimizer = QuboPortfolioOptimizer(data)
        result = optimizer.optimize()
        return result
    except ValueError as e:
        raise HTTPException(status_code=400, detail=f"Invalid data format: {str(e)}")
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Optimization failed: {str(e)}")

if __name__ == "__main__":
    print("Starting Quantum Portfolio Optimization API on port 8001...")
    uvicorn.run("main:app", host="0.0.0.0", port=8001, reload=True)
