package beatbinder.persistence.stub;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
 
import beatbinder.exceptions.CollectionNotFoundException;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.ICollectionPersistence;
import beatbinder.exceptions.DuplicateCollectionException;
import beatbinder.objects.CollType;

public class UserCollectionStubTest {
    private ICollectionPersistence collectionPersistence;

    @BeforeEach
    public void setup() {
        collectionPersistence = StubFactory.createCollectionPersistence();
    }

    @Test
    public void testCreateCollectionValid() {
        SongCollection toAdd = new SongCollection(999, 1, "test", 2023, CollType.ALBUM, false);
        SongCollection onceAdded = collectionPersistence.createCollection(toAdd);

        assertNotEquals(onceAdded, null);
    }

    @Test
    public void testCreateCollectionThrowsWhenDuplicateAdded() {
        SongCollection toAdd = new SongCollection(999, 1, "title", 2023, CollType.ALBUM, false);
        collectionPersistence.createCollection(toAdd);

        assertThrows(DuplicateCollectionException.class, () -> {
            collectionPersistence.createCollection(toAdd);
        });
    }

    @Test
    public void testDeleteCollectionValid() {
        SongCollection toAdd = new SongCollection(999, 1, "2", 2023, CollType.ALBUM, false);
        toAdd = collectionPersistence.createCollection(toAdd);

        SongCollection deleted = collectionPersistence.deleteCollection(toAdd);
        assertEquals(toAdd.getID(), deleted.getID());
    }

    @Test
    public void testDeleteCollectionThrowsWhenNotPresent() {
        SongCollection toAdd = new SongCollection(999, 999, "title", 2023, CollType.ALBUM, false);

        assertThrows(CollectionNotFoundException.class, () -> {
            collectionPersistence.deleteCollection(toAdd);
        });
    }

    @Test
    public void testUpdateCollectionValid() {
        SongCollection toAdd = new SongCollection(-1, 1, "title", 2023, CollType.ALBUM, false);
        toAdd = collectionPersistence.createCollection(toAdd);

        SongCollection updated = new SongCollection(toAdd.getID(), 1, "updated title", 2023, CollType.ALBUM, true);
        SongCollection result = collectionPersistence.updateCollection(updated);
        assertEquals(updated.getTitle(), result.getTitle());
        assertTrue(result.isLiked());
    }

    @Test
    public void testUpdateCollectionThrowsWhenNotPresent() {
        SongCollection toAdd = new SongCollection(999, 999, "title", 2023, CollType.ALBUM, false);
        assertThrows(CollectionNotFoundException.class, () -> {
            collectionPersistence.updateCollection(toAdd);
        });
    }

    @Test
    public void testGetAllCollectionsIsUnmodifiable() {
        SongCollection toAdd = new SongCollection(0, 1, "title", 2023, CollType.ALBUM, false);
        List<SongCollection> collection = collectionPersistence.getAllCollections();

        assertThrows(UnsupportedOperationException.class, () -> {
            collection.add(toAdd);
        });
    }

    @Test
    public void testSetSongPositionInCollection() {
        // set up test
        SongCollection toAdd = new SongCollection(999, 1, "3", 2023, CollType.ALBUM, false);
        toAdd = collectionPersistence.createCollection(toAdd);
        collectionPersistence.addSongToCollection(toAdd.getID(), 1);
        collectionPersistence.addSongToCollection(toAdd.getID(), 0);
        // check that the collection is set up correctly
        collectionPersistence.getSongIdsByCollection(toAdd.getID());
        assertEquals(collectionPersistence.getSongIdsByCollection(toAdd.getID()).get(0), 1);
        assertEquals(collectionPersistence.getSongIdsByCollection(toAdd.getID()).get(1), 0);
        // switch them
        collectionPersistence.setSongPosition(toAdd.getID(), 0, 1);
        assertEquals(collectionPersistence.getSongIdsByCollection(toAdd.getID()).get(0), 0);
        assertEquals(collectionPersistence.getSongIdsByCollection(toAdd.getID()).get(1), 1);
    }
}
