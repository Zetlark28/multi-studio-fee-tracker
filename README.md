# Multi Studio Fee Tracker

Applicazione demo per la gestione delle competenze (fee) tra un professionista che collabora con più studi (clienti) e i servizi fatturabili a ciascuno di essi.

## Struttura del repository

| Cartella                                                     | Descrizione                                              |
| -------------------------------------------------------------- | ----------------------------------------------------------- |
| [`multi-studio-fee-tracker-be`](multi-studio-fee-tracker-be)   | Backend — API REST in Spring Boot + JPA (H2 in sviluppo)   |
| [`multi-studio-fee-tracker-fe`](multi-studio-fee-tracker-fe)   | Frontend — React + TypeScript + Vite                       |
| [`adr`](adr)                                                    | Architecture Decision Records                              |

## Avvio rapido

1. Avvia il backend (dettagli in [`multi-studio-fee-tracker-be/README.md`](multi-studio-fee-tracker-be/README.md)):

   ```bash
   cd multi-studio-fee-tracker-be
   ./mvnw spring-boot:run
   ```

   API disponibili su `http://localhost:8080/api`.

2. Avvia il frontend (dettagli in [`multi-studio-fee-tracker-fe/README.md`](multi-studio-fee-tracker-fe/README.md)):

   ```bash
   cd multi-studio-fee-tracker-fe
   npm install
   npm run dev
   ```

   App disponibile su `http://localhost:5173`.

## Decisioni architetturali

Le scelte principali (stack, modello del dominio) sono documentate come ADR nella cartella [`adr`](adr):

- [00001 — Backend cloud vs local](adr/00001-backend%20cloud%20vs%20local.md)
- [00002 — Domain entities](adr/00002-domain%20entities.md)
