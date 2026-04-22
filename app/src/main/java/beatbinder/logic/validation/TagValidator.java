package beatbinder.logic.validation;

import java.util.List;
import beatbinder.objects.Tag;

/**
 * Class for validating {@link Tag} instances, including ID and name.
 * 
 * @see Tag
 */
public class TagValidator {
    /**
     * Validate a list of given {@link Tag} instances.
     * 
     * @param tags the {@link Tag} instances to be validated.
     * @throws IllegalArgumentException if any {@link Tag} is invalid.
     */
    public void validateTags(List<Tag> tags) {
        for (Tag tag : tags) {
            validateTag(tag);
        }
    }

    /**
     * Validates a given {@link Tag}.
     * 
     * @param tag the {@link Tag} to be validated.
     * @throws IllegalArgumentException if {@code tag} is invalid.
     */
    public void validateTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null.");
        }
        validateID(tag.getID());
        validateName(tag.getName());
    }

    /**
     * Ensures the {@code id} of a {@link Tag} is structurally valid (though not necessarily 
     * present in the database).
     * 
     * @param id the ID to be validated.
     * @throws IllegalArgumentException if {@code id} is invalid.
     */
    public void validateID(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Tag cannot have ID "+id+" (ID < 0)");
        }
    }

    /**
     * Validates the {@code name} of a {@link Tag}.
     * 
     * @param name the name to be validated.
     * @throws IllegalArgumentException if {@code name} is invalid.
     */
    public void validateName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tag cannot have empty name.");
        }
    }
}
