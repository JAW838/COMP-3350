package beatbinder.presentation;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import beatbinder.logic.TagManager;
import beatbinder.objects.Tag;
import beatbinder.presentation.components.PanelUtils;
import beatbinder.presentation.components.TagPanel;
import beatbinder.presentation.interfaces.IPageView;
import beatbinder.presentation.interfaces.ISearchable;
import beatbinder.presentation.interfaces.ITagFilterable;
import beatbinder.presentation.pages.PlatformView;
import beatbinder.presentation.pages.UserLibraryView;
import beatbinder.presentation.popups.ManagePlatformTagsDialog;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.MainWindowTexter;

/**
 * The central window of the application.
 * <p>
 * Responsible for managing and displaying the primary views of the program,
 * including the {@link UserLibraryView} and {@link PlatformView}. Also
 * coordinates
 * the search bar and tag-based filtering interface used throughout the app.
 * <p>
 * This class serves as the main controller for navigating between views and
 * integrating
 * key UI components, making it the core entry point for the application's user
 * interface.
 *
 * @see UserLibraryView
 * @see PlatformView
 */
public class MainWindow extends javax.swing.JFrame {
    private JFrame frame;
    private JPanel contentPanel;
    private JPanel tagListPanel;
    private final Map<String, IPageView> views = new LinkedHashMap<>();
    private String currentViewKey = "";
    private TagPanel selectedTagPanel;

    private final TagManager tagManager;

    /**
     * Constructs the main application window and initializes its UI.
     *
     * @param registeredViews the list of views to be registered and made navigable
     *                        within the app.
     * @param tagManager      the {@link TagManager} used for tag-based filtering
     *                        across views.
     */
    public MainWindow(List<IPageView> registeredViews, TagManager tagManager) {
        for (IPageView view : registeredViews) {
            registerView(view);
        }

        this.tagManager = tagManager;
        this.selectedTagPanel = null;

        setTitle(MainWindowTexter.windowName());
        setName("appFrame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DefaultsTexter.mainWindowWidth(), DefaultsTexter.mainWindowHeight());
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeUI();
        switchToView(currentViewKey); // Show initial view
    }

    /**
     * Registers a given {@link IPageView} to be navigable in the UI.
     * 
     * @param view the {@link IPageView} to register.
     */
    private void registerView(IPageView view) {
        if (views.isEmpty()) {
            currentViewKey = view.getKey(); // default to the first view registered
        }
        views.put(view.getKey(), view);
    }

    /**
     * Creates the user interface.
     */
    private void initializeUI() {
        contentPanel = createContentPanel();
        JPanel topPanel = createTopPanel();
        JPanel tagBarPanel = createTagBarPanel();
        JPanel northPanel = createNorthPanel(topPanel, tagBarPanel);

        add(northPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Switch UI to a given registered view.
     * 
     * @param viewKey the String key of the view to display.
     */
    private void switchToView(String viewKey) {
        IPageView view = views.get(viewKey);
        if (view != null) {
            view.refresh();
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, viewKey);
        }
    }

    /**
     * Adds the {@link JPanel} panels in {@code views} to the current window JPanel.
     * 
     * @return a {@link JPanel} with {@code views} panels added.
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new CardLayout());
        for (IPageView view : views.values()) {
            panel.add(view.getPanel(), view.getKey());
        }
        return panel;
    }

    /**
     * Adds a button to toggle between user and library views, and the search bar.
     * 
     * @return a panel that toggles between user and library views and has a search
     *         bar.
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setName("topPanel");

        // Create search bar and button to clear input
        JTextField searchField = new JTextField();
        JButton clearButton = new JButton("X");
        clearButton.setFocusable(false);
        clearButton.setMargin(new Insets(2, 5, 2, 5));

        // Create search button to perform search
        JButton searchButton = new JButton("Search");
        // Add functionality to search bar
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            IPageView currentView = views.get(currentViewKey);
            if (currentView instanceof ISearchable searchableView) {
                searchableView.search(query);
            } else {
                JOptionPane.showMessageDialog(frame, "This view does not support searching.", "Search Unavailable",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Create button for switching between platform and user library
        JToggleButton toggleButton = new JToggleButton();
        toggleButton.setName("switchViewButton");
        updateToggleButtonLabel(toggleButton);
        toggleButton.setFocusable(false);
        toggleButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        // Add functionality
        toggleButton.addActionListener(e -> {
            currentViewKey = getToggledViewKey(currentViewKey);
            updateToggleButtonLabel(toggleButton);
            switchToView(currentViewKey);
            refreshTags();
            searchField.setText("");
            IPageView currentView = views.get(currentViewKey);
            if (currentView instanceof ISearchable searchableView) {
                searchableView.search("");
            }
            if (currentView instanceof ITagFilterable tagFilterableView) {
                tagFilterableView.filterByTag(null);
            }
        });
        // Add to panel
        topPanel.add(toggleButton, BorderLayout.WEST);

        // Add functionality to clear button
        clearButton.addActionListener(e -> {
            searchField.setText("");
            IPageView currentView = views.get(currentViewKey);
            if (currentView instanceof ISearchable searchableView) {
                searchableView.search("");
            }
        });

        // Add search bar and clear button to a search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(clearButton, BorderLayout.EAST);

        // Add search panel and search button to a new panel
        JPanel searchBarWithButton = new JPanel(new BorderLayout(5, 5));
        searchBarWithButton.add(searchPanel, BorderLayout.CENTER);
        searchBarWithButton.add(searchButton, BorderLayout.EAST);

        // add new panel to topPanel and return
        topPanel.add(searchBarWithButton, BorderLayout.CENTER);
        return topPanel;
    }

    /**
     * Creates a {@link JPanel} that displays user-created {@link Tag} instances.
     * 
     * @return a {@link JPanel} that displays user-created {@link Tag} instances.
     */
    private JPanel createTagBarPanel() {
        tagListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        tagListPanel.setName("tagListPanel");
        refreshTags();

        JPanel tagBarPanel = PanelUtils.createTitledScrollSection(
                "Tags",
                tagListPanel,
                createTagMenuButton(),
                BorderLayout.EAST);
        tagBarPanel.setName("tagBarPanel");
        tagBarPanel.setPreferredSize(new Dimension(0, 100));
        return tagBarPanel;
    }

    private void handleTagClick(Tag tag) {
        IPageView currentView = views.get(currentViewKey);
        if (currentView instanceof ITagFilterable filterableView) {
            filterableView.filterByTag(tag);
        } else {
            JOptionPane.showMessageDialog(this, "This view does not support tag filtering.", "Filter Unavailable",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Creates a {@link JPanel} at the top of the window, with {@code topPanel} on
     * the top half and
     * {@code tagBarPanel} on the bottom half.
     * 
     * @param topPanel    the {@link JPanel} for the top of the window.
     * @param tagBarPanel the panel to place just below {@code topPanel}.
     * @return a panel with {@code topPanel} above {@code tagBarPanel}.
     */
    private JPanel createNorthPanel(JPanel topPanel, JPanel tagBarPanel) {
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(topPanel);
        northPanel.add(tagBarPanel);
        return northPanel;
    }

    /**
     * Creates a {@link JPanel} burger menu for {@link Tag} instances.
     * 
     * @return a {@link JPanel} with a menu for tags.
     */
    private JButton createTagMenuButton() {
        // Create tag menu button
        JButton menuButton = new JButton("Manage");
        menuButton.setName("tagMenuButton");
        menuButton.setFocusPainted(false);
        menuButton.setPreferredSize(new Dimension(100, 40));
        menuButton.setToolTipText("Manage your tags");
        menuButton.setFont(new Font("Arial", Font.PLAIN, 14)); // Slightly smaller to fit better
        menuButton.setForeground(Color.BLACK);
        menuButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuButton.setOpaque(true);

        // Add functionality
        menuButton.addActionListener(e -> {
            new ManagePlatformTagsDialog(frame, tagManager).setVisible(true);
            refreshTags(); // Refresh tag bar after closing the manager
        });

        return menuButton;
    }

    /**
     * Refreshes the tags in {@code tagListPanel}.
     */
    private void refreshTags() {
        tagListPanel.removeAll();
        List<Tag> tags = tagManager.getAllTags();

        for (Tag tag : tags) {
            TagPanel tagPanel = new TagPanel(tag, null);
            tagPanel.setOnClick(t -> handleTagSelection(tagPanel, tag));
            tagListPanel.add(tagPanel);
        }

        tagListPanel.revalidate();
        tagListPanel.repaint();
    }

    private void handleTagSelection(TagPanel clickedPanel, Tag tag) {
        if (selectedTagPanel != null && selectedTagPanel != clickedPanel) {
            selectedTagPanel.setSelected(false);
        }
        if (selectedTagPanel == clickedPanel) {
            // Unselect if already selected
            clickedPanel.setSelected(false);
            selectedTagPanel = null;
            handleTagClick(null); // Remove filter
        } else {
            clickedPanel.setSelected(true);
            selectedTagPanel = clickedPanel;
            handleTagClick(tag); // Apply filter
        }
    }

    private void updateToggleButtonLabel(JToggleButton toggleButton) {
        String nextKey = getToggledViewKey(currentViewKey);
        String nextLabel = views.get(nextKey).getDisplayName();
        toggleButton.setText("Switch to " + nextLabel);
    }

    // Utility methods assuming only two views for now
    private String getToggledViewKey(String current) {
        for (String key : views.keySet()) {
            if (!key.equals(current))
                return key;
        }
        return current;
    }
}
