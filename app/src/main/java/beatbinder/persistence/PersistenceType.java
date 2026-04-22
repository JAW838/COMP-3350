package beatbinder.persistence;

/**
 * Represents the type of database the project should use.
 */
public enum PersistenceType {
    /**
     * The full SQLite database with minimal seeding.
     */
    PROD,
    /**
     * The SQLite database with seeding.
     */
    TEST,
    /**
     * Native java database implementation with seeding.
     */
    STUB
}
