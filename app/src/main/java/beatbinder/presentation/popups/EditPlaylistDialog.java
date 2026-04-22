package beatbinder.presentation.popups;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import beatbinder.exceptions.SongNotFoundException;
import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.presentation.components.SelectableListPanel;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.EditPlaylistTexter;

/**
 * Window allowing the user to edit a given {@link SongCollection}.
 */
public class EditPlaylistDialog extends JDialog {
    /**
     * The {@link SongCollection} to edit.
     */
    private final SongCollection songCollection;
    /**
     * The {@link CollectionManager} used to get {@link SongCollection} information.
     */
    private final CollectionManager collectionManager;
    /**
     * The {@link SongManager} used to get {@link Song} information.
     */
    private final SongManager songManager;

    /**
     * Creates a dialog allowing the user to edit a given {@link SongCollection},
     * including
     * 
     * @param parent
     * @param songCollection
     * @param collectionManager
     * @param songManager
     */
    public EditPlaylistDialog(
            JDialog parent,
            SongCollection songCollection,
            CollectionManager collectionManager,
            SongManager songManager) {
        super(parent, EditPlaylistTexter.windowName(), true);
        this.songCollection = songCollection;
        this.collectionManager = collectionManager;
        this.songManager = songManager;

        setSize(DefaultsTexter.smallDialogWidth(), DefaultsTexter.smallDialogHeight());
        setResizable(false);
        setLocationRelativeTo(parent);

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        List<Integer> songIDs = collectionManager.getSongIDsByCollection(songCollection);
        List<Song> songs = new ArrayList<>();
        for (int id : songIDs) {
            Song song = songManager.getSongByID(id);
            if (song != null)
                songs.add(song);
        }

        SelectableListPanel<Song> panel = new SelectableListPanel<>(
                songs,
                EditPlaylistTexter.removeButtonName(),
                Song::getTitle,
                selected -> {
                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            EditPlaylistTexter.confirmMessageStart() + selected.getTitle()
                                    + EditPlaylistTexter.confirmMessageEnd(),
                            EditPlaylistTexter.confirmButtonName(),
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            collectionManager.deleteSongFromPlaylist(songCollection, selected);
                            dispose();
                        } catch (SongNotFoundException ex) {
                            JOptionPane.showMessageDialog(this, ex.getMessage());
                        }
                    }
                });
        panel.setName("playlistEditPanel");

        add(panel, BorderLayout.CENTER);
    }
}
