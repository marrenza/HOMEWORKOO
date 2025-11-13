package dao.postgresimpl;

import dao.BachecaDAO; // <-- La TUA interfaccia (File 1)
import database.DatabaseConnection;
import model.Bacheca;
import model.TitoloBacheca; // <-- Il tuo model (con fromString)
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class PostgresBachecaDAO implements BachecaDAO {

    private static final String COLUMN_ID_UTENTE = "id_utente";
    private static final String COLUMN_TITOLO = "titolo";

    private final Connection connection;
    private static final Logger logger = Logger.getLogger(PostgresBachecaDAO.class.getName());

    public PostgresBachecaDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addBacheca(Bacheca bacheca) {
        // Controllo per evitare duplicati (ispirato al prof)
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

    @Override
    public Bacheca getBachecaById(int id) {
        String sql = "SELECT * FROM bacheca WHERE id = ?";
        try (PreparedStatement stmt = this.connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Bacheca( // Costruttore a 4 param
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
