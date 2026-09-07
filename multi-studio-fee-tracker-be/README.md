# Multi Studio Fee Tracker — Backend

API REST (Spring Boot + JPA) per la gestione di clienti (studi) e dei loro servizi fatturabili.

## Prerequisiti

- Java 25
- Maven (incluso il wrapper `./mvnw`)
- Un database PostgreSQL raggiungibile (di default `localhost:5432`, vedi sotto)

## Avvio

```bash
./mvnw spring-boot:run
```

Il server si avvia su `http://localhost:8080`, con base path `/api` (configurato in `server.servlet.context-path`, vedi `application.yaml`). Il database è PostgreSQL: connessione, credenziali e nome del database si configurano con le variabili d'ambiente `DB_HOST` (default `localhost`), `DB_PORT` (default `5432`), `DB_NAME`, `DB_USER`, `DB_PASSWORD` (queste ultime tre hanno un default valido solo per sviluppo, vedi `application.yaml`). Lo schema viene creato/aggiornato automaticamente da Hibernate (`ddl-auto: update`), non essendoci ancora uno strumento di migrazione (Flyway/Liquibase).

Il secret usato per firmare i JWT ha un default valido solo per sviluppo (`application.yaml`). In produzione impostare la variabile d'ambiente `JWT_SECRET` con un valore casuale di almeno 32 byte.

## Autenticazione

L'API richiede autenticazione JWT. Ogni `AppUser` (il professionista) vede e gestisce solo i clienti che ha creato lui stesso — l'isolamento è per singolo utente ("row-level").

| Endpoint              | Descrizione                                              |
| ---------------------- | ----------------------------------------------------------- |
| `POST /api/auth/register` | Crea un nuovo utente (`username`, `password`), ritorna subito un token (login automatico) |
| `POST /api/auth/login`    | Autentica un utente esistente, ritorna un token             |

Tutte le altre richieste devono includere l'header `Authorization: Bearer <token>`. Senza token → `401`; su una risorsa di un altro utente → `404` (mai `403`, per non rivelarne l'esistenza).

### Utente admin

`POST /api/auth/register` crea sempre e solo utenti normali: non esiste un modo per auto-promuoversi ad admin. L'unico admin possibile è quello seminato al boot: se le variabili d'ambiente `ADMIN_USERNAME` e `ADMIN_PASSWORD` sono entrambe impostate all'avvio, un `AppUser` con quelle credenziali e ruolo admin viene creato automaticamente (solo se non esiste già — idempotente, non sovrascrive la password ad ogni riavvio). Senza queste variabili nessun account admin viene creato.

```bash
ADMIN_USERNAME=admin ADMIN_PASSWORD=change-me-please ./mvnw spring-boot:run
```

L'admin vede e gestisce i dati di **tutti** gli utenti (nessun filtro per proprietario) sugli endpoint esistenti (`/clients`, `/client-billable-services`, `/user-client-activities`), senza restrizioni aggiuntive a livello di rotta — la differenza è solo nei dati restituiti.

In più, solo l'admin può usare gli endpoint di gestione utenti:

| Endpoint                                | Descrizione                                              |
| ----------------------------------------- | ----------------------------------------------------------- |
| `GET /api/admin/users`                    | Elenca tutti gli `AppUser` (username + ruolo, mai la password) |
| `POST /api/admin/users`                   | Crea un nuovo utente normale (`username`, `password`)    |
| `PUT /api/admin/users/{username}/password` | Imposta una nuova password per l'utente indicato (senza richiedere quella attuale) |

Chiamati da un utente non admin, questi endpoint rispondono `403`.

## Endpoint

Tutti gli endpoint applicativi sono sotto `/api`.

| Risorsa                  | Base path                        |
| ------------------------- | ---------------------------------- |
| Clienti                   | `/api/clients`                     |
| Servizi fatturabili        | `/api/client-billable-services`    |
| Registrazione attività     | `/api/user-client-activities`      |

Ogni risorsa espone le operazioni CRUD standard: `POST` (crea), `GET /{id}` (dettaglio), `GET` (lista paginata), `PUT /{id}` (aggiorna), `DELETE /{id}` (elimina). Ogni cliente creato viene associato automaticamente all'utente autenticato che lo ha creato.

### Registrazione attività

`price` e `fee` sono sempre calcolati dal server (in sola lettura, un valore eventualmente inviato viene ignorato) in base al `type` (`FeeType`) del cliente indicato in `clientId`:

- **DAILY**: passare `quantity` (intero, ≥ 1 — numero di giorni), non passare `services`. `fee = client.fee * quantity`, `price` resta `null`.
- **PERCENT**: passare `services` (lista di `{serviceId, quantity}`, uno o più, quantity ≥ 1 per ciascuno — non passare `quantity` a livello di attività). `price = Σ (prezzo servizio * quantity)`, `fee = price * client.fee / 100`.

Se il cliente non ha un `type`/`fee` configurato, o i dati inviati non sono coerenti con il `type`, la richiesta viene rifiutata con `400`.

### Actuator

```
GET /api/actuator/health
```

Endpoint di health check (Spring Boot Actuator). È l'unico endpoint actuator esposto di default; per abilitarne altri (es. `/info`, `/metrics`) imposta `management.endpoints.web.exposure.include` in `application.yaml`.

## Build

```bash
./mvnw clean package
```
