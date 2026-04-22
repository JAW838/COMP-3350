package beatbinder.persistence;

import java.sql.Connection;
import java.util.List;

import beatbinder.persistence.sqlite.SchemaInitializer;
import beatbinder.persistence.sqlite.SongCollectionDB;
import beatbinder.persistence.sqlite.SongDB;
import beatbinder.persistence.sqlite.TagDB;
import beatbinder.persistence.stub.StubFactory;

public class PersistenceFactory {
    private static ISongPersistence songPersistence;
    private static ICollectionPersistence collectionPersistence;
    private static ITagPersistence tagPersistence;

    public static void initialise(PersistenceType type, boolean seed) {
        // Always reset before initializing to ensure clean state
        reset();

        switch (type) {
            case PROD, TEST -> {
                String dbpath = type == PersistenceType.PROD ? "prod.db" : "test.db";
                try {
                    ConnectionManager.initialize(dbpath);
                    Connection conn = ConnectionManager.get();

                    // Reset the database for tests
                    if (dbpath.equals("test.db"))
                        SchemaInitializer.dropSchema(conn);

                    // Set logging to true if you want to see which tables are created
                    SchemaInitializer.setLoggingEnabled(false);
                    // Make the tables
                    SchemaInitializer.initializeSchema(conn);

                    // Get the sql files and seed the tables with data
                    if (seed) {
                        DatabaseSeeder.seed(conn, List.of(
                                "/db/seeder/seed_artists.sql",
                                "/db/seeder/seed_genres.sql",
                                "/db/seeder/seed_collections.sql",
                                "/db/seeder/seed_songs.sql",
                                "/db/seeder/seed_collection_songs.sql"
                        ));
                    }

                    songPersistence = new SongDB(conn);
                    collectionPersistence = new SongCollectionDB(conn);
                    tagPersistence = new TagDB(conn);
                } catch (Exception e) {
                    // Close connection if initialization failed
                    ConnectionManager.close();
                    System.err.println("Error setting up database: " + e.getMessage());
                    fallbackToStubs();
                }
            }
            case STUB -> fallbackToStubs();
        }
    }

    private static void fallbackToStubs() {
        songPersistence = StubFactory.createSongPersistence();
        collectionPersistence = StubFactory.createCollectionPersistence();
        tagPersistence = StubFactory.createTagPersistence();
    }

    public static ISongPersistence getSongPersistence() {
        if (songPersistence == null) {
            throw new IllegalStateException("PersistenceFactory not initialized");
        }
        return songPersistence;
    }

    public static ICollectionPersistence getCollectionPersistence() {
        if (collectionPersistence == null) {
            throw new IllegalStateException("PersistenceFactory not initialized");
        }
        return collectionPersistence;
    }

    public static ITagPersistence getTagPersistence() {
        if (tagPersistence == null) {
            throw new IllegalStateException("PersistenceFactory not initialized");
        }
        return tagPersistence;
    }

    public static void reset() {
        ConnectionManager.close();
        songPersistence = null;
        collectionPersistence = null;
        tagPersistence = null;
    }
}