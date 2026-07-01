# Quantum Portfolio Optimization Master Research

## Executive Summary
This document integrates the theoretical foundations of **Quadratic Unconstrained Binary Optimization (QUBO)** with a practical roadmap for implementation. By transforming a portfolio into a "Quantum Puzzle," we unlock superior risk-adjusted returns and a deeper understanding of asset interplay through "Entanglement Graphs."

---

## 1. The Core Innovation: Weight Transformation
Traditional portfolio management (like Markowitz) treats weights as continuous percentages (e.g., 23.456%). The Quantum approach transforms this:

###  How Weights are Assigned
1.  **Binary Discretization**: Portfolio weights are represented by "Quantum Bits" (Qubits). Instead of a continuous slider, we use a binary expansion (e.g., bits representing 1%, 2%, 4%, 8%).
2.  **Entanglement-Driven Allocation**: Weights are assigned not just by individual performance, but by finding the **Ground State** of a graph where edges represent correlations. If two assets are "Entangled" (Highly correlated), the system penalizes simultaneous high weights to force diversification.
3.  **Lot-Based Precision**: The weights naturally map to real-world units (e.g., number of shares or lots), making the output directly executable without rounding errors.

---

## 2. QUBO vs. Classical `scipy.optimize` (SLSQP/BFGS)
Why switch from the industry-standard `scipy.optimize` to a Quantum-inspired QUBO approach?

| Feature | `scipy.optimize.minimize` | Quantum QUBO Solver |
| :--- | :--- | :--- |
| **Optimization Landscape** | Assumes a smooth, convex surface. | Handles "bumpy," non-convex surfaces. |
| **Local Minima Trap** | High risk of getting stuck in "good enough" solutions. | Uses **Quantum Tunneling** to jump over risk barriers to find the Global Optimum. |
| **Integer Constraints** | Struggles with discrete units (can't buy 0.4 shares). | Naturally discrete; built for whole-number/unit logic. |
| **Asset Interplay** | Treat assets as independent variables in an equation. | Treats assets as a **Networked Graph** (Entanglement). |
| **Execution Time** | Scales linearly but slows down with complex constraints. | Solves combinatorial explosions exponentially faster (on Quantum Hardware). |

### The "Tunneling" Advantage
`scipy.optimize` works like a ball rolling down a hill; it stops at the first valley it finds. **QUBO (Quantum Annealing)** allows the ball to *pass through* the hill if there is a deeper, safer valley on the other side.

---

## 3. Practical Implementation Roadmap

### Step 1: Input Data Processing (The "Seed")
The system accepts your current portfolio as the starting "Quantum State":
```json
{
  "total_investment": 100,
  "portfolio": [
    {"asset": "AAPL", "percentage": 40, "investment_value": 40, "quantity": 0.20},
    {"asset": "GOOG", "percentage": 30, "investment_value": 30, "quantity": 0.015},
    {"asset": "GOLD", "percentage": 20, "investment_value": 20, "quantity": 0.01},
    {"asset": "US10Y", "percentage": 10, "investment_value": 10, "quantity": 0.10}
  ]
}
```

### Step 2: Data Ingestion & Covariance Calculation
- **Action**: Fetch historical daily closing prices.
- **Output**: Returns and Covariance Matrix ($\Sigma$). This matrix defines the "Entanglement" between assets.

### Step 3: QUBO Construction (The "Puzzle" Creation)
Build the Hamiltonian objective function:
$$H(x) = A \sum_{i,j} \sigma_{ij} x_i x_j - B \sum_{i} \mu_i x_i + C (\sum_i w_i x_i - Invest_{total})^2$$
where $\sigma_{ij}$ is the **Entanglement Edge** (Correlation).

### Step 4: Solver Execution
- **Method**: Use **Simulated Annealing** (via Python's `neal` library) to simulate a quantum search.
- **Outcome**: The algorithm identifies the "Ground State"—the set of weights with the lowest energy/risk.

---

## 4. "Quantum" Insights & Visualization Design
The value isn't just in the numbers; it's in the **Visual Rationale**.

### 🕸️ The Entanglement Graph
- **Concept**: A 3D web of your portfolio.
- **Node Size**: Final Weight Assignment.
- **Node Color**: Entanglement Score density.
- **Edge Thickness**: Degree of correlation.

---

## 5. Final Output: The Transmuted Portfolio
The resulting output justifies the weights through **Entanglement Scores**.

```json
{
  "total_investment": 100,
  "optimization_method": "Quantum-Annealing",
  "result": "Ground State Identified",
  "transmutation_summary": "Shifted weights from high-entanglement clusters to low-entanglement safe havens.",
  "portfolio": [
    {
      "asset": "AAPL",
      "weight_assignment": 35.0,
      "investment_value": 35.0,
      "entanglement_score": 0.88,
      "rationale": "High entanglement with GOOG; weight reduced to lower cluster risk."
    },
    {
      "asset": "GOOG",
      "weight_assignment": 25.0,
      "investment_value": 25.0,
      "entanglement_score": 0.82,
      "rationale": "High correlation with AAPL; optimized for tech sector ground state."
    },
    {
      "asset": "GOLD",
      "weight_assignment": 25.0,
      "investment_value": 25.0,
      "entanglement_score": -0.15,
      "rationale": "Negative entanglement with Tech; provides quantum-level diversification."
    },
    {
      "asset": "US10Y",
      "weight_assignment": 15.0,
      "investment_value": 15.0,
      "entanglement_score": -0.42,
      "rationale": "Anti-correlated with global risk; provides stability to the ground state."
    }
  ]
}
```

---

## 6. Conclusion
By migrating from `scipy.optimize` to a **QUBO-based framework**, you move from simple math to **Systemic Intelligence**. You aren't just calculating weights; you are solving the "Quantum Puzzle" of global interconnected risk.
