package dao.postgresimpl;

import dao.UtenteDAO;
import database.DatabaseConnection;
import model.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione concreta dell'interfaccia {@link UtenteDAO} per il database PostgreSQL.
 * Questa classe gestisce tutte le operazioni di persistenza relative agli utenti del sistema,
 * inclusi registrazione, login e gestione del profilo, utilizzando query JDBC sicure.
 *
 * @author marrenza
 * @version 1.0
 */
public class PostgresUtenteDAO implements UtenteDAO{
    /** La connessione attiva al database. */
    private final Connection connection;

    /** Logger per la gestione degli errori SQL. */
    private static final Logger LOGGER = Logger.getLogger(PostgresUtenteDAO.class.getName());

    /**
     * Costruttore della classe DAO.
     *
     * @param connection La connessione al database da utilizzare.
     */
    public PostgresUtenteDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserisce un nuovo utente nel database.
     * Utilizza la clausola SQL {@code RETURNING id} supportata da PostgreSQL
     * per recuperare immediatamente l'ID generato per il nuovo utente e aggiornare l'oggetto.
     *
     * @param utente L'oggetto Utente da salvare.
     */
    @Override
    public void addUtente(Utente utente) {
        String sql = "INSERT INTO utente (nome, login, password) VALUES (?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getLogin());
            stmt.setString(3, utente.getPassword());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                utente.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore addUtente", e);
        }
    }

    /**
     * Recupera un utente dal database tramite il suo ID univoco.
     *
     * @param id L'ID dell'utente da cercare.
     * @return L'oggetto Utente trovato, oppure {@code null} se non esiste.
     */
    @Override
    public Utente getUtenteById(int id) {
        String sql = "SELECT * FROM utente WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Utente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("login"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore getUtenteById", e);
        }
        return null; // Non trovato
    }

    /**
     * Recupera un utente dal database tramite il suo login (username).
     * Questo metodo è essenziale per la fase di autenticazione.
     *
     * @param login Il nome utente da cercare.
     * @return L'oggetto Utente trovato, oppure {@code null} se non esiste.
     */
    @Override
    public Utente getUtenteByLogin(String login) {
        String sql = "SELECT * FROM utente WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Usa il TUO costruttore a 4 parametri
                return new Utente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("login"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore getUtenteByLogin", e);
        }
        return null; // Non trovato
    }

    /**
     * Recupera la lista completa di tutti gli utenti nel sistema.
     *
     * @return Una lista di oggetti Utente.
     */
    @Override
    public List<Utente> getAllUtenti() {
        List<Utente> utenti = new ArrayList<>();
        String sql = "SELECT * FROM utente";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Usa il TUO costruttore a 4 parametri
                utenti.add(new Utente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("login"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore getAllUtenti", e);
        }
        return utenti;
    }

    /**
     * Aggiorna i dati di un utente esistente (nome, login, password).
     *
     * @param utente L'oggetto Utente con i dati aggiornati.
     */
    @Override
    public void updateUtente(Utente utente) {
        String sql = "UPDATE utente SET nome = ?, login = ?, password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, utente.getNome());
            stmt.setString(2, utente.getLogin());
            stmt.setString(3, utente.getPassword());
            stmt.setInt(4, utente.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore updateUtente", e);
        }
    }

    /**
     * Elimina un utente dal database tramite il suo ID.
     *
     * @param id L'ID dell'utente da eliminare.
     */
    @Override
    public void deleteUtenteById(int id) {
        String sql = "DELETE FROM utente WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore deleteUtenteById", e);
        }
    }
}
