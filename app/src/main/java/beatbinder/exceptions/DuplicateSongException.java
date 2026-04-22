package beatbinder.exceptions;

/**
 * Thrown when a requested song already exists.
 * 
 * This may occur when attempting to create or add a song that already exists in
 * the underlying data store.
 */
public class DuplicateSongException extends RuntimeException{
    public DuplicateSongException(String message) {
        super(message);
    }
}
