package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class SongListDialogTexter {
    private static Document doc = null;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String panelName() {
        return Texter.getTextFromTag(doc, "panelName");
    }
}
