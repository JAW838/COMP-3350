package beatbinder.logic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import beatbinder.logic.validation.SongValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import beatbinder.persistence.ISongPersistence;
import beatbinder.objects.Song;


/*
 * Will be testing all SongManager methods. This includes:
 * getSongByID, getAllSongs, getLikedSongs, isSongLiked, toggleLikeSong
 */
public class SongManagerTest {
    private SongManager songManager;
    private ISongPersistence mockSongPersistence;
    private List<Song> mockSongs;
    // Create a SongManager before each test
    @BeforeEach
    public void setup() {
        mockSongPersistence = mock(ISongPersistence.class);

        mockSongs = new ArrayList<>();
        mockSongs.add(new Song(1,1,"Song one",2025,1,"this is song one",200));
        mockSongs.add(new Song(2,1,"Song two",2025,1,"this is song two",200).toggleLike());
        mockSongs.add(new Song(3,1,"Song three",2025,1,"this is song three",200));
        when(mockSongPersistence.getAllSongs()).thenAnswer(invocation -> new ArrayList<>(mockSongs));
        when(mockSongPersistence.getSongByID(1)).thenAnswer(invocation -> mockSongs.get(0));
        when(mockSongPersistence.getSongByID(2)).thenAnswer(invocation -> mockSongs.get(1));
        when(mockSongPersistence.getSongByID(3)).thenAnswer(invocation -> mockSongs.get(2));
        when(mockSongPersistence.getSongByID(-1)).thenThrow(new IllegalArgumentException("Song ID cannot be negative"));
        when(mockSongPersistence.getSongByID(9999)).thenThrow(new RuntimeException("Song with ID 9999 not found"));

        songManager = new SongManager(mockSongPersistence, new SongValidator());
    }

    @Test
    public void testGetAllSongs() {
        List<Song> allSongs = songManager.getAllSongs();

        assertNotNull(allSongs);
        assertEquals(3, allSongs.size());
    }

    @Test
    public void testGetLikedSongs() {
        List<Song> likedSongs = songManager.getLikedSongs();
        assertNotNull(likedSongs);
        assertEquals(1, likedSongs.size());
        assertTrue(likedSongs.get(0).isLiked());
    }

    @Test
    public void testIsSongLiked() {
        assertTrue(songManager.isSongLiked(2));
        assertFalse(songManager.isSongLiked(1));

    }

    @Test
    public void  testToggleLikeSongFromUnlikedToLiked(){
        Song song = mockSongs.get(0); // initially unliked
        assertFalse(song.isLiked());

        Song toggled = song.toggleLike(); // simulate immutability
        when(mockSongPersistence.updateSong(any())).thenReturn(toggled);

        boolean result = songManager.toggleLikeSong(song);

        assertTrue(result); // new song should now be liked
        verify(mockSongPersistence).updateSong(argThat(updated ->
                updated.getID() == song.getID() && updated.isLiked()
        ));

    }

    @Test
    public void testToggleLikeSongFromLikedToUnliked(){
        Song song = mockSongs.get(1); // initially liked
        assertTrue(song.isLiked());

        Song toggled = song.toggleLike(); // simulate immutability
        when(mockSongPersistence.updateSong(any())).thenReturn(toggled);

        boolean result = songManager.toggleLikeSong(song);

        assertFalse(result); // new song should now be unliked
        verify(mockSongPersistence).updateSong(argThat(updated ->
                updated.getID() == song.getID() && !updated.isLiked()
        ));
    }

    @Test
    public void testGetSongByIDWithNegativeID() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            songManager.getSongByID(-1);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("id"), "Should throw exception for negative ID");
    }

    @Test
    public void testGetSongByIDWithNonExistentID() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            songManager.getSongByID(9999);
        });
        assertTrue(exception.getMessage().contains("Song with ID"));
    }

    @Test
    public void testIsSongLikedWithNonExistentID() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            songManager.isSongLiked(9999);
        });
        assertTrue(exception.getMessage().contains("Song with ID"));
    }

    @Test
    public void testToggleLikeSongWithNullSong() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            songManager.toggleLikeSong(null);
        });
        assertTrue(exception.getMessage().contains("Song cannot be null"), "Should throw exception for null song");
    }

    @Test
    public void testGetSongByID() {
        Song expectedSong = mockSongs.get(0);
        Song retrievedSong = songManager.getSongByID(expectedSong.getID());

        assertNotNull(retrievedSong);
        assertEquals(expectedSong.getID(), retrievedSong.getID());
        assertEquals(expectedSong.getTitle(), retrievedSong.getTitle());
    }

    @Test
    public void testGetLikedSongsWhenNoneLiked() {
        List<Song> unlikedSongs = new ArrayList<>();
        for (Song song : mockSongs) {
            if (song.isLiked()) {
                song = song.toggleLike();
            }
            unlikedSongs.add(song);
        }
        when(mockSongPersistence.getAllSongs()).thenReturn(unlikedSongs);

        List<Song> likedSongs = songManager.getLikedSongs();
        assertTrue(likedSongs.isEmpty());
    }

    @Test
    public void testToggleLikeSongFailsValidation() {
        Song invalidSong = mock(Song.class);
        when(invalidSong.getID()).thenReturn(42);  // Some dummy ID
        doThrow(new IllegalArgumentException("Invalid song")).when(invalidSong).isLiked();

        assertThrows(IllegalArgumentException.class, () -> {
            songManager.toggleLikeSong(invalidSong);
        });

        verify(mockSongPersistence, never()).updateSong(any());
    }

    @Test
    public void testToggleLikeSongReturnsCorrectValue() {
        Song song = mockSongs.get(0); // initially unliked
        boolean wasLiked = song.isLiked();

        Song toggled = song.toggleLike(); // simulate immutability
        when(mockSongPersistence.updateSong(any())).thenReturn(toggled);

        boolean result = songManager.toggleLikeSong(song);

        assertEquals(!wasLiked, result, "Return value should reflect new like status");
    }

    @Test
    public void testGetAllSongsReturnsCopyNotReference() {
        List<Song> allSongs = songManager.getAllSongs();
        allSongs.clear(); // Modify the returned list

        // Ensure internal list in mock is unaffected
        assertEquals(3, songManager.getAllSongs().size(), "Internal song list should be unaffected");
    }

    @Test
    public void testGetLikedSongsWhenNoSongsExist() { //edgiest of edge cases
        when(mockSongPersistence.getAllSongs()).thenReturn(new ArrayList<>());

        List<Song> likedSongs = songManager.getLikedSongs();
        assertNotNull(likedSongs);
        assertTrue(likedSongs.isEmpty(), "Liked songs should be empty when no songs exist");
    }

    @Test
    public void testUpdateNoteSuccessfullyUpdatesNote() {
        Song original = mockSongs.get(0);
        String newNote = "Updated note";
        Song updated = original.updateNote(newNote);

        when(mockSongPersistence.updateSong(any())).thenReturn(updated);

        Song result = songManager.updateNote(original, newNote);

        assertEquals(newNote, result.getNote());
        assertEquals(original.getID(), result.getID());
    }

    @Test
    public void testSearchSongByTitle() {
        List<Song> searched = songManager.searchSongByTitle(mockSongs,"Song one");
        assertEquals("Song one", searched.get(0).getTitle());
        assertEquals(1, searched.size());

        searched = songManager.searchSongByTitle(mockSongs,"Song");
        assertEquals(3, searched.size());

        searched = songManager.searchSongByTitle(mockSongs,"g on");
        assertEquals("Song one", searched.get(0).getTitle());
        assertEquals(1, searched.size());

        searched = songManager.searchSongByTitle(mockSongs,"G TW");
        assertEquals("Song two", searched.get(0).getTitle());
        assertEquals(1, searched.size());
    }

    @Test
    public void testBadSearchSongByTitle() {
        assertThrows(IllegalArgumentException.class, () -> songManager.searchSongByTitle(mockSongs,null));
        assertThrows(IllegalArgumentException.class, () -> songManager.searchSongByTitle(mockSongs,""));
    }

    @Test
    public void getSongsByArtistOneReturnsThreeSongs() {
        List<Song> songs = songManager.getAllSongsByArtist(1);
        assertEquals(3, songs.size());

        assertEquals("Song one", songs.get(0).getTitle());
        assertEquals("Song two", songs.get(1).getTitle());
        assertEquals("Song three", songs.get(2).getTitle());
    }

    @Test
    public void getSongsByArtistTwoReturnsEmptyList() {
        List<Song> songs = songManager.getAllSongsByArtist(2);
        assertEquals(0, songs.size());
    }

}
