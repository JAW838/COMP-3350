package beatbinder.persistence.stub;

import beatbinder.exceptions.TagNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import beatbinder.objects.Tag;
import beatbinder.persistence.ITagPersistence;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.persistence.PersistenceType;
import static org.junit.jupiter.api.Assertions.*;

public class TagStubTest {
    ITagPersistence tagPersistence;

    @BeforeEach
    public void setup() {
        PersistenceFactory.initialise(PersistenceType.STUB, true);
        tagPersistence = PersistenceFactory.getTagPersistence();
    }

    @Test
    public void testCreateTag() {
        Tag tag = new Tag("Jazz");
        Tag created = tagPersistence.createTag(tag);

        assertNotEquals(-1, created.getID());
        assertEquals("Jazz", created.getName());
    }

    @Test
    public void testDeleteTag() {
        Tag tag = tagPersistence.createTag(new Tag("HipHop"));
        Tag deleted = tagPersistence.deleteTag(tag);

        assertEquals(tag, deleted);
        assertThrows(TagNotFoundException.class, () -> tagPersistence.getTagByID(tag.getID()));
    }

    @Test
    public void testUpdateTag() {
        // create tag
        Tag toAdd = tagPersistence.createTag(new Tag("Jazzy"));
        Tag updated = tagPersistence.updateTag(toAdd);
        assertEquals(toAdd.getID(), updated.getID());
    }

    @Test
    public void testGetAllTags() {
        Tag tag1 = tagPersistence.createTag(new Tag("Tag1"));
        Tag tag2 = tagPersistence.createTag(new Tag("Tag2"));

        List<Tag> all = tagPersistence.getAllTags();
        assertTrue(all.contains(tag1));
        assertTrue(all.contains(tag2));
    }

    @Test
    public void testGetTagById() {
        Tag tag = tagPersistence.createTag(new Tag("Funky"));
        Tag fetched = tagPersistence.getTagByID(tag.getID());

        assertEquals(tag, fetched);
    }

    @Test
    public void testAddTagToSong() {
        Tag tag1 = tagPersistence.createTag(new Tag("Groovvy"));
        assertTrue(tagPersistence.addTagToSong(tag1.getID(), 0).contains(tag1));
    }

    @Test
    public void testDeleteTagFromSong() {
        Tag tag = tagPersistence.createTag(new Tag("Ambient"));
        tagPersistence.addTagToSong(tag.getID(), 5);

        List<Tag> afterDelete = tagPersistence.deleteTagFromSong(tag.getID(), 5);
        assertFalse(afterDelete.contains(tag));
    }

    @Test
    public void testGetTagsOfSong() {
        Tag tag1 = tagPersistence.createTag(new Tag("Pop"));
        Tag tag2 = tagPersistence.createTag(new Tag("Dance"));

        tagPersistence.addTagToSong(tag1.getID(), 10);
        tagPersistence.addTagToSong(tag2.getID(), 10);

        List<Tag> songTags = tagPersistence.getTagsOfSong(10);
        assertTrue(songTags.contains(tag1));
        assertTrue(songTags.contains(tag2));
    }

    @Test
    public void testGetSongIdsByTag() {
        Tag tag = tagPersistence.createTag(new Tag("Indie"));

        tagPersistence.addTagToSong(tag.getID(), 1);
        tagPersistence.addTagToSong(tag.getID(), 2);

        List<Integer> songIds = tagPersistence.getSongIdsByTag(tag.getID());
        assertTrue(songIds.contains(1));
        assertTrue(songIds.contains(2));
    }

    @Test
    public void testCreateDuplicateTagNameThrows() {
        tagPersistence.createTag(new Tag("Rock"));
        assertThrows(RuntimeException.class, () -> {
            tagPersistence.createTag(new Tag("Rock"));
        });
    }

    @Test
    public void testDeleteNonExistentTagThrows() {
        Tag nonexistent = new Tag(999, "Imaginary");
        assertThrows(TagNotFoundException.class, () -> tagPersistence.deleteTag(nonexistent));
    }

    @Test
    public void testGetTagByInvalidIDThrows() {
        assertThrows(TagNotFoundException.class, () -> tagPersistence.getTagByID(-42));
    }

    @Test
    public void testAddDuplicateTagToSongThrows() {
        Tag tag = tagPersistence.createTag(new Tag("Lofi"));
        tagPersistence.addTagToSong(tag.getID(), 1);
        assertThrows(RuntimeException.class, () -> tagPersistence.addTagToSong(tag.getID(), 1));
    }

    @Test
    public void testDeleteTagFromSongWhenNotPresentThrows() {
        Tag tag = tagPersistence.createTag(new Tag("EDM"));
        assertThrows(TagNotFoundException.class, () -> {
            tagPersistence.deleteTagFromSong(tag.getID(), 999);
        });
    }

    @Test
    public void testGetAllTagsCount() {
        int initialSize = tagPersistence.getAllTags().size();
        tagPersistence.createTag(new Tag("TagA"));
        tagPersistence.createTag(new Tag("TagB"));

        assertEquals(initialSize + 2, tagPersistence.getAllTags().size());
    }

    @Test
    public void testUpdateTagNameChange() {
        Tag original = tagPersistence.createTag(new Tag("OldName"));
        Tag updatedTag = new Tag(original.getID(), "NewName"); // create new Tag with same ID but new name

        Tag updated = tagPersistence.updateTag(updatedTag);

        assertEquals(original.getID(), updated.getID());
        assertEquals("NewName", updated.getName());
    }

}
