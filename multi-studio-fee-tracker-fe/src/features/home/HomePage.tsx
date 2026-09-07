import { useEffect, useState } from 'react';
import { clientBillableServicesApi } from '../../api/clientBillableServices';
import { clientsApi } from '../../api/clients';
import { toErrorMessage } from '../../api/http';
import { ActivityList } from '../clients/ActivityList';
import type { Client, ClientBillableService } from '../../types';
import styles from './home.module.css';

export function HomePage() {
  const [clients, setClients] = useState<Client[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [loadError, setLoadError] = useState<string | null>(null);

  const [clientId, setClientId] = useState('');
  const [services, setServices] = useState<ClientBillableService[]>([]);
  const [isLoadingServices, setIsLoadingServices] = useState(false);
  const [servicesError, setServicesError] = useState<string | null>(null);

  useEffect(() => {
    let isCancelled = false;
    setIsLoading(true);
    setLoadError(null);
    clientsApi
      .list()
      .then((response) => {
        if (!isCancelled) {
          setClients(response.data);
        }
      })
      .catch((err) => {
        if (!isCancelled) {
          setLoadError(toErrorMessage(err));
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
  }, []);

  useEffect(() => {
    if (!clientId) {
      setServices([]);
      return;
    }
    let isCancelled = false;
    setIsLoadingServices(true);
    setServicesError(null);
    clientBillableServicesApi
      .listByClient(Number(clientId))
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
  }, [clientId]);

  const selectedClient = clients.find((client) => client.id === Number(clientId)) ?? null;

  return (
    <section>
      <h1 className={styles.title}>Registra attività</h1>

      <div className={styles.card}>
        {isLoading ? (
          <p className={styles.hint}>Caricamento...</p>
        ) : loadError ? (
          <p className={styles.error}>{loadError}</p>
        ) : clients.length === 0 ? (
          <p className={styles.hint}>Nessun cliente presente. Vai su "Clienti" per crearne uno.</p>
        ) : (
          <select
            className={styles.selectFull}
            value={clientId}
            onChange={(event) => setClientId(event.target.value)}
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
            <ActivityList client={selectedClient} services={services} />
          ))}
      </div>
    </section>
  );
}
