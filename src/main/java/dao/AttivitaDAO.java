package dao;

import model.Attivita;
import java.util.List;

/**
 * Interfaccia che definisce le operazioni di accesso ai dati (CRUD) per l'entità {@link Attivita}.
 * <p>
 * Questa interfaccia astrae la logica di persistenza per le attività della Checklist.
 * Le implementazioni concrete (es. su database PostgreSQL) gestiranno le query SQL effettive.
 * </p>
 *
 * @author marrenza
 * @version 1.0
 */
public interface AttivitaDAO {
    /**
     * Salva una nuova attività nel database.
     *
     * @param attivita L'oggetto Attivita da persistere.
     */
    void addAttivita(Attivita attivita);

    /**
     * Recupera tutte le attività associate a uno specifico ToDo.
     * Utilizzato per ricostruire la Checklist quando si carica un ToDo.
     *
     * @param todoId L'identificativo del ToDo genitore.
     * @return Una lista di oggetti {@link Attivita}.
     */
    List<Attivita> getAttivitaByToDoId(int todoId);

    /**
     * Aggiorna i dati di un'attività esistente (es. cambio stato Completato/Non Completato).
     *
     * @param attivita L'oggetto Attivita con i dati aggiornati.
     */
    void updateAttivita(Attivita attivita);

    /**
     * Elimina una singola attività dal database in base al suo ID.
     *
     * @param attivitaId L'identificativo dell'attività da rimuovere.
     */
    void deleteAttivita(int attivitaId);

    /**
     * Elimina tutte le attività associate a uno specifico ToDo.
     * Solitamente chiamato quando viene eliminato l'intero ToDo genitore (cancellazione a cascata).
     *
     * @param todoId L'identificativo del ToDo genitore.
     */
    void deleteAttivitaByToDoId(int todoId);
}
