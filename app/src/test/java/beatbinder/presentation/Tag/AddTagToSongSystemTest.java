package beatbinder.presentation.Tag;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.objects.Tag;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.testutils.TagHelper;
import beatbinder.testutils.TestSetupHelper;

public class AddTagToSongSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private TagManager tagManager;
    private SongManager songManager;

    @Override
    protected void onSetUp() throws Exception {
        // Use TestSetupHelper to initialize the test environment
        TestSetupHelper.TestSetup setup = TestSetupHelper.initializeTestEnvironment(robot());
        window = setup.window;
        tagManager = setup.tagManager;
        songManager = setup.songManager;

        // Wait for GUI initialization
        robot().waitForIdle();
        Thread.sleep(1000);

        // Ensure the main window is ready before testing
        window.requireVisible();
    }

    @Test
    public void testAddTagToSong() throws Exception {
        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();
        window.panel("tagBarPanel").requireVisible();

        // Get all songs and verify there are songs available
        List<Song> initialSongs = songManager.getAllSongs();
        assertThat(initialSongs).isNotEmpty();

        // Get the first song to add a tag to
        Song firstSong = initialSongs.getFirst();

        // Create a tag to add to the song
        String tagName = "TestTagForSong";
        DialogFixture tagDialogFixture = TagHelper.createTag(window, robot(), tagName);

        // Verify the tag was created
        List<Tag> tagList = tagManager.getAllTags();
        assertThat(tagList).hasSize(1);
        assertThat(tagList.getFirst().getName()).isEqualTo(tagName);

        // Find the created tag
        Tag createdTag = tagList.stream()
                .filter(tag -> tag.getName().equals(tagName))
                .findFirst()
                .orElse(null);
        assertThat(createdTag).isNotNull();

        // Close the tag dialog
        tagDialogFixture.close();

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Get the initial tags of the song
        List<Tag> initialSongTags = tagManager.getTagsOfSong(firstSong);

        // Click the song menu button to show the popup menu
        window.panel("songPanel").button("menuButton_" + firstSong.getID()).click();

        // Wait for the popup menu to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the song details button within the popup menu
        JMenuItem songDetailItem = (JMenuItem) robot().finder().find(component ->
                component instanceof JMenuItem &&
                        ("detailsItem_" + firstSong.getID()).equals(component.getName())
        );
        assertThat(songDetailItem).isNotNull();

        // Click the song details button
        robot().click(songDetailItem);

        // Wait for the SongDetailsView dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the song details dialog
        JDialog songDetailsDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.getName().equals("songDetailsViewDialog"));
        assertThat(songDetailsDialog).isNotNull();

        // Create a DialogFixture for the song details dialog
        DialogFixture songDetailsFixture = new DialogFixture(robot(), songDetailsDialog);

        // Find the tag menu button using the DialogFixture
        songDetailsFixture.button("tagMenuButton").click();

        // Wait for the popup menu to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the "Add Tag" menu item
        JMenuItem addTagItem = (JMenuItem) robot().finder().find(component ->
                component instanceof JMenuItem &&
                        "addTagItem".equals(component.getName())
        );
        assertThat(addTagItem).isNotNull();

        // Click the "Add Tag" menu item
        robot().click(addTagItem);

        // Wait for the AddTagDialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the AddTagDialog
        JDialog addTagDialog = (JDialog) robot().finder().find(component ->
                component instanceof JDialog &&
                        component.isVisible() &&
                        component != songDetailsDialog
        );
        assertThat(addTagDialog).isNotNull();

        // Create a DialogFixture for the add tag dialog
        DialogFixture addTagFixture = new DialogFixture(robot(), addTagDialog);

        // Find the JList within the dialog using the DialogFixture
        JListFixture listFixture = addTagFixture.list("itemList");
        listFixture.requireVisible();

        // Select the tag by its name
        SwingUtilities.invokeAndWait(() -> {
            JList<?> itemList = listFixture.target();
            for (int i = 0; i < itemList.getModel().getSize(); i++) {
                Object item = itemList.getModel().getElementAt(i);
                if (item instanceof Tag && ((Tag) item).getName().equals(tagName)) {
                    itemList.setSelectedIndex(i);
                    break;
                }
            }
        });

        Thread.sleep(500);

        // Click the confirm button using the DialogFixture
        addTagFixture.button("confirmButton").click();

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify the tag was added to the song
        List<Tag> updatedSongTags = tagManager.getTagsOfSong(firstSong);
        assertThat(updatedSongTags.size()).isGreaterThan(initialSongTags.size());

        // Verify the specific tag was added
        boolean tagFound = updatedSongTags.stream()
                .anyMatch(tag -> tag.getName().equals(tagName));
        assertThat(tagFound).isTrue();
    }


    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }
}
