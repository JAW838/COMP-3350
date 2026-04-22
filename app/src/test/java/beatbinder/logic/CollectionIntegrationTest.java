package beatbinder.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import beatbinder.exceptions.SongNotFoundException;
import beatbinder.logic.validation.SongCollectionValidator;
import beatbinder.logic.validation.SongValidator;
import beatbinder.persistence.ICollectionPersistence;
import beatbinder.persistence.ISongPersistence;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.persistence.PersistenceType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import beatbinder.exceptions.DuplicateSongException;
import beatbinder.objects.CollType;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;

//Will implement the testing between CollectionManager and the Database
public class CollectionIntegrationTest {

    private CollectionManager collectionManager;
    private SongManager songManager;
    @BeforeEach
    public void setUp() throws IOException {
        PersistenceFactory.reset();  //
        PersistenceFactory.initialise(PersistenceType.TEST, true); //
        ICollectionPersistence collectionPersistence = PersistenceFactory.getCollectionPersistence();
        ISongPersistence songPersistence = PersistenceFactory.getSongPersistence();
        collectionManager = new CollectionManager(collectionPersistence,new SongValidator(), new SongCollectionValidator());
        songManager = new SongManager(songPersistence, new SongValidator());
    }

    @AfterEach
    public void tearDown() throws IOException {
        PersistenceFactory.reset();
    }

    @Test
    public void testCreateAndRetrievePlaylist() {
        int initialSize = collectionManager.getAllPlaylists().size();

        SongCollection created = collectionManager.createPlaylist("Gym Mix");

        // Retrieve the playlist again from the database
        List<SongCollection> all = collectionManager.getAllPlaylists();
        assertEquals(initialSize + 1, all.size());

        SongCollection retrieved = all.stream()
                .filter(p -> p.getTitle().equals("Gym Mix"))
                .findFirst()
                .orElseThrow();

        assertEquals(CollType.PLAYLIST, retrieved.getType());
        assertEquals("Gym Mix", retrieved.getTitle());
        assertEquals(created.getID(), retrieved.getID());
    }

    @Test
    public void testToggleLikePlaylist() {
        SongCollection playlist = collectionManager.createPlaylist("Study Tunes");

        // Should be liked initially
        assertTrue(playlist.isLiked());

        collectionManager.toggleLikeCollection(playlist);  // toggles to false

        // Refetch updated collection by ID
        SongCollection updated = collectionManager
                .getAllPlaylists()
                .stream()
                .filter(p -> p.getID() == playlist.getID())
                .findFirst()
                .orElseThrow();

        assertFalse(updated.isLiked());

        List<SongCollection> liked = collectionManager.getLikedCollections();
        assertTrue(liked.stream().noneMatch(c -> c.getID() == updated.getID()));
    }


    @Test
    public void testAddAndRemoveSongFromPlaylist() {
        SongCollection playlist = collectionManager.createPlaylist("Chill Tunes");
        Song song = new Song(1, 1, "Test Song", 2025, 1, "test", 200);

        collectionManager.addSongToPlaylist(playlist, song);
        List<Integer> afterAdd = collectionManager.getSongIDsByCollection(playlist);
        assertTrue(afterAdd.contains(song.getID()));
        assertEquals(1, afterAdd.size());

        // Remove the song
        collectionManager.deleteSongFromPlaylist(playlist, song);
        List<Integer> afterRemove = collectionManager.getSongIDsByCollection(playlist);
        assertFalse(afterRemove.contains(song.getID()));
        assertEquals(0, afterRemove.size());
    }

    @Test
    public void testAddDuplicateSongThrowsException() {
        SongCollection playlist = collectionManager.createPlaylist("Throwbacks");
        List<Song> songs = songManager.getAllSongs();

        assertFalse(songs.isEmpty(), "No songs in database, cannot test" );
        Song song = songs.getFirst();

        collectionManager.addSongToPlaylist(playlist, song);
        assertThrows(DuplicateSongException.class, () -> {
            collectionManager.addSongToPlaylist(playlist, song);
        });
    }

    @Test
    public void testDeletePlaylist() {
        int initialSize = collectionManager.getAllPlaylists().size();

        SongCollection playlist = collectionManager.createPlaylist("Chill Vibes");
        assertEquals(initialSize + 1, collectionManager.getAllPlaylists().size());

        collectionManager.deletePlaylist(playlist);

        List<SongCollection> playlists = collectionManager.getAllPlaylists();
        assertEquals(initialSize, playlists.size());
        assertTrue(playlists.stream().noneMatch(p -> p.getID() == playlist.getID()));
    }

    @Test
    public void testUpdatePlaylistTitle() {
        SongCollection playlist = collectionManager.createPlaylist("Workout");
        SongCollection updated = collectionManager.updatePlaylist(playlist, "Workout Updated");

        assertEquals("Workout Updated", updated.getTitle());
        assertEquals(playlist.getID(), updated.getID());
    }

    @Test
    public void testGetAllAlbumsAndLikedAlbums() {
        List<SongCollection> albums = collectionManager.getAllAlbums();
        assertNotNull(albums);

        // Like the first album
        SongCollection album = albums.get(0);
        collectionManager.toggleLikeCollection(album);

        List<SongCollection> likedAlbums = collectionManager.getLikedAlbums();
        assertTrue(likedAlbums.stream().anyMatch(a -> a.getID() == album.getID()));
    }

    @Test
    public void testGetAllCollections() {
        List<SongCollection> all = collectionManager.getAllCollections();
        assertNotNull(all);
        assertTrue(all.size() >= 0);  // basic sanity
    }


    @Test
    public void testGetSongIDsByCollection() {
        SongCollection playlist = collectionManager.createPlaylist("Test Songs");
        List<Song> songs = songManager.getAllSongs();

        assertFalse(songs.isEmpty(), "No songs in database, cannot test" );
        Song song = songs.getFirst();

        collectionManager.addSongToPlaylist(playlist, song);
        List<Integer> ids = collectionManager.getSongIDsByCollection(playlist);

        assertNotNull(ids);
        assertTrue(ids.contains(song.getID()));
    }
    @Test
    public void testSongOrderInPlaylist() {
        SongCollection playlist = collectionManager.createPlaylist("Ordered Mix");
        List<Song> songs = songManager.getAllSongs();

        assertTrue(songs.size() > 1, "Need at least two songs to test");
        Song song1 = songs.get(0);
        Song song2 = songs.get(1);


        collectionManager.addSongToPlaylist(playlist, song1);
        collectionManager.addSongToPlaylist(playlist, song2);

        List<Integer> ids = collectionManager.getSongIDsByCollection(playlist);
        assertEquals(List.of(song1.getID(), song2.getID()), ids); // Order check
    }

    @Test
    public void testDeleteNonExistentSongFromPlaylist() {
        SongCollection playlist = collectionManager.createPlaylist("Test");
        Song nonExistentSong = new Song(500, 1, "Ghost", 2025, 1, "", 180);

        assertThrows(SongNotFoundException.class, () -> {
            collectionManager.deleteSongFromPlaylist(playlist, nonExistentSong);
        });
    }

    @Test
    public void testImmutableSongToggle() {
        Song song = new Song(300, 1, "Immutable", 2025, 1, "", 180);
        Song liked = song.toggleLike();

        assertNotSame(song, liked);
        assertTrue(liked.isLiked());
        assertFalse(song.isLiked());
    }

    @Test // edgy edge case
    public void testAddInvalidSongToPlaylistThrows() {
        SongCollection playlist = collectionManager.createPlaylist("Invalid Song Playlist");
        Song invalidSong = new Song(1, 1, "", 2020, 1, "", -100); // invalid name and duration

        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.addSongToPlaylist(playlist, invalidSong);
        });
    }

    @Test
    public void testCreatePlaylistWithNullTitleThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.createPlaylist(null);
        });
    }

    @Test
    public void testCreatePlaylistWithBlankTitleThrows() {
        // Null should throw
        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.createPlaylist(null);
        });

        // Empty string should throw
        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.createPlaylist("");
        });

        // Whitespaces only should NOT throw, so test that this works:
        assertDoesNotThrow(() -> {
            SongCollection playlist = collectionManager.createPlaylist("   ");
            assertEquals("   ", playlist.getTitle());
        });
    }

    @Test
    public void testAddNullSongToPlaylistThrows() {
        SongCollection playlist = collectionManager.createPlaylist("Null Song Test");

        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.addSongToPlaylist(playlist, null);
        });
    }

    @Test
    public void testDeleteNullSongFromPlaylistThrows() {
        SongCollection playlist = collectionManager.createPlaylist("Null Delete Test");

        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.deleteSongFromPlaylist(playlist, null);
        });
    }

    @Test
    public void testAddSongToNullPlaylistThrows() {
        Song validSong = new Song(900, 1, "Valid Song", 2024, 1, "test", 180);

        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.addSongToPlaylist(null, validSong);
        });
    }

    @Test
    public void testDoubleToggleLikePlaylistRestoresOriginalState() {
        SongCollection playlist = collectionManager.createPlaylist("Double Toggle");

        // Initially liked by default
        boolean originalLiked = playlist.isLiked();

        // Toggle like once
        collectionManager.toggleLikeCollection(playlist);
        SongCollection afterFirstToggle = collectionManager.getAllPlaylists().stream()
                .filter(p -> p.getID() == playlist.getID())
                .findFirst()
                .orElseThrow();
        assertNotEquals(originalLiked, afterFirstToggle.isLiked());

        // Toggle like again
        collectionManager.toggleLikeCollection(afterFirstToggle);
        SongCollection afterSecondToggle = collectionManager.getAllPlaylists().stream()
                .filter(p -> p.getID() == playlist.getID())
                .findFirst()
                .orElseThrow();

        // Should be back to original state
        assertEquals(originalLiked, afterSecondToggle.isLiked());
    }

}
