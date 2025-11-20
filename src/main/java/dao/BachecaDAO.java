package dao;

import model.Bacheca;
import java.util.List;

/**
 * Interfaccia che definisce le operazioni di accesso ai dati (CRUD) per l'entità {@link Bacheca}.
 * <p>
 * Gestisce la persistenza delle bacheche (es. Università, Lavoro, Tempo Libero)
 * e il loro recupero dal database.
 * </p>
 *
 * @author Utente
 * @version 1.0
 */
public interface BachecaDAO {
    /**
     * Salva una nuova bacheca nel database.
     *
     * @param bacheca L'oggetto Bacheca da salvare.
     */
    void addBacheca(Bacheca bacheca);

    /**
     * Recupera una bacheca specifica tramite il suo identificativo univoco.
     *
     * @param id L'ID della bacheca da cercare.
     * @return L'oggetto {@link Bacheca} trovato, oppure {@code null} se non esiste.
     */
    Bacheca getBachecaById(int id);

    /**
     * Recupera tutte le bacheche appartenenti a uno specifico utente.
     * Utilizzato durante il login per caricare lo spazio di lavoro dell'utente.
     *
     * @param userId L'ID dell'utente proprietario.
     * @return Una lista di oggetti {@link Bacheca}.
     */
    List<Bacheca> getBachecaByUserId(int userId);

    /**
     * Aggiorna i dati di una bacheca esistente (es. modifica della descrizione).
     *
     * @param bacheca L'oggetto Bacheca con i dati aggiornati.
     */
    void updateBacheca(Bacheca bacheca);

    /**
     * Elimina una bacheca dal database tramite il suo ID.
     *
     * @param id L'ID della bacheca da eliminare.
     */
    void deleteBacheca(int id);

    /**
     * Recupera una bacheca specificando il titolo e l'ID dell'utente proprietario.
     * Utile per verificare se un utente possiede già una certa bacheca
     * (es. per impedire la creazione di due bacheche "Università").
     *
     * @param titolo   Il titolo della bacheca (come stringa).
     * @param idUtente L'ID dell'utente proprietario.
     * @return L'oggetto {@link Bacheca} trovato, oppure {@code null} se non esiste.
     */
    Bacheca getBachecaByTitoloAndUtente(String titolo, int idUtente);
}
