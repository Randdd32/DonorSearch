import { useState, useRef, useEffect } from 'react';
import { Outlet, Link, useLocation, useNavigate } from 'react-router-dom';
import { clsx } from 'clsx';
import { Server, ListChecks, Link as LinkIcon, LogOut, Sun, Moon, Menu, Monitor, ChevronDown, User } from 'lucide-react';
import { useUiStore } from '../../store/uiStore';
import styles from './DashboardLayout.module.css';

export const DashboardLayout = () => {
  const { theme, toggleTheme, isSidebarOpen, toggleSidebar } = useUiStore();
  const location = useLocation();
  const navigate = useNavigate();

  const [isProfileOpen, setIsProfileOpen] = useState(false);
  const profileRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (profileRef.current && !profileRef.current.contains(event.target as Node)) {
        setIsProfileOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  },[]);

  const handleLogout = () => {
    setIsProfileOpen(false);
    navigate('/');
  };

  return (
    <div className={styles.layout}>
      <aside className={clsx(styles.sidebar, { [styles.closed]: !isSidebarOpen })}>
        <div className={styles.sidebarHeader}>
          <Link to="/" className={styles.logo}>
            <Monitor size={24} className={styles.logoIcon} />
            {isSidebarOpen && <span>DonorSearch</span>}
          </Link>
        </div>
        
        <nav className={styles.nav}>
          <Link 
            to="/" 
            className={clsx(styles.navItem, { [styles.active]: location.pathname === '/' })}
          >
            <Server size={20} className={styles.navIcon} />
            {isSidebarOpen && <span>Учетные единицы</span>}
          </Link>

          <Link 
            to="/compatibility" 
            className={clsx(styles.navItem, { [styles.active]: location.pathname === '/compatibility' })}
          >
            <ListChecks size={20} className={styles.navIcon} />
            {isSidebarOpen && <span>Правила совместимости</span>}
          </Link>

          <Link 
            to="/mappings" 
            className={clsx(styles.navItem, { [styles.active]: location.pathname === '/mappings' })}
          >
            <LinkIcon size={20} className={styles.navIcon} />
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
            
            <div className={styles.profileWrapper} ref={profileRef}>
              <div className={styles.userProfile} onClick={() => setIsProfileOpen(!isProfileOpen)}>
                <div className={styles.avatar}>A</div>
                <div className={styles.userInfo}>
                  <span className={styles.userName}>Admin</span>
                  <span className={styles.userRole}>Суперадминистратор</span>
                </div>
                <ChevronDown size={16} className={clsx(styles.chevron, { [styles.rotated]: isProfileOpen })} />
              </div>
              
              {isProfileOpen && (
                <div className={styles.dropdownMenu}>
                  <Link to="/profile" className={styles.dropdownItem} onClick={() => setIsProfileOpen(false)}>
                    <User size={16} />
                    Мой профиль
                  </Link>
                  <div className={styles.dropdownDivider} />
                  <button className={clsx(styles.dropdownItem, styles.logoutBtn)} onClick={handleLogout}>
                    <LogOut size={16} />
                    Выйти из системы
                  </button>
                </div>
              )}
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