package beatbinder.persistence.stub;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import beatbinder.exceptions.CollectionNotFoundException;
import beatbinder.objects.CollType;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.ICollectionPersistence;

public class PlatformCollectionStubTest {
    private ICollectionPersistence collectionPersistence;

    @BeforeEach
    public void setup() {
        collectionPersistence = StubFactory.createCollectionPersistence();
    }

    @Test
    public void testCreateCollectionWorks() {
        SongCollection toAdd = new SongCollection(999, 0, "4", 2024, CollType.ALBUM, false);
        SongCollection result = collectionPersistence.createCollection(toAdd);
        assertEquals(toAdd.getTitle(), result.getTitle());
        assertEquals(toAdd.getYear(), result.getYear());
        assertEquals(toAdd.isLiked(), result.isLiked());
        assertEquals(toAdd.getType(), result.getType());
    }

    @Test
    public void testDeleteCollectionWorks() {
        // Create a new collection for this test with a specific ID
        SongCollection toAdd = new SongCollection(999, 999, "tite", 2023, CollType.ALBUM, false);
        SongCollection added = collectionPersistence.createCollection(toAdd);

        // Now delete it
        SongCollection result = collectionPersistence.deleteCollection(added);
        assertEquals(added.getTitle(), result.getTitle());
        assertEquals(added.getYear(), result.getYear());
        assertEquals(added.isLiked(), result.isLiked());
        assertEquals(added.getType(), result.getType());
    }

    @Test
    public void testUpdateCollectionWorks() {
        SongCollection toAdd = new SongCollection(999, 0, "5", 2024, CollType.ALBUM, false);
        toAdd = collectionPersistence.createCollection(toAdd);
        SongCollection updated = new SongCollection(toAdd.getID(), 1, "updated title", 2023, CollType.ALBUM, true);
        SongCollection result = collectionPersistence.updateCollection(updated);
        assertEquals(updated.getTitle(), result.getTitle());
        assertTrue(result.isLiked());
    }

    @Test
    public void testCollectionNotFoundThrows() {
        SongCollection toAdd = new SongCollection(999, 999, "tite", 2023, CollType.ALBUM, false);
        assertThrows(CollectionNotFoundException.class, () -> {
            collectionPersistence.deleteCollection(toAdd);
        });
    }

    @Test
    public void testGetAllCollectionsIsUnmodifiable() {
        SongCollection toAdd = new SongCollection(0, 0, "title", 2024, CollType.ALBUM, false);
        List<SongCollection> collection = collectionPersistence.getAllCollections();
        assertThrows(UnsupportedOperationException.class, () -> {
            collection.add(toAdd);
        });
    }
}
