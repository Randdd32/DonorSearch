import { Outlet, Link, useLocation } from 'react-router-dom';
import { clsx } from 'clsx';
import { LayoutDashboard, Settings, LogOut, Sun, Moon, Menu, Monitor } from 'lucide-react';
import { useUiStore } from '../../store/uiStore';
import styles from './DashboardLayout.module.css';

export const DashboardLayout = () => {
  const { theme, toggleTheme, isSidebarOpen, toggleSidebar } = useUiStore();
  const location = useLocation();

  return (
    <div className={styles.layout}>
      <aside className={clsx(styles.sidebar, { [styles.closed]: !isSidebarOpen })}>
        <div className={styles.sidebarHeader}>
          <div className={styles.logo}>
            <Monitor size={24} className={styles.logoIcon} />
            {isSidebarOpen && <span>DonorSearch</span>}
          </div>
        </div>
        
        <nav className={styles.nav}>
          <Link 
            to="/" 
            className={clsx(styles.navItem, { [styles.active]: location.pathname === '/' })}
          >
            <Monitor size={20} />
            {isSidebarOpen && <span>Учетные единицы</span>}
          </Link>

          <Link 
            to="/compatibility" 
            className={clsx(styles.navItem, { [styles.active]: location.pathname === '/compatibility' })}
          >
            <LayoutDashboard size={20} />
            {isSidebarOpen && <span>Правила совместимости</span>}
          </Link>

          <Link 
            to="/mappings" 
            className={clsx(styles.navItem, { [styles.active]: location.pathname === '/mappings' })}
          >
            <Settings size={20} />
            {isSidebarOpen && <span>Таблица сопоставления</span>}
          </Link>
        </nav>
      </aside>

      <div className={styles.mainWrapper}>
        <header className={styles.header}>
          <div className={styles.headerLeft}>
            <button onClick={toggleSidebar} className={styles.iconButton}>
              <Menu size={20} />
            </button>
            <h2 className={styles.pageTitle}>Панель управления</h2>
          </div>
          
          <div className={styles.headerRight}>
            <button onClick={toggleTheme} className={styles.iconButton} title="Сменить тему">
              {theme === 'light' ? <Moon size={20} /> : <Sun size={20} />}
            </button>
            
            <div className={styles.userProfile}>
              <div className={styles.avatar}>A</div>
              <div className={styles.userInfo}>
                <span className={styles.userName}>Admin</span>
                <span className={styles.userRole}>Суперадминистратор</span>
              </div>
              <button className={styles.iconButton} title="Выйти">
                <LogOut size={18} />
              </button>
            </div>
          </div>
        </header>

        <main className={styles.content}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};