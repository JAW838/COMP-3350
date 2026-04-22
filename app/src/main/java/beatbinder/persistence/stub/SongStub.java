package beatbinder.persistence.stub;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import beatbinder.objects.Song;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.persistence.ISongPersistence;

public class SongStub implements ISongPersistence{
    private HashMap<Integer, Song> songs; // songID:song
    private HashMap<Integer, String> artists; // artistID:artist
    private HashMap<Integer, String> genres; // genreID:genre

    public SongStub(HashMap<Integer, Song> songs, HashMap<Integer, String> artists,
        HashMap<Integer, String> genres) {

        this.songs = songs;
        this.artists = artists;
        this.genres = genres;
    }

    // Take an ID, return the associated song
    @Override
    public Song getSongByID(int id) {
        if (songs.containsKey(id)) {
            return fillSong(songs.get(id));
        }
        else {
            throw new SongNotFoundException("Song with ID " + id + " not found.");
        }
    }

    @Override
    public Song updateSong(Song song) {
        if (songs.containsKey(song.getID())) {
            songs.put(song.getID(), song);
            return fillSong(songs.get(song.getID()));
        }
        else {
            throw new SongNotFoundException("Song with ID " + song.getID() + " not found.");
        }
    }

    @Override
    public List<Song> getCollectionSongs(int[] ids) {
        List<Song> foundSongs = new ArrayList<Song>(ids.length);

        for (int i = 0; i < ids.length; i++) {
            if (songs.containsKey(ids[i])) {
                foundSongs.add(songs.get(ids[i]));
            }
        }

        return Collections.unmodifiableList(foundSongs);
    }

    @Override
    public List<Song> getAllSongs() {
        List<Song> allSongs = new ArrayList<Song>(songs.size());

        for (Song song : songs.values()) {
            allSongs.add(fillSong(song));
        }

        return Collections.unmodifiableList(allSongs);
    }

    // Some songs are stored without their genre/artist
    // This method fills in the missing data.
    private Song fillSong(Song song) {
        String artist = null;
        String genre = null;
        if (song.getArtist() == null) {
            artist = artists.get(song.getArtistID());
        }
        if (song.getGenre() == null) {
            genre = genres.get(song.getGenreID());
        }
        if (artist != null || genre != null) {
            song = new Song(song.getID(), song.getArtistID(), song.getTitle(), song.getYear(),
                    artist, song.getGenreID(), genre, song.getNote(), song.getRuntime());
        }
        return song;
    }
}
