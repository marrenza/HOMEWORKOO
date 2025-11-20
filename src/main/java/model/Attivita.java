package model;

/**
 * Rappresenta una singola attività (o sotto-task) all'interno di una Checklist.
 * Ogni attività possiede un nome e uno stato (Completato o Non Completato)[cite: 24].
 * L'insieme delle attività costituisce la checklist necessaria per completare un ToDo.
 * @author marrenza
 * @version 1.0
 */
public class Attivita {
    /** Il nome o la descrizione testuale dell'attività. */
    private String nome;

    /** Lo stato di completamento dell'attività. */
    private StatoAttivita stato;

    /** L'identificativo univoco dell'attività nel database. */
    private int id;

    /** L'identificativo del ToDo a cui questa attività appartiene (chiave esterna). */
    private int idToDo;

    /**
     * Costruttore per creare una nuova attività (in memoria).
     * Lo stato viene inizializzato automaticamente a {@code NON_COMPLETATO} come da default[cite: 24].
     * @param nome Il nome o la descrizione dell'attività da svolgere.
     */
    public Attivita(String nome) {
        this.nome = nome;
        this.stato = StatoAttivita.NON_COMPLETATO;
    }

    /**
     * Costruttore completo per ricostruire un'attività esistente (solitamente dal database).
     *
     * @param id     L'ID univoco dell'attività.
     * @param idToDo L'ID del ToDo genitore.
     * @param nome   Il nome dell'attività.
     * @param stato  Lo stato attuale dell'attività.
     */
    public Attivita(int id, int idToDo, String nome, StatoAttivita stato) {
        this.id = id;
        this.idToDo = idToDo;
        this.nome = nome;
        this.stato = stato;
    }

    /**
     * Restituisce il nome dell'attività.
     * @return Una stringa contenente il nome.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Imposta il nome dell'attività.
     * @param nome Il nuovo nome da assegnare.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Restituisce lo stato corrente dell'attività.
     * @return {@code StatoAttivita.COMPLETATO} o {@code StatoAttivita.NON_COMPLETATO}.
     */
    public StatoAttivita getStato() {
        return stato;
    }

    /**
     * Imposta lo stato dell'attività.
     * @param stato Il nuovo stato.
     */
    public void setStato(StatoAttivita stato) {
        this.stato = stato;
    }

    /**
     * Restituisce l'ID univoco dell'attività.
     * @return L'identificativo intero.
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'ID dell'attività.
     * @param id Il nuovo identificativo.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce l'ID del ToDo a cui questa attività è associata.
     * @return L'ID del ToDo genitore.
     */
    public int getIdTodo() {
        return idToDo;
    }

    /**
     * Imposta l'ID del ToDo genitore.
     * @param idToDo Il nuovo ID del ToDo.
     */
    public void setIdTodo(int idToDo) {
        this.idToDo = idToDo;
    }
}
