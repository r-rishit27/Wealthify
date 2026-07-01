import axios from 'axios';

export interface Asset {
  asset: string;
  percentage: number;
  investment_value: number;
  quantity: number;
}

export interface PortfolioRequest {
  total_investment: number;
  portfolio: Asset[];
}

export interface OptimizedAsset {
  asset: string;
  weight_assignment: number;
  investment_value: number;
  entanglement_score: number;
  rationale: string;
}

export interface OptimizationResponse {
  total_investment: number;
  optimization_method: string;
  result: string;
  transmutation_summary: string;
  portfolio: OptimizedAsset[];
}

const QUANTUM_SERVICE_URL = import.meta.env.VITE_QUANTUM_SERVICE_URL || 'http://localhost:8001';

export const quantumOptimizationService = {
  optimize: async (portfolioData: PortfolioRequest): Promise<OptimizationResponse> => {
    try {
      // Ensure all values are numbers
      const sanitizedData: PortfolioRequest = {
        total_investment: Number(portfolioData.total_investment) || 0,
        portfolio: portfolioData.portfolio.map(asset => ({
          asset: asset.asset,
          percentage: Number(asset.percentage) || 0,
          investment_value: Number(asset.investment_value) || 0,
          quantity: Number(asset.quantity) || 0,
        })),
      };
      const response = await axios.post<OptimizationResponse>(
        `${QUANTUM_SERVICE_URL}/optimize_portfolio`,
        sanitizedData,
        { headers: { 'Content-Type': 'application/json' } }
      );
      return response.data;
    } catch (error: any) {
      console.error('Quantum Optimization Error:', error.response?.data || error.message);
      console.error('Request data:', portfolioData);
      throw error;
    }
  },
};
