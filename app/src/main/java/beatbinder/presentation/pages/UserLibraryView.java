package beatbinder.presentation.pages;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

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
import beatbinder.presentation.popups.CreatePlaylistDialog;
import beatbinder.presentation.text.content.DefaultsTexter;
import beatbinder.presentation.text.content.UserLibraryTexter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

/**
 * Displays a searchable UI window for the user's library, including liked
 * songs, created playlists, and liked albums.
 * <p>
 * Implements {@link IPageView} for page navigation and {@link ISearchable} for
 * keyword-based filtering.
 * <p>
 * This class initializes and manages separate panels for each category of
 * content.
 *
 * @see IPageView
 * @see ISearchable
 */
public class UserLibraryView implements IPageView, ISearchable, ITagFilterable {
        /**
         * {@link JPanel} displaying all songs and collections liked by the user by
         * displaying
         * {@code likedSongsPanel}, {@code likedAlbumsPanel}, and
         * {@code savedPlaylistsPanel}.
         */
        private JPanel libraryPanel;
        /**
         * A {@link JPanel} containing all {@link Song} instances marked as liked by the
         * user.
         */
        private JPanel likedSongsPanel;
        /**
         * A {@link JPanel} containing albums liked by the user.
         */
        private JPanel likedAlbumsPanel;
        /**
         * A {@link JPanel} containing playlists saved by the user.
         */
        private JPanel savedPlaylistsPanel;
        private SortOption sortOption;
        private String currentSearch;
        private Tag currentTag;

        private final SongManager songManager;
        private final CollectionManager collectionManager;
        private final TagManager tagManager;

        /**
         * Creates a new {@link UserLibraryView} to present the user's library. A user's
         * libarary
         * contains liked songs, liked albums and saved playlists.
         * 
         * @param songManager       a {@link SongManager} used to retrieve {@link Song}
         *                          information.
         * @param collectionManager a {@link CollectionManager} used to retrieve
         *                          {@link SongCollection}
         *                          information.
         * @param tagManager        a {@link TagManager} used to retrieve {@link Tag}
         *                          information.
         */
        public UserLibraryView(SongManager songManager, CollectionManager collectionManager, TagManager tagManager) {
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
                return "userLibraryView";
        }

        @Override
        public String getDisplayName() {
                return UserLibraryTexter.windowName();
        }

        @Override
        public JPanel getPanel() {
                return libraryPanel;
        }

        @Override
        public void refresh() {
                refreshPlaylists();
                refreshLikedAlbums();
                refreshLikedSongs();
        }

        @Override
        public void search(String search) {
                if (search.equals("")) {
                        currentSearch = null;
                        refresh();
                } else {
                        currentSearch = search;

                        List<SongCollection> playlistResults = collectionManager.searchCollectionByTitle(search,
                                        CollType.PLAYLIST);
                        updatePlaylists(playlistResults);

                        List<SongCollection> albumResults = collectionManager.searchCollectionByTitle(search,
                                        CollType.ALBUM);
                        updateLikedAlbums(albumResults);

                        List<Song> songResults = songManager.searchSongByTitle(songManager.getLikedSongs(), search);
                        updateLikedSongsPanel(songResults);
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
                refreshLikedSongs();
        }

        /**
         * Refreshes {@code savedPlaylistsPanel} with the all of the user's current
         * saved playlists.
         */
        private void refreshPlaylists() {
                if (currentSearch != null) {
                        search(currentSearch);
                } else {
                        updatePlaylists(collectionManager.getAllPlaylists());
                }
        }

        /**
         * Refreshes {@code savedPlaylistsPanel} with specific playlists.
         */
        private void updatePlaylists(List<SongCollection> playlists) {
                PanelUtils.populatePanel(savedPlaylistsPanel, playlists,
                                playlist -> new CollectionPanel(playlist, collectionManager, songManager, tagManager,
                                                this::refreshPlaylists));
        }

        /**
         * Refreshes {@code likedAlbumsPanel} with the all of the user's current liked
         * albums.
         */
        private void refreshLikedAlbums() {
                if (currentSearch != null) {
                        search(currentSearch);
                } else {
                        updateLikedAlbums(collectionManager.getLikedAlbums());
                }
        }

        /**
         * Refreshes {@code likedAlbumsPanel} with specific albums.
         */
        private void updateLikedAlbums(List<SongCollection> albums) {
                PanelUtils.populatePanel(likedAlbumsPanel, albums,
                                collection -> new CollectionPanel(collection, collectionManager, songManager,
                                                tagManager,
                                                this::refreshLikedAlbums));
        }

        /**
         * Refreshes {@code likedSongsPanel} with all of the user's current liked songs.
         */
        private void refreshLikedSongs() {
                if (currentSearch != null) {
                        search(currentSearch);
                } else {
                        updateLikedSongsPanel(songManager.getLikedSongs());
                }
        }

        /**
         * Refreshes {@code likedSongsPanel} with a specific list of songs
         */
        private void updateLikedSongsPanel(List<Song> songs) {
                if (currentTag != null) {
                        songs = tagManager.getSongsByTag(songs, currentTag);
                }
                songs = songManager.sortSongs(songs, sortOption);
                PanelUtils.populatePanel(likedSongsPanel, songs,
                                song -> new SongPanel(song, songManager, collectionManager, tagManager,
                                                this::refreshLikedSongs));
        }

        private void initializeUI() {
                libraryPanel = new JPanel(new BorderLayout());
                libraryPanel.setName("userLibraryPanel");

                // Liked songs
                likedSongsPanel = new JPanel();
                JButton sortButton = SortButtonFactory.createSortMenuButton(selectedOption -> {
                        this.sortOption = selectedOption;
                        if (currentSearch != null) {
                                search(currentSearch);
                        } else {
                                refreshLikedSongs();
                        }
                });
                likedSongsPanel.setName("likedSongsPanel");

                JPanel likedSongsSection = PanelUtils.createTitledScrollSection(
                                UserLibraryTexter.songPanel(), likedSongsPanel, sortButton, BorderLayout.NORTH, true);
                libraryPanel.add(likedSongsSection, BorderLayout.CENTER);

                // Playlists
                savedPlaylistsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
                savedPlaylistsPanel.setName("savedPlaylistsPanel");
                JPanel savedPlaylistsWrapper = PanelUtils.createTitledScrollSection(
                                UserLibraryTexter.playlistPanel(), savedPlaylistsPanel,
                                createCreatePlaylistButton(),
                                BorderLayout.NORTH);

                // Liked albums
                likedAlbumsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
                likedAlbumsPanel.setName("likedAlbumsPanel");
                JPanel likedAlbumsWrapper = PanelUtils.createTitledScrollSection(
                                UserLibraryTexter.albumPanel(), likedAlbumsPanel);

                // Combine the two into a split pane
                JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, savedPlaylistsWrapper,
                                likedAlbumsWrapper);
                leftSplitPane.setResizeWeight(0.5);
                leftSplitPane.setDividerSize(5);
                leftSplitPane.setPreferredSize(new Dimension(350, 0));

                libraryPanel.add(leftSplitPane, BorderLayout.WEST);
        }

        /**
         * Creates a {@link JButton} which opens a {@link CreatePlaylistDialog} to
         * create a new
         * playlist.
         * 
         * @return a {@link JButton} which opens a playlist creation menu.
         * 
         * @see CreatePlaylistDialog
         */
        private JButton createCreatePlaylistButton() {
                JButton button = new JButton(UserLibraryTexter.createPlaylist());
                button.setName("createPlaylistButton");
                button.setFocusPainted(false);
                button.setForeground(Color.BLACK);
                button.setFont(new Font(DefaultsTexter.defaultFont(), Font.BOLD, 14));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setOpaque(true);
                button.setPreferredSize(new Dimension(150, 40));

                button.addActionListener(e -> {
                        CreatePlaylistDialog dialog = new CreatePlaylistDialog(
                                        (JFrame) SwingUtilities.getWindowAncestor(libraryPanel),
                                        collectionManager);
                        dialog.setName("createPlaylistDialog");
                        dialog.setVisible(true);
                        String name = dialog.getPlaylistName();
                        if (name != null) {
                                JOptionPane.showMessageDialog(libraryPanel, "Created playlist: " + name);
                                refreshPlaylists();
                        }
                });

                return button;
        }
}
