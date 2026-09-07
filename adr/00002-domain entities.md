# ADR 00001: Backend cloud vs local

## Stato
 Accettata

## Data
03/09/2026

## Contesto
Bisogna identificare le entity dell'applicazione. Il DB sarà un postgresql, quindi relazionale. Ci saranno sicuramente le seguenti entity: user, studio, userStudioFee, studioActivityRate, userActivity

## Decisione
Di seguito le entity definite:
- AppUser: String username (PK), String password (encrypted rsa).
- Client (studio dentistico): Long Id (PK), String Name.
- UserClientFee: AppUser.username (FK), Client.id (FK), BigDecimal fee, Enum type (DAILY OR PERCENT)
- ClientService: Long Id (PK), Client.id (FK), BigDecimal price.
- UserClientActivity: Long Id (PK), AppUser.username (FK), Client.id(FK), ClientService.id(FK - nullable if type of fee is DAILY), Long date, BigDecimal price (nullable if type of fee is DAILY), BigDecimal fee.


## Conseguenze
Utilizzo di database relazionali --> PostgreSQL

## Aggiornamento 03/09/2026
L'entity `ClientService` è stata rinominata in `ClientBillableService` per evitare la collisione di naming con il service layer applicativo (convention `EntityName` + `Service` per le classi di business logic, es. `ClientService` per il CRUD di `Client`).

## Aggiornamento 03/09/2026 (2)
Introdotta autenticazione: `AppUser` è l'unico soggetto che accede all'app, ed è il professionista che crea per sé i clienti. `Client` acquisisce un riferimento diretto `owner` (AppUser.username, FK, obbligatorio) impostato dal server in base all'utente autenticato — ogni cliente appartiene a un solo professionista.

Di conseguenza `UserClientFee` (che modellava una relazione N:M tra AppUser e Client con `fee`+`type`) è ridondante e viene **eliminata**: con la relazione owner 1:N diretta, `fee` e `type` (enum `FeeType`: DAILY/PERCENT) sono stati spostati direttamente su `Client`.

## Aggiornamento 03/09/2026 (3)
Implementata la CRUD di `UserClientActivity` ("registrazione attività"). Per lo stesso motivo dell'aggiornamento precedente, il campo `appUser` viene **eliminato** da `UserClientActivity`: l'appartenenza si deriva da `client.owner`, dato che ogni attività è sempre legata a un cliente.

Il campo singolo `ClientService.id` (FK nullable) previsto dalla decisione originale viene sostituito da una relazione `@ManyToMany` con `ClientBillableService` (tabella di join `user_client_activity_service`), perché per i clienti a tariffa PERCENT l'utente può selezionare più servizi svolti nella stessa attività.

Regole applicate in fase di creazione/modifica di un'attività, in base al `FeeType` del cliente:
- **DAILY**: l'utente inserisce solo `fee` (importo); `services` e `price` non sono ammessi e vengono azzerati dal server.
- **PERCENT**: l'utente seleziona uno o più `services`; il server calcola `price` come somma dei prezzi dei servizi selezionati, e `fee` come `price * client.fee / 100` (arrotondato a 2 decimali) — qualunque `fee` inviata dal client viene ignorata e sovrascritta.
- Se il cliente non ha un `type` configurato (o, in caso PERCENT, non ha una `fee` percentuale impostata), la richiesta viene rifiutata con `400`.

## Aggiornamento 03/09/2026 (4)
Corretto un bug: per i clienti DAILY, la `fee` dell'attività non era un valore libero inseribile dall'utente, ma deve sempre coincidere con la tariffa giornaliera del cliente (`client.fee`). Aggiunto il campo `quantity` (Integer, ≥ 1) su `UserClientActivity`, che rappresenta il numero di unità dell'attività (es. giorni per DAILY, numero di volte per i servizi selezionati in PERCENT). `fee` è ora sempre calcolata dal server, mai accettata in input:
- **DAILY**: `fee = client.fee * quantity`.
- **PERCENT**: `price = (somma prezzi servizi selezionati) * quantity`; `fee = price * client.fee / 100`.

## Aggiornamento 03/09/2026 (5)
Corretta la modellazione PERCENT: la quantità non è unica per l'intera attività, ma va indicata per ciascun servizio selezionato (es. "Pulizia x2, Otturazione x1"). La relazione `@ManyToMany` verso `ClientBillableService` viene quindi sostituita da una entity di join con attributo proprio, `UserClientActivityServiceEntry` (`activity_id`, `service_id`, `quantity`) — nome scelto (invece di `UserClientActivityService`) per evitare la stessa collisione di naming con il service layer già risolta per `ClientService`/`ClientBillableService`.

Il campo `quantity` a livello di `UserClientActivity` resta, ma è **esclusivo per DAILY** (numero di giorni); per PERCENT è `null` e non viene richiesto, dato che la quantità è ora specificata per singolo servizio. `price` per PERCENT diventa la somma di `prezzo servizio * quantità` per ogni servizio selezionato.