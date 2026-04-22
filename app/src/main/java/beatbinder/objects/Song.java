package beatbinder.objects;

/**
 * Represents a {@code Song}, a type of {@link MediaItem} with additional information such as
 * genre, runtime, and a note.
 * 
 * A {@code Song} is immutable and contains both a genre ID and name, a text note (if any), and its
 * runtime in seconds.
 * 
 * @see MediaItem
 */
public class Song extends MediaItem {
    /**
     * The unique genre ID associated with the song.
     */
    private final int genreID;
    /**
     * The name of the genre.
     */
    private final String genre;
    /**
     * The custom note applied to the song by the user. May be empty but not {@code null}.
     */
    private final String note;
    /**
     * The runtime of the song in seconds.
     */
    private final int runtime;


    /**
     * Creates a new {@link Song}, a type of {@link MediaItem}.
     * 
     * Liked status defaults to false.
     * 
     * @param id the unique ID.
     * @param artistID the unique artist ID.
     * @param title the title.
     * @param year the year of release.
     * @param artist the artist name.
     * @param genreID the unique genre ID.
     * @param genre the genre name.
     * @param note the note applied by the user; may be empty but not {@code null}.
     * @param runtime the runtime of the song in seconds.
     */
    public Song(int id, int artistID, String title, int year, String artist,
                int genreID, String genre, String note, int runtime){
        super(id, artistID, title, year, artist, false);
        this.genre = genre;
        this.genreID = genreID;
        this.note = note;
        this.runtime = runtime;
    }

    /**
     * Creates a new {@link Song}, a type of {@link MediaItem}, without artist or genre names.
     * 
     * Liked status defaults to false.
     * 
     * @param id the unique ID.
     * @param artistID the unique artist ID.
     * @param title the title.
     * @param year the release year.
     * @param genreID the unique genre ID.
     * @param note the note applied by the user; may be empty but not {@code null}.
     * @param runtime the runtime of the song in seconds.
     */
    public Song(int id, int artistID, String title, int year, int genreID, String note, int runtime){
        super(id, artistID, title, year, null, false);
        this.genre = null;
        this.genreID = genreID;
        this.note = note;
        this.runtime = runtime;
    }

    /**
     * Creates a copy {@link Song} with modified {@code liked} and {@code note} fields.
     * 
     * Used internally to create a modified instance.
     * 
     * {@code note} can be empty but not {@code null}.
     * 
     * @param song the {@link Song} being copied.
     * @param note the new note.
     * @param liked the new liked status.
     */
    private Song(Song song, String note, boolean liked) {
        super(song.getID(), song.getArtistID(), song.getTitle(), song.getYear(), song.getArtist(),
                liked);
        this.genre = song.genre;
        this.genreID = song.genreID;
        this.note = note;
        this.runtime = song.runtime;
    }

    /**
     * Creates a copy of this {@link Song} with the liked status toggled.
     *
     * @return a new {@link Song} with the liked status reversed.
     */
    @Override
    public final Song toggleLike() {
        return new Song(this, note, !isLiked());
    }

    /**
     * Creates a copy {@link Song} with an updated note.
     * 
     * {@code newNote} can be empty but not {@code null}.
     * 
     * @param newNote the new note.
     * @return the updated {@link Song}.
     */
    public final Song updateNote(String newNote) {
        return new Song(this, newNote, isLiked());
    }

    /**
     * Retrieves the unique genre ID of this {@link Song}.
     * @return the genre ID.
     */
    public final int getGenreID() {return this.genreID;}
    /**
     * Retrieves the genre name of this {@link Song}.
     * @return the genre name.
     */
    public final String getGenre() {return this.genre;}
    /**
     * Retrieves the user note on this {@link Song}.
     * @return the note.
     */
    public final String getNote() {return this.note;}
    /**
     * Retrieves the runtime of this {@link Song} in seconds.
     * @return the runtime in seconds.
     */
    public final int getRuntime() {return this.runtime;}
}
