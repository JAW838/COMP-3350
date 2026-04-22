package beatbinder.exceptions;

/**
 * Thrown when a {@link Tag} with the same {@code name} as a pre-existing tag is created.
 * 
 * This may occur when attempting to create a {@link Tag}.
 */
public class DuplicateTagNameException extends RuntimeException{
    public DuplicateTagNameException(String message) {
        super(message);
    }
}
