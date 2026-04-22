package beatbinder.logic.validation;

import java.util.List;

import beatbinder.objects.Song;

/**
 * Class for validating {@link Song} instances and all their fields.
 * 
 * @see Song
 */
public class SongValidator extends MediaItemValidator{

    /**
     * Validates a given list of {@link Song} instances.
     * 
     * @param songs the {@link Song} instances to be validated.
     * @throws IllegalArgumentException if a song is invalid.
     */
    public void validateSongs(List<Song> songs) {
        for (Song song : songs) {
            validateSong(song);
        }
    }

    /**
     * Validates a given {@link Song} instance.
     * 
     * @param song the {@link Song} to be validated.
     * @throws IllegalArgumentException if the song is invalid.
     */
    public void validateSong(Song song) {
        if (song == null) {
            throw new IllegalArgumentException("Song cannot be null.");
        }
        validateMedia(song);
        coreTests(song);
        validateGenre(song.getGenre());
    }

    /**
     * Validates a {@link Song} instance with a core test suite.
     * 
     * @param song the {@link Song} to be validated.
     * @throws IllegalArgumentException if the song is invalid.
     */
    private void coreTests(Song song) {
        validateGenreID(song.getGenreID());
        validateNote(song.getNote());
        validateRuntime(song.getRuntime());
    }

    /**
     * Ensures a given genre {@code id} is structurally valid (though not necessarily present
     * in the database).
     * 
     * @param id the ID to be validated.
     * @throws IllegalArgumentException if {@id} is invalid.
     */
    private void validateGenreID(int id) {
        if (id < 0) {throw new IllegalArgumentException("Genre cannot have ID "+id+" (ID < 0)");}
    }

    /**
     * Validates the genre name.
     * 
     * A genre is considered invalid if it is an empty string (""), but {@code null} is allowed.
     *
     * @param genre the genre name to validate.
     * @throws IllegalArgumentException if {@code genre} is an empty string.
     */
    private void validateGenre(String genre) {
        if (genre != null && genre.equals("")) {
            throw new IllegalArgumentException("Genre name cannot be empty.");
        }
    }

    /**
     * Ensures the {@code note} of a {@link Song} is valid ({@code note != null})
     * 
     * @param note the note to be validated
     * @throws IllegalArgumentException if {@code note} is invalid.
     */
    private void validateNote(String note) {
        if (note == null) {
            throw new IllegalArgumentException("Note cannot be null.");
        }
    }

    /**
     * Ensures a {@link Song} instance has a realistic runtime in seconds ({@code runtime > 0}). 
     * 
     * @param runtime the time to be validated.
     * @throws IllegalArgumentException if {@code runtime} is invalid.
     */
    private void validateRuntime(int runtime) {
        if (runtime < 0) {
            throw new IllegalArgumentException("Runtime cannot be negative.");
        }
    }
}
