package beatbinder.logic;

import beatbinder.persistence.ISongPersistence;
import beatbinder.presentation.components.SortOption;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.logic.validation.SongValidator;
import beatbinder.objects.Song;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles updates to {@link Song} instances, such as liking songs and adding notes,
 * acting as an intermediary between the presentation and persistence layers.
 * 
 * This class uses {@link SongValidator} for validation and communicates with the database
 * through {@link ISongPersistence}. It ensures that data passed between layers is valid
 * and transforms {@link Song} instances when necessary to support these interactions.
 */
public class SongManager {
    /**
     * The interface used to interact with the database. Limits access of database to only
     * {@link Song}-related methods like reading and updating.
     */
    private ISongPersistence songPersistence;
    /**
     * The validator used for validating {@link Song}-related behaviour.
     */
    private SongValidator songValidator;


    //------------------------------------------------------------------------------------------------//


    /**
     * Constructs a {@link SongManager} for managing {@link Song} instances in the database.
     * This manager uses {@link ISongPersistence} to communicate with the database and 
     * {@link SongValidator} to validate data coming in and out of the database.
     * 
     * @param persistence the persistence interface used to store and retrieve songs
     * @param songValidator the validator used to validate data flowing through the manager
     */
    public SongManager(ISongPersistence persistence, SongValidator songValidator) {
        this.songPersistence = persistence;
        this.songValidator = songValidator;
    }


    //------------------------------------------------------------------------------------------------//



    /**
     * Retrieves all {@link Song} instances stored in the database, returned with no order.
     * 
     * All songs are validated before being returned.
     * 
     * @return A list of all songs in the database.
     * @throws IllegalArgumentException If a song from the database fails verification.
     */
    public List<Song> getAllSongs() {
        List<Song> songs = songPersistence.getAllSongs();
        songValidator.validateSongs(songs);
        
        return songs;
    }



    /**
     * Retrieves a specific {@link Song} by its {@code songID}.
     * 
     * {@code songID} is validated before use. The returned {@link Song} is also validated.
     * 
     * @param songID the ID of the song to retrieve
     * @return the validated {@link Song} with the specified ID
     * @throws IllegalArgumentException if {@code songID < 0} or if the retrieved song fails validation
     */
    public Song getSongByID(int songID) {
        // check with validation to see if ID is valid
        songValidator.validateID(songID);
        // get song
        Song song = songPersistence.getSongByID(songID);
        // validate song
        songValidator.validateSong(song);
        return song;
    }


    // Gets Songs that are made by artist with ID: artistID
    public List<Song> getAllSongsByArtist(int artistID) {
        List<Song> artistSongs = new ArrayList<>();

        for(Song song : songPersistence.getAllSongs()) {
            if(song.getArtistID() == artistID) {
                artistSongs.add(song);
            }
        }

        return artistSongs;
    }


    /**
     * Retrieves all {@link Song} instances that have been marked as liked by the user with no 
     * order.
     * 
     * Songs are validated before being returned.
     * 
     * @return A list of all liked songs.
     * @throws IllegalArgumentException If a song being returned fails validation.
     */
    public List<Song> getLikedSongs() {
        List<Song> allSongs = songPersistence.getAllSongs();
        List<Song> likedSongs = new java.util.ArrayList<>();

        for(Song song : allSongs) {
            if(song.isLiked()) {
                likedSongs.add(song);
                songValidator.validateSong(song);
            }
        }
        
        return likedSongs;
    }



    /**
     * Checks if a given {@link Song} has been marked as liked by the user.
     * 
     * {@code songID} is validated to be {@code > 0}
     * 
     * @param songID The ID of the song to check.
     * @return Whether or not the song has been marked as liked.
     * @throws IllegalArgumentException If {@code songID < 0} or if {@code song} fails validation.
     * @throws SongNotFoundException If the {@code songID} is not found in the database.
     */
    public boolean isSongLiked(int songID) {
        songValidator.validateID(songID);
        Song song = getSongByID(songID);
        songValidator.validateSong(song);

        return song.isLiked();
    }



    /**
     * Toggles the liked status on the given {@link Song}.
     * 
     * {@code song} is validated before use. Updated {@link Song} is validated before being
     * returned.
     * 
     * @param song The {@link Song} to be toggled.
     * @return The updated song with the liked status switched.
     * @throws IllegalArgumentException If {@code song} fails validation or if the song being 
     * returned fails.
     * @throws SongNotFoundException If the song is not found in the database.
     */
    public boolean toggleLikeSong(Song song) {
        songValidator.validateSong(song);

        Song updatedSong = song.toggleLike();
        updatedSong = songPersistence.updateSong(updatedSong);

        songValidator.validateSong(updatedSong);
        
        return updatedSong.isLiked();
    }



    /**
     * Replaces the note on the given {@link Song}.
     * 
     * {@code song} is validated before use. Updated {@link Song} is validated before being
     * returned.
     * 
     * @param song The {@link Song} to be updated.
     * @param note The {@link String} that contains the updated note.
     * @return The updated song with the liked status switched.
     * @throws IllegalArgumentException If {@code song} fails validation or if the song being 
     * returned fails.
     * @throws SongNotFoundException If the song is not found in the database.
     */
    public Song updateNote(Song song, String note) {
        songValidator.validateSong(song);

        Song updatedSong = song.updateNote(note);
        updatedSong = songPersistence.updateSong(updatedSong);

        songValidator.validateSong(updatedSong);

        return updatedSong;
    }

    public List<Song> sortSongs(List<Song> songs, SortOption option) {
        Comparator<Song> comparator = getComparator(option);
        if (comparator == null) {
            return songs;
        }
        return songs.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
    }

    /**
     * Searches for {@link Song} instances in {@code songs} with titles similar to {@code title}.
     *
     * @param songs the list of songs to search through
     * @param title the title to search
     * @return songs that have similar titles to {@code title}.
     */
    public List<Song> searchSongByTitle(List<Song> songs, String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Song title cannot be null or empty.");
        }

        String lower = title.toLowerCase();
        List<Song> searched = new ArrayList<>();

        for (Song song : songs) {
            // check if the title of the song contains the search parameter
            if (song.getTitle().toLowerCase().contains(lower)) {
                songValidator.validateSong(song);
                searched.add(song);
            }
        }

        return searched;
    }

    private Comparator<Song> getComparator(SortOption option) {
        return switch (option) {
            case DEFAULT -> null;
            case TITLE -> Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER);
            case ARTIST -> Comparator.comparing(Song::getArtist, String.CASE_INSENSITIVE_ORDER);
            case RUNTIME -> Comparator.comparingInt(Song::getRuntime); // or getDuration()
        };
    }
}
