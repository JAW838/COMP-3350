package beatbinder.logic;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.List;

import beatbinder.exceptions.DuplicateSongException;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.logic.validation.SongCollectionValidator;
import beatbinder.logic.validation.SongValidator;
import beatbinder.persistence.ICollectionPersistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import beatbinder.objects.CollType;
import beatbinder.objects.SongCollection;
import beatbinder.objects.Song;
import beatbinder.persistence.PersistenceFactory;
import org.mockito.Mock; //big smooth brain type deal

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/*
 * Will be testing all CollectionManager methods. This includes:
 * createPlaylist, deletePlaylist, updatePlaylist, getAllCollections,
 * addSongToPlaylist, deleteSongFromPlaylist
 */
public class CollectionManagerTest {

    @Mock
    private ICollectionPersistence mockCollectionPersistence;
    private CollectionManager collectionManager;
    // Create a CollectionManager before each test
    @BeforeEach
    public void setup() {
        mockCollectionPersistence = mock(ICollectionPersistence.class);
        SongValidator songValidator = new SongValidator();
        SongCollectionValidator collValidator = new SongCollectionValidator();
        collectionManager = new CollectionManager(mockCollectionPersistence, songValidator, collValidator);
    }

    @AfterEach
    public void tearDown() throws IOException {
        PersistenceFactory.reset();
    }

    //testing mock stuff cause real dilly daddle ah type behaviour
    @Test
    public void testGetAllCollections_UsesMockedPersistence() {
        SongCollection mockCollection = new SongCollection(0, 0, "Mock", 0, "f", CollType.PLAYLIST, true);
        when(mockCollectionPersistence.getAllCollections()).thenReturn(List.of(mockCollection));

        List<SongCollection> result = collectionManager.getAllCollections();

        assertEquals(1, result.size());
        assertEquals("Mock", result.get(0).getTitle());
        verify(mockCollectionPersistence).getAllCollections(); // verifies it was actually called
    }


    @Test
    public void testGetAllAlbums_UsesMockedPersistence() {
        SongCollection album1 = new SongCollection(2, 1, "album one", 0, "f", CollType.ALBUM, true);
        SongCollection playlist1 = new SongCollection(3, 2, "Playlist One", 202, "d", CollType.PLAYLIST, false);
        SongCollection album2 = new SongCollection(4, 3, "album two", 2023, "l", CollType.ALBUM, true);

        // Mocking return value of getAllCollections()
        when(mockCollectionPersistence.getAllCollections()).thenReturn(List.of(album1, playlist1, album2));

        // Run the method
        List<SongCollection> albums = collectionManager.getAllAlbums();

        // Assertions
        assertEquals(2, albums.size(), "Should return only album-type collections");
        assertTrue(albums.contains(album1));
        assertTrue(albums.contains(album2));
        assertFalse(albums.contains(playlist1));

        // Verify interaction with mock
        verify(mockCollectionPersistence, times(1)).getAllCollections();
    }

   @Test
    public void testGetAllPlaylists(){
        SongCollection p1 = new SongCollection(3, 1, "p1", 2025, CollType.PLAYLIST, true);
        SongCollection a1 = new SongCollection(4, 2, "a1", 2025, CollType.ALBUM, false);
        when(mockCollectionPersistence.getAllCollections()).thenReturn(List.of(p1, a1));

        List<SongCollection> playListCollection = collectionManager.getAllPlaylists();

        assertEquals(1, playListCollection.size());
        assertEquals(CollType.PLAYLIST, playListCollection.get(0).getType());
   }

   @Test
    public void testGetLikedCollections(){
        SongCollection liked = new SongCollection(5, 1, "Liked", 2025, CollType.PLAYLIST, true);
        SongCollection notLiked = new SongCollection(6, 2, "Not liked", 2025, CollType.PLAYLIST, false);
        when(mockCollectionPersistence.getAllCollections()).thenReturn(List.of(liked, notLiked));

        List<SongCollection> likedOnly = collectionManager.getLikedCollections();

        assertEquals(1, likedOnly.size());
        assertTrue(likedOnly.contains(liked));
        assertFalse(likedOnly.contains(notLiked));
    }

    @Test
    public void testGetLikedAlbumsFromDatabase() {
        List<SongCollection> likedAlbums = collectionManager.getLikedAlbums();
        likedAlbums.add(new SongCollection(-1, 1, "title", 2023, CollType.ALBUM, true));
        assertNotNull(likedAlbums);
        for(SongCollection collection : likedAlbums) {
            assertTrue(collection.isLiked());
            assertEquals(CollType.ALBUM, collection.getType());
        }
    }
    @Test
    public void testGetLikedAlbums() {

        SongCollection liked = new SongCollection(5, 1, "Liked", 2025, CollType.ALBUM, true);
        SongCollection notLiked = new SongCollection(6, 2, "Not Liked", 2025, CollType.ALBUM, false);
        when(mockCollectionPersistence.getAllCollections()).thenReturn(List.of(liked, notLiked));

        List<SongCollection> likedOnly = collectionManager.getLikedAlbums();

        assertEquals(1, likedOnly.size());
        assertTrue(likedOnly.contains(liked));
        assertFalse(likedOnly.contains(notLiked));

    }

    @Test
    public void testGetSongIDsByCollection() {
        SongCollection playlist = new SongCollection(3, 1, "Playlist One", 2022, CollType.PLAYLIST, true);
        when(mockCollectionPersistence.getSongIdsByCollection(playlist.getID()))
                .thenReturn(List.of(1, 2, 3));

        List<Integer> ids = collectionManager.getSongIDsByCollection(playlist);

        assertNotNull(ids, "Returned ID list should not be null");
        assertEquals(List.of(1, 2, 3), ids, "Returned ID list should match expected values");
        verify(mockCollectionPersistence).getSongIdsByCollection(playlist.getID());
    }

    @Test
    public void testCreateValidPlaylist() {
        SongCollection playlist = new SongCollection(5, 0, "Test Playlist", 2025, CollType.PLAYLIST, true);
        when(mockCollectionPersistence.createCollection(any())).thenReturn(playlist);

        SongCollection result = collectionManager.createPlaylist("Test Playlist");
        assertNotNull(result);
        assertEquals("Test Playlist", result.getTitle());
    }

    @Test
    public void testCreateInvalidPlaylist(){
        assertThrows(IllegalArgumentException.class,() -> {
            collectionManager.createPlaylist(null);
        });
    }

    @Test
    public void testDeletePlaylist() {
        SongCollection toDelete = new SongCollection(6, 1, "To Delete", 2025, CollType.PLAYLIST, true);
        when(mockCollectionPersistence.deleteCollection(toDelete)).thenReturn(toDelete);

        SongCollection result = collectionManager.deletePlaylist(toDelete);

        assertEquals(toDelete, result);
        verify(mockCollectionPersistence).deleteCollection(toDelete);
    }

    @Test
    public void testUpdatePlaylist() {
        SongCollection oldPlaylist = new SongCollection(6, 1, "Old", 2025, CollType.PLAYLIST, true);
        SongCollection updated = new SongCollection(6, 1, "New", 2025, CollType.PLAYLIST, true);
        when(mockCollectionPersistence.updateCollection(any())).thenReturn(updated);

        SongCollection result = collectionManager.updatePlaylist(oldPlaylist, "New");
        assertEquals("New", result.getTitle());
    }

     @Test
     public void testUpdatePlaylistWithInvalidTitle() {
         when(mockCollectionPersistence.createCollection(any())).thenReturn(new SongCollection(0, 0, "s", 2025, CollType.PLAYLIST, true));
         SongCollection playlist = collectionManager.createPlaylist("Original Title");
         assertThrows(IllegalArgumentException.class,() -> {collectionManager.updatePlaylist(playlist, "");});
     }

    @Test
    public void testAddDuplicateSongToPlaylist(){
        SongCollection playlist = new SongCollection(6, 1, "Playlist", 2025, CollType.PLAYLIST, true);
        Song song = new Song(6, 1, "Song", 2024, 1, "note", 180);
        when(mockCollectionPersistence.addSongToCollection(playlist.getID(), song.getID())).thenThrow(DuplicateSongException.class);

        assertThrows(DuplicateSongException.class, () -> collectionManager.addSongToPlaylist(playlist, song));
    }

    @Test
    public void testDeleteSongFromPlaylist(){
        SongCollection playlist = new SongCollection(51, 1, "Playlist", 2024, CollType.PLAYLIST, true);
        Song song = new Song(5, 1, "Delete", 2024, 1, "hey", 180);
        when(mockCollectionPersistence.getSongIdsByCollection(playlist.getID())).thenReturn(List.of(song.getID()));
        collectionManager.deleteSongFromPlaylist(playlist, song);
        verify(mockCollectionPersistence).deleteSongFromCollection(playlist.getID(), song.getID());
    }

    @Test
    public void testDeleteNonexistentSongFromPlaylist() {
        SongCollection playlist = new SongCollection(45, 1, "Playlist", 2024, CollType.PLAYLIST, true);
        Song song = new Song(4, 1, "Ghost", 2024, 1, "note", 180);
        doThrow(new IllegalArgumentException()).when(mockCollectionPersistence).deleteSongFromCollection(playlist.getID(), song.getID());

        assertThrows(SongNotFoundException.class, () -> collectionManager.deleteSongFromPlaylist(playlist, song));
    }

    @Test
    public void testToggleLikeCollection() {
        SongCollection playlist = new SongCollection(5, 1, "Toggle", 2024, CollType.PLAYLIST, false);

        collectionManager.toggleLikeCollection(playlist);

        verify(mockCollectionPersistence).updateCollection(argThat(updated ->
                updated.getID() == playlist.getID() &&
                        updated.getTitle().equals(playlist.getTitle()) &&
                        updated.getType() == playlist.getType() &&
                        updated.getYear() == playlist.getYear() &&
                        updated.isLiked() == true // Should now be liked
        ));
    }

    @Test
    public void testAddNullSongToPlaylist() {
        SongCollection playlist = new SongCollection(7, 1, "Playlist", 2024, CollType.PLAYLIST, true);
        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.addSongToPlaylist(playlist, null);
        });
    }

    //updated testToggleLikeOnNullCollection method
    @Test
    public void testToggleLikeOnNullCollection() {
        assertThrows(IllegalArgumentException.class, () -> collectionManager.toggleLikeCollection(null));
    }

     @Test
     public void testUpdatePlaylistWithNullCollection() {
         assertThrows(IllegalArgumentException.class, () -> {
             collectionManager.updatePlaylist(null, "New Title");
         });
     }



    @Test
    public void testGetSongIDsByCollection_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.getSongIDsByCollection(null);
        });
    }
    @Test
    public void testDeleteSongFromPlaylist_SongNotFound() {
        SongCollection playlist = new SongCollection(9, 1, "palylist", 2025, CollType.PLAYLIST, true);
        Song song = new Song(4, 2, "Mising song", 2024, 1, "note", 180);

        when(mockCollectionPersistence.getSongIdsByCollection(playlist.getID())).thenReturn(List.of(1, 3));

        assertThrows(SongNotFoundException.class, () -> {
            collectionManager.deleteSongFromPlaylist(playlist, song);
        });
    }

    // to test
    @Test
    public void testSetSongPosition_ValidInput() {
        // Arrange
        SongCollection playlist = new SongCollection(10, 1, "My Playlist", 2025, CollType.PLAYLIST, true);
        Song song = new Song(7, 1, "My Song", 2024, 1, "note", 180);
        int newPosition = 2;

        // Mock internal state and behavior
        when(mockCollectionPersistence.getSongIdsByCollection(playlist.getID()))
                .thenReturn(List.of(7, 8, 9)); // song ID 7 is present, so songInPlaylist == true

        when(mockCollectionPersistence.setSongPosition(playlist.getID(), song.getID(), newPosition))
                .thenReturn(List.of(8, 7, 9)); // mocked new order


        List<Integer> updatedIDs = collectionManager.setSongPosition(playlist, song, newPosition);

        assertNotNull(updatedIDs);
        assertEquals(List.of(8, 7, 9), updatedIDs);
        verify(mockCollectionPersistence).setSongPosition(playlist.getID(), song.getID(), newPosition);
    }

    @Test
    public void testSetSongPosition_SongNotInPlaylist_ShouldThrowException() {
        SongCollection playlist = new SongCollection(11, 1, "My Playlist", 2025, CollType.PLAYLIST, true);
        Song song = new Song(100, 1, "Missing Song", 2024, 1, "note", 180);
        int newPosition = 1;

        // The mocked song list does NOT include the song ID 100
        when(mockCollectionPersistence.getSongIdsByCollection(playlist.getID()))
                .thenReturn(List.of(1, 2, 3)); // song ID 100 is missing

        assertThrows(SongNotFoundException.class, () -> {
            collectionManager.setSongPosition(playlist, song, newPosition);
        });
    }

    @Test
    public void testSetSongPosition_InvalidPosition_ShouldThrowException() {
        SongCollection playlist = new SongCollection(12, 1, "My Playlist", 2025, CollType.PLAYLIST, true);
        Song song = new Song(7, 1, "Song", 2024, 1, "note", 180);
        int invalidPosition = -1;

        when(mockCollectionPersistence.getSongIdsByCollection(playlist.getID()))
                .thenReturn(List.of(7, 8, 9)); // song is present

        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.setSongPosition(playlist, song, invalidPosition);
        });
    }

    @Test //edgiest edge case
    public void testSetSongPosition_PositionTooLarge_ShouldThrowException() {
        SongCollection playlist = new SongCollection(13, 1, "My Playlist", 2025, CollType.PLAYLIST, true);
        Song song = new Song(7, 1, "Song", 2024, 1, "note", 180);
        int outOfBoundsPosition = 5; // size is 3

        when(mockCollectionPersistence.getSongIdsByCollection(playlist.getID()))
                .thenReturn(List.of(7, 8, 9)); // 3 elements, max index = 2

        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.setSongPosition(playlist, song, outOfBoundsPosition);
        });
    }

    @Test
    public void testSearchCollectionByTitle() {
        SongCollection playlist = new SongCollection(9, 1, "palylist", 2025, CollType.PLAYLIST, true);
        SongCollection test = new SongCollection(10, 1, "test", 2025, CollType.ALBUM, true);
        SongCollection hey = new SongCollection(11, 1, "hey hey people", 2025, CollType.PLAYLIST, true);
        when(mockCollectionPersistence.getAllCollections()).thenReturn(List.of(playlist, test, hey));

        List<SongCollection> searched = collectionManager.searchCollectionByTitle("palylist", CollType.PLAYLIST);
        assertEquals(1, searched.size());
        assertEquals(9, searched.get(0).getID());

        searched = collectionManager.searchCollectionByTitle("e", null);
        assertEquals(2, searched.size());

        searched = collectionManager.searchCollectionByTitle("palylist", CollType.ALBUM);
        assertEquals(0, searched.size());
    }

    @Test
    public void testBadSearchCollectionByTitle() {
        assertThrows(IllegalArgumentException.class, () -> collectionManager.searchCollectionByTitle(null, null));
        assertThrows(IllegalArgumentException.class, () -> collectionManager.searchCollectionByTitle("", CollType.PLAYLIST));
    }

    @Test
    public void testDeletePlaylistWithNullCollection() {
        assertThrows(IllegalArgumentException.class, () -> {
            collectionManager.deletePlaylist(null);
        });
    }

}
