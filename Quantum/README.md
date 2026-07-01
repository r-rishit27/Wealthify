# Quantum Portfolio Optimization API

## Overview

This project implements a **Quantum-inspired Portfolio Optimization** system using **Quadratic Unconstrained Binary Optimization (QUBO)** principles. Unlike traditional portfolio management strategies that often rely on convex optimization (like Markowitz Mean-Variance), this system treats portfolio allocation as a **combinatorial optimization problem**, solving for the "Ground State" (lowest energy/risk configuration) of a Hamiltonian equation.

The core logic transforms continuous portfolio weights into discrete "Qubits" via binary expansion and uses **Simulated Annealing** to escape local minima, exploring the solution landscape to find global optima that balances risk, return, and diversification.

## Features

-   **Quantum-Inspired Algorithm**: Utilizes the Hamiltonian formulation to represent Risk, Return, and Budget constraints.
-   **Simulated Annealing**: Uses the `neal` library to simulate quantum tunneling effects for better optimization on complex landscapes.
-   **Entanglement-Aware**: Accounts for asset correlations (covariance) as "entanglement" penalties between assets.
-   **FastAPI Integration**: robust REST API for seamless integration with frontend or other services.
-   **Discrete Allocation**: Optimizes using binary integer slots, ensuring realistic unit allocations.

## Installation & Setup

1.  **Install Dependencies**:
    Ensure you have Python installed, then install the required packages:
    ```bash
    pip install fastapi uvicorn numpy pandas yfinance dimod neal
    ```

2.  **Run the Server**:
    Start the FastAPI server:
    ```bash
    uvicorn main:app --reload
    ```
    The API will be accessible at `http://127.0.0.1:8000`.

## API Documentation

### Endpoint: `/optimize_portfolio`

-   **Method**: `POST`
-   **Summary**: Accepts a user's current portfolio/investment constraints and returns a rebalanced portfolio optimized using the QUBO algorithm.

#### Algorithm Detailed Explanation

The `optimize_portfolio` endpoint triggers a sophisticated QUBO solver. Here is how the "Quantum Puzzle" is solved:

1.  **Binary Expansion (The "Qubits")**:
    Traditional weights (0-100%) are continuous. We discretize them using binary powers ($2^0, 2^1, ..., 2^6$).
    $$ Weight_i = \sum_{k} 2^k \cdot x_{i,k} $$
    Where $x_{i,k}$ is a binary variable (0 or 1). This allows us to map the problem onto a binary solver.

2.  **The Hamiltonian (Objective Function)**:
    We construct an energy equation $H$ that the solver tries to minimize:
    $$ H = \alpha \sum_{i,j} \sigma_{ij} w_i w_j - (1-\alpha) \sum_{i} \mu_i w_i + \lambda (\sum w_i - Total)^2 $$
    -   **Risk Term**: $\sum \sigma_{ij} w_i w_j$ minimizes variance (volatility). $\sigma_{ij}$ represents the covariance matrix.
    -   **Return Term**: $-\sum \mu_i w_i$ maximizes expected returns.
    -   **Constraint**: $(\sum w_i - Total)^2$ ensures the total weights sum to 100%.

3.  **Simulated Annealing**:
    Instead of standard gradient descent, we use Simulated Annealing. This algorithm mimics the cooling of metals, allowing the system to occasionally accept "worse" solutions (thermal fluctuations) to escape local minima, eventually settling into the **Ground State** (Global Minimum).

#### Sample Request (Input)

Send this JSON body to the `/optimize_portfolio` endpoint:

```json
{
  "total_investment": 10000,
  "portfolio": [
    {
      "asset": "AAPL",
      "percentage": 45,
      "investment_value": 4500,
      "quantity": 3
    },
    {
      "asset": "GOOG",
      "percentage": 30,
      "investment_value": 3000,
      "quantity": 1
    },
    {
      "asset": "MSFT",
      "percentage": 15,
      "investment_value": 1500,
      "quantity": 1
    },
    {
      "asset": "AMZN",
      "percentage": 10,
      "investment_value": 1000,
      "quantity": 0.5
    }
  ]
}
```

#### Sample Response (Output)

The API returns the Ground State configuration:

```json
{
  "total_investment": 10000.0,
  "optimization_method": "Simulated Annealing (QUBO)",
  "result": "Ground State Energy Minimized",
  "transmutation_summary": "Solved Hamiltonian for lowest energy configuration.",
  "portfolio": [
    {
      "asset": "AAPL",
      "weight_assignment": 23.0,
      "investment_value": 2300.0,
      "rationale": "Quantum-Annealing Optimized (Ground State)"
    },
    {
      "asset": "GOOG",
      "weight_assignment": 28.0,
      "investment_value": 2800.0,
      "rationale": "Quantum-Annealing Optimized (Ground State)"
    },
    {
      "asset": "MSFT",
      "weight_assignment": 25.0,
      "investment_value": 2500.0,
      "rationale": "Quantum-Annealing Optimized (Ground State)"
    },
    {
      "asset": "AMZN",
      "weight_assignment": 24.0,
      "investment_value": 2400.0,
      "rationale": "Quantum-Annealing Optimized (Ground State)"
    }
  ],
  "metrics": {
    "ground_state_energy": -99992.7378586721,
    "total_units_allocated": 100.0
  }
}
```
