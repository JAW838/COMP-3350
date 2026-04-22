package beatbinder.presentation.popups;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.objects.Tag;
import beatbinder.presentation.components.PanelUtils;
import beatbinder.presentation.components.TagPanel;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.MainWindowTexter;
import beatbinder.presentation.text.content.SongDetailsTexter;

/**
 * Displays detailed information about a {@link Song}, including artist,
 * runtime, user-applied
 * tags, and a custom user note.
 * 
 * @see Song
 * @see Tag
 */
public class SongDetailsDialog extends JDialog {
    private Song song;
    private final SongManager songManager;
    private final TagManager tagManager;

    private JPanel notePanel;
    private JLabel noteLabel;
    private JTextArea noteTextArea;
    private JButton editNoteButton;
    private JPanel tagListPanel;
    private boolean isEditingNote = false;

    /**
     * Creates a {@link SongDetailsDialog} to display detailed information about a
     * given {@link Song}.
     * <p>
     * Displayed information includes title, artist, runtime, release year, applied
     * tags, and the
     * custom user note.
     * 
     * @param parent      the {@link Window} that called this view.
     * @param song        the {@link Song} to display.
     * @param songManager the {@link SongManager} used to get {@link Song} details.
     * @param tagManager  the {@link TagManager} used to get {@link Tag} details.
     *
     */
    public SongDetailsDialog(Window parent, Song song, SongManager songManager, TagManager tagManager) {
        super(parent, MainWindowTexter.windowName(), ModalityType.APPLICATION_MODAL);
        this.song = song;
        this.songManager = songManager;
        this.tagManager = tagManager;

        setName("songDetailsViewDialog");

        // set settings of window
        setSize(DefaultsTexter.defaultDialogWidth(), DefaultsTexter.defaultDialogHeight());
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Basic info
        contentPanel.add(new JLabel(SongDetailsTexter.titleInfo() + song.getTitle()));
        contentPanel.add(new JLabel(SongDetailsTexter.artistInfo() + song.getArtist()));
        contentPanel.add(new JLabel(SongDetailsTexter.genreInfo() + song.getGenre()));
        contentPanel.add(new JLabel(SongDetailsTexter.runtimeInfo() + formatRuntime(song.getRuntime())));

        // Note panel
        notePanel = new JPanel(new BorderLayout());
        notePanel.setBorder(BorderFactory.createTitledBorder(SongDetailsTexter.notePanelName()));
        notePanel.setAlignmentX(LEFT_ALIGNMENT);

        noteLabel = new JLabel(formatNoteHtml(song.getNote()));
        noteLabel.setVerticalAlignment(JLabel.TOP);

        noteTextArea = new JTextArea(song.getNote());
        noteTextArea.setLineWrap(true);
        noteTextArea.setWrapStyleWord(true);
        // noteTextArea.setVisible(false);

        editNoteButton = new JButton(SongDetailsTexter.editButton());
        editNoteButton.addActionListener(e -> toggleNoteEdit());

        notePanel.add(noteLabel, BorderLayout.CENTER);
        notePanel.add(editNoteButton, BorderLayout.SOUTH);

        contentPanel.add(notePanel);

        contentPanel.add(Box.createVerticalStrut(10));

        // Add content panel to CENTER
        add(contentPanel, BorderLayout.CENTER);

        // Tags panel setup
        tagListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tagListPanel.setName("tagListPanel");
        refreshTags();

        JPanel tagsSection = PanelUtils.createTitledScrollSection(
                SongDetailsTexter.tagPanelName(),
                tagListPanel,
                createTagButton(),
                BorderLayout.EAST);
        tagsSection.setPreferredSize(new Dimension(0, 100));

        // Footer button panel
        JButton closeButton = new JButton(SongDetailsTexter.closeButton());
        closeButton.setName("closeButton");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        // Combine tagsSection and buttonPanel into one panel
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(tagsSection);
        southPanel.add(buttonPanel);

        // Add the combined panel to the SOUTH
        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Formats a given time in seconds into a min:sec displayable String.
     * 
     * @param seconds the time to format.
     * @return a String displaying a time in min:sec format.
     */
    private String formatRuntime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }

    /**
     * Creates a {@link JButton} which opens a popup menu which allows a user to add
     * or remove tags
     * from the song.
     * 
     * @return the created {@link JButton}.
     */
    private JButton createTagButton() {
        JButton menuButton = new JButton(SongDetailsTexter.tagButton());
        menuButton.setName("tagMenuButton");
        menuButton.setFocusPainted(false);
        menuButton.setPreferredSize(new Dimension(40, 40));
        menuButton.setFont(new Font(DefaultsTexter.defaultFont(), Font.BOLD, 18));
        menuButton.setForeground(Color.BLACK);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.setOpaque(true);

        // Create popup menu
        JPopupMenu tagMenu = new JPopupMenu();
        tagMenu.setName("tagMenu");

        JMenuItem addItem = new JMenuItem(SongDetailsTexter.addTag());
        addItem.setName("addTagItem");
        JMenuItem removeItem = new JMenuItem(SongDetailsTexter.removeTag());
        removeItem.setName("removeTagItem");

        // Add action listeners
        addItem.addActionListener(e -> {
            AddTagToSongDialog dialog = new AddTagToSongDialog(this, song, tagManager);
            dialog.setVisible(true);
            refreshTags();
        });

        removeItem.addActionListener(e -> {
            RemoveTagFromSongDialog dialog = new RemoveTagFromSongDialog(this, song, tagManager);
            dialog.setVisible(true);
            refreshTags();
        });

        tagMenu.add(addItem);
        tagMenu.add(removeItem);

        // Show menu on button click
        menuButton.addActionListener(e -> tagMenu.show(menuButton, 0, menuButton.getHeight()));

        return menuButton;
    }

    /**
     * Refreshes list of tags applied to the song.
     */
    private void refreshTags() {
        tagListPanel.removeAll();
        List<Tag> tags = tagManager.getTagsOfSong(song);

        for (Tag tag : tags) {
            tagListPanel.add(new TagPanel(tag, null));
        }

        revalidate();
        repaint();
    }

    /**
     * Toggles between viewing and editing the user note. When in view mode, the
     * note cannot be
     * edited.
     */
    private void toggleNoteEdit() {
        notePanel.removeAll();

        if (!isEditingNote) {
            // Switch to edit mode
            noteTextArea = new JTextArea(song.getNote());
            noteTextArea.setLineWrap(true);
            noteTextArea.setWrapStyleWord(true);
            noteTextArea.setEditable(true);
            noteTextArea.setCaretPosition(noteTextArea.getText().length());

            JScrollPane scrollPane = new JScrollPane(noteTextArea);
            scrollPane.setPreferredSize(new Dimension(400, 100));

            editNoteButton.setText(SongDetailsTexter.saveButton());

            notePanel.add(scrollPane, BorderLayout.CENTER);
            notePanel.add(editNoteButton, BorderLayout.SOUTH);
            isEditingNote = true;

        } else {
            // Switch to view mode
            String newNote = noteTextArea.getText().trim();
            song = songManager.updateNote(song, newNote);

            noteLabel = new JLabel(formatNoteHtml(newNote));
            noteLabel.setVerticalAlignment(JLabel.TOP);

            editNoteButton.setText(SongDetailsTexter.editButton());

            notePanel.add(noteLabel, BorderLayout.CENTER);
            notePanel.add(editNoteButton, BorderLayout.SOUTH);
            isEditingNote = false;
        }

        notePanel.revalidate();
        notePanel.repaint();
    }

    private String formatNoteHtml(String noteText) {
        return SongDetailsTexter.htmlFormatOpen() +
                (noteText == null || noteText.isBlank() ? SongDetailsTexter.noNote() : noteText) +
                SongDetailsTexter.htmlFormatClose();
    }
}