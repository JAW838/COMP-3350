package beatbinder.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

public class DatabaseSeederTest {

    private Connection connection;

    @BeforeEach
    public void setup() {
        // Initialize the persistence layer with TEST type but don't seed yet
        PersistenceFactory.initialise(PersistenceType.TEST, false);
        connection = ConnectionManager.get();

        // Verify the connection is not null
        assertNotNull(connection, "Database connection should not be null");
    }

    @AfterEach
    public void tearDown() {
        // Reset the persistence factory to clean up resources
        PersistenceFactory.reset();
    }

    @Test
    public void testSeedFilesCanBeLoaded() {
        // Test that each seed file can be loaded
        // We're not executing the SQL, just verifying the files can be found
        assertDoesNotThrow(() -> {
            DatabaseSeeder.seed(connection, java.util.List.of(
                    "/db/seeder/seed_artists.sql",
                    "/db/seeder/seed_genres.sql",
                    "/db/seeder/seed_collections.sql",
                    "/db/seeder/seed_songs.sql",
                    "/db/seeder/seed_collection_songs.sql"
            ));
            System.out.println("[DEBUG_LOG] Successfully loaded all seed files");
        }, "Should be able to load all seed files without throwing an exception");
    }
}
