import { useEffect, useState, type FormEvent } from 'react';
import { toErrorMessage } from '../../api/http';
import { userClientActivitiesApi } from '../../api/userClientActivities';
import type { Client, ClientBillableService, UserClientActivity } from '../../types';
import { decodeDate, encodeDate, today } from '../../utils/date';
import styles from './clients.module.css';

interface ActivityListProps {
  client: Client;
  services: ClientBillableService[];
  onActivitiesChange?: () => void;
}

const currencyFormatter = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' });

export function ActivityList({ client, services, onActivitiesChange }: ActivityListProps) {
  const [activities, setActivities] = useState<UserClientActivity[]>([]);
  const [date, setDate] = useState(today());
  const [dailyQuantity, setDailyQuantity] = useState('1');
  const [serviceQuantities, setServiceQuantities] = useState<Record<number, string>>({});
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadActivities();
  }, [client.id]);

  async function loadActivities() {
    setIsLoading(true);
    setError(null);
    try {
      const response = await userClientActivitiesApi.listByClient(client.id);
      setActivities(response.data);
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }

  function toggleService(serviceId: number) {
    setServiceQuantities((current) => {
      const next = { ...current };
      if (serviceId in next) {
        delete next[serviceId];
      } else {
        next[serviceId] = '1';
      }
      return next;
    });
  }

  function setServiceQuantity(serviceId: number, value: string) {
    setServiceQuantities((current) => ({ ...current, [serviceId]: value }));
  }

  function describeServices(selections: UserClientActivity['services']): string {
    return selections
      .map((selection) => {
        const service = services.find((item) => item.id === selection.serviceId);
        const name = service?.name ?? '?';
        return selection.quantity !== 1 ? `${name} x${selection.quantity}` : name;
      })
      .join(', ');
  }

  async function handleCreate(event: FormEvent) {
    event.preventDefault();
    if (!date) {
      return;
    }
    const dateCode = encodeDate(date);

    setIsSubmitting(true);
    setError(null);
    try {
      let created: UserClientActivity;
      if (client.type === 'DAILY') {
        const parsedQuantity = Number(dailyQuantity);
        if (!Number.isInteger(parsedQuantity) || parsedQuantity < 1) {
          return;
        }
        created = await userClientActivitiesApi.create({ clientId: client.id, date: dateCode, quantity: parsedQuantity });
      } else {
        const selections = Object.entries(serviceQuantities).map(([serviceId, quantity]) => ({
          serviceId: Number(serviceId),
          quantity: Number(quantity),
        }));
        if (selections.length === 0 || selections.some((selection) => !Number.isInteger(selection.quantity) || selection.quantity < 1)) {
          return;
        }
        created = await userClientActivitiesApi.create({ clientId: client.id, date: dateCode, services: selections });
      }
      setActivities((current) => [...current, created]);
      setDate(today());
      setDailyQuantity('1');
      setServiceQuantities({});
      onActivitiesChange?.();
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDelete(activity: UserClientActivity) {
    if (!window.confirm('Eliminare questa attività?')) {
      return;
    }

    setError(null);
    try {
      await userClientActivitiesApi.remove(activity.id);
      setActivities((current) => current.filter((item) => item.id !== activity.id));
      onActivitiesChange?.();
    } catch (err) {
      setError(toErrorMessage(err));
    }
  }

  const selectedServiceCount = Object.keys(serviceQuantities).length;
  const canSubmit =
    Boolean(date) && (client.type === 'DAILY' ? Boolean(dailyQuantity) : selectedServiceCount > 0);

  return (
    <div>
      <h2 className={styles.subtitle}>Registrazione attività</h2>

      {!client.type ? (
        <p className={styles.hint}>Configura prima la tariffa del cliente per registrare un'attività.</p>
      ) : (
        <form className={styles.formStacked} onSubmit={handleCreate}>
          {client.type === 'DAILY' ? (
            <div className={styles.formRow}>
              <input
                className={styles.input}
                type="date"
                value={date}
                onChange={(event) => setDate(event.target.value)}
                disabled={isSubmitting}
              />
              <input
                className={styles.input}
                type="number"
                step="1"
                min="1"
                inputMode="numeric"
                placeholder="Quantità (giorni)"
                value={dailyQuantity}
                onChange={(event) => setDailyQuantity(event.target.value)}
                disabled={isSubmitting}
              />
            </div>
          ) : (
            <input
              className={styles.input}
              type="date"
              value={date}
              onChange={(event) => setDate(event.target.value)}
              disabled={isSubmitting}
            />
          )}

          {client.type === 'PERCENT' &&
            (services.length === 0 ? (
              <p className={styles.hint}>Aggiungi prima un servizio per poter registrare un'attività.</p>
            ) : (
              <div className={styles.checkboxGroup}>
                {services.map((service) => {
                  const isSelected = service.id in serviceQuantities;
                  return (
                    <div key={service.id} className={styles.checkboxRow}>
                      <label className={styles.checkboxLabel}>
                        <input
                          type="checkbox"
                          checked={isSelected}
                          onChange={() => toggleService(service.id)}
                          disabled={isSubmitting}
                        />
                        {service.name} — {currencyFormatter.format(service.price)}
                      </label>
                      {isSelected && (
                        <input
                          className={styles.quantityInput}
                          type="number"
                          step="1"
                          min="1"
                          inputMode="numeric"
                          placeholder="Quantità"
                          value={serviceQuantities[service.id]}
                          onChange={(event) => setServiceQuantity(service.id, event.target.value)}
                          disabled={isSubmitting}
                        />
                      )}
                    </div>
                  );
                })}
              </div>
            ))}

          <button className={styles.button} type="submit" disabled={isSubmitting || !canSubmit}>
            Registra attività
          </button>
        </form>
      )}

      {error && <p className={styles.error}>{error}</p>}

      {isLoading ? (
        <p className={styles.hint}>Caricamento...</p>
      ) : activities.length === 0 ? (
        <p className={styles.hint}>Nessuna attività registrata.</p>
      ) : (
        <ul className={styles.list}>
          {activities.map((activity) => (
            <li key={activity.id} className={styles.listItem}>
              <span className={styles.listItemText}>
                {decodeDate(activity.date)} — {currencyFormatter.format(activity.fee)}
                {activity.quantity != null && activity.quantity !== 1 && ` (x${activity.quantity})`}
                {activity.price != null && ` (su ${currencyFormatter.format(activity.price)})`}
                {activity.services.length > 0 && ` — ${describeServices(activity.services)}`}
              </span>
              <button
                className={styles.deleteButton}
                onClick={() => handleDelete(activity)}
                aria-label="Elimina attività"
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
