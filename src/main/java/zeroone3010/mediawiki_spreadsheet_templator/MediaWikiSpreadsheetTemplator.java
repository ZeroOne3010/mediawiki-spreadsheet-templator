package zeroone3010.mediawiki_spreadsheet_templator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MediaWikiSpreadsheetTemplator {
    private static final Date NOW = new Date();
    private static SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static void main(String[] args) {
        MediaWikiSpreadsheetTemplator templator = new MediaWikiSpreadsheetTemplator();
        templator.generatePages(new File("/tmp/in.xls"));
    }

    public void generatePages(final File file) {
        Workbook workbook = null;
        try {
            workbook = Workbook.getWorkbook(file);
        } catch (BiffException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        final Sheet sheet = workbook.getSheet(0);
        final String templateName = sheet.getName();
        final Cell[] parameterNames = sheet.getRow(0);
        final List<Map<String, String>> templates = readRows(sheet, parameterNames);
        final Document xmlDocument = createDocument();
        addTemplatesAsPages(templates, xmlDocument);
        writeXmlOut(xmlDocument);
        System.out.println("DONE");
    }

    private void writeXmlOut(Document xmlDocument) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(xmlDocument);

        StreamResult console = new StreamResult(System.out);
        StreamResult outputFile = new StreamResult(new File("/tmp/out.xml"));

        try {
            transformer.transform(source, console);
            transformer.transform(source, outputFile);
        } catch (TransformerException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void addTemplatesAsPages(List<Map<String, String>> templates, Document xmlDocument) {
        for (Map<String, String> template : templates) {
            xmlDocument.getElementsByTagName("mediawiki").item(0).appendChild(createPage(xmlDocument, template));
        }
    }

    private Node createPage(Document document, Map<String, String> template) {
        final Element pageElement = document.createElement("page");
        pageElement.appendChild(createElementWithContent(document, "title", "page title todo")); // TODO
        pageElement.appendChild(createElementWithContent(document, "ns", "0"));
        pageElement.appendChild(createElementWithContent(document, "sha1", null));
        final Element revisionElement = createElementWithContent(document, "revision", null);
        revisionElement.appendChild(createElementWithContent(document, "timestamp", createTimestamp()));
        final Element contributor = createElementWithContent(document, "contributor", null);
        contributor.appendChild(createElementWithContent(document, "username", "username todo")); // TODO
        revisionElement.appendChild(contributor);
        final String textContent = createTemplate("template name todo", template); // TODO
        final Element textElement = createElementWithContent(document, "text", textContent);
        textElement.setAttribute("xml:space", "preserve");
        try {
            textElement.setAttribute("bytes", Integer.toString(textContent.getBytes("UTF-8").length));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(1);
        }
        revisionElement.appendChild(textElement);
        pageElement.appendChild(revisionElement);
        return pageElement;
    }

    private String createTimestamp() {
        return XML_DATE_FORMAT.format(NOW);
    }

    private Element createElementWithContent(Document document, String elementName, String content) {
        Element element = document.createElement(elementName);
        element.setTextContent(content);
        return element;
    }

    private static Document createDocument() {
        Document document = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.newDocument();
            Element rootElement = document.createElement("mediawiki");
            rootElement.setAttribute("xmlns", "http://www.mediawiki.org/xml/export-0.6/");
            rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttribute("xsi:schemaLocation",
                    "http://www.mediawiki.org/xml/export-0.6/ http://www.mediawiki.org/xml/export-0.6.xsd");
            rootElement.setAttribute("version", "0.6");
            rootElement.setAttribute("xml:lang", "en");
            document.appendChild(rootElement);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return document;
    }

    private List<Map<String, String>> readRows(final Sheet sheet, final Cell[] parameterNames) {
        final List<Map<String, String>> templates = new ArrayList<>();
        for (int row = 1; row < sheet.getRows(); row++) {
            Map<String, String> template = new LinkedHashMap<>();
            for (int param = 0; param < sheet.getRow(row).length; param++) {
                template.put(parameterNames[param].getContents(), sheet.getRow(row)[param].getContents());
            }
            templates.add(template);
        }
        return templates;
    }

    private String createTemplate(final String name, final Map<String, String> params) {
        StringBuilder sb = new StringBuilder("{{").append(name);
        if (!params.isEmpty()) {
            sb.append("\n");
        }
        for (Entry<String, String> entry : params.entrySet()) {
            sb.append("|").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        sb.append("}}");
        return sb.toString();
    }
}
