package beatbinder.presentation.text.content;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import beatbinder.presentation.text.exceptions.DuplicateTextException;
import beatbinder.presentation.text.exceptions.TextNotFoundException;

class Texter {
//    private static String filepath = "beatbinder/presentation/text/text/";
    public static Document init(String fileName) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            // Load the XML file from resources
            var inputStream = Texter.class.getClassLoader().getResourceAsStream("text/" + fileName);
//            var inputStream = Texter.class.getClassLoader().getResourceAsStream(filepath + fileName);
            if (inputStream == null) {
                throw new RuntimeException("Could not find resource: text/" + fileName);
//                throw new RuntimeException("Could not find resource: "+filepath + fileName);
            }
            
            Document doc = dBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTextFromTag(Document doc, String name) {
        NodeList list = doc.getElementsByTagName(name);

        if (list.getLength() > 1) {
            throw new DuplicateTextException("More than one entry found.");
        }
        if (list.getLength() < 1) {
            throw new TextNotFoundException("No entry found.");
        }

        return list.item(0).getTextContent();
    }
}