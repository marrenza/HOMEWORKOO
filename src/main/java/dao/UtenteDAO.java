package dao;

import model.Utente;
import java.util.List;

/**
 * Interfaccia che definisce le operazioni di accesso ai dati (CRUD) per l'entità {@link Utente}.
 * <p>
 * Questa interfaccia gestisce la persistenza degli account utente, supportando
 * funzionalità critiche come la registrazione, il login (recupero per username)
 * e la gestione del profilo.
 * </p>
 *
 * @author marrenza
 * @version 1.0
 */
public interface UtenteDAO {
    /**
     * Inserisce un nuovo utente nel database.
     * Utilizzato principalmente durante la fase di registrazione.
     *
     * @param utente L'oggetto Utente da salvare (con login e password).
     */
    void addUtente(Utente utente);

    /**
     * Recupera un utente tramite il suo identificativo numerico univoco.
     *
     * @param id L'ID dell'utente.
     * @return L'oggetto {@link Utente} trovato, oppure {@code null} se non esiste.
     */
    Utente getUtenteById(int id);

    /**
     * Recupera un utente tramite il suo login (username).
     * Questo metodo è fondamentale per la procedura di autenticazione (login)
     * e per verificare l'univocità del nome utente in fase di registrazione.
     *
     * @param login Il nome utente (username) da cercare.
     * @return L'oggetto {@link Utente} trovato, oppure {@code null} se non esiste.
     */
    Utente getUtenteByLogin(String login);

    /**
     * Recupera la lista completa di tutti gli utenti registrati nel sistema.
     * Utilizzato per popolare le liste di selezione nella funzionalità di
     * condivisione dei ToDo (per scegliere con chi condividere un'attività).
     *
     * @return Una lista di tutti gli oggetti {@link Utente}.
     */
    List<Utente> getAllUtenti();

    /**
     * Aggiorna le informazioni di un utente esistente.
     *
     * @param utente L'oggetto Utente con i dati aggiornati.
     */
    void updateUtente(Utente utente);

    /**
     * Elimina un utente dal database in base al suo ID.
     *
     * @param id L'identificativo dell'utente da eliminare.
     */
    void deleteUtenteById(int id);
}
