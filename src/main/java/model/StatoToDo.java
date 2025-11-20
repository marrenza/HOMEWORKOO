package model;

/**
 * Definisce i possibili stati di avanzamento di un {@link ToDo}.
 *
 * @author marrenza
 * @version 1.0
 */
public enum StatoToDo {
    /**
     * Indica che l'attività è stata portata a termine con successo.
     */
    COMPLETATO,

    /**
     * Indica che l'attività è ancora da svolgere o è in corso.
     * Questo è lo stato di default alla creazione.
     */
    NON_COMPLETATO
}
