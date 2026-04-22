package beatbinder.persistence.sqlite;

import beatbinder.objects.CollType;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.ICollectionPersistence;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static beatbinder.persistence.sqlite.SQLConstants.*;

public class SongCollectionDB implements ICollectionPersistence{

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }


    private Connection connection;

    public SongCollectionDB(Connection connection) {
        this.connection = connection;
    }


    public List<Integer> setSongPosition(int collectionID, int songID, int newPosition) {

        String sql = "SELECT song_id AS id FROM collection_songs WHERE collection_id = ? ORDER BY position";
        try {
            // Get the current position of the song
            int currentPosition = getCurrentSongPosition(collectionID, songID);
            if (currentPosition != newPosition) {
                // Update positions
                updateSongPositions(collectionID, songID, currentPosition, newPosition);
            }

            // Return the list of song IDs
            List<Integer> songIDs = fetchSongIDs(sql, collectionID);
            return songIDs;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update song position", e);
        }
    }


    public SongCollection createCollection(SongCollection coll) {
        String sql = """
            INSERT INTO collections (type, artist_id, title, year, liked)
            VALUES (?, ?, ?, ?, ?)
            """;
            
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, coll.getType().name());
        pstmt.setInt(2, coll.getArtistID());
        pstmt.setString(3, coll.getTitle());
        pstmt.setInt(4, coll.getYear());
        pstmt.setInt(5, coll.isLiked() ? 1 : 0);

        pstmt.executeUpdate();
        
        // Get the generated ID using last_insert_rowid()
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) {
                int generatedID = rs.getInt(1);
                return new SongCollection(generatedID,
                        coll.getArtistID(),
                        coll.getTitle(),
                        coll.getYear(),
                        coll.getArtist(),
                        coll.getType(),
                        coll.isLiked()
                );
            } else {
                throw new RuntimeException("Failed to get generated ID");
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to create collection", e);
    }
}


    public SongCollection deleteCollection(SongCollection coll) {
        String sql = """
            DELETE FROM collections
            WHERE id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, coll.getID());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return coll; // Return the deleted collection object
            } else {
                throw new RuntimeException("Collection not found or already deleted");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete collection", e);
        }
    }


    public SongCollection updateCollection(SongCollection coll) {
        String sql = """
            UPDATE collections
            SET
                title = ?,
                liked = ?
            WHERE id = ?;
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, coll.getTitle());
            pstmt.setInt(2, coll.isLiked() ? 1 : 0);
            pstmt.setInt(3, coll.getID());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return coll; // Return the updated collection object
            } else {
                throw new RuntimeException("Collection not found or not updated");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update collection", e);
        }
    }


    public List<SongCollection> getAllCollections() {
        List<SongCollection> collections = new ArrayList<>();  // Initialize the list
        String sql = SELECT_COLLECTION + " "
                + "FROM collections c "
                + ARTIST_JOIN_COLLECTION;

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next()) {
                collections.add(makeCollection(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving songs", e);
        }
        return collections;
    }


    public List<Integer> getSongIdsByCollection(int collectionID) {

        String sql = "SELECT song_id AS id " +
                "FROM collection_songs " +
                "WHERE collection_id = ? " +
                "ORDER BY position";

        return fetchSongIDs(sql, collectionID);
    }



    public List<Integer> getSongIdsByArtist(int artistID) {
        String sql = "SELECT s.id FROM songs s "
                + "WHERE s.artist_id = ? ";

        return fetchSongIDs(sql, artistID);
    }


    public List<Integer> getSongIdsByGenre(int genreID) {
        String sql = "SELECT s.id FROM songs s "
                + "WHERE s.genre_id = ? ";

        return fetchSongIDs(sql, genreID);
    }


    public List<Integer> addSongToCollection(int collectionID, int songID) {
        String positionQuery = """
            SELECT COALESCE(MAX(position), 0) + 1 AS next_position
            FROM collection_songs
            WHERE collection_id = ?
            """;

        String insertSongQuery = """
            INSERT INTO collection_songs (collection_id, song_id, position)
            VALUES (?, ?, ?)
            """;

        String getAllSongsQuery = """
            SELECT song_id 
            FROM collection_songs 
            WHERE collection_id = ? 
            ORDER BY position
            """;

        try (PreparedStatement positionStmt = connection.prepareStatement(positionQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertSongQuery);
             PreparedStatement getAllSongsStmt = connection.prepareStatement(getAllSongsQuery)) {

            // Get next position
            positionStmt.setInt(1, collectionID);
            try (ResultSet rs = positionStmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Failed to determine position for the song");
                }
                int nextPosition = rs.getInt("next_position");

                // Insert the new song
                insertStmt.setInt(1, collectionID);
                insertStmt.setInt(2, songID);
                insertStmt.setInt(3, nextPosition);
                insertStmt.executeUpdate();

                // Get all songs in the collection
                getAllSongsStmt.setInt(1, collectionID);
                List<Integer> songIds = new ArrayList<>();
                try (ResultSet songsRs = getAllSongsStmt.executeQuery()) {
                    while (songsRs.next()) {
                        songIds.add(songsRs.getInt("song_id"));
                    }
                }
            
            return Collections.unmodifiableList(songIds);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Failed to add song to collection", e);
    }
}


    public List<Integer> deleteSongFromCollection(int collectionID, int songID) {
        String deleteQuery = """
            DELETE FROM collection_songs
            WHERE collection_id = ? AND song_id = ?
            """;

        String adjustPositionsQuery = """
            UPDATE collection_songs
            SET position = position - 1
            WHERE collection_id = ? AND position > (
                SELECT position FROM collection_songs
                WHERE collection_id = ? AND song_id = ?
            )
            """;

        String getRemainingQuery = """
            SELECT song_id 
            FROM collection_songs 
            WHERE collection_id = ? 
            ORDER BY position
            """;

        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery);
             PreparedStatement adjustStmt = connection.prepareStatement(adjustPositionsQuery);
             PreparedStatement getRemainingStmt = connection.prepareStatement(getRemainingQuery)) {

            // Remove song from the collection
            deleteStmt.setInt(1, collectionID);
            deleteStmt.setInt(2, songID);
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                // Adjust positions of remaining songs
                adjustStmt.setInt(1, collectionID);
                adjustStmt.setInt(2, collectionID);
                adjustStmt.setInt(3, songID);
                adjustStmt.executeUpdate();

                // Get remaining songs in the collection
                getRemainingStmt.setInt(1, collectionID);
                List<Integer> songIds = new ArrayList<>();
                try (ResultSet rs = getRemainingStmt.executeQuery()) {
                    while (rs.next()) {
                        songIds.add(rs.getInt("song_id"));
                    }
                }
            
            return Collections.unmodifiableList(songIds);
        } else {
            throw new RuntimeException("Failed to delete song from collection");
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error while deleting song from collection", e);
    }
}



    // Helper function that returns a list of song ids.
    // Takes int paramaters for SQL statements. Mainly ids of artists, genres...
    private List<Integer> fetchSongIDs(String sql, int... params) {
        List<Integer> songIDs = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                statement.setInt(i + 1, params[i]);
            }

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int songId = rs.getInt("id");
                    songIDs.add(songId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving songs", e);
        }
        return songIDs;
    }

    // Helper function that creates a songCollection object which should be a playlist and returns it
    private SongCollection makeCollection(ResultSet rs) throws SQLException {

        int collectionId = rs.getInt("id");
        String type = rs.getString("type");
        int artistId = rs.getInt("artist_id");
        String title = rs.getString("title");
        int year = rs.getInt("year"); // Can be null
        boolean liked = rs.getInt("liked") == 1;
        String artistName = rs.getString("artist_name");

        // Create and return the SongCollection object
        return new SongCollection(
                collectionId,
                artistId,
                title,
                year,
                artistName,
                CollType.valueOf(type.toUpperCase()), // Convert type to CollType enum
                liked
        );
    }

    // Helper function for getting the initial position of a song when changing playlist order
    private int getCurrentSongPosition(int collectionID, int songID) throws SQLException {

        String query = "SELECT position FROM collection_songs WHERE collection_id = ? AND song_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, collectionID);
            stmt.setInt(2, songID);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt("position");
                } else {
                    throw new IllegalArgumentException("Song not found in the collection.");
                }
            }
        }
    }

    // Helper function for changing playlist song order
    private void updateSongPositions(int collectionID, int songID, int currentPosition, int newPosition) throws SQLException {
        String query = """
        UPDATE collection_songs 
        SET position = CASE
            WHEN song_id = ? THEN ?
            WHEN position > ? AND position <= ? THEN position - 1
            WHEN position >= ? AND position < ? THEN position + 1
            ELSE position
        END
        WHERE collection_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, songID);
            stmt.setInt(2, newPosition);

            if (newPosition > currentPosition) {
                // Moving down
                stmt.setInt(3, currentPosition);
                stmt.setInt(4, newPosition);
                stmt.setInt(5, 0);  // Unused for this case
                stmt.setInt(6, 0);  // Unused for this case
            } else {
                // Moving up
                stmt.setInt(3, 0);  // Unused for this case
                stmt.setInt(4, 0);  // Unused for this case
                stmt.setInt(5, newPosition);
                stmt.setInt(6, currentPosition);
            }

            stmt.setInt(7, collectionID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update song position", e);
        }
    }

}