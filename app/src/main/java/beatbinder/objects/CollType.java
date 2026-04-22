package beatbinder.objects;

/**
 * Represents the type of a {@link SongCollection}, indicating whether it can be modified.
 *
 * @see SongCollection
 */

public enum CollType {
    /**
     * A {@link SongCollection} that cannot be modified.
     */
    ALBUM,

    /**
     * A {@link SongCollection} that can be modified.
     */
    PLAYLIST
}
