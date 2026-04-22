package beatbinder.persistence.stub;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import beatbinder.exceptions.DuplicateTagException;
import beatbinder.exceptions.DuplicateTagNameException;
import beatbinder.exceptions.TagNotFoundException;
import beatbinder.objects.Tag;
import beatbinder.persistence.ITagPersistence;

public class TagStub implements ITagPersistence{
    private HashMap<Integer, Tag> tags;
    private HashMap<Integer, Integer[]> tagEntites; // key:[tagID,songID]

    public TagStub(HashMap<Integer, Tag> tags, HashMap<Integer, Integer[]> tagEntities) {
        this.tags = tags;
        this.tagEntites = tagEntities;
    }

    @Override
    public Tag createTag(Tag tag) {
        // TODO: Duplicate tags can be created right now
        // Right now every tag.getID will be -1, so the containsKey will always return false
        // Need to compare by name somehow...
        if (containsDuplicateName(tag.getName())) {
            throw new DuplicateTagNameException("Tag with name "+tag.getName()+" already exists.");
        }

        int newID = getEmptyID();
        Tag newTag = new Tag(newID, tag.getName());

        tags.put(newID, newTag);
        return newTag;
    }

    @Override
    public Tag deleteTag(Tag tag) {
        if (!tags.containsKey(tag.getID())) {
            throw new TagNotFoundException("Tag with ID " + tag.getID() + " does not exist.");
        }
        // remove the tag itself
        tags.remove(tag.getID());

        // remove the tag from all songs that possess it
        for (Integer key : tagEntites.keySet()) {
            if (tagEntites.get(key)[0] == tag.getID()) {
                tagEntites.remove(key);
            }
        }
        return tag;
    }

    @Override
    public Tag updateTag(Tag tag) {
        if (!tags.containsKey(tag.getID())) {
            throw new TagNotFoundException("Tag with ID " + tag.getID() + " does not exist.");
        }

        tags.put(tag.getID(), tag);
        return tag;
    }

    @Override
    public List<Tag> getAllTags() {
        List<Tag> allTags = new ArrayList<>(tags.size());

        for (Tag tag : tags.values()) {
            allTags.add(tag);
        }

        return Collections.unmodifiableList(allTags);
    }

    @Override
    public Tag getTagByID(int id) {
        if (!tags.containsKey(id)) {
            throw new TagNotFoundException("Tag with ID " + id + " does not exist.");
        }

        return tags.get(id);
    }

    @Override
    public List<Tag> addTagToSong(int tagID, int songID) {
        // check that the tag exists
        if (!tags.containsKey(tagID)) {
            throw new TagNotFoundException("Tag with ID " + tagID + " does not exist.");
        }

        // check if the song already has the tag
        if (songHasTag(tagID, songID) >= 0) {
            throw new DuplicateTagException("Song with ID " + songID + " already has tag with ID " + tagID + ".");
        }

        // give the tag to the song
        tagEntites.put(tagEntites.size(), new Integer[]{tagID, songID});

        return Collections.unmodifiableList(getTagsOfSong(songID));
    }

    @Override
    public List<Tag> deleteTagFromSong(int tagID, int songID) {
        // check if the tag exists
        if (!tags.containsKey(tagID)) {
            throw new TagNotFoundException("Tag with ID " + tagID + " does not exist.");
        }
        
        // find the key to the tag:song pair
        Integer tagKey = -1;
        for (Integer key : tagEntites.keySet()) {
            if (tagEntites.get(key)[0] == tagID && tagEntites.get(key)[1] == songID) {
                tagKey = key;
                break;
            }
        }

        // throw an error if key isn't found
        if (tagKey == -1) {
            throw new TagNotFoundException("Song with ID " + songID + " does not have tag with ID " + tagID + ".");
        }

        // remove and return
        tagEntites.remove(tagKey);
        return Collections.unmodifiableList(getTagsOfSong(songID));
    }

    @Override
    public List<Tag> getTagsOfSong(int songID) {
        List<Integer> tagIdList = new ArrayList<>();

        // get ID of all tags on a song
        for (Integer[] data : tagEntites.values()) {
            if (data[1] == songID) {
                tagIdList.add(data[0]);
            }
        }

        // use the IDs to get the tags
        List<Tag> foundTags = new ArrayList<>(tagIdList.size());
        for (Integer id : tagIdList) {
            foundTags.add(tags.get(id));
        }

        // return tags
        return Collections.unmodifiableList(foundTags);
    }

    @Override
    public List<Integer> getSongIdsByTag(int tagID) {
        List<Integer> songs = new ArrayList<>();

        // get ID of all songs with a tag
        for (Integer[] data : tagEntites.values()) {
            if (data[0] == tagID) {
                songs.add(data[1]);
            }
        }

        // return the song IDs
        return Collections.unmodifiableList(songs);
    }

    private int songHasTag(int tagID, int songID) {
        for (Integer entry : tagEntites.keySet()) {
            if (tagEntites.get(entry)[0] == tagID && tagEntites.get(entry)[1] == songID) {
                return entry;
            }
        }

        return -1;
    }

    /**
     * Finds an unused ID.
     * @return
     */
    private int getEmptyID() {
        int out = 0;
        while (true) {
            if (!tags.containsKey(out)) {
                return out;
            }
            out++;
        }
    }

    /**
     * Checks if there is a {@link Tag} in {@code tags} with the same name as the given {@code name}.
     * @param name the name to find.
     * @return whether an existing {@link Tag} has the same name.
     */
    private boolean containsDuplicateName(String name) {
        for (Tag tag : tags.values()) {
            if (tag.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
