package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class AddTagDialogTexter {
    private static Document doc = null;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String addButton() {
        return Texter.getTextFromTag(doc, "addButton");
    }

    public static String addMessageStart() {
        return Texter.getTextFromTag(doc, "addMessageStart");
    }

    public static String addMessageEnd() {
        return Texter.getTextFromTag(doc, "addMessageEnd");
    }
}
