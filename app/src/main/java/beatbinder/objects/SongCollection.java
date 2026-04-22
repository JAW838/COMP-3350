package beatbinder.objects;

/**
 * Represents a group of songs, a type of {@link MediaItem} with additional information about the
 * type of group.
 * 
 * A {@link SongCollection} is immutable.
 * 
 * @see MediaItem
 */
public class SongCollection extends MediaItem {
    /**
     * Represents the type of group that describes a {@link SongCollection}, dictating how they
     * can be interacted with. 
     * 
     * @see CollType
     */
    private final CollType type;

    /**
     * Creates a {@link SongCollection}, a type of {@link MediaItem}.
     * 
     * @param id the unique ID of the {@link SongCollection}.
     * @param artistID the unique artist ID.
     * @param title the title.
     * @param year the release year.
     * @param artist the artist name.
     * @param type the type of {@link SongCollection}.
     * @param liked the liked status.
     * @see CollType
     */
    public SongCollection(int id, int artistID, String title, int year,
            String artist, CollType type, boolean liked) {
        super(id, artistID, title, year, artist, liked);
        this.type = type;
    }

    /**
     * Creates a {@link SongCollection}, a type of {@link MediaItem}, without the artist name.
     * 
     * @param id the unique ID of the {@link SongCollection}.
     * @param artistID the unique artist ID.
     * @param title the title.
     * @param year the release year.
     * @param type the type of {@link SongCollection}.
     * @param liked the liked status.
     */
    public SongCollection(int id, int artistID, String title, int year,
            CollType type, boolean liked) {
        super(id, artistID, title, year, null, liked);
        this.type = type;
    }

    /**
     * Creates a copy of a given {@link SongCollection} with a modified {@code liked} field.
     * 
     * Used internally to create a modified instance.
     * 
     * @param songCollection the {@link SongCollection} to be modified.
     * @param liked the new liked status.
     */
    private SongCollection(SongCollection songCollection, boolean liked) {
        super(songCollection.getID(), songCollection.getArtistID(), songCollection.getTitle(), songCollection.getYear(), songCollection.getArtist(), liked);
        this.type = songCollection.type;
    }

    /**
     * Retrieves the {@link CollType} of this {@link SongCollection} which determines whether it can
     * be modified.
     * 
     * @return the {@link CollType}.
     */
    public final CollType getType() {return this.type;}

    /**
     * Creates a copy of this {@link SongCollection} with the liked status toggled.
     * 
     * @return a new {@link SongCollection} with the liked status toggled.
     */
    @Override
    public SongCollection toggleLike() {
        return new SongCollection(this, !this.isLiked());
    }
}
