import api from './api';

export enum NewsDirection {
  UP = 'UP',
  DOWN = 'DOWN',
  NEUTRAL = 'NEUTRAL',
}

export interface NewsItem {
  title: string;
  url: string;
  source: string;
  publishedAt: string;
  symbol?: string;
  direction: NewsDirection;
  summary?: string;
}

export interface NewsResponse {
  items: NewsItem[];
  fetchedAt: string;
}

export const newsService = {
  getTickerNews: async (): Promise<NewsResponse> => {
    const response = await api.get<NewsResponse>('/news/ticker');
    return response.data;
  },
};
