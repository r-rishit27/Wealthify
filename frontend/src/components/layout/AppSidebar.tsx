import { useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import { 
  Home, 
  Briefcase, 
  TrendingUp, 
  Receipt, 
  ChevronDown,
  ChevronRight,
  Wallet,
  Bitcoin,
  HelpCircle,
  Users,
  BarChart3,
  PieChart,
  DollarSign,
  Building2
} from 'lucide-react';
import { cn } from '@/lib/utils';

interface NavItem {
  label: string;
  path: string;
  icon: React.ComponentType<{ className?: string }>;
  children?: { label: string; path: string }[];
}

const navItems: NavItem[] = [
  { label: 'Dashboard', path: '/', icon: Home },
  { label: 'Portfolios', path: '/portfolios', icon: Briefcase },
  { label: 'Market', path: '/market', icon: BarChart3 },
  { label: 'Transactions', path: '/transactions', icon: Receipt },
];

interface WatchlistItem {
  symbol: string;
  name: string;
  price: number;
  change: number;
  changePercent: number;
}

const mockWatchlist: WatchlistItem[] = [
  { symbol: 'S&P 500', name: 'S&P 500', price: 4549.78, change: 13.02, changePercent: 0.30 },
  { symbol: 'S&P 500', name: 'S&P 500', price: 4549.78, change: 13.02, changePercent: 0.30 },
];

interface AppSidebarProps {
  isCollapsed: boolean;
}

export const AppSidebar = ({ isCollapsed }: AppSidebarProps) => {
  const location = useLocation();
  const [expandedItems, setExpandedItems] = useState<string[]>(['Stock & Fund']);

  const toggleExpand = (label: string) => {
    setExpandedItems(prev =>
      prev.includes(label)
        ? prev.filter(item => item !== label)
        : [...prev, label]
    );
  };

  const isActive = (path: string) => {
    if (path === '/') {
      // Dashboard is active if on root or any /dashboard/:portfolioId route
      return location.pathname === '/' || location.pathname.startsWith('/dashboard/');
    }
    return location.pathname === path;
  };
  const isChildActive = (children?: { path: string }[]) =>
    children?.some(child => location.pathname === child.path);

  return (
    <aside
      className={cn(
        "min-h-screen bg-card border-r border-border flex flex-col overflow-hidden transition-all duration-200",
        isCollapsed ? "w-16" : "w-64"
      )}
    >
      {/* Logo */}
      <div className="p-4 flex items-center gap-3">
        <img 
          src="/PHOTO-2026-02-04-16-34-38.jpg" 
          alt="Logo" 
          className="w-10 h-10 rounded-full object-cover"
        />
        {!isCollapsed && (
          <span className="font-semibold text-sm">Wealthify</span>
        )}
      </div>

      {/* AI Watchlist */}
      {!isCollapsed && (
        <div className="px-4 mb-4">
          <p className="text-xs font-medium text-muted-foreground mb-3 tracking-wider">
            AI WATCHLIST
          </p>
          <div className="space-y-2">
            {mockWatchlist.map((item, idx) => (
              <div
                key={idx}
                className="p-3 rounded-lg border border-border bg-card hover:bg-muted/50 transition-colors cursor-pointer"
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded bg-primary/10 flex items-center justify-center">
                      <span className="text-[10px] font-semibold text-primary">S&P<br/>500</span>
                    </div>
                    <div>
                      <p className="text-sm font-medium">{item.symbol}</p>
                      <p className="text-xs text-muted-foreground tabular-nums">
                        {item.price.toLocaleString()}
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xs font-medium text-success">+{item.changePercent}%</p>
                    <p className="text-xs text-success tabular-nums">+{item.change}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Main Navigation */}
      <div className="flex-1 overflow-y-auto scrollbar-thin px-2">
        {!isCollapsed && (
          <p className="text-xs font-medium text-muted-foreground mb-3 tracking-wider px-2">
            MAIN MENU
          </p>
        )}
        <nav className="space-y-1">
          {navItems.map((item) => (
            <div key={item.label}>
              <NavLink
                to={item.path}
                className={({ isActive }) =>
                  cn(
                    "flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors",
                    isActive
                      ? "bg-primary/10 text-primary"
                      : "text-muted-foreground hover:bg-muted hover:text-foreground",
                    isCollapsed && "justify-center"
                  )
                }
              >
                <item.icon className="w-5 h-5" />
                {!isCollapsed && <span>{item.label}</span>}
              </NavLink>
            </div>
          ))}
        </nav>
      </div>
    </aside>
  );
};
