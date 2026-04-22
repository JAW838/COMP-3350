package beatbinder.presentation.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import beatbinder.objects.Tag;
import beatbinder.presentation.text.content.DefaultsTexter;

/**
 * UI panel component that displays a {@link Tag} instance.
 * 
 * @see Tag
 */
public class TagPanel extends JPanel {
    /**
     * The {@link Tag} being displayed.
     */
    private final Tag tag;
    private Consumer<Tag> onClick;

    // Color constants
    private static final Color BG_NORMAL = UIManager.getColor(DefaultsTexter.panelBackgroundKey());
    private static final Color BG_HOVER = new Color(210, 210, 210);
    private boolean selected;
    private static final Color BG_SELECTED = new Color(160, 160, 160); // Darker color for selection
    private static final Color BORDER_NORMAL = new Color(180, 180, 180);
    private static final Color BORDER_HOVER = new Color(120, 120, 120);

    public TagPanel(Tag tag, Consumer<Tag> onClick) {
        this.tag = tag;
        this.onClick = onClick;
        this.selected = false;

        setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        setOpaque(true);
        setBackground(BG_NORMAL);
        setBorder(BorderFactory.createLineBorder(BORDER_NORMAL));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setFont(getFont().deriveFont(Font.PLAIN, 12f));
        setAlignmentY(Component.CENTER_ALIGNMENT);

        setRoundedCorners();

        initializeUI();
        initializeClickHandler();
        initializeHoverEffect();
    }

    /**
     * Initialises UI
     */
    private void initializeUI() {
        JLabel nameLabel = new JLabel(tag.getName());
        nameLabel.setFont(getFont());
        nameLabel.setFocusable(false);
        add(nameLabel);
    }

    /**
     * Adds an effect to the panel when the mouse hovers over it.
     */
    private void initializeHoverEffect() {
        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(BG_HOVER);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_HOVER),
                        BorderFactory.createEmptyBorder(2, 8, 2, 8)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (selected) {
                    setBackground(BG_SELECTED);
                } else {
                    setBackground(BG_NORMAL);
                }
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_NORMAL),
                        BorderFactory.createEmptyBorder(2, 8, 2, 8)));
            }
        };

        addMouseListener(hoverAdapter);
        for (Component comp : getComponents()) {
            comp.addMouseListener(hoverAdapter);
        }
    }

    /**
     * Has the panel pretend it's an Apple icon by rounding its corners.
     * Unfortunately it doesn't understand how to be a squircle.
     */
    private void setRoundedCorners() {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_NORMAL),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        setBackground(BG_NORMAL);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Rounded background
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Rounded border
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getForeground());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();
    }

    private void initializeClickHandler() {
        MouseAdapter clickAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (onClick != null) {
                    onClick.accept(tag);
                }
            }
        };
        addMouseListener(clickAdapter);
        for (Component comp : getComponents()) {
            comp.addMouseListener(clickAdapter);
        }
    }

    public void setOnClick(Consumer<Tag> onClick) {
        this.onClick = onClick;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateSelectionStyle();
    }

    private void updateSelectionStyle() {
        if (selected) {
            setBackground(BG_SELECTED);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_HOVER),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        } else {
            setBackground(BG_NORMAL);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_NORMAL),
                    BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        }
        repaint();
    }
}
