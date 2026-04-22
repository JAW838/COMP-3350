package beatbinder.presentation.popups;

import java.awt.Window;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import beatbinder.exceptions.DuplicateSongException;
import beatbinder.logic.CollectionManager;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.presentation.components.SelectableListPanel;
import beatbinder.presentation.text.content.AddToPlaylistTexter;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.ErrorTexter;

/**
 * Displays a popup allowing the user to add a {@link Song} to a {@link SongCollection}.
 */
public class AddToPlaylistDialog extends JDialog {
    private boolean confirmed = false;

    /**
     * Creates a popup allowing the user to add a {@link Song} to a {@link SongCollection}.
     * 
     * @param parent the {@link Window} calling this popup. 
     * @param collectionManager the {@link CollectionManager} used to retrieve {@link SongCollection}
     * information.
     * @param song the {@link Song} to add.
     */
    public AddToPlaylistDialog(Window parent, CollectionManager collectionManager, Song song) {
        super(parent, AddToPlaylistTexter.windowName(), ModalityType.APPLICATION_MODAL);
        setSize(DefaultsTexter.smallDialogWidth(), DefaultsTexter.smallDialogHeight());
        setResizable(false);
        setLocationRelativeTo(parent);

        List<SongCollection> playlists = collectionManager.getAllPlaylists();

        SelectableListPanel<SongCollection> panel = new SelectableListPanel<>(
                playlists,
                AddToPlaylistTexter.confirmButton(),
                SongCollection::getTitle,
                selectedCollection -> {
                    try {
                        collectionManager.addSongToPlaylist(selectedCollection, song);
                        confirmed = true;
                    } catch (DuplicateSongException dse) {
                        JOptionPane.showMessageDialog(this, ErrorTexter.songDuplicate(),
                                ErrorTexter.windowName(), JOptionPane.ERROR_MESSAGE);
                    } 
                    dispose();
                });
        panel.setName("PlaylistSelectionPanel");

        add(panel);
    }

    public boolean wasConfirmed() {
        return confirmed;
    }
}
