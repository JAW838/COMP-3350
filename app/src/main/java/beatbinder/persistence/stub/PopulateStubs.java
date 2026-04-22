package beatbinder.persistence.stub;

import java.util.HashMap;

import beatbinder.objects.SongCollection;
import beatbinder.objects.Tag;
import beatbinder.objects.CollType;
import beatbinder.objects.Song;

public class PopulateStubs {
    public static void makeStubs(HashMap<Integer, Song> songs, HashMap<Integer, String> artists,
        HashMap<Integer, String> genres, HashMap<Integer, Integer[]> collectionEntries,
        HashMap<Integer, SongCollection> collectionMap, HashMap<Integer, Integer[]> artistEntries,
        HashMap<Integer, Integer[]> genreEntries, HashMap<Integer, Tag> tags,
        HashMap<Integer, Integer[]> tagEntities) {

            makeArtists(artists);
            makeGenres(genres);
            makeSongs(songs);
            makeCollections(collectionMap);
            makeArtistSongs(artistEntries);
            makeGenreSongs(genreEntries);
            putSongsInCollections(collectionEntries);
    }

    private static void makeArtists(HashMap<Integer, String> artists) {
        artists.put(0,"You");
        artists.put(1, "BeatBinder");
        artists.put(2, "Gwen Stefani");
        artists.put(3, "Bruno Mars");
        artists.put(4, "Taylor Swift");
        artists.put(5, "QUEEN");
        artists.put(6, "Hoshimachi Suisei");
        artists.put(7, "Michael Jackson");
    }

    private static void makeGenres(HashMap<Integer, String> genres) {
        genres.put(0, "Pop"); // swift
        genres.put(1, "J-Pop"); // suisei
        genres.put(2, "Rock"); // bohemian rhapsody
        genres.put(3, "Funk"); //uptown funk
    }

    private static void makeSongs(HashMap<Integer, Song> songs) {
        Song sa = new Song(0, 6, "Stellar Stellar", 2021, 1, "note", 230);
        Song sb = new Song(1, 5, "Bohemian Rhapsody", 1975, 2, "note", 356);
        Song sc = new Song(2, 2, "Hollaback Girl", 2004, 0, "note", 211);
        Song sd = new Song(3, 3, "Uptown Funk", 2014, 3, "note", 271);
        Song se = new Song(4, 4, "Shake If Off", 2014, 0, "note", 242);

        songs.put(sa.getID(), sa);
        songs.put(sb.getID(), sb);
        songs.put(sc.getID(), sc);
        songs.put(sd.getID(), sd);
        songs.put(se.getID(), se);
    }

    private static void makeCollections(HashMap<Integer, SongCollection> collectionMap) {

        SongCollection colla = new SongCollection(0, 6, "Still Sill Stellar", 2021, CollType.ALBUM, false);
        SongCollection collb = new SongCollection(1, 5, "Bohemian Rhapsody", 1975, CollType.ALBUM, false);
        SongCollection collc = new SongCollection(2, 0, "Favourites", 2025, CollType.PLAYLIST, false);
        SongCollection colld = new SongCollection(3, 1, "Certified Bangers", 2025, CollType.PLAYLIST, false);
        // full disclosure I've never seriously listened to MJ -Jonas
        SongCollection colle = new SongCollection(4, 7, "Thriller", 1982, CollType.ALBUM, false);
        SongCollection collf = new SongCollection(5, 7, "Off The Wall", 1979, CollType.ALBUM, false);


        collectionMap.put(colla.getID(), colla);
        collectionMap.put(collb.getID(), collb);
        collectionMap.put(collc.getID(), collc);
        collectionMap.put(colld.getID(), colld);
        collectionMap.put(colle.getID(), colle);
        collectionMap.put(collf.getID(), collf);

    }

    // artistID:songID
    private static void makeArtistSongs(HashMap<Integer, Integer[]> artistEntries) {
        artistEntries.put(0, new Integer[]{5,1});
        artistEntries.put(1, new Integer[]{6,0});
        artistEntries.put(2, new Integer[]{2,2});
        artistEntries.put(3, new Integer[]{3,3});
        artistEntries.put(4, new Integer[]{4,4});
    }

    // genreID:songID
    private static void makeGenreSongs(HashMap<Integer, Integer[]> genreEntries) {
        genreEntries.put(0, new Integer[]{0,2});
        genreEntries.put(1, new Integer[]{0,4});
        genreEntries.put(2, new Integer[]{1,0});
        genreEntries.put(3, new Integer[]{2,1});
        genreEntries.put(4, new Integer[]{3,3});
    }

    // collectionID:songID:position
    private static void putSongsInCollections(HashMap<Integer, Integer[]> collectionEntries) {
        collectionEntries.put(0, new Integer[]{0,0,1});
        collectionEntries.put(1, new Integer[]{1,1,1});
        collectionEntries.put(2, new Integer[]{2,0,1});
        collectionEntries.put(3, new Integer[]{2,3,2});
        collectionEntries.put(4, new Integer[]{3,0,1});
        collectionEntries.put(5, new Integer[]{3,1,2});
        collectionEntries.put(6, new Integer[]{3,2,3});
        collectionEntries.put(7, new Integer[]{3,3,4});
    }
}
