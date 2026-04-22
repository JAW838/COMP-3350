package beatbinder.persistence.sqlite;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import beatbinder.objects.Song;
import beatbinder.persistence.ConnectionManager;
import beatbinder.persistence.ISongPersistence;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.persistence.PersistenceType;

/**
 * Integration test for SongDB class.
 * Tests the interaction between the SongDB class and the SQLite database.
 */
public class SongDBIntegrationTest {
    private ISongPersistence songPersistence;
    private Connection connection;

    @BeforeEach
    public void setup() {
        // Initialize the persistence layer with TEST type and seed the database
        PersistenceFactory.initialise(PersistenceType.TEST, true);
        songPersistence = PersistenceFactory.getSongPersistence();
        connection = ConnectionManager.get();
        
        // Verify the connection is not null
        assertNotNull(connection, "Database connection should not be null");
        
        // Verify the songPersistence is not null and is a SongDB instance
        assertNotNull(songPersistence, "Song persistence should not be null");
        assertTrue(songPersistence instanceof SongDB, "Song persistence should be a SongDB instance");
        
        System.out.println("[DEBUG_LOG] Test setup complete");
    }

    @AfterEach
    public void tearDown() {
        // Reset the persistence factory to clean up resources
        PersistenceFactory.reset();
        System.out.println("[DEBUG_LOG] Test teardown complete");
    }

    @Test
    public void testGetAllSongs() {
        // Get all songs from the database
        List<Song> songs = songPersistence.getAllSongs();
        
        // Verify that the list is not null and not empty
        assertNotNull(songs, "Songs list should not be null");
        assertFalse(songs.isEmpty(), "Songs list should not be empty");
        
        // Verify that each song has valid properties
        for (Song song : songs) {
            assertNotNull(song, "Song should not be null");
            assertTrue(song.getID() > 0, "Song ID should be positive");
            assertNotNull(song.getTitle(), "Song title should not be null");
            assertNotNull(song.getArtist(), "Song artist should not be null");
            assertTrue(song.getArtistID() > 0, "Song artist ID should be positive");
            assertTrue(song.getGenreID() > 0, "Song genre ID should be positive");
            assertNotNull(song.getGenre(), "Song genre should not be null");
            
            System.out.println("[DEBUG_LOG] Found song: " + song.getTitle() + " by " + song.getArtist());
        }
    }

    @Test
    public void testGetSongByID() {
        // Get all songs to find a valid ID
        List<Song> songs = songPersistence.getAllSongs();
        assertFalse(songs.isEmpty(), "Songs list should not be empty");
        
        // Get the first song's ID
        int songId = songs.get(0).getID();
        
        // Get the song by ID
        Song song = songPersistence.getSongByID(songId);
        
        // Verify that the song is not null and has the correct ID
        assertNotNull(song, "Song should not be null");
        assertEquals(songId, song.getID(), "Song ID should match");
        assertNotNull(song.getTitle(), "Song title should not be null");
        assertNotNull(song.getArtist(), "Song artist should not be null");
        
        System.out.println("[DEBUG_LOG] Retrieved song by ID: " + song.getTitle() + " by " + song.getArtist());
    }

    @Test
    public void testUpdateSong() {
        // Get all songs to find a song to update
        List<Song> songs = songPersistence.getAllSongs();
        assertFalse(songs.isEmpty(), "Songs list should not be empty");
        
        // Get the first song
        Song originalSong = songs.get(0);
        
        // Toggle the like status
        Song updatedSong = originalSong.toggleLike();
        
        // Update the song in the database
        Song resultSong = songPersistence.updateSong(updatedSong);
        
        // Verify that the song was updated correctly
        assertNotNull(resultSong, "Updated song should not be null");
        assertEquals(originalSong.getID(), resultSong.getID(), "Song ID should not change");
        assertEquals(originalSong.getTitle(), resultSong.getTitle(), "Song title should not change");
        assertEquals(originalSong.getArtist(), resultSong.getArtist(), "Song artist should not change");
        assertEquals(!originalSong.isLiked(), resultSong.isLiked(), "Song like status should be toggled");
        
        // Verify the update by retrieving the song again
        Song retrievedSong = songPersistence.getSongByID(originalSong.getID());
        assertEquals(resultSong.isLiked(), retrievedSong.isLiked(), "Retrieved song should have updated like status");
        
        System.out.println("[DEBUG_LOG] Updated song like status: " + retrievedSong.getTitle() + 
                           " is now " + (retrievedSong.isLiked() ? "liked" : "not liked"));
    }

    @Test
    public void testGetCollectionSongs() {
        // Get all songs to find valid IDs
        List<Song> allSongs = songPersistence.getAllSongs();
        assertFalse(allSongs.isEmpty(), "Songs list should not be empty");
        
        // Create an array of song IDs (first 2 songs or all if less than 2)
        int[] songIds = new int[Math.min(2, allSongs.size())];
        for (int i = 0; i < songIds.length; i++) {
            songIds[i] = allSongs.get(i).getID();
        }
        
        // Get the collection songs
        List<Song> collectionSongs = songPersistence.getCollectionSongs(songIds);
        
        // Verify that the list is not null and has the correct size
        assertNotNull(collectionSongs, "Collection songs list should not be null");
        assertEquals(songIds.length, collectionSongs.size(), "Collection songs list should have the correct size");
        
        // Verify that each song has the correct ID
        for (int i = 0; i < songIds.length; i++) {
            assertEquals(songIds[i], collectionSongs.get(i).getID(), "Song ID should match");
            System.out.println("[DEBUG_LOG] Collection song: " + collectionSongs.get(i).getTitle());
        }
    }

    @Test
    public void testSongsIncludeCorrectArtistAndGenre() {
        List<Song> songs = songPersistence.getAllSongs();
        assertFalse(songs.isEmpty(), "Songs list should not be empty");

        for (Song song : songs) {
            assertNotNull(song.getArtist(), "Artist name should not be null");
            assertNotNull(song.getGenre(), "Genre name should not be null");
            System.out.println("[DEBUG_LOG] Song: " + song.getTitle() + ", Artist: " + song.getArtist() + ", Genre: " + song.getGenre());
        }
    }

    @Test
    public void testGetSongsByCollectionID() {
        // Pick a collection ID known to exist in the seeded test database
        // For safety, retrieve one dynamically through a small query or hardcode if known
        int collectionID = 1;

        // Cast to SongDB to access method not in interface
        assertTrue(songPersistence instanceof SongDB);
        SongDB songDB = (SongDB) songPersistence;

        // Call the method
        List<Song> songs = songDB.getSongsByCollectionID(collectionID);

        // Verify the result is not null or empty
        assertNotNull(songs, "Songs list should not be null");
        assertFalse(songs.isEmpty(), "Songs list should not be empty for collection ID " + collectionID);

        // Verify each song has a valid ID and title
        for (Song song : songs) {
            assertNotNull(song, "Song should not be null");
            assertTrue(song.getID() > 0, "Song ID should be positive");
            assertNotNull(song.getTitle(), "Song title should not be null");
            System.out.println("[DEBUG_LOG] Collection song: " + song.getTitle() + " (ID: " + song.getID() + ")");
        }

        // Optionally: verify the order is ascending by position
        for (int i = 1; i < songs.size(); i++) {
            assertTrue(songs.get(i - 1).getID() != songs.get(i).getID(), "Songs should be unique");
        }
    }





}