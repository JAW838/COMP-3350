package beatbinder.persistence.stub;

import beatbinder.persistence.ConnectionManager;
import org.junit.jupiter.api.*;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionIntegrationTest {
    private static final String TEST_DB_PATH = "test.db";

    @BeforeEach
    void cleanUpBefore() {
        ConnectionManager.reset();  // Ensure clean state
        deleteTestDbFile();         // Delete existing DB if any
    }

    @AfterEach
    void cleanUpAfter() {
        ConnectionManager.reset();
        deleteTestDbFile();
    }

    private void deleteTestDbFile() {
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    void testConnectionIsInitialized() {
        assertFalse(ConnectionManager.isInitialized());

        ConnectionManager.initialize(TEST_DB_PATH);
        assertTrue(ConnectionManager.isInitialized());

        Connection conn = ConnectionManager.get();
        assertNotNull(conn);
    }

    @Test
    void testConnectionIsSingleton() {
        ConnectionManager.initialize(TEST_DB_PATH);
        Connection conn1 = ConnectionManager.get();

        ConnectionManager.initialize(TEST_DB_PATH); // should not reinitialize
        Connection conn2 = ConnectionManager.get();

        assertSame(conn1, conn2);
    }

    @Test
    void testGetWithoutInitializationThrows() {
        assertThrows(IllegalStateException.class, ConnectionManager::get);
    }

    @Test
    void testCloseConnection() throws SQLException {
        ConnectionManager.initialize(TEST_DB_PATH);
        Connection conn = ConnectionManager.get();

        ConnectionManager.close();

        assertFalse(ConnectionManager.isInitialized());
        assertTrue(conn.isClosed());
    }

    @Test
    void testResetClosesConnection() throws SQLException {
        ConnectionManager.initialize(TEST_DB_PATH);
        Connection conn = ConnectionManager.get();

        ConnectionManager.reset();

        assertFalse(ConnectionManager.isInitialized());
        assertTrue(conn.isClosed(), "Connection should be closed after reset");
    }

    @Test // edgy edge case
    void testDoubleCloseDoesNotThrow() {
        ConnectionManager.initialize(TEST_DB_PATH);
        ConnectionManager.close();
        assertDoesNotThrow(ConnectionManager::close);
    }

    @Test
    void testReinitializeAfterReset() throws SQLException {
        ConnectionManager.initialize(TEST_DB_PATH);
        Connection conn1 = ConnectionManager.get();
        ConnectionManager.reset();

        assertTrue(conn1.isClosed());

        ConnectionManager.initialize(TEST_DB_PATH);
        Connection conn2 = ConnectionManager.get();

        assertNotNull(conn2);
        assertFalse(conn2.isClosed());
        assertNotSame(conn1, conn2);
    }


}
