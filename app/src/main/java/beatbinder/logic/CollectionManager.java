package beatbinder.logic;

import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.exceptions.CollectionNotFoundException;
import beatbinder.exceptions.DuplicateCollectionException;
import beatbinder.exceptions.DuplicateSongException;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.exceptions.DuplicatePlaylistNameException;
import beatbinder.logic.validation.SongCollectionValidator;
import beatbinder.logic.validation.SongValidator;
import beatbinder.objects.CollType;
import beatbinder.persistence.ICollectionPersistence;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the creation, reading, updating and deleting of collections in the database, acting as
 * an intermediary between presentation and persistence.
 * 
 * This class handles data validation with {@link SongCollectionValidator} and
 * {@link SongValidator}, and retrieves data from {@link ICollectionPersistence}. It ensures only
 * valid data in exchanged between presentation and persistence, and manipulates data to make the
 * jobs of both sides easier.
 */
public class CollectionManager {
    /**
     * The current year in which a {@code PLAYLIST} is being created.
     */
    private static final int year = Year.now().getValue();
    /**
     * The artist ID assigned to the user, used for {@link SongCollection} instances of type
     * {@code PLAYLIST}
     */
    private static final int userArtistID = 1;
    /**
     * Used for created playlists. ID assignment is handled by persistence but we need an
     * ID to give the playlist temporarily, so we assign -1.
     */
    private static final int defaultID = -1;

    /**
     * Store persistence to get/send information from/to the database about {@link SongCollection}
     * instances.
     */
    private ICollectionPersistence collectionPersistence;
    /**
     * Validator used to validate information related to {@link Song} instances.
     */
    private SongValidator songValidator;
    /**
     * Validator used to validate information related to {@link SongCollection} instances.
     */
    private SongCollectionValidator collValidator;


    //------------------------------------------------------------------------------------------------//


    /**
     * Constructs a {@link CollectionManager} for managing groups of songs,
     * such as {@code PLAYLIST} and {@code ALBUM}. The manager uses the provided persistence
     * and validator components to enforce business rules and data integrity.
     *
     * @param persistence      the persistence interface used to store and retrieve collections
     * @param songValidator    validates individual songs for inclusion in collections
     * @param collValidator    validates the structural and logical integrity of song collections
    */
    public CollectionManager(ICollectionPersistence persistence, SongValidator songValidator,
            SongCollectionValidator collValidator) {
        this.collectionPersistence = persistence;
        this.songValidator = songValidator;
        this.collValidator = collValidator;
    }


    //------------------------------------------------------------------------------------------------//



    /**
     * Retrieves all song collections (albums and playlists) from the database.
     * The list is unordered—no guarantees are made about the order of returned items.
     * 
     * Each collection is validated before being returned.
     *
     * @return a list of all stored {@link SongCollection} instances.
     * @throws IllegalArgumentException if validation fails
     */
    public List<SongCollection> getAllCollections() {
        List<SongCollection> colls = collectionPersistence.getAllCollections();
        collValidator.validateColls(colls);
        return colls;
    }



    /**
     * Retrieves only album-type song collections from the database.
     * The list has no order.
     * 
     * Each album is validated before being returned.
     * 
     * @return a list of all stored {@link SongCollection} instances of type {@code ALBUM}.
     * @throws IllegalArgumentException if validation fails
     */
    public List<SongCollection> getAllAlbums() {
        List<SongCollection> allCollections = collectionPersistence.getAllCollections();
        List<SongCollection> albums = new java.util.ArrayList<>();

        for (SongCollection collection : allCollections) {
            if (collection.getType() == CollType.ALBUM) {
                albums.add(collection);
                collValidator.validateColl(collection);
            }
        }

        return albums;
    }



    /**
     * Retrieves all playlist-type song collections from the database.
     * The list has no order.
     * 
     * Each playlist is validated before being returned.
     * 
     * @return a list of all stored {@link SongCollection} instances of type {@code PLAYLIST}.
     * @throws IllegalArgumentException if validation fails
     */
    public List<SongCollection> getAllPlaylists() {
        List<SongCollection> allCollections = collectionPersistence.getAllCollections();
        List<SongCollection> playlists = new java.util.ArrayList<>();

        for (SongCollection collection : allCollections) {
            if (collection.getType() == CollType.PLAYLIST) {
                playlists.add(collection);
                collValidator.validateColl(collection);
            }
        }

        return playlists;
    }



    /**
     * Retrieves all song collections that are marked as liked by the user.
     * 
     * Only collections considered liked are returned, and each one is validated before inclusion.
     *
     * @return a list of liked {@link SongCollection} instances.
     * @throws IllegalArgumentException if validation fails
     */
    public List<SongCollection> getLikedCollections() {
        List<SongCollection> allCollections = collectionPersistence.getAllCollections();
        List<SongCollection> likedCollections = new java.util.ArrayList<>();

        for (SongCollection collection : allCollections) {
            if (collection.isLiked()) {
                likedCollections.add(collection);
                collValidator.validateColl(collection);
            }
        }
        
        return likedCollections;
    }



    /**
     * Retrieves all song collections of type {@code ALBUM} that has been marked as liked by the
     * user.
     * 
     * Only liked albums are returned, and each one is validated before inclusion.
     * 
     * @return a list of liked {@link SongCollection} instances.
     * @throws IllegalArgumentException if validation fails
     */
    public List<SongCollection> getLikedAlbums() {
        List<SongCollection> likedCollections = getLikedCollections();
        List<SongCollection> likedAlbums = new java.util.ArrayList<>();

        for (SongCollection collection : likedCollections) {
            if (collection.getType() == CollType.ALBUM) {
                likedAlbums.add(collection);
                collValidator.validateColl(collection);
            }
        }
        
        return likedAlbums;
    }



    /**
     * Retrieves the IDs of all songs in the given song collection, regardless of collection type
     * ({@code PLAYLIST} or {@code ALBUM}).
     * 
     * All song IDs are validated before being returned.
     * 
     * @param coll the song collection to retrieve song IDs from; must be a valid 
     * {@link SongCollection}.
     * @return a list of song IDs corresponding to {@link Song} instances.
     * @throws IllegalArgumentException if validation fails
     */
    public List<Integer> getSongIDsByCollection(SongCollection coll) {
        collValidator.validateColl(coll);

        List<Integer> ids = collectionPersistence.getSongIdsByCollection(coll.getID());
        songValidator.validateIDs(ids);

        return ids;
    }



    /**
     * Toggles the liked status of the given {@link SongCollection}.
     * 
     * @param coll the {@link SongCollection} for like to be toggled.
     * 
     * @return The updated {@link SongCollection} instance.
     * 
     * @throws IllegalArgumentException if {@code coll} is invalid.
     * @throws CollectionNotFoundException if {@code coll} does not exist in the database.
     */
    public SongCollection toggleLikeCollection(SongCollection coll) {
        if (coll == null) {
            throw new IllegalArgumentException();
        }
        collValidator.validateColl(coll);

        SongCollection updatedColl = coll.toggleLike();

        return collectionPersistence.updateCollection(updatedColl);
    }



    /**
     * Create a {@link SongCollection} of type {@code PLAYLIST} with title {@code title}.
     * 
     * {@code title} cannot be {@code null}
     * 
     * @param title The name of the {@link SongCollection} of type {@code PLAYLIST} to be created.
     * @return The created {@link SongCollection} of type {@code PLAYLIST}.
     * @throws IllegalArgumentException If {@code title == null} or generated {@link SongCollection}
     * is invalid.
     * @throws DuplicateCollectionException if {@link SongCollection} already exists.
     */
    public SongCollection createPlaylist(String title) {
        // validate title
        assessNewPlaylistName(title);

        // create song collection blueprint
        SongCollection coll = new SongCollection(
                defaultID,
                userArtistID,
                title,
                year,
                CollType.PLAYLIST,
                true);

        // create song collection and verify
        SongCollection newColl = collectionPersistence.createCollection(coll);
        collValidator.validateColl(newColl);

        return newColl;
    }



    /**
     * Updates the given {@link SongCollection} with a new title and saves the changes to the database.
     * 
     * All input is validated before use, and the updated {@link SongCollection} is validated before return.
     *
     * @param coll the collection to update; must not be {@code null}
     * @param newTitle the new title to assign; must not be {@code null}
     * @return the updated {@link SongCollection}
     * @throws IllegalArgumentException if validation fails.
     * @throws CollectionNotFoundException if {@code coll} is not in the database.
     */
    public SongCollection updatePlaylist(SongCollection coll, String newTitle) {
        // Check our current playlist is good
        collValidator.validateColl(coll);
        // Check that our new playlist name is good
        assessNewPlaylistName(newTitle);

        SongCollection collection = new SongCollection(
            coll.getID(),
            coll.getArtistID(),
            newTitle,
            coll.getYear(),
            coll.getArtist(),
            coll.getType(),
            coll.isLiked());

        collValidator.validateColl(collection);
        SongCollection updated = collectionPersistence.updateCollection(collection);
        collValidator.validateColl(updated);

        return updated;
    }



    /**
     * Deletes a {@link SongCollection} of type {@code PLAYLIST} from the database.
     * 
     * {@code coll} is validated before deletion, and the returned {@link SongCollection} is 
     * validated.
     * 
     * @param coll The {@link SongCollection} to be deleted; must not be {@code null}.
     * @return The {@link SongCollection} that was deleted.
     * @throws IllegalArgumentException if validation fails.
     * @throws SongNotFoundException if {@code coll} is not in the database.
     */
    public SongCollection deletePlaylist(SongCollection coll) {
        collValidator.validateColl(coll);
        coll = collectionPersistence.deleteCollection(coll);
        collValidator.validateColl(coll);

        return coll;
    }



    /**
     * Add a given {@link Song} to a given {@link SongCollection} of type {@code PLAYLIST} and
     * retrieve {@code IDs} of all {@link Song} instances in the given {@link SongCollection}
     * 
     * {@code coll} and {@code song} are validated before use, and {@code IDs} are validated
     * before returning in order.
     * 
     * @param coll The {@link SongCollection} to add {@code song} to.
     * @param song The {@link Song} to be added to {@code coll}
     * @return The {@code IDs} of all {@link Song} instances in {@code coll} in order.
     * @throws IllegalArgumentException if {@code coll} or {@code song} are invalid.
     * @throws CollectionNotFoundException if {@link SongCollection} is not found in database.
     */
    public List<Integer> addSongToPlaylist(SongCollection coll, Song song) {
        collValidator.validateColl(coll);
        songValidator.validateSong(song);

        // check if song is already in playlist
        if (songInPlaylist(coll, song)) {
            throw new DuplicateSongException("Song already in playlist");
        }

        // add and validate output
        List<Integer> ids = collectionPersistence.addSongToCollection(coll.getID(), song.getID());
        songValidator.validateIDs(ids);

        return ids;
    }



    /**
     * Removes the given {@link Song} from the specified {@link SongCollection}, then retrieves the
     * {@code IDs} of the remaining songs in the collection in order.
     * 
     * Both {@code coll} and {@code song} are validated before use. The resulting {@code IDs} are
     * also validated before being returned.
     * 
     * @param coll the {@link SongCollection} to remove the song from
     * @param song the {@link Song} to remove
     * @return a list of {@code IDs} for the remaining {@link Song} instances in the collection
     * in order.
     * @throws SongNotFoundException if the song is not found in the given collection
     * @throws IllegalArgumentException if validation fails
    */
    public List<Integer> deleteSongFromPlaylist(SongCollection coll, Song song) {
        collValidator.validateColl(coll);
        songValidator.validateSong(song);

        if (!songInPlaylist(coll, song)) {
            throw new SongNotFoundException("Song not in playlist");
        }

        List<Integer> ids = collectionPersistence.deleteSongFromCollection(coll.getID(), song.getID());
        songValidator.validateIDs(ids);

        return ids;
    }



    /**
     * Move a given {@link Song} to a specified {@code position} in the specified {@link SongCollection}
     * 
     * Both {@code coll} and {@code song} are validated before use, and {@code position} is
     * verified to be within the bounds of {@code coll}
     * 
     * @param coll The {@link SongCollection} the song belongs to.
     * @param song The {@link Song} that is being moved.
     * @param position The position the song is being moved to.
     * @return The {@code IDs} of {@link Song} instances in the song collection in their new order.
     * @throws IllegalArgumentException If {@code coll} or {@code song} are invalid, or if
     * {@code position} is out of range.
     * @throws CollectionNotFoundException If {@code coll} is not found in the database.
     */
    public List<Integer> setSongPosition(SongCollection coll, Song song, int position) {
        collValidator.validateColl(coll);
        songValidator.validateSong(song);

        if (!songInPlaylist(coll, song)) {
            throw new SongNotFoundException("Song not in playlist");
        }

        if (position < 1 || position > collectionSize(coll)) {
            throw new IllegalArgumentException("Song position must be within the bounds of the playlist.");
        }

        List<Integer> ids = collectionPersistence.setSongPosition(coll.getID(), song.getID(), position);
        songValidator.validateIDs(ids);

        return ids;
    }

    /**
     * Retrieves a list of {@link SongCollection} instances with type {@link CollType} whose titles
     * contain {@code title}.
     * <p>
     * {@code type == null} retrieves all song collections of all types.
     *
     * @param title the title to search
     * @param type the {@link Colltype} the {@link SongCollection} should have. {@code null}
     * includes all types.
     * @return all song collections containing {@code title} in the title.
     */
    public List<SongCollection> searchCollectionByTitle(String title, CollType type) {
        if (title == null || title == "") {
            throw new IllegalArgumentException("Title cannot be null");
        }

        String lower = title.toLowerCase();
        List<SongCollection> allCollections = collectionPersistence.getAllCollections();
        List<SongCollection> searched = new ArrayList<>();

        for (SongCollection songCollection : allCollections) {
            if (songCollection.getTitle().toLowerCase().contains(lower)) {
                if (songCollection.getType().equals(type) || type == null) {
                    searched.add(songCollection);
                }
            }
        }

        return searched;
    }




    /**
     * Get the number of {@link Song} instances in a given {@link SongCollection}
     * @param coll The {@link SongCollection} to get the size of. 
     * @return The size of the song collection.
     * @throws IllegalArgumentException If {@code coll} is invalid.
     */
    private int collectionSize(SongCollection coll) {
        return getSongIDsByCollection(coll).size();
    }



    /**
     * Check if a given {@link Song} is in a given {@link SongCollection}
     * 
     * @param coll The {@link SongCollection} to check for the song.
     * @param song The {@link Song} to look for.
     * @return Whether the song is in the {@link SongCollection} or not.
     * @throws CollectionNotFoundException If {@code coll} is not found in the database.
     */
    private boolean songInPlaylist(SongCollection coll, Song song) {
        List<Integer> songsInCollection = getSongIDsByCollection(coll);
        return songsInCollection.contains(song.getID());
    }


    private boolean nameAlreadyTaken(String name) {
        List<SongCollection> allCollections = getAllCollections();
        for (SongCollection collection : allCollections) {
            if (collection.getTitle().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void assessNewPlaylistName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Playlist name cannot be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }
        if (nameAlreadyTaken(name)) {
            throw new DuplicatePlaylistNameException("Playlist name already taken");
        }
    }
}
