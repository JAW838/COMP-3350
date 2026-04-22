/*
 * This code gracefully donated by the sample project.
 * If it ain't broke, right?
 */

package beatbinder.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A class that handles the creation, closure, and resetting of the SQLite database connection.
 */
public class ConnectionManager {

    /**
     * Connection with the SQLite database.
     */
    private static Connection connection;

    /**
     * Creates a connection with the SQLite database.
     * 
     * @param dbFilePath the path to the database.
     * @throws RuntimeException if database is not found.
     */
    public static void initialize(String dbFilePath) {
        if (connection != null) return;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize DB connection", e);
        }
    }

    /**
     * Retrieves the connection made to the SQLite database.
     * 
     * @return the connection with the SQLite database.
     * @throws IllegalStateException if connection with SQLite database does not exist.
     */
    public static Connection get() {
        if (connection == null) {
            throw new IllegalStateException("Connection not initialized");
        }
        return connection;
    }

    /**
     * Closes the connection with the SQLite database.
     * 
     * @throws RuntimeException if the connection fails to close.
     */
    public static void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close DB connection", e);
            } finally {
                connection = null;
            }
        }
    }

    /**
     * Resets the connection with the SQLite database.
     */
    public static void reset() {
        close();
    }

    /**
     * Retrieves whether there is an active connection to the SQLite database.
     * @return whether there is an active connection.
     */
    public static boolean isInitialized() {
        return connection != null;
    }
}
