import { useEffect, useState, type FormEvent } from 'react';
import { clientBillableServicesApi } from '../../api/clientBillableServices';
import { toErrorMessage } from '../../api/http';
import type { ClientBillableService } from '../../types';
import styles from './clients.module.css';

interface ServiceListProps {
  clientId: number;
  onServicesChange?: (services: ClientBillableService[]) => void;
}

const currencyFormatter = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' });

export function ServiceList({ clientId, onServicesChange }: ServiceListProps) {
  const [services, setServices] = useState<ClientBillableService[]>([]);
  const [newName, setNewName] = useState('');
  const [newPrice, setNewPrice] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadServices();
  }, [clientId]);

  useEffect(() => {
    onServicesChange?.(services);
  }, [services, onServicesChange]);

  async function loadServices() {
    setIsLoading(true);
    setError(null);
    try {
      const response = await clientBillableServicesApi.listByClient(clientId);
      setServices(response.data);
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    const name = newName.trim();
    const price = Number(newPrice.replace(',', '.'));
    if (!name || !Number.isFinite(price) || price <= 0) {
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const created = await clientBillableServicesApi.create(clientId, name, price);
      setServices((current) => [...current, created]);
      setNewName('');
      setNewPrice('');
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete(service: ClientBillableService) {
    if (!window.confirm(`Eliminare il servizio "${service.name}"?`)) {
      return;
    }

    setError(null);
    try {
      await clientBillableServicesApi.remove(service.id);
      setServices((current) => current.filter((item) => item.id !== service.id));
    } catch (err) {
      setError(toErrorMessage(err));
    }
  }

  return (
    <div>
      <h2 className={styles.subtitle}>Servizi</h2>

      <form className={styles.formStacked} onSubmit={handleCreate}>
        <input
          className={styles.input}
          type="text"
          placeholder="Nome servizio"
          value={newName}
          onChange={(event) => setNewName(event.target.value)}
          disabled={isSubmitting}
        />
        <div className={styles.formRow}>
          <input
            className={styles.input}
            type="number"
            step="0.01"
            min="0"
            inputMode="decimal"
            placeholder="Prezzo (€)"
            value={newPrice}
            onChange={(event) => setNewPrice(event.target.value)}
            disabled={isSubmitting}
          />
          <button className={styles.button} type="submit" disabled={isSubmitting || !newName.trim() || !newPrice}>
            Aggiungi
          </button>
        </div>
      </form>

      {error && <p className={styles.error}>{error}</p>}

      {isLoading ? (
        <p className={styles.hint}>Caricamento...</p>
      ) : services.length === 0 ? (
        <p className={styles.hint}>Nessun servizio presente.</p>
      ) : (
        <ul className={styles.list}>
          {services.map((service) => (
            <li key={service.id} className={styles.listItem}>
              <span className={styles.listItemText}>
                {service.name} — {currencyFormatter.format(service.price)}
              </span>
              <button
                className={styles.deleteButton}
                onClick={() => handleDelete(service)}
                aria-label="Elimina servizio"
              >
                ✕
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
