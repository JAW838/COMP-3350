package beatbinder.logic.validation;

import java.time.Year;
import java.util.List;

import beatbinder.objects.MediaItem;

/**
 * Validates {@link MediaItem} instances, including ID, artist information, year, and title.
 * 
 * @see MediaItem
 */
class MediaItemValidator {
    /**
     * Runs all validation tests on a given {@link MediaItem}.
     * 
     * @param item the {@link MediaItem} to be validated.
     * @throws IllegalArgumentException if {@code item} fails validation.
     */
    protected void validateMedia(MediaItem item) {
        coreTests(item);
        validateArtist(item.getArtist());
    }

    /**
     * Runs all validation tests on a given list of {@link MediaItem} instances.
     * 
     * @param item the list of {@link MediaItem} instances to be validated.
     * @throws IllegalArgumentException if any instance fails validation.
     */
    protected void validateMedias(List<MediaItem> items) {
        for (MediaItem mediaItem : items) {
            validateMedia(mediaItem);
        }
    }

    /**
     * Ensures that all entries in {@code ids} are structurally valid IDs (but not necessarily present 
     * in the database).
     *
     * @param ids the list of IDs to validate.
     * @throws IllegalArgumentException if any ID fails validation.
     */
    public void validateIDs(List<Integer> ids) {
        for (Integer integer : ids) {
            validateID(integer);
        }
    }

    /**
     * Ensures a given {@code id} is structurally valid (but not necessarily present in the
     * database).
     * 
     * @param id the ID to validate.
     * @throws IllegalArgumentException if {@code id} fails validation.
     */
    public void validateID(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Song cannot have ID "+id+" (ID < 0)");
        }
    }

    /**
     * Ensures a given {@code id} is a structurally valid artist ID (but not necessarily present
     * in the database).
     * 
     * @param id the ID to validate.
     * @throws IllegalArgumentException if {@code id} is invalid.
     */
    private void validateArtistID(int id) {
        if (id < 0) {throw new IllegalArgumentException("Artist cannot have ID "+id+" (ID < 0)");}
    }

    /**
     * Ensures a given {@code year} is not too far in the future.
     * 
     * @param year the year to validate
     * @throws IllegalArgumentException if {@code year} is invalid.
     */
    private void validateYear(int year) {
        if (year > Year.now().getValue() + 1) {
            throw new IllegalArgumentException("Year cannot be "+year+" as it is too far in the future.");
        }
    }

    /**
     * Ensures a given {@code title} is valid (not {@code null} or empty)
     * 
     * @param title the title to validate
     * @throws IllegalArgumentException if the title is invalid
     */
    private void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
    }

    /**
     * Ensures a given {@code name} is valid (not empty).
     * 
     * @param name the name to validate.
     * @throws IllegalArgumentException if {@code name} is invalid.
     */
    private void validateArtist(String name) {
        if (name != null && name.equals("")) {
            throw new IllegalArgumentException("Artist name cannot be empty.");
        }
    }

    /**
     * Ensures a {@link MediaItem} is not null.
     * 
     * @param item the {@MediaItem} to validate.
     * @throws IllegalArgumentException if {@code item} is invalid.
     */
    private void validateItem(MediaItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot be null");
        }
    }

    /**
     * Runs a core test suite on a given {@link MediaItem} to validate the most important fields
     * it contains, including its ID, artist ID, year, and title.
     * 
     * @param item the {@link MediaItem} to validate.
     * @throws IllegalArgumentException if the item fails validation.
     */
    private void coreTests(MediaItem item) {
        validateItem(item);
        validateID(item.getID());
        validateArtistID(item.getArtistID());
        validateYear(item.getYear());
        validateTitle(item.getTitle());
    }
}
