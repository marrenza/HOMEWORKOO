package dao.postgresimpl;

import dao.BachecaDAO;
import database.DatabaseConnection;
import model.Bacheca;
import model.TitoloBacheca;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementazione concreta dell'interfaccia {@link BachecaDAO} per il database PostgreSQL.
 * Questa classe gestisce la persistenza delle bacheche (contenitori di ToDo)
 * utilizzando query SQL dirette tramite JDBC. Gestisce anche la logica per prevenire
 * la creazione di bacheche duplicate per lo stesso utente.
 *
 * @author marrenza
 * @version 1.0
 */
public class PostgresBachecaDAO implements BachecaDAO {
    /** Nome della colonna ID Utente nel database. */
    private static final String COLUMN_ID_UTENTE = "id_utente";

    /** Nome della colonna Titolo nel database. */
    private static final String COLUMN_TITOLO = "titolo";

    /** La connessione attiva al database. */
    private final Connection connection;

    /** Logger per tracciare errori e informazioni operative. */
    private static final Logger logger = Logger.getLogger(PostgresBachecaDAO.class.getName());

    /**
     * Costruttore della classe DAO.
     *
     * @param connection La connessione al database da utilizzare.
     */
    public PostgresBachecaDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Aggiunge una nuova bacheca al database.
     * Implementa una logica di controllo duplicati: prima di inserire, verifica se
     * esiste già una bacheca con lo stesso titolo per lo stesso utente.
     * Se esiste, non crea un duplicato ma aggiorna l'ID dell'oggetto passato con quello esistente.
     * Se non esiste, esegue l'inserimento e recupera l'ID generato.
     *
     * @param bacheca L'oggetto Bacheca da salvare.
     */
    @Override
    public void addBacheca(Bacheca bacheca) {
        Bacheca existing = getBachecaByTitoloAndUtente(bacheca.getTitolo().name(), bacheca.getIdUtente());
        if (existing != null) {
            logger.info("La bacheca \"" + bacheca.getTitolo().name() + "\" esiste già.");
            bacheca.setId(existing.getId());
            return;
        }

        String sql = "INSERT INTO bacheca (" + COLUMN_TITOLO + ", descrizione, " + COLUMN_ID_UTENTE + ") VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, bacheca.getTitolo().name());
            stmt.setString(2, bacheca.getDescrizione());
            stmt.setInt(3, bacheca.getIdUtente());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                bacheca.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.severe("Errore addBacheca: " + e.getMessage());
        }
    }

    /**
     * Recupera una bacheca tramite il suo ID.
     * Converte il record del database in un oggetto {@link Bacheca}, risolvendo
     * l'enum {@link TitoloBacheca} dalla stringa salvata.
     *
     * @param id L'ID della bacheca da cercare.
     * @return L'oggetto Bacheca trovato, o {@code null} se non esiste.
     */
    @Override
    public Bacheca getBachecaById(int id) {
        String sql = "SELECT * FROM bacheca WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Bacheca(
                        rs.getInt("id"),
                        TitoloBacheca.fromString(rs.getString(COLUMN_TITOLO)),
                        rs.getString("descrizione"),
                        rs.getInt(COLUMN_ID_UTENTE)
                );
            }
        } catch (SQLException e) {
            logger.severe("Errore getBachecaById: " + e.getMessage());
        }
        return null;
    }

    /**
     * Recupera tutte le bacheche associate a un determinato utente.
     *
     * @param userId L'ID dell'utente proprietario.
     * @return Una lista di oggetti Bacheca.
     */
    @Override
    public List<Bacheca> getBachecaByUserId(int userId) {
        List<Bacheca> bacheche = new ArrayList<>();
        String sql = "SELECT * FROM bacheca WHERE " + COLUMN_ID_UTENTE + " = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bacheche.add(new Bacheca(
                        rs.getInt("id"),
                        TitoloBacheca.fromString(rs.getString(COLUMN_TITOLO)),
                        rs.getString("descrizione"),
                        rs.getInt(COLUMN_ID_UTENTE)
                ));
            }
        } catch (SQLException e) {
            logger.severe("Errore getBachecaByUserId: " + e.getMessage());
        }
        return bacheche;
    }

    /**
     * Aggiorna le informazioni di una bacheca esistente.
     * Attualmente, aggiorna solo la descrizione.
     *
     * @param bacheca L'oggetto Bacheca con i dati aggiornati.
     */
    @Override
    public void updateBacheca(Bacheca bacheca) {
        String sql = "UPDATE bacheca SET descrizione = ? WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setString(1, bacheca.getDescrizione());
            stmt.setInt(2, bacheca.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Errore updateBacheca: " + e.getMessage());
        }
    }

    /**
     * Elimina una bacheca dal database.
     *
     * @param id L'ID della bacheca da eliminare.
     */
    @Override
    public void deleteBacheca(int id) {
        String sql = "DELETE FROM bacheca WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Errore deleteBacheca: " + e.getMessage());
        }
    }

    /**
     * Cerca una bacheca specifica in base al titolo e all'utente proprietario.
     * Questo metodo è fondamentale per garantire che un utente non abbia due bacheche
     * con lo stesso titolo (es. due bacheche "Lavoro").
     *
     * @param titolo   Il titolo della bacheca (stringa dell'enum).
     * @param idUtente L'ID dell'utente proprietario.
     * @return L'oggetto Bacheca se trovato, altrimenti {@code null}.
     */
    @Override
    public Bacheca getBachecaByTitoloAndUtente(String titolo, int idUtente) {
        String sql = "SELECT * FROM bacheca WHERE " + COLUMN_TITOLO + " = ? AND " + COLUMN_ID_UTENTE + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, titolo);
            stmt.setInt(2, idUtente);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Bacheca(
                        rs.getInt("id"),
                        TitoloBacheca.fromString(rs.getString(COLUMN_TITOLO)),
                        rs.getString("descrizione"),
                        rs.getInt(COLUMN_ID_UTENTE)
                );
            }
        } catch (SQLException e) {
            logger.severe("Errore getBachecaByTitoloAndUtente: " + e.getMessage());
        }
        return null;
    }
}
