package beatbinder.presentation.components;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Font;

import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.SongPanelTexter;

/**
 * Creates a UI panel for changing the position of a {@link Song} in a
 * {@link SongCollection}.
 */
public class ReorderableSongPanel extends JPanel {
    /**
     * The current position of the {@link Song} in the {@link SongCollection}.
     */
    private final int songIndex;
    /**
     * The total number of {@link Song} instances in the {@link SongCollection}.
     */
    private final int totalSongs;
    /**
     * The {@link SongCollection} being displayed.
     */
    private final SongCollection collection;
    /**
     * The {@link Song} being moved.
     */
    private final Song song;
    /**
     * The {@link CollectionManager} being used to communicate with the database.
     */
    private final CollectionManager collectionManager;
    private final Runnable refreshCallback;

    /**
     * Creates a UI panel for changing the position of a {@link Song} in a
     * {@link SongCollection}.
     * 
     * This panel displays the song and provides controls to reorder it within the
     * collection.
     * 
     * @param song              the {@link Song} to be moved.
     * @param songIndex         the current position (0-based) of the song in the
     *                          {@link SongCollection}.
     * @param totalSongs        the total number of songs in the
     *                          {@link SongCollection}.
     * @param collection        the {@link SongCollection} the song belongs to.
     * @param songManager       the {@link SongManager} used to retrieve song
     *                          information.
     * @param collectionManager the {@link CollectionManager} used to modify
     *                          collection data.
     * @param tagManager        the {@link TagManager} used to retrieve tag
     *                          information.
     * @param refreshCallback   a callback to refresh the UI after reordering.
     */
    public ReorderableSongPanel(
            Song song,
            int songIndex,
            int totalSongs,
            SongCollection collection,
            SongManager songManager,
            CollectionManager collectionManager,
            TagManager tagManager,
            Runnable refreshCallback) {
        this.song = song;
        this.songIndex = songIndex + 1; // ???
        this.totalSongs = totalSongs;
        this.collection = collection;
        this.collectionManager = collectionManager;
        this.refreshCallback = refreshCallback;

        setLayout(new BorderLayout());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, SongPanelTexter.defaultSongHeight()));
        // Create the base SongPanel
        SongPanel panel = new SongPanel(song, songManager, collectionManager, tagManager, refreshCallback,
                SongPanelContext.PLAYLIST_VIEW);
        panel.setName("ReorderableSongPanel");

        add(panel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.EAST);
    }

    /**
     * Creates buttons to move a {@link Song} up and down in a
     * {@link SongCollection}.
     * 
     * @return a {@link JPanel} with up and down buttons on it.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2)); // spacing between buttons
        panel.setName("ReorderableSongButtonPanel");

        JButton upButton = new JButton(SongPanelTexter.upButton());
        upButton.setName("ReorderableSongUpButton");
        JButton downButton = new JButton(SongPanelTexter.downButton());
        downButton.setName("ReorderableSongDownButton");

        Dimension buttonSize = new Dimension(35, 20);
        Font smallFont = new Font(DefaultsTexter.defaultFont(), Font.PLAIN, 12);

        for (JButton btn : new JButton[] { upButton, downButton }) {
            btn.setPreferredSize(buttonSize);
            btn.setFont(smallFont);
            btn.setMargin(new Insets(0, 0, 0, 0)); // remove extra padding
            btn.setFocusPainted(false);
            btn.setFocusable(false);
        }

        upButton.addActionListener(e -> moveSong(songIndex - 1));
        downButton.addActionListener(e -> moveSong(songIndex + 1));

        // Disable buttons on the first + last song in the playlist
        if (songIndex == 1) {
            upButton.setEnabled(false);
        }
        if (songIndex == totalSongs) {
            downButton.setEnabled(false);
        }

        panel.setOpaque(false); // match background
        panel.add(upButton);
        panel.add(downButton);

        return panel;
    }

    /**
     * Move the {@link Song} to a new position {@code newPosition}.
     * 
     * @param newPosition the position to move the {@link Song} to.
     */
    private void moveSong(int newPosition) {
        // request the database to move the song
        collectionManager.setSongPosition(collection, song, newPosition);
        // update the page
        refreshCallback.run();

    }
}
