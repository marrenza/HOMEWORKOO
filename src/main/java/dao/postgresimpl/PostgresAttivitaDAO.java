package dao.postgresimpl;

import dao.AttivitaDAO;
import database.DatabaseConnection;
import model.Attivita;
import model.StatoAttivita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione concreta dell'interfaccia {@link AttivitaDAO} per il database PostgreSQL.
 * Questa classe gestisce le operazioni CRUD sulla tabella 'attivita' utilizzando
 * connessioni JDBC e PreparedStatement per garantire la sicurezza e l'efficienza delle query.
 *
 * @author marrenza
 * @version 1.0
 */
public class PostgresAttivitaDAO implements AttivitaDAO {
    /** La connessione attiva al database. */
    private final Connection connection;

    /** Logger per tracciare eventuali eccezioni SQL. */
    private static final Logger logger = Logger.getLogger(PostgresAttivitaDAO.class.getName());

    /**
     * Costruttore della classe DAO.
     *
     * @param connection La connessione al database da utilizzare per le operazioni.
     */
    public PostgresAttivitaDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Salva una nuova attività nel database.
     * <p>
     * Esegue una query {@code INSERT} e utilizza la clausola {@code RETURNING id}
     * di PostgreSQL per recuperare immediatamente la chiave primaria generata
     * e aggiornare l'oggetto {@link Attivita} passato come parametro.
     * </p>
     *
     * @param attivita L'oggetto Attivita da salvare.
     */
    @Override
    public void addAttivita(Attivita attivita) {
        String sql = "INSERT INTO attivita (nome, stato, id_todo) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, attivita.getNome());
            stmt.setString(2, attivita.getStato().name());
            stmt.setInt(3, attivita.getIdTodo());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                attivita.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore addAttivita", e);
        }
    }

    /**
     * Recupera tutte le attività associate a uno specifico ToDo.
     * <p>
     * Esegue una query {@code SELECT} filtrando per la chiave esterna {@code id_todo}.
     * Converte ogni riga del {@code ResultSet} in un oggetto {@link Attivita}.
     * </p>
     *
     * @param todoId L'identificativo del ToDo genitore.
     * @return Una lista di oggetti {@link Attivita}.
     */
    @Override
    public List<Attivita> getAttivitaByToDoId(int todoId) {
        List<Attivita> attivitaList = new ArrayList<>();
        String sql = "SELECT * FROM attivita WHERE id_todo = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                attivitaList.add(new Attivita(
                        rs.getInt("id"),
                        rs.getInt("id_todo"),
                        rs.getString("nome"),
                        StatoAttivita.valueOf(rs.getString("stato"))
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore getAttivitaByToDoId", e);
        }
        return attivitaList;
    }

    /**
     * Aggiorna i dati di un'attività esistente nel database.
     * Modifica il nome e lo stato dell'attività identificata dal suo ID.
     *
     * @param attivita L'oggetto Attivita contenente i dati aggiornati.
     */
    @Override
    public void updateAttivita(Attivita attivita) {
        String sql = "UPDATE attivita SET nome = ?, stato = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, attivita.getNome());
            stmt.setString(2, attivita.getStato().name());
            stmt.setInt(3, attivita.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore updateAttivita", e);
        }
    }

    /**
     * Elimina una singola attività dal database specificando il suo ID.
     *
     * @param attivitaId L'identificativo dell'attività da rimuovere.
     */
    @Override
    public void deleteAttivita(int attivitaId) {
        String sql = "DELETE FROM attivita WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, attivitaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore deleteAttivita", e);
        }
    }

    /**
     * Elimina tutte le attività collegate a un determinato ToDo.
     * Utile per mantenere l'integrità referenziale o pulire i dati quando un ToDo viene eliminato.
     *
     * @param todoId L'ID del ToDo genitore.
     */
    @Override
    public void deleteAttivitaByToDoId(int todoId) {
        String sql = "DELETE FROM attivita WHERE id_todo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore deleteAttivitaByToDoId", e);
        }
    }
}
