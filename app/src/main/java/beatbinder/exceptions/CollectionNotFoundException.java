package beatbinder.exceptions;

/**
 * Thrown when a requested collection (e.g., album or playlist) cannot be found.
 * 
 * This may occur when attempting to access or modify a collection that does not exist in
 * the underlying data store.
 */
public class CollectionNotFoundException extends RuntimeException {
    public CollectionNotFoundException(String message) {
        super(message);
    }
}
