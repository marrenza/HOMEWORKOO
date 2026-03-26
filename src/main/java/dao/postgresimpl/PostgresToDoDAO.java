package dao.postgresimpl;

import dao.ToDoDAO;
import dao.UtenteDAO;
import dao.AttivitaDAO;
import dao.CondivisioneDAO;
import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementazione concreta dell'interfaccia {@link ToDoDAO} per il database PostgreSQL.
 * <p>
 * Questa classe gestisce tutte le operazioni di persistenza relative ai {@link ToDo}.
 * Si occupa di query complesse che coinvolgono join con la tabella delle condivisioni
 * e utilizza altri DAO (Utente, Attivita, Condivisione) per ricostruire l'intero
 * grafo degli oggetti quando un ToDo viene letto dal database.
 * </p>
 *
 * @author Utente
 * @version 1.1
 */
public class PostgresToDoDAO implements ToDoDAO {

    private final Connection connection;
    private final UtenteDAO utenteDAO;
    private final AttivitaDAO attivitaDAO;
    private final CondivisioneDAO condivisioneDAO;

    private static final Logger logger = Logger.getLogger(PostgresToDoDAO.class.getName());

    private static final String COL_STATO = "stato";
    private static final String COL_ID_AUTORE = "id_autore";
    private static final String COL_ID_BACHECA = "id_bacheca";
    private static final String SELECT_COLUMNS = "id, titolo, descrizione, scadenza, image_path, url, colore_sfondo, stato, posizione, id_autore, id_bacheca";

    /**
     * Costruttore della classe.
     * Riceve le dipendenze (altri DAO) necessarie per costruire oggetti ToDo completi.
     *
     * @param connection      La connessione al database.
     * @param utenteDAO       Il DAO per gestire gli utenti.
     * @param attivitaDAO     Il DAO per gestire le attività della checklist.
     * @param condivisioneDAO Il DAO per gestire le condivisioni.
     */
    public PostgresToDoDAO(Connection connection, UtenteDAO utenteDAO, AttivitaDAO attivitaDAO, CondivisioneDAO condivisioneDAO) {
        this.connection = connection;
        this.utenteDAO = utenteDAO;
        this.attivitaDAO = attivitaDAO;
        this.condivisioneDAO = condivisioneDAO;
    }

    /**
     * Inserisce un nuovo ToDo nel database.
     *
     * @param todo Il ToDo da salvare.
     */
    @Override
    public void addToDo(ToDo todo) {
        String sql = "INSERT INTO todo (titolo, descrizione, scadenza, image_path, url, colore_sfondo, stato, posizione, id_autore, id_bacheca) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());
            stmt.setDate(3, java.sql.Date.valueOf(todo.getScadenza()));
            stmt.setString(4, todo.getImagePath());
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

    /**
     * Recupera un ToDo dato il suo ID.
     *
     * @param id L'ID del ToDo.
     * @return L'oggetto ToDo completo, o null se non trovato.
     */
    @Override
    public ToDo getToDoById(int id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM todo WHERE id = ?";
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

    /**
     * Recupera tutti i ToDo presenti nel sistema (senza filtri).
     *
     * @return Lista di tutti i ToDo.
     */
    @Override
    public List<ToDo> getAllToDo() {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT " + SELECT_COLUMNS + " FROM todo ORDER BY posizione ASC";

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

    /**
     * Aggiorna un ToDo esistente.
     *
     * @param todo Il ToDo con i dati aggiornati.
     */
    @Override
    public void updateToDo(ToDo todo) {
        String sql = "UPDATE todo SET titolo = ?, descrizione = ?, scadenza = ?, image_path = ?, " +
                "url = ?, colore_sfondo = ?, stato = ?, posizione = ?, id_bacheca = ? " +
                "WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, todo.getTitolo());
            stmt.setString(2, todo.getDescrizione());
            stmt.setDate(3, java.sql.Date.valueOf(todo.getScadenza()));
            stmt.setString(4, todo.getImagePath());
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

    /**
     * Elimina un ToDo dal database.
     *
     * @param id L'ID del ToDo da eliminare.
     */
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

    /**
     * Cerca i ToDo per termine di ricerca (titolo o descrizione).
     *
     * @param searchTerm Il testo da cercare.
     * @param userId     L'ID dell'utente corrente.
     * @return Lista di ToDo trovati.
     */
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
            stmt.setString(3, "%" + searchTerm.toLowerCase() + "%");
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

    /**
     * Cerca i ToDo che scadono entro (o il) una certa data.
     *
     * @param date   La data limite.
     * @param userId L'ID dell'utente corrente.
     * @return Lista di ToDo in scadenza.
     */
    @Override
    public List<ToDo> findToDosByScadenza(LocalDate date, int userId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT t.* FROM todo t " +
                "LEFT JOIN condivisione c ON t.id = c.id_todo " +
                "WHERE (t.id_autore = ? OR c.id_utente = ?) " +
                "AND t.scadenza = ? " +
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

    /**
     * Cerca i ToDo che scadono *esattamente* oggi.
     * <p>
     * Utilizza la condizione {@code t.scadenza = ?} per filtrare solo la data odierna.
     * </p>
     *
     * @param userId L'ID dell'utente corrente.
     * @return Lista di ToDo che scadono oggi.
     */
    @Override
    public List<ToDo> findToDosScadenzaOggi(int userId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT t.* FROM todo t " +
                "LEFT JOIN condivisione c ON t.id = c.id_todo " +
                "WHERE (t.id_autore = ? OR c.id_utente = ?) " +
                "AND t.scadenza = ? " + // UGUALE A OGGI
                "GROUP BY t.id " +
                "ORDER BY t.scadenza ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now())); // Data odierna
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                todos.add(costruisciToDoDaResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore findToDosScadenzaOggi", e);
        }
        return todos;
    }

    /**
     * Cerca i ToDo già scaduti (scadenza minore di oggi) e non completati.
     * <p>
     * Utilizza la condizione {@code t.scadenza < ?} e filtra per stato {@code NON_COMPLETATO}.
     * </p>
     *
     * @param userId L'ID dell'utente corrente.
     * @return Lista di ToDo scaduti da recuperare.
     */
    @Override
    public List<ToDo> findToDosScaduti(int userId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT t.* FROM todo t " +
                "LEFT JOIN condivisione c ON t.id = c.id_todo " +
                "WHERE (t.id_autore = ? OR c.id_utente = ?) " +
                "AND t.scadenza < ? " + // MINORE DI OGGI
                "AND t.stato = 'NON_COMPLETATO' " + // Solo quelli non fatti
                "GROUP BY t.id " +
                "ORDER BY t.scadenza ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.now())); // Confronta con oggi
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                todos.add(costruisciToDoDaResultSet(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore findToDosScaduti", e);
        }
        return todos;
    }

    /**
     * Recupera i ToDo da visualizzare in una specifica bacheca per un utente.
     * Include i ToDo creati dall'utente o condivisi con lui.
     *
     * @param bachecaId L'ID della bacheca.
     * @param utenteId  L'ID dell'utente corrente.
     * @return Lista di ToDo visibili.
     */
    @Override
    public List<ToDo> getToDosForBachecaAndUtente(int bachecaId, int utenteId) {
        List<ToDo> todos = new ArrayList<>();
        String sql = "SELECT t.* FROM todo t " +
                "JOIN bacheca b_origin ON t.id_bacheca = b_origin.id " +
                "LEFT JOIN condivisione c ON t.id = c.id_todo " +
                "WHERE b_origin.titolo = (SELECT titolo FROM bacheca WHERE id = ?) " +
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

    /**
     * Imposta come completati tutti i ToDo di una bacheca.
     *
     * @param bachecaId L'ID della bacheca.
     */
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