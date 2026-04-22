package beatbinder.persistence;

import java.util.List;
import beatbinder.objects.SongCollection;

/**
 * Interface for interacting with the {@link SongCollection} aspect of the database.
 * <p>
 * Provides methods for creating, updating, deleting, and retrieving {@link SongCollection}
 * instances, as well as managing their associated songs. Also supports access to songs
 * filtered by collection, genre, and artist.
 * 
 * @see SongCollection
 */
public interface ICollectionPersistence {
    /**
     * Creates a new {@link SongCollection} in the database with no songs attached.
     * 
     * @param songCollection the {@link SongCollection} to add.
     * @return the added {@link SongCollection}.
     */
    SongCollection createCollection(SongCollection songCollection);

    /**
     * Deletes a given {@link SongCollection} from the database, along with its associated list of
     * songs.
     * 
     * @param songCollection the {@link SongCollection} to delete.
     * @return the deleted {@link SongCollection}.
     */
    SongCollection deleteCollection(SongCollection songCollection);

    /**
     * Replaces a given {@link SongCollection} in the database with {@code songCollection},
     * without changing its associated song list.
     * 
     * @param songCollection the {@link SongCollection} to replace the old one.
     * @return the updated {@link SongCollection}.
     */
    SongCollection updateCollection(SongCollection songCollection);

    /**
     * Retrieves an unordered list of all {@link SongCollection} instances in the database.
     * 
     * @return a list of all {@link SongCollection} instances in the database.
     */
    List<SongCollection> getAllCollections();

    /**
     * Retrieves an ordered list of the IDs of songs contained in the {@link SongCollection} with
     * the given {@code ID}.
     *
     * @param ID the ID of the {@link SongCollection}.
     * @return a list of song IDs in the collection.
     */
    List<Integer> getSongIdsByCollection(int ID);

    /**
     * Retrieves an unordered list of the IDs of songs made by an artist with the given artist
     * {@code ID}.
     * 
     * @param ID the artist ID
     * @return a list of songs made by the artist.
     */
    List<Integer> getSongIdsByArtist(int ID);
    /**
     * Retrieves an unordered list of the IDs of songs in a genre with the given genre {@code ID}.
     * 
     * @param ID the genre ID.
     * @return a list of songs in the genre.
     */
    List<Integer> getSongIdsByGenre(int ID);

    /**
     * Adds a given song with ID {@code songID} to a given {@link SongCollection} with ID
     * {@code collectionID}. The song is appended to the end of the list of songs, which is
     * returned in order.
     * 
     * @param collectionID the {@link SongCollection} ID.
     * @param songID the song ID.
     * @return a list of song IDs currently in the collection.
     */
    List<Integer> addSongToCollection(int collectionID, int songID);

    /**
     * Deletes a song with ID {@code songID} from a given {@link SongCollection} with ID
     * {@code collectionID}. Songs are re-adjusted to fill any gaps and are returned in order.
     * 
     * @param collectionID the {@link SongCollection} ID.
     * @param songID the song ID.
     * @return the list of song IDs remaining in the collection.
     */
    List<Integer> deleteSongFromCollection(int collectionID, int songID);

    /**
     * Moves the song with ID {@code songID} to the given {@code position} within the
     * {@link SongCollection} identified by {@code collectionID}. The other songs are reordered
     * accordingly, and the updated order is returned.
     * 
     * @param collectionID the ID of the {@link SongCollection}.
     * @param songID the ID of the song to move.
     * @param position the new position of the song.
     * @return a list of song IDs in the updated order.
     */
    List<Integer> setSongPosition(int collectionID, int songID, int position);
}
