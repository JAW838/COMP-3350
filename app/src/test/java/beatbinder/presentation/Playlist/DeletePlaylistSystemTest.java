package beatbinder.presentation.Playlist;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import beatbinder.logic.CollectionManager;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.testutils.TestSetupHelper;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import javax.swing.*;

public class DeletePlaylistSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private CollectionManager collectionManager;
    private final String playlistName = "Test Playlist";

    @Override
    protected void onSetUp() throws Exception {
        // Use TestSetupHelper to initialize the test environment
        TestSetupHelper.TestSetup setup = TestSetupHelper.initializeTestEnvironment(robot());
        window = setup.window;
        collectionManager = setup.collectionManager;

        // Wait for GUI initialization
        robot().waitForIdle();
        Thread.sleep(1000);

        // Ensure the main window is ready before testing
        window.requireVisible();
    }

    @Test
    public void testDeletePlaylist() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();

        // Wait for the User Library view to load
        window.panel("userLibraryPanel").requireVisible();

        // Create a playlist to delete
        createPlaylist(playlistName);

        // Verify the playlist was created
        List<SongCollection> playlists = collectionManager.getAllPlaylists();
        SongCollection createdPlaylist = playlists.stream()
                .filter(p -> p.getTitle().equals(playlistName))
                .findFirst()
                .orElse(null);

        assertThat(createdPlaylist).isNotNull();
        assertThat(createdPlaylist.getTitle()).isEqualTo(playlistName);

        // Click on the playlist title to open the playlist view
        window.panel("userLibraryPanel").label("title_" + createdPlaylist.getID()).click();

        // Wait for the playlist view dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the playlist view dialog
        JDialog playlistViewDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("playlistViewDialog"));
        assertThat(playlistViewDialog).isNotNull();

        // Find the delete button
        JButton deleteButton = (JButton) robot().finder().find(playlistViewDialog, component ->
                component instanceof JButton &&
                        "playlistDeleteButton".equals(component.getName()));
        assertThat(deleteButton).isNotNull();

        // Click the delete button
        robot().click(deleteButton);

        // Wait for the confirmation dialog
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the confirmation dialog
        JOptionPane confirmPane = (JOptionPane) robot().finder().find(component ->
                component instanceof JOptionPane);
        assertThat(confirmPane).isNotNull();

        // Get the dialog containing the confirmation pane
        JDialog confirmDialog = (JDialog) SwingUtilities.getWindowAncestor(confirmPane);
        assertThat(confirmDialog).isNotNull();

        // Find and click the Yes button on the confirmation dialog
        JButton yesButton = (JButton) robot().finder().find(confirmDialog, component ->
                component instanceof JButton &&
                        "Yes".equals(((JButton) component).getText()));
        assertThat(yesButton).isNotNull();
        robot().click(yesButton);

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify the playlist was deleted
        List<SongCollection> remainingPlaylists = collectionManager.getAllPlaylists();
        boolean playlistExists = remainingPlaylists.stream()
                .anyMatch(p -> p.getTitle().equals(playlistName));
        assertThat(playlistExists).isFalse();
    }

    private void createPlaylist(String name) throws Exception {
        // Find the create playlist button and click it
        window.panel("userLibraryPanel").button("createPlaylistButton").click();

        // Wait for the create playlist dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the create playlist dialog
        JDialog createPlaylistDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("createPlaylistDialog"));

        // Locate the playlist name text field
        JTextField playlistNameField = (JTextField) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JTextField &&
                        "playlistNameField".equals(component.getName()));

        // Enter the playlist name
        robot().focus(playlistNameField);
        robot().enterText(name);

        // Find the confirm button
        JButton confirmCreationButton = (JButton) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JButton &&
                        "confirmPlaylistCreationButton".equals(component.getName()));

        // Click the confirm button to create the playlist
        robot().click(confirmCreationButton);

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Handle any confirmation message if it appears
        try {
            JButton okButton = (JButton) robot().finder().find(component ->
                    component instanceof JButton &&
                            "OK".equals(((JButton) component).getText()));
            if (okButton != null) {
                robot().click(okButton);
            }
        } catch (Exception e) {
            // No confirmation dialog found, which is fine
        }
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }
}