package beatbinder.presentation.popups;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import beatbinder.exceptions.DuplicateTagException;
import beatbinder.logic.TagManager;
import beatbinder.objects.Tag;
import beatbinder.presentation.components.SelectableListPanel;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.ErrorTexter;
import beatbinder.presentation.text.content.ManageTagsDialogTexter;

/**
 * Displays a dialog to create and delete {@link Tag} instances.
 */
public class ManagePlatformTagsDialog extends JDialog {
    private Tag selectedTag;
    private TagManager tagManager;
    private SelectableListPanel<Tag> panel;

    /**
     * Creates a {@link ManagePlatformTagsDialog} to create and delete {@link Tag}
     * instances.
     * 
     * @param parent     the {@link JFrame} that called this dialog.
     * @param tagManager the {@link TagManager} being used to get {@link Tag}
     *                   details.
     */
    public ManagePlatformTagsDialog(JFrame parent, TagManager tagManager) {
        super(parent, ManageTagsDialogTexter.windowName(), true);
        setSize(DefaultsTexter.smallDialogWidth(), DefaultsTexter.smallDialogHeight());
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        this.tagManager = tagManager;
        setName("manageTagsDialog");

        List<Tag> songTags = tagManager.getAllTags();

        panel = new SelectableListPanel<>(
                songTags,
                ManageTagsDialogTexter.removeButton(),
                Tag::getName,
                tag -> {
                    selectedTag = tag;
                    removeTag();
                });
        panel.setName("tagSelectionPanel");

        add(panel);

        JButton createButton = new JButton(ManageTagsDialogTexter.createButton());
        createButton.setName("createTagButton");
        createButton.addActionListener(e -> createNewTag());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setName("buttonPanel");
        buttonPanel.add(createButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Deletes the {@link Tag} in {@code selectedTag}.
     */
    private void removeTag() {
        // Confirm that the user would like to delete
        int result = JOptionPane.showConfirmDialog(
                this,
                ManageTagsDialogTexter.removeConfirmMessageStart() + selectedTag.getName()
                        + ManageTagsDialogTexter.removeConfirmMessageEnd(),
                ManageTagsDialogTexter.removeConfirmName(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            tagManager.deleteTag(selectedTag);
            panel.updateItems(tagManager.getAllTags());
        }
    }

    /**
     * Opens a new dialog to take user input and creates a new {@link Tag} with a
     * user-given name.
     */
    private void createNewTag() {
        String input = JOptionPane.showInputDialog(this, ManageTagsDialogTexter.createTagMessage());
        if (input != null) {
            try {
                tagManager.createTag(input);
                panel.updateItems(tagManager.getAllTags());
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, ErrorTexter.emptyTagName(),
                        ErrorTexter.windowName(), JOptionPane.ERROR_MESSAGE);
            } catch (DuplicateTagException e) {
                JOptionPane.showMessageDialog(this, ErrorTexter.platformTagDuplicate(),
                        ErrorTexter.windowName(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}