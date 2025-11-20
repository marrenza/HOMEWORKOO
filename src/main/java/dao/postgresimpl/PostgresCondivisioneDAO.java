package dao.postgresimpl;

import dao.CondivisioneDAO;
import database.DatabaseConnection;
import model.Condivisione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione concreta dell'interfaccia {@link CondivisioneDAO} per il database PostgreSQL.
 * Questa classe gestisce la tabella associativa 'condivisione', che realizza la relazione
 * molti-a-molti tra Utenti e ToDo. Gestisce l'aggiunta e la rimozione dei permessi di
 * visualizzazione condivisa.
 *
 * @author marrenza
 * @version 1.0
 */
public class PostgresCondivisioneDAO implements CondivisioneDAO {
    /** La connessione attiva al database. */
    private final Connection connection;

    /** Logger per tracciare errori e warning. */
    private static final Logger LOGGER = Logger.getLogger(PostgresCondivisioneDAO.class.getName());

    /**
     * Costruttore della classe DAO.
     *
     * @param connection La connessione al database da utilizzare.
     */
    public PostgresCondivisioneDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Aggiunge una nuova condivisione al database.
     * Tenta di inserire una coppia (id_utente, id_todo). Se la coppia esiste già,
     * il database solleva un'eccezione di violazione di chiave primaria (SQLState 23505).
     * In questo caso, l'eccezione viene catturata e ignorata, poiché il risultato desiderato
     * (il ToDo è condiviso) è già stato raggiunto.
     *
     * @param condivisione L'oggetto Condivisione da salvare.
     */
    @Override
    public void addCondivisione(Condivisione condivisione) {
        String sql = "INSERT INTO condivisione (id_utente, id_todo) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, condivisione.getIdUtente());
            stmt.setInt(2, condivisione.getIdToDo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Ignora l'errore di chiave duplicata (se già condiviso)
            if (!e.getSQLState().equals("23505")) {
                LOGGER.log(Level.SEVERE, "Errore addCondivisione", e);
            }
        }
    }

    /**
     * Rimuove una condivisione dal database.
     * Utilizza sia l'ID utente che l'ID ToDo per identificare univocamente la riga da eliminare.
     *
     * @param condivisione L'oggetto Condivisione da rimuovere.
     */
    @Override
    public void deleteCondivisione(Condivisione condivisione) {
        String sql = "DELETE FROM condivisione WHERE id_utente = ? AND id_todo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, condivisione.getIdUtente());
            stmt.setInt(2, condivisione.getIdToDo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore deleteCondivisione", e);
        }
    }

    /**
     * Recupera tutte le condivisioni associate a un determinato ToDo.
     * Restituisce una lista di oggetti {@link Condivisione} contenenti gli ID degli utenti
     * con cui il ToDo è condiviso.
     *
     * @param todoId L'ID del ToDo.
     * @return Una lista di condivisioni.
     */
    @Override
    public List<Condivisione> getCondivisioniByToDoId(int todoId) {
        List<Condivisione> condivisioni = new ArrayList<>();
        String sql = "SELECT * FROM condivisione WHERE id_todo = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, todoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                condivisioni.add(new Condivisione(
                        rs.getInt("id_utente"),
                        rs.getInt("id_todo")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore getCondivisioniByToDoId", e);
        }
        return condivisioni;
    }

    /**
     * Recupera tutte le condivisioni associate a un determinato Utente.
     * Restituisce una lista di oggetti {@link Condivisione} che rappresentano i ToDo
     * condivisi CON questo utente da altre persone.
     *
     * @param utenteId L'ID dell'utente.
     * @return Una lista di condivisioni.
     */
    @Override
    public List<Condivisione> getCondivisioniByUtenteId(int utenteId) {
        List<Condivisione> condivisioni = new ArrayList<>();
        String sql = "SELECT * FROM condivisione WHERE id_utente = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, utenteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                condivisioni.add(new Condivisione(
                        rs.getInt("id_utente"),
                        rs.getInt("id_todo")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore getCondivisioniByUtenteId", e);
        }
        return condivisioni;
    }
}