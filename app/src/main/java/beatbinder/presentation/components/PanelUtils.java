package beatbinder.presentation.components;

import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * 
 */
public class PanelUtils {

    /**
     * Creates a titled {@link JPanel} containing a scrollable content area.
     * 
     * @param title        the title of the border around the panel.
     * @param contentPanel the content to place inside the scrollable area.
     * @return a {@link JPanel} with a titled border and scrollable content.
     */
    public static JPanel createTitledScrollSection(String title, JPanel contentPanel) {
        return createTitledScrollSection(title, contentPanel, null, null, false);
    }

    /**
     * Creates a titled {@link JPanel} containing a scrollable content area.
     * 
     * @param title        the title of the border around the panel.
     * @param contentPanel the content to place inside the scrollable area.
     * @param headerButton a {@link JButton} to place in the specified region
     *                     (e.g., BorderLayout.NORTH).
     * @param region       the region of the {@link BorderLayout} to place the
     *                     {@code headerButton}.
     * @return a {@link JPanel} with a titled border and scrollable content.
     */
    public static JPanel createTitledScrollSection(String title, JPanel contentPanel, JButton headerButton,
            String region) {
        return createTitledScrollSection(title, contentPanel, headerButton, region, false);
    }

    /**
     * Creates a titled {@link JPanel} containing a scrollable content area.
     * 
     * @param title          the title of the border around the panel.
     * @param contentPanel   the content to place inside the scrollable area.
     * @param verticalLayout if {@code true}, sets the content panel's layout to
     *                       vertical
     *                       {@link BoxLayout}.
     * @return a {@link JPanel} with a titled border and scrollable content.
     */
    public static JPanel createTitledScrollSection(String title, JPanel contentPanel, boolean verticalLayout) {
        return createTitledScrollSection(title, contentPanel, null, null, verticalLayout);
    }

    /**
     * Creates a titled {@link JPanel} containing a scrollable content area.
     * Optionally adds a button to the specified region of the panel border layout.
     * 
     * @param title          the title of the border around the panel.
     * @param contentPanel   the content to place inside the scrollable area.
     * @param headerButton   an optional {@link JButton} to place in the specified
     *                       region
     *                       (e.g., BorderLayout.NORTH).
     * @param region         the region of the {@link BorderLayout} to place the
     *                       {@code headerButton},
     *                       if it is not {@code null}.
     * @param verticalLayout if {@code true}, sets the content panel’s layout to
     *                       vertical
     *                       {@link BoxLayout}.
     * @return a {@link JPanel} with a titled border and scrollable content.
     */
    public static JPanel createTitledScrollSection(String title, JPanel contentPanel, JButton headerButton,
            String region,
            boolean verticalLayout) {
        if (verticalLayout) {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        }

        JScrollPane scrollPane = createScrollPane(contentPanel);

        // create the panel
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createTitledBorder(title));

        // place headerButton
        if (headerButton != null) {
            wrapper.add(headerButton, region);
        }

        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Replaces all content in {@code panel} with {@code items} while applying their
     * respective
     * functions.
     * 
     * @param panel  the {@link JPanel} to be populated.
     * @param items  the items to be placed in {@code panel}.
     * @param mapper the action that should happen when an item is selected.
     */
    public static <T> void populatePanel(JPanel panel, List<T> items, Function<T, JPanel> mapper) {
        panel.removeAll();
        for (T item : items) {
            panel.add(mapper.apply(item));
        }
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Creates a {@link JScrollPane} with preset scroll settings.
     * 
     * @param contentPanel the content to place in the {@link JScrollPane}.
     * @return the new {@link JScrollPane}.
     */
    private static JScrollPane createScrollPane(JPanel contentPanel) {
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        // Set the vertical scrollbar to always be visible
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }
}
