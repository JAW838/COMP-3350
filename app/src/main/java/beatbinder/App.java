package beatbinder;

import java.awt.EventQueue;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import beatbinder.logic.SongManager;
import beatbinder.logic.validation.SongCollectionValidator;
import beatbinder.logic.validation.SongValidator;
import beatbinder.logic.CollectionManager;
import beatbinder.logic.TagManager;
import beatbinder.logic.validation.TagValidator;
import beatbinder.persistence.PersistenceFactory;
import beatbinder.persistence.PersistenceType;
import beatbinder.persistence.ISongPersistence;
import beatbinder.persistence.ITagPersistence;
import beatbinder.persistence.ICollectionPersistence;
import beatbinder.presentation.MainWindow;
import beatbinder.presentation.interfaces.IPageView;
import beatbinder.presentation.pages.PlatformView;
import beatbinder.presentation.pages.UserLibraryView;
import beatbinder.presentation.text.content.TextInitialiser;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting Beat Binder...");

        // Initialize persistence
        PersistenceFactory.initialise(PersistenceType.PROD, true);
        
        // Initialize text
        TextInitialiser.initailizeText();

        EventQueue.invokeLater(new Runnable() {

            public void run() {

                for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) { // nimbus look and feel
                    if ("Nimbus".equals(info.getName())) {
                        try {
                            UIManager.setLookAndFeel(info.getClassName());
                        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                                | UnsupportedLookAndFeelException e) {

                            e.printStackTrace();
                        }
                        break;
                    }
                }

                // Get persistence instances
                ISongPersistence songPersistence = PersistenceFactory.getSongPersistence();
                ICollectionPersistence collectionPersistence = PersistenceFactory.getCollectionPersistence();
                ITagPersistence tagPersistence = PersistenceFactory.getTagPersistence();
                                SongValidator songValidator = new SongValidator();
                SongCollectionValidator collValidator = new SongCollectionValidator();

                // Create managers
                SongManager songManager = new SongManager(songPersistence, songValidator);
                CollectionManager collectionManager = new CollectionManager(collectionPersistence, songValidator, collValidator);
                TagManager tagManager = new TagManager(tagPersistence, new TagValidator(), songValidator);

                List<IPageView> views = List.of(
                        new PlatformView(songManager, collectionManager, tagManager),
                        new UserLibraryView(songManager, collectionManager, tagManager));
                // Create and show main window
                new MainWindow(views, tagManager);
            }
        });
    }
}
