package beatbinder.logic.validation;

import java.util.List;

import beatbinder.objects.CollType;
import beatbinder.objects.SongCollection;

/**
 * Validates {@link SongCollection} instances.
 * 
 * @see SongCollection
 */
public class SongCollectionValidator extends MediaItemValidator {

    /**
     * Ensures a given {@link SongCollection} is valid (not null and contains valid fields).
     * 
     * @param collection the {@link SongCollection} to validate
     * @throws IllegalArgumentException if {@code collection} fails validation.
     */
    public void validateColl(SongCollection collection) {
        validateMedia(collection);
        coreTests(collection);
    }

    /**
     * Validates a given list of {@link SongCollections}, ensuring they are not null and contain
     * valid data.
     * 
     * @param collections the {@link SongCollection} instances to validate.
     * @throws IllegalArgumentException if an instance is invalid.
     */
    public void validateColls(List<SongCollection> collections) {
        for (SongCollection songCollection : collections) {
            validateColl(songCollection);
        }
    }

    /**
     * Validates a {@link SongCollection} instance with a core test suite.
     * 
     * @param collection the {@link SongCollection} to be validated.
     * @throws IllegalArgumentException if validation is failed.
     */
    private void coreTests(SongCollection collection) {
        isNull(collection);
        validateType(collection.getType());
    }

    /**
     * Ensures a given {@link SongCollection} is not null.
     * 
     * @param collection the {@link SongCollection} to be validated.
     * @throws IllegalArgumentException if {@code collection} fails validation.
     */
    private void isNull(SongCollection collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection cannot be null.");
        }
    }

    /**
     * Ensures a given {@link SongCollection} has a valid {@link CollType}.
     * 
     * @param type the {@link CollType} to be validated.
     * @throws IllegalArgumentException if validation is failed.
     */
    private void validateType(CollType type) {
        if (type == null) {
            throw new IllegalArgumentException("Collection type cannot be null.");
        }
    }
}
