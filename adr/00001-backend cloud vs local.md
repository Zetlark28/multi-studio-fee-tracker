# ADR 00001: Backend cloud vs local

## Stato
 Accettata

## Data
03/09/2026

## Contesto
L'app è una demo e verrà utilizzata principalmente da un solo utente, utilizzando principalmente il cellulare. Bisogna minimizzare/azzerare i costi dell'applicativo in quanto è una demo per il portfolio su github.

## Decisione
Si è optato per deploy su cloud con java + postgresql tutto con docker.

## Alternative considerate
- **Tutto in locale su telefono **  — pro: zero costi cloud / contro: non resiliente

## Conseguenze
Lo sviluppo e gestione risulta semplice, bisogna però identificare il cloud provider meno dispendioso.