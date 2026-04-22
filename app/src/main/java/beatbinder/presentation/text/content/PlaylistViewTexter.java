package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class PlaylistViewTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String panelTitle() {
        return Texter.getTextFromTag(doc, "panelTitle");
    }

    public static String editButton() {
        return Texter.getTextFromTag(doc, "editButton");
    }

    public static String renameButton() {
        return Texter.getTextFromTag(doc, "renameButton");
    }

    public static String deleteButton() {
        return Texter.getTextFromTag(doc, "deleteButton");
    }

    public static String renameDialog() {
        return Texter.getTextFromTag(doc, "renameDialog");
    }

    public static String deletePlaylistMessage() {
        return Texter.getTextFromTag(doc, "deletePlaylistMessage");
    }

    public static String deleteConfirm() {
        return Texter.getTextFromTag(doc, "deleteConfirm");
    }
}
