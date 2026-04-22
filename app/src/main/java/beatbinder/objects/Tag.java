package beatbinder.objects;

/**
 * Represents a {@link Tag}, a label a user can attach to a {@link Song} for easier navigation.
 * 
 * A {@link Tag} is immutable.
 * 
 * @see Song
 */
public class Tag {
    /**
     * The unique ID of a {@link Tag}, used to store and retrieve tags from the database.
     */
    private final int id;
    /**
     * The name of a {@link Tag}.
     */
    private final String name;
    /**
     * Placeholder ID so the {@link Tag} can be passed to the database for proper ID assignment.
     */
    private final int DEFAULT_ID = -1;

    /**
     * Creates a {@link Tag} instance with no valid fields.
     */
    public Tag() {
        id = -1;
        name = null;
    }

    public Tag(String name) {
        this.name = name;
        this.id = DEFAULT_ID;
    }

    /**
     * Creates a {@link Tag} instance with a given ID and name.
     * 
     * @param id the unique ID of the {@link Tag}.
     * @param name the name of the {@link Tag}.
     */
    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Creates a copy {@link Tag} with the data of {@code tag}.
     * 
     * @param tag the tag being copied.
     */
    public Tag(Tag tag) {
        this.name = tag.getName();
        this.id = tag.getID();
    }

    /**
     * Retrieves the name of this {@link Tag}.
     * 
     * @return the name of this {@link Tag}.
     */
    public String getName() {return this.name;}
    /**
     * Retrieves the unique ID of this {@link Tag}.
     * 
     * @return the unique ID of this {@link Tag}.
     */
    public int getID() {return this.id;}
}
