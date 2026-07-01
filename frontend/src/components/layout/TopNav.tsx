import { Menu, Bell } from 'lucide-react';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

interface TopNavProps {
  onToggleSidebar: () => void;
}

export const TopNav = ({ onToggleSidebar }: TopNavProps) => {
  return (
    <header className="h-16 border-b border-border bg-card flex items-center justify-between px-6">
      <div className="flex items-center gap-4">
        <button
          onClick={onToggleSidebar}
          className="w-9 h-9 rounded-full bg-muted flex items-center justify-center hover:bg-muted/80 transition-colors"
        >
          <Menu className="w-4 h-4 text-muted-foreground" />
        </button>
      </div>

      {/* Right side */}
      <div className="flex items-center gap-4">
        {/* Notifications */}
        <button className="w-10 h-10 rounded-full bg-muted flex items-center justify-center hover:bg-muted/80 transition-colors">
          <Bell className="w-5 h-5 text-muted-foreground" />
        </button>

        {/* User Profile */}
        <div className="flex items-center gap-3">
          <Avatar className="w-10 h-10">
            <AvatarImage src="https://www.shareindia.com/wp-content/uploads/2022/10/5-Lessons-Every-Successful-Investor-Has-Learned.webp" />
            <AvatarFallback className="bg-primary text-primary-foreground">BI</AvatarFallback>
          </Avatar>
          <div className="hidden md:block">
            <p className="text-sm font-medium">John Doe</p>
            <p className="text-xs text-muted-foreground">admin@wealthify.com</p>
          </div>
        </div>
      </div>
    </header>
  );
};
