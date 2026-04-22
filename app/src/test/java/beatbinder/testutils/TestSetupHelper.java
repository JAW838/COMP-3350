package beatbinder.testutils;

import beatbinder.logic.*;
import beatbinder.logic.validation.*;
import beatbinder.persistence.*;
import beatbinder.presentation.MainWindow;
import beatbinder.presentation.pages.*;
import beatbinder.presentation.text.content.TextInitialiser;
import javax.swing.*;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;

import java.util.List;

public class TestSetupHelper {

    public static class TestSetup {
        public FrameFixture window;
        public SongManager songManager;
        public TagManager tagManager;
        public CollectionManager collectionManager;
    }

    public static TestSetup initializeTestEnvironment(org.assertj.swing.core.Robot robot) throws Exception {
        // Initialize text
        TextInitialiser.initailizeText();

        // Set Nimbus Look-and-Feel (as in App) - Now wrapped in SwingUtilities.invokeAndWait
        SwingUtilities.invokeAndWait(() -> {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        });

        // Rest of the method remains the same...
        PersistenceFactory.initialise(PersistenceType.TEST, true);
        // ... (rest of the existing code)
        ISongPersistence songPersistence = PersistenceFactory.getSongPersistence();
        ITagPersistence tagPersistence = PersistenceFactory.getTagPersistence();
        ICollectionPersistence collectionPersistence = PersistenceFactory.getCollectionPersistence();

        SongValidator songValidator = new SongValidator();
        TagValidator tagValidator = new TagValidator();
        SongCollectionValidator collValidator = new SongCollectionValidator();

        SongManager songManager = new SongManager(songPersistence, songValidator);
        TagManager tagManager = new TagManager(tagPersistence, tagValidator, songValidator);
        CollectionManager collectionManager = new CollectionManager(collectionPersistence, songValidator, collValidator);

        // Set up views
        PlatformView platformView = GuiActionRunner.execute(() ->
                new PlatformView(songManager, collectionManager, tagManager)
        );
        UserLibraryView userLibraryView = GuiActionRunner.execute(() ->
                new UserLibraryView(songManager, collectionManager, tagManager)
        );

        // Create main window
        MainWindow mainWindow = GuiActionRunner.execute(() ->
                new MainWindow(List.of(platformView, userLibraryView), tagManager)
        );

        // Attach FrameFixture to the MainWindow
        FrameFixture window = new FrameFixture(robot, mainWindow);

        // Wait for GUI initialization
        robot.waitForIdle();
        Thread.sleep(1000);

        // Return everything needed for the test
        TestSetup setup = new TestSetup();
        setup.window = window;
        setup.songManager = songManager;
        setup.tagManager = tagManager;
        setup.collectionManager = collectionManager;
        return setup;
    }
}