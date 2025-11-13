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

public class PostgresAttivitaDAO implements AttivitaDAO {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(PostgresAttivitaDAO.class.getName());

    public PostgresAttivitaDAO(Connection connection) {
        this.connection = connection;
    }

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
