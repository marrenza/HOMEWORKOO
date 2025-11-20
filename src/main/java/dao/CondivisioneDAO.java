package dao;
import model.Condivisione;
import model.ToDo;
import model.Utente;
import java.util.List;

/**
 * Interfaccia che definisce le operazioni di accesso ai dati (CRUD) per l'entità {@link Condivisione}.
 * Questa interfaccia gestisce la tabella associativa che collega gli Utenti ai ToDo.
 * Permette di aggiungere nuove condivisioni, rimuoverle e recuperare le liste di
 * condivisione filtrando per ToDo o per Utente.
 *
 * @author marrenza
 * @version 1.0
 */
public interface CondivisioneDAO {
    /**
     * Aggiunge una nuova condivisione nel database.
     * Crea un record che collega un utente specifico a un ToDo specifico.
     *
     * @param condivisione L'oggetto Condivisione da salvare.
     */
    void addCondivisione(Condivisione condivisione);

    /**
     * Rimuove una condivisione esistente dal database.
     * Revoca l'accesso di un utente a un determinato ToDo.
     *
     * @param condivisione L'oggetto Condivisione da eliminare (contenente gli ID di utente e ToDo).
     */
    void deleteCondivisione(Condivisione condivisione);

    /**
     * Recupera tutte le condivisioni attive per uno specifico ToDo.
     * Utile per visualizzare l'elenco degli utenti con cui un'attività è stata condivisa.
     *
     * @param todoId L'identificativo del ToDo.
     * @return Una lista di oggetti {@link Condivisione}.
     */
    List<Condivisione> getCondivisioniByToDoId(int todoId);

    /**
     * Recupera tutte le condivisioni associate a uno specifico utente.
     * Restituisce, in sostanza, i link ai ToDo che altri hanno condiviso con questo utente.
     *
     * @param utenteId L'identificativo dell'utente.
     * @return Una lista di oggetti {@link Condivisione}.
     */
    List<Condivisione> getCondivisioniByUtenteId(int utenteId);
}