package beatbinder.persistence.stub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import beatbinder.exceptions.CollectionNotFoundException;
import beatbinder.exceptions.DuplicateCollectionException;
import beatbinder.exceptions.DuplicateSongException;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.ICollectionPersistence;

public class CollectionStub implements ICollectionPersistence {
    private HashMap<Integer, Integer[]> collectionEntries; // id:[collectionID,songID,position]
    private HashMap<Integer, String> artists; // artistID:artist
    private HashMap<Integer, SongCollection> collectionMap; // collectionID:collection
    private HashMap<Integer, Integer[]> artistEntries; // id:[artistID,songID]
    private HashMap<Integer, Integer[]> genreEntries; // id:[genreID,songID]

    public CollectionStub(HashMap<Integer, Integer[]> collectionEntries,
            HashMap<Integer, String> artists, HashMap<Integer, SongCollection> collectionMap,
            HashMap<Integer, Integer[]> artistEntries, HashMap<Integer, Integer[]> genreEntries) {
        this.collectionEntries = collectionEntries;
        this.artists = artists;
        this.collectionMap = collectionMap;
        this.artistEntries = artistEntries;
        this.genreEntries = genreEntries;
    }

    // Update a collection with new information
    @Override
    public SongCollection updateCollection(SongCollection songCollection) {
        collectionExists(songCollection.getID());

        collectionMap.put(songCollection.getID(), songCollection);
        return fillCollection(songCollection);
    }

    // Remove a given collection from db
    @Override
    public SongCollection deleteCollection(SongCollection songCollection) {
        collectionExists(songCollection.getID());
        return fillCollection(collectionMap.remove(songCollection.getID()));
    }

    // Take a new collection and add it to the db
    @Override
    public SongCollection createCollection(SongCollection coll) {
        if (collectionMap.containsKey(coll.getID())) {
            throw new DuplicateCollectionException("Collection with ID "
                    + coll.getID() + " already exists.");
        }

        checkDuplicateName(coll.getTitle());

        int newKey = getEmptyKey();

        SongCollection newColl = new SongCollection(
                newKey,
                coll.getArtistID(), 
                coll.getTitle(),
                coll.getYear(),
                coll.getArtist(),
                coll.getType(),
                coll.isLiked());
        collectionMap.put(newKey, newColl);
        return fillCollection(newColl);
    }

    // Returns all SongCollections currently in db
    @Override
    public List<SongCollection> getAllCollections() {
        List<SongCollection> allCollections = new ArrayList<>();

        for (SongCollection songCollection : collectionMap.values()) {
            allCollections.add(fillCollection(songCollection));
        }

        return Collections.unmodifiableList(allCollections);
    }

    // Add a song to the end of a collection
    @Override
    public List<Integer> addSongToCollection(int collectionID, int songID) {
        // check if the collection exists
        collectionExists(collectionID);

        // check if the song is already in the collection
        int key = findSongInCollection(collectionID, songID);
        if (key >= 0) {
            throw new DuplicateSongException("Song with ID " + songID +
                    " already in collection with ID " + collectionID);
        }

        // put the song at the end of the playlist
        int newKey = collectionEntries.size();
        int position = getKeysToSongsInCollection(collectionID).size() + 1;
        collectionEntries.put(newKey, new Integer[] { collectionID, songID, position });

        // cast Integer[] to unmodifiable list and return
        return Collections.unmodifiableList(Arrays.asList(collectionEntries.get(newKey)));
    }

    @Override
    public List<Integer> deleteSongFromCollection(int collectionID, int songID) {
        collectionExists(collectionID);
        // get song key
        int key = findSongInCollection(collectionID, songID);
        // remove song
        Integer[] song = collectionEntries.remove(key);
        int songPos = song[2]; // save song position
        // get all songs in playlist
        List<Integer> allSongs = getKeysToSongsInCollection(collectionID);
        // loop over songs, moving them if they need moving
        for (Integer songKey : allSongs) {
            Integer[] currSong = collectionEntries.get(songKey);
            if (currSong[2] > songPos) {
                // move the song up
                currSong[2] = currSong[2] - 1;
                // save
                collectionEntries.put(songKey, currSong);
            }
        }
        return Collections.unmodifiableList(getSongIdsByCollection(collectionID));
    }

    // my best work. Clean O(n) solution - no sorting required
    // Returns all song IDs associated with a collection ID
    @Override
    public List<Integer> getSongIdsByCollection(int collectionID) {
        List<Integer[]> matchingSongs = filterSongsByID(collectionEntries, collectionID);
        return Collections.unmodifiableList(orderSongsByPosition(matchingSongs));
    }

    // Finds all song IDs associated with an artist ID
    @Override
    public List<Integer> getSongIdsByArtist(int artistID) {
        return Collections.unmodifiableList(getSongIDsByLabel(artistEntries, artistID));
    }

    // Finds all song IDs associated with a genre ID
    @Override
    public List<Integer> getSongIdsByGenre(int genreID) {
        return Collections.unmodifiableList(getSongIDsByLabel(genreEntries, genreID));
    }

    @Override
    public List<Integer> setSongPosition(int collectionID, int songID, int position) {
        // get song from collection
        int key = findSongInCollection(collectionID, songID);
        if (key < 0) {
            throw new SongNotFoundException("Song with ID " + songID + " not found in collection with ID " + collectionID); 
        }
        Integer[] data = collectionEntries.get(key);

        // save the old position 
        int oldPos = data[2];
        // set new position
        data[2] = position;
        collectionEntries.put(key, data);

        // get all songs in collection
        List<Integer> songKeys = getKeysToSongsInCollection(collectionID);

        // iterate over them, adding 1 to positions between the old and new positions
        for (Integer songKey : songKeys) {
            if (songKey.equals(key)) continue; // skip the song being moved

            Integer[] currSong = collectionEntries.get(songKey);
            int pos = currSong[2];

            if (oldPos < position) {
                // Moving down: shift songs in (oldPos, position] up by 1
                if (pos > oldPos && pos <= position) {
                    currSong[2]--;
                }
            } else if (oldPos > position) {
                // Moving up: shift songs in [position, oldPos) down by 1
                if (pos >= position && pos < oldPos) {
                    currSong[2]++;
                }
            }
        }


        // return the ID of all songs in the collection in order.
        return getSongIdsByCollection(collectionID);
    }

    private void collectionExists(int ID) {
        if (!collectionMap.containsKey(ID)) {
            throw new CollectionNotFoundException("Collection with ID " + ID + " not found.");
        }
    }

    // returns the id of the data found in collectionEntries
    // returns -1 if not found
    private int findSongInCollection(int collectionID, int songID) {

        // look for a collection containing the song
        for (Integer keys : collectionEntries.keySet()) {
            if (collectionEntries.get(keys)[0] == collectionID &&
                    collectionEntries.get(keys)[1] == songID) {

                return keys;
            }
        }

        return -1;
    }

    // Filters a map of song metadata arrays by matching label ID (e.g., collection,
    // artist, genre)
    private List<Integer[]> filterSongsByID(Map<Integer, Integer[]> songMap, int targetID) {
        List<Integer[]> result = new ArrayList<>();
        for (Integer[] songData : songMap.values()) {
            if (songData[0] == targetID) {
                result.add(songData);
            }
        }
        return result;
    }

    // Returns a list of song IDs matching a given ID (no ordering)
    // For example, all songs by a given artist or genre
    private List<Integer> getSongIDsByLabel(Map<Integer, Integer[]> songMap, int ID) {
        List<Integer> result = new ArrayList<>();
        for (Integer[] songData : songMap.values()) {
            if (songData[0] == ID) {
                result.add(songData[1]);
            }
        }
        return result;
    }

    // Orders song IDs by their position field
    // Expects input like [collectionID, songID, position]
    private List<Integer> orderSongsByPosition(List<Integer[]> songsWithPosition) {
        Integer[] sorted = new Integer[songsWithPosition.size()];
        for (Integer[] songData : songsWithPosition) {
            sorted[songData[2]-1] = songData[1];
        }
        return Arrays.asList(sorted);
    }

    // It is permissible for a collection to not have all the information (missing
    // artist name)
    // This function replaces information that is missing.
    private SongCollection fillCollection(SongCollection songCollection) {
        String artist = null;
        if (songCollection.getArtist() == null) {
            artist = artists.get(songCollection.getArtistID());
        }
        if (artist != null) {
            songCollection = new SongCollection(songCollection.getID(),
                    songCollection.getArtistID(), songCollection.getTitle(),
                    songCollection.getYear(), artist, songCollection.getType(),
                    songCollection.isLiked());
        }
        return songCollection;
    }

    // Returns the location (key) of songs with a given collection ID
    private List<Integer> getKeysToSongsInCollection(int collectionID) {
        List<Integer> result = new ArrayList<>();
        for (Integer songData : collectionEntries.keySet()) {
            if (collectionEntries.get(songData)[0] == collectionID) {
                result.add(songData);
            }
        }
        return result;
    }

    /**
     * Finds an unused key.
     * @return
     */
    private int getEmptyKey() {
        int key = 0;
        Set<Integer> keys = collectionMap.keySet();
        while (keys.contains(key)) {
            key++;
        }
        return key;
    }

    private void checkDuplicateName(String name) {
        for (SongCollection coll : collectionMap.values()) {
            if (coll.getTitle().equals(name)) {
                throw new DuplicateCollectionException("Collection with title "+name+" already exists.");
            }
        }
    }
}
