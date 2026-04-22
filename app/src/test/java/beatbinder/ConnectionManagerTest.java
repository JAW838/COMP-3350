package beatbinder;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import beatbinder.persistence.ConnectionManager;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.persistence.PersistenceType;
import beatbinder.presentation.text.content.TextInitialiser;

public class ConnectionManagerTest {
    
    @Test
    public void testProductionDatabaseConnection() {
        // Initialize text first
        TextInitialiser.initailizeText();
        
        // Initialize the production connection
        PersistenceFactory.initialise(PersistenceType.PROD, true);
        
        // Get the connection
        Connection connection = ConnectionManager.get();
        
        // Verify the connection is not null
        assertNotNull(connection, "Production database connection (prod.db) should not be null");
        
        // Verify the connection is valid
        try {
            assertTrue(connection.isValid(5), "Production database connection should be valid");
        } catch (SQLException e) {
            fail("Failed to validate production database connection: " + e.getMessage());
        } finally {
            // Close the connection
            ConnectionManager.close();
        }
    }

    @Test
    public void testDatabaseConnection() {
        // Initialize text first
        TextInitialiser.initailizeText();
        
        // Initialize the test connection
        PersistenceFactory.initialise(PersistenceType.TEST, true);
        
        // Get the connection
        Connection connection = ConnectionManager.get();
        
        // Verify the connection is not null
        assertNotNull(connection, "Test database connection (test.db) should not be null");
        
        // Verify the connection is valid
        try {
            assertTrue(connection.isValid(5), "Test database connection should be valid");
        } catch (SQLException e) {
            fail("Failed to validate test database connection: " + e.getMessage());
        } finally {
            // Close the connection
            ConnectionManager.close();
        }
    }
}