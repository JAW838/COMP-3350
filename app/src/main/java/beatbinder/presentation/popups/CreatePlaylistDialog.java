package beatbinder.presentation.popups;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import beatbinder.exceptions.DuplicatePlaylistNameException;
import beatbinder.logic.CollectionManager;
import beatbinder.objects.SongCollection;
import beatbinder.presentation.text.content.CreatePlaylistTexter;
import beatbinder.presentation.text.content.ErrorTexter;

/**
 * Window that allows the user to create a new {@link SongCollection} of type {@code PLAYLIST}.
 */
public class CreatePlaylistDialog extends JDialog {
    /**
     * Area the user can input the name of the {@code PLAYLIST}.
     */
    private JTextField playlistNameField;
    /**
     * Playlist name.
     */
    private String playlistName;

    private CollectionManager collectionManager;

    /**
     * Creates a new window which allows the user to create a new playlist with a custom name.
     * 
     * @param parent the {@link JFrame} that called this dialog.
     * @param collectionManager the {@link CollectionManager} used to get {@link SongCollection} 
     * information.
     */
    public CreatePlaylistDialog(JFrame parent, CollectionManager collectionManager) {
        super(parent, CreatePlaylistTexter.windowName(), true);

        this.collectionManager = collectionManager;
        setName("createPlaylistDialog");
        setResizable(false);

        initializeUI();
        setLocationRelativeTo(null); // Center of screen always
        pack();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));

        // Message label
        JLabel messageLabel = new JLabel(CreatePlaylistTexter.promptText());
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(messageLabel, BorderLayout.NORTH);

        // Text field
        playlistNameField = new JTextField(20);
        playlistNameField.setName("playlistNameField");
        playlistNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 10),
                playlistNameField.getBorder()));
        add(playlistNameField, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setName("playlistCreationButtonPanel");
        JButton cancelButton = new JButton(CreatePlaylistTexter.cancelButton());
        cancelButton.setName("cancelPlaylistCreationButton");
        JButton confirmButton = new JButton(CreatePlaylistTexter.confirmButton());
        confirmButton.setName("confirmPlaylistCreationButton");

        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        cancelButton.addActionListener(e -> {
            handleCancel();
        });

        confirmButton.addActionListener(e -> {
            handleConfirm();
        });
    }

    private void handleCancel() {
        playlistName = null;
        dispose();
    }

    /**
     * Passes the new {@code playlistName} to {@link CollectionManager#createPlaylist(String)}
     * to create the new playlist.
     */
    private void handleConfirm() {
        playlistName = playlistNameField.getText().trim();
        if (!playlistName.isEmpty()) {
            try {
                collectionManager.createPlaylist(playlistName);
            } catch (DuplicatePlaylistNameException dce) {
                JOptionPane.showMessageDialog(this, ErrorTexter.collectionDuplicate(), ErrorTexter.windowName(), JOptionPane.ERROR_MESSAGE);
                playlistName = null;
            }
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, ErrorTexter.emptyPlaylistName(),
                ErrorTexter.windowName(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retrieves the name of the new playlist.
     * @return the name of the new playlist.
     */
    public String getPlaylistName() {
        return playlistName;
    }
}
