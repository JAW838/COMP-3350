package beatbinder.exceptions;

/**
 * Thrown when a requested tag already exists.
 * 
 * This may occur when attempting to create or add a tag that already exists in
 * the underlying data store.
 */
public class DuplicateTagException extends RuntimeException{
    public DuplicateTagException(String message) {
        super(message);
    }
}
