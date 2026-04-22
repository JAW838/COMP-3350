package beatbinder.presentation.Song;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import beatbinder.logic.SongManager;
import beatbinder.objects.Song;
import beatbinder.persistence.*;
import beatbinder.testutils.TestSetupHelper;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

public class LikeSongSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private SongManager songManager;

    @Override
    protected void onSetUp() throws Exception {
        // Use TestSetupHelper to initialize the test environment
        TestSetupHelper.TestSetup setup = TestSetupHelper.initializeTestEnvironment(robot());
        window = setup.window;
        songManager = setup.songManager;

        // Wait for GUI initialization
        robot().waitForIdle();
        Thread.sleep(1000);;

        // Ensure the main window is ready before testing
        window.requireVisible();
    }

    @Test
    public void testLikingSongAddsItToUserLibrary() throws Exception {
        // Wait for the UI to be ready
        window.requireVisible();

        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Get the song panel and verify it's visible and has songs
        window.panel("songPanel").requireVisible();
        List<Song> initialSongs = songManager.getAllSongs();
        assertThat(initialSongs).isNotEmpty();

        // Find the first song and verify it's initially unliked
        Song firstSong = initialSongs.getFirst();
        assertThat(firstSong.isLiked()).isFalse();

        // Click the like button for the first song
        window.panel("songPanel")
                .button("likeButton_" + firstSong.getID())
                .click();

        Thread.sleep(500);

        // Verify the song is now liked
        Song updatedSong = songManager.getSongByID(firstSong.getID());
        assertThat(updatedSong.isLiked()).isTrue();

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();

        // Wait for the User Library view to load
        Thread.sleep(500);
        // Check we're on the right page
        window.panel("userLibraryPanel").requireVisible();

        // Verify the liked song appears in the User Library
        window.panel("likedSongsPanel").requireVisible();
        List<Song> likedSongs = songManager.getLikedSongs();
        assertThat(likedSongs).hasSize(1);
        assertThat(likedSongs.getFirst().getID()).isEqualTo(firstSong.getID());

        // Verify the song panel exists in the liked songs section
        window.panel("likedSongsPanel")
                .panel("infoPanel_" + firstSong.getID())
                .requireVisible();


    }

    @Test
    public void testUnlikingSongRemovesItFromUserLibrary() throws Exception {
        // First, set up a liked song
        window.requireVisible();
        window.panel("platformPanel").requireVisible();
        window.panel("songPanel").requireVisible();
        
        List<Song> initialSongs = songManager.getAllSongs();
        Song firstSong = initialSongs.getFirst();
        window.panel("songPanel")
                .button("likeButton_" + firstSong.getID())
                .click();

        Thread.sleep(500);

        // Switch to User Library view
        window.panel("topPanel").toggleButton("switchViewButton").click();
        window.panel("userLibraryPanel").requireVisible();
        window.panel("likedSongsPanel").requireVisible();

        // Unlike the song from the User Library
        window.panel("likedSongsPanel")
                .button("likeButton_" + firstSong.getID())
                .click();

        Thread.sleep(500);

        // Verify the song is now unliked
        Song updatedSong = songManager.getSongByID(firstSong.getID());
        assertThat(updatedSong.isLiked()).isFalse();

        // Verify the song no longer appears in liked songs
        List<Song> likedSongs = songManager.getLikedSongs();
        assertThat(likedSongs).isEmpty();
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }
}