package beatbinder.logic;

import java.util.ArrayList;
import java.util.List;

import beatbinder.exceptions.DuplicateTagException;
import beatbinder.exceptions.TagNotFoundException;
import beatbinder.logic.validation.SongValidator;
import beatbinder.logic.validation.TagValidator;
import beatbinder.objects.Song;
import beatbinder.objects.Tag;

import beatbinder.persistence.ITagPersistence;

/**
 * Handles updates to {@link Tag} instances, including creating, reading, updating, and deleting.
 * Communicates with the database via {@link ITagPersistence}, acting as an intermediary between
 * presentation and persistence.
 * 
 * Validates {@link Song} instances with {@link SongValidator} and {@link Tag} instances with
 * {@link TagValidator}, ensuring complete data is passed between the layers.
 */
public class TagManager {
    /**
     * The interface used to communicate with the database about incoming and outgoing {@link Tag}
     * instances.
     */
    private ITagPersistence tagPersistence;
    /**
     * The validator used to validate {@link Tag} instances and their data.
     */
    private TagValidator tagValidator;
    /**
     * The validator used to validate {@link Song} instances and their data.
     */
    private SongValidator songValidator;



    //------------------------------------------------------------------------------------------------//



    /**
     * Create a {@link TagManager} to handle communication between presentation and persistence
     * regarding {@link Tag} instances.
     * 
     * The manager uses {@link SongValidator} to validate {@link Song} instances and
     * {@link TagValidator} to validate {@link Tag} instances.
     * 
     * @param tagPersistence The persistence interface used to manage tags.
     * @param tagValidator The validator used to validate tags.
     * @param songValidator The validator used to validate song data.
     */
    public TagManager(ITagPersistence tagPersistence, TagValidator tagValidator,
            SongValidator songValidator) {
        this.tagPersistence = tagPersistence;
        this.tagValidator = tagValidator;
        this.songValidator = songValidator;
    }



    //------------------------------------------------------------------------------------------------//



    /**
     * Creates a new {@link Tag} and stores it in the database.
     * 
     * {@code tagName} is validated to be non-{@code null}. The resulting {@link Tag} is also validated
     * before being returned.
     * 
     * @param tagName the name of the tag to create
     * @return the created and validated {@link Tag}
     * @throws IllegalArgumentException if {@code tagName} or the new {@link Tag} fails validation
     * @throws DuplicateTagException if a tag with the same name already exists
     */
    public Tag createTag(String tagName) {
        tagValidator.validateName(tagName);
        Tag out = tagPersistence.createTag(new Tag(tagName));
        tagValidator.validateTag(out);

        return out;
    }



    /**
     * Deletes the specified {@link Tag} from the database.
     * 
     * {@code tag} is validated before being deleted. The deleted tag is validated before returning.
     * 
     * @param tag the tag to be deleted.
     * @return the deleted and validated {@link Tag}.
     * @throws IllegalArgumentException if {@code tag} or the deleted {@link Tag} fail validation.
     * @throws TagNotFoundException if {@code tag} is not found in the database.
     */
    public Tag deleteTag(Tag tag) {
        tagValidator.validateTag(tag);
        Tag out = tagPersistence.deleteTag(tag);
        tagValidator.validateTag(out);

        return out;
    }



    /**
     * Updates a given {@link Tag} with a new {@code name}.
     * 
     * A new {@link Tag} is constructed using the original tag's ID and the provided {@code name}.
     * It is validated both before and after being saved to ensure integrity.
     * 
     * @param tag the tag to update.
     * @param name the new name to give the tag.
     * @return the updated and verified {@link Tag}
     * @throws IllegalArgumentException if  the updated {@link Tag} fails validation.
     * @throws TagNotFoundException if {@code tag} is not found in the database.
     */
    public Tag updateTag(Tag tag, String name) {
        // create and validate new tag
        Tag newTag = new Tag(tag.getID(), name);
        tagValidator.validateTag(newTag);
        // update tag
        Tag out = tagPersistence.updateTag(new Tag(tag.getID(), name));
        // validate tag returned from db
        tagValidator.validateTag(out);

        return out;
    }



    /**
     * Adds a given {@link Tag} to a {@link Song} and retrieves all tags attached to that song.
     * The returned list is unordered.
     * 
     * Both {@code tag} and {@code song} are validated before use. Each returned {@link Tag}
     * is also validated.
     * 
     * @param tag the {@link Tag} to be added to the song.
     * @param song the {@link Song} to add the tag to.
     * @return a list of {@link Tag} instances currently attached to {@code song}.
     * @throws IllegalArgumentException if {@code tag}, {@code song}, or any returned {@link Tag}
     * fail validation, or if the number of tags returned is inconsistent.
     * @throws TagNotFoundException if {@code tag} is not found in the database.
     */
    public List<Tag> addTagToSong(Tag tag, Song song) {
        // validate input
        tagValidator.validateTag(tag);
        songValidator.validateSong(song);

        // find the number of tags currently on the song
        int size = tagPersistence.getTagsOfSong(song.getID()).size();
        // add the tag to the song
        List<Tag> out = tagPersistence.addTagToSong(tag.getID(), song.getID());
        
        // check if the tag was added properly
        if (out.size() != size + 1) {
            throw new IllegalArgumentException(
                "Number of tags belonging to song with ID "+song.getID()+" was inconsistent.");
        }

        // validate the new tag
        tagValidator.validateTags(out);
        
        return out;
    }



    /**
     * Removes a given {@link Tag} from a {@link Song} and returns the tags still attached to the 
     * song.
     * Returned tags are unordered.
     * 
     * {@code tag} and {@code song} are validated before use, and tags being returned are validated.
     * 
     * @param tag the {@link Tag} to be deleted.
     * @param song the {@link Song} that {@code tag} is being deleted from.
     * @return a list of {@link Tag} instances still attached to the {@link Song}.
     * @throws IllegalArgumentException If {@code tag} or {@code song} fail validation, or if any
     * returned {@link Tag} fails validation.
     * @throws TagNotFoundException If {@code tag} is not found to be attached to {@code song}
     */
    public List<Tag> removeTagFromSong(Tag tag, Song song) {
        tagValidator.validateTag(tag);
        songValidator.validateSong(song);

        int size = tagPersistence.getTagsOfSong(song.getID()).size();

        List<Tag> tags = tagPersistence.deleteTagFromSong(tag.getID(), song.getID());
        tagValidator.validateTags(tags);

        if (tags.size() != size - 1) {
            throw new IllegalArgumentException(
                "Number of tags belonging to song with ID "+song.getID()+" was inconsistent.");
        }

        return tags;
    }



    /**
     * Retrieves all {@link Tag} instances in the database.
     * 
     * All {@link Tag} instances are validated before being returned.
     * 
     * @return a list of all {@link Tag} instances.
     * @throws IllegalArgumentException if a {@link Tag} fails validation.
     */
    public List<Tag> getAllTags() {
        List<Tag> tags = tagPersistence.getAllTags();
        tagValidator.validateTags(tags);
        return tags;
    }



    /**
     * Retrieves the {@link Song}s contained in {@code songs} that contain the given {@code tag}.
     * 
     * {@code tag} is validated before use. {@code IDs} being returned are validated before
     * returning the list of {@link Song}s.
     * 
     * @param songs the list of {@link Song}s to search through
     * @param tag the {@link Tag} to filter {@code songs} by.
     * @return a list of {@code songs} corresponding to {@link Song} instances associated with {@code tag}.
     * @throws IllegalArgumentException if {@code tag} is invalid or any returned {@code ID} fails validation.
     */
    public List<Song> getSongsByTag(List<Song> songs, Tag tag) {
        tagValidator.validateTag(tag);
        List<Integer> ids = tagPersistence.getSongIdsByTag(tag.getID());
        List<Song> filtered = new ArrayList<>();

        for (Song song : songs) {
            if (ids.contains(song.getID())) {
                filtered.add(song);
            }
        }
        songValidator.validateIDs(ids);
        return filtered;
    }



    /**
     * Retrieves all {@link Tag} instances associated with a {@code song}, returning them without
     * order.
     * 
     * {@code song} is validated before use, and all returned {@link Tag} instances are validated.
     * 
     * @param song the {@link Song} to find all {@link Tag} instances for.
     * @return all {@link Tag} instances associated with a given {@link Song}
     * @throws IllegalArgumentException if {@code song} is invalid or if any {@link Tag} being
     * returned fails validation.
     */
    public List<Tag> getTagsOfSong(Song song) {
        songValidator.validateSong(song);
        List<Tag> tags = tagPersistence.getTagsOfSong(song.getID());
        tagValidator.validateTags(tags);

        return tags;
    }
}
