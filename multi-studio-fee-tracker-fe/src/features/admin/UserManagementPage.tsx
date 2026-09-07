import { useEffect, useState, type FormEvent } from 'react';
import { adminApi, type AppUserSummary } from '../../api/admin';
import { toErrorMessage } from '../../api/http';
import styles from './admin.module.css';

export function UserManagementPage() {
  const [users, setUsers] = useState<AppUserSummary[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [newUsername, setNewUsername] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [isCreating, setIsCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);

  const [editingUsername, setEditingUsername] = useState<string | null>(null);
  const [passwordDraft, setPasswordDraft] = useState('');
  const [isSavingPassword, setIsSavingPassword] = useState(false);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);

  useEffect(() => {
    loadUsers();
  }, []);

  async function loadUsers() {
    setIsLoading(true);
    setError(null);
    try {
      const response = await adminApi.listUsers();
      setUsers(response);
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    const username = newUsername.trim();
    if (!username || newPassword.length < 8) {
      return;
    }

    setIsCreating(true);
    setCreateError(null);
    try {
      const created = await adminApi.createUser(username, newPassword);
      setUsers((current) => [...current, created]);
      setNewUsername('');
      setNewPassword('');
    } catch (err) {
      setCreateError(toErrorMessage(err));
    } finally {
      setIsCreating(false);
    }
  }

  function startEditingPassword(username: string) {
    setEditingUsername(username);
    setPasswordDraft('');
    setPasswordError(null);
    setPasswordSuccess(null);
  }

  async function handleSavePassword(username: string, event: FormEvent) {
    event.preventDefault();
    if (passwordDraft.length < 8) {
      return;
    }

    setIsSavingPassword(true);
    setPasswordError(null);
    try {
      await adminApi.setPassword(username, passwordDraft);
      setPasswordSuccess(`Password aggiornata per ${username}.`);
      setEditingUsername(null);
    } catch (err) {
      setPasswordError(toErrorMessage(err));
    } finally {
      setIsSavingPassword(false);
    }
  }

  return (
    <section>
      <h1 className={styles.title}>Gestione utenti</h1>

      <div className={styles.card}>
        <h2 className={styles.subtitle}>Nuovo utente</h2>
        <form className={styles.form} onSubmit={handleCreate}>
          <input
            className={styles.input}
            type="text"
            placeholder="Username (min. 3 caratteri)"
            value={newUsername}
            onChange={(event) => setNewUsername(event.target.value)}
            disabled={isCreating}
            autoComplete="off"
          />
          <input
            className={styles.input}
            type="password"
            placeholder="Password (min. 8 caratteri)"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
            disabled={isCreating}
            autoComplete="new-password"
          />
          <button
            className={styles.button}
            type="submit"
            disabled={isCreating || !newUsername.trim() || newPassword.length < 8}
          >
            Crea utente
          </button>
        </form>
        {createError && <p className={styles.error}>{createError}</p>}
      </div>

      {error && <p className={styles.error}>{error}</p>}
      {passwordSuccess && <p className={styles.success}>{passwordSuccess}</p>}

      {isLoading ? (
        <p className={styles.hint}>Caricamento...</p>
      ) : users.length === 0 ? (
        <p className={styles.hint}>Nessun utente presente.</p>
      ) : (
        <ul className={styles.list}>
          {users.map((user) => (
            <li key={user.username} className={styles.listItem}>
              <div className={styles.listItemHeader}>
                <span className={styles.username}>{user.username}</span>
                <span className={styles.roleBadge}>{user.role}</span>
              </div>

              {editingUsername === user.username ? (
                <form className={styles.passwordRow} onSubmit={(event) => handleSavePassword(user.username, event)}>
                  <input
                    className={styles.input}
                    type="password"
                    placeholder="Nuova password (min. 8 caratteri)"
                    value={passwordDraft}
                    onChange={(event) => setPasswordDraft(event.target.value)}
                    disabled={isSavingPassword}
                    autoComplete="new-password"
                  />
                  <button
                    className={styles.button}
                    type="submit"
                    disabled={isSavingPassword || passwordDraft.length < 8}
                  >
                    Salva
                  </button>
                  <button
                    type="button"
                    className={styles.secondaryButton}
                    onClick={() => setEditingUsername(null)}
                    disabled={isSavingPassword}
                  >
                    Annulla
                  </button>
                </form>
              ) : (
                <button className={styles.secondaryButton} onClick={() => startEditingPassword(user.username)}>
                  Cambia password
                </button>
              )}
              {editingUsername === user.username && passwordError && <p className={styles.error}>{passwordError}</p>}
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
