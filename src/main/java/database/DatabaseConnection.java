package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static Connection connection = null;
    private static final String DBNAME = "todo_manager_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "3570";
    private static final String URL = "jdbc:postgresql://localhost:5432/todo_manager_db";
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    private DatabaseConnection() {
    }

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

