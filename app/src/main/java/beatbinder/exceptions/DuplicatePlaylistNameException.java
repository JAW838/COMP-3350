package beatbinder.exceptions;

public class DuplicatePlaylistNameException extends RuntimeException {
    public DuplicatePlaylistNameException(String message) {
        super(message);
    }
}
