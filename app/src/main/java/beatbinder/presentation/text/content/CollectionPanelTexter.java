package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class CollectionPanelTexter {
    private static Document doc;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static int defaultCollectionSize() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "defaultCollectionSize"));
    }

    public static int emptyBorderSize() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "emptyBorderSize"));
    }

    public static int lineBorderSize() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "lineBorderSize"));
    }
}
