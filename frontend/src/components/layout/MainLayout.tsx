import { ReactNode, useState } from 'react';
import { AppSidebar } from './AppSidebar';
import { TopNav } from './TopNav';
import { NewsTicker } from './NewsTicker';

interface MainLayoutProps {
  children: ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);

  return (
    <div className="min-h-screen flex w-full bg-background">
      <AppSidebar isCollapsed={isSidebarCollapsed} />
      <div className="flex-1 flex flex-col">
        <TopNav
          onToggleSidebar={() => setIsSidebarCollapsed((prev) => !prev)}
        />
        <NewsTicker />
        <main className="flex-1 p-6 overflow-y-auto">
          {children}
        </main>
      </div>
    </div>
  );
};
