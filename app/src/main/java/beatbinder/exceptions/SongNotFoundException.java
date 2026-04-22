package beatbinder.exceptions;

/**
 * Thrown when a requested song cannot be found.
 * 
 * This may occur when attempting to access or modify a song that does not exist in
 * the underlying data store.
 */
public class SongNotFoundException extends RuntimeException{
    public SongNotFoundException(String message) {
        super(message);
    }
}
