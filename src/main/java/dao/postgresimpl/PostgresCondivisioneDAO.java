package dao.postgresimpl;

import dao.CondivisioneDAO;
import database.DatabaseConnection;
import model.Condivisione;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresCondivisioneDAO implements CondivisioneDAO {

    private final Connection connection;
    private static final Logger LOGGER = Logger.getLogger(PostgresCondivisioneDAO.class.getName());

    public PostgresCondivisioneDAO(Connection connection) {
        this.connection = connection;
    }

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