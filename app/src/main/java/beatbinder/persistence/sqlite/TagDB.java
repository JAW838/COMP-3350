package beatbinder.persistence.sqlite;


import beatbinder.exceptions.DuplicateTagException;
import beatbinder.objects.Tag;
import beatbinder.persistence.ITagPersistence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagDB implements ITagPersistence {

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }


    private Connection connection;

    public TagDB(Connection connection) {
        this.connection = connection;
    }


    public Tag createTag(Tag tag) {
        String sql = """
                INSERT INTO tags (name)
                VALUES (?)
                """;

        try(PreparedStatement pstmt = connection.prepareStatement(sql);) {
            pstmt.setString(1, tag.getName());

            pstmt.executeUpdate();

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int generatedID = rs.getInt(1);
                    return new Tag(generatedID,
                            tag.getName()
                    );
                } else {
                    throw new RuntimeException("Failed to get generated ID");
                }
            }

        } catch(SQLException e) {
            throw new DuplicateTagException("Failed to create tag");
        }
    }


    public Tag updateTag(Tag tag) {
        String sql = """
                UPDATE tags
                SET name = ?
                WHERE id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, tag.getName());
            pstmt.setInt(2, tag.getID());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return tag;
            } else {
                throw new RuntimeException("Tag not found or not updated");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update tag", e);
        }
    }


    public Tag deleteTag(Tag tag) {
        String sql = """
                DELETE FROM tags
                WHERE id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tag.getID());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return tag;
            } else {
                throw new RuntimeException("Tag not found or already deleted");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag", e);
        }
    }


    public Tag getTagByID(int id) {
        String sql = """
            SELECT id, name FROM tags
            WHERE id = ?
            """;


        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);  // <-- Set the parameter here

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return makeTag(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get tag", e);
        }

        return null; // Return null if no tag was found
    }

    public List<Tag> getTagsOfSong(int songID) {
        String selectSql = """
        SELECT t.id, t.name FROM tags t
        JOIN song_tags st ON t.id = st.tag_id
        WHERE st.song_id = ?
        """;

        try (PreparedStatement selectStmt = connection.prepareStatement(selectSql)) {

            selectStmt.setInt(1, songID);
            try (ResultSet rs = selectStmt.executeQuery()) {
                List<Tag> tags = new ArrayList<>();
                while (rs.next()) {
                    tags.add(makeTag(rs));
                }
                return tags;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve tags for song", e);
        }
    }




    public List<Tag> addTagToSong(int tagID, int songID) {
        String insertSql = """
        INSERT INTO song_tags (tag_id, song_id)
        VALUES (?, ?)
        """;

        try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
            insertStmt.setInt(1, tagID);
            insertStmt.setInt(2, songID);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new DuplicateTagException("Failed to add tag to song");
        }

        return getTagsOfSong(songID);
    }

    public List<Tag> deleteTagFromSong(int tagID, int songID) {
        String deleteSql = """
        DELETE FROM song_tags
        WHERE tag_id = ? AND song_id = ?
        """;

        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql)) {

            deleteStmt.setInt(1, tagID);
            deleteStmt.setInt(2, songID);
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag from song", e);
        }

        return getTagsOfSong(songID);
    }



    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT id, name FROM tags";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tags.add(makeTag(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving tags", e);
        }
        return tags;
    }

    public List<Integer> getSongIdsByTag(int tagID) {
        List<Integer> songIDs = new ArrayList<>();
        String sql = """
                SELECT song_id FROM song_tags
                WHERE tag_id = ?
                """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tagID);

            try(ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()){
                    songIDs.add(rs.getInt("song_id"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get song IDs", e);
        }

        return songIDs;
    }




    private Tag makeTag(ResultSet rs) throws SQLException {
        return new Tag(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
