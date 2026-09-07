import { useEffect, useMemo, useState } from 'react';
import { clientBillableServicesApi } from '../../api/clientBillableServices';
import { clientsApi } from '../../api/clients';
import { toErrorMessage } from '../../api/http';
import { userClientActivitiesApi } from '../../api/userClientActivities';
import type { Client, ClientBillableService, UserClientActivity } from '../../types';
import {
  buildDateCode,
  currentMonth,
  daysInMonth,
  decodeDate,
  encodeMonth,
  firstWeekdayOfMonth,
} from '../../utils/date';
import styles from './history.module.css';

const currencyFormatter = new Intl.NumberFormat('it-IT', { style: 'currency', currency: 'EUR' });

const WEEKDAY_LABELS = ['Lun', 'Mar', 'Mer', 'Gio', 'Ven', 'Sab', 'Dom'];

const MONTH_LABELS = [
  'Gennaio',
  'Febbraio',
  'Marzo',
  'Aprile',
  'Maggio',
  'Giugno',
  'Luglio',
  'Agosto',
  'Settembre',
  'Ottobre',
  'Novembre',
  'Dicembre',
];

type View =
  | { level: 'calendar' }
  | { level: 'day'; dateCode: number }
  | { level: 'client'; dateCode: number; clientId: number };

function revenueOf(activities: UserClientActivity[]): number {
  return activities.reduce((sum, activity) => sum + activity.fee, 0);
}

function groupBy<T>(items: T[], keyOf: (item: T) => number): Map<number, T[]> {
  const map = new Map<number, T[]>();
  for (const item of items) {
    const key = keyOf(item);
    const list = map.get(key);
    if (list) {
      list.push(item);
    } else {
      map.set(key, [item]);
    }
  }
  return map;
}

export function HistoryPage() {
  const [monthValue, setMonthValue] = useState(currentMonth());
  const [activities, setActivities] = useState<UserClientActivity[]>([]);
  const [clients, setClients] = useState<Client[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [view, setView] = useState<View>({ level: 'calendar' });

  const [year, month] = monthValue.split('-').map(Number);

  useEffect(() => {
    let isCancelled = false;
    setIsLoading(true);
    setError(null);
    setView({ level: 'calendar' });

    Promise.all([userClientActivitiesApi.listForMonth(encodeMonth(monthValue)), clientsApi.list()])
      .then(([activityList, clientResponse]) => {
        if (!isCancelled) {
          setActivities(activityList);
          setClients(clientResponse.data);
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
  }, [monthValue]);

  const clientNameById = useMemo(() => new Map(clients.map((client) => [client.id, client.name])), [clients]);
  const activitiesByDate = useMemo(() => groupBy(activities, (activity) => activity.date), [activities]);

  function changeMonth(delta: number) {
    const next = new Date(year, month - 1 + delta, 1);
    setMonthValue(`${next.getFullYear()}-${String(next.getMonth() + 1).padStart(2, '0')}`);
  }

  const monthHeader = (
    <div className={styles.monthHeader}>
      <button className={styles.navButton} onClick={() => changeMonth(-1)} aria-label="Mese precedente">
        ‹
      </button>
      <span className={styles.monthLabel}>
        {MONTH_LABELS[month - 1]} {year}
      </span>
      <button className={styles.navButton} onClick={() => changeMonth(1)} aria-label="Mese successivo">
        ›
      </button>
    </div>
  );

  return (
    <section>
      <h1 className={styles.title}>Storico</h1>

      {monthHeader}

      {isLoading ? (
        <p className={styles.hint}>Caricamento...</p>
      ) : error ? (
        <p className={styles.error}>{error}</p>
      ) : view.level === 'calendar' ? (
        <CalendarView
          year={year}
          month={month}
          activitiesByDate={activitiesByDate}
          onSelectDay={(dateCode) => setView({ level: 'day', dateCode })}
        />
      ) : view.level === 'day' ? (
        <DayView
          dateCode={view.dateCode}
          activities={activitiesByDate.get(view.dateCode) ?? []}
          clientNameById={clientNameById}
          onSelectClient={(clientId) => setView({ level: 'client', dateCode: view.dateCode, clientId })}
          onBack={() => setView({ level: 'calendar' })}
        />
      ) : (
        <ClientDayView
          dateCode={view.dateCode}
          clientId={view.clientId}
          clientName={clientNameById.get(view.clientId) ?? '?'}
          activities={(activitiesByDate.get(view.dateCode) ?? []).filter(
            (activity) => activity.clientId === view.clientId,
          )}
          onBack={() => setView({ level: 'day', dateCode: view.dateCode })}
        />
      )}
    </section>
  );
}

interface CalendarViewProps {
  year: number;
  month: number;
  activitiesByDate: Map<number, UserClientActivity[]>;
  onSelectDay: (dateCode: number) => void;
}

function CalendarView({ year, month, activitiesByDate, onSelectDay }: CalendarViewProps) {
  const totalDays = daysInMonth(month, year);
  const leadingBlanks = firstWeekdayOfMonth(month, year);

  const cells: Array<{ day: number; dateCode: number; revenue: number } | null> = [];
  for (let i = 0; i < leadingBlanks; i += 1) {
    cells.push(null);
  }
  for (let day = 1; day <= totalDays; day += 1) {
    const dateCode = buildDateCode(day, month, year);
    const dayActivities = activitiesByDate.get(dateCode) ?? [];
    cells.push({ day, dateCode, revenue: revenueOf(dayActivities) });
  }

  return (
    <div className={styles.calendar}>
      {WEEKDAY_LABELS.map((label) => (
        <div key={label} className={styles.weekdayLabel}>
          {label}
        </div>
      ))}
      {cells.map((cell, index) =>
        cell === null ? (
          <div key={`blank-${index}`} className={styles.dayCellEmpty} />
        ) : (
          <button
            key={cell.dateCode}
            className={cell.revenue > 0 ? styles.dayCellActive : styles.dayCell}
            onClick={() => cell.revenue > 0 && onSelectDay(cell.dateCode)}
            disabled={cell.revenue === 0}
          >
            <span className={styles.dayNumber}>{cell.day}</span>
            {cell.revenue > 0 && <span className={styles.dayRevenue}>{currencyFormatter.format(cell.revenue)}</span>}
          </button>
        ),
      )}
    </div>
  );
}

interface DayViewProps {
  dateCode: number;
  activities: UserClientActivity[];
  clientNameById: Map<number, string>;
  onSelectClient: (clientId: number) => void;
  onBack: () => void;
}

function DayView({ dateCode, activities, clientNameById, onSelectClient, onBack }: DayViewProps) {
  const byClient = useMemo(() => groupBy(activities, (activity) => activity.clientId), [activities]);
  const totalRevenue = revenueOf(activities);

  return (
    <div>
      <button className={styles.backButton} onClick={onBack}>
        ← Calendario
      </button>

      <h2 className={styles.subtitle}>
        {decodeDate(dateCode)} — {currencyFormatter.format(totalRevenue)}
      </h2>

      <ul className={styles.list}>
        {Array.from(byClient.entries()).map(([clientId, clientActivities]) => (
          <li key={clientId} className={styles.listItem}>
            <button className={styles.listItemButton} onClick={() => onSelectClient(clientId)}>
              <span>{clientNameById.get(clientId) ?? '?'}</span>
              <span className={styles.listItemRevenue}>{currencyFormatter.format(revenueOf(clientActivities))}</span>
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

interface ClientDayViewProps {
  dateCode: number;
  clientId: number;
  clientName: string;
  activities: UserClientActivity[];
  onBack: () => void;
}

function ClientDayView({ dateCode, clientId, clientName, activities, onBack }: ClientDayViewProps) {
  const [services, setServices] = useState<ClientBillableService[]>([]);

  useEffect(() => {
    let isCancelled = false;
    clientBillableServicesApi
      .listByClient(clientId)
      .then((response) => {
        if (!isCancelled) {
          setServices(response.data);
        }
      })
      .catch(() => {
        // best-effort: fall back to showing service ids if names can't be loaded
      });
    return () => {
      isCancelled = true;
    };
  }, [clientId]);

  function describeServices(selections: UserClientActivity['services']): string {
    return selections
      .map((selection) => {
        const service = services.find((item) => item.id === selection.serviceId);
        const name = service?.name ?? '?';
        return selection.quantity !== 1 ? `${name} x${selection.quantity}` : name;
      })
      .join(', ');
  }

  return (
    <div>
      <button className={styles.backButton} onClick={onBack}>
        ← {decodeDate(dateCode)}
      </button>

      <h2 className={styles.subtitle}>{clientName}</h2>

      <ul className={styles.list}>
        {activities.map((activity) => (
          <li key={activity.id} className={styles.listItem}>
            <span className={styles.listItemText}>
              {currencyFormatter.format(activity.fee)}
              {activity.quantity != null && activity.quantity !== 1 && ` (x${activity.quantity})`}
              {activity.price != null && ` (su ${currencyFormatter.format(activity.price)})`}
              {activity.services.length > 0 && ` — ${describeServices(activity.services)}`}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
