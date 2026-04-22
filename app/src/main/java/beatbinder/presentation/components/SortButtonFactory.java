package beatbinder.presentation.components;

import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import beatbinder.presentation.text.content.SortButtonFactoryTexter;

public class SortButtonFactory {
    public static JButton createSortMenuButton(Consumer<SortOption> onSortOptionSelected) {
        JButton sortButton = new JButton(SortButtonFactoryTexter.sortButton());
        sortButton.setFocusable(false);

        JPopupMenu popupMenu = new JPopupMenu();

        for (SortOption option : SortOption.values()) {
            JMenuItem item = new JMenuItem(option.getDisplayName());
            item.addActionListener(e -> {
                onSortOptionSelected.accept(option);
            });
            popupMenu.add(item);
        }

        // Show popup on button click
        sortButton.addActionListener(e -> {
            popupMenu.show(sortButton, 0, sortButton.getHeight());
        });

        return sortButton;
    }
}