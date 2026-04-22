package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class UserLibraryTexter {
    private static Document doc;
    public static void init(String fileName) {
        doc = Texter.init(fileName);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String songPanel() {
        return Texter.getTextFromTag(doc, "songPanel");
    }

    public static String playlistPanel() {
        return Texter.getTextFromTag(doc, "playlistPanel");
    }

    public static String createPlaylist() {
        return Texter.getTextFromTag(doc, "createPlaylist");
    }

    public static String font() {
        return Texter.getTextFromTag(doc, "font");
    }

    public static String albumPanel() {
        return Texter.getTextFromTag(doc, "albumPanel");
    }
}
