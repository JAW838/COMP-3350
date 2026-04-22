package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class PlatformViewTexter{
    private static Document doc = null;
    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String songSection() {
        return Texter.getTextFromTag(doc, "song-section");
    }

    public static String albumSection() {
        return Texter.getTextFromTag(doc, "album-section");
    }
}
