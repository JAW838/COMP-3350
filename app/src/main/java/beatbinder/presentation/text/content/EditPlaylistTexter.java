package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class EditPlaylistTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String removeButtonName() {
        return Texter.getTextFromTag(doc, "removeButtonName");
    }

    public static String confirmMessageStart() {
        return Texter.getTextFromTag(doc, "confirmMessageStart");
    }

    public static String confirmMessageEnd() {
        return Texter.getTextFromTag(doc, "confirmMessageEnd");
    }

    public static String confirmButtonName() {
        return Texter.getTextFromTag(doc, "confirmButtonName");
    }
}
