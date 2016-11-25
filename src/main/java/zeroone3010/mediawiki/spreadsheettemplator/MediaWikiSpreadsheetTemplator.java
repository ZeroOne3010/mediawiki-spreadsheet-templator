package zeroone3010.mediawiki.spreadsheettemplator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MediaWikiSpreadsheetTemplator {
    private static final String PAGENAME = "pagename";

    public static void main(String[] args) {
        MediaWikiSpreadsheetTemplator templator = new MediaWikiSpreadsheetTemplator();
        
        if(System.getProperty("username") == null || System.getProperty("in") == null || System.getProperty("out") == null) {
          System.out.println("\nUsage:\njava -Din=\"input_file.xls\" -Dout=\"output_file.xml\" -Dusername=\"Desired wiki username\" -jar name_of_the_jar_file.jar\n");
          System.exit(1);
        }
        templator.generatePages(new File(System.getProperty("in")));
    }
    
    public void generatePages(final File file) {
        final ExcelToTemplateDataParser parser = new ExcelToTemplateDataParser();
        final TemplateDataCollection parsedCollection = parser.parseExcelFile(file);
        final MediaWikiXmlDocument mediaWikiDocument = new MediaWikiXmlDocument(System.getProperty("username"));
        final List<Page> pages = convertDataToPages(parsedCollection.getTemplateName(), parsedCollection.getData());
        System.out.println("Creating " + pages.size() + " pages...");
        for (Page page : pages) {
            mediaWikiDocument.addPage(page.getTitle(), page.getContent());
        }
        mediaWikiDocument.writeToFile(System.getProperty("out"));
        System.out.println("Done.");
    }

    private List<Page> convertDataToPages(String templateName, List<Map<String, String>> templates) {
        final List<Page> results = new ArrayList<>();
        for (Map<String, String> template : templates) {
            final String title = createTitle(template);
            final String content = createTemplate(templateName, template);
            results.add(new Page(title, content));
        }
        return results;
    }

    private String createTitle(Map<String, String> template) {
        String title = getIgnoreCase(template, PAGENAME);
        if (title == null) {
            title = getIgnoreCase(template, "name");
        }
        return title;
    }

    private String getIgnoreCase(Map<String, String> map, String key) {
        for (String string : map.keySet()) {
            if (key.equalsIgnoreCase(string)) {
                return map.get(string);
            }
        }
        return null;
    }


    private String createTemplate(final String name, final Map<String, String> params) {
        StringBuilder sb = new StringBuilder("{{").append(name);
        if (!params.isEmpty()) {
            sb.append("\n");
        }
        for (Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(PAGENAME)) {
                continue;
            }
            sb.append("|").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        sb.append("}}");
        return sb.toString();
    }
}
