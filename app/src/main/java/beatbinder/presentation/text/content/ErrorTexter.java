package beatbinder.presentation.text.content;

import org.w3c.dom.*;

public class ErrorTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String collectionMissing() {
        return Texter.getTextFromTag(doc, "collectionMissing");
    }

    public static String collectionDuplicate() {
        return Texter.getTextFromTag(doc, "collectionDuplicate");
    }

    public static String songMissing() {
        return Texter.getTextFromTag(doc, "songMissing");
    }

    public static String emptyPlaylistName() {
        return Texter.getTextFromTag(doc, "emptyPlaylistName");
    }

    public static String songDuplicate() {
        return Texter.getTextFromTag(doc, "songDuplicate");
    }

    public static String platformTagDuplicate() {
        return Texter.getTextFromTag(doc, "platformTagDuplicate");
    }

    public static String songTagDuplicate() {
        return Texter.getTextFromTag(doc, "songTagDuplicate");
    }

    public static String emptyTagName() {
        return Texter.getTextFromTag(doc, "emptyTagName");
    }
}
