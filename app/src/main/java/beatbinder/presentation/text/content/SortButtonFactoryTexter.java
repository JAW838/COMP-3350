package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class SortButtonFactoryTexter {
    private static Document doc;
    public static void init(String fileName) {
        doc = Texter.init(fileName);
    }

    public static String sortButton() {
        return Texter.getTextFromTag(doc, "sortButton");
    }
}
