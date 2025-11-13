package dao.postgresimpl;

import dao.ToDoDAO;
import dao.UtenteDAO;
import database.DatabaseConnection;
import model.StatoToDo;
import model.ToDo;
import model.Utente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresToDoDAO implements ToDoDAO{
    private final Connection connection;
    private final UtenteDAO utenteDAO;
    private static final Logger logger = Logger.getLogger(PostgresToDoDAO.class.getName());
    private static final String COL_DESCRIZIONE = "descrizione";
    private static final String COL_COLORE = "colore";
    private static final String COL_TITOLO = "titolo";
    private static final String COL_SCADENZA = "scadenza";
    private static final String COL_STATO = "stato";
    private static final String COL_POSIZIONE = "posizione";
    private static final String COL_ID_BOARD = "id_board";
    private static final String COL_ID_UTENTE = "id_utente";
    private static final String COL_CONDIVISO_DA_UTENTE = "condiviso_da_utente";
    private static final String COL_ID_AUTORE = "id_autore";
    private static final String COL_ID_BACHECA = "id_bacheca";



    public PostgresToDoDAO(Connection connection, UtenteDAO utenteDAO) {
        this.connection = connection;
        this.utenteDAO = utenteDAO;
    }

    @Override
    public void addToDo(ToDo todo) {
        String sql = "INSERT INTO todo (titolo, descrizione, scadenza, image_path, url, colore_sfondo, stato, posizione, id_autore, id_bacheca) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());

            stmt.setDate(3, java.sql.Date.valueOf(todo.getScadenza()));
            stmt.setString(4, todo.getImaginePath());
            stmt.setString(5, todo.getURL());
            stmt.setString(6, todo.getColoreSfondo());


            if (todo.getStato() == null) {
                todo.setStato(StatoToDo.NON_COMPLETATO);
            }
            stmt.setString(7, todo.getStato().name());

            stmt.setInt(8, todo.getPosizione());
            stmt.setInt(9, todo.getIdAutore());
            stmt.setInt(10, todo.getIdBacheca());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                todo.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore addToDo", e);
        }
    }

    @Override
    public ToDo getToDoById(int id) {
        String sql = "SELECT * FROM todo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return costruisciToDoDaResultSet(rs);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore getToDoById", e);
        }
        return null;
    }

    @Override
    public List<ToDo> getAllToDo() {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todo ORDER BY posizione ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                todos.add(costruisciToDoDaResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore getAllToDo", e);
        }
        return todos;
    }

    @Override
    public void updateToDo(ToDo todo) {
        String sql = "UPDATE todo SET titolo = ?, descrizione = ?, scadenza = ?, image_path = ?, " +
                "url = ?, colore_sfondo = ?, stato = ?, posizione = ?, id_bacheca = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());
            stmt.setDate(3, java.sql.Date.valueOf(todo.getScadenza()));
            stmt.setString(4, todo.getImaginePath());
            stmt.setString(5, todo.getURL());
            stmt.setString(6, todo.getColoreSfondo());
            stmt.setString(7, todo.getStato().name());
            stmt.setInt(8, todo.getPosizione());
            stmt.setInt(9, todo.getIdBacheca());
            stmt.setInt(10, todo.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore updateToDo", e);
        }
    }

    @Override
    public void deleteToDo(int id) {
        String sql = "DELETE FROM todo WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore deleteToDo", e);
        }
    }

    @Override
    public List<ToDo> getToDoByBachecaId(int bachecaId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT * FROM todo WHERE " + COL_ID_BACHECA + " = ? ORDER BY posizione ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bachecaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                todos.add(costruisciToDoDaResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore getToDoByBachecaId", e);
        }
        return todos;
    }

    @Override
    public void markAllToDoAsCompletedByBachecaId(int bachecaId) {
        String sql = "UPDATE todo SET " + COL_STATO + " = ? WHERE " + COL_ID_BACHECA + " = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, StatoToDo.COMPLETATO.name());
            stmt.setInt(2, bachecaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore markAllToDoAsCompleted", e);
        }
    }



    private ToDo costruisciToDoDaResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String titolo = rs.getString("titolo");
        String descrizione = rs.getString("descrizione");
        LocalDate scadenza = rs.getDate("scadenza").toLocalDate();
        String imaginePath = rs.getString("image_path");
        String url = rs.getString("url");
        String coloreSfondo = rs.getString("colore_sfondo");
        int posizione = rs.getInt("posizione");
        StatoToDo stato = StatoToDo.valueOf(rs.getString(COL_STATO));
        int idAutore = rs.getInt(COL_ID_AUTORE);
        int idBacheca = rs.getInt(COL_ID_BACHECA);

        Utente autore = utenteDAO.getUtenteById(idAutore);

        ToDo todo = new ToDo(id, titolo, descrizione, scadenza, imaginePath, url, coloreSfondo, posizione, autore);

        todo.setStato(stato);
        todo.setIdBacheca(idBacheca);

        // (Mancano checklist e condivisioni, le caricheremo dopo)

        return todo;
    }



}
