package beatbinder.presentation.text.content;

import org.w3c.dom.*;

public class ManageTagsDialogTexter {
    private static Document doc = null;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "window-name");
    }

    public static String removeButton() {
        return Texter.getTextFromTag(doc, "removeButton");
    }

    public static String createButton() {
        return Texter.getTextFromTag(doc, "createButton");
    }

    public static String removeConfirmMessageStart() {
        return Texter.getTextFromTag(doc, "removeConfirmMessageStart");
    }

    public static String removeConfirmMessageEnd() {
        return Texter.getTextFromTag(doc, "removeConfirmMessageEnd");
    }

    public static String removeConfirmName() {
        return Texter.getTextFromTag(doc, "removeConfirmName");
    }

    public static String createTagMessage() {
        return Texter.getTextFromTag(doc, "createTagMessage");
    }
}
