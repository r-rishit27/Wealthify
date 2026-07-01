import { useState, useEffect } from 'react';
import { useNewsTicker } from '@/hooks/useNewsTicker';
import { NewsItem, NewsDirection } from '@/services/newsService';
import { cn } from '@/lib/utils';

export const NewsTicker = () => {
  const { data, isLoading, isError } = useNewsTicker();
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isPaused, setIsPaused] = useState(false);

  const newsItems = data?.items || [];

  // Auto-rotate news items every 5 seconds
  useEffect(() => {
    if (newsItems.length <= 1 || isPaused) return;

    const interval = setInterval(() => {
      setCurrentIndex((prev) => (prev + 1) % newsItems.length);
    }, 5000);

    return () => clearInterval(interval);
  }, [newsItems.length, isPaused]);

  // Don't render if no news or error
  if (isError || (!isLoading && newsItems.length === 0)) {
    return null;
  }

  const currentItem: NewsItem | undefined = newsItems[currentIndex];

  const getDirectionIcon = (direction: NewsDirection) => {
    switch (direction) {
      case NewsDirection.UP:
        return <span className="text-success text-sm">📈</span>;
      case NewsDirection.DOWN:
        return <span className="text-destructive text-sm">📉</span>;
      default:
        return <span className="w-3 h-3 flex items-center justify-center text-muted-foreground">•</span>;
    }
  };

  return (
    <div
      className="w-full bg-sidebar border-b border-border px-4 py-2 flex items-center gap-4 text-xs"
      onMouseEnter={() => setIsPaused(true)}
      onMouseLeave={() => setIsPaused(false)}
    >
      {/* Label */}
      <div className="flex-shrink-0 font-semibold text-muted-foreground uppercase tracking-wider">
        Market News
      </div>

      {/* News Items */}
      <div className="flex-1 overflow-hidden relative h-5">
        {isLoading ? (
          <div className="flex items-center gap-2 text-muted-foreground">
            <div className="w-3 h-3 border-2 border-muted-foreground border-t-transparent rounded-full animate-spin" />
            <span>Loading news...</span>
          </div>
        ) : currentItem ? (
          <div
            key={currentIndex}
            className="flex items-center gap-2 animate-fade-in transition-opacity duration-300"
            style={{ animation: 'fadeIn 0.3s ease-in-out' }}
          >
            {getDirectionIcon(currentItem.direction)}
            <a
              href={currentItem.url}
              target="_blank"
              rel="noopener noreferrer"
              className={cn(
                "hover:underline cursor-pointer transition-all",
                "text-foreground hover:text-primary"
              )}
            >
              <span className="font-medium">{currentItem.title}</span>
              {currentItem.source && (
                <span className="text-muted-foreground ml-2">
                  • {currentItem.source}
                </span>
              )}
            </a>
          </div>
        ) : null}
      </div>

      {/* Navigation dots */}
      {newsItems.length > 1 && (
        <div className="flex-shrink-0 flex items-center gap-1">
          {newsItems.slice(0, Math.min(5, newsItems.length)).map((_, idx) => (
            <button
              key={idx}
              onClick={() => setCurrentIndex(idx)}
              className={cn(
                "w-1.5 h-1.5 rounded-full transition-all",
                idx === currentIndex
                  ? "bg-primary w-4"
                  : "bg-muted-foreground/50 hover:bg-muted-foreground"
              )}
              aria-label={`Go to news item ${idx + 1}`}
            />
          ))}
          {newsItems.length > 5 && (
            <span className="text-muted-foreground text-[10px] ml-1">
              +{newsItems.length - 5}
            </span>
          )}
        </div>
      )}
    </div>
  );
};
