package beatbinder.presentation.components;

/**
 * Defines sorting options for sorting {@link Song} instances.
 * Each option has a user-friendly display name used in UI components.
 */
public enum SortOption {
    DEFAULT("Default"),
    TITLE("Title"),
    ARTIST("Artist"),
    RUNTIME("Runtime");

    private final String displayName;

    /**
     * Called by Java automatically when the enum is initialised.
     * 
     * @param displayName the user-readable name of an enum.
     */
    SortOption(String displayName) {
        this.displayName = displayName;
    }
    /**
     * Retrieves the display name associated with a {@link SortOption} value.
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}