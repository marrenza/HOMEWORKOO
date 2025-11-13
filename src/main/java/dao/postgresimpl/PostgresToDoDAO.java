package dao.postgresimpl;

import dao.ToDoDAO;
import dao.UtenteDAO;
import dao.AttivitaDAO;
import dao.CondivisioneDAO;
import database.DatabaseConnection;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;

public class PostgresToDoDAO implements ToDoDAO{
    private final Connection connection;
    private final UtenteDAO utenteDAO;
    private final AttivitaDAO attivitaDAO;
    private final CondivisioneDAO condivisioneDAO;

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



    public PostgresToDoDAO(Connection connection, UtenteDAO utenteDAO, AttivitaDAO attivitaDAO, CondivisioneDAO condivisioneDAO) {
        this.connection = connection;
        this.utenteDAO = utenteDAO;
        this.attivitaDAO = attivitaDAO;
        this.condivisioneDAO = condivisioneDAO;
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
    public List<ToDo> findToDosByTerm(String searchTerm, int userId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT t.* FROM todo t " +
                "LEFT JOIN condivisione c ON t.id = c.id_todo " +
                "WHERE (t.id_autore = ? OR c.id_utente = ?) " +
                "AND (LOWER(t.titolo) LIKE ? OR LOWER(t.descrizione) LIKE ?) " +
                "GROUP BY t.id " +
                "ORDER BY t.scadenza ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, "%" + searchTerm.toLowerCase() + "%"); // Cerca 'searchTerm' ovunque
            stmt.setString(4, "%" + searchTerm.toLowerCase() + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                todos.add(costruisciToDoDaResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore findToDosByTerm", e);
        }
        return todos;
    }

    @Override
    public List<ToDo> findToDosByScadenza(LocalDate date, int userId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT t.* FROM todo t " +
                "LEFT JOIN condivisione c ON t.id = c.id_todo " +
                "WHERE (t.id_autore = ? OR c.id_utente = ?) " +
                "AND t.scadenza <= ? " +
                "GROUP BY t.id " +
                "ORDER BY t.scadenza ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                todos.add(costruisciToDoDaResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore findToDosByScadenza", e);
        }
        return todos;
    }

    @Override
    public List<ToDo> findToDosScadenzaOggi(int userId) {
        return findToDosByScadenza(LocalDate.now(), userId);
    }

    @Override
    public List<ToDo> getToDosForBachecaAndUtente(int bachecaId, int utenteId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT t.* FROM todo t " +
                "LEFT JOIN condivisione c ON t.id = c.id_todo " +
                "WHERE t.id_bacheca = ? " +
                "AND (t.id_autore = ? OR c.id_utente = ?) " +
                "GROUP BY t.id " +
                "ORDER BY t.posizione ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, bachecaId);
            stmt.setInt(2, utenteId);
            stmt.setInt(3, utenteId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                todos.add(costruisciToDoDaResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore getToDosForBachecaAndUtente", e);
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

        List<Attivita> attivitaList = attivitaDAO.getAttivitaByToDoId(id);
        Checklist checklist = new Checklist();
        checklist.setAttivita(attivitaList);
        todo.setChecklist(checklist);

        List<Condivisione> condivisioni = condivisioneDAO.getCondivisioniByToDoId(id);
        for (Condivisione c : condivisioni) {
            c.setUtente(utenteDAO.getUtenteById(c.getIdUtente()));
            c.setToDo(todo);
        }
        todo.setCondivisioni(condivisioni);

        return todo;
    }
}
