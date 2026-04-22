package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class SongPanelTexter {
    private static Document doc;
    public static void init(String fileName) {
        doc = Texter.init(fileName);
    }

    public static String like() {
        return Texter.getTextFromTag(doc, "like");
    }

    public static String unlike() {
        return Texter.getTextFromTag(doc, "unlike");
    }

    public static String menuIcon() {
        return Texter.getTextFromTag(doc, "menuIcon");
    }

    public static String add() {
        return Texter.getTextFromTag(doc, "addSong");
    }

    public static String details() {
        return Texter.getTextFromTag(doc, "songDetails");
    }

    public static String viewArtist() {
        return Texter.getTextFromTag(doc, "viewArtist");
    }

    public static String upButton() {
        return Texter.getTextFromTag(doc, "upButton");
    }

    public static String downButton() {
        return Texter.getTextFromTag(doc, "downButton");
    }

    public static int defaultSongHeight() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "defaultSongHeight"));
    }

    public static int emptyBorderSize() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "emptyBorderSize"));
    }

    public static int matteBorderSize() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "matteBorderSize"));
    }

    public static int panelInfoSpacing() {
        return Integer.parseInt(Texter.getTextFromTag(doc, "panelInfoSpacing"));
    }

    public static float heartSize() {
        return Float.parseFloat(Texter.getTextFromTag(doc, "heartSize"));
    }
}
