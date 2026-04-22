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

public class CreatePlaylistSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private CollectionManager collectionManager;
    private final String playlistName = "Test Playlist";
    private final String playlistName2 = "Another Playlist";

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
    public void testCreatePlaylist() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();

        // Wait for the User Library view to load
        window.panel("userLibraryPanel").requireVisible();

        // Find the create playlist button and click it
        window.panel("userLibraryPanel").button("createPlaylistButton").click();

        // Wait for the create playlist dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the create playlist dialog
        JDialog createPlaylistDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("createPlaylistDialog"));

        // Ensure the dialog is visible
        assertThat(createPlaylistDialog).isNotNull();
        assertThat(createPlaylistDialog.isVisible()).isTrue();

        // Locate the playlist name text field
        JTextField playlistNameField = (JTextField) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JTextField &&
                        "playlistNameField".equals(component.getName()));
        assertThat(playlistNameField).isNotNull();

        // Enter the playlist name
        robot().focus(playlistNameField);
        robot().enterText(playlistName);

        // Find the confirm button
        JButton confirmCreationButton = (JButton) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JButton &&
                        "confirmPlaylistCreationButton".equals(component.getName()));
        assertThat(confirmCreationButton).isNotNull();

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

        // Verify the playlist was created
        List<SongCollection> playlists = collectionManager.getAllPlaylists();
        SongCollection createdPlaylist = playlists.stream()
                .filter(p -> p.getTitle().equals(playlistName))
                .findFirst()
                .orElse(null);

        assertThat(createdPlaylist).isNotNull();
        assertThat(createdPlaylist.getTitle()).isEqualTo(playlistName);
    }

    @Test
    public void testCreateMultiplePlaylists() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();

        // Wait for the User Library view to load
        window.panel("userLibraryPanel").requireVisible();

        // Create first playlist
        createPlaylistWithName(playlistName);

        // Create second playlist
        createPlaylistWithName(playlistName2);

        // Verify both playlists were created
        List<SongCollection> playlists = collectionManager.getAllPlaylists();

        SongCollection firstPlaylist = playlists.stream()
                .filter(p -> p.getTitle().equals(playlistName))
                .findFirst()
                .orElse(null);

        SongCollection secondPlaylist = playlists.stream()
                .filter(p -> p.getTitle().equals(playlistName2))
                .findFirst()
                .orElse(null);

        assertThat(firstPlaylist).isNotNull();
        assertThat(secondPlaylist).isNotNull();
        assertThat(firstPlaylist.getTitle()).isEqualTo(playlistName);
        assertThat(secondPlaylist.getTitle()).isEqualTo(playlistName2);
    }

    private void createPlaylistWithName(String name) throws Exception {
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

    @Test
    public void testCancelPlaylistCreation() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();
        window.panel("userLibraryPanel").requireVisible();

        // Get initial playlist count
        int initialPlaylistCount = collectionManager.getAllPlaylists().size();

        // Open create playlist dialog
        window.panel("userLibraryPanel").button("createPlaylistButton").click();
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the create playlist dialog
        JDialog createPlaylistDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("createPlaylistDialog"));

        // Enter a playlist name
        JTextField playlistNameField = (JTextField) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JTextField &&
                        "playlistNameField".equals(component.getName()));
        robot().focus(playlistNameField);
        robot().enterText(playlistName);

        // Find and click the cancel button
        JButton cancelButton = (JButton) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JButton &&
                        "cancelPlaylistCreationButton".equals(component.getName()));
        robot().click(cancelButton);

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify no playlist was created
        int finalPlaylistCount = collectionManager.getAllPlaylists().size();
        assertThat(finalPlaylistCount).isEqualTo(initialPlaylistCount);
    }

    @Test
    public void testCreateDuplicatePlaylist() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();
        window.panel("userLibraryPanel").requireVisible();

        // Create first playlist
        createPlaylistWithName(playlistName);

        // Get playlist count after first creation
        int playlistCountAfterFirstCreation = collectionManager.getAllPlaylists().size();

        // Try to create a playlist with the same name
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

        // Enter the same playlist name
        robot().focus(playlistNameField);
        robot().enterText(playlistName);

        // Find the confirm button
        JButton confirmCreationButton = (JButton) robot().finder().find(createPlaylistDialog, component ->
                component instanceof JButton &&
                        "confirmPlaylistCreationButton".equals(component.getName()));

        // Click the confirm button to attempt to create the duplicate playlist
        robot().click(confirmCreationButton);

        // Wait for the error dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the error dialog
        JOptionPane errorPane = (JOptionPane) robot().finder().find(component ->
                component instanceof JOptionPane);
        assertThat(errorPane).isNotNull();

        // Get the dialog containing the error pane
        JDialog errorDialog = (JDialog) SwingUtilities.getWindowAncestor(errorPane);
        assertThat(errorDialog).isNotNull();

        // Find and click the OK button on the error dialog
        JButton okButton = (JButton) robot().finder().find(errorDialog, component ->
                component instanceof JButton &&
                        "OK".equals(((JButton) component).getText()));
        assertThat(okButton).isNotNull();
        robot().click(okButton);

        // Wait for the error dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify that the create playlist dialog has closed
        assertThat(createPlaylistDialog.isVisible()).isFalse();

        // Verify that no new playlist was created (count should be the same)
        int finalPlaylistCount = collectionManager.getAllPlaylists().size();
        assertThat(finalPlaylistCount).isEqualTo(playlistCountAfterFirstCreation);

        // Verify that there is only one playlist with the given name
        List<SongCollection> playlists = collectionManager.getAllPlaylists();
        long playlistsWithSameName = playlists.stream()
                .filter(p -> p.getTitle().equals(playlistName))
                .count();
        assertThat(playlistsWithSameName).isEqualTo(1);
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }
}
