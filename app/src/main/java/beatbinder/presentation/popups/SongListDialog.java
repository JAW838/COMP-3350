package beatbinder.presentation.popups;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.presentation.components.PanelUtils;
import beatbinder.presentation.components.SortOption;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.SongListDialogTexter;

/**
 * Abstract base class for displaying a list of {@link Song} instances in a {@link JPanel}.
 */
public abstract class SongListDialog extends JDialog {
    protected final SongManager songManager;
    protected final TagManager tagManager;
    protected SortOption sortOption;

    protected final JPanel songListPanel;

    /**
     * Creates a scrollable empty {@link JPanel}.
     * 
     * @param songManager the {@link SongManager} used to get {@link Song} information.
     * @param tagManager the {@link TagManager} used to get {@link Tag} information.
     */
    public SongListDialog(SongManager songManager, TagManager tagManager) {
        this.songManager = songManager;
        this.tagManager = tagManager;
        this.sortOption = SortOption.DEFAULT;

        songListPanel = new JPanel();
        songListPanel.setLayout(new BoxLayout(songListPanel, BoxLayout.Y_AXIS));

        setModal(true);
        setSize(DefaultsTexter.defaultDialogWidth(), DefaultsTexter.defaultDialogHeight());
        setResizable(false);
        setLocationRelativeTo(null);
    }

    protected void initializeUI() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        container.add(createTopPanel(), BorderLayout.NORTH);
        container.add(createScrollSection(), BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        if (bottomPanel != null) {
            container.add(bottomPanel, BorderLayout.SOUTH);
        }

        setContentPane(container);
    }

    /**
     * Creates a scrollable section.
     * @return the new scrollable section.
     */
    private JPanel createScrollSection() {
        refreshSongListContents();
        JButton sortButton = getSortButton();
        return PanelUtils.createTitledScrollSection(
                SongListDialogTexter.panelName(),
                songListPanel,
                sortButton,
                BorderLayout.NORTH,
                true);
    }

    /**
     * Refreshes the {@link Song} instances displayed in the dialog.
     */
    protected void refreshSongListPanel() {
        refreshSongListContents();
        revalidate();
        repaint();
    }

    protected JButton getSortButton() {
        return null; // default: no sort button
    }

    /**
     * Create a separate section at the top of the panel.
     * @return
     */
    protected abstract JPanel createTopPanel();

    /**
     * Refresh the contents of the dialog.
     */
    protected abstract void refreshSongListContents();

    /**
     * Create a separate section at the bottom of the panel.
     * @return
     */
    protected abstract JPanel createBottomPanel();
}