package beatbinder.presentation.popups;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import beatbinder.exceptions.DuplicateTagException;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.objects.Tag;
import beatbinder.presentation.components.SelectableListPanel;
import beatbinder.presentation.text.content.AddTagDialogTexter;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.ErrorTexter;

/**
 * Displays a popup allowing the user to apply any {@link Tag} to a given
 * {@link Song}.
 * 
 * @see Tag
 * @see Song
 */
public class AddTagToSongDialog extends JDialog {
    /**
     * The {@link Song} being applied to.
     */
    private Song song;
    /**
     * The {@link Tag} currently selected by the user.
     */
    private Tag selectedTag;
    /**
     * The {@link TagManager} used to get {@link Tag} information.
     */
    private TagManager tagManager;

    /**
     * Create a popup that allows a user to apply a selected {@link Tag} from a list
     * to a given
     * {@link Song}.
     * 
     * @param parent     the {@link JDialog} calling this popup.
     * @param song       the {@link Song} being added to.
     * @param tagManager the {@link TagManager} used to get {@link Tag}
     *                   informoation.
     * @param refresh
     */
    public AddTagToSongDialog(JDialog parent, Song song, TagManager tagManager) {
        super(parent, AddTagDialogTexter.windowName(), true);

        this.song = song;
        this.tagManager = tagManager;

        setSize(DefaultsTexter.smallDialogWidth(), DefaultsTexter.smallDialogHeight());
        setResizable(false);
        setLocationRelativeTo(parent);

        List<Tag> availableTags = tagManager.getAllTags();

        SelectableListPanel<Tag> panel = new SelectableListPanel<>(
                availableTags,
                AddTagDialogTexter.addButton(),
                Tag::getName,
                tag -> {
                    selectedTag = tag;
                    addTag();
                });

        add(panel);
    }

    /**
     * Adds {@code selectedTag} to {@code song}.
     */
    private void addTag() {
        try {
            tagManager.addTagToSong(selectedTag, song);
            dispose();
        } catch (DuplicateTagException e) {
            JOptionPane.showMessageDialog(this, ErrorTexter.songTagDuplicate(),
                    ErrorTexter.windowName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    public Tag getSelectedTag() {
        return selectedTag;
    }
}
