import { useEffect, useState } from 'react';
import { clearSession, getIsAdmin, getUsername, subscribe } from './api/session';
import { ClientsPage } from './features/clients/ClientsPage';
import { DashboardPage } from './features/dashboard/DashboardPage';
import { HistoryPage } from './features/history/HistoryPage';
import { HomePage } from './features/home/HomePage';
import { LoginPage } from './features/auth/LoginPage';
import { ProfilePage } from './features/profile/ProfilePage';
import { UserManagementPage } from './features/admin/UserManagementPage';
import styles from './App.module.css';

type Section = 'dashboard' | 'home' | 'storico' | 'clienti' | 'profilo';

function App() {
  const [username, setUsername] = useState(getUsername());
  const [isAdmin, setIsAdmin] = useState(getIsAdmin());
  const [section, setSection] = useState<Section>('dashboard');
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  useEffect(
    () =>
      subscribe(() => {
        setUsername(getUsername());
        setIsAdmin(getIsAdmin());
      }),
    [],
  );

  if (!username) {
    return <LoginPage />;
  }

  function selectSection(next: Section) {
    setSection(next);
    setIsSidebarOpen(false);
  }

  return (
    <div className={styles.app}>
      <header className={styles.header}>
        <button className={styles.menuButton} aria-label="Apri menu" onClick={() => setIsSidebarOpen(true)}>
          ☰
        </button>
        <h1 className={styles.brand}>Multi Studio Fee Tracker</h1>
      </header>

      {isSidebarOpen && <div className={styles.backdrop} onClick={() => setIsSidebarOpen(false)} />}

      <aside className={isSidebarOpen ? styles.sidebarOpen : styles.sidebar}>
        <div className={styles.sidebarUser}>{username}</div>
        <nav className={styles.sidebarNav}>
          {isAdmin ? (
            <>
              <button
                className={section === 'dashboard' ? styles.sidebarLinkActive : styles.sidebarLink}
                onClick={() => selectSection('dashboard')}
              >
                Gestione utenti
              </button>
              <button
                className={section === 'profilo' ? styles.sidebarLinkActive : styles.sidebarLink}
                onClick={() => selectSection('profilo')}
              >
                Profilo
              </button>
            </>
          ) : (
            <>
              <button
                className={section === 'dashboard' ? styles.sidebarLinkActive : styles.sidebarLink}
                onClick={() => selectSection('dashboard')}
              >
                Dashboard
              </button>
              <button
                className={section === 'home' ? styles.sidebarLinkActive : styles.sidebarLink}
                onClick={() => selectSection('home')}
              >
                Registra attività
              </button>
              <button
                className={section === 'storico' ? styles.sidebarLinkActive : styles.sidebarLink}
                onClick={() => selectSection('storico')}
              >
                Storico
              </button>
              <button
                className={section === 'clienti' ? styles.sidebarLinkActive : styles.sidebarLink}
                onClick={() => selectSection('clienti')}
              >
                Clienti
              </button>
              <button
                className={section === 'profilo' ? styles.sidebarLinkActive : styles.sidebarLink}
                onClick={() => selectSection('profilo')}
              >
                Profilo
              </button>
            </>
          )}
        </nav>
        <button className={styles.logoutButton} onClick={clearSession}>
          Esci
        </button>
      </aside>

      <main className={styles.main}>
        {section === 'profilo' ? (
          <ProfilePage />
        ) : isAdmin ? (
          <UserManagementPage />
        ) : section === 'dashboard' ? (
          <DashboardPage />
        ) : section === 'home' ? (
          <HomePage />
        ) : section === 'storico' ? (
          <HistoryPage />
        ) : (
          <ClientsPage />
        )}
      </main>
    </div>
  );
}

export default App;
