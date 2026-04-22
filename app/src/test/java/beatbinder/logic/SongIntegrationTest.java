package beatbinder.logic;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import beatbinder.logic.validation.SongValidator;
import beatbinder.objects.Song;
import beatbinder.persistence.ISongPersistence;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.persistence.PersistenceType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class SongIntegrationTest {
    private SongManager songManager;
    private ISongPersistence songPersistence;
    @BeforeEach
    public void setUp() throws IOException {
        PersistenceFactory.reset();
        PersistenceFactory.initialise(PersistenceType.TEST, true); // No seed data
        songPersistence = PersistenceFactory.getSongPersistence();
        songManager = new SongManager(songPersistence, new SongValidator());
    }

    @AfterEach
    public void tearDown() throws IOException {
        PersistenceFactory.reset();
    }

    @Test
    public void testGetAllSongs() {
        List<Song> songs = songManager.getAllSongs();
        assertNotNull(songs);
        assertTrue(songs.size() > 0);
    }
    @Test
    public void testToggleLikeSong() {
        Song song = songManager.getAllSongs().get(1);


        boolean liked = songManager.toggleLikeSong(song);
        assertTrue(liked, "Song should be liked after first toggle");


        Song updatedSong = songManager.getSongByID(song.getID());


        boolean unliked = songManager.toggleLikeSong(updatedSong);
        assertFalse(unliked, "Song should be unliked after second toggle");
    }

    @Test
    public void testGetLikedSongs() {
        List<Song> allSongs = songManager.getAllSongs();
        assertFalse(allSongs.isEmpty());

        // Find a song that is not liked
        Song target = allSongs.stream().filter(s -> !s.isLiked()).findFirst().orElse(null);
        assertNotNull(target, "Expected at least one unliked song");

        // Like the song
        songManager.toggleLikeSong(target);

        List<Song> likedSongs = songManager.getLikedSongs();
        assertTrue(likedSongs.stream().anyMatch(s -> s.getID() == target.getID()));
    }

    @Test
    public void testGetLikedSongs_EmptyWhenNoLikes() {
        // Unlike all songs to ensure no liked songs
        List<Song> allSongs = songManager.getAllSongs();
        for (Song s : allSongs) {
            if (s.isLiked()) {
                songManager.toggleLikeSong(s);
            }
        }
        List<Song> likedSongs = songManager.getLikedSongs();
        assertNotNull(likedSongs);
        assertTrue(likedSongs.isEmpty(), "Liked songs list should be empty if no songs are liked");
    }

    @Test
    public void testGetSongByID_Valid() {
        Song existing = songManager.getAllSongs().get(0);
        Song found = songManager.getSongByID(existing.getID());
        assertEquals(existing.getID(), found.getID());
    }

    @Test
    public void testGetSongByID_Invalid() {
        assertThrows(IllegalArgumentException.class, () -> songManager.getSongByID(-1));
    }

    @Test
    public void testIsSongLiked() {
        Song song = songManager.getAllSongs().get(0);
        boolean initiallyLiked = song.isLiked();

        boolean result = songManager.isSongLiked(song.getID());
        assertEquals(initiallyLiked, result);
    }

    @Test
    public void testIsSongLiked_InvalidIDThrows() {
        assertThrows(IllegalArgumentException.class, () -> songManager.isSongLiked(-100));
    }

    @Test
    public void testToggleLikeSong_NullSongThrows() {
        assertThrows(IllegalArgumentException.class, () -> songManager.toggleLikeSong(null));
    }

    @Test
    public void testUpdateNote() {
        Song song = songManager.getAllSongs().get(0);
        String newNote = "This is a new note";

        Song updatedSong = songManager.updateNote(song, newNote);
        assertEquals(newNote, updatedSong.getNote(), "Song note should be updated");
    }



}
