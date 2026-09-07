import { useCallback, useEffect, useState } from 'react';
import { clientBillableServicesApi } from '../../api/clientBillableServices';
import { clientsApi } from '../../api/clients';
import { dashboardApi } from '../../api/dashboard';
import { toErrorMessage } from '../../api/http';
import { ActivityList } from '../clients/ActivityList';
import type { Client, ClientBillableService, DashboardData } from '../../types';
import { currentMonth, encodeMonth } from '../../utils/date';
import styles from './dashboard.module.css';

const currencyFormatter = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' });

export function DashboardPage() {
  const [month, setMonth] = useState(currentMonth());
  const [data, setData] = useState<DashboardData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [clients, setClients] = useState<Client[]>([]);
  const [isLoadingClients, setIsLoadingClients] = useState(true);
  const [clientsError, setClientsError] = useState<string | null>(null);

  const [selectedClientId, setSelectedClientId] = useState('');
  const [services, setServices] = useState<ClientBillableService[]>([]);
  const [isLoadingServices, setIsLoadingServices] = useState(false);
  const [servicesError, setServicesError] = useState<string | null>(null);

  const loadDashboard = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const result = await dashboardApi.getData(encodeMonth(month));
      setData(result);
    } catch (err) {
      setError(toErrorMessage(err));
    } finally {
      setIsLoading(false);
    }
  }, [month]);

  useEffect(() => {
    loadDashboard();
  }, [loadDashboard]);

  useEffect(() => {
    let isCancelled = false;
    setIsLoadingClients(true);
    setClientsError(null);
    clientsApi
      .list()
      .then((response) => {
        if (!isCancelled) {
          setClients(response.data);
        }
      })
      .catch((err) => {
        if (!isCancelled) {
          setClientsError(toErrorMessage(err));
        }
      })
      .finally(() => {
        if (!isCancelled) {
          setIsLoadingClients(false);
        }
      });
    return () => {
      isCancelled = true;
    };
  }, []);

  useEffect(() => {
    if (!selectedClientId) {
      setServices([]);
      return;
    }
    let isCancelled = false;
    setIsLoadingServices(true);
    setServicesError(null);
    clientBillableServicesApi
      .listByClient(Number(selectedClientId))
      .then((response) => {
        if (!isCancelled) {
          setServices(response.data);
        }
      })
      .catch((err) => {
        if (!isCancelled) {
          setServicesError(toErrorMessage(err));
        }
      })
      .finally(() => {
        if (!isCancelled) {
          setIsLoadingServices(false);
        }
      });
    return () => {
      isCancelled = true;
    };
  }, [selectedClientId]);

  const selectedClient = clients.find((client) => client.id === Number(selectedClientId)) ?? null;

  return (
    <section>
      <h1 className={styles.title}>Dashboard</h1>

      <div className={styles.card}>
        <div className={styles.monthRow}>
          <label className={styles.monthLabel} htmlFor="dashboard-month">
            Mese
          </label>
          <input
            id="dashboard-month"
            className={styles.monthInput}
            type="month"
            value={month}
            onChange={(event) => setMonth(event.target.value)}
          />
        </div>

        {isLoading ? (
          <p className={styles.hint}>Caricamento...</p>
        ) : error ? (
          <p className={styles.error}>{error}</p>
        ) : (
          <div className={styles.statGrid}>
            <div className={styles.stat}>
              <span className={styles.statValue}>{currencyFormatter.format(data?.totalRevenue ?? 0)}</span>
              <span className={styles.statLabel}>Fatturato</span>
            </div>
            <div className={styles.stat}>
              <span className={styles.statValue}>{data?.workDays ?? 0}</span>
              <span className={styles.statLabel}>Giorni lavorati</span>
            </div>
            <div className={styles.stat}>
              <span className={styles.statValue}>{data?.totalClients ?? 0}</span>
              <span className={styles.statLabel}>Clienti attivi</span>
            </div>
          </div>
        )}
      </div>

      <div className={styles.card}>
        <h2 className={styles.subtitle}>Nuova attività</h2>

        {isLoadingClients ? (
          <p className={styles.hint}>Caricamento clienti...</p>
        ) : clientsError ? (
          <p className={styles.error}>{clientsError}</p>
        ) : clients.length === 0 ? (
          <p className={styles.hint}>Nessun cliente presente. Crea prima un cliente nella sezione "Clienti".</p>
        ) : (
          <select
            className={styles.select}
            value={selectedClientId}
            onChange={(event) => setSelectedClientId(event.target.value)}
          >
            <option value="">Seleziona cliente</option>
            {clients.map((client) => (
              <option key={client.id} value={client.id}>
                {client.name}
              </option>
            ))}
          </select>
        )}

        {selectedClient &&
          (isLoadingServices ? (
            <p className={styles.hint}>Caricamento servizi...</p>
          ) : servicesError ? (
            <p className={styles.error}>{servicesError}</p>
          ) : (
            <ActivityList client={selectedClient} services={services} onActivitiesChange={loadDashboard} />
          ))}
      </div>
    </section>
  );
}
