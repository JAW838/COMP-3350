package beatbinder.presentation.popups;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.presentation.components.SongPanel;
import beatbinder.presentation.text.content.AlbumViewTexter;
import beatbinder.presentation.text.content.DefaultsTexter;

/**
 * Displays a popup showing all {@link SongCollection} instances.
 */
public class AlbumDialog extends SongListDialog {

    private SongCollection songCollection;
    private final CollectionManager collectionManager;

    /**
     * Creates a popup showing all {@link SongCollection} instances.
     * 
     * @param songCollection    the {@link SongCollection} being displayed.
     * @param collectionManager the {@link CollectionManager} used to get
     *                          {@link SongCollection}
     *                          information.
     * @param songManager       the {@link SongManager} used to get {@link Song}
     *                          information.
     * @param tagManager        the {@link TagManager} used to get {@link Tag}
     *                          information.
     */
    public AlbumDialog(SongCollection songCollection,
            CollectionManager collectionManager,
            SongManager songManager, TagManager tagManager) {
        super(songManager, tagManager);
        this.songCollection = songCollection;
        this.collectionManager = collectionManager;

        setTitle(AlbumViewTexter.panelName() + songCollection.getTitle());

        initializeUI();
    }

    @Override
    protected JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setName("albumViewTopPanel");

        JLabel titleLabel = new JLabel(songCollection.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font(DefaultsTexter.defaultFont(), Font.BOLD, 20));

        JLabel artistLabel = new JLabel(songCollection.getArtist(), SwingConstants.CENTER);
        artistLabel.setFont(new Font(DefaultsTexter.defaultFont(), Font.PLAIN, 14));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(artistLabel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void refreshSongListContents() {
        songListPanel.removeAll();
        List<Integer> songIDs = collectionManager.getSongIDsByCollection(songCollection);
        for (int songID : songIDs) {
            Song song = songManager.getSongByID(songID);
            if (song != null) {
                songListPanel.add(
                        new SongPanel(song, songManager, collectionManager, tagManager, this::refreshSongListPanel));
            }
        }
    }

    @Override
    protected JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("albumViewBottomPanel");

        JToggleButton likeButton = new JToggleButton(AlbumViewTexter.likeButton());
        likeButton.setSelected(songCollection.isLiked());
        likeButton.addActionListener(e -> {
            songCollection = collectionManager.toggleLikeCollection(songCollection);     
        });

        panel.add(likeButton, BorderLayout.CENTER);
        return panel;
    }
}
