package beatbinder.testutils;

import javax.swing.JDialog;
import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.Robot;
import org.assertj.swing.finder.JOptionPaneFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JOptionPaneFixture;

public class TagHelper {

    /**
     * Finds the tag management dialog
     * @param robot The robot to use for finding the dialog
     * @return The found dialog
     */
    public static JDialog getCreateTagDialog(Robot robot) {
        return robot.finder().find(new GenericTypeMatcher<JDialog>(JDialog.class) {
            @Override
            protected boolean isMatching(JDialog dialog1) {
                return "manageTagsDialog".equals(dialog1.getName()) && dialog1.isVisible();
            }
        });
    }

    /**
     * Creates a new tag with the given name
     * @param window The main application window
     * @param robot The robot to use for UI interaction
     * @param tagName The name of the tag to create
     * @return The dialog fixture for the tag management dialog
     * @throws Exception If an error occurs during tag creation
     */
    public static DialogFixture createTag(FrameFixture window, Robot robot, String tagName) throws Exception {
        // Click tag menu button
        window.panel("tagBarPanel").button("tagMenuButton").requireVisible().requireEnabled().click();

        // Wait for popup
        robot.waitForIdle();
        Thread.sleep(500);

        // Find the dialog
        JDialog dialog = getCreateTagDialog(robot);
        dialog.isVisible();

        // Create the DialogFixture
        DialogFixture dialogFixture = new DialogFixture(robot, dialog);

        // Click create button
        dialogFixture.button("createTagButton").requireVisible().isEnabled();
        dialogFixture.button("createTagButton").click();

        // Find and interact with the input dialog
        JOptionPaneFixture optionPane = JOptionPaneFinder.findOptionPane()
                .using(robot);
        optionPane.requireVisible();

        // Enter text and click OK
        optionPane.textBox().enterText(tagName);
        optionPane.okButton().click();

        // Wait for the dialog to close
        robot.waitForIdle();
        Thread.sleep(500);

        return dialogFixture;
    }
}
