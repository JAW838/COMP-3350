package beatbinder.presentation.Tag;

import beatbinder.logic.TagManager;
import beatbinder.objects.Tag;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.presentation.text.content.ErrorTexter;
import beatbinder.testutils.TagHelper;
import beatbinder.testutils.TestSetupHelper;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JListFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;

import javax.swing.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateTagSystemTest extends AssertJSwingJUnitTestCase {

    private FrameFixture window;
    private TagManager tagManager;

    @Override
    protected void onSetUp() throws Exception {  // Remove throws Exception as it's not necessary
        // Use TestSetupHelper to initialize the test environment
        TestSetupHelper.TestSetup setup = TestSetupHelper.initializeTestEnvironment(robot());
        window = setup.window;
        tagManager = setup.tagManager;

        // Wait for GUI initialization
        robot().waitForIdle();
        Thread.sleep(1000);

        // Ensure the main window is ready before testing
        window.requireVisible();
    }

    @Test
    public void createTagAppearsInTagList() throws Exception {
        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();
        window.panel("tagBarPanel").requireVisible();

        List<Tag> initialTags = tagManager.getAllTags();
        assertThat(initialTags).hasSize(0);

        // Create the tag
        String tagName = "NewTag";
        DialogFixture dialogFixture = TagHelper.createTag(window, robot(), tagName);

        // Verify the tag was created
        List<Tag> tagList = tagManager.getAllTags();
        assertThat(tagList).hasSize(1);
        assertThat(tagList.getFirst().getName()).isEqualTo(tagName);

        // Check it appears in the selection panel
        dialogFixture.panel("tagSelectionPanel").requireVisible();
        JListFixture listFixture = dialogFixture.panel("tagSelectionPanel").list("itemList");
        listFixture.requireVisible()
            .requireItemCount(1);
        assertThat(listFixture.contents()).contains(tagName);

        // Close the dialog
        dialogFixture.close();

        // Wait for the dialog to close and main window to update
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify the tag appears in the main tag panel
        window.panel("tagListPanel").requireVisible()
                .label().requireText(tagName);
    }


    @Test
    public void createTagWithExistingNameShowsErrorMessage() throws Exception {
        // Verify we're on the platform view
        window.panel("platformPanel").requireVisible();
        window.panel("tagBarPanel").requireVisible();

        List<Tag> initialTags = tagManager.getAllTags();
        assertThat(initialTags).hasSize(0);

        // Create first tag
        String tagName = "DuplicateTagTest";
        DialogFixture dialogFixture = TagHelper.createTag(window, robot(), tagName);

        // Verify the tag was created
        List<Tag> tagList = tagManager.getAllTags();
        assertThat(tagList).hasSize(1);
        assertThat(tagList.getFirst().getName()).isEqualTo(tagName);

        // Try to create a tag with the same name
        dialogFixture.button("createTagButton").click();

        // Find and interact with the input dialog again
        JOptionPaneFixture secondInputPane = JOptionPaneFinder.findOptionPane()
                .using(robot());

        // Enter the same tag name and click OK
        secondInputPane.textBox().enterText(tagName);
        secondInputPane.okButton().click();

        // Wait for the error dialog to appear
        robot().waitForIdle();
        Thread.sleep(500);

        // Find and verify the error dialog
        JOptionPaneFixture errorPane = JOptionPaneFinder.findOptionPane()
                .using(robot());

        // Verify the error message
        errorPane.requireTitle(ErrorTexter.windowName());
        errorPane.requireMessage(ErrorTexter.platformTagDuplicate());

        // Close the error dialog
        errorPane.okButton().click();

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);

        // Verify no new tag was created
        List<Tag> finalTagList = tagManager.getAllTags();
        assertThat(finalTagList).hasSize(1);

        // Close the dialog
        dialogFixture.close();

        // Wait for the dialog to close
        robot().waitForIdle();
        Thread.sleep(500);
    }

    @Override
    protected void onTearDown() {
        if (window != null) {
            window.cleanUp();
        }
        PersistenceFactory.reset();
    }

}
