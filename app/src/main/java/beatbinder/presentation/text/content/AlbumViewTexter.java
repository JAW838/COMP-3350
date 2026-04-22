package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class AlbumViewTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String panelName() {
        return Texter.getTextFromTag(doc, "panelName");
    }

    public static String likeButton() {
        return Texter.getTextFromTag(doc, "likeButton");
    }

    public static String font() {
        return Texter.getTextFromTag(doc, "font");
    }
}
