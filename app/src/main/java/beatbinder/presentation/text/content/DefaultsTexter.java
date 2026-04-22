package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class DefaultsTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String defaultFont() {
        return Texter.getTextFromTag(doc, "defaultFont");
    }

    public static String panelBackgroundKey() {
        return Texter.getTextFromTag(doc, "panelBackgroundKey");
    }

    public static int mainWindowWidth() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "mainWindowWidth"));
    }

    public static int mainWindowHeight() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "mainWindowHeight"));
    }

    public static int defaultDialogWidth() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "defaultDialogWidth"));
    }

    public static int defaultDialogHeight() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "defaultDialogHeight"));
    }

    public static int smallDialogWidth() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "smallDialogWidth"));
    }

    public static int smallDialogHeight() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "smallDialogHeight"));
    }
}
