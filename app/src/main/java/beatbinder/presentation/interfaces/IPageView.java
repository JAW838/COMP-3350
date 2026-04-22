package beatbinder.presentation.interfaces;

import javax.swing.JPanel;

/**
 * Interface for pages which display information about songs and collections in variable ways.
 */
public interface IPageView {
    /**
     * Retrieves the unique page key.
     * @return
     */
    String getKey();
    /**
     * Retrieves the display name of the page.
     * @return the display name.
     */
    String getDisplayName();
    /**
     * Retrieves the {@link JPanel} displayed by the page.
     * @return
     */
    JPanel getPanel();
    /**
     * Refreshes the page.
     * <p>
     * No refresh by default.
     */
    default void refresh() {
        // No refresh by default
    }
}
