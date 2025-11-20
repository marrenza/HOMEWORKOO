package dao;

import model.ToDo;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaccia che definisce le operazioni di accesso ai dati (CRUD) per l'entità {@link ToDo}.
 * Questa interfaccia è il cuore della persistenza delle attività. Oltre alle operazioni standard
 * (Create, Read, Update, Delete), definisce metodi complessi per la ricerca (per termine o scadenza)
 * e per il recupero dei ToDo visibili a un utente, inclusi quelli condivisi.
 *
 * @author marrenza
 * @version 1.0
 */
public interface ToDoDAO {
    /**
     * Salva un nuovo ToDo nel database.
     *
     * @param todo L'oggetto ToDo da persistere.
     */
    void addToDo(ToDo todo);

    /**
     * Recupera un ToDo specifico tramite il suo ID univoco.
     *
     * @param id L'identificativo del ToDo.
     * @return L'oggetto {@link ToDo} trovato, oppure {@code null} se non esiste.
     */
    ToDo getToDoById(int id);

    /**
     * Recupera la lista di tutti i ToDo presenti nel database.
     * <p>
     * Nota: Questo metodo restituisce tutti i ToDo senza filtri per utente.
     * </p>
     *
     * @return Una lista completa di oggetti {@link ToDo}.
     */
    List<ToDo> getAllToDo();

    /**
     * Aggiorna le informazioni di un ToDo esistente.
     * Utilizzato per modificare titolo, descrizione, stato, posizione, ecc.
     *
     * @param todo L'oggetto ToDo con i dati aggiornati.
     */
    void updateToDo(ToDo todo);

    /**
     * Elimina un ToDo dal database.
     *
     * @param id L'identificativo del ToDo da eliminare.
     */
    void deleteToDo(int id);

    /**
     * Cerca i ToDo che contengono un determinato termine nel titolo o nella descrizione.
     * La ricerca è limitata ai ToDo visibili dall'utente specificato (sia quelli creati da lui,
     * sia quelli condivisi con lui).
     *
     * @param searchTerm La stringa da cercare.
     * @param userId     L'ID dell'utente che effettua la ricerca.
     * @return Una lista di {@link ToDo} corrispondenti ai criteri.
     */
    List<ToDo> findToDosByTerm(String searchTerm, int userId);

    /**
     * Cerca i ToDo che scadono entro una certa data.
ì     * La ricerca è limitata ai ToDo visibili dall'utente specificato (autore o condivisi).
     *
     * @param date   La data limite per la scadenza.
     * @param userId L'ID dell'utente che effettua la ricerca.
     * @return Una lista di {@link ToDo} in scadenza.
     */
    List<ToDo> findToDosByScadenza(LocalDate date, int userId);

    /**
     * Cerca i ToDo in scadenza nella giornata odierna.
     * Metodo di convenienza che richiama {@link #findToDosByScadenza} con la data corrente.
     *
     * @param userId L'ID dell'utente che effettua la ricerca.
     * @return Una lista di {@link ToDo} che scadono oggi.
     */
    List<ToDo> findToDosScadenzaOggi(int userId);

    /**
     * Recupera i ToDo da visualizzare in una specifica bacheca per un determinato utente.
     * <p>
     * Questo metodo implementa la logica di visibilità condivisa:
     * restituisce i ToDo che appartengono alla bacheca specificata E che sono:
     * <ul>
     * <li>Creati dall'utente (autore), OPPURE</li>
     * <li>Condivisi con l'utente (tramite la tabella condivisione).</li>
     * </ul>
     * </p>
     *
     * @param bachecaId L'ID della bacheca da visualizzare.
     * @param utenteId  L'ID dell'utente corrente.
     * @return La lista dei ToDo visibili in quella bacheca per quell'utente.
     */
    List<ToDo> getToDosForBachecaAndUtente(int bachecaId,  int utenteId);

    /**
     * Imposta come "COMPLETATO" tutti i ToDo appartenenti a una specifica bacheca.
     * Operazione batch utile per chiudere rapidamente una lista di attività.
     *
     * @param bachecaId L'ID della bacheca target.
     */
    void markAllToDoAsCompletedByBachecaId(int bachecaId);
}
