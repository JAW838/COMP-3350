package beatbinder.persistence.sqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class SchemaInitializer {
    private static final Logger logger = Logger.getLogger(SchemaInitializer.class.getName());
    private static boolean loggingEnabled = false;  // Disabled by default

    // Method to enable/disable logging
    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }

    public static void initializeSchema(Connection connection) throws SQLException {
        executeSqlFromResource(connection, "/db/schema/schema_init.sql");
    }

    public static void dropSchema(Connection connection) throws SQLException {
        executeSqlFromResource(connection, "/db/schema/schema_drop.sql");
    }

    // Loads files from resource folder and executes statements one by one
    private static void executeSqlFromResource(Connection connection, String resourcePath) throws SQLException {
        try {
            String sql = loadResourceAsString(resourcePath);
            String[] statements = sql.split(";");

            try (Statement statement = connection.createStatement()) {
                // Enable foreign keys
                statement.execute("PRAGMA foreign_keys = ON");

                // Execute each statement
                for (String sqlStatement : statements) {
                    sqlStatement = sqlStatement.trim();
                    if (!sqlStatement.isEmpty()) {
                        try {
                            statement.execute(sqlStatement);
                            if (loggingEnabled) {
                                logger.info("Successfully executed: " + sqlStatement.substring(0, Math.min(50, sqlStatement.length())) + "...");
                            }
                        } catch (SQLException e) {
                            if (loggingEnabled) {
                                logger.severe("Failed to execute statement: " + sqlStatement);
                            }
                            throw e; // Re-throw to stop the schema creation
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new SQLException("Failed to load SQL from resource: " + resourcePath, e);
        }
    }

    private static String loadResourceAsString(String resourcePath) throws IOException {
        try (InputStream is = SchemaInitializer.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }
}