package beatbinder.presentation.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.presentation.popups.AddToPlaylistDialog;
import beatbinder.presentation.popups.ArtistDialog;
import beatbinder.presentation.popups.SongDetailsDialog;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.SongPanelTexter;

/**
 * Displays detailed information about a given {@link Song} instance.
 * 
 * @see Song
 */
public class SongPanel extends JPanel {
    /**
     * The song being displayed.
     */
    private final Song song;
    private Runnable refreshCallback;
    /**
     * How the song was accessed.
     */
    private final SongPanelContext context;

    /**
     * The {@link SongManager} used to access {@link Song} information.
     */
    private SongManager songManager;
    /**
     * The {@link CollectionManager} used to access {@link SongCollection} information.
     */
    private CollectionManager collectionManager;
    /**
     * The {@link TagManager} used to access {@link Tag} information.
     */
    private TagManager tagManager;

    /**
     * Creates a {@link SongPanel} to display detailed information about a given {@link Song}.
     * <p>
     * Options displayed depend on the way the song was accessed.
     * 
     * @param song the {@link Song} to display.
     * @param songManager the {@link SongManager} used to retrieve song information.
     * @param collectionManager the {@link CollectionManager} used to retrieve {@link SongCollection}
     * information.
     * @param tagManager the {@link TagManager} used to retrieve {@link Tag} information.
     * @param refreshCallback
     * @param context the {@link SongPanelContext} indicating how the song was accessed.
     */
    public SongPanel(Song song, SongManager songManager, CollectionManager collectionManager, TagManager tagManager,
            Runnable refreshCallback, SongPanelContext context) {
        this.song = song;
        this.songManager = songManager;
        this.collectionManager = collectionManager;
        this.tagManager = tagManager;
        this.refreshCallback = refreshCallback;
        this.context = context;

        int emptyBorderSize = SongPanelTexter.emptyBorderSize();
        int matteBorderSize = SongPanelTexter.matteBorderSize();
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, matteBorderSize, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(emptyBorderSize, emptyBorderSize, emptyBorderSize, emptyBorderSize)));
        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, SongPanelTexter.defaultSongHeight()));

        initializeUI();
    }

    /**
     * Creates a {@link SongPanel} to display detailed information about a given {@link Song} with
     * the default context.
     * 
     * @param song the {@link Song} to display.
     * @param songManager the {@link SongManager} used to retrieve song information.
     * @param collectionManager the {@link CollectionManager} used to retrieve {@link SongCollection}
     * information.
     * @param tagManager the {@link TagManager} used to retrieve {@link Tag} information.
     * @param refreshCallback
     */
    public SongPanel(Song song, SongManager songManager, CollectionManager collectionManager, TagManager tagManager,
            Runnable refreshCallback) {
        this(song, songManager, collectionManager, tagManager, refreshCallback, SongPanelContext.DEFAULT);
    }

    /**
     * Starts the UI component.
     */
    private void initializeUI() {
        // Create the labels
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setName("infoPanel_"+song.getID());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, SongPanelTexter.panelInfoSpacing(), 0, SongPanelTexter.panelInfoSpacing());
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel(song.getTitle());
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Grow to fill available space
        infoPanel.add(titleLabel, gbc);

        // Artist
        JLabel artistLabel = new JLabel(song.getArtist());
        gbc.gridx = 1;
        gbc.weightx = 0; // Fixed
        infoPanel.add(artistLabel, gbc);

        // Runtime
        JLabel runtimeLabel = new JLabel(formatRuntime(song.getRuntime()));
        gbc.gridx = 2;
        infoPanel.add(runtimeLabel, gbc);

        // Like button
        JButton likeButton = new JButton(); // empty heart
        likeButton.setName("likeButton_" + song.getID());
        likeButton.setText(song.isLiked() ? SongPanelTexter.like() : SongPanelTexter.unlike());
        likeButton.setBorderPainted(false);
        likeButton.setContentAreaFilled(false);
        likeButton.setFocusPainted(false);
        likeButton.setFont(likeButton.getFont().deriveFont(SongPanelTexter.heartSize()));

        likeButton.addActionListener(e -> {
            songManager.toggleLikeSong(song);
            refreshCallback.run();
        });

        JButton menuButton = new JButton(SongPanelTexter.menuIcon());
        menuButton.setName("menuButton_" + song.getID());

        // Create popup menu
        JPopupMenu menu = new JPopupMenu();
        menu.setName("menu_" + song.getID());

        JMenuItem addItem = new JMenuItem(SongPanelTexter.add());
        addItem.setName("addItem_" + song.getID());
        JMenuItem detailsItem = new JMenuItem(SongPanelTexter.details());
        detailsItem.setName("detailsItem_" + song.getID());

        // Add action listeners using the stored song
        addItem.addActionListener(e -> handleAdd());
        detailsItem.addActionListener(e -> showDetails());

        if (context != SongPanelContext.ARTIST_VIEW) {
            JMenuItem viewArtistItem = new JMenuItem(SongPanelTexter.viewArtist());
            viewArtistItem.addActionListener(e -> openArtistView());
            menu.add(viewArtistItem);
        }

        menu.add(addItem);
        menu.add(detailsItem);

        menuButton.addActionListener(e -> menu.show(menuButton, 0, menuButton.getHeight()));

        // add burger menu
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(likeButton, BorderLayout.WEST);
        buttonPanel.add(menuButton, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(230, 230, 230)); // Light gray
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(UIManager.getColor(DefaultsTexter.panelBackgroundKey()));
            }
        });

        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    /**
     * Formats a given time in seconds into a min:sec displayable String.
     * @param seconds the time to format.
     * @return a String displaying a time in min:sec format.
     */
    private String formatRuntime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    /**
     * Creates a {@link AddToPlaylistDialog} to add a {@link Song} to a {@link SongCollection}.
     * 
     * @see AddToPlaylistDialog
     */
    private void handleAdd() {
        Window window = SwingUtilities.getWindowAncestor(this);

        AddToPlaylistDialog dialog = new AddToPlaylistDialog(window, collectionManager, song);
        dialog.setName("addToPlaylistDialog");
        dialog.setVisible(true);
    }

    /**
     * Creates a {@link SongDetailsDialog} to display detailed information about a {@link Song}.
     * 
     * @see SongDetailsDialog
     */
    private void showDetails() {
        Window parentFrame = SwingUtilities.getWindowAncestor(this);
        SongDetailsDialog detailsView = new SongDetailsDialog(parentFrame, song, songManager, tagManager);
        detailsView.setVisible(true);
    }

    /**
     * Creates a {@link ArtistDialog} to display other songs by an artist.
     * 
     * @see ArtistDialog
     */
    private void openArtistView() {
        ArtistDialog dialog = new ArtistDialog(song, songManager, collectionManager, tagManager);
        dialog.setVisible(true);
    }
}