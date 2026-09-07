import { useEffect, useState, type FormEvent } from 'react';
import { clientsApi } from '../../api/clients';
import { toErrorMessage } from '../../api/http';
import { getUsername } from '../../api/session';
import type { Client } from '../../types';
import styles from './clients.module.css';

interface ClientListProps {
  onSelectClient: (id: number) => void;
}

export function ClientList({ onSelectClient }: ClientListProps) {
  const [clients, setClients] = useState<Client[]>([]);
  const [newClientName, setNewClientName] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadClients();
  }, []);

  async function loadClients() {
    setIsLoading(true);
    setError(null);
    try {
      const response = await clientsApi.list();
      setClients(response.data);
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    const name = newClientName.trim();
    if (!name) {
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const created = await clientsApi.create(name);
      setClients((current) => [...current, created]);
      setNewClientName('');
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete(client: Client) {
    if (!window.confirm(`Eliminare "${client.name}"?`)) {
      return;
    }

    setError(null);
    try {
      await clientsApi.remove(client.id);
      setClients((current) => current.filter((item) => item.id !== client.id));
    } catch (err) {
      setError(toErrorMessage(err));
    }
  }

  return (
    <section>
      <h1 className={styles.title}>Clienti</h1>

      <form className={styles.form} onSubmit={handleCreate}>
        <input
          className={styles.input}
          type="text"
          placeholder="Nome nuovo cliente"
          value={newClientName}
          onChange={(event) => setNewClientName(event.target.value)}
          disabled={isSubmitting}
        />
        <button className={styles.button} type="submit" disabled={isSubmitting || !newClientName.trim()}>
          Aggiungi
        </button>
      </form>

      {error && <p className={styles.error}>{error}</p>}

      {isLoading ? (
        <p className={styles.hint}>Caricamento...</p>
      ) : clients.length === 0 ? (
        <p className={styles.hint}>Nessun cliente presente.</p>
      ) : (
        <ul className={styles.list}>
          {clients.map((client) => (
            <li key={client.id} className={styles.listItem}>
              <button className={styles.listItemButton} onClick={() => onSelectClient(client.id)}>
                {client.name}
                {client.createdBy && client.createdBy !== getUsername() && (
                  <span className={styles.ownerLabel}> — {client.createdBy}</span>
                )}
              </button>
              <button
                className={styles.deleteButton}
                onClick={() => handleDelete(client)}
                aria-label={`Elimina ${client.name}`}
              >
                ✕
              </button>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}
