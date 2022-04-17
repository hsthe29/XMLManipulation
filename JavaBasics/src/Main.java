/**
 * @Author: Ho Sy The
 * @StudentID: 20200614
 * @Specific: IT1 04 - K65
 *
 * */

import org.w3c.dom.Document;
import java.io.ByteArrayOutputStream;

public class Main {
    public static void main(String[] args) {
        XMLReader xr = new XMLReader();
        try {
            Document doc = xr.loadDocument();
            xr.parseXML(doc);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xr.createXML(out);
            String xml = out.toString();
            xr.saveXML(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
