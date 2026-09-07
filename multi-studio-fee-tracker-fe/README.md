# Multi Studio Fee Tracker — Frontend

Interfaccia React (Vite + TypeScript) per la gestione di clienti (studi) e dei loro servizi fatturabili.

## Prerequisiti

- Node.js 20+ e npm
- Il backend [`multi-studio-fee-tracker-be`](../multi-studio-fee-tracker-be) avviato (di default su `http://localhost:8080`)

## Setup

```bash
npm install
```

## Variabili di ambiente

Copia il file di esempio e personalizzalo se necessario:

```bash
cp .env.example .env
```

| Variabile             | Obbligatoria | Default (se assente) | Descrizione                                                                                      |
| ---------------------- | ------------ | --------------------- | ------------------------------------------------------------------------------------------------- |
| `VITE_API_BASE_URL`    | No           | `/api`                 | URL base delle API backend. In sviluppo puoi ometterla: il dev server proxa `/api` verso `http://localhost:8080` (vedi `vite.config.ts`). Impostala se il backend gira su un host/porta diversi o in build di produzione, dove il proxy di sviluppo non è disponibile. |

## Avvio in sviluppo

1. Avvia il backend (dalla cartella `multi-studio-fee-tracker-be`):

   ```bash
   ./mvnw spring-boot:run
   ```

2. Avvia il frontend:

   ```bash
   npm run dev
   ```

3. Apri `http://localhost:5173`. Non esiste auto-registrazione dalla UI: gli account si creano dalla sezione "Gestione utenti", visibile solo a chi accede con un utente admin (vedi il README del backend per come configurarne uno). Un utente normale vede solo i clienti che ha creato lui stesso; un admin vede solo la gestione utenti, non le sezioni Dashboard/Clienti/Attività. Il token di sessione è salvato in `localStorage`.

## Build di produzione

```bash
npm run build
```

I file generati vengono scritti in `dist/`. Se il backend non è raggiungibile su `/api` dello stesso host, imposta `VITE_API_BASE_URL` prima della build (o al momento del deploy) con l'URL completo del backend.

Per verificare la build in locale:

```bash
npm run preview
```

## Struttura del progetto

```
src/
  api/                        client HTTP verso le API backend (clients, client-billable-services, auth, admin), sessione (token + ruolo in localStorage)
  features/auth/               login
  features/admin/              "Gestione utenti" (solo per l'admin): elenco, creazione, cambio password
  features/clients/           feature "Clienti": lista, dettaglio, servizi
  types.ts                    tipi condivisi allineati ai DTO del backend
  App.tsx                     gate di autenticazione + layout applicativo (sidebar diversa per admin/utente normale)
```
