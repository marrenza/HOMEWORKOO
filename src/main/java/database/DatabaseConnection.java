package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestisce la connessione al database PostgreSQL tramite il pattern Singleton.
 * <p>
 * Questa classe garantisce che esista un'unica istanza della connessione attiva
 * per tutta la durata dell'applicazione, centralizzando la configurazione e
 * ottimizzando l'uso delle risorse.
 * </p>
 *
 * @author marrenza
 * @version 1.0
 */
public class DatabaseConnection {
    /** L'unica istanza condivisa della connessione (Singleton). */
    private static Connection connection = null;

    /** Nome del database a cui connettersi. */
    private static final String DBNAME = "todo_manager_db";

    /** Username per l'accesso al database. */
    private static final String USER = "postgres";

    /** Password per l'accesso al database. */
    private static final String PASSWORD = "3570";

    /** URL di connessione JDBC formattato per PostgreSQL. */
    private static final String URL = "jdbc:postgresql://localhost:5432/todo_manager_db";

    /** Logger per tracciare eventi di connessione ed eventuali errori. */
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    /**
     * Costruttore privato.
     * Impedisce l'istanziazione diretta della classe dall'esterno,
     * forzando l'uso del metodo {@link #getConnection()}.
     */
    private DatabaseConnection() {
    }

    /**
     * Restituisce l'istanza unica della connessione al database.
     * Carica dinamicamente il driver PostgreSQL.
     *
     * @return L'oggetto {@link Connection} attivo verso il database.
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                LOGGER.info("Connessione al database stabilita.");
            } catch (ClassNotFoundException e) {
                LOGGER.severe("Errore: Driver PostgreSQL non trovato.");
                e.printStackTrace();
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Errore di connessione al database", e);
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Chiude la connessione al database se è attualmente aperta.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                LOGGER.info("Connessione al database chiusa.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Errore durante la chiusura della connessione al database:", e);
                e.printStackTrace();
            }
        }
    }
}

