package beatbinder.presentation.popups;

import java.util.List;

import javax.swing.JDialog;

import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.objects.Tag;
import beatbinder.presentation.components.SelectableListPanel;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.RemoveTagDialogTexter;

/**
 * Displays a dialog box for removing {@link Tag} instances from a selected
 * {@link Song}.
 * 
 * @see Tag
 * @see Song
 */
public class RemoveTagFromSongDialog extends JDialog {
    private Tag selectedTag;
    private Song song;
    private TagManager tagManager;

    /**
     * Creates a {@link RemoveTagFromSongDialog} with {@code song} being the song to remove
     * tags from.
     * 
     * @param parent     the {@link JDialog} that called this dialog.
     * @param song       the {@link Song} to remove tags from.
     * @param tagManager the {@link TagManager} used to get {@link Tag} details.
     * @param refresh
     */
    public RemoveTagFromSongDialog(JDialog parent, Song song, TagManager tagManager) {
        super(parent, RemoveTagDialogTexter.windowName(), true);
        setSize(DefaultsTexter.smallDialogWidth(), DefaultsTexter.smallDialogHeight());
        setResizable(false);
        setLocationRelativeTo(parent);

        this.song = song;
        this.tagManager = tagManager;

        initializeUI();
    }

    private void initializeUI() {
        List<Tag> songTags = tagManager.getTagsOfSong(song);

        SelectableListPanel<Tag> panel = new SelectableListPanel<>(
                songTags,
                RemoveTagDialogTexter.removeButton(),
                Tag::getName,
                tag -> {
                    selectedTag = tag;
                    removeTag();
                });

        add(panel);
    }

    private void removeTag() {
        tagManager.removeTagFromSong(selectedTag, song);
        dispose();
    }

    public Tag getSelectedTag() {
        return selectedTag;
    }
}