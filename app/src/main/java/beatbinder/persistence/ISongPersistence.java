package beatbinder.persistence;

import java.util.List;
import beatbinder.objects.Song;

/**
 * Interface for interacting with the {@link Song} aspect of the database.
 * Contains functionality to update songs as well as retrieving one or multiple songs at a time.
 */
public interface ISongPersistence {
    /**
     * Replaces the associated {@link Song} instance in the database with the new {@code song}.
     * 
     * @param song the new {@link Song}.
     * @return the new Song.
     * @throws SongNotFoundException if {@code song} is not found in the database.
     */
    Song updateSong(Song song);

    /**
     * Retrieves the {@link Song} instance associated with the given {@code id}.
     * 
     * @param id the ID of the {@link Song} to return.
     * @return the song with ID = {@code id}.
     * @throws SongNotFoundException if a song with ID {@code id} is not found in the database.
     */
    Song getSongByID(int id);

    /**
     * Retrieves a list of {@link Song} instances with the associated {@code IDs}.
     * 
     * Returned instances have no order.
     * 
     * @param IDs the IDs of the {@link Song} instances to retrieve.
     * @return the songs with IDs in {@code IDs}.
     * @throws SongNotFoundException if an ID is not found to have an associated song in the database.
     */
    List<Song> getCollectionSongs(int[] IDs); // Jonas is small brain for making this int[] not List<Integer>
    
    /**
     * Retrieves a list of all {@link Song} instances in the database.
     * 
     * @return a list of all songs in the database.
     */
    List<Song> getAllSongs();
}
