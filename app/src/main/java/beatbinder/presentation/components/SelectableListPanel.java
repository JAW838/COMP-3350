package beatbinder.presentation.components;

import java.awt.Component;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import beatbinder.presentation.text.content.SelectableListPanelTexter;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JScrollPane;

/**
 * Defines a generic UI component that displays a scrollable, selectable list of items and a
 * confirm button.
 */
public class SelectableListPanel<T> extends JPanel {
    /**
     * The items formatted to be displayed.
     */
    private JList<T> itemList;
    /**
     * The item currently selected.
     */
    private T selectedItem;
    /**
     * A function that converts each item into a String for display.
     */
    private Function<T, String> itemToString;
    /**
     * The items being displayed.
     */
    private DefaultListModel<T> listModel;

    /**
     * Creates a scrollable, selectable UI component displaying generic information.
     * 
     * @param items the list of items to display.
     * @param buttonLabel the label on the confirm button.
     * @param itemToString a function that converts each item {@code T} to a readable {@code String}
     * for display.
     * @param onConfirm what to do if a user selects an item and clicks the button.
     */
    public SelectableListPanel(
            List<T> items,
            String buttonLabel,
            Function<T, String> itemToString,
            Consumer<T> onConfirm) {

        this.itemToString = itemToString;

        listModel = new DefaultListModel<>();
        for (T item : items) {
            listModel.addElement(item);
        }

        setLayout(new BorderLayout(10, 10));

        itemList = new JList<>(listModel);
        itemList.setName("itemList");
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        itemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                label.setText(itemToString.apply((T) value));
                return label;
            }
        });

        add(new JScrollPane(itemList), BorderLayout.CENTER);

        // Create and set up confirm button
        JButton confirmButton = new JButton(buttonLabel);
        confirmButton.setName("confirmButton");
        confirmButton.addActionListener(e -> {
            selectedItem = itemList.getSelectedValue();
            if (selectedItem != null) {
                onConfirm.accept(selectedItem);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        SelectableListPanelTexter.warningMessage(),
                        SelectableListPanelTexter.warningTitle(),
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        add(confirmButton, BorderLayout.SOUTH);
    }

    /**
     * Retrieves the currently selected item.
     * @return
     */
    public T getSelectedItem() {
        return selectedItem;
    }

    /**
     * Updates the items on the page with a given list of {@code newItems}.
     * @param newItems
     */
    public void updateItems(List<T> newItems) {
        listModel.clear();
        for (T item : newItems) {
            listModel.addElement(item);
        }
    }
}
