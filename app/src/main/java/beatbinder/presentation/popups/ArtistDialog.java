package beatbinder.presentation.popups;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.presentation.components.SongPanel;
import beatbinder.presentation.components.SongPanelContext;
import beatbinder.presentation.components.SortButtonFactory;
import beatbinder.presentation.text.content.DefaultsTexter;

/**
 * Window displaying all songs made by an artist.
 */
public class ArtistDialog extends SongListDialog {
    private final CollectionManager collectionManager;
    private final Song song;

    /**
     * Create a popup displaying all songs made by the artist of a given
     * {@link Song}.
     * 
     * @param song              the {@link Song} with the artist to display.
     * @param songManager       the {@link SongManager} used to get {@link Song}
     *                          information.
     * @param collectionManager the {@link CollectionManager} to get
     *                          {@link SongCollection}
     *                          information.
     * @param tagManager        the {@link TagManager} used to get {@link Tag}
     *                          information.
     */
    public ArtistDialog(Song song,
            SongManager songManager,
            CollectionManager collectionManager,
            TagManager tagManager) {
        super(songManager, tagManager);
        this.song = song;
        this.collectionManager = collectionManager;

        setTitle(song.getArtist());
        initializeUI();
    }

    @Override
    protected JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel artistLabel = new JLabel(song.getArtist(), SwingConstants.CENTER);
        artistLabel.setFont(new Font(DefaultsTexter.defaultFont(), Font.BOLD, 20));
        panel.add(artistLabel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected JPanel createBottomPanel() {
        return null; // no bottom buttons needed for artist view
    }

    @Override
    protected void refreshSongListContents() {
        songListPanel.removeAll();
        List<Song> songs = songManager.getAllSongsByArtist(song.getArtistID());
        songs = songManager.sortSongs(songs, sortOption);
        for (Song song : songs) {
            songListPanel.add(new SongPanel(song, songManager, collectionManager, tagManager,
                    this::refreshSongListPanel, SongPanelContext.ARTIST_VIEW));
        }

    }

    @Override
    protected JButton getSortButton() {
        return SortButtonFactory.createSortMenuButton(selectedOption -> {
            this.sortOption = selectedOption;
            refreshSongListPanel();
        });
    }
}