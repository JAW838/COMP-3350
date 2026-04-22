package beatbinder.presentation.Album;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import beatbinder.logic.CollectionManager;
import beatbinder.objects.SongCollection;
import beatbinder.persistence.*;
import beatbinder.testutils.TestSetupHelper;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import javax.swing.*;

public class LikeAlbumSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private CollectionManager collectionManager;

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
    public void testLikingAlbumAddsItToUserLibrary() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Get the collection panel and verify it's visible and has albums
        window.panel("collectionPanel").requireVisible();
        List<SongCollection> initialAlbums = collectionManager.getAllAlbums();
        assertThat(initialAlbums).isNotEmpty();

        // Find the first album and verify it's initially unliked
        SongCollection firstAlbum = initialAlbums.getFirst();
        assertThat(firstAlbum.isLiked()).isFalse();

        // Click on the album title to open the album view
        window.panel("collectionPanel").label("title_" + firstAlbum.getID()).click();

        // Wait for the album view dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the album view dialog
        JDialog albumViewDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("albumViewDialog"));
        assertThat(albumViewDialog).isNotNull();

        // Find the like button in the album view dialog
        JToggleButton likeButton = (JToggleButton) robot().finder().find(albumViewDialog, component ->
                component instanceof JToggleButton);
        assertThat(likeButton).isNotNull();

        // Click the like button
        robot().click(likeButton);

        // Close the dialog
        albumViewDialog.dispose();

        // Verify the album is now liked
        List<SongCollection> allAlbums = collectionManager.getAllAlbums();
        SongCollection updatedAlbum = null;
        for (SongCollection album : allAlbums) {
            if (album.getID() == firstAlbum.getID()) {
                updatedAlbum = album;
                break;
            }
        }
        assertThat(updatedAlbum).isNotNull();
        assertThat(updatedAlbum.isLiked()).isTrue();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();

        // Wait for the User Library view to load
        window.panel("userLibraryPanel").requireVisible();

        // Verify the liked album appears in the User Library
        window.panel("likedAlbumsPanel").requireVisible();
        List<SongCollection> likedAlbums = collectionManager.getLikedAlbums();
        assertThat(likedAlbums).hasSize(1);
        assertThat(likedAlbums.getFirst().getID()).isEqualTo(firstAlbum.getID());
    }

    @Test
    public void testUnlikingAlbumRemovesItFromUserLibrary() throws Exception {
        // First, set up a liked album
        window.requireVisible();
        window.panel("platformPanel").requireVisible();
        window.panel("collectionPanel").requireVisible();

        List<SongCollection> initialAlbums = collectionManager.getAllAlbums();
        SongCollection firstAlbum = initialAlbums.getFirst();

        // Click on the album title to open the album view
        window.panel("collectionPanel").label("title_" + firstAlbum.getID()).click();

        // Wait for the album view dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the album view dialog
        JDialog albumViewDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("albumViewDialog"));
        assertThat(albumViewDialog).isNotNull();

        // Find the like button in the album view dialog
        JToggleButton likeButton = (JToggleButton) robot().finder().find(albumViewDialog, component ->
                component instanceof JToggleButton);
        assertThat(likeButton).isNotNull();

        // Click the like button to like the album
        robot().click(likeButton);

        // Close the dialog
        albumViewDialog.dispose();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();
        window.panel("userLibraryPanel").requireVisible();
        window.panel("likedAlbumsPanel").requireVisible();

        // Verify the album is in the liked albums
        List<SongCollection> likedAlbums = collectionManager.getLikedAlbums();
        assertThat(likedAlbums).hasSize(1);
        assertThat(likedAlbums.getFirst().getID()).isEqualTo(firstAlbum.getID());

        // Click on the album title in the liked albums panel to open the album view
        window.panel("likedAlbumsPanel").label("title_" + firstAlbum.getID()).click();

        // Wait for the album view dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the album view dialog
        JDialog unlikeAlbumViewDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("albumViewDialog"));
        assertThat(unlikeAlbumViewDialog).isNotNull();

        // Find the like button in the album view dialog
        JToggleButton unlikeButton = (JToggleButton) robot().finder().find(unlikeAlbumViewDialog, component ->
                component instanceof JToggleButton);
        assertThat(unlikeButton).isNotNull();

        // Click the like button to unlike the album
        robot().click(unlikeButton);

        // Close the dialog
        unlikeAlbumViewDialog.dispose();

        // Verify the album is now unliked
        List<SongCollection> allAlbums = collectionManager.getAllAlbums();
        SongCollection updatedAlbum = null;
        for (SongCollection album : allAlbums) {
            if (album.getID() == firstAlbum.getID()) {
                updatedAlbum = album;
                break;
            }
        }
        assertThat(updatedAlbum).isNotNull();
        assertThat(updatedAlbum.isLiked()).isFalse();

        // Verify the album no longer appears in liked albums
        likedAlbums = collectionManager.getLikedAlbums();
        assertThat(likedAlbums).isEmpty();
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }
}
