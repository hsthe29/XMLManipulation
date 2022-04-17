/**
 * @Author: Ho Sy The
 * @StudentID: 20200614
 * @Specific: IT1 04 - K65
 *
 * */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class XMLReader {

    private static final String filePath = "resource/vd012.net.xml";
    private static final String xmlFilePath = "resource/vd012.rou.xml";
    private final DocumentBuilderFactory dbf;
    private List<Element> listElements = null;
    private List<Integer> nums = null;

    public XMLReader() {
        this.dbf = DocumentBuilderFactory.newInstance();
    }

    public Document loadDocument() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(new File(filePath));
        doc.getDocumentElement().normalize();
        return doc;
    }

    public void parseXML(Document doc) {
        NodeList list = doc.getElementsByTagName("edge");

        Stream<Node> nodeStream = IntStream.range(0, list.getLength())
                .mapToObj(list::item);

        listElements = nodeStream.filter(c -> c instanceof Element)
                .map(c -> (Element)c)
                .filter(c -> c.getAttribute("id").startsWith("E"))
                .toList();

        nums = new ArrayList<>();
        for (Element temp : listElements)
            nums.add(Integer.parseInt(temp.getAttribute("id").substring(1)));

    }

    public void createXML(OutputStream out) throws XMLStreamException {

        XMLOutputFactory output = XMLOutputFactory.newInstance();

        XMLStreamWriter writer = output.createXMLStreamWriter(out);

        writer.writeStartDocument("utf-8", "1.0");
        writer.writeStartElement("routes");


        int n = listElements.size();
        int M = Collections.max(nums);
        for(int i = 0; i < n; i++) {
            double impatience = (nums.get(i) < (M / 2 + 1)) ? (nums.get(i) * 1.0 / (M / 2 + 1)) : (nums.get(i) * 1.0 / M);
            String id_element = listElements.get(i).getAttribute("id");

            writer.writeStartElement("personFlow");
                writer.writeAttribute("id", String.format("p%da", nums.get(i)));
                writer.writeAttribute("begin", "1");
                writer.writeAttribute("period", "5");
                writer.writeAttribute("impatience", String.format("%.2f", impatience));

                writer.writeStartElement("walk");
                    writer.writeAttribute("from", id_element);
                    writer.writeAttribute("to", "-" + id_element);
                writer.writeEndElement();
            writer.writeEndElement();

            writer.writeStartElement("personFlow");
                writer.writeAttribute("id", String.format("p%db", nums.get(i)));
                writer.writeAttribute("begin", "1");
                writer.writeAttribute("period", "5");
                writer.writeAttribute("impatience", String.format("%.2f", 1 - impatience));

                writer.writeStartElement("walk");
                    writer.writeAttribute("from", "-" + id_element);
                    writer.writeAttribute("to", id_element);
                writer.writeEndElement();
            writer.writeEndElement();
        }

        writer.writeEndDocument();

        writer.flush();
        writer.close();
    }

    public void saveXML(String xml) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");

        StreamSource source = new StreamSource(new StringReader(xml));
        StreamResult output = new StreamResult(new File(xmlFilePath));
        transformer.transform(source, output);
    }

}
