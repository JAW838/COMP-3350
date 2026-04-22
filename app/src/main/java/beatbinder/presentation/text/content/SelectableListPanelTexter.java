package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class SelectableListPanelTexter {
    private static Document doc = null;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String warningMessage() {
        return Texter.getTextFromTag(doc, "warningMessage");
    }

    public static String warningTitle() {
        return Texter.getTextFromTag(doc, "warningTitle");
    }
}
