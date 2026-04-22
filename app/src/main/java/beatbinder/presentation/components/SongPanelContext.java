package beatbinder.presentation.components;

/**
 * Represents how a {@link Song} was accessed.
 * Determines what options are shown in the dropdown menu.
 */
public enum SongPanelContext {
    /**
     * The {@link Song} was accessed outside of a group of songs.
     */
    DEFAULT,
    /**
     * The {@link Song} was accessed from an artist page.
     */
    ARTIST_VIEW,
    /**
     * The {@link Song} was accessed from a {@link SongCollection}.
     */
    PLAYLIST_VIEW,
}
