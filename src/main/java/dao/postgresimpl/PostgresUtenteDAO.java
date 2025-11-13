package dao.postgresimpl;

import dao.UtenteDAO;
import database.DatabaseConnection;
import model.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresUtenteDAO implements UtenteDAO{
    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(PostgresUtenteDAO.class.getName());

    public PostgresUtenteDAO(Connection connection) {
        this.connection = connection;
    }

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
