package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class AddToPlaylistTexter {
    private static Document doc = null;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String confirmButton() {
        return Texter.getTextFromTag(doc, "confirmButton");
    }

    public static String cancelButton() {
        return Texter.getTextFromTag(doc, "cancelButton");
    }
}
