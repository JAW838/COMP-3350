package beatbinder.exceptions;

/**
 * Thrown when a requested collection (e.g., album or playlist) already exists.
 * 
 * This may occur when attempting to create or modify a collection that already exists in
 * the underlying data store.
 */
public class DuplicateCollectionException extends RuntimeException{
    public DuplicateCollectionException(String message) {
        super(message);
    }
}
