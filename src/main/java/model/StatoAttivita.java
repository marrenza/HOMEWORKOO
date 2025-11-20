package model;

/**
 * Definisce gli stati possibili per una singola {@link Attivita} all'interno di una Checklist.
 *
 * @author marrenza
 * @version 1.0
 */
public enum StatoAttivita {
    /**
     * L'attività della checklist è stata spuntata/completata.
     */
    COMPLETATO,

    /**
     * L'attività della checklist deve ancora essere eseguita.
     */
    NON_COMPLETATO
}
