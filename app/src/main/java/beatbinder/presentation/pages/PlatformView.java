package beatbinder.presentation.pages;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.*;

import beatbinder.logic.CollectionManager;
import beatbinder.logic.SongManager;
import beatbinder.logic.TagManager;
import beatbinder.objects.CollType;
import beatbinder.objects.Song;
import beatbinder.objects.SongCollection;
import beatbinder.objects.Tag;
import beatbinder.presentation.components.CollectionPanel;
import beatbinder.presentation.components.PanelUtils;
import beatbinder.presentation.components.SongPanel;
import beatbinder.presentation.components.SortButtonFactory;
import beatbinder.presentation.components.SortOption;
import beatbinder.presentation.components.WrapLayout;
import beatbinder.presentation.interfaces.IPageView;
import beatbinder.presentation.interfaces.ISearchable;
import beatbinder.presentation.interfaces.ITagFilterable;
import beatbinder.presentation.text.content.PlatformViewTexter;

/**
 * Displays a searchable UI window for the platform library, including all songs
 * and albums.
 * Implements {@link IPageView} for page navigation and {@link ISearchable} for
 * keyword-based filtering.
 * <p>
 * This class initializes and manages separate panels for each category of
 * content.
 *
 * @see IPageView
 * @see ISearchable
 */
public class PlatformView implements IPageView, ISearchable, ITagFilterable {
        /**
         * A {@link JPanel} containing all content on the page, including
         * {@code songPanel}
         * and {@code collectionPanel}.
         */
        private JPanel mainPanel;
        /**
         * A {@link JPanel} containing all {@link Song} instances on the platform.
         */
        private JPanel songPanel;
        /**
         * A {@link JPanel} containing all {@link SongCollection} instances of type
         * {@code ALBUM} on the platform.
         */
        private JPanel collectionPanel;
        private SortOption sortOption;
        private String currentSearch;
        private Tag currentTag;

        private SongManager songManager;
        private CollectionManager collectionManager;
        private TagManager tagManager;

        /**
         * Creates a {@link PlatformView} to display {@link Song} and
         * {@link SongCollection} instances
         * present on the platform by default.
         * 
         * @param songManager       a {@link SongManager} used to retrieve {@link Song}
         *                          information.
         * @param collectionManager a {@link CollectionManager} used to retrieve
         *                          {@link SongCollection}
         *                          information.
         * @param tagManager        a {@link TagManager} used to retrieve {@link Tag}
         *                          information.
         */
        public PlatformView(SongManager songManager, CollectionManager collectionManager, TagManager tagManager) {
                this.songManager = songManager;
                this.collectionManager = collectionManager;
                this.tagManager = tagManager;
                this.sortOption = SortOption.DEFAULT;

                this.currentSearch = null;
                this.currentTag = null;

                initializeUI();

                SwingUtilities.invokeLater(this::refresh);
        }

        @Override
        public String getKey() {
                return "platformView";
        }

        @Override
        public String getDisplayName() {
                return PlatformViewTexter.windowName();
        }

        @Override
        public JPanel getPanel() {
                return mainPanel;
        }

        @Override
        public void search(String search) {
                if (search.equals("")) {
                        currentSearch = null;
                        refresh();
                } else {
                        currentSearch = search;
                        // Update the list of songs
                        List<Song> songResults = songManager.searchSongByTitle(songManager.getAllSongs(), search);
                        updateSongPanel(songResults);

                        List<SongCollection> albumResults = collectionManager.searchCollectionByTitle(search,
                                        CollType.ALBUM);
                        updateAlbums(albumResults);
                }
        }

        @Override
        public void filterByTag(Tag tag) {
                if (tag == null || tag.equals(currentTag)) {
                        // reset the tag
                        currentTag = null;
                } else {
                        currentTag = tag;
                }
                refreshSongs();
        }

        private void initializeUI() {
                mainPanel = new JPanel(new BorderLayout());
                mainPanel.setName("platformPanel");

                // Songs
                songPanel = new JPanel();
                JButton sortButton = SortButtonFactory.createSortMenuButton(selectedOption -> {
                        this.sortOption = selectedOption;
                        if (currentSearch != null) {
                                search(currentSearch);
                        } else {
                                refreshSongs();
                        }
                });
                sortButton.setName("sortButton");
                songPanel.setName("songPanel");
                JPanel songsSection = PanelUtils.createTitledScrollSection(
                                PlatformViewTexter.songSection(), songPanel, sortButton, BorderLayout.NORTH, true);

                // Albums
                collectionPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10)); // Unified layout
                collectionPanel.setName("collectionPanel");
                JPanel albumsSection = PanelUtils.createTitledScrollSection(
                                PlatformViewTexter.albumSection(), collectionPanel);

                JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, songsSection, albumsSection);
                splitPane.setResizeWeight(0.5);

                mainPanel.add(splitPane, BorderLayout.CENTER);
        }

        @Override
        public void refresh() {
                refreshSongs();
                refreshCollections();
        }

        /**
         * Update {@link Song} instances displayed in {@code songPanel}.
         */
        private void refreshSongs() {
                if (currentSearch != null) {
                        search(currentSearch);
                } else {
                        updateSongPanel(songManager.getAllSongs());
                }
        }

        private void updateSongPanel(List<Song> songs) {
                if (currentTag != null) {
                        // Filter the current list of songs by the selected tag
                        songs = tagManager.getSongsByTag(songs, currentTag);
                }
                songs = songManager.sortSongs(songs, sortOption);
                PanelUtils.populatePanel(songPanel, songs, song -> new SongPanel(song, songManager, collectionManager,
                                tagManager, this::refreshSongs));
        }

        private void refreshCollections() {
                if (currentSearch != null) {
                        search(currentSearch);
                } else {
                        updateAlbums(collectionManager.getAllAlbums());
                }
        }

        /**
         * Update {@link SongCollection} instances displayed in {@code collectionPanel}.
         */
        private void updateAlbums(List<SongCollection> albums) {
                PanelUtils.populatePanel(collectionPanel, albums,
                                collection -> new CollectionPanel(collection, collectionManager, songManager,
                                                tagManager, this::refreshCollections));
        }
}
