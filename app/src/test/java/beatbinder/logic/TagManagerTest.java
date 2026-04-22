package beatbinder.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import beatbinder.logic.validation.SongValidator;
import beatbinder.logic.validation.TagValidator;
import beatbinder.objects.Song;
import beatbinder.objects.Tag;
import beatbinder.persistence.ITagPersistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TagManagerTest {
    private TagManager tagManager;
    private ITagPersistence itp;

    @BeforeEach
    public void init() {
        itp = mock(ITagPersistence.class);  
        tagManager = new TagManager(itp, new TagValidator(), new SongValidator());
    }

    @Test
    public void testValidCreateTag() {
        Tag tag1 = new Tag(1,"tag");
        when(itp.createTag(any())).thenReturn(tag1);
        Tag tag = tagManager.createTag("tag"); // calls itp.createTag, provides a new Tag
        assertEquals(tag1.getID(), tag.getID());
        assertEquals("tag", tag.getName());
    }

    @Test
    public void testInvalidCreateTag() {
        // prime itp to return what would be returned
        when(itp.createTag(null)).thenReturn(new Tag(1,null));
        when(itp.createTag(any())).thenReturn(new Tag(""));
        assertThrows(IllegalArgumentException.class,
                () -> tagManager.createTag(null));
        assertThrows(IllegalArgumentException.class,
                () -> tagManager.createTag(""));
        
    }

    @Test
    public void testValidDeleteTag() {
        Tag tag = new Tag(1,"hey");
        when(itp.deleteTag(tag)).thenReturn(tag);
        assertEquals(tag, tagManager.deleteTag(tag));
    }

    @Test
    public void testInvalidDeleteTag() {
        Tag tag = new Tag(1, null);
        when(itp.deleteTag(tag)).thenReturn(tag);
        assertThrows(IllegalArgumentException.class, () -> tagManager.deleteTag(tag));
    }

    @Test
    public void testValidUpdateTag() {
        Tag tag = new Tag(1,"hey");
        String newName = "new";
        when(itp.updateTag(any(Tag.class))).thenReturn(new Tag(tag.getID(), newName));
        Tag newtag = tagManager.updateTag(tag, newName);
        assertEquals(newName, newtag.getName());
        assertEquals(tag.getID(), newtag.getID());
    }

    @Test
    public void testInvalidUpdateTag() {
        Tag tag = new Tag(1,"hey");
        String newName = "";
        when(itp.updateTag(tag)).thenReturn(new Tag(tag.getID(), newName));
        assertThrows(IllegalArgumentException.class, () -> tagManager.updateTag(tag, newName));
    }

    @Test
    public void testValidAddTagToSong() {
        // make fakes
        Tag tag = new Tag(1,"new");
        Song song = new Song(0, 0, "es", 2025, 0, "note", 0);
        // addTagToSong calls getTagsOfSong so prime it to give the right size
        when(itp.getTagsOfSong(song.getID())).thenReturn(new ArrayList<>());
        when(itp.addTagToSong(tag.getID(), song.getID())).thenReturn(new ArrayList<>(List.of(tag)));
        // execute
        List<Tag> output = tagManager.addTagToSong(tag, song);
        // validate
        assertEquals(1, output.size());
        assertEquals(true, output.contains(tag));
    }

    @Test
    public void testInvalidAddTagToSong() {
        Tag tag = new Tag(0, "g");
        Song song = new Song(0, 0, "es", 2025, 0, null, 0);
        when(itp.getTagsOfSong(song.getID())).thenReturn(new ArrayList<>());
        when(itp.addTagToSong(tag.getID(), song.getID())).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> tagManager.addTagToSong(tag, song));
    }

    @Test
    public void testValidRemoveTagFromSong() {
        Tag tag = new Tag(0,"e");
        Song song = new Song(0, 0, "es", 2025, 0, "note", 0);
        when(itp.getTagsOfSong(song.getID())).thenReturn(new ArrayList<>(List.of(tag)));
        when(itp.deleteTagFromSong(tag.getID(), song.getID())).thenReturn(new ArrayList<>());
        List<Tag> tags = tagManager.removeTagFromSong(tag, song);
        assertEquals(tags.size(), 0);
    }

    @Test
    public void testInvalidRemoveTagFromSong() {
        Tag tag = new Tag(0,"d");
        Song song = new Song(0, 0, "es", 2025, 0, null, 0);
        when(itp.getTagsOfSong(song.getID())).thenReturn(new ArrayList<>());
        when(itp.deleteTagFromSong(tag.getID(), song.getID())).thenReturn(new ArrayList<>());
        assertThrows(IllegalArgumentException.class, () -> tagManager.removeTagFromSong(tag, song));
    }

    // nothing that can go wrong here is the fault of TagManager
    // exceptions will be handled by TagValidator which has been tested to death already
    @Test
    public void testGetAllTags() {
        List<Tag> result = new ArrayList<>();
        when(itp.getAllTags()).thenReturn(result);
        assertEquals(result, tagManager.getAllTags());
    }

    /// same as above
    @Test
    public void testGetSongIdsByTag() {
        Tag tag = new Tag(1,"j");

        Song song1 = new Song(1, 0, "title1", 2020, 0, "note", 0);
        Song song2 = new Song(2, 0, "title2", 2021, 0, "note", 0);
        List<Song> songs = List.of(song1, song2);

        List<Integer> result = new ArrayList<>();
        when(itp.getSongIdsByTag(tag.getID())).thenReturn(result);
        assertEquals(result, tagManager.getSongsByTag(songs,tag));
    }

    @Test
    public void testGetTagsOfSong() {
        Song song = new Song(0, 0, "es", 2025, 0, "note", 0);
        Tag tag = new Tag(1,"h");
        List<Tag> tags = new ArrayList<>(List.of(tag));
        when(itp.getTagsOfSong(song.getID())).thenReturn(tags);
        assertEquals(tags, tagManager.getTagsOfSong(song));
    }
}
