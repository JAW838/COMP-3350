package beatbinder.objects;

/**
 * Abstract base class that contains data common to both {@link Song} and {@link SongCollection}.
 * 
 * Encapsulates shared metadata such as ID, artist information, release year, title, and liked status.
 * Intended to reduce duplication and ensure consistent structure between media-related objects.
 *
 * This class is not meant to be instantiated directly.
 * 
 * @see Song
 * @see SongCollection
 */
public abstract class MediaItem {
    /**
     * The unique ID of a {@link MediaItem} used for storing and retrieving the item from the
     * database.
     */
    private final int id;
    /**
     * The unique ID of an artist of {@link MediaItem} used for storing and retrieving an artist's
     * name and information from the database.
     */
    private final int artistID;
    /**
     * The release year of a {@link MediaItem}.
     */
    private final int year;
    /**
     * The title of a {@link MediaItem}.
     */
    private final String title;
    /**
     * The name of the artist of a {@link MediaItem}.
     */
    private final String artist;
    /**
     * The liked status of a {@link MediaItem}.
     */
    private final boolean liked;

    /**
     * Initializes an immutable {@link MediaItem} with data common to both {@link Song} and
     * {@link SongCollection}, including ID, artist information, title, release year, and liked status.
     *
     * @param id the unique ID of the item.
     * @param artistID the unique ID of the item's artist.
     * @param title the title of the item.
     * @param year the release year of the item.
     * @param artist the name of the item's artist.
     * @param liked the liked status of the item.
     */
    protected MediaItem(int id, int artistID, String title, int year, String artist, boolean liked){
        this.id = id; this.artistID = artistID; this.title = title;
        this.year = year; this.artist = artist; this.liked = liked;
    }

    /**
     * Returns the unique ID of this {@link MediaItem}.
     *
     * @return the unique ID.
     */
    public final int getID() {return this.id;}
    /**
     * Returns the unique artist ID of this {@link MediaItem}.
     * 
     * @return the unique artist ID.
     */
    public final int getArtistID() {return this.artistID;}
    /**
     * Returns the artist of this {@link MediaItem}.
     * 
     * @return the name of the artist.
     */
    public final String getArtist() {return this.artist;}
    /**
     * Returns the title of this {@link MediaItem}.
     * 
     * @return the title.
     */
    public final String getTitle() {return this.title;}
    /**
     * Returns the year of this {@link MediaItem}.
     * 
     * @return the year.
     */
    public final int getYear() {return this.year;}
    /**
     * Returns the liked status of this {@link MediaItem}.
     * 
     * @return the liked status.
     */
    public final boolean isLiked() {return this.liked;}

    /**
     * Creates a copy of this {@link MediaItem} with the liked status toggled.
     *
     * @return a new {@link MediaItem} with the liked status reversed.
     */
    public abstract MediaItem toggleLike();
}
