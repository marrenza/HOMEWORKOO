package model;

/**
 * Rappresenta la relazione di condivisione tra un {@link Utente} e un {@link ToDo}.
 * Questa classe modella la tabella associativa nel database che collega un utente
 * a un'attività specifica. Quando esiste un oggetto Condivisione, significa che
 * quel determinato ToDo deve apparire anche nella bacheca di quel determinato Utente,
 * oltre che in quella dell'autore originale.
 *
 * @author marrenza
 * @version 1.0
 */
public class Condivisione {
    /** L'oggetto Utente con cui il ToDo è condiviso. */
    private Utente utente;

    /** L'oggetto ToDo oggetto della condivisione. */
    private ToDo toDo;

    /** L'ID dell'utente (chiave esterna verso la tabella utente). */
    private int idUtente;

    /** L'ID del ToDo (chiave esterna verso la tabella todo). */
    private int idToDo;

    /**
     * Costruttore che crea una condivisione utilizzando gli oggetti completi.
     *
     * @param utente L'utente che riceve la condivisione.
     * @param toDo   Il ToDo da condividere.
     */
    public Condivisione(Utente utente, ToDo toDo) {
        this.utente = utente;
        this.toDo = toDo;
    }

    /**
     * Costruttore che crea una condivisione utilizzando solo gli ID.
     * Utile quando si caricano i dati dalle tabelle di associazione del database
     * prima di aver "idratato" gli oggetti completi.
     *
     * @param idUtente L'ID dell'utente.
     * @param idToDo   L'ID del ToDo.
     */
    public Condivisione(int idUtente, int idToDo) {
        this.idUtente = idUtente;
        this.idToDo = idToDo;
    }

    /**
     * Restituisce l'oggetto Utente associato a questa condivisione.
     * @return L'oggetto Utente.
     */
    public Utente getUtente() {
        return utente;
    }

    /**
     * Imposta l'oggetto Utente per questa condivisione.
     * @param utente Il nuovo utente.
     */
    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    /**
     * Restituisce l'oggetto ToDo associato a questa condivisione.
     * @return L'oggetto ToDo.
     */
    public ToDo getToDo() {
        return toDo;
    }

    /**
     * Imposta l'oggetto ToDo per questa condivisione.
     * @param toDo Il nuovo ToDo.
     */
    public void setToDo(ToDo toDo) {
        this.toDo = toDo;
    }

    /**
     * Restituisce l'ID dell'utente coinvolto nella condivisione.
     * @return L'identificativo intero dell'utente.
     */
    public int getIdUtente() {
        return idUtente;
    }

    /**
     * Restituisce l'ID del ToDo condiviso.
     * @return L'identificativo intero del ToDo.
     */
    public int getIdToDo() {
        return idToDo;
    }
}
