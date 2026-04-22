package beatbinder.presentation.text.content;

import org.w3c.dom.*;

public class MainWindowTexter {
    private static Document doc = null;
    
    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "window-name");
    }

    public static String SwitchToPersonalLibrary() {
        return Texter.getTextFromTag(doc, "user-toggle");
    }

    public static String toExplorePage() {
        return Texter.getTextFromTag(doc, "to-explore-page");
    }

    public static String toPersonalLibrary() {
        return Texter.getTextFromTag(doc, "to-personal-lib");
    }
}
