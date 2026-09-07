import { useEffect, useState, type FormEvent } from 'react';
import { clientsApi } from '../../api/clients';
import { toErrorMessage } from '../../api/http';
import type { Client, ClientBillableService, FeeType } from '../../types';
import { ServiceList } from './ServiceList';
import { ActivityList } from './ActivityList';
import styles from './clients.module.css';

interface ClientDetailProps {
  clientId: number;
  onBack: () => void;
}

export function ClientDetail({ clientId, onBack }: ClientDetailProps) {
  const [client, setClient] = useState<Client | null>(null);
  const [services, setServices] = useState<ClientBillableService[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isCancelled = false;
    setIsLoading(true);
    setError(null);

    clientsApi
      .getById(clientId)
      .then((result) => {
        if (!isCancelled) {
          setClient(result);
        }
      })
      .catch((err) => {
        if (!isCancelled) {
          setError(toErrorMessage(err));
        }
      })
      .finally(() => {
        if (!isCancelled) {
          setIsLoading(false);
        }
      });

    return () => {
      isCancelled = true;
    };
  }, [clientId]);

  return (
    <section>
      <button className={styles.backButton} onClick={onBack}>
        ← Clienti
      </button>

      {isLoading && <p className={styles.hint}>Caricamento...</p>}
      {error && <p className={styles.error}>{error}</p>}

      {client && (
        <>
          <h1 className={styles.title}>{client.name}</h1>
          <FeeSettingsForm client={client} onSaved={setClient} />
          <ServiceList clientId={client.id} onServicesChange={setServices} />
          <ActivityList client={client} services={services} />
        </>
      )}
    </section>
  );
}

interface FeeSettingsFormProps {
  client: Client;
  onSaved: (client: Client) => void;
}

function FeeSettingsForm({ client, onSaved }: FeeSettingsFormProps) {
  const [type, setType] = useState<FeeType | ''>(client.type ?? '');
  const [fee, setFee] = useState(client.fee != null ? String(client.fee) : '');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!type) {
      return;
    }
    const parsedFee = Number(fee.replace(',', '.'));
    if (!Number.isFinite(parsedFee) || parsedFee <= 0) {
      return;
    }

    setIsSubmitting(true);
    setError(null);
    try {
      const updated = await clientsApi.update(client.id, client.name, parsedFee, type);
      onSaved(updated);
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div>
      <h2 className={styles.subtitle}>Tariffa</h2>
      <form className={styles.formStacked} onSubmit={handleSubmit}>
        <div className={styles.formRow}>
          <select
            className={styles.select}
            value={type}
            onChange={(event) => setType(event.target.value as FeeType)}
            disabled={isSubmitting}
          >
            <option value="" disabled>
              Seleziona tipo tariffa
            </option>
            <option value="DAILY">Giornaliera</option>
            <option value="PERCENT">Percentuale</option>
          </select>
          <input
            className={styles.input}
            type="number"
            step="0.01"
            min="0"
            inputMode="decimal"
            placeholder={type === 'PERCENT' ? 'Percentuale (%)' : 'Tariffa giornaliera (€)'}
            value={fee}
            onChange={(event) => setFee(event.target.value)}
            disabled={isSubmitting}
          />
        </div>
        <button className={styles.button} type="submit" disabled={isSubmitting || !type || !fee}>
          Salva tariffa
        </button>
      </form>
      {error && <p className={styles.error}>{error}</p>}
    </div>
  );
}
