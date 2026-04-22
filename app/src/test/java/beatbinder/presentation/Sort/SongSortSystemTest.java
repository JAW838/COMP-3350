package beatbinder.presentation.Sort;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import beatbinder.objects.Tag;
import beatbinder.presentation.components.SortOption;
import beatbinder.testutils.TagHelper;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.JListFixture;

import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.Song;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.testutils.TestSetupHelper;

public class SongSortSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private SongManager songManager;
    private TagManager tagManager;

    @Override
    protected void onSetUp() throws Exception {
        // Use TestSetupHelper to initialize the test environment
        TestSetupHelper.TestSetup setup = TestSetupHelper.initializeTestEnvironment(robot());
        window = setup.window;
        songManager = setup.songManager;
        tagManager = setup.tagManager;

        // Wait for GUI initialization
        robot().waitForIdle();
        Thread.sleep(1000);

        // Ensure the main window is ready before testing
        window.requireVisible();
    }

    /**
     * Helper method to get the artist label from a song's info panel
     * @param songId The ID of the song
     * @return The JLabel containing the artist name
     */
    private JLabel getArtistLabelFromInfoPanel(int songId) {
        JPanel infoPanel = window.panel("songPanel").panel("infoPanel_" + songId).target();
        assertThat(infoPanel).isNotNull();

        // Find the artist label (second label in the panel)
        JLabel artistLabel = null;
        int labelCount = 0;
        for (int i = 0; i < infoPanel.getComponentCount(); i++) {
            if (infoPanel.getComponent(i) instanceof JLabel) {
                labelCount++;
                if (labelCount == 2) {
                    artistLabel = (JLabel) infoPanel.getComponent(i);
                    break;
                }
            }
        }
        assertThat(artistLabel).isNotNull();
        return artistLabel;
    }

    @Test
    public void testSortByArtist() throws Exception {
        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Get songs
        window.panel("songPanel").requireVisible();
        List<Song> initialSongs = songManager.getAllSongs();
        assertThat(initialSongs).isNotEmpty();

        // Get first song
        Song firstSong = initialSongs.getFirst();

        // Get and verify the artist label for the first song
        JLabel artistLabel = getArtistLabelFromInfoPanel(firstSong.getID());
        assertThat(artistLabel.getText()).isEqualTo(firstSong.getArtist());

        // Click the sort button
        window.panel("platformPanel").button("sortButton").click();

        // Wait for the popup menu to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find the "Artist" menu item
        JMenuItem artistMenuItem = (JMenuItem) robot().finder().find(component ->
                component instanceof JMenuItem &&
                "Artist".equals(((JMenuItem) component).getText())
        );
        assertThat(artistMenuItem).isNotNull();

        // Click the "Artist" menu item
        robot().click(artistMenuItem);

        // Wait for the sorting to complete
        robot().waitForIdle();
        Thread.sleep(500);

        // Get the sorted songs
        List<Song> sortedSongs = songManager.sortSongs(songManager.getAllSongs(), SortOption.ARTIST);
        assertThat(sortedSongs).isNotEmpty();

        // Get the first song after sorting
        Song firstSortedSong = sortedSongs.getFirst();

        // Get and verify the artist label for the sorted first song
        JLabel sortedArtistLabel = getArtistLabelFromInfoPanel(firstSortedSong.getID());
        assertThat(sortedArtistLabel.getText()).isEqualTo(firstSortedSong.getArtist());
    }

    @Test
    public void sortByTagShowsOnlyOneSong() throws Exception {
        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();

        // Get songs
        window.panel("songPanel").requireVisible();
        List<Song> initialSongs = songManager.getAllSongs();
        assertThat(initialSongs).isNotEmpty();
        assertThat(initialSongs.size()).isGreaterThanOrEqualTo(2);

        // Get first song
        Song firstSong = initialSongs.getFirst();
        // Get first song panel
        window.panel("songPanel").panel("infoPanel_" + firstSong.getID()).requireVisible();
        // Get second song
        Song secondSong = initialSongs.get(1);
        // Get second song panel
        window.panel("songPanel").panel("infoPanel_" + secondSong.getID()).requireVisible();

        // Create a tag to add to the first song
        String tagName = "TestTagForFiltering";
        DialogFixture tagDialogFixture = TagHelper.createTag(window, robot(), tagName);

        // Verify the tag was created
        List<Tag> tagList = tagManager.getAllTags();
        assertThat(tagList).hasSize(1);
        assertThat(tagList.getFirst().getName()).isEqualTo(tagName);

        // Close the tag dialog
        tagDialogFixture.close();

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Click the song menu button to show the popup menu for the first song
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

        // Close the song details dialog
        songDetailsFixture.close();

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify both song panels are still visible
        window.panel("songPanel").panel("infoPanel_" + firstSong.getID()).requireVisible();
        window.panel("songPanel").panel("infoPanel_" + secondSong.getID()).requireVisible();

        // Replace the tag finding section with this:
        // Find and click the tag in the tag list panel
        window.panel("tagBarPanel").panel("tagListPanel").requireVisible();

        // Find the TagPanel component in the tag list panel
        // Verify the tag appears in the main tag panel
        JLabel tag = window.panel("tagListPanel").requireVisible()
                .label().requireText(tagName).target();


        // Click the found tag panel
        robot().click(tag);

        // Wait for the filtering to complete
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify that only the first song panel is visible and the second is not
        window.panel("songPanel").panel("infoPanel_" + firstSong.getID()).requireVisible();

        boolean exists = false;
        try {
            window.panel("songPanel").panel("infoPanel_" + secondSong.getID());
            exists = true;
        } catch (ComponentLookupException e) {
            // Component not found, which is what we want
        }
        assertThat(exists).isFalse();
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }
}