package beatbinder.persistence.sqlite;

public class SQLConstants {

    // Frequent Song query components
    public static final String SELECT_SONG = "SELECT s.*, a.name AS artist_name, g.name AS genre_name";
    public static final String ARTIST_JOIN_SONGS = "JOIN artists a ON s.artist_id = a.id";
    public static final String GENRE_JOIN_SONGS = "JOIN genres g ON s.genre_id = g.id";
    // Base start for every get song query
    public static final String SELECT_SONG_BASE =
            SELECT_SONG + " "
            + "FROM songs s" + " "
            + ARTIST_JOIN_SONGS + " "
            + GENRE_JOIN_SONGS;

    public static final String SELECT_COLLECTION = "SELECT c.*, a.name AS artist_name";
    public static final String ARTIST_JOIN_COLLECTION = "JOIN artists a ON c.artist_id = a.id";
}
