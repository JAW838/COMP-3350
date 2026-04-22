package beatbinder.persistence.stub;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import beatbinder.persistence.ISongPersistence;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.objects.Song;


public class UserSongStubTest {
    private ISongPersistence songPersistence;

    @BeforeEach
    public void setup() {
        songPersistence = StubFactory.createSongPersistence();
    }

    @Test
    public void testGetSongByIDThrowsWhenNotPresent() {
        assertThrows(SongNotFoundException.class, () -> {
            songPersistence.getSongByID(999);
        });
    }

    @Test
    public void testUpdateSongThrowsWhenNotPresent() {
        Song toUpdate = new Song(999, 1, "title", 203, 1, "note", 180);
        assertThrows(SongNotFoundException.class, () -> {
            songPersistence.updateSong(toUpdate);
        });
    }

    @Test
    public void testUpdateSongWorks() {
        // This test assumes there's at least one song in the stub database
        // We'll need to get a song first, then update it
        List<Song> allSongs = songPersistence.getAllSongs();
        if (!allSongs.isEmpty()) {
            Song song = allSongs.get(0);
            Song updated = new Song(
                song.getID(),
                song.getArtistID(),
                "updated title",
                song.getYear(),
                song.getArtist(),
                song.getGenreID(),
                song.getGenre(),
                "updated note",
                song.getRuntime());
            updated = updated.toggleLike();
            Song result = songPersistence.updateSong(updated);
            assertEquals(updated.getTitle(), result.getTitle());
            assertEquals(updated.getNote(), result.getNote());
            assertEquals(!song.isLiked(), result.isLiked());
        }
    }

    @Test
    public void testGetCollectionSongs() {
        // This test assumes there are songs in the stub database
        List<Song> allSongs = songPersistence.getAllSongs();
        if (!allSongs.isEmpty()) {
            int[] songIds = new int[allSongs.size()];
            for (int i = 0; i < allSongs.size(); i++) {
                songIds[i] = allSongs.get(i).getID();
            }
            List<Song> collectionEntries = songPersistence.getCollectionSongs(songIds);
            assertNotNull(collectionEntries);
            assertTrue(collectionEntries.size() <= allSongs.size());
        }
    }

    @Test
    public void testGetAllSongsIsUnmodifiable() {
        List<Song> songs = songPersistence.getAllSongs();
        Song newSong = new Song(0, 1, "title", 2023, 1, "note", 180);
        assertThrows(UnsupportedOperationException.class, () -> {
            songs.add(newSong);
        });
    }
}
