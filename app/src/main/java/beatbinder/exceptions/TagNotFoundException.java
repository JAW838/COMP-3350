package beatbinder.exceptions;

/**
 * Thrown when a requested tag cannot be found.
 * 
 * This may occur when attempting to access or modify a tag that does not exist in
 * the underlying data store.
 */
public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(String message) {
        super(message);
    }
}
