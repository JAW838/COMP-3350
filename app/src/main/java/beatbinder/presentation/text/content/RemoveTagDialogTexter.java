package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class RemoveTagDialogTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String removeButton() {
        return Texter.getTextFromTag(doc, "removeButton");
    }
}
