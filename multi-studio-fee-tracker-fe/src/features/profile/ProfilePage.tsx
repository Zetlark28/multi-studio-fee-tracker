import { useState, type FormEvent } from 'react';
import { authApi } from '../../api/auth';
import { toErrorMessage } from '../../api/http';
import { getUsername } from '../../api/session';
import styles from './profile.module.css';

export function ProfilePage() {
  const username = getUsername();
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const mismatch = confirmPassword.length > 0 && newPassword !== confirmPassword;
  const canSubmit = !!username && oldPassword.length > 0 && newPassword.length >= 8 && newPassword === confirmPassword;

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!canSubmit || !username) {
      return;
    }

    setIsSaving(true);
    setError(null);
    setSuccess(null);
    try {
      await authApi.changePassword(username, oldPassword, newPassword);
      setSuccess('Password aggiornata con successo.');
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsSaving(false);
    }
  }

  return (
    <section>
      <h1 className={styles.title}>Profilo</h1>

      <div className={styles.card}>
        <p className={styles.hint}>Utente: {username}</p>

        <form className={styles.form} onSubmit={handleSubmit}>
          <input
            className={styles.input}
            type="password"
            placeholder="Password attuale"
            value={oldPassword}
            onChange={(event) => setOldPassword(event.target.value)}
            disabled={isSaving}
            autoComplete="current-password"
          />
          <input
            className={styles.input}
            type="password"
            placeholder="Nuova password (min. 8 caratteri)"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
            disabled={isSaving}
            autoComplete="new-password"
          />
          <input
            className={styles.input}
            type="password"
            placeholder="Conferma nuova password"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
            disabled={isSaving}
            autoComplete="new-password"
          />
          {mismatch && <p className={styles.error}>Le password non coincidono.</p>}
          <button className={styles.button} type="submit" disabled={isSaving || !canSubmit}>
            {isSaving ? 'Salvataggio...' : 'Cambia password'}
          </button>
        </form>

        {error && <p className={styles.error}>{error}</p>}
        {success && <p className={styles.success}>{success}</p>}
      </div>
    </section>
  );
}
