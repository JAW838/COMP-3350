package beatbinder.persistence.sqlite;

import beatbinder.objects.CollType;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.ConnectionManager;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.persistence.PersistenceType;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class SongCollectionDBIntegrationTest {

    private Connection connection;
    private SongCollectionDB songCollectionDB;
    private SongDB songDB;


    @BeforeEach
    public void setUp() throws SQLException {
        // Initialize with TEST type and seed the database
        PersistenceFactory.initialise(PersistenceType.TEST, true);
        
        // Get persistence instances through factory
        songCollectionDB = (SongCollectionDB) PersistenceFactory.getCollectionPersistence();
        songDB = (SongDB) PersistenceFactory.getSongPersistence();
        connection = ConnectionManager.get();

        // Verify initialization
        assertNotNull(connection, "Database connection should not be null");
        assertNotNull(songCollectionDB, "SongCollection persistence should not be null");
        assertNotNull(songDB, "Song persistence should not be null");
        
        System.out.println("[DEBUG_LOG] SongCollection test setup complete");
    }

    @AfterEach
    public void tearDown() throws SQLException {
        System.out.println("[DEBUG_LOG] SongCollection test teardown starting");
        PersistenceFactory.reset();
        System.out.println("[DEBUG_LOG] SongCollection test teardown complete");
    }


    @Test
    public void testCreateCollection() {
        SongCollection input = new SongCollection(-1, 1, "Test Title", 2020, "Test Artist", CollType.ALBUM, true);
        SongCollection created = songCollectionDB.createCollection(input);

        assertNotEquals(-1, created.getID());
        assertEquals("Test Title", created.getTitle());
        assertEquals(CollType.ALBUM, created.getType());
        assertTrue(created.isLiked());
    }

    @Test
    public void testDeleteCollection() {
        SongCollection created = songCollectionDB.createCollection(
                new SongCollection(-1, 1, "To Delete", 2021, "X", CollType.PLAYLIST, false)
        );

        SongCollection deleted = songCollectionDB.deleteCollection(created);
        assertEquals(created.getID(), deleted.getID());

        assertThrows(RuntimeException.class, () -> songCollectionDB.deleteCollection(created));
    }

    @Test
    public void testUpdateCollection() {
        SongCollection created = songCollectionDB.createCollection(
                new SongCollection(-1, 2, "Initial Title", 2018, "Artist", CollType.ALBUM, false)
        );

        SongCollection updated = new SongCollection(
                created.getID(), 2, "Updated Title", 2018, "Artist", CollType.ALBUM, true
        );

        SongCollection result = songCollectionDB.updateCollection(updated);

        assertEquals("Updated Title", result.getTitle());
        assertTrue(result.isLiked());
    }

    @Test
    public void testAddAndGetSongIds() {
        SongCollection collection = songCollectionDB.createCollection(
                new SongCollection(-1, 3, "Mix", 2022, "DJ", CollType.PLAYLIST, false)
        );

        List<Song> allSongs = songDB.getAllSongs();
        assertTrue(allSongs.size() > 1);
        Song song1 = allSongs.get(0);
        Song song2 = allSongs.get(1);

        List<Integer> songIds = songCollectionDB.addSongToCollection(collection.getID(), song1.getID());
        assertEquals(List.of(song1.getID()), songIds);

        songIds = songCollectionDB.addSongToCollection(collection.getID(), song2.getID());
        assertEquals(List.of(song1.getID(), song2.getID()), songIds);
    }

    @Test
    public void testDeleteSongFromCollection() {
        SongCollection collection = songCollectionDB.createCollection(
                new SongCollection(-1, 3, "Mix", 2022, "DJ", CollType.PLAYLIST, false)
        );

        List<Song> allSongs = songDB.getAllSongs();
        assertTrue(allSongs.size() > 1);
        Song song1 = allSongs.get(0);
        Song song2 = allSongs.get(1);

        songCollectionDB.addSongToCollection(collection.getID(), song1.getID());
        songCollectionDB.addSongToCollection(collection.getID(), song2.getID());

        List<Integer> remaining = songCollectionDB.deleteSongFromCollection(collection.getID(), song1.getID());
        assertEquals(List.of(song2.getID()), remaining);
    }

    @Test
    public void testSetSongPosition() {
        SongCollection collection = songCollectionDB.createCollection(
                new SongCollection(-1, 4, "Party", 2023, "Band", CollType.PLAYLIST, false)
        );

        songCollectionDB.addSongToCollection(collection.getID(), 1);
        songCollectionDB.addSongToCollection(collection.getID(), 2);
        songCollectionDB.addSongToCollection(collection.getID(), 3);

        List<Integer> result = songCollectionDB.setSongPosition(collection.getID(), 3, 0);
        assertEquals(List.of(3, 1, 2), result);
    }

    @Test
    public void testGetAllCollections() throws SQLException {
        // Get initial state of collections
        List<SongCollection> initialCollections = songCollectionDB.getAllCollections();
        int initialSize = initialCollections.size();

        // Create new collections
        SongCollection coll1 = new SongCollection(-1, 1, "Test Album A", 2000, "Test Artist", CollType.ALBUM, false);
        SongCollection coll2 = new SongCollection(-1, 1, "Test Playlist B", 2022, "Test Artist", CollType.PLAYLIST, true);

        songCollectionDB.createCollection(coll1);
        songCollectionDB.createCollection(coll2);

        // Get updated list of collections
        List<SongCollection> result = songCollectionDB.getAllCollections();

        // Verify results
        assertEquals(initialSize + 2, result.size(), "Should have 2 more collections than initial state");
        assertTrue(result.stream().anyMatch(c -> c.getTitle().equals("Test Album A")), "Should contain Test Album A");
        assertTrue(result.stream().anyMatch(c -> c.getTitle().equals("Test Playlist B")), "Should contain Test Playlist B");


    }

    @Test
    void testGetSongIdsByArtist() {
        // Get an existing artist ID from the seeded database
        int artistId;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM artists LIMIT 1")) {
            assertTrue(rs.next(), "Seeded database should contain at least one artist");
            artistId = rs.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get test artist", e);
        }

        // Get initial count of songs for this artist
        List<Integer> initialSongIds = songCollectionDB.getSongIdsByArtist(artistId);
        int initialCount = initialSongIds.size();
        
        // Create a new collection for this artist
        SongCollection collection = songCollectionDB.createCollection(
            new SongCollection(-1, artistId, "Test Artist Collection", 2022, "Test Artist", CollType.PLAYLIST, false)
        );

        // Get songs after creating collection
        List<Integer> result = songCollectionDB.getSongIdsByArtist(artistId);
        
        // Verify results
        assertNotNull(result, "Result should not be null");
        assertEquals(initialCount, result.size(), 
            "Should return same number of songs as before (collections don't affect artist's songs)");
        assertTrue(result.containsAll(initialSongIds), 
            "Should contain all original songs");
        
        // Verify each song ID is valid
        for (Integer songId : result) {
            assertTrue(songId > 0, "Song ID should be positive");
        }
    }

    @Test
    void testGetSongIdsByNonExistentArtist() {
        List<Integer> result = songCollectionDB.getSongIdsByArtist(-1);
        assertTrue(result.isEmpty(), "Should return empty list for non-existent artist");
    }


    @Test
    void testGetSongIdsByGenre() {
        // Get an existing genre ID from the seeded database
        int genreId;
        String genreName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM genres LIMIT 1")) {
            assertTrue(rs.next(), "Seeded database should contain at least one genre");
            genreId = rs.getInt("id");
            genreName = rs.getString("name");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get test genre", e);
        }

        // Get initial songs for this genre
        List<Integer> initialSongIds = songCollectionDB.getSongIdsByGenre(genreId);
        int initialCount = initialSongIds.size();
        assertTrue(initialCount > 0, 
            "Seeded database should contain songs for genre: " + genreName);

        // Get a different genre to verify cross-genre isolation
        int otherGenreId;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM genres WHERE id != " + genreId + " LIMIT 1")) {
            assertTrue(rs.next(), "Seeded database should contain at least two genres");
            otherGenreId = rs.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get other genre", e);
        }

        // Get songs for the other genre
        List<Integer> otherGenreSongs = songCollectionDB.getSongIdsByGenre(otherGenreId);

        // Verify results for main genre
        List<Integer> result = songCollectionDB.getSongIdsByGenre(genreId);
        
        assertAll(
            () -> assertEquals(initialCount, result.size(), 
                "Should return same number of songs as initial count"),
            () -> assertTrue(result.containsAll(initialSongIds), 
                "Should contain all original songs"),
            () -> assertTrue(Collections.disjoint(result, otherGenreSongs), 
                "Songs from different genres should not overlap"),
            () -> result.forEach(songId -> 
                assertTrue(songId > 0, "Each song ID should be positive"))
        );
    }

    @Test
    void testGetSongIdsByNonExistentGenre() {
        List<Integer> result = songCollectionDB.getSongIdsByGenre(-1);
        assertTrue(result.isEmpty(), "Should return empty list for non-existent genre");
    }


}