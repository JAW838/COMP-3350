package beatbinder.presentation.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.CollType;
import beatbinder.objects.SongCollection;
import beatbinder.presentation.popups.AlbumDialog;
import beatbinder.presentation.popups.PlaylistDialog;
import beatbinder.presentation.popups.SongListDialog;
import beatbinder.presentation.text.content.CollectionPanelTexter;

/**
 * Displays a {@link SongCollection} in a tile-style UI component, with behavior depending on its
 * {@link CollType} (e.g., album or playlist).
 * 
 * @see SongCollection
 */
public class CollectionPanel extends JPanel {
    /**
     * The {@link SongCollection} being displayed.
     */
    private final SongCollection songCollection;

    /**
     * The {@link CollectionManager} used to retrieve information about the current collection.
     */
    private CollectionManager collectionManager;
    
    /**
     * The {@link SongManager} used to retrieve information about songs.
     */
    private SongManager songManager;

    /**
     * The {@link TagManager} used to retrieve information about tags.
     */
    private TagManager tagManager;

    private Runnable refreshCallback;

    /**
     * Creates a {@link CollectionPanel} to display a {@code songCollection}.
     * 
     * @param songCollection the {@link SongCollection} to display.
     * @param collectionManager the {@link CollectionManager} being used.
     * @param songManager the {@link SongManager} being used.
     * @param tagManager the {@link TagManager} being used.
     * @param refreshCallback a function to refresh the display when needed (used by playlist views).
     */
    public CollectionPanel(SongCollection songCollection, CollectionManager collectionManager, SongManager songManager,
            TagManager tagManager,
            Runnable refreshCallback) {
        this.songCollection = songCollection;
        this.collectionManager = collectionManager;
        this.songManager = songManager;
        this.tagManager = tagManager;
        this.refreshCallback = refreshCallback;

        // set dimensions of panel
        final int tileSize = CollectionPanelTexter.defaultCollectionSize();
        setPreferredSize(new Dimension(tileSize, tileSize));

        // set look of panel
        int emptyBorderSize = CollectionPanelTexter.emptyBorderSize();
        int lineBorderSize = CollectionPanelTexter.lineBorderSize();
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, lineBorderSize, true), // rounded, soft border
                BorderFactory.createEmptyBorder(emptyBorderSize, emptyBorderSize, emptyBorderSize, emptyBorderSize)));
        setBackground(Color.WHITE);
        setOpaque(true);

        setLayout(new BorderLayout());

        initializeUI();
    }

    /**
     * Initializes the UI elements of the panel, including the clickable title label.
     */
    private void initializeUI() {
        // Create a clickable label for the album title
        JLabel title = new JLabel(songCollection.getTitle(), SwingConstants.CENTER);
        title.setName("title_" + songCollection.getID());
        title.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        title.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(Color.LIGHT_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showCollectionView();
                refreshCallback.run();
            }
        });

        add(title);
    }

    /**
     * Opens the appropriate view dialog (album or playlist) for the current {@link SongCollection}.
     */
    private void showCollectionView() {
        SongListDialog collectionView;

        if (songCollection.getType() == CollType.ALBUM) {
            collectionView = new AlbumDialog(songCollection, collectionManager, songManager, tagManager);
            collectionView.setName("albumViewDialog");
        } else {
            collectionView = new PlaylistDialog(songCollection, collectionManager, songManager, tagManager);
            collectionView.setName("playlistViewDialog");
            refreshCallback.run();
        }
        collectionView.setVisible(true);
    }

    /**
     * Retrieves the {@link SongCollection} being displayed.
     * 
     * @return the current {@link SongCollection}.
     */
    public SongCollection getSongCollection() {
        return this.songCollection;
    }
}
