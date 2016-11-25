package zeroone3010.mediawiki.spreadsheettemplator;

import java.io.File;
import java.util.List;

public class MediaWikiSpreadsheetTemplator {

    public static void main(String[] args) {
        MediaWikiSpreadsheetTemplator templator = new MediaWikiSpreadsheetTemplator();

        if (propertyIsNull("username") || propertyIsNull("in") || propertyIsNull("out")) {
          System.out.println("\nUsage:\njava -Din=\"input_file.xls\" -Dout=\"output_file.xml\" -Dusername=\"Desired wiki username\" -jar name_of_the_jar_file.jar\n");
          System.exit(1);
        }
        templator.generatePages(new File(System.getProperty("in")));
    }

    private static boolean propertyIsNull(final String username) {
        return System.getProperty(username) == null;
    }

    public void generatePages(final File file) {
        final ExcelToTemplateDataParser parser = new ExcelToTemplateDataParser();
        final TemplateDataCollection parsedCollection = parser.parseExcelFile(file);
        final MediaWikiPageCreator creator = new MediaWikiPageCreator();
        final List<Page> pages = creator.convertDataToPages(parsedCollection.getTemplateName(), parsedCollection.getData());
        System.out.println("Creating " + pages.size() + " pages...");
        final MediaWikiXmlDocument mediaWikiDocument = new MediaWikiXmlDocument(System.getProperty("username"));
        for (Page page : pages) {
            mediaWikiDocument.addPage(page.getTitle(), page.getContent());
        }
        mediaWikiDocument.writeToFile(System.getProperty("out"));
        System.out.println("Done.");
    }

}
