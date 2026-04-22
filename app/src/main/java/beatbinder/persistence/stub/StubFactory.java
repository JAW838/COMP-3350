package beatbinder.persistence.stub;

import java.util.HashMap;
import beatbinder.objects.SongCollection;
import beatbinder.objects.Tag;
import beatbinder.objects.Song;

public class StubFactory {
    private static HashMap<Integer, Song> songs = null; // songID:song
    private static HashMap<Integer, String> artists = null; // artistID:artist
    private static HashMap<Integer, String> genres = null; // genreID:genre

    private static HashMap<Integer, Integer[]> collectionEntries = null; // id:[collectionID,songID,position]
    private static HashMap<Integer, SongCollection> collectionMap = null; // collectionID:collection
    private static HashMap<Integer, Integer[]> artistEntries = null; // id:[artistID,songID]
    private static HashMap<Integer, Integer[]> genreEntries = null; // id:[genreID,songID]

    private static HashMap<Integer, Tag> tags = null;
    private static HashMap<Integer, Integer[]> tagEntities = null;

    public static SongStub createSongPersistence() {
        confirmCreation();
        return new SongStub(songs, artists, genres);
    }

    public static CollectionStub createCollectionPersistence() {
        confirmCreation();
        return new CollectionStub(collectionEntries, artists, collectionMap, artistEntries, genreEntries);
    }

    public static TagStub createTagPersistence() {
        confirmCreation();
        return new TagStub(tags, tagEntities);
    }

    private static void confirmCreation() {
        if (artists == null || songs == null || genres == null || collectionEntries == null ||
            collectionMap == null || artistEntries == null || genreEntries == null) {
            // Initialize all HashMaps before calling makeStubs
            songs = new HashMap<>();
            artists = new HashMap<>();
            genres = new HashMap<>();
            collectionEntries = new HashMap<>();
            collectionMap = new HashMap<>();
            artistEntries = new HashMap<>();
            genreEntries = new HashMap<>();
            tags = new HashMap<>();
            tagEntities = new HashMap<>();

            PopulateStubs.makeStubs(songs, artists, genres, collectionEntries, collectionMap, artistEntries, genreEntries, tags, tagEntities);
        }
    }
}
