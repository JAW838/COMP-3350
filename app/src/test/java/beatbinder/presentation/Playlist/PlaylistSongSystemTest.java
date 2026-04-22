package beatbinder.presentation.Playlist;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.*;
import beatbinder.testutils.TestSetupHelper;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import javax.swing.*;

public class PlaylistSongSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private SongManager songManager;
    private CollectionManager collectionManager;
    private final String playlistName = "Test Playlist";

    @Override
    protected void onSetUp() throws Exception {
        // Use TestSetupHelper to initialize the test environment
        TestSetupHelper.TestSetup setup = TestSetupHelper.initializeTestEnvironment(robot());
        window = setup.window;
        songManager = setup.songManager;
        collectionManager = setup.collectionManager;

        // Wait for GUI initialization
        robot().waitForIdle();
        Thread.sleep(1000);

        // Ensure the main window is ready before testing
        window.requireVisible();
    }

    @Test
    public void testAddAndRemoveSongFromPlaylist() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();

        // Wait for the User Library view to load
        window.panel("userLibraryPanel").requireVisible();


        // Find the create playlist button by its text
        window.panel("userLibraryPanel").button("createPlaylistButton").click();

        // Wait for the create playlist dialog to appear and GUI to be idle
        robot().waitForIdle();
        Thread.sleep(500);

        JDialog createPlaylistDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("createPlaylistDialog"));

        // Ensure the dialog is visible
        assertThat(createPlaylistDialog).isNotNull();
        assertThat(createPlaylistDialog.isVisible()).isTrue();

        // Locate the playlist name text field using its name
        JTextField playlistNameField = (JTextField) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JTextField &&
                        "playlistNameField".equals(component.getName()));
        assertThat(playlistNameField).isNotNull();

        // Enter the playlist name
        robot().focus(playlistNameField);
        robot().enterText(playlistName);

        // Find the confirm button using its name
        JButton confirmCreationButton = (JButton) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JButton &&
                        "confirmPlaylistCreationButton".equals(component.getName()));
        assertThat(confirmCreationButton).isNotNull();

        // Click the confirm button to create the playlist
        robot().click(confirmCreationButton);

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Get confirmation message
        JLabel messageLabel = (JLabel) robot().finder().find(component ->
                component instanceof JLabel &&
                        "Created playlist: Test Playlist".equals(((JLabel) component).getText()));

        JButton okButton = (JButton) robot().finder().find(component ->
                component instanceof JButton &&
                        "OK".equals(((JButton) component).getText()));

        if (okButton != null) {
            robot().click(okButton);
        }

        // Verify the playlist was created
        List<SongCollection> playlists = collectionManager.getAllPlaylists();
        SongCollection createdPlaylist = playlists.stream()
                .filter(p -> p.getTitle().equals(playlistName))
                .findFirst()
                .orElse(null);

        assertThat(createdPlaylist).isNotNull();

        // Switch back to Platform view to see songs
        window.panel("topPanel").toggleButton("switchViewButton").click();
        window.panel("platformPanel").requireVisible();

        // Get the song panel and verify it's visible and has songs
        window.panel("songPanel").requireVisible();
        List<Song> initialSongs = songManager.getAllSongs();
        assertThat(initialSongs).isNotEmpty();

        // Find the first song
        Song firstSong = initialSongs.getFirst();
        Thread.sleep(500);

        // Click the menu button to show the popup menu
        window.panel("songPanel").button("menuButton_" + firstSong.getID()).click();

        // Wait for the popup menu to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the addItem button within the popup menu
        JMenuItem addItem = (JMenuItem) robot().finder().find(component ->
                component instanceof JMenuItem &&
                        ("addItem_" + firstSong.getID()).equals(component.getName())
        );
        assertThat(addItem).isNotNull();

        // Click the addItem button
        robot().click(addItem);

        // Wait for the AddToPlaylistDialog to appear
        robot().waitForIdle();
        Thread.sleep(500);


        JDialog addToPlaylistDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("addToPlaylistDialog"));
        assertThat(addToPlaylistDialog).isNotNull();


        // Find the JList within the dialog
        JList<?> itemList = (JList<?>) robot().finder().find(addToPlaylistDialog, component ->
                component instanceof JList &&
                        "itemList".equals(component.getName()));
        assertThat(itemList).isNotNull();

        // Select the playlist by its name
        String targetPlaylistName = playlistName;
        SwingUtilities.invokeAndWait(() -> {
            DefaultListModel<?> model = (DefaultListModel<?>) itemList.getModel();
            for (int i = 0; i < model.size(); i++) {
                Object item = model.get(i);
                if (item instanceof SongCollection && ((SongCollection) item).getTitle().equals(targetPlaylistName)) {
                    itemList.setSelectedIndex(i);
                    break;
                }
            }
        });

        // Find and click the confirm button
        JButton confirmAddButton = (JButton) robot().finder().find(addToPlaylistDialog, component ->
                component instanceof JButton &&
                        "confirmButton".equals(component.getName()));
        assertThat(confirmAddButton).isNotNull();

        SwingUtilities.invokeAndWait(confirmAddButton::doClick);

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Switch back to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();
        window.panel("userLibraryPanel").requireVisible();

        // Click on our playlist to open it
        window.panel("userLibraryPanel").label("title_" + createdPlaylist.getID()).click();

        // Wait for the playlist view dialog
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify the song is in the playlist
        List<Integer> playlistSongIds = collectionManager.getSongIDsByCollection(createdPlaylist);
        assertThat(playlistSongIds).contains(firstSong.getID());

        // Find the playlist view dialog that opened when we clicked the playlist title
        JDialog playlistViewDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("playlistViewDialog"));
        assertThat(playlistViewDialog).isNotNull();


        // Find and click the edit button within the playlist view dialog
        JButton editButton = (JButton) robot().finder().find(playlistViewDialog, component ->
                component instanceof JButton &&
                        "playlistEditButton".equals(component.getName()));
        assertThat(editButton).isNotNull();

        robot().click(editButton);

        // Wait for the edit playlist dialog
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the edit playlist dialog
        JDialog editPlaylistDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("editPlaylistDialog"));
        assertThat(editPlaylistDialog).isNotNull();

        // Find the SelectableListPanel and then the JList within it
        JPanel selectableListPanel = (JPanel) robot().finder().find(editPlaylistDialog, component ->
                component instanceof JPanel &&
                        "playlistEditPanel".equals(component.getName()));
        assertThat(selectableListPanel).isNotNull();


        JList<?> songList = (JList<?>) robot().finder().find(selectableListPanel, component ->
                component instanceof JList &&
                        "itemList".equals(component.getName()));
        assertThat(songList).isNotNull();


        // Select the song to remove
        SwingUtilities.invokeAndWait(() -> {
            DefaultListModel<?> model = (DefaultListModel<?>) songList.getModel();
            for (int i = 0; i < model.size(); i++) {
                Object item = model.get(i);
                if (item instanceof Song && ((Song) item).getID() == firstSong.getID()) {
                    songList.setSelectedIndex(i);
                    break;
                }
            }
        });

        // Click the remove button
        JButton removeButton = (JButton) robot().finder().find(editPlaylistDialog, component ->
                component instanceof JButton &&
                        "confirmButton".equals(component.getName()));
        assertThat(removeButton).isNotNull();

        robot().click(removeButton);

        // Wait for the confirmation dialog
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the JOptionPane and click Yes
        JOptionPane optionPane = (JOptionPane) robot().finder().find(component ->
                component instanceof JOptionPane);
        assertThat(optionPane).isNotNull();

        // Get the JDialog containing the JOptionPane
        JDialog optionDialog = (JDialog) SwingUtilities.getWindowAncestor(optionPane);
        assertThat(optionDialog).isNotNull();

        // Find and click the Yes button
        JButton yesButton = (JButton) robot().finder().find(optionDialog, component ->
                component instanceof JButton &&
                        "Yes".equals(((JButton) component).getText()));
        assertThat(yesButton).isNotNull();

        robot().click(yesButton);

        // Wait for the dialogs to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify the song is no longer in the playlist
        playlistSongIds = collectionManager.getSongIDsByCollection(createdPlaylist);
        assertThat(playlistSongIds).doesNotContain(firstSong.getID());
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }
}