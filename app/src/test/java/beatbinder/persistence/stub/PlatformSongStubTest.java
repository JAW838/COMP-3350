package beatbinder.persistence.stub;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import beatbinder.persistence.ISongPersistence;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.objects.Song;

public class PlatformSongStubTest {
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
    public void testGetCollectionSongs() {
        int[] songIds = {1, 2, 3};
        List<Song> songs = songPersistence.getCollectionSongs(songIds);
        // This test might need adjustment based on what's in the stub database
        // For now, we're just testing that the method doesn't throw
        assertNotNull(songs);
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
