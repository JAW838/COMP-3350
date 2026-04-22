package beatbinder.persistence.sqlite;

import beatbinder.objects.Song;
import beatbinder.persistence.ISongPersistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static beatbinder.persistence.sqlite.SQLConstants.*;

public class SongDB implements ISongPersistence {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }


    private Connection connection;

    public SongDB(Connection connection) {
        this.connection = connection;
    }

    public Song updateSong(Song song) {
        String sql = """
            UPDATE songs
            SET
                artist_id = ?,
                genre_id = ?,
                title = ?,
                year = ?,
                duration = ?,
                liked = ?,
                notes = ?
            WHERE id = ?;
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, song.getArtistID());
            pstmt.setInt(2, song.getGenreID());
            pstmt.setString(3, song.getTitle());
            pstmt.setInt(4, song.getYear());
            pstmt.setInt(5, song.getRuntime());
            pstmt.setInt(6, song.isLiked() ? 1 : 0); // Convert boolean to int
            pstmt.setString(7, song.getNote());
            pstmt.setInt(8, song.getID()); // The song ID to update

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating song", e);
        }
        return song;
    }


    public Song getSongByID(int songID) {
        Song song = null;
        String sql = SELECT_SONG_BASE + " "// select all song fields + artist and genre names from song join artist and genre
                + "WHERE s.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, songID);
            ResultSet rs = statement.executeQuery();

            while(rs.next()) {
                song = makeSong(rs);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving song", e);
        }

        return song;
    }


    public List<Song> getSongsByCollectionID(int collectionID) {
        List<Song> songs = new ArrayList<>();  // Initialize the list
        String sql = SELECT_SONG + ", cs.position "
                + "FROM songs s "
                + ARTIST_JOIN_SONGS + " "
                + GENRE_JOIN_SONGS + " "
                + "JOIN collection_songs cs ON s.id = cs.song_id "
                + "WHERE cs.collection_id = ? "
                + "ORDER BY cs.position ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1, collectionID);
            try (ResultSet rs = statement.executeQuery()) {
                while(rs.next()) {
                    songs.add(makeSong(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving songs", e);
        }

        return songs;
    }


    // Get many songs by their id's
    public List<Song> getCollectionSongs(int[] IDs) {
        List<Song> songs = new ArrayList<>();  // Initialize the list

        for (int id : IDs) {
            songs.add(getSongByID(id));
        }
        return songs;
    }


    public List<Song> getAllSongs() {
        return fetchSongs(SELECT_SONG_BASE);
    }

    public List<Song> getLikedSongs() {
        String sql = SELECT_SONG_BASE + " "
                + "WHERE liked = 1 ";

        return fetchSongs(sql);
    }


    private List<Song> fetchSongs(String sql) {
        List<Song> songs = new ArrayList<>();  // Initialize the list here
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while(rs.next()) {
                songs.add(makeSong(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving songs", e);
        }
        return songs;
    }


    private Song makeSong(ResultSet rs) throws SQLException {
        Song song = null;

        int songId = rs.getInt("id");
        int artistId = rs.getInt("artist_id");
        int genreId = rs.getInt("genre_id");
        String title = rs.getString("title");
        int year = rs.getInt("year");
        int duration = rs.getInt("duration");
        boolean liked = rs.getInt("liked") == 1;
        String notes = rs.getString("notes");
        String artistName = rs.getString("artist_name");
        String genreName = rs.getString("genre_name");

        song = new Song(songId, artistId, title, year, artistName, genreId, genreName, notes, duration);
        if (liked) {
            song = song.toggleLike();
        }
        return song;
    }


}