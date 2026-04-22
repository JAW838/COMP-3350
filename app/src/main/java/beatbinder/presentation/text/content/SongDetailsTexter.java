package beatbinder.presentation.text.content;

import org.w3c.dom.Document;

public class SongDetailsTexter {
    private static Document doc = null;

    public static void init(String name) {
        doc = Texter.init(name);
    }

    public static String windowName() {
        return Texter.getTextFromTag(doc, "windowName");
    }

    public static String titleInfo() {
        return Texter.getTextFromTag(doc, "title-info");
    }

    public static String artistInfo() {
        return Texter.getTextFromTag(doc, "artist-info");
    }

    public static String genreInfo() {
        return Texter.getTextFromTag(doc, "genre-info");
    }

    public static String runtimeInfo() {
        return Texter.getTextFromTag(doc, "runtime-info");
    }

    public static String notePanelName() {
        return Texter.getTextFromTag(doc, "notePanelName");
    }

    public static String tagPanelName() {
        return Texter.getTextFromTag(doc, "tagPanelName");
    }

    public static String closeButton() {
        return Texter.getTextFromTag(doc, "closeButton");
    }

    public static String saveButton() {
        return Texter.getTextFromTag(doc, "saveButton");
    }

    public static String editButton() {
        return Texter.getTextFromTag(doc, "editButton");
    }

    public static String htmlFormatOpen() {
        return Texter.getTextFromTag(doc, "htmlFormatOpen");
    }

    public static String htmlFormatClose() {
        return Texter.getTextFromTag(doc, "htmlFormatClose");
    }

    public static String noNote() {
        return Texter.getTextFromTag(doc, "noNote");
    }

    public static String tagButton() {
        return Texter.getTextFromTag(doc, "tagButton");
    }

    public static String addTag() {
        return Texter.getTextFromTag(doc, "addTag");
    }

    public static String removeTag() {
        return Texter.getTextFromTag(doc, "removeTag");
    }
}
