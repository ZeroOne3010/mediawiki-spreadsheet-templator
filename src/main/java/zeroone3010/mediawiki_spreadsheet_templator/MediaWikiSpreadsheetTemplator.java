package zeroone3010.mediawiki_spreadsheet_templator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MediaWikiSpreadsheetTemplator {
    public static void main(String[] args) {
        MediaWikiSpreadsheetTemplator templator = new MediaWikiSpreadsheetTemplator();
        System.out.println("Username: " + System.getProperty("username"));
        templator.generatePages(new File(System.getProperty("in")));
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
        final MediaWikiXmlDocument mediaWikiDocument = new MediaWikiXmlDocument(System.getProperty("username"));
        final List<Page> pages = convertDataToPages(templateName, templates);
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
        String title = getIgnoreCase(template, "pagename");
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
