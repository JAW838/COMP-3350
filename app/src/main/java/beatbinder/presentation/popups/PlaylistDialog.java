package beatbinder.presentation.popups;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import beatbinder.exceptions.DuplicatePlaylistNameException;
import beatbinder.exceptions.SongNotFoundException;
import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.CollType;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.presentation.components.ReorderableSongPanel;
import beatbinder.presentation.components.SongPanel;
import beatbinder.presentation.components.SongPanelContext;
import beatbinder.presentation.components.SortButtonFactory;
import beatbinder.presentation.components.SortOption;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.ErrorTexter;
import beatbinder.presentation.text.content.PlaylistViewTexter;

/**
 * Window to display the contents of a {@link SongCollection}.
 */
public class PlaylistDialog extends SongListDialog {
    /**
     * The {@link SongCollection} being displayed.
     */
    private final SongCollection songCollection;
    /**
     * The {@link CollectionManager} being used to get {@link SongCollection}
     * information.
     */
    private final CollectionManager collectionManager;

    /**
     * Creates a new {@link PlaylistDialog} to display a given {@link SongCollection}
     * and its contents.
     * 
     * @param songCollection    the {@link SongCollection} being displayed.
     * @param collectionManager the {@link CollectionManager} being used to get
     *                          {@link SongCollection}
     *                          information.
     * @param songManager       the {@link SongManager} being used to get
     *                          {@link Song} information.
     * @param tagManager        the {@link TagManager} being used to get {@link Tag}
     *                          information.
     */
    public PlaylistDialog(SongCollection songCollection,
            CollectionManager collectionManager,
            SongManager songManager,
            TagManager tagManager) {
        super(songManager, tagManager);
        this.songCollection = songCollection;
        this.collectionManager = collectionManager;

        setTitle("Playlist: " + songCollection.getTitle());

        initializeUI();
    }

    /**
     * Creates a top panel containing the name of the panel.
     */
    @Override
    protected JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setName("playlistViewTopPanel");
        JLabel titleLabel = new JLabel(songCollection.getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(new Font(DefaultsTexter.defaultFont(), Font.BOLD, 20));

        panel.add(titleLabel, BorderLayout.CENTER);
        return panel;
    }

    @Override
    protected void refreshSongListContents() {
        songListPanel.removeAll();
        List<Integer> songIDs = collectionManager.getSongIDsByCollection(songCollection);
        List<Song> songs = new ArrayList<>();

        for (Integer id : songIDs) {
            Song song = songManager.getSongByID(id);
            if (song != null) {
                songs.add(song);
            }
        }

        if (sortOption != SortOption.DEFAULT) {
            // If not the default option, we'll sort and use regular SongPanels
            songs = songManager.sortSongs(songs, sortOption);
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                songListPanel.add(new SongPanel(song, songManager, collectionManager, tagManager,
                        this::refreshSongListPanel, SongPanelContext.PLAYLIST_VIEW));
            }
        } else {
            // If the default sort option, we'll add ReorderableSongPanels
            // The user should only be allowed to reorder in the default view
            for (int i = 0; i < songs.size(); i++) {
                Song song = songs.get(i);
                songListPanel.add(new ReorderableSongPanel(
                        song,
                        i,
                        songs.size(),
                        songCollection,
                        songManager,
                        collectionManager,
                        tagManager,
                        this::refreshSongListPanel));
            }
        }
    }

    /**
     * Creates a panel with edit, rename, and delete buttons.
     */
    @Override
    protected JPanel createBottomPanel() {
        if (songCollection.getType() == CollType.PLAYLIST) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            panel.setName("playlistViewBottomPanel");

            JButton editButton = new JButton(PlaylistViewTexter.editButton());
            editButton.setName("playlistEditButton");
            editButton.addActionListener(e -> {
                handleEditPlaylist();
                refreshSongListPanel();
            });

            JButton renameButton = new JButton(PlaylistViewTexter.renameButton());
            renameButton.setName("playlistRenameButton");
            renameButton.addActionListener(e -> handleRenamePlaylist());

            JButton deleteButton = new JButton(PlaylistViewTexter.deleteButton());
            deleteButton.setName("playlistDeleteButton");
            deleteButton.addActionListener(e -> handleDeletePlaylist());

            panel.add(editButton);
            panel.add(renameButton);
            panel.add(deleteButton);

            return panel;
        }
        return null;
    }

    @Override
    protected JButton getSortButton() {
        return SortButtonFactory.createSortMenuButton(selectedOption -> {
            this.sortOption = selectedOption;
            refreshSongListPanel();
        });
    }

    /**
     * Renames a {@link SongCollection}.
     */
    private void handleRenamePlaylist() {
        String currentName = songCollection.getTitle();
        String newName = JOptionPane.showInputDialog(
                this,
                PlaylistViewTexter.renameDialog(),
                currentName);

        if (newName != null) {
            try {
                collectionManager.updatePlaylist(songCollection, newName);
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ErrorTexter.emptyPlaylistName(),
                        ErrorTexter.windowName(),
                        JOptionPane.ERROR_MESSAGE);
            } catch (DuplicatePlaylistNameException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        ErrorTexter.collectionDuplicate(),
                        ErrorTexter.windowName(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Creates a new {@link EditPlaylistDialog} to allow the user to edit their
     * playlist.
     */
    private void handleEditPlaylist() {
        EditPlaylistDialog dialog = new EditPlaylistDialog(
                this,
                songCollection,
                collectionManager,
                songManager);
        dialog.setName("editPlaylistDialog");
        dialog.setVisible(true);
    }

    /**
     * Deletes a given playlist.
     */
    private void handleDeletePlaylist() {
        int response = JOptionPane.showConfirmDialog(
                this,
                PlaylistViewTexter.deletePlaylistMessage(),
                PlaylistViewTexter.deleteConfirm(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            try {
                collectionManager.deletePlaylist(songCollection);
                dispose();
            } catch (SongNotFoundException e) {
                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        ErrorTexter.windowName(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
