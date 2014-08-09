package zeroone3010.mediawiki_spreadsheet_templator;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MediaWikiXmlDocument {
    private static final Date NOW = new Date();
    private static final SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    private Document document;
    private String contributorUsername;

    public MediaWikiXmlDocument(final String contributorUsername) {
        this.contributorUsername = contributorUsername;
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
            throw new RuntimeException(e);
        }
    }

    public void addPage(final String pageTitle, final String pageContent) {
        final Element pageElement = document.createElement("page");
        pageElement.appendChild(createElementWithContent("title", pageTitle));
        pageElement.appendChild(createElementWithContent("ns", "0"));
        pageElement.appendChild(createElementWithContent("sha1", null));
        final Element revisionElement = createElementWithContent("revision", null);
        revisionElement.appendChild(createElementWithContent("timestamp", createTimestamp()));
        final Element contributor = createElementWithContent("contributor", null);
        contributor.appendChild(createElementWithContent("username", contributorUsername));
        revisionElement.appendChild(contributor);
        final Element textElement = createElementWithContent("text", pageContent);
        textElement.setAttribute("xml:space", "preserve");
        try {
            textElement.setAttribute("bytes", Integer.toString(pageContent.getBytes("UTF-8").length));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        revisionElement.appendChild(textElement);
        pageElement.appendChild(revisionElement);
        document.getElementsByTagName("mediawiki").item(0).appendChild(pageElement);
    }

    private Element createElementWithContent(final String elementName, final String content) {
        Element element = document.createElement(elementName);
        element.setTextContent(content);
        return element;
    }

    private String createTimestamp() {
        return XML_DATE_FORMAT.format(NOW);
    }

    public void writeToFile(final String fileName) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult outputFile = new StreamResult(new File(fileName));

        try {
            transformer.transform(source, outputFile);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
