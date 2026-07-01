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

export interface SummarizerResponse {
  summary: string;
  status: string;
  model_used: string;
}

const AI_SUMMARIZER_SERVICE_URL = import.meta.env.VITE_AI_SUMMARIZER_SERVICE_URL || 'http://localhost:8002';

export const aiSummarizerService = {
  getRecommendation: async (portfolioData: PortfolioRequest): Promise<SummarizerResponse> => {
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
      const portfolioJson = encodeURIComponent(JSON.stringify(sanitizedData));
      const response = await axios.get<SummarizerResponse>(
        `${AI_SUMMARIZER_SERVICE_URL}/recommend`,
        { params: { portfolio_json: portfolioJson } }
      );
      return response.data;
    } catch (error: any) {
      console.error('AI Summarizer Error:', error.response?.data || error.message);
      console.error('Request data:', portfolioData);
      const detail = error.response?.data?.detail;
      const msg = typeof detail === 'string' ? detail : error.message;
      throw new Error(msg || 'AI summarizer request failed');
    }
  },
};
