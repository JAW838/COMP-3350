package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class CollectionViewTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String panelFont() {
        return Texter.getTextFromTag(doc, "panelFont");
    }

    public static String songsName() {
        return Texter.getTextFromTag(doc, "songsName");
    }

    public static String loadError() {
        return Texter.getTextFromTag(doc, "loadError");
    }

}
