package beatbinder.persistence.sqlite;

import beatbinder.exceptions.DuplicateTagException;
import beatbinder.objects.Tag;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagDBTest {
    private Connection connection;
    private TagDB tagDB;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        tagDB = new TagDB(connection);

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE tags (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE)");
            stmt.executeUpdate("CREATE TABLE song_tags (tag_id INTEGER NOT NULL, song_id INTEGER NOT NULL, PRIMARY KEY(tag_id, song_id), FOREIGN KEY(tag_id) REFERENCES tags(id))");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    @Test
    void testCreateAndGetAllTags() {
        Tag tag1 = tagDB.createTag(new Tag(-1, "Workout"));
        Tag tag2 = tagDB.createTag(new Tag(-1, "Chill"));

        List<Tag> tags = tagDB.getAllTags();

        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(t -> t.getName().equals("Workout")));
        assertTrue(tags.stream().anyMatch(t -> t.getName().equals("Chill")));
    }

    @Test
    void testCreateDuplicateTagThrowsException() {
        tagDB.createTag(new Tag(-1, "Rock"));
        assertThrows(DuplicateTagException.class, () -> tagDB.createTag(new Tag(-1, "Rock")));
    }

    @Test
    void testUpdateTag() {
        Tag original = tagDB.createTag(new Tag(-1, "Old Name"));
        Tag updated = tagDB.updateTag(new Tag(original.getID(), "New Name"));

        assertEquals("New Name", updated.getName());
    }

    @Test
    void testAddAndGetTagsOfSong() {
        Tag tag = tagDB.createTag(new Tag(-1, "Party"));
        List<Tag> tags = tagDB.addTagToSong(tag.getID(), 42);

        assertEquals(1, tags.size());
        assertEquals("Party", tags.get(0).getName());
    }

    @Test
    void testDeleteTagFromSong() {
        Tag tag = tagDB.createTag(new Tag(-1, "Jazz"));
        tagDB.addTagToSong(tag.getID(), 99);
        tagDB.deleteTagFromSong(tag.getID(), 99);

        List<Tag> tags = tagDB.getTagsOfSong(99);
        assertTrue(tags.isEmpty());
    }

    @Test
    void testGetSongIdsByTag() {
        Tag tag = tagDB.createTag(new Tag(-1, "Energetic"));
        tagDB.addTagToSong(tag.getID(), 1);
        tagDB.addTagToSong(tag.getID(), 2);

        List<Integer> songIds = tagDB.getSongIdsByTag(tag.getID());
        assertEquals(2, songIds.size());
        assertTrue(songIds.contains(1));
        assertTrue(songIds.contains(2));
    }

    @Test
    void testGetTagByID() {
        // Create a tag (createTag should insert it and return it with a valid ID)
        Tag created = tagDB.createTag(new Tag(-1, "Focus"));

        // Use that ID to fetch it via getTagByID
        Tag fetched = tagDB.getTagByID(created.getID());

        // Validate it’s not null and matches
        assertNotNull(fetched);
        assertEquals(created.getID(), fetched.getID());
        assertEquals(created.getName(), fetched.getName());
    }

    @Test
    void testDeleteTag() {
        Tag created = tagDB.createTag(new Tag(-1, "Calm"));
        Tag deleted = tagDB.deleteTag(created);

        assertEquals(created.getID(), deleted.getID());
        assertNull(tagDB.getTagByID(created.getID()));
    }

    @Test
    void testGetTagByIDNotFound() {
        assertNull(tagDB.getTagByID(999));  // Arbitrary non-existing ID
    }
}
